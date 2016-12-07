package com.theone.sns.logic.user.impl;

import android.text.TextUtils;
import android.util.Log;

import com.theone.sns.common.FusionCode.CommonColumnsValue;
import com.theone.sns.framework.logic.BaseLogic;
import com.theone.sns.logic.adapter.db.SettingDbAdapter;
import com.theone.sns.logic.model.user.Setting;
import com.theone.sns.logic.user.ISettingLogic;

public class SettingLogicImpl extends BaseLogic implements ISettingLogic {

	private static final String TAG = "SettingLogicImpl";

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {

		boolean value = defaultValue;

		if (TextUtils.isEmpty(key)) {

			Log.e(TAG, "getBoolean, key is null");

			return value;
		}

		Setting setting = SettingDbAdapter.getInstance().get(key);

		if (null != setting) {
			value = (Integer.valueOf(setting.value) == CommonColumnsValue.TRUE_VALUE);
		}

		return value;
	}

	@Override
	public long set(String key, boolean value) {

		long result = -1;

		if (TextUtils.isEmpty(key)) {

			Log.e(TAG, "set, key is null");

			return result;
		}

		result = SettingDbAdapter.getInstance().insert(key, value);

		return result;
	}
}
