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
import com.theone.sns.logic.adapter.db.model.FilterColumns;
import com.theone.sns.logic.model.mblog.Filter;

public class FilterDbAdapter {

	private static final String TAG = "FilterDbAdapter";

	private static FilterDbAdapter sInstance;

	private ContentResolver mCr;

	private FilterDbAdapter() {

		mCr = TheOneApp.getResolver();
	}

	public static synchronized FilterDbAdapter getInstance() {

		if (null == sInstance) {

			sInstance = new FilterDbAdapter();
		}

		return sInstance;
	}

	public long insertFilter(Filter filter) {

		long result = -1;

		if (null != filter) {

			Uri uri = URIField.MBLOG_FILTER_URI;

			ContentValues cv = FilterColumns.setValues(filter);

			Uri resultUri = mCr.insert(uri, cv);

			if (null != resultUri) {

				result = ContentUris.parseId(resultUri);
			}

		} else {
			Log.w(TAG, "insertFilter fail, filter is null");
		}

		return result;
	}

	public Filter getFilter() {

		Filter filter = null;

		Cursor cursor = null;

		try {

			Uri uri = URIField.MBLOG_FILTER_URI;

			cursor = mCr.query(uri, null, null, null, null);

			if (null != cursor && cursor.moveToFirst()) {

				filter = FilterColumns.parseCursorToFilter(cursor);
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return filter;
	}

	public int deleteFilter() {

		int result = -1;

		Uri uri = URIField.MBLOG_FILTER_URI;

		result = mCr.delete(uri, null, null);

		Log.i(TAG, "deleteFilter, result = " + result);

		return result;
	}
}
