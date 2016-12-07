package com.theone.sns.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.text.ClipboardManager;

import com.theone.sns.TheOneApp;
import com.theone.sns.common.TheOneConstants;
import com.theone.sns.ui.main.MainActivity;

public class HelperFunc {

	private static final String TAG = HelperFunc.class.getSimpleName();

	static int CHAT_PRE_IMAGE_WIDTH = HelperFunc.scale(150);

	static int CHAT_PRE_IMAGE_HEIGHT = HelperFunc.scale(150);

    static int CHAT_LOCATION_WIDTH = HelperFunc.scale(217);

    static int CHAT_LOCATION_HEIGHT = HelperFunc.scale(121);

	public final static int scale(int value) {
		return (int) (value * TheOneApp.getContext().getResources().getDisplayMetrics().scaledDensity);
	}

	public final static float dip2px(float dpValue) {
		final float scale = TheOneApp.getContext().getResources().getDisplayMetrics().scaledDensity;
		return (dpValue * scale);
	}

	public final static float px2dip(float pxValue) {
		final float scale = TheOneApp.getContext().getResources().getDisplayMetrics().scaledDensity;
		return (pxValue / scale);
	}

	public final static float px2sp(float pxValue) {
		final float scale = TheOneApp.getContext().getResources().getDisplayMetrics().scaledDensity;
		return (pxValue / scale);
	}

	public final static float sp2px(float spValue) {
		final float scale = TheOneApp.getContext().getResources().getDisplayMetrics().scaledDensity;
		return (spValue * scale);
	}

