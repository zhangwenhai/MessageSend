package com.theone.sns.logic.adapter.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.theone.sns.component.database.TheOneDatabaseHelper.Tables;
import com.theone.sns.logic.model.mblog.MBlogTag;
import com.theone.sns.logic.model.mblog.base.LocationTag;
import com.theone.sns.logic.model.mblog.base.TextTag;
import com.theone.sns.logic.model.mblog.base.UserTag;

public class TagColumns {

	private static final String TAG = "TagColumns";

	public static final String ID = "_id";

	public static final String TEXT_TAG_NAME = "text_tag_name";

	public static final String LOCATION_TAG_NAME = "location_tag_name";

	public static final String USER_TAG_NAME = "user_tag_name";

	public static final String USER_TAG_USERID = "user_tag_userId";

	public static final String X = "x";

	public static final String Y = "y";

	public static final String ALIGN = "align";

	public static MBlogTag parseCursorToTag(Cursor cursor) {

		MBlogTag mBlogTag = new MBlogTag();

		mBlogTag.id = cursor.getString(cursor.getColumnIndex(ID));

		String textTagName = cursor.getString(cursor
				.getColumnIndex(TEXT_TAG_NAME));

		String locationTagName = cursor.getString(cursor
				.getColumnIndex(LOCATION_TAG_NAME));

		String userTagName = cursor.getString(cursor
				.getColumnIndex(USER_TAG_NAME));

		String userTagUserId = cursor.getString(cursor
				.getColumnIndex(USER_TAG_USERID));

		if (!TextUtils.isEmpty(textTagName)) {

			TextTag textTag = new TextTag();

			textTag.name = textTagName;

			mBlogTag.text = textTag;

		} else if (!TextUtils.isEmpty(locationTagName)) {

			LocationTag locationTag = new LocationTag();

			locationTag.name = locationTagName;

			mBlogTag.location = locationTag;

		} else if (!TextUtils.isEmpty(userTagName)
				&& !TextUtils.isEmpty(userTagUserId)) {

			UserTag userTag = new UserTag();

			userTag.name = userTagName;

			userTag.user_id = userTagUserId;

			mBlogTag.user = userTag;
		}

		mBlogTag.x = cursor.getFloat(cursor.getColumnIndex(X));

		mBlogTag.y = cursor.getFloat(cursor.getColumnIndex(Y));

		mBlogTag.align = cursor.getString(cursor.getColumnIndex(ALIGN));

		return mBlogTag;
	}

	public static ContentValues setValues(MBlogTag mBlogTag) {

		ContentValues cv = new ContentValues();

		TextTag textTag = mBlogTag.text;

		if (null != textTag) {

			cv.put(TEXT_TAG_NAME, textTag.name);
		}

		LocationTag locationTag = mBlogTag.location;

		if (null != locationTag) {

			cv.put(LOCATION_TAG_NAME, locationTag.name);
		}

		UserTag userTag = mBlogTag.user;

		if (null != userTag) {

			cv.put(USER_TAG_NAME, userTag.name);

			cv.put(USER_TAG_USERID, userTag.user_id);
		}

		cv.put(X, mBlogTag.x);

		cv.put(Y, mBlogTag.y);

		cv.put(ALIGN, mBlogTag.align);

		return cv;
	}

	public static void createTagTable(SQLiteDatabase db) {

		StringBuffer sql = new StringBuffer();

		sql.append(" create table if not exists ").append(Tables.MBLOG_TAG);
		sql.append(" ( ");

		sql.append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sql.append(TEXT_TAG_NAME).append(" TEXT, ");
		sql.append(LOCATION_TAG_NAME).append(" TEXT, ");
		sql.append(USER_TAG_NAME).append(" TEXT, ");
		sql.append(USER_TAG_USERID).append(" TEXT, ");
		sql.append(X).append(" TEXT, ");
		sql.append(Y).append(" TEXT, ");
		sql.append(ALIGN).append(" TEXT ");

		sql.append(" );");

		db.execSQL(sql.toString());

		Log.d(TAG, "create " + Tables.MBLOG_TAG + " success!");
	}
}