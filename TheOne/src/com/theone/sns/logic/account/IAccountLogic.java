package com.theone.sns.logic.account;

import com.theone.sns.framework.logic.ILogic;
import com.theone.sns.logic.model.account.base.Privacy;
import com.theone.sns.logic.model.user.MeInfo;
import com.theone.sns.logic.model.user.User;

public interface IAccountLogic extends ILogic {

	/**
	 * 登陆
	 */
	String login(String loginAccount, String password);

	/**
	 * 验证验证码(忘记密码)
	 */
	String checkVerifyCode(String phoneNumber, String verifyCode);

	/**
	 * 验证用户密码(重置密码)
	 */
	String checkPassword(String password);

	/**
	 * 更新用户密码
	 */
	String updatePassword(String password);

	/**
	 * 从数据库获取用户自己信息(Me Tab页面展示自己信息)
	 */
	User getMyUserInfoFromDB();

	/**
	 * 获取用户自己信息(Me Tab页面展示自己信息)
	 */
	String getMyUserInfo();

	/**
	 * 更新用户自己属性
	 */
	String updateMyCharacter(String character);

	/**
	 * 更新用户自己信息(用户注册成功后设置自己的信息)
	 */
	String updateMyUserInfo(MeInfo meInfo);

	/**
	 * 更新用户设置(用户注册成功后设置自己的信息)
	 */
	String updatePrivacy(Privacy privacy);

	/**
	 * 获取用户自己所有的Tag
	 */
	String getMyAllTags();

	/**
	 * 获取兴趣标签
	 */
	String getInterest();
}
