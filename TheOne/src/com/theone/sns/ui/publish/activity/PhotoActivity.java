package com.theone.sns.ui.publish.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.TheOneConstants;
import com.theone.sns.ui.main.MainActivity;
import com.theone.sns.util.DownloadUtil;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.ImageLoaderUtil;
import com.theone.sns.util.PictureHelper;
import com.theone.sns.util.patch.Android5Patch;
import com.theone.sns.util.uiwidget.CameraPreview;
import com.theone.sns.util.uiwidget.ResizableCameraPreview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Android手指拍照
 */
public class PhotoActivity extends Activity implements com.theone.sns.util.PictureCallback {

    // 声明一个Bundle对象，用来存储数据
    private Bundle bundle = null;
    private static final int DISAPPEAR = 0;
    private boolean isFocus = false;
    private int widthPixels;
    private int heightPixels;
    private ResizableCameraPreview mPreview;
    private RelativeLayout RelativeLayout1;
    private int mCameraId = 0;
    private DrawCaptureRect mDraw;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DISAPPEAR: {
                    if (null != mDraw && null != mDraw.getParent()) {
                        ((ViewGroup) mDraw.getParent()).removeView(mDraw);
                    }
                    break;
                }
            }
        }
    };
    private Runnable mSearchPicFolders = new Runnable() {
        @Override
        public void run() {
            String[] projection = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                    null, null, MediaStore.Images.Media.DATE_MODIFIED + " DESC");
            if (cursor.moveToFirst()) {
                do {
                    int picPathIndex = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    final String path = cursor.getString(picPathIndex);

                    if (!new File(path).exists()) {
                        continue;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bm = ImageLoaderUtil.getBitmapByWidth(path, (int) HelperFunc.dip2px(50), 0, (int) HelperFunc.dip2px(50));
                            bm = Android5Patch.getRotateBitmap(bm, path);
                            image.setImageBitmap(bm);
                        }
                    });
                    break;
                } while (cursor.moveToNext());
            }
        }
    };
    private boolean frist = true;
    private ImageView image;
    private PictureHelper m_pictureHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_main);

        widthPixels = getResources().getDisplayMetrics().widthPixels;
        heightPixels = getResources().getDisplayMetrics().heightPixels;

        m_pictureHelper = new PictureHelper(this, this);

        mPreview = new ResizableCameraPreview(this, mCameraId,
                CameraPreview.LayoutMode.FitToParent, false);

        RelativeLayout.LayoutParams previewLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout1 = (RelativeLayout) findViewById(R.id.FrameLayout1);
        RelativeLayout1.addView(mPreview, 0, previewLayoutParams);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(widthPixels,
                (int) (heightPixels - HelperFunc.dip2px(60) - MainActivity.statusBarHeight - widthPixels));
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        findViewById(R.id.start_view).setLayoutParams(lp);

        image = (ImageView) findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_pictureHelper.getPictureFromGallery(PictureHelper.PHOTO_PICKED_FROM_GALLERY);
            }
        });

        findViewById(R.id.flashlight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Camera.Parameters parameters = mPreview.getmCamera().getParameters();
                if (Camera.Parameters.FLASH_MODE_TORCH.equals(parameters.getFlashMode())) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    ((ImageView) view).setImageDrawable(getResources().getDrawable(
                            R.drawable.photo_flash_icon));
                    mPreview.getmCamera().setParameters(parameters);
                } else {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    ((ImageView) view).setImageDrawable(getResources().getDrawable(
                            R.drawable.photo_flash_close_icon));
                    mPreview.getmCamera().setParameters(parameters);
                }
            }
        });

        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPreview.getmCamera().autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {
                        mDraw.setMcolorfill(getResources().getColor(R.color.color_ffd800));
                        mHandler.sendEmptyMessageDelayed(DISAPPEAR, 1000);
                    }
                });
            }
        });

        mPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (null != mDraw && null != mDraw.getParent()) {
                            ((ViewGroup) mDraw.getParent()).removeView(mDraw);
                        }
                        mDraw = new DrawCaptureRect(PhotoActivity.this, (int) motionEvent.getX(),
                                (int) motionEvent.getY(), 100, 100, getResources().getColor(
                                R.color.white));
                        addContentView(mDraw, new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT));
                        break;
                    }
                }

                return false;

            }
        });

        new Thread(mSearchPicFolders).start();
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        if (frist) {

            frist = false;
        }
    }

    /**
     * 按钮被点击触发的事件
     *
     * @param v
     */
    public void takePictureOnclick(View v) {
        if (mPreview.getmCamera() != null) {
            switch (v.getId()) {
                case R.id.takepicture:
                    findViewById(R.id.takepicture).setEnabled(false);
                    mPreview.getmCamera().takePicture(null, null, new MyPictureCallback());
                    break;
            }
        }
    }

    /**
     * 按钮被点击触发的事件
     *
     * @param v
     */
    public void reversalOnclick(View v) {
        if (v.getId() == R.id.reversal) {
            // 切换前后摄像头
            int cameraCount = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();
            for (int i = 0; i < cameraCount; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (mCameraId == 1) {
                    mPreview.stop();
                    RelativeLayout1.removeView(mPreview);
                    mCameraId = 0;
                    createCameraPreview();
                    break;
                } else {
                    mPreview.stop();
                    RelativeLayout1.removeView(mPreview);
                    mCameraId = 1;
                    createCameraPreview();
                    break;
                }

            }
        }
    }

    private void createCameraPreview() {
        mPreview = new ResizableCameraPreview(this, mCameraId,
                CameraPreview.LayoutMode.FitToParent, false);
        RelativeLayout.LayoutParams previewLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout1.addView(mPreview, 0, previewLayoutParams);
    }

    public void OnClickBack(View view) {
        finish();
    }

    @Override
    public void setPicture(File f) {
        setResult(
                PictureHelper.PHOTO_CROP_1,
                new Intent()
                        .putExtra(FusionAction.PublicAction.PHOTO_FORM_AU, f.getPath()));
        finish();
    }

    @Override
    public void setPicture(File f, int angle, int camera) {

    }

    @Override
    public boolean saveToSDCard() {
        return false;
    }

    @Override
    public Integer[] needCropImage(File f) {
        return new Integer[]{TheOneConstants.USER_AVATAR_MAX_SIZE,
                TheOneConstants.USER_AVATAR_MAX_SIZE};
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

    private final class MyPictureCallback implements PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                bundle = new Bundle();
                bundle.putByteArray("bytes", data); // 将图片字节数据保存在bundle当中，实现数据交换
                File f = saveToSDCard(data); // 保存图片到sd卡中
                setResult(
                        PictureHelper.PHOTO_RESULT,
                        new Intent()
                                .putExtra(FusionAction.PublicAction.FILE_PATH, f.getPath())
                                .putExtra(FusionAction.PublicAction.FILE_ANGLE,
                                        getPreviewDegree(PhotoActivity.this))
                                .putExtra(FusionAction.PublicAction.CAMERA_TYPE, mCameraId));
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将拍下来的照片存放在SD卡中
     *
     * @param data
     * @throws IOException
     */
    public File saveToSDCard(byte[] data) throws IOException {
        String filename = "NewPhotoTheOne" + System.currentTimeMillis();
        File fileFolder = new File(DownloadUtil.SDPATH);
        if (!fileFolder.exists()) {
            fileFolder.mkdir();
        }
        File jpgFile = new File(fileFolder, filename);
        FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流
        outputStream.write(data); // 写入sd卡中
        outputStream.close(); // 关闭输出流
        return jpgFile;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_CAMERA: // 按下拍照按钮
                if (mPreview.getmCamera() != null && event.getRepeatCount() == 0) {
                    // 拍照
                    // 注：调用takePicture()方法进行拍照是传入了一个PictureCallback对象——当程序获取了拍照所得的图片数据之后
                    // ，PictureCallback对象将会被回调，该对象可以负责对相片进行保存或传入网络
                    mPreview.getmCamera().takePicture(null, null, new MyPictureCallback());
                }
        }
        return super.onKeyDown(keyCode, event);
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

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    class DrawCaptureRect extends View {
        private int mcolorfill;
        private int mleft, mtop, mwidth, mheight;

        public DrawCaptureRect(Context context, int left, int top, int width, int height,
                               int colorfill) {
            super(context);
            this.mcolorfill = colorfill;
            this.mleft = left;
            this.mtop = top;
            this.mwidth = width;
            this.mheight = height;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Paint mpaint = new Paint();
            mpaint.setColor(mcolorfill);
            mpaint.setStyle(Paint.Style.FILL);
            mpaint.setStrokeWidth(2.0f);
            mpaint.setAntiAlias(true);
            canvas.drawLine(mleft, mtop, mleft + mwidth, mtop, mpaint);
            canvas.drawLine(mleft + mwidth, mtop, mleft + mwidth, mtop + mheight, mpaint);
            canvas.drawLine(mleft, mtop, mleft, mtop + mheight, mpaint);
            canvas.drawLine(mleft, mtop + mheight, mleft + mwidth, mtop + mheight, mpaint);
            super.onDraw(canvas);
        }

        public void setMcolorfill(int mcolorfill) {
            this.mcolorfill = mcolorfill;
            invalidate();
        }
    }
}
