package com.theone.sns.util.patch;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;

import com.theone.sns.util.HelperFunc;

import java.io.File;

public class Android5Patch {

	private static final String TAG = "Coco.Android5Patch";

	@TargetApi(5)
	public static int getRotate(File f) {
		if (Build.VERSION.SDK_INT < 5 || f == null)
			return 0;
		try {
			android.media.ExifInterface exif = new android.media.ExifInterface(f.getAbsolutePath());
			int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION,
					android.media.ExifInterface.ORIENTATION_NORMAL);
			int rotate = 0;
			switch (orientation) {
			case android.media.ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case android.media.ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case android.media.ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			}
			return rotate;
		} catch (Exception e) {
			// LogUtil.error(TAG, e);
			return 0;
		}
	}

	public static Bitmap getRotateBitmap(Bitmap bt, String localImagePath) {
		Bitmap newBitmap = null;
		if (bt == null || TextUtils.isEmpty(localImagePath) || !new File(localImagePath).exists()) {
			return newBitmap;
		}
		File file = new File(localImagePath);
		int degree = Android5Patch.getRotate(file);
		newBitmap = HelperFunc.rotateImg(bt, degree);
		return newBitmap;
	}
}
