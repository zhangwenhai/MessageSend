/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.theone.sns.util.crop;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.theone.sns.util.ImageLoaderUtil;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Collection of utility functions used in this package.
 */
public class Util {
	public static final int DIRECTION_LEFT = 0;
	public static final int DIRECTION_RIGHT = 1;
	public static final int DIRECTION_UP = 2;
	public static final int DIRECTION_DOWN = 3;
	public static final boolean RECYCLE_INPUT = true;
	public static final boolean NO_RECYCLE_INPUT = false;
	public static final String REVIEW_ACTION = "com.cooliris.media.action.REVIEW";
	private static final String TAG = "Util";
	public static boolean isShowDialog = true;

	private Util() {
	}

	// Rotates the bitmap by the specified degree.
	// If a new bitmap is created, the original bitmap is recycled.
	public static Bitmap rotate(Bitmap b, int degrees) {
		return rotateAndMirror(b, degrees, false);
	}

	// Rotates and/or mirrors the bitmap. If a new bitmap is created, the
	// original bitmap is recycled.
	public static Bitmap rotateAndMirror(Bitmap b, int degrees, boolean mirror) {
		if ((degrees != 0 || mirror) && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
			if (mirror) {
				m.postScale(-1, 1);
				degrees = (degrees + 360) % 360;
				if (degrees == 0 || degrees == 180) {
					m.postTranslate((float) b.getWidth(), 0);
				} else if (degrees == 90 || degrees == 270) {
					m.postTranslate((float) b.getHeight(), 0);
				} else {
					throw new IllegalArgumentException("Invalid degrees=" + degrees);
				}
			}

			try {
				Bitmap b2 = ImageLoaderUtil.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
				if (b != b2) {
					b.recycle();
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				// We have no memory to rotate. Return the original bitmap.
			}
		}
		return b;
	}

	public static <T> int indexOf(T[] array, T s) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(s)) {
				return i;
			}
		}
		return -1;
	}

	public static void closeSilently(Closeable c) {
		if (c == null)
			return;
		try {
			c.close();
		} catch (Throwable t) {
			// do nothing
		}
	}

	public static void closeSilently(ParcelFileDescriptor c) {
		if (c == null)
			return;
		try {
			c.close();
		} catch (Throwable t) {
			// do nothing
		}
	}

	public static void Assert(boolean cond) {
		if (!cond) {
			throw new AssertionError();
		}
	}

	public static Animation slideOut(View view, int to) {
		view.setVisibility(View.INVISIBLE);
		Animation anim;
		switch (to) {
		case DIRECTION_LEFT:
			anim = new TranslateAnimation(0, -view.getWidth(), 0, 0);
			break;
		case DIRECTION_RIGHT:
			anim = new TranslateAnimation(0, view.getWidth(), 0, 0);
			break;
		case DIRECTION_UP:
			anim = new TranslateAnimation(0, 0, 0, -view.getHeight());
			break;
		case DIRECTION_DOWN:
			anim = new TranslateAnimation(0, 0, 0, view.getHeight());
			break;
		default:
			throw new IllegalArgumentException(Integer.toString(to));
		}
		anim.setDuration(500);
		view.startAnimation(anim);
		return anim;
	}

	public static Animation slideIn(View view, int from) {
		view.setVisibility(View.VISIBLE);
		Animation anim;
		switch (from) {
		case DIRECTION_LEFT:
			anim = new TranslateAnimation(-view.getWidth(), 0, 0, 0);
			break;
		case DIRECTION_RIGHT:
			anim = new TranslateAnimation(view.getWidth(), 0, 0, 0);
			break;
		case DIRECTION_UP:
			anim = new TranslateAnimation(0, 0, -view.getHeight(), 0);
			break;
		case DIRECTION_DOWN:
			anim = new TranslateAnimation(0, 0, view.getHeight(), 0);
			break;
		default:
			throw new IllegalArgumentException(Integer.toString(from));
		}
		anim.setDuration(500);
		view.startAnimation(anim);
		return anim;
	}

	public static <T> T checkNotNull(T object) {
		if (object == null)
			throw new NullPointerException();
		return object;
	}

	public static boolean equals(Object a, Object b) {
		return (a == b) || (a == null ? false : a.equals(b));
	}

	public static boolean isPowerOf2(int n) {
		return (n & -n) == n;
	}

	public static int nextPowerOf2(int n) {
		n -= 1;
		n |= n >>> 16;
		n |= n >>> 8;
		n |= n >>> 4;
		n |= n >>> 2;
		n |= n >>> 1;
		return n + 1;
	}

	public static float distance(float x, float y, float sx, float sy) {
		float dx = x - sx;
		float dy = y - sy;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	public static int clamp(int x, int min, int max) {
		if (x > max)
			return max;
		if (x < min)
			return min;
		return x;
	}

	private static class BackgroundJob extends MonitoredActivity.LifeCycleAdapter implements
			Runnable {

		private final MonitoredActivity mActivity;
		private final ProgressDialog mDialog;
		private final Runnable mJob;
		private final Handler mHandler;
		private final Runnable mCleanupRunner = new Runnable() {
			public void run() {
				mActivity.removeLifeCycleListener(BackgroundJob.this);
				if (mDialog.getWindow() != null)
					mDialog.dismiss();
			}
		};

		public BackgroundJob(MonitoredActivity activity, Runnable job, ProgressDialog dialog,
				Handler handler) {
			mActivity = activity;
			mDialog = dialog;
			mJob = job;
			mActivity.addLifeCycleListener(this);
			mHandler = handler;
		}

		public void run() {
			try {
				mJob.run();
			} finally {
				mHandler.post(mCleanupRunner);
			}
		}

		@Override
		public void onActivityDestroyed(MonitoredActivity activity) {
			// We get here only when the onDestroyed being called before
			// the mCleanupRunner. So, run it now and remove it from the queue
			mCleanupRunner.run();
			mHandler.removeCallbacks(mCleanupRunner);
		}

		@Override
		public void onActivityStopped(MonitoredActivity activity) {
			mDialog.hide();
		}

		@Override
		public void onActivityStarted(MonitoredActivity activity) {
			mDialog.show();
		}
	}

	public static void startBackgroundJob(MonitoredActivity activity, String title, String message,
			Runnable job, Handler handler) {
		// Make the progress dialog uncancelable, so that we can gurantee
		// the thread will be done before the activity getting destroyed.
		ProgressDialog dialog = ProgressDialog.show(activity, title, message, true, false);
		new Thread(new BackgroundJob(activity, job, dialog, handler)).start();
	}

	public static Bitmap transform(Matrix scaler, Bitmap source, int targetWidth, int targetHeight,
			boolean scaleUp, boolean recycle) {
		int deltaX = source.getWidth() - targetWidth;
		int deltaY = source.getHeight() - targetHeight;
		if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
			/*
			 * In this case the bitmap is smaller, at least in one dimension,
			 * than the target. Transform it by placing as much of the image as
			 * possible into the target and leaving the top/bottom or left/right
			 * (or both) black.
			 */
			Bitmap b2 = ImageLoaderUtil
					.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b2);

			int deltaXHalf = Math.max(0, deltaX / 2);
			int deltaYHalf = Math.max(0, deltaY / 2);
			Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf
					+ Math.min(targetWidth, source.getWidth()), deltaYHalf
					+ Math.min(targetHeight, source.getHeight()));
			int dstX = (targetWidth - src.width()) / 2;
			int dstY = (targetHeight - src.height()) / 2;
			Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight - dstY);
			c.drawBitmap(source, src, dst, null);
			if (recycle) {
				source.recycle();
			}
			return b2;
		}
		float bitmapWidthF = source.getWidth();
		float bitmapHeightF = source.getHeight();

		float bitmapAspect = bitmapWidthF / bitmapHeightF;
		float viewAspect = (float) targetWidth / targetHeight;

		if (bitmapAspect > viewAspect) {
			float scale = targetHeight / bitmapHeightF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		} else {
			float scale = targetWidth / bitmapWidthF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		}

		Bitmap b1;
		if (scaler != null) {
			// this is used for minithumb and crop, so we want to filter here.
			b1 = ImageLoaderUtil.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
					scaler, true);
		} else {
			b1 = source;
		}

		if (recycle && b1 != source) {
			source.recycle();
		}

		int dx1 = Math.max(0, b1.getWidth() - targetWidth);
		int dy1 = Math.max(0, b1.getHeight() - targetHeight);

		Bitmap b2 = ImageLoaderUtil.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth, targetHeight);

		if (b2 != b1) {
			if (recycle || b1 != source) {
				b1.recycle();
			}
		}

		return b2;
	}

	public static BitmapFactory.Options createNativeAllocOptions() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inNativeAlloc = true;
		return options;
	}

	public static Bitmap makeBitmap(int minSideLength, int maxNumOfPixels, Uri uri,
			ContentResolver cr, boolean useNative) {
		ParcelFileDescriptor input = null;
		try {
			input = cr.openFileDescriptor(uri, "r");
			BitmapFactory.Options options = null;
			if (useNative) {
				options = createNativeAllocOptions();
			}
			return makeBitmap(minSideLength, maxNumOfPixels, uri, cr, input, options);
		} catch (IOException ex) {
			return null;
		} finally {
			closeSilently(input);
		}
	}

	public static Bitmap makeBitmap(int minSideLength, int maxNumOfPixels,
			ParcelFileDescriptor pfd, boolean useNative) {
		BitmapFactory.Options options = null;
		if (useNative) {
			options = createNativeAllocOptions();
		}
		return makeBitmap(minSideLength, maxNumOfPixels, null, null, pfd, options);
	}

	public static Bitmap makeBitmap(int minSideLength, int maxNumOfPixels, Uri uri,
			ContentResolver cr, ParcelFileDescriptor pfd, BitmapFactory.Options options) {
		try {
			if (pfd == null)
				pfd = makeInputStream(uri, cr);
			if (pfd == null)
				return null;
			if (options == null)
				options = new BitmapFactory.Options();

			FileDescriptor fd = pfd.getFileDescriptor();
			options.inJustDecodeBounds = true;
			BitmapManager.instance().decodeFileDescriptor(fd, options);
			if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
				return null;
			}
			options.inSampleSize = computeSampleSize(options, minSideLength, maxNumOfPixels);
			options.inJustDecodeBounds = false;

			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			return BitmapManager.instance().decodeFileDescriptor(fd, options);
		} catch (OutOfMemoryError ex) {
			Log.e(TAG, "Got oom exception ", ex);
			return null;
		} finally {
			closeSilently(pfd);
		}
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength,
			int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == UriImage.UNCONSTRAINED) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == UriImage.UNCONSTRAINED) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == UriImage.UNCONSTRAINED) && (minSideLength == UriImage.UNCONSTRAINED)) {
			return 1;
		} else if (minSideLength == UriImage.UNCONSTRAINED) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength,
			int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static ParcelFileDescriptor makeInputStream(Uri uri, ContentResolver cr) {
		try {
			return cr.openFileDescriptor(uri, "r");
		} catch (IOException ex) {
			return null;
		}
	}

	/**
	 * Alert a progress dialog, and do background work in a new thread. Dismiss
	 * the dialog when job done, and handle callback.
	 * 
	 * @param context used to show dialog.
	 * @param title title in progress dialog.
	 * @param message message in progress dialog.
	 * @param job job to do in new thread.
	 * @param callback something to do after job done.
	 * @param handler used to dismiss the dialog, and handle callback.
	 */
	public static void startBackgroundJobPic(Context context, String title, String message,
			Runnable job, OnBgJobDoneListener callback, Handler handler) {
		ProgressDialog dialog = null;
		if (isShowDialog) {
			dialog = ProgressDialog.show(context, title, message, true, false);
		}

		new Thread(new BackgroundJobPic(job, dialog, callback, handler)).start();
	}

	/**
	 * To do something after background job done.
	 * 
	 * @author doumingming
	 * 
	 */
	public interface OnBgJobDoneListener {
		void onJobDone();
	}

	private static class BackgroundJobPic implements Runnable {
		private ProgressDialog mDialog;

		private Runnable mJob;

		private OnBgJobDoneListener mCallback;

		private Handler mHandler;

		public BackgroundJobPic(Runnable job, ProgressDialog dialog, OnBgJobDoneListener callback,
				Handler handler) {
			mDialog = dialog;
			mJob = job;
			mCallback = callback;
			mHandler = handler;
		}

		@Override
		public void run() {
			try {
				mJob.run();
			} finally {
				mHandler.post(mCleanupRunner);
			}
		}

		private final Runnable mCleanupRunner = new Runnable() {
			public void run() {
				if (null != mDialog && mDialog.getWindow() != null) {
					mDialog.dismiss();
				}

				if (null != mCallback) {
					mCallback.onJobDone();
				}
			}
		};
	}
}
