package com.theone.sns.ui.publish.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.TheOneConstants;
import com.theone.sns.ui.base.BasicActivity;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.ImageLoaderUtil;
import com.theone.sns.util.PictureCallback;
import com.theone.sns.util.PictureHelper;
import com.theone.sns.util.RoundProgressBar;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import us.pinguo.androidsdk.PGColorBuffer;
import us.pinguo.androidsdk.PGImageSDK;
import us.pinguo.androidsdk.PGRendererMethod;

public class PublishActivity extends BasicActivity implements PictureCallback {

	private final static int SHOW_PHOTO = 1;

	private final static int VOLUME = 2;

	private final static int SAVE_SUE = 3;

	private static final String strKey = "4F521E8A6AAB77CF386A10579F976115FAB1EBC3BB3A0E513D29F9BF65D87100225F7C8E717A1EF27ADF4EAC85E0544EBDCBE9A54B16C33D8858A204E76A6301E3393354FB58B9BB8CE028FABDAB13B9DF95179B463F5CC3C6EDB3BFC4D9E3508D56525F0CAF8E5E13F4C779F83C97D49179CBB7F23CF26A9A461B7DB3063B0DF69F13690B7986D82FD174E6C1B33AA1A7F52E1A43B2252C3C4BE92CB8978539DA2F999B26686D6F5A4A437B4017ECD5DE9066436AA8B122CC033A1B6C2AFB4D359F6BEA7AF14458170B394B53158B6DD89FB34407BE4125E8A7AE02DFC3BB4CABB782B8BDD3B78656773721710BFFE9D5CD665C9270CA2247C82A830C54F09CC0296FC3A09B0DA371E5A178B95E90DAB30A1349387B4D5D20F100A5CBA97FFB40365D426307128EACE0B5093AEFC82557B26102448A5F9BF0D152669800C706E40D98FC3F689A174290272981261AC2DE060597383519279D60B647568BDE21EC00401B31670BB2E60EC61C8D48CF220232A887C47075810DAD0AC67893956E4A627147DA0B1FD940B65E62ADD8582533C29012DAD8F6217879F593B45645F63006DB08CF6AD5767CFA97F5641CFA8106C947A5DD8B8C40FA4996F11316F666D038C0D4C260DB9AFE4306E72152DAF05D50ADC1DFC16E3DE6BF85E12EC51398FBECAE8EFBA008011C5CC43C3725556E8D61A150C0D09122F598966D1EF2E37C27B096B2E443A27B33B41A39A4B0E7E57E0E7164A475AB2ED03E6C58DEBB25A553B27481DEA242BCB0984B783FFF8398F98FE4F98918F32C6A0EA44CE27FC6FB9BE3B4CB87356EAC65134E706E7ECE38D7CE9FFD3A373DE76FDD883EF45E0097912CFB43698223AFC5AC327F6B39B8A8D326E39BD6E0AEB5B807B724F8A9B084B821E6DF7DA4917F1626A5962440DFC98AF34F7BE6603D979DBA6A37C46799D7D89FB34407BE412593E63FA8820959AE568352A9E960A8D2BB1B7A8740EBC352B16ED574B87E2DD6BD5CA53C09A93079A47D26F2030A719A8D52B473CF94E4A7EC65E005628B6C707F85740EA68C86E7F9FFCD03F84A960FA3FB3DC4C7AA06AC559A24D8E09B46979E1D4344B630F999F87859DFD71A23F469E19BD169BD13CBE8F9D3A2324950C87287F1AFF184EC9CECA6A2F66693E2C7E88937E75F2D542D22BB9B5C34396552696F0631D81A1C85F0D152669800C70627BC63FB1E54FE88002863F9B0CA0C346466FAEA756459D047017E1CCB45910B93E63FA8820959AE568352A9E960A8D2D73091457CBACBF48F4AC51A4F58954E3BABA6B77239FF337CFA97F5641CFA81025E76F571E49772B45F7F8AD86F1AE4814E1B5395090306955FE4518014F4DD6520AFB56BE383771EC8B3140C5C10B31D6122035DD30B3946D764AF4013B9DCE81D41E80EFEF5130DA6D57FFD5310BCA47D26F2030A719A8D52B473CF94E4A739EAA2BC1612BA92FE9C4EFFFAEB8518735089CCF741AD577CFA97F5641CFA81025E76F571E497720D40467365780E8F09A44E5C27199AF76520AFB56BE383771EC8B3140C5C10B31D6122035DD30B3946D764AF4013B9DCF8B27AA20B3BA408545B384AAAACDFB46520AFB56BE383771EC8B3140C5C10B31D6122035DD30B3946D764AF4013B9DCF8B27AA20B3BA408A342323A590606885C12BD4D11012C33EA52A568D9B71DCA374D1381064C68EBD4202182ABC0EDBC4F178EE15B51CF92";

