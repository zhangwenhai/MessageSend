package com.theone.sns.logic.adapter.http;

import android.text.TextUtils;

import com.theone.sns.common.FusionConfig;
import com.theone.sns.component.http.BaseRequest;
import com.theone.sns.component.http.IHttpListener;
import com.theone.sns.util.HttpUtil;

public class RegisterRequester extends BaseRequest {

	private static final String NAME = "name";

	private static final String PHONE_NUMBER = "phone_no";

	private static final String EMAIL = "email";

	private static final String PASSWORD = "password";

	private static final String VERIFY_CODE = "verification_code";

	private static final String VERIFY_REGISTER_INVITE_CODE = "register_code";

	public RegisterRequester(String localRequestId, IHttpListener httpListener) {
		super(localRequestId, httpListener);
	}

	public void getVerifyCode(String phoneNumber) {

		path = FusionConfig.GET_VERIFIY_CODE;

		method = HttpMethod.POST;

		needAuth = false;

		putJsonParameter(PHONE_NUMBER, phoneNumber);

		start();
	}

	public void emailResetPassword(String email) {

		path = FusionConfig.EMAIL_RESET_PASSWORD_URL;

		method = HttpMethod.POST;

		needAuth = false;

		putJsonParameter(EMAIL, email);

		start();
	}

	/**
	 * 手机注册
	 */
	public void checkPhoneVerifyCodeAndCreateAccount(String name,
			String password, String phoneNumber, String verifyCode,
			String register_code) {

		path = FusionConfig.CHECK_VERIFIY_CODE_AND_OPERATION_ACCOUNT_URL;

		method = HttpMethod.POST;

		needAuth = false;

		putJsonParameter(NAME, name);

		putJsonParameter(PASSWORD, password);

		putJsonParameter(PHONE_NUMBER, phoneNumber);

		putJsonParameter(VERIFY_CODE, verifyCode);

		if (!TextUtils.isEmpty(register_code)) {

			putJsonParameter(VERIFY_REGISTER_INVITE_CODE, register_code);
		}

		start();
	}

	/**
	 * 邮箱注册
	 */
	public void createEmailAccount(String name, String password,
			String emailName, String register_code) {

		path = FusionConfig.CHECK_VERIFIY_CODE_AND_OPERATION_ACCOUNT_URL;

		method = HttpMethod.POST;

		needAuth = false;

		putJsonParameter(NAME, name);

		putJsonParameter(PASSWORD, password);

		putJsonParameter(EMAIL, emailName);

		if (!TextUtils.isEmpty(register_code)) {

			putJsonParameter(VERIFY_REGISTER_INVITE_CODE, register_code);
		}

		start();
	}

	/**
	 * 获取注册邀请码
	 */
	public void getRegisterInviteCode() {

		path = HttpUtil.buildPath(FusionConfig.GET_REGISTER_INVITE_CODE_URL);

		method = HttpMethod.GET;

		start();
	}

	public void verifyRegisterInviteCode(String registerCode) {

		path = FusionConfig.VERIFY_REGISTER_INVITE_CODE_URL;

		method = HttpMethod.POST;

		needAuth = false;

		putJsonParameter(VERIFY_REGISTER_INVITE_CODE, registerCode);

		start();
	}
}
