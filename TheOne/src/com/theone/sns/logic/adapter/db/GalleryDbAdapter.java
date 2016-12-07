package com.theone.sns.logic.adapter.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.theone.sns.TheOneApp;
import com.theone.sns.component.database.TheOneDatabaseHelper;
import com.theone.sns.component.database.URIField;
import com.theone.sns.logic.adapter.db.model.GalleryColumns;
import com.theone.sns.logic.model.user.base.Gallery;

public class GalleryDbAdapter {

	private static final String TAG = "GalleryDbAdapter";

	private static GalleryDbAdapter sInstance;

	private ContentResolver mCr;

	private GalleryDbAdapter() {

		mCr = TheOneApp.getResolver();
	}

	public static synchronized GalleryDbAdapter getInstance() {

		if (null == sInstance) {

			sInstance = new GalleryDbAdapter();
		}

		return sInstance;
	}

	public void insertGalleryList(List<Gallery> galleryList) {

		if (null == galleryList || galleryList.isEmpty()) {

			return;
		}

		for (Gallery gallery : galleryList) {

			insertGallery(gallery);
		}
	}

	public long insertGallery(Gallery gallery) {

		long result = -1;

		if (null != gallery) {

			Gallery g = getGalleryById(gallery._id);

			if (null != g) {

				Log.e(TAG, "in method insertGallery, gallery has exist");

				return -1;
			}

			Uri uri = URIField.GALLERY_URI;

			ContentValues cv = GalleryColumns.setValues(gallery);

			Uri resultUri = mCr.insert(uri, cv);

			if (null != resultUri) {

				result = ContentUris.parseId(resultUri);
			}

		} else {
			Log.w(TAG, "insertGallery fail, gallery is null...");
		}

		return result;
	}

	public List<Gallery> getAllGallery(String type) {

		List<Gallery> galleryList = new ArrayList<Gallery>();

		Cursor cursor = null;

		Uri uri = URIField.GALLERY_URI;

		try {

			cursor = mCr.query(uri, null, GalleryColumns.TYPE + "=?",
					new String[] { type }, null);

			while (cursor.moveToNext()) {

				Gallery gallery = GalleryColumns.parseCursorToGallery(cursor);

				if (null != gallery) {

					galleryList.add(gallery);
				}
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return galleryList;
	}

	public Gallery getGalleryById(String mId) {

		Gallery gallery = null;

		Cursor cursor = null;

		try {

			Uri uri = URIField.GALLERY_URI;

			cursor = mCr.query(uri, null, GalleryColumns.MID + "=?",
					new String[] { mId }, null);

			if (null != cursor && cursor.moveToFirst()) {

				gallery = GalleryColumns.parseCursorToGallery(cursor);
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return gallery;
	}

	public int deleteAllGallery(String type) {

		int result = -1;

		Uri uri = URIField.GALLERY_URI;

		result = mCr.delete(uri, GalleryColumns.TYPE + "=?",
				new String[] { type });

		Log.i(TAG, "deleteALLGallery, result = " + result);

		return result;
	}
}