	private PhotoGridViewAdapter mPhotoGridViewAdapter;

	private static PictureHelper m_pictureHelper = null;

	private String strEffect = "Effect=Normal";

	private ExecutorService mExectorService;

	private LayoutInflater inflater;

	private GridView photoGridView;

	private PGImageSDK mPGImageSdk;

	private Bitmap aftetbitmap;

	private ImageView image;

	private Bitmap bitmap;

	private int type = 0;

	private int height;

	private int with;

	private File f;

	private CharSequence[] items = {
	/* 原图: */"Normal",
	/* 魔法美肤-甜美可人: */"C360_Skin_Sweet",
	/* 日系-甜美: */"C360_LightColor_SweetRed",
	/* 日系-清凉: */"C360_LightColor_ColorBlue",
	/* 日系-唯美: */"C360_LightColor_Beauty",
	/* 日系-淡雅: */"C360_LightColor_LowSatGreen",
	/* 日系-清新: */"C360_LightColor_NatureFresh",
	/* 日系-温暖: */"C360_LightColor_NatureWarm",
	/* LOMO-电影: */"C360_LOMO_Film",
	/* LOMO-浅回忆: */"C360_LOMO_Recall",
	/* 效果增强-自动增强: */"C360_Enhance_Auto",
	/* 复古-金色年华: */"C360_Retro_Rustic",
	/* 流光溢彩-彩虹: */"C360_Colorful_rainbow" };

	private CharSequence[] items_name = { "原图", "甜美可人", "甜美 ", "清凉", "唯美", "淡雅", "清新", "温暖", "电影",
			"浅回忆", "自动增强", "金色年华", "彩虹" };