	public static Bitmap rotateImg(Bitmap src, int rotate) {
		try {
			if (rotate == 0)
				return src;

			Matrix matrix = new Matrix();
			matrix.postRotate(rotate);
			Bitmap rotateBitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(),
					matrix, true);
			if (null != rotateBitmap) {
				src.recycle();
			}
			return rotateBitmap;
		} catch (Exception e) {
		}
		return src;
	}

	public final static Bitmap getScaleImage(File f, int maxWidth, int maxHeight) {
		if (f == null || !f.exists())
			return null;
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(f.getAbsolutePath(), o);

			int width = o.outWidth, height = o.outHeight;

			int scale = (int) Math.ceil(getScale(width, height, maxWidth, maxHeight));
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath(), o2);
			if (bmp == null)
				return null;
			return bmp;
		} catch (Exception e) {
			// LogUtil.error(TAG, e);
			return null;
		} catch (OutOfMemoryError e) {
			return null;
		}
	}

	private final static double getScale(int width, int height, int maxWidth, int maxHeight) {
		double scale = 1;
		if (width <= TheOneConstants.MIN_PIC_WIDTH_OR_HEIGHT
				&& height <= TheOneConstants.MIN_PIC_WIDTH_OR_HEIGHT)
			return 1;

		if (width > maxWidth || height > maxHeight) {
			double scaleWidth = ((double) width) / maxWidth;
			double scaleHeight = ((double) height) / maxHeight;
			scale = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;
			if (scale < 1)
				return 1;

			int newWidth = (int) (width / scale);
			if (newWidth < TheOneConstants.MIN_PIC_WIDTH_OR_HEIGHT) {
				return ((double) width) / TheOneConstants.MIN_PIC_WIDTH_OR_HEIGHT;
			}

			int newHeight = (int) (height / scale);
			if (newHeight < TheOneConstants.MIN_PIC_WIDTH_OR_HEIGHT) {
				return ((double) height) / TheOneConstants.MIN_PIC_WIDTH_OR_HEIGHT;
			}
		}

		return scale;
	}

	public static void rotateChatImg(File f, int rotate) {
		rotateChatImg(f, f, rotate);
	}

	public static void rotateChatImg(File src, File dest, int rotate) {
		try {
			if (rotate == 0)
				return;
			Matrix matrix = new Matrix();
			matrix.postRotate(rotate);
			Bitmap original = decodeFile(src, TheOneConstants.CHAT_PICTURE_MAX_HEIGHT);
			Bitmap rotateBitmap = Bitmap.createBitmap(original, 0, 0, original.getWidth(),
					original.getHeight(), matrix, true);
			saveChatBmp2File(rotateBitmap, dest);
			original.recycle();
			rotateBitmap.recycle();
			rotateBitmap = null;
			original = null;
		} catch (Exception e) {
		}
	}

	public final static Bitmap decodeFile(File f, int maxSize) {
		return getScaleImage(f, maxSize, maxSize);
	}

	public final static boolean saveChatBmp2File(Bitmap bmp, File f) {
		return saveBmp2File(bmp, f, TheOneConstants.CHAT_PICTURE_QUALITY);
	}

	public final static boolean saveBmp2File(Bitmap bmp, File f, int quality) {
		return saveBmp2File(bmp, f, quality, Bitmap.CompressFormat.JPEG);
	}

	public final static boolean saveBmp2File(Bitmap bmp, File f, int quality,
			Bitmap.CompressFormat format) {
		if (bmp == null || f == null)
			return false;
		FileOutputStream out = null;
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			out = new FileOutputStream(f);
			bmp.compress(format, quality, out);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void safeSleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}

	public final static void startTabHostPage(Context context, int selectTab) {
		startTabHostPage(context, selectTab, true);
	}

	public final static Intent startTabHostPage(Context context, int selectTab, boolean startNow) {
		Intent intent = new Intent();
		intent.putExtra(MainActivity.TAB_ACTIVE_INDEX, selectTab);
		intent.setClass(context, MainActivity.class);
		if (startNow)
			context.startActivity(intent);
		return intent;
	}

	public static Bitmap drawChatBitmap(Bitmap bmp, NinePatch maskBmp) {
		return drawMaskedBitmap(bmp, maskBmp, HelperFunc.CHAT_PRE_IMAGE_WIDTH,
				CHAT_PRE_IMAGE_HEIGHT);
	}

	private static Bitmap drawMaskedBitmap(Bitmap bmp, NinePatch maskBmp, int width, int height) {
		if (bmp == null || maskBmp == null)
			return bmp;

		if (width < 0 || height < 0) {
			return bmp;
		}

		Bitmap bitmapOut = ImageLoaderUtil.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		if (null == bitmapOut) {
			return null;
		}

		Canvas c = new Canvas(bitmapOut);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		paint.setColor(Color.BLACK);

		Rect src = null;
		if (bmp.getWidth() >= bmp.getHeight()) {
			src = new Rect((bmp.getWidth() - bmp.getHeight()) / 2, 0, bmp.getHeight()
					+ (bmp.getWidth() - bmp.getHeight()) / 2, bmp.getHeight());
		} else {
			src = new Rect(0, (bmp.getHeight() - bmp.getWidth()) / 2, bmp.getWidth(),
					bmp.getWidth() + (bmp.getHeight() - bmp.getWidth()) / 2);
		}

		Rect dst = new Rect(0, 0, width, height);
		c.drawBitmap(bmp, src, dst, null);
		maskBmp.draw(c, dst, paint);
		return bitmapOut;
	}

    public static Bitmap drawGeoBitmap(Bitmap bmp, NinePatch maskBmp) {
        return drawMaskedGaoBitmap(bmp, maskBmp,
                HelperFunc.CHAT_LOCATION_WIDTH,
                HelperFunc.CHAT_LOCATION_HEIGHT);
    }

    private static Bitmap drawMaskedGaoBitmap(Bitmap bmp, NinePatch maskBmp,
                                              int width, int height) {
        if (bmp == null || maskBmp == null)
            return bmp;

        if (width < 0 || height < 0) {
            return bmp;
        }

        Bitmap bitmapOut = ImageLoaderUtil.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);

        if (null == bitmapOut) {
            return null;
        }

        Canvas c = new Canvas(bitmapOut);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        paint.setColor(Color.BLACK);

        Rect dst = new Rect(0, 0, width, height);
        c.drawBitmap(bmp, null, dst, null);
        maskBmp.draw(c, dst, paint);
        return bitmapOut;
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

	public final static int getRecordingSeconds(long len) {
		return (int) Math.round(len / 1000.0);
	}

	public final static int roundToSecond(long len) {
		int sec = (int) Math.round(len / 1000.0);
		return sec <= 1 ? 1 : sec;
	}

    /**
     * 实现文本复制功能 add by wangqianzhou
     *
     * @param content
     */
    public static void copy(String content, Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }
}
