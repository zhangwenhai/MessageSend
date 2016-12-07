package com.theone.sns.logic.account.impl;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.theone.sns.common.FusionCode.AccountStatusCode;
import com.theone.sns.common.FusionCode.SettingKey;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType.AccountMessageType;
import com.theone.sns.component.http.IHttpListener;
import com.theone.sns.component.http.Result;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.framework.logic.BaseLogic;
import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.account.IAccountLogic;
import com.theone.sns.logic.adapter.db.AccountDbAdapter;
import com.theone.sns.logic.adapter.db.SettingDbAdapter;
import com.theone.sns.logic.adapter.db.UserDbAdapter;
import com.theone.sns.logic.adapter.http.AccountRequester;
import com.theone.sns.logic.chat.IPushListener;
import com.theone.sns.logic.chat.impl.ChatManager;
import com.theone.sns.logic.model.account.Account;
import com.theone.sns.logic.model.account.Tag;
import com.theone.sns.logic.model.account.base.Interest;
import com.theone.sns.logic.model.account.base.Privacy;
import com.theone.sns.logic.model.user.MeInfo;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.util.HttpUtil;
import com.theone.sns.util.StringUtil;

public class AccountLogicImpl extends BaseLogic implements IAccountLogic {

	private static final String TAG = "AccountLogicImpl";

	private String account;

