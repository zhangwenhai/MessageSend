package com.theone.sns.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.theone.sns.common.FusionAction;
import com.theone.sns.common.TheOneConstants;
import com.theone.sns.ui.base.BasicActivity;
import com.theone.sns.ui.publish.activity.VideoActivity;
import com.theone.sns.util.crop.CropImage;
import com.theone.sns.util.patch.Android5Patch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PictureHelper implements PictureHelperImpl {

	public static final int PHOTO_RESULT = 1;

	public static final int PHOTO_CROP_1 = 2;

	private File m_currentPhotoFile;
	private PictureCallback m_callback;
	private Activity m_activity;
	private boolean playCameraSound = false;

	public PictureHelper(Activity activity, PictureCallback callback) {
		m_activity = activity;
		m_callback = callback;
	}

	public void setPlayCameraSound() {
		playCameraSound = true;
	}

	/**
	 * take photo
	 */
	public void getPictureFromCamera() {
		try {
			if (!FileStore.isSDCardAvailable() && m_callback.saveToSDCard()) {
				// CocoLocalBroadcastUtil.broadcastNoSDCard();
				return;
			}
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			m_currentPhotoFile = new File(FileUtil.getSavePath(), System.currentTimeMillis() + "");
			if (!m_currentPhotoFile.exists()) {
				m_currentPhotoFile.createNewFile();
			}

			Uri uri = Uri.fromFile(m_currentPhotoFile);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			m_activity.startActivityForResult(intent, PHOTO_CAMERA);
		} catch (Exception e) {
			m_callback.setPicture(null);
		}
	}

	public void getVideoFromCamera() {
		Intent mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		m_currentPhotoFile = new File(DownloadUtil.SDPATH, System.currentTimeMillis() + "");
		if (!m_currentPhotoFile.exists() && m_currentPhotoFile.canWrite()) {
			try {
				m_currentPhotoFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Uri uri = Uri.fromFile(m_currentPhotoFile);
		mIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		mIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);// 画质0.5
		mIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
		m_activity.startActivityForResult(mIntent, VIDEO_CAMERA);
	}

	public void getPictureFromGallery(int pickType) {
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			m_activity.startActivityForResult(intent, pickType);
		} catch (ActivityNotFoundException e) {
			m_callback.setPicture(null);
		}
	}

	public void getVideoFromGallery(int pickType) {
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("video/*");
			m_activity.startActivityForResult(intent, pickType);
		} catch (ActivityNotFoundException e) {
			m_callback.setPicture(null);
		}
	}

	public void processActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_CANCELED) {
			m_callback.setPicture(null);
			return;
		}
		switch (requestCode) {
		case PHOTO_PICKED_FROM_GALLERY: {
			getPictureFromGalleryResult(data.getData());
			break;
		}
		case PHOTO_CAMERA: {
			getPictureFromCameraResult();
			break;
		}
		case VIDEO_CAMERA: {
			if (resultCode == Activity.RESULT_FIRST_USER) {
				m_callback.setPicture(getVideoFromCameraResult(data));
			} else {
				m_callback.setPicture(getVideoFromCameraResult(requestCode, resultCode, data));
			}
			break;
		}
		case PHOTO_CROP: {
			m_callback.setPicture(m_currentPhotoFile);
			break;
		}
		case PHOTO_USE_CONFIRM: {
			m_callback.setPicture(m_currentPhotoFile);
			break;
		}
		case PHOTO_CHOOSE_MULTIPLE: {
			if (null != data) {
				boolean original = data.getBooleanExtra(KEY_ORIGINAL, false);
				getMultiplePhotoResult(data, original);
			}
			break;
		}
		case PHOTO_RESULT: {
			if (resultCode == PHOTO_CROP_1) {
				if (null == data
						&& TextUtils.isEmpty(data
								.getStringExtra(FusionAction.PublicAction.PHOTO_FORM_AU))) {
					return;
				}
				String path = data.getStringExtra(FusionAction.PublicAction.PHOTO_FORM_AU);
				if (!new File(path).exists()) {
					return;
				}
				m_callback.setPicture(new File(path));
				break;
			}
			if (null == data) {
				m_callback.setPicture(null);
				return;
			}
			String path = data.getStringExtra(FusionAction.PublicAction.FILE_PATH);
			int angle = data.getIntExtra(FusionAction.PublicAction.FILE_ANGLE, -1);
			int camera = data.getIntExtra(FusionAction.PublicAction.CAMERA_TYPE, -1);
			m_callback.setPicture(new File(path), angle, camera);
			break;
		}

		case VIDEO_PICKED_FROM_GALLERY: {
			getVideoFromGalleryResult(data.getData());
			break;
		}
		}
	}

	private File getVideoFromCameraResult(int requestCode, int resultCode, Intent data) {
		File tmpFile = null;
		m_currentPhotoFile = new File(DownloadUtil.SDPATH + VideoActivity.nameFile);
		try {
			if (m_currentPhotoFile == null || !m_currentPhotoFile.exists()) {
				return tmpFile;
			}
			FileInputStream fis = new FileInputStream(m_currentPhotoFile);
			tmpFile = new File(Environment.getExternalStorageDirectory(), "recordVideo_0_5.3gp");
			FileOutputStream fos = new FileOutputStream(tmpFile);
			byte[] buf = new byte[1024];
			int len;
			while ((len = fis.read(buf)) > 0) {
				fos.write(buf, 0, len);
			}
			fis.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return m_currentPhotoFile;
	}

	private File getVideoFromCameraResult(Intent data) {
		File tmpFile = null;
		m_currentPhotoFile = new File(data.getStringExtra(FusionAction.PublicAction.VIDEO_PATH));
		if (m_currentPhotoFile == null || !m_currentPhotoFile.exists()) {
			return tmpFile;
		}
		return m_currentPhotoFile;
	}

	private void getPictureFromCameraResult() {
		if (m_currentPhotoFile == null || !m_currentPhotoFile.exists()) {
			// CocoLocalBroadcastUtil.broadcastNoSDCard();
			return;
		}

		Integer[] cropImage = m_callback.needCropImage(m_currentPhotoFile);
		if (cropImage != null) {
			cropImage(m_currentPhotoFile, cropImage);
		} else {
			// do check degree and rotate operate
			File thumbFile = getThumbAndRotate(m_currentPhotoFile.getAbsolutePath(),
					TheOneConstants.CHAT_PICTURE_MAX_WIDTH,
					TheOneConstants.CHAT_PICTURE_MAX_HEIGHT, TheOneConstants.CHAT_PICTURE_QUALITY);

			if (null != thumbFile) {
				m_callback.setPicture(thumbFile);
			}
		}
	}

	private void getPictureFromGalleryResult(Uri uri) {
		String pickPicPath = getPath(uri);
		if (pickPicPath == null) {
			m_callback.setPicture(null);
			return;
		}

		m_currentPhotoFile = copyImage(pickPicPath);
		if (m_currentPhotoFile == null || !m_currentPhotoFile.exists()) {
			m_callback.setPicture(null);
			return;
		}

		Integer[] cropImage = m_callback.needCropImage(m_currentPhotoFile);
		if (cropImage != null) {
			cropImage(m_currentPhotoFile, cropImage);
		} else {
			Intent intent = new Intent();
			intent.setClass(m_activity, UsePictureConfirmActivity.class);
			intent.putExtra(UsePictureConfirmActivity.INTENT_PICTURE_PATH,
					m_currentPhotoFile.getPath());
			m_activity.startActivityForResult(intent, PHOTO_USE_CONFIRM);
		}
	}

	private void getVideoFromGalleryResult(final Uri uri) {
		((BasicActivity) m_activity).showForceLoadingDialog();
		final String pickPicPath = getPath(uri);
		if (pickPicPath == null) {
			m_callback.setPicture(null);
			return;
		}

		new AsyncTask<File, Integer, String>() {
			@Override
			protected String doInBackground(File... files) {
				m_currentPhotoFile = copyImage(pickPicPath);
				return "";
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);
				if (m_currentPhotoFile == null || !m_currentPhotoFile.exists()) {
					m_callback.setPicture(null);
					((BasicActivity) m_activity).hideLoadingDialog();
					return;
				}
				m_callback.setPicture(m_currentPhotoFile,
						MediaPlayer.create(m_activity.getApplicationContext(), uri).getDuration(),
						0);
				((BasicActivity) m_activity).hideLoadingDialog();
			}
		}.execute(m_currentPhotoFile);

	}

	public static File copyImage(String src) {
		File dst = new File(FileStore.genNewFilePath());
		if (FileUtil.fileCopy(src, dst.getPath()) == null) {
			return null;
		}

		if (dst == null || !dst.exists()) {
			return null;
		}

		return dst;
	}

	private void cropImage(File f, Integer[] cropImage) {
        // TODO TODO TODO
        final Uri photoUri = Uri.fromFile(f);
        Intent intent = new Intent();
        intent.setClass(m_activity, CropImage.class);
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", cropImage[0]);
//        intent.putExtra("outputY", cropImage[1]);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("setWallpaper", false);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);//黑边
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        m_activity.startActivityForResult(intent, PHOTO_CROP);
    }

	@TargetApi(19)
	public String getPath(Uri uri) {
		if (uri == null)
			return null;
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(m_activity, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(m_activity, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(m_activity, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(m_activity, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;

	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
			String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	public static String formatPath(Uri uri) {
		String path = uri.getPath();
		if (path == null)
			return null;
		if (path.startsWith("file:///"))
			return path.substring(7);
		return path;
	}

	public void takePhoto() {
		getPictureFromCamera();
	}

	public void choosePhoto() {
		// Intent intent = new Intent(m_activity, PicFolderListActivity.class);
		// m_activity.startActivityForResult(intent, PHOTO_CHOOSE_MULTIPLE);
	}

	public void reset() {
		m_activity = null;
	}

	private void getMultiplePhotoResult(Intent data, final boolean original) {
		if (null == data) {
			return;
		}

		final List<String> paths = data.getStringArrayListExtra(KEY_PHOTO_MULTIPLE_FILES);
		if (null == paths || paths.isEmpty()) {
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				for (String path : paths) {
					File thumbFile = getThumbAndRotate(path,
							TheOneConstants.CHAT_PICTURE_MAX_WIDTH,
							TheOneConstants.CHAT_PICTURE_MAX_HEIGHT,
							TheOneConstants.CHAT_PICTURE_QUALITY);

					if (null != thumbFile) {
						if (original) {
							final String destPath = FileStore.genNewFilePath();
							FileUtil.fileCopy(path, destPath);
							m_callback.setOriginalPicture(thumbFile, destPath);
						} else {
							m_callback.setPicture(thumbFile);
						}
					}
				}
			}
		}).start();
	}

	private File getThumbAndRotate(String path, int width, int height, int compressRate) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}

		File src = new File(path);
		if (src.exists()) {
			try {
				Bitmap bitmap = ImageLoaderUtil.getBitmapByWidth(path, width, 0, height);
				// rotate image
				final int degree = Android5Patch.getRotate(src);
				bitmap = HelperFunc.rotateImg(bitmap, degree);
				if (null != bitmap) {
					// save image
					final String destPath = FileStore.genNewFilePath();
					ImageLoaderUtil.saveBitmap(bitmap, destPath, compressRate);
					File destFile = new File(destPath);
					if (destFile.exists()) {
						return destFile;
					}
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
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

}

interface PictureHelperImpl {

	public static final String TAG = "PictureHelper";

	public static final int PHOTO_CAMERA = 8024;

	public static final int PHOTO_PICKED_FROM_GALLERY = 8025;

	public static final int PHOTO_CROP = 8026;

	public static final int PHOTO_USE_CONFIRM = 8027;

	public static final int VIDEO_CAMERA = 8028;
	/**
	 * Request for multiple photos
	 */
	public static final int PHOTO_CHOOSE_MULTIPLE = 8029;

	public static final int VIDEO_PICKED_FROM_GALLERY = 8030;

	/**
	 * After select photos, you should setResult( KEY_PHOTO_MULTIPLE_FILES,
	 * ArrayList<String>.)
	 */
	public static final String KEY_PHOTO_MULTIPLE_FILES = "KEY_PHOTO_MULTIPLE_FILES";

	/**
	 * Whether original photos, setResult(KEY_ORIGINAL, boolean).
	 */
	public static final String KEY_ORIGINAL = "KEY_ORIGINAL";

}