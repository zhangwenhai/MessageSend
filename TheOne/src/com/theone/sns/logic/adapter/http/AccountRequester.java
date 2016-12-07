package com.theone.sns.logic.adapter.http;

import com.google.gson.JsonObject;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.component.http.BaseRequest;
import com.theone.sns.component.http.IHttpListener;
import com.theone.sns.logic.model.account.base.Privacy;
import com.theone.sns.logic.model.user.MeInfo;
import com.theone.sns.util.HttpUtil;

public class AccountRequester extends BaseRequest {

	public AccountRequester(String localRequestId, IHttpListener httpListener) {
		super(localRequestId, httpListener);
	}

	public void Login(String userName, String password) {

		path = HttpUtil.buildURL(FusionConfig.GET_USER_TOKEN_URL);

		method = HttpMethod.POST;

		contentType = "application/x-www-form-urlencoded";

		needAuth = false;

		putFormParameter(AccountParamter.CLIENT_ID,
				FusionConfig.LOGIN_CLIENT_ID_VALUE);

		putFormParameter(AccountParamter.CLIENT_SECRET,
				FusionConfig.LOGIN_CLIENT_SECRET_VALUE);

		putFormParameter(AccountParamter.GRANT_TYPE,
				FusionConfig.LOGIN_GRANT_TYPE_VALUE);

		putFormParameter(AccountParamter.USERNAME, userName);

		putFormParameter(AccountParamter.PASSWORD, password);

		start();
	}

	public void checkPassword(String password) {

		path = FusionConfig.CHECK_PASSWORD_URL;

		method = HttpMethod.POST;

		putJsonParameter(AccountParamter.PASSWORD, password);

		start();
	}

	public void updatePassword(String password) {

		path = FusionConfig.UPDATE_PASSWORD_URL;

		method = HttpMethod.PUT;

		putJsonParameter(AccountParamter.PASSWORD, password);

		start();
	}

	public void getMyUserInfo() {

		path = FusionConfig.CHECK_VERIFIY_CODE_AND_OPERATION_ACCOUNT_URL;

		method = HttpMethod.GET;

		start();
	}

	public void updateMyCharacter(String character) {

		path = FusionConfig.CHECK_VERIFIY_CODE_AND_OPERATION_ACCOUNT_URL;

		method = HttpMethod.PUT;

		putJsonParameter("character", character);

		start();
	}

	public void updateMyUserInfo(MeInfo meInfo) {

		path = FusionConfig.CHECK_VERIFIY_CODE_AND_OPERATION_ACCOUNT_URL;

		method = HttpMethod.PUT;

		setJsonObject((JsonObject) meInfo.toJson());

		start();
	}

	public void updatePrivacy(Privacy privacy) {

		path = FusionConfig.CHECK_VERIFIY_CODE_AND_OPERATION_ACCOUNT_URL;

		method = HttpMethod.PUT;

		putJsonParameter(PrivacyParamter.PRIVACY, (JsonObject) privacy.toJson());

		start();
	}

	public void getMyAllTags() {

		path = FusionConfig.GET_MY_ALL_TAGS_URL;

		method = HttpMethod.GET;

		start();
	}

	public void getInterest() {

		path = FusionConfig.GET_INTEREST_URL;

		method = HttpMethod.GET;

		start();
	}

	public void logout() {

		path = FusionConfig.LOGOUT_URL;

		method = HttpMethod.POST;

		start();
	}

	public interface AccountParamter {

		public static final String CLIENT_ID = "client_id";

		public static final String CLIENT_SECRET = "client_secret";

		public static final String GRANT_TYPE = "grant_type";

		public static final String USERNAME = "username";

		public static final String PASSWORD = "password";
	}

	public interface PrivacyParamter {

		public static final String PRIVACY = "privacy";
	}
}