	@Override
	public String login(String loginAccount, String password) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(loginAccount) || TextUtils.isEmpty(password)) {
			Log.e(TAG, "in method login parameters is null");
			return requestId;
		}

		if (StringUtil.isEmail(loginAccount)) {

			account = loginAccount.trim();

		} else if (StringUtil.isMobile(loginAccount)) {

			account = FusionConfig.COUNTRY_CODE
					+ StringUtil.fixPortalPhoneNumber(loginAccount,
							FusionConfig.COUNTRY_CODE);
		} else {

			Log.e(TAG, "in method login account error");
			return requestId;
		}

		new AccountRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					Account curAccount = (Account) BaseModel.fromJson(
							result.jsonObject, Account.class);

					if (null != curAccount) {

						FusionConfig.getInstance().saveAccountFromServer(
								curAccount);

						String character = curAccount.profile.character;

						String avatar_url = curAccount.profile.avatar_url;

						if (TextUtils.isEmpty(character)) {

							sendEmptyMessage(AccountMessageType.LOGIN_SUCCESS_NEED_SET_CHARACTER);

						} else if (TextUtils.isEmpty(avatar_url)
								|| FusionConfig.DEFAULT_AVATAR_URL
										.equals(avatar_url)) {

							sendEmptyMessage(AccountMessageType.LOGIN_SUCCESS_NEED_SET_USERINFO);

						} else {

							sendEmptyMessage(AccountMessageType.LOGIN_SUCCESS);
						}

					} else {

						sendEmptyMessage(AccountMessageType.LOGIN_FAIL);
					}

				} else {

					sendEmptyMessage(AccountMessageType.LOGIN_FAIL);
				}
			}

		}).Login(account, password);

		return requestId;
	}

	@Override
	public String checkVerifyCode(String phoneNumber, String verifyCode) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(verifyCode)) {
			Log.e(TAG, "in method checkVerifyCode parameters is null");
			return requestId;
		}

		String phone = phoneNumber;

		if (StringUtil.isMobile(phoneNumber)) {

			phone = FusionConfig.COUNTRY_CODE
					+ StringUtil.fixPortalPhoneNumber(phoneNumber,
							FusionConfig.COUNTRY_CODE);
		} else {

			Log.e(TAG, "in method checkVerifyCode phone error");
			return requestId;
		}

		new AccountRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					Account curAccount = (Account) BaseModel.fromJson(
							result.jsonObject, Account.class);

					if (null != curAccount) {

						FusionConfig.getInstance().saveAccountFromServer(
								curAccount);

						sendEmptyMessage(AccountMessageType.CHECK_VERIFY_CODE_SUCCESS);

					} else {

						sendEmptyMessage(AccountMessageType.CHECK_VERIFY_CODE_FAIL);
					}

				} else {

					sendEmptyMessage(AccountMessageType.CHECK_VERIFY_CODE_FAIL);
				}
			}

		}).Login(phone, verifyCode);

		return requestId;
	}

	@Override
	public String checkPassword(String password) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(password)) {
			Log.e(TAG, "in method checkPassword , password is null");
			return requestId;
		}

		new AccountRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					sendEmptyMessage(AccountMessageType.CHECK_PASSWORD_SUCCESS);

				} else if (AccountStatusCode.CHECK_PASSWORD_FAIL == result.statusCode) {

					sendEmptyMessage(AccountMessageType.CHECK_PASSWORD_FAIL);
				}
			}

		}).checkPassword(password.trim());

		return requestId;
	}

	@Override
	public String updatePassword(String password) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(password)) {
			Log.e(TAG, "in method updatePassword,password is null");
			return requestId;
		}

		new AccountRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					sendEmptyMessage(AccountMessageType.UPDATE_PASSWORD_SUCCESS);

				} else {

					sendEmptyMessage(AccountMessageType.UPDATE_PASSWORD_FAIL);
				}
			}

		}).updatePassword(password.trim());

		return requestId;
	}

	public User getMyUserInfoFromDB() {

		return UserDbAdapter.getInstance().getUserByUserId(
				FusionConfig.getInstance().getUserId());
	}

	@Override
	public String getMyUserInfo() {

		String requestId = StringUtil.getRequestSerial();

		new AccountRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					User user = (User) BaseModel.fromJson(result.jsonObject,
							User.class);

					if (null != user) {

						user.avatar_url = HttpUtil
								.addUserAvatarUrlWH(user.avatar_url);

						UserDbAdapter.getInstance().insert(user);

						FusionConfig.getInstance().reloadMyUser();

						sendMessage(AccountMessageType.GET_MYUSER_INFO_SUCCESS,
								new UIObject(result.localRequestId, user));

						ChatManager.getInstance().callBackPushListener(
								IPushListener.GROUP_CHANGE, null);

					} else {

						sendEmptyMessage(AccountMessageType.GET_MYUSER_INFO_FAIL);
					}

				} else {

					sendEmptyMessage(AccountMessageType.GET_MYUSER_INFO_FAIL);
				}
			}

		}).getMyUserInfo();

		return requestId;
	}

	@Override
	public String updateMyCharacter(final String character) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(character)) {

			Log.e(TAG, "in method updateMyCharacter, character is null");

			return requestId;
		}

		new AccountRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					AccountDbAdapter.getInstance().updateAccountCharacter(
							FusionConfig.getInstance().getUserId(), character);

					FusionConfig.getInstance().reloadAccount();

					sendEmptyMessage(AccountMessageType.UPDATE_MY_CHARACTER_SUCCESS);

				} else {

					sendEmptyMessage(AccountMessageType.UPDATE_MY_CHARACTER_FAIL);
				}
			}

		}).updateMyCharacter(character);

		return requestId;
	}

	@Override
	public String updateMyUserInfo(final MeInfo meInfo) {

		String requestId = StringUtil.getRequestSerial();

		if (null == meInfo) {
			Log.e(TAG, "in method updateMyUserInfo, meInfo is null");
			return requestId;
		}

		new AccountRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					AccountDbAdapter.getInstance().updateAccountCharacter(
							FusionConfig.getInstance().getUserId(),
							meInfo.character);

					FusionConfig.getInstance().reloadAccount();

					sendEmptyMessage(AccountMessageType.UPDATE_MYUSER_INFO_SUCCESS);

				} else {

					sendEmptyMessage(AccountMessageType.UPDATE_MYUSER_INFO_FAIL);
				}
			}

		}).updateMyUserInfo(meInfo);

		return requestId;
	}

	@Override
	public String updatePrivacy(final Privacy privacy) {

		String requestId = StringUtil.getRequestSerial();

		if (null == privacy) {
			Log.e(TAG, "in method updatePrivacy, meInfo is null");
			return requestId;
		}

		new AccountRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					SettingDbAdapter.getInstance().insert(
							SettingKey.ONLY_FOLLOWING_CHAT_KEY,
							privacy.can_chat_if_followed);

					SettingDbAdapter.getInstance().insert(
							SettingKey.ONLY_FOLLOWING_GROUP_CHAT_KEY,
							privacy.can_invited_if_followed);

					SettingDbAdapter.getInstance().insert(
							SettingKey.FIND_ME_FROM_PHONE_NUMBER_KEY,
							privacy.can_found_by_phone);

					sendEmptyMessage(AccountMessageType.UPDATE_PRIVACY_INFO_SUCCESS);

				} else {

					sendEmptyMessage(AccountMessageType.UPDATE_PRIVACY_INFO_FAIL);
				}
			}

		}).updatePrivacy(privacy);

		return requestId;
	}

	@Override
	public String getMyAllTags() {

		String requestId = StringUtil.getRequestSerial();

		new AccountRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					sendMessage(
							AccountMessageType.GET_MY_ALL_TAGS_SUCCESS,
							new UIObject(result.localRequestId, BaseModel
									.fromJson(result.jsonObject, Tag.class)));

				} else {

					sendEmptyMessage(AccountMessageType.GET_MY_ALL_TAGS_FAIL);
				}
			}

		}).getMyAllTags();

		return requestId;
	}

	@Override
	public String getInterest() {

		String requestId = StringUtil.getRequestSerial();

		new AccountRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<String> interestList = new ArrayList<String>();

					List<BaseModel> baseModelList = BaseModel.fromJson(
							result.jsonArray, Interest.class);

					if (null != baseModelList && !baseModelList.isEmpty()) {

						for (BaseModel baseModel : baseModelList) {

							Interest interest = (Interest) baseModel;

							interestList.addAll(interest.tags);
						}
					}

					AccountDbAdapter.getInstance().updateInterest(
							FusionConfig.getInstance().getUserId(),
							interestList);

					FusionConfig.getInstance().reloadAccount();

					sendMessage(AccountMessageType.GET_INTEREST_SUCCESS,
							new UIObject(result.localRequestId, interestList));

				} else {

					sendEmptyMessage(AccountMessageType.GET_INTEREST_FAIL);
				}
			}

		}).getInterest();

		return requestId;
	}
}
