package com.theone.sns.logic.adapter.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.theone.sns.TheOneApp;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.component.database.TheOneDatabaseHelper;
import com.theone.sns.component.database.URIField;
import com.theone.sns.logic.adapter.db.model.UserColumns;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.model.user.base.Gallery;

public class UserDbAdapter {

	private static final String TAG = "UserDbAdapter";

	private static UserDbAdapter sInstance;

	private ContentResolver mCr;

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            上下文
	 */
	private UserDbAdapter() {

		mCr = TheOneApp.getResolver();
	}

	public static synchronized UserDbAdapter getInstance() {

		if (null == sInstance) {

			sInstance = new UserDbAdapter();
		}

		return sInstance;
	}

	public List<User> getAllUser(String type) {

		List<User> userList = new ArrayList<User>();

		Cursor cursor = null;

		Uri uri = URIField.USER_URI;

		try {

			cursor = mCr.query(uri, null, UserColumns.TYPE + "=?",
					new String[] { type }, null);

			while (cursor.moveToNext()) {

				User user = UserColumns.parseCursorToUser(cursor);

				if (null != user) {

					userList.add(user);
				}
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return userList;
	}

	public long insert(User user) {

		if (null == user) {

			Log.e(TAG, "insertUser fail, user is null");

			return -1;
		}

		User tempUser = getUserByUserId(user.userId);

		if (null != tempUser) {

			return updateUser(user);
		}

		return insertUser(user);
	}

	public long insertUser(User user) {

		long result = -1;

		if (null == user) {

			Log.e(TAG, "insertUser fail, user is null");

			return result;
		}

		Uri uri = URIField.USER_URI;

		if (null != getUser(user.userId, user.type)) {

			return updateUser(user);
		}

		ContentValues cv = UserColumns.setValues(user);

		Uri resultUri = mCr.insert(uri, cv);

		if (null != resultUri) {

			result = ContentUris.parseId(resultUri);
		}

		if (-1 != result) {

			List<Gallery> galleryList = user.gallery;

			GalleryDbAdapter.getInstance().insertGalleryList(galleryList);
		}

		return result;
	}

	public int updateUser(User user) {

		int result = -1;

		if (null == user) {

			Log.e(TAG, "updateUser fail, user is null");

			return result;
		}

		Uri uri = URIField.USER_URI;

		ContentValues cv = UserColumns.updateValues(user);

		result = mCr.update(uri, cv, UserColumns.USER_ID + "=?",
				new String[] { user.userId });

		return result;
	}

	public User getUserByUserId(String userId) {

		if (TextUtils.isEmpty(userId)) {

			Log.e(TAG, "getUserByUserId fail, userId is null");

			return null;
		}

		User user = null;

		Cursor cursor = null;

		Uri uri = URIField.USER_URI;

		try {

			cursor = mCr.query(uri, null, UserColumns.USER_ID + "=?",
					new String[] { userId }, null);

			if (null != cursor && cursor.moveToFirst()) {

				user = UserColumns.parseCursorToUser(cursor);
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return user;
	}

	public User getUser(String userId, String type) {

		if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(type)) {

			Log.e(TAG, "getUser fail, parameter is null");

			return null;
		}

		User user = null;

		Cursor cursor = null;

		Uri uri = URIField.USER_URI;

		try {

			cursor = mCr.query(uri, null, UserColumns.USER_ID + "=? AND "
					+ UserColumns.TYPE + "=?", new String[] { userId, type },
					null);

			if (null != cursor && cursor.moveToFirst()) {

				user = UserColumns.parseCursorToUser(cursor);
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return user;
	}

	public int deleteUserByUserId(String userId) {

		int result = -1;

		if (TextUtils.isEmpty(userId)
				|| userId.equals(FusionConfig.getInstance().getUserId())) {

			return result;
		}

		Uri uri = URIField.USER_URI;

		result = mCr.delete(uri, UserColumns.USER_ID + "=?",
				new String[] { userId });

		Log.i(TAG, "deleteUserByUserId, result = " + result);

		return result;
	}

	public int deleteAllUserByType(String type) {

		int result = -1;

		if (TextUtils.isEmpty(type)) {

			return result;
		}

		Uri uri = URIField.USER_URI;

		result = mCr
				.delete(uri, UserColumns.TYPE + "=?", new String[] { type });

		Log.i(TAG, "deleteAllUserByType, result = " + result);

		return result;
	}
}
