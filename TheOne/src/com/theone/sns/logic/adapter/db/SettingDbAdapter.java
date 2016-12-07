package com.theone.sns.logic.adapter.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.theone.sns.TheOneApp;
import com.theone.sns.common.FusionCode.CommonColumnsValue;
import com.theone.sns.component.database.TheOneDatabaseHelper;
import com.theone.sns.component.database.URIField;
import com.theone.sns.logic.adapter.db.model.SettingColumns;
import com.theone.sns.logic.model.user.Setting;

public class SettingDbAdapter {

	private static final String TAG = "SettingDbAdapter";

	private static SettingDbAdapter sInstance;

	private ContentResolver mCr;

	private SettingDbAdapter() {

		mCr = TheOneApp.getResolver();
	}

	public static synchronized SettingDbAdapter getInstance() {

		if (null == sInstance) {

			sInstance = new SettingDbAdapter();
		}

		return sInstance;
	}

	public Setting get(String key) {

		if (TextUtils.isEmpty(key)) {

			Log.e(TAG, "get fail, key is null");

			return null;
		}

		Cursor cursor = null;

		Setting setting = null;

		Uri uri = URIField.SETTING_URI;

		try {

			cursor = mCr.query(uri, null, SettingColumns.KEY + "=?",
					new String[] { key }, null);

			if (null != cursor && cursor.moveToFirst()) {

				setting = SettingColumns.parseCursorToSetting(cursor);
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return setting;
	}

	public boolean getBoolean(String key, boolean defaultValue) {

		boolean value = defaultValue;

		Setting setting = get(key);

		if (null != setting) {

			value = (Integer.valueOf(setting.value) == CommonColumnsValue.TRUE_VALUE);
		}

		return value;
	}

	public int getInt(String key) {

		int value = 0;

		Setting setting = get(key);

		if (null != setting) {

			value = Integer.valueOf(setting.value);
		}

		return value;
	}

	public String getString(String key) {

		String value = "";

		Setting setting = get(key);

		if (null != setting) {

			value = setting.value;
		}

		return value;
	}

	public long insert(String key, boolean value) {

		long result = -1;

		if (TextUtils.isEmpty(key)) {

			Log.e(TAG, "set, key is null");

			return result;
		}

		Setting setting = new Setting();

		setting.key = key;

		setting.value = (value ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE) + "";

		result = insert(setting);

		return result;
	}

	public long insert(String key, int value) {

		long result = -1;

		if (TextUtils.isEmpty(key)) {

			Log.e(TAG, "set, key is null");

			return result;
		}

		Setting setting = new Setting();

		setting.key = key;

		setting.value = value + "";

		result = insert(setting);

		return result;
	}

	public long insert(String key, String value) {

		long result = -1;

		if (TextUtils.isEmpty(key)) {

			Log.e(TAG, "set, key is null");

			return result;
		}

		Setting setting = new Setting();

		setting.key = key;

		setting.value = value;

		result = insert(setting);

		return result;
	}

	private long insert(Setting setting) {

		if (null == setting) {

			Log.e(TAG, "insert fail, setting is null");

			return -1;
		}

		Setting tempSetting = get(setting.key);

		if (null != tempSetting) {

			return updateSetting(setting);
		}

		return insertSetting(setting);
	}

	private long insertSetting(Setting setting) {

		long result = -1;

		if (null == setting) {

			Log.e(TAG, "insertSetting fail, setting is null");

			return result;
		}

		Uri uri = URIField.SETTING_URI;

		ContentValues cv = SettingColumns.setValues(setting);

		Uri resultUri = mCr.insert(uri, cv);

		if (null != resultUri) {

			result = ContentUris.parseId(resultUri);
		}

		return result;
	}

	private int updateSetting(Setting setting) {

		int result = -1;

		if (null == setting) {

			Log.e(TAG, "updateSetting fail, setting is null");

			return result;
		}

		Uri uri = URIField.SETTING_URI;

		ContentValues cv = SettingColumns.updateValues(setting);

		result = mCr.update(uri, cv, SettingColumns.KEY + "=?",
				new String[] { setting.key });

		return result;
	}
}
