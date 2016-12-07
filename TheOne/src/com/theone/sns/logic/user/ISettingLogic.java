package com.theone.sns.logic.user;

public interface ISettingLogic {

	boolean getBoolean(String key, boolean defaultValue);

	long set(String key, boolean value);
}
