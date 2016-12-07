package com.theone.sns.logic.account.impl;

import android.text.TextUtils;
import android.util.Log;

import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType.AccountMessageType;
import com.theone.sns.component.http.FailResult;
import com.theone.sns.component.http.IHttpListener;
import com.theone.sns.component.http.Result;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.framework.logic.BaseLogic;
import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.account.IRegisterLogic;
import com.theone.sns.logic.adapter.http.RegisterRequester;
import com.theone.sns.logic.model.account.Account;
import com.theone.sns.logic.model.account.RegisterInviteCode;
import com.theone.sns.util.HttpUtil;
import com.theone.sns.util.StringUtil;

public class RegisterLogicImpl extends BaseLogic implements IRegisterLogic {

	private static final String TAG = "RegisterLogicImpl";

	@Override
	public String getVerifyCode(String phoneNumber) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(phoneNumber)) {
			Log.e(TAG, "in method getVerifyCode phoneNumber is null");
			return requestId;
		}

		String phone = FusionConfig.COUNTRY_CODE
				+ StringUtil.fixPortalPhoneNumber(phoneNumber,
						FusionConfig.COUNTRY_CODE);

		new RegisterRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {
					sendEmptyMessage(AccountMessageType.GET_VERIFY_CODE_SUCCESS);
				} else {
					sendEmptyMessage(AccountMessageType.GET_VERIFY_CODE_FAIL);
				}
			}

		}).getVerifyCode(phone);

		return requestId;
	}

	@Override
	public String emailResetPassword(String email) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(email)) {

			Log.e(TAG, "in method emailResetPassword email is null");

			return requestId;
		}

		new RegisterRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					sendEmptyMessage(AccountMessageType.EMAIL_RESET_PASSWORD_SUCCESS);

				} else {

					sendEmptyMessage(AccountMessageType.EMAIL_RESET_PASSWORD_FAIL);
				}
			}

		}).emailResetPassword(email);

		return requestId;
	}

	public String checkVerifyCodeAndCreateAccount(String name, String password,
			String phoneNumber, String verifyCode, String register_code) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)
				|| TextUtils.isEmpty(verifyCode)) {
			Log.e(TAG,
					"in method checkVerifyCodeAndCreateAccount parameters is null");
			return requestId;
		}

		String phone = FusionConfig.COUNTRY_CODE
				+ StringUtil.fixPortalPhoneNumber(phoneNumber,
						FusionConfig.COUNTRY_CODE);

		new RegisterRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {
				if (HttpUtil.checkStatusCode(result.statusCode)) {

					Account curAccount = (Account) BaseModel.fromJson(
							result.jsonObject, Account.class);

					if (null != curAccount) {

						FusionConfig.getInstance().saveAccountFromServer(
								curAccount);

						sendEmptyMessage(AccountMessageType.CHECK_VERIFY_CODE_AND_CREATE_ACCOUNT_SUCCESS);

					} else {

						sendEmptyMessage(AccountMessageType.CHECK_VERIFY_CODE_AND_CREATE_ACCOUNT_FAIL);
					}

				} else {

					FailResult failResult = (FailResult) BaseModel.fromJson(
							result.jsonObject, FailResult.class);

					sendMessage(
							AccountMessageType.CHECK_VERIFY_CODE_AND_CREATE_ACCOUNT_FAIL,
							new UIObject(result.localRequestId, failResult));
				}
			}

		}).checkPhoneVerifyCodeAndCreateAccount(name, password, phone,
				verifyCode, register_code);

		return requestId;
	}

	@Override
	public String createEmailAccount(String name, String password,
			String emailName, String register_code) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)
				|| TextUtils.isEmpty(emailName)
				|| !StringUtil.isEmail(emailName)) {

			Log.e(TAG, "in method createEmailAccount parameters is error");

			return requestId;
		}

		new RegisterRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {
				if (HttpUtil.checkStatusCode(result.statusCode)) {

					Account curAccount = (Account) BaseModel.fromJson(
							result.jsonObject, Account.class);

					if (null != curAccount) {

						FusionConfig.getInstance().saveAccountFromServer(
								curAccount);

						sendEmptyMessage(AccountMessageType.CREATE_EMAIL_ACCOUNT_SUCCESS);

					} else {

						sendEmptyMessage(AccountMessageType.CREATE_EMAIL_ACCOUNT_FAIL);
					}

				} else {

					FailResult failResult = (FailResult) BaseModel.fromJson(
							result.jsonObject, FailResult.class);

					sendMessage(AccountMessageType.CREATE_EMAIL_ACCOUNT_FAIL,
							new UIObject(result.localRequestId, failResult));
				}
			}

		}).createEmailAccount(name, password, emailName.trim(), register_code);

		return requestId;
	}

	@Override
	public String getRegisterInviteCode() {

		String requestId = StringUtil.getRequestSerial();

		new RegisterRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					RegisterInviteCode registerInviteCode = (RegisterInviteCode) BaseModel
							.fromJson(result.jsonObject,
									RegisterInviteCode.class);

					if (null != registerInviteCode) {

						sendMessage(
								AccountMessageType.GET_REGISTER_INVITE_CODE_SUCCESS,
								new UIObject(result.localRequestId,
										registerInviteCode));
					} else {

						sendEmptyMessage(AccountMessageType.GET_REGISTER_INVITE_CODE_FAIL);
					}

				} else {

					sendEmptyMessage(AccountMessageType.GET_REGISTER_INVITE_CODE_FAIL);
				}
			}

		}).getRegisterInviteCode();

		return requestId;
	}

	@Override
	public String verifyRegisterInviteCode(String registerCode) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(registerCode)) {

			Log.e(TAG,
					"in method verifyRegisterInviteCode registerCode is null");

			return requestId;
		}

		new RegisterRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					sendEmptyMessage(AccountMessageType.VERIFY_REGISTER_INVITE_CODE_SUCCESS);

				} else {

					sendEmptyMessage(AccountMessageType.VERIFY_REGISTER_INVITE_CODE_FAIL);
				}
			}

		}).verifyRegisterInviteCode(registerCode);

		return requestId;
	}
}
