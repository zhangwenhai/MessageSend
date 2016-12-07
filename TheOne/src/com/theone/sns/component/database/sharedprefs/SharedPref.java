package com.theone.sns.component.database.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPref {

	private SharedPreferences mSharedPreferences = null;

	private Editor mEditor = null;

	private static SharedPref sharePref = null;

	public synchronized static SharedPref getInstance(Context context) {

		if (null == sharePref) {

			sharePref = new SharedPref(context);
		}

		return sharePref;
	}

	private SharedPref(Context context) {

		mSharedPreferences = context.getSharedPreferences(
				context.getPackageName(), Context.MODE_PRIVATE);
	}

	public String getString(String key, String devValue) {

		return mSharedPreferences.getString(key, devValue);
	}

	public boolean getBoolean(String key, boolean defValue) {

		return mSharedPreferences.getBoolean(key, defValue);
	}

	public int getInt(String key, int defValue) {

		return mSharedPreferences.getInt(key, defValue);
	}

	public long getLong(String key, long defValue) {

		return mSharedPreferences.getLong(key, defValue);
	}

	public float getFloat(String key, float defValue) {

		return mSharedPreferences.getFloat(key, defValue);
	}

	public boolean put(String key, String value) {

		mEditor = mSharedPreferences.edit();

		mEditor.putString(key, value);

		return mEditor.commit();
	}

	public boolean put(String key, boolean value) {

		mEditor = mSharedPreferences.edit();

		mEditor.putBoolean(key, value);

		return mEditor.commit();
	}

	public boolean put(String key, float value) {

		mEditor = mSharedPreferences.edit();

		mEditor.putFloat(key, value);

		return mEditor.commit();
	}

	public boolean put(String key, long value) {

		mEditor = mSharedPreferences.edit();

		mEditor.putLong(key, value);

		return mEditor.commit();
	}

	public boolean put(String key, int value) {

		mEditor = mSharedPreferences.edit();

		mEditor.putInt(key, value);

		return mEditor.commit();
	}

	public boolean removeKey(String key) {

		mEditor = mSharedPreferences.edit();

		mEditor.remove(key);

		return mEditor.commit();
	}

	public boolean clear() {

		mEditor.clear();

		return mEditor.commit();
	}
}
