package com.theone.sns.logic.adapter.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.theone.sns.TheOneApp;
import com.theone.sns.component.database.TheOneDatabaseHelper;
import com.theone.sns.component.database.URIField;
import com.theone.sns.logic.adapter.db.model.TagColumns;
import com.theone.sns.logic.model.mblog.MBlogTag;

public class TagDbAdapter {

	private static final String TAG = "TagDbAdapter";

	private static TagDbAdapter sInstance;

	private ContentResolver mCr;

	private TagDbAdapter() {

		mCr = TheOneApp.getResolver();
	}

	public static synchronized TagDbAdapter getInstance() {

		if (null == sInstance) {

			sInstance = new TagDbAdapter();
		}

		return sInstance;
	}

	public long insertMBlogTag(MBlogTag tag) {

		long result = -1;

		if (null != tag) {

			Uri uri = URIField.MBLOG_TAG_URI;

			ContentValues cv = TagColumns.setValues(tag);

			Uri resultUri = mCr.insert(uri, cv);

			if (null != resultUri) {

				result = ContentUris.parseId(resultUri);
			}

		} else {
			Log.w(TAG, "insertMBlogTag fail, comment is null...");
		}

		return result;
	}

	public MBlogTag getMBlogTagById(String tagId) {

		MBlogTag tag = null;

		Cursor cursor = null;

		try {

			Uri uri = URIField.MBLOG_TAG_URI;

			cursor = mCr.query(uri, null, TagColumns.ID + "=?",
					new String[] { tagId }, null);

			if (null != cursor && cursor.moveToFirst()) {

				tag = TagColumns.parseCursorToTag(cursor);
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return tag;
	}

	public int deleteTagById(String id) {

		int result = -1;

		Uri uri = URIField.MBLOG_TAG_URI;

		result = mCr.delete(uri, TagColumns.ID + "=?", new String[] { id });

		Log.i(TAG, "deleteTagById, result = " + result);

		return result;
	}
}
