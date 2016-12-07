package com.theone.sns.logic.adapter.db.model;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.theone.sns.common.FusionCode.CommonColumnsValue;
import com.theone.sns.component.database.TheOneDatabaseHelper.Tables;
import com.theone.sns.logic.model.mblog.Filter;
import com.theone.sns.logic.model.mblog.base.Age;
import com.theone.sns.logic.model.mblog.base.Height;
import com.theone.sns.util.StringUtil;

public class FilterColumns {

	private static final String TAG = "FilterColumns";

	public static final String ID = "_id";

	public static final String ENABLED = "enabled";

	public static final String AVATAR_MODE_ENABLED = "avatar_mode_enabled";

	public static final String ROLE = "role";

	public static final String PURPOSES = "purposes";

	public static final String ONLINE = "online";

	public static final String REGION = "region";

	public static final String AGE_MIN = "age_min";

	public static final String AGE_MAX = "age_max";

	public static final String HEIGHT_MIN = "height_min";

	public static final String HEIGHT_MAX = "height_max";

	public static final String INTERESTS = "interests";

	public static Filter parseCursorToFilter(Cursor cursor) {

		Filter filter = new Filter();

		filter.enabled = cursor.getInt(cursor.getColumnIndex(ENABLED)) == CommonColumnsValue.TRUE_VALUE;

		filter.avatar_mode_enabled = cursor.getInt(cursor
				.getColumnIndex(AVATAR_MODE_ENABLED)) == CommonColumnsValue.TRUE_VALUE;

		filter.role = StringUtil.StringToList(cursor.getString(cursor
				.getColumnIndex(ROLE)));

		filter.purposes = StringUtil.StringToList(cursor.getString(cursor
				.getColumnIndex(PURPOSES)));

		filter.online = cursor.getInt(cursor.getColumnIndex(ONLINE)) == CommonColumnsValue.TRUE_VALUE;

		filter.region = cursor.getString(cursor.getColumnIndex(REGION));

		filter.age = getAge(cursor);

		filter.height = getHeight(cursor);

		filter.interests = StringUtil.StringToList(cursor.getString(cursor
				.getColumnIndex(INTERESTS)));

		return filter;
	}

	public static ContentValues setValues(Filter filter) {

		ContentValues cv = new ContentValues();

		cv.put(ENABLED, filter.enabled ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		cv.put(AVATAR_MODE_ENABLED,
				filter.avatar_mode_enabled ? CommonColumnsValue.TRUE_VALUE
						: CommonColumnsValue.FALSE_VALUE);

		cv.put(ONLINE, filter.online ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		cv.put(REGION, filter.region);

		if (null != filter.age) {

			cv.put(AGE_MIN, filter.age.min);

			cv.put(AGE_MAX, filter.age.max);
		}

		if (null != filter.height) {

			cv.put(HEIGHT_MIN, filter.height.min);

			cv.put(HEIGHT_MAX, filter.height.max);
		}

		putListString(cv, INTERESTS, filter.interests);

		putListString(cv, ROLE, filter.role);

		putListString(cv, PURPOSES, filter.purposes);

		return cv;
	}

	private static void putListString(ContentValues cv, String columns,
			List<String> values) {

		if (null != values && !values.isEmpty()) {

			cv.put(columns, StringUtil.listToString(values));
		}
	}

	private static Age getAge(Cursor cursor) {

		Age age = new Age();

		age.min = cursor.getInt(cursor.getColumnIndex(AGE_MIN));

		age.max = cursor.getInt(cursor.getColumnIndex(AGE_MAX));

		return age;
	}

	private static Height getHeight(Cursor cursor) {

		Height height = new Height();

		height.min = cursor.getInt(cursor.getColumnIndex(HEIGHT_MIN));

		height.max = cursor.getInt(cursor.getColumnIndex(HEIGHT_MAX));

		return height;
	}

	public static void createFilterTable(SQLiteDatabase db) {

		StringBuffer sql = new StringBuffer();

		sql.append(" create table if not exists ").append(Tables.MBLOG_FILTER);
		sql.append(" ( ");

		sql.append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sql.append(ENABLED).append(" INTEGER NOT NULL DEFAULT 0, ");
		sql.append(AVATAR_MODE_ENABLED).append(" INTEGER NOT NULL DEFAULT 0, ");
		sql.append(ROLE).append(" TEXT, ");
		sql.append(PURPOSES).append(" TEXT, ");
		sql.append(ONLINE).append(" INTEGER NOT NULL DEFAULT 0, ");
		sql.append(REGION).append(" TEXT, ");
		sql.append(AGE_MIN).append(" INTEGER NOT NULL DEFAULT 0, ");
		sql.append(AGE_MAX).append(" INTEGER NOT NULL DEFAULT 0, ");
		sql.append(HEIGHT_MIN).append(" INTEGER NOT NULL DEFAULT 0, ");
		sql.append(HEIGHT_MAX).append(" INTEGER NOT NULL DEFAULT 0, ");
		sql.append(INTERESTS).append(" TEXT ");

		sql.append(" );");

		db.execSQL(sql.toString());

		Log.d(TAG, "create " + Tables.MBLOG_FILTER + " success!");
	}
}