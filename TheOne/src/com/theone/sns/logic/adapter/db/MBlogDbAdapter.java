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
import com.theone.sns.logic.adapter.db.model.MBlogColumns;
import com.theone.sns.logic.model.mblog.MBlog;

public class MBlogDbAdapter {

	private static final String TAG = "MBlogDbAdapter";

	private static MBlogDbAdapter sInstance;

	private ContentResolver mCr;

	private MBlogDbAdapter() {

		mCr = TheOneApp.getResolver();
	}

	public static synchronized MBlogDbAdapter getInstance() {

		if (null == sInstance) {

			sInstance = new MBlogDbAdapter();
		}

		return sInstance;
	}

	public long insertMBlog(MBlog mblog) {

		long result = -1;

		if (null != mblog) {

			if (null != getMBlogById(mblog._id, mblog.mblog_type)) {

				return -1;
			}

			Uri uri = URIField.MBLOG_URI;

			ContentValues cv = MBlogColumns.setValues(mblog);

			Uri resultUri = mCr.insert(uri, cv);

			if (null != resultUri) {

				result = ContentUris.parseId(resultUri);
			}

		} else {
			Log.w(TAG, "insertMBlog fail, mblog is null...");
		}

		return result;
	}

	public List<MBlog> getAllMBlog(String type) {

		List<MBlog> mBlogList = new ArrayList<MBlog>();

		Cursor cursor = null;

		Uri uri = URIField.MBLOG_URI;

		try {

			cursor = mCr.query(uri, null, MBlogColumns.MBLOG_TYPE + "=?",
					new String[] { type }, null);

			while (cursor.moveToNext()) {

				MBlog mBlog = MBlogColumns.parseCursorToMBlog(cursor);

				if (null != mBlog) {

					mBlogList.add(mBlog);
				}
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return mBlogList;
	}

	public MBlog getMBlogById(String mblogId, String mblogType) {

		MBlog mBlog = null;

		Cursor cursor = null;

		Uri uri = URIField.MBLOG_URI;

		try {

			cursor = mCr.query(uri, null, MBlogColumns.MBLOGID + "=? AND "
					+ MBlogColumns.MBLOG_TYPE + "=?", new String[] { mblogId,
					mblogType }, null);

			if (null != cursor && cursor.moveToFirst()) {

				mBlog = MBlogColumns.parseCursorToMBlog(cursor);
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return mBlog;
	}

	public int deleteAllMBlog(String type) {

		int result = -1;

		Uri uri = URIField.MBLOG_URI;

		result = mCr.delete(uri, MBlogColumns.MBLOG_TYPE + "=?",
				new String[] { type });

		return result;
	}
}
