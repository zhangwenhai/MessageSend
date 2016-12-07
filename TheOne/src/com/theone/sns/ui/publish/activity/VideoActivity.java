package com.theone.sns.ui.publish.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.TheOneConstants;
import com.theone.sns.ui.base.BasicActivity;
import com.theone.sns.util.DownloadUtil;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.ImageLoaderUtil;
import com.theone.sns.util.MyCamPara;
import com.theone.sns.util.PictureCallback;
import com.theone.sns.util.PictureHelper;
import com.theone.sns.util.patch.Android5Patch;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class VideoActivity extends BasicActivity implements SurfaceHolder.Callback, PictureCallback {
	private static final int UPDAT_PROGRESSBAR = 1;
	private SurfaceView cameraView;
	private SurfaceHolder surfaceHolder;
	private Camera camera;
	private boolean isRecord = false;
	private MediaRecorder mediarecorder;
	private int bestWidth = 0;
	private int bestHeight = 0;
	private boolean isFocus = false;
	public static final String nameFile = "NewTheOne";
	private int widthPixels;
	private int heightPixels;
	private ProgressBar imageProgressbar;
	private ThreadShow mThreadShow;
	private TextView time;
	private boolean isRecording = false;
	private long startTime;
	private PictureHelper m_pictureHelper = null;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDAT_PROGRESSBAR: {
				if (isRecord) {
					imageProgressbar.setProgress(msg.arg1);
					imageProgressbar.setMax(15);
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
					date.setTime(msg.arg1 * 1000);
					String str = sdf.format(date);
					time.setText(str);
				}
				if (msg.arg1 == 15) {
					isRecord = false;
					mThreadShow.setContinue(false);
					stopRecord();
				}
				break;
			}
			default:
			}
		}
	};

	private Runnable mSearchPicFolders = new Runnable() {
		@Override
		public void run() {
			String[] projection = { MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA,
					MediaStore.Video.Thumbnails.DATA };
			Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
					null, null, MediaStore.Video.Media.DATE_MODIFIED + " DESC");
			if (cursor.moveToFirst()) {
				do {
					int picPathIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
					int picPathIndex1 = cursor
							.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
					final String photoPath = cursor.getString(picPathIndex1);
					if (!new File(photoPath).exists()) {
						continue;
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Bitmap bm = ImageLoaderUtil.getBitmapByWidth(photoPath,
									(int) HelperFunc.dip2px(50), 0, (int) HelperFunc.dip2px(50));
							bm = Android5Patch.getRotateBitmap(bm, photoPath);
							image.setImageBitmap(bm);
						}
					});
					break;
				} while (cursor.moveToNext());
			}
		}
	};
	private ImageView image;
	private ImageView image2;
	private long videoTime;
	private TextView image3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_main);

		widthPixels = getResources().getDisplayMetrics().widthPixels;
		heightPixels = getResources().getDisplayMetrics().heightPixels;

		cameraView = (SurfaceView) this.findViewById(R.id.CameraView);
		cameraView.setLayoutParams(new FrameLayout.LayoutParams(widthPixels, widthPixels / 3 * 4));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(widthPixels,
				(int) (heightPixels - HelperFunc.dip2px(100) - widthPixels));
		lp.gravity = Gravity.BOTTOM;
		findViewById(R.id.start_view).setLayoutParams(lp);
		surfaceHolder = cameraView.getHolder();
		surfaceHolder.setKeepScreenOn(true);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);

		imageProgressbar = (ProgressBar) findViewById(R.id.image_progressbar);
		time = (TextView) findViewById(R.id.time);
		image = (ImageView) findViewById(R.id.image1);
		image2 = (ImageView) findViewById(R.id.image2);
		image3 = (TextView) findViewById(R.id.image3);

		m_pictureHelper = new PictureHelper(this, this);

		mThreadShow = new ThreadShow();
		findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isRecord) {
					isRecord = false;
					mThreadShow.setContinue(false);
					stopRecord();
				} else {
					mThreadShow.setContinue(true);
					mThreadShow.setI(0);
					startRecord();
					new Thread(mThreadShow).start();
					isRecord = true;
				}
			}
		});

		findViewById(R.id.flashlight).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Camera.Parameters parameters = camera.getParameters();
				if (Camera.Parameters.FLASH_MODE_TORCH.equals(parameters.getFlashMode())) {
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
					((ImageView) view).setImageDrawable(getResources().getDrawable(
							R.drawable.photo_flash_icon));
					camera.setParameters(parameters);
				} else {
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
					((ImageView) view).setImageDrawable(getResources().getDrawable(
							R.drawable.photo_flash_close_icon));
					camera.setParameters(parameters);
				}
			}
		});

		findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				setResult(RESULT_CANCELED);
				onBackPressed();
			}
		});

		image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				m_pictureHelper.getVideoFromGallery(PictureHelper.VIDEO_PICKED_FROM_GALLERY);
			}
		});

		image2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(FusionAction.MBlogAction.VIDEOSHOW_ACTION).putExtra(
						FusionAction.MBlogAction.VIDEO_PATH, DownloadUtil.SDPATH + nameFile)
						.putExtra(FusionAction.MBlogAction.VIDEO_TIME, videoTime));
			}
		});

		image3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent mIntent = new Intent();
				mIntent.putExtra(FusionAction.PublicAction.VIDEO_TIME, videoTime);
				setResult(RESULT_OK, mIntent);
				finish();
			}
		});

		new Thread(mSearchPicFolders).start();
	}

	@Override
	protected void initLogics() {

	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera = Camera.open();// 获取Camera对象
			camera.setPreviewDisplay(holder);// 设置预览监听
			Camera.Parameters parameters = camera.getParameters();
			camera.setDisplayOrientation(getPreviewDegree(VideoActivity.this));
			List<String> list = parameters.getSupportedFocusModes();
			if (list.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
				isFocus = true;
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

			}

			List<Camera.Size> previewSizes = parameters.getSupportedPictureSizes();
			Camera.Size s = MyCamPara.getInstance().getPreviewSize(previewSizes, 480);
			bestWidth = s.height;
			bestHeight = s.width;
			parameters.setPreviewSize(bestHeight, bestWidth);
			camera.setParameters(parameters);

			camera.startPreview();// 启动摄像头预览
			if (isFocus)
				camera.autoFocus(null);
		} catch (Exception e) {
			camera.release();// 如果在设置摄像头的时候出现异常，在这里释放资源
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {// 当要销毁时，先停止预览，并释放资源
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	@Override
	protected void onPause() {
		if (isRecording) {
			if (mediarecorder != null) {
				// 停止录制
				mediarecorder.stop();
				mediarecorder.reset();

				// 释放资源
				mediarecorder.release();
			}
			camera.lock();
			isRecording = false;
			isRecord = false;
			mThreadShow.setContinue(false);
			camera.startPreview();
		}
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (m_pictureHelper == null || null == data) {
			finish();
			return;
		}

		m_pictureHelper.processActivityResult(requestCode, resultCode, data);

	}

	/**
	 * 开始录像
	 */
	public void startRecord() {
		mediarecorder = new MediaRecorder();// 创建mediarecorder对象
		try {
			// 解锁camera
			camera.unlock();
			mediarecorder.setCamera(camera);
			// 设置录制视频源为Camera(相机)
			mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mediarecorder.setOrientationHint(90);
			mediarecorder.setOutputFile(DownloadUtil.SDPATH + nameFile);
			mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

			mediarecorder.setVideoSize(bestHeight, bestWidth);
			mediarecorder.setVideoFrameRate(15);
			mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
			mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
			mediarecorder.setMaxDuration(15000);
			mediarecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
			mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
			// 准备录制
			mediarecorder.prepare();
			// 开始录制
			mediarecorder.start();
			startTime = System.currentTimeMillis();
			isRecording = true;
		} catch (IOException e) {
			e.printStackTrace();
			isRecording = false;
			showToast(R.string.video_error);
			finish();

		}
	}

	/**
	 * 停止录制
	 */
	public void stopRecord() {
		if (mediarecorder != null) {
			try {
				// 停止录制
				mediarecorder.stop();
				mediarecorder.reset();

				// 释放资源
				mediarecorder.release();
				camera.lock();
				isRecording = false;
				camera.stopPreview();
				updateGallery(DownloadUtil.SDPATH + nameFile);
				image2.setVisibility(View.VISIBLE);
				image3.setVisibility(View.VISIBLE);
				videoTime = System.currentTimeMillis() - startTime;
			} catch (Exception e) {
				e.printStackTrace();
				showToast(R.string.video_error);
				finish();
			}
		}
	}

	/**
	 * 用于根据手机方向获得相机预览画面旋转的角度
	 */
	public static int getPreviewDegree(Activity activity) {
		// 获得手机的方向
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degree = 0;
		// 根据手机的方向计算相机预览画面应该选择的角度
		switch (rotation) {
		case Surface.ROTATION_0:
			degree = 90;
			break;
		case Surface.ROTATION_90:
			degree = 0;
			break;
		case Surface.ROTATION_180:
			degree = 270;
			break;
		case Surface.ROTATION_270:
			degree = 180;
			break;
		}
		return degree;
	}

	private void updateGallery(String filename) {// filename是我们的文件全名，包括后缀哦
		MediaScannerConnection.scanFile(this, new String[] { filename }, null,
				new MediaScannerConnection.OnScanCompletedListener() {
					@Override
					public void onScanCompleted(String s, Uri uri) {
						Log.i("ExternalStorage", "Scanned " + s + ":");
						Log.i("ExternalStorage", "-> uri=" + uri);
					}
				});
	}

	@Override
	public void setPicture(File f) {

	}

	@Override
	public void setPicture(File f, int angle, int camera) {
		Intent mIntent = new Intent();
		mIntent.putExtra(FusionAction.PublicAction.VIDEO_TIME, angle);
		mIntent.putExtra(FusionAction.PublicAction.VIDEO_PATH, f.getPath());
		setResult(RESULT_FIRST_USER, mIntent);
		finish();
	}

	@Override
	public boolean saveToSDCard() {
		return false;
	}

	@Override
	public Integer[] needCropImage(File f) {
		return new Integer[] { TheOneConstants.USER_AVATAR_MAX_SIZE,
				TheOneConstants.USER_AVATAR_MAX_SIZE };
	}

	@Override
	public void onDeletePicture() {

	}

	@Override
	public void onPreviewPicture() {

	}

	@Override
	public void setOriginalPicture(File f, String originalPath) {

	}

	// 线程类
	class ThreadShow implements Runnable {

		private boolean isContinue = true;

		private int i = 0;

		@Override
		public void run() {
			while (isContinue) {
				i = i + 1;
				try {
					Thread.sleep(1000);
					final Message msg = new Message();
					msg.what = UPDAT_PROGRESSBAR;
					msg.arg1 = i;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mHandler.sendMessage(msg);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public void setContinue(boolean isContinue) {
			this.isContinue = isContinue;
		}

		public void setI(int i) {
			this.i = i;
			final Message msg = new Message();
			msg.what = UPDAT_PROGRESSBAR;
			msg.arg1 = i;
			mHandler.sendMessage(msg);
		}
	}
}