	private int[] items_id = { R.drawable.camera360_yuantu, R.drawable.camera360_tianmeikeren,
			R.drawable.camera360_photo_tianmei, R.drawable.camera360_qinglian,
			R.drawable.camera360_weimei, R.drawable.camera360_danya, R.drawable.camera360_qingxin,
			R.drawable.camera360_wennuan, R.drawable.camera360_dianying,
			R.drawable.camera360_qianhuiyi, R.drawable.camera360_zidongzengqiang,
			R.drawable.camera360_jinse, R.drawable.camera360_caihong };

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_PHOTO:
				Bitmap bitmap = (Bitmap) msg.obj;
				Bitmap oldBitmap = aftetbitmap;
				aftetbitmap = bitmap;
				image.setImageBitmap(aftetbitmap);
				if (oldBitmap != null && oldBitmap != bitmap) {
					oldBitmap.recycle();
				}
				break;
			case VOLUME: {
				showVolume(msg.arg1);
				break;
			}
			case SAVE_SUE: {
				hideLoadingDialog();
				Intent mIntent = new Intent(FusionAction.PublicAction.ADDLABEL_ACTION);
				mIntent.putExtra(FusionAction.PublicAction.FILE_PATH, f.getPath() + "1");
				mIntent.putExtra(FusionAction.PublicAction.PUBLIC_TYPE, type);
				if (FusionAction.PublicAction.TYPE_VIDEO == type && null != videoFile) {
					mIntent.putExtra(FusionAction.PublicAction.VIDEO_PATH, videoFile.getPath());
					mIntent.putExtra(FusionAction.PublicAction.VIDEO_TIME, time);
				}
				if (!TextUtils.isEmpty(audioPath)) {
					mIntent.putExtra(FusionAction.PublicAction.AUDIO_PATH, audioPath);
					mIntent.putExtra(FusionAction.PublicAction.AUDIO_TIME, (int) recorderTime);
				}
				startActivity(mIntent);
				finish();
			}
			default:
				break;
			}

		}

		;
	};
	private String audioPath = null;
	private File videoFile;
	private int time;

	private void showVolume(int arg1) {
		switch (arg1) {
		case 0: {
			volumeImageView.setImageDrawable(getResources().getDrawable(R.drawable.publish_audio));
			break;
		}
		case 1: {
			volumeImageView.setImageDrawable(getResources()
					.getDrawable(R.drawable.publish_audio_01));
			break;
		}
		case 2: {
			volumeImageView.setImageDrawable(getResources()
					.getDrawable(R.drawable.publish_audio_02));
			break;
		}
		case 3: {
			volumeImageView.setImageDrawable(getResources()
					.getDrawable(R.drawable.publish_audio_03));
			break;
		}
		case 4: {
			volumeImageView.setImageDrawable(getResources()
					.getDrawable(R.drawable.publish_audio_04));
			break;
		}
		case 5: {
			volumeImageView.setImageDrawable(getResources()
					.getDrawable(R.drawable.publish_audio_05));
			break;
		}
		case 6: {
			volumeImageView.setImageDrawable(getResources()
					.getDrawable(R.drawable.publish_audio_06));
			break;
		}
		case 7: {
			volumeImageView.setImageDrawable(getResources()
					.getDrawable(R.drawable.publish_audio_07));
			break;
		}
		case 8: {
			volumeImageView.setImageDrawable(getResources()
					.getDrawable(R.drawable.publish_audio_08));
			break;
		}
		case 9: {
			volumeImageView.setImageDrawable(getResources().getDrawable(R.drawable.publish_audio));
			break;
		}
		default:
		}
	}

	private PGRendererMethod mRendererMethod = new PGRendererMethod() {

		@Override
		public void rendererAction() {
			renderType(EM_MAKE_TYPE.RENDER_NORMAL);

			if (!this.setImageFromARGB(0, getARGB(bitmap), bitmap.getWidth(), bitmap.getHeight())) {

				return;
			}

			if (!this.setEffect(strEffect)) {
				return;
			}

			if (!this.make()) {

				return;
			}

			PGColorBuffer pgColorBuffer = this.getMakedImage2Buffer();
			Bitmap bitmap = null;
			if (pgColorBuffer != null) {
				bitmap = Bitmap.createBitmap(pgColorBuffer.getColorBuffer(),
						pgColorBuffer.getImageWidth(), pgColorBuffer.getImageHeight(),
						Bitmap.Config.ARGB_8888);

				mHandler.obtainMessage(SHOW_PHOTO, bitmap).sendToTarget();
			}
		}

	};
	private TextView confirTextView;
	private TextView remakeTextView;
	private TextView addAudioTextView;
	private long currentTime;
	private MediaRecorder recorder;
	private ImageView audioViewImageView;
	private RoundProgressBar soundPlaybar;
	private RelativeLayout rela;
	private boolean isShowing = false;
	private long recorderTime;
	private VolumeThread mVolumeThread = new VolumeThread(200);
	private View popView;
	private PopupWindow popupWindow;
	private ImageView volumeImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.public_main);

		getView();

		setView();
	}

	@Override
	public void onResume() {
		super.onResume();

		try {
			InputStream is = getAssets().open("load_background.jpg");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] array = new byte[1024 * 4];
			int readCount = 0;

			while ((readCount = is.read(array)) != -1) {
				baos.write(array, 0, readCount);
			}
			mPGImageSdk = new PGImageSDK(getApplicationContext(), strKey, baos.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		mExectorService.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mPGImageSdk.destroySDK();
			}
		});
	}

	private void getView() {
		type = getIntent().getIntExtra(FusionAction.PublicAction.PUBLIC_TYPE, 0);
		with = getResources().getDisplayMetrics().widthPixels;
		height = getResources().getDisplayMetrics().heightPixels;
		if (0 == type) {
			finish();
		}

		confirTextView = (TextView) findViewById(R.id.confir);
		remakeTextView = (TextView) findViewById(R.id.remake);
		addAudioTextView = (TextView) findViewById(R.id.add_audio);
		audioViewImageView = (ImageView) findViewById(R.id.audio_view);
		soundPlaybar = (RoundProgressBar) findViewById(R.id.sound_playbar);
		rela = (RelativeLayout) findViewById(R.id.rela);

		image = (ImageView) findViewById(R.id.image1);
		image.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
				with));

		photoGridView = (GridView) findViewById(R.id.photo_gridView);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				(int) ((HelperFunc.dip2px(86) + HelperFunc.dip2px(2)) * items.length - HelperFunc
						.dip2px(2)),
				(int) HelperFunc.dip2px(150));
		photoGridView.setLayoutParams(lp);
	}

	private void setView() {
		m_pictureHelper = new PictureHelper(this, this);

		initPopupWindow();

		confirTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showLoadingDialog();
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (null == aftetbitmap) {
							ImageLoaderUtil.saveBitmap(bitmap, f.getPath() + "1",
									TheOneConstants.PUBLIC_PICTURE_QUALITY,
									Bitmap.CompressFormat.JPEG);
						} else {
							ImageLoaderUtil.saveBitmap(aftetbitmap, f.getPath() + "1",
									TheOneConstants.PUBLIC_PICTURE_QUALITY,
									Bitmap.CompressFormat.JPEG);
						}
						mHandler.sendEmptyMessage(SAVE_SUE);
					}
				}).start();
			}
		});

		audioViewImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!isShowing) {
					isShowing = true;
					MediaPlayer player = new MediaPlayer();
					String path = Environment.getExternalStorageDirectory().getAbsolutePath()
							+ "/peipei.amr";
					try {
						player.setDataSource(path);
						player.prepare();
					} catch (IOException e) {
						e.printStackTrace();
					}
					player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mediaPlayer) {
							isShowing = false;
							soundPlaybar.stopCartoom();
						}
					});
					player.start();
					soundPlaybar.startCartoom(0, recorderTime);
				}
			}
		});

		remakeTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (type) {
				case FusionAction.PublicAction.TYPE_VIDEO:
					// m_pictureHelper.getVideoFromCamera();
					startActivityForResult(new Intent(FusionAction.PublicAction.VIDEO_ACTION),
							PictureHelper.VIDEO_CAMERA);
					break;
				case FusionAction.PublicAction.TYPE_PHOTO:
					// m_pictureHelper.getPictureFromCamera();
					startActivityForResult(new Intent(FusionAction.PublicAction.PHOTO_ACTION),
							PictureHelper.PHOTO_RESULT);
					break;
				case FusionAction.PublicAction.TYPE_ALBUM:
					m_pictureHelper.getPictureFromGallery(PictureHelper.PHOTO_PICKED_FROM_GALLERY);
					break;
				}
			}
		});

		mExectorService = Executors.newSingleThreadExecutor(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				// TODO Auto-generated method stub
				return new Thread(r);
			}
		});

		switch (type) {
		case FusionAction.PublicAction.TYPE_VIDEO:
			// m_pictureHelper.getVideoFromCamera();
			startActivityForResult(new Intent(FusionAction.PublicAction.VIDEO_ACTION),
					PictureHelper.VIDEO_CAMERA);
			break;
		case FusionAction.PublicAction.TYPE_PHOTO:
			// m_pictureHelper.getPictureFromCamera();
			startActivityForResult(new Intent(FusionAction.PublicAction.PHOTO_ACTION),
					PictureHelper.PHOTO_RESULT);
			break;
		case FusionAction.PublicAction.TYPE_ALBUM:
			m_pictureHelper.getPictureFromGallery(PictureHelper.PHOTO_PICKED_FROM_GALLERY);
			break;
		}

		addAudioTextView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				switch (motionEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					currentTime = System.currentTimeMillis();
					startRecord();
					popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
					popupWindow.update();
					return true;
				case MotionEvent.ACTION_UP:
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					stopRecord();
					popupWindow.dismiss();
					break;
				case MotionEvent.ACTION_CANCEL:
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					stopRecord();
					popupWindow.dismiss();
					break;
				default:
					break;
				}
				return false;
			}
		});
	}

	private void startRecord() {
		String FileName = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/peipei.amr";
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		recorder.setOutputFile(FileName);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			recorder.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
		recorder.start();
		recorder.getMaxAmplitude();
		mVolumeThread.startVolume();
		new Thread(mVolumeThread).start();
	}

	private void stopRecord() {
		try {
			recorder.stop();// 停止刻录
			recorder.release(); // 刻录完成一定要释放资源
			recorder = null;
			mVolumeThread.stopVolume();
			recorderTime = System.currentTimeMillis() - currentTime - 500;
		} catch (Exception e) {
			e.printStackTrace();
		}
		rela.setVisibility(View.VISIBLE);
		audioPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/peipei.amr";
	}

	private void initPopupWindow() {
		popView = LayoutInflater.from(PublishActivity.this).inflate(R.layout.volume_pop, null);
		volumeImageView = (ImageView) popView.findViewById(R.id.volume);
		popView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (null != popupWindow)
					popupWindow.dismiss();
			}
		});
		popupWindow = new PopupWindow(popView, LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT, true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setHeight(getResources().getDisplayMetrics().heightPixels);
		popupWindow.setWidth(getResources().getDisplayMetrics().widthPixels);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popupWindow.setAnimationStyle(R.style.popwin_anim_style);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (m_pictureHelper == null || null == data) {
			finish();
			return;
		}

		m_pictureHelper.processActivityResult(requestCode, resultCode, data);

		if (PictureHelper.VIDEO_CAMERA == requestCode) {
			time = data.getIntExtra(FusionAction.PublicAction.VIDEO_TIME, 0);
		}

	}

	@Override
	protected void initLogics() {

	}

	@Override
	public void setPicture(File f) {
		if (null == f) {
			finish();
			return;
		}
		aftetbitmap = null;
		this.f = f;
		if (FusionAction.PublicAction.TYPE_VIDEO == type) {
			videoFile = f;
			Bitmap bitmap1 = ThumbnailUtils.createVideoThumbnail(f.getPath(),
					MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
			addAudioTextView.setVisibility(View.GONE);
			int widt;
			Bitmap resizedBitmap = null;
			if (bitmap1.getWidth() >= bitmap1.getHeight()) {
				widt = bitmap1.getHeight();
			} else {
				widt = bitmap1.getWidth();
			}
			resizedBitmap = Bitmap.createBitmap(bitmap1, 0, 0, widt, widt, new Matrix(), true);
			bitmap = resizedBitmap;
			image.setImageBitmap(resizedBitmap);
		} else {
			bitmap = ImageLoaderUtil.getBigBitmapByWidth(f.getPath(), with, 0);
			addAudioTextView.setVisibility(View.VISIBLE);
			image.setImageBitmap(bitmap);
		}

		if (null == mPhotoGridViewAdapter) {
			mPhotoGridViewAdapter = new PhotoGridViewAdapter();
			photoGridView.setAdapter(mPhotoGridViewAdapter);
		}
		mPhotoGridViewAdapter.setFrist(true);
		mPhotoGridViewAdapter.notifyDataSetChanged();
	}

	@Override
	public void setPicture(File f, int angle, int camera) {
		this.f = f;
		int i = HelperFunc.readPictureDegree(f.getPath());
		aftetbitmap = null;
		Bitmap bitmap1 = null;
		if (i == 90 || i == 270) {
			bitmap1 = ImageLoaderUtil.getBitmapByWidth(f.getPath(), height, 0, with);
		} else {
			bitmap1 = ImageLoaderUtil.getBitmapByWidth(f.getPath(), with, 0, height);
		}
		int widt;
		int difference;
		Bitmap resizedBitmap = null;
		if (bitmap1.getWidth() >= bitmap1.getHeight()) {
			widt = bitmap1.getHeight();
			difference = bitmap1.getWidth() - bitmap1.getHeight();
		} else {
			widt = bitmap1.getWidth();
			difference = bitmap1.getHeight() - bitmap1.getWidth();
		}
		Matrix matrix = new Matrix();
		if (camera == 0) {
			matrix.postRotate(i + angle);
			resizedBitmap = Bitmap.createBitmap(bitmap1, 0, 0, widt, widt, matrix, true);
		} else if (camera == 1) {
			matrix.postRotate(i - angle);
			matrix.postScale(-1, 1);
			resizedBitmap = Bitmap.createBitmap(bitmap1, difference, 0, widt, widt, matrix, true);
		}
		bitmap = resizedBitmap;
		image.setImageBitmap(resizedBitmap);

		if (null == mPhotoGridViewAdapter) {
			mPhotoGridViewAdapter = new PhotoGridViewAdapter();
			photoGridView.setAdapter(mPhotoGridViewAdapter);
		}
		mPhotoGridViewAdapter.notifyDataSetChanged();
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

	public static int[] getARGB(Bitmap bitmap) {

		if (bitmap == null) {
			return null;
		}

		int[] ARGBArray = new int[bitmap.getWidth() * bitmap.getHeight()];

		bitmap.getPixels(ARGBArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
				bitmap.getHeight());

		return ARGBArray;
	}

	private class PhotoGridViewAdapter extends BaseAdapter {

		private boolean isFrist = true;

		public void setFrist(boolean isFrist) {
			this.isFrist = isFrist;
		}

		@Override
		public int getCount() {
			return items.length;
		}

		@Override
		public Object getItem(int i) {
			return i;
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			final ImageLoaderViewHolder holder;
			if (view == null) {
				if (null == inflater) {
					inflater = LayoutInflater.from(getApplicationContext());
				}
				view = inflater.inflate(R.layout.photo_gridview_item, null);
				holder = new ImageLoaderViewHolder();
				holder.imageView = (ImageView) view.findViewById(R.id.photo);
				holder.mTextView = (TextView) view.findViewById(R.id.photo_name);
				view.setTag(holder);
			} else {
				holder = (ImageLoaderViewHolder) view.getTag();
			}

			holder.mTextView.setText(items_name[i].toString());
			final String strEffect1 = "Effect=" + items[i].toString();
			holder.imageView.setImageDrawable(getResources().getDrawable(items_id[i]));

			if (isFrist) {
				if (i == 0) {
					view.setBackgroundColor(getResources().getColor(R.color.color_2b282d));
				} else {
					view.setBackgroundColor(Color.BLACK);
				}
				holder.isChecked = false;
			} else {
				if (holder.isChecked) {

					view.setBackgroundColor(getResources().getColor(R.color.color_2b282d));

					holder.isChecked = false;

				} else {
					view.setBackgroundColor(Color.BLACK);
				}
			}

			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					isFrist = false;

					holder.isChecked = true;

					PhotoGridViewAdapter.this.notifyDataSetChanged();

					strEffect = strEffect1;
					mPGImageSdk.renderAction(mRendererMethod);
				}
			});

			return view;
		}
	}

	private class VolumeThread implements Runnable {
		int milliseconds = 200;
		private boolean isRun = true;

		public VolumeThread(int i) {
			milliseconds = i;
		}

		@Override
		public void run() {
			int vuSize = 0;
			while (isRun) {
				try {
					Thread.sleep(milliseconds);

					if (null != recorder) {
						vuSize = 10 * recorder.getMaxAmplitude() / 32768;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.what = VOLUME;
				msg.arg1 = vuSize;
				mHandler.sendMessage(msg);
			}
		}

		public void stopVolume() {
			isRun = false;
		}

		public void startVolume() {
			isRun = true;
		}
	}

	/**
	 * 得到amr的时长
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static long getAmrDuration(File file) {
		long duration = -1;
		int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
			long length = file.length();// 文件的长度
			int pos = 6;// 设置初始位置
			int frameCount = 0;// 初始帧数
			int packedPos = -1;
			// ///////////////////////////////////////////////////
			byte[] datas = new byte[1];// 初始数据值
			while (pos <= length) {
				randomAccessFile.seek(pos);
				if (randomAccessFile.read(datas, 0, 1) != 1) {
					duration = length > 0 ? ((length - 6) / 650) : 0;
					break;
				}
				packedPos = (datas[0] >> 3) & 0x0F;
				pos += packedSize[packedPos] + 1;
				frameCount++;
			}
			// ///////////////////////////////////////////////////
			duration += frameCount * 20;// 帧数*20
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return duration;
	}
}
