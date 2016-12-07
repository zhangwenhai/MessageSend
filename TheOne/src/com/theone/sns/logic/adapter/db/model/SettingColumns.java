package com.theone.sns.logic.adapter.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.theone.sns.component.database.TheOneDatabaseHelper.Tables;
import com.theone.sns.logic.model.user.Setting;

public class SettingColumns {

	private static final String TAG = "SettingColumns";

	public static final String ID = "_id";

	public final static String KEY = "key";

	public final static String VALUE = "value";

	public static Setting parseCursorToSetting(Cursor cursor) {

		Setting setting = new Setting();

		setting.id = cursor.getString(cursor.getColumnIndex(ID));

		setting.key = cursor.getString(cursor.getColumnIndex(KEY));

		setting.value = cursor.getString(cursor.getColumnIndex(VALUE));

		return setting;
	}

	public static ContentValues setValues(Setting setting) {

		ContentValues cv = new ContentValues();

		cv.put(KEY, setting.key);

		cv.put(VALUE, setting.value);

		return cv;
	}

	public static ContentValues updateValues(Setting setting) {

		ContentValues cv = new ContentValues();

		cv.put(VALUE, setting.value);

		return cv;
	}

	public static void createSettingTable(SQLiteDatabase db) {

		StringBuffer sql = new StringBuffer();

		sql.append(" create table if not exists ").append(Tables.SETTING);
		sql.append(" ( ");

		sql.append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sql.append(KEY).append(" TEXT, ");
		sql.append(VALUE).append(" TEXT ");

		sql.append(" );");

		db.execSQL(sql.toString());

		Log.d(TAG, "create " + Tables.SETTING + " success!");
	}
}