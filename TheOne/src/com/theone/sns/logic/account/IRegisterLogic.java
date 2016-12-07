package com.theone.sns.logic.account;

import com.theone.sns.framework.logic.ILogic;

public interface IRegisterLogic extends ILogic {

	/**
	 * 获取验证码
	 * 
	 * @param phoneNumber
	 */
	String getVerifyCode(String phoneNumber);

	/**
	 * 通过邮件重置密码
	 */
	String emailResetPassword(String email);

	/**
	 * 验证验证码是否正确,如果正确创建账号(手机注册)
	 * 
	 * @param verifyCode
	 */
	String checkVerifyCodeAndCreateAccount(String name, String password,
			String phoneNumber, String verifyCode, String register_code);

	/**
	 * 邮箱注册
	 */
	String createEmailAccount(String name, String password, String emailName,
			String register_code);

	/**
	 * 获取注册邀请码
	 */
	String getRegisterInviteCode();

	/**
	 * 验证注册邀请码
	 */
	String verifyRegisterInviteCode(String registerCode);
}
