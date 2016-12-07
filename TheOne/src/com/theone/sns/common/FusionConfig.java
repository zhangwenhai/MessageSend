/*
 * 文件名: FusionConfig.java
 * 创建人: zhouyujun
 */
package com.theone.sns.common;

import java.io.File;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import com.theone.sns.R;
import com.theone.sns.TheOneApp;
import com.theone.sns.common.FusionCode.SettingKey;
import com.theone.sns.common.FusionCode.SharedprefsKey;
import com.theone.sns.component.http.IHttpListener;
import com.theone.sns.component.http.Result;
import com.theone.sns.component.upload.auth.Authorizer;
import com.theone.sns.logic.adapter.db.AccountDbAdapter;
import com.theone.sns.logic.adapter.db.FilterDbAdapter;
import com.theone.sns.logic.adapter.db.SettingDbAdapter;
import com.theone.sns.logic.adapter.db.UserDbAdapter;
import com.theone.sns.logic.adapter.http.AccountRequester;
import com.theone.sns.logic.model.account.Account;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.user.IUserLogic;
import com.theone.sns.ui.base.BasicActivity;
import com.theone.sns.util.HttpUtil;
import com.theone.sns.util.StringUtil;

/**
 * 全局配置类，单例模式<BR>
 * 
 * @author zhouyujun
 */
public class FusionConfig {

	private static final String TAG = "FusionConfig";

	public static final String DEFAULT_AVATAR_URL = "http://www.gravatar.com/avatar";

	/**
	 * the one scheme
	 */
	public static final String THEONE_SCHEME = "http";

	/**
	 * the one host for real
	 */
	public static final String THEONE_HOST = "api.itheone.cn/";

	/**
	 * 获取网络请求访问的TOKEN(登陆)
	 */
	public static final String GET_USER_TOKEN_URL = "oauth2/token";

	/**
	 * 退出登录URL
	 */
	public static final String LOGOUT_URL = "oauth2/token/revoke";

	/**
	 * 获取验证码
	 */
	public static final String GET_VERIFIY_CODE = "code";

	/**
	 * 校验验证码并创建账号(post),获取用户信息(get)，更新用户信息(put)
	 */
	public static final String CHECK_VERIFIY_CODE_AND_OPERATION_ACCOUNT_URL = "account";

	/**
	 * 手机注册,验证用户输入的验证码
	 */
	public static final String CHECK_VERIFIY_CODE_URL = "account/phone/verifiy";

	/**
	 * 验证用户密码(重置密码)
	 */
	public static final String CHECK_PASSWORD_URL = "account/password/verify";

	/**
	 * 更新用户密码(重置密码)
	 */
	public static final String UPDATE_PASSWORD_URL = "account/password";

	/**
	 * 忘记密码(通过邮件重置密码)
	 */
	public static final String EMAIL_RESET_PASSWORD_URL = "account/password/reset";

	/**
	 * 动态筛选参数
	 */
	public static final String MBLOG_FILTER_URL = "account/filter";

	/**
	 * 用户Tag
	 */
	public static final String GET_MY_ALL_TAGS_URL = "account/tags";

	/**
	 * 获取兴趣标签
	 */
	public static final String GET_INTEREST_URL = "account/public-tags";

	/**
	 * 搜索品牌TAG
	 */
	public static final String SEARCH_TAGS_URL = "tags";

	/**
	 * 发布微博
	 */
	public static final String MBLOG_OPERATION_URL = "posts";

	/**
	 * 获取关注好友的微博列表
	 */
	public static final String MBLOG_LIST_FOLLOWING_URL = "posts/following";

	/**
	 * 点击微博照片上的tag,获取相同tag的相册列表
	 */
	public static final String MBLOG_TAG_TO_GALLERY_URL = "posts/gallery";

	/**
	 * 微博条目相关操作
	 */
	public static final String MBLOG_ID_OPERATION_URL = "posts/%1$s";

	/**
	 * 微博点赞相关操作
	 */
	public static final String MBLOG_LIKES_OPERATION_URL = "posts/%1$s/likes";

	/**
	 * 微博评论相关操作
	 */
	public static final String MBLOG_COMMENTS_OPERATION_URL = "posts/%1$s/comments";

	/**
	 * 删除评论
	 */
	public static final String MBLOG_DELETE_COMMENTS_URL = "posts/%1$s/comments/%2$s";

	/**
	 * 系统推荐好友
	 */
	public static final String USER_RECOMMEND_URL = "users/recommendations";

	/**
	 * 搜索用户
	 */
	public static final String USER_SEARCH_URL = "users/search";

	/**
	 * 上传用户通讯录，获取用户是否注册应用等信息
	 */
	public static final String USER_SEARCH_CONTACT_URL = "users/search/contacts";

	/**
	 * 根据用户ID获取用户信息
	 */
	public static final String USER_GET_BY_ID_URL = "users/%1$s";

	/**
	 * 根据用户ID获取用户相册列表
	 */
	public static final String USER_GET_GALLERY_BY_ID_URL = "users/%1$s/gallery";

	/**
	 * 根据用户ID获取用户微博列表
	 */
	public static final String USER_GET_MBLOGLIST_BY_ID_URL = "users/%1$s/posts";

	/**
	 * 根据用户ID获取圈我的相册
	 */
	public static final String USER_GET_MENTIONED_BY_ID_URL = "users/%1$s/mentioned";

	/**
	 * 根据用户ID获取按标签分类的缩略图
	 */
	public static final String USER_GET_TAGTHUMBNAILS_BY_ID_URL = "users/%1$s/tags";

	/**
	 * 根据用户ID操作用户备注名
	 */
	public static final String USER_NOTE_OPERATION_BY_ID_URL = "users/%1$s/note";

	/**
	 * 列出所有用户关系，包含关注、粉丝、密友、屏蔽,已经关系设置
	 */
	public static final String USER_RELATIONSHIPS_URL = "users/%1$s/relationships";

	/**
	 * 列出当前用户所有Group/创建新的聊天组URL
	 */
	public static final String GROUP_URL = "groups";

	/**
	 * 根据组ID更新组信息
	 */
	public static final String UPDATE_GROUP_INFO_URL = "groups/%1$s";

	/**
	 * 更新组成员列表
	 */
	public static final String UPDATE_GROUP_MEMBER_URL = "groups/%1$s/members";

	/**
	 * 组的消息处理(拉取未读消息（离线消息）/创建新消息(发消息给好友))
	 */
	public static final String GROUP_MESSAGE_URL = "groups/messages";

	/**
	 * 获取其他人对自己的动态
	 */
	public static final String GET_NOTIFY_ACTION_ME_URL = "actions/me";

	/**
	 * 获取注册邀请码
	 */
	public static final String GET_REGISTER_INVITE_CODE_URL = "register-code";

	/**
	 * 验证注册邀请码
	 */
	public static final String VERIFY_REGISTER_INVITE_CODE_URL = "register-code/verify";

	/**
	 * 发现TAB中附近的微博相册
	 */
	public static final String FIND_MBLOG_GALLERY_NEAR_URL = "recommendations/posts/near";

	/**
	 * 发现TAB中最热的微博相册
	 */
	public static final String FIND_MBLOG_GALLERY_HOT_URL = "recommendations/posts/popular";

	/**
	 * 发现TAB中最新的微博相册
	 */
	public static final String FIND_MBLOG_GALLERY_NEW_URL = "recommendations/posts/new";

	/**
	 * 发现TAB中附近的用户相册
	 */
	public static final String FIND_USER_GALLERY_NEAR_URL = "recommendations/users/near";

	/**
	 * 发现TAB中最热的用户相册
	 */
	public static final String FIND_USER_GALLERY_HOT_URL = "recommendations/users/popular";

	/**
	 * 发现TAB中最新的用户相册
	 */
	public static final String FIND_USER_GALLERY_NEW_URL = "recommendations/users/new";

	/**
	 * 发现TAB中搜索标签相册URL
	 */
	public static final String FIND_SEARCH_GALLERY_URL = "recommendations/posts/search";

	/**
	 * 发现TAB中搜索用户URL
	 */
	public static final String FIND_SEARCH_USER_URL = "recommendations/users/search";

	public static final String HTTP_REQUEST_HEADER_TOKEN_KEY = "Authorization";

	public static final String HTTP_REQUEST_HEADER_LOCATION_KEY = "Geo-Position";

	public static final String HTTP_REQUEST_HEADER_TOKEN_VALUE_PREFIX = "bearer ";

	public static final String LOGIN_CLIENT_ID_VALUE = "f862f658-ad89-4fcb-995b-7a4c50554ff6";

	public static final String LOGIN_CLIENT_SECRET_VALUE = "3274MF2R1MC1M3F9G01GJ2G00G77GLCB";

	public static final String LOGIN_GRANT_TYPE_VALUE = "password";

	public static final String ENCODE_CHARSET_NAME = "ISO-8859-1";

	public static final String COUNTRY_CODE = "+86";

	public static final String UPLOAD_BUCKET_NAME = "theonex";

	public static final String MEDIA_URL_PREFIX = "http://"
			+ UPLOAD_BUCKET_NAME + ".qiniudn.com/";

	public static final String HOCKEYAPP_APP_ID = "4fef9d4f16fc84ece360c49b7cfae92a";

	/**
	 * 多个属性存在数据库一个字段时，需要用分隔符隔开
	 */
	public static final String DB_COLUMN_SEPARATE = ";";

	public static final String LOCATION_SEPARATE = ",";

	/**
	 * 当前activity
	 * */
	public static BasicActivity currentActivity;

	/**
	 * 用户账户信息
	 */
	private Account mAccount;

	/**
	 * 获取自己的信息
	 */
	private User mMyUser;

	public static boolean isInitLocation = false;

	/**
	 * 图像缓存手机路径的目录，主要用于缓存
	 */
	public static final String SAVE_PHOTO_FLODER = Environment
			.getDataDirectory()
			+ "/data/"
			+ TheOneApp.getContext().getPackageName()
			+ File.separator
			+ "imagecache/";

	/**
	 * 图像缓存SD卡路径的目录，主要用于缓存
	 */
	public static final String SAVE_SD_FLODER = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/theone/imagecache/";

	/**
	 * 图片保存到手机内存的目录，主要用于保存图片
	 */
	public static final String SAVE_IMAGE_INTERNAL_PATH = Environment
			.getDataDirectory()
			+ "/data/"
			+ TheOneApp.getContext().getPackageName()
			+ File.separator
			+ "saveimages/";

	/**
	 * 图像保存到手机外存的目录，主要用于保存图片
	 */
	public static final String SAVE_IMAGE_EXTERNAL_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/theone/saveimages/";

	/**
	 * 图像保存到手机外存的目录，主要用于保存图片
	 */
	public static final String SAVE_VIDEO_EXTERNAL_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/theone/videocache/";

	/**
	 * 图片保存到手机内存的目录，主要用于保存图片
	 */
	public static final String SAVE_VIDEO_INTERNAL_PATH = Environment
			.getDataDirectory()
			+ "/data/"
			+ TheOneApp.getContext().getPackageName()
			+ File.separator
			+ "videocache/";

	/**
	 * 图像文件的前缀
	 */
	public static final String IMAGE_PREFIX = "image";

	/**
	 * 图像文件的后缀
	 */
	public static final String IMAGE_SUFFIX = ".jpg";

	/**
	 * 客户端版本号
	 */
	private String clientVersion;

	/**
	 * 单例对象
	 */
	private static FusionConfig mFusionConfig;

	/**
	 * 私有化构造器
	 */
	private FusionConfig() {
	}

	/**
	 * 获取单例的 FusionConfig对象
	 *
	 * @return FusionConfig对象
	 */
	public static FusionConfig getInstance() {

		if (null == mFusionConfig) {

			mFusionConfig = new FusionConfig();
		}

		return mFusionConfig;
	}

	/**
	 * set clientVersion
	 *
	 * @param clientVersion
	 *            the clientVersion to set
	 */
	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public Authorizer getUploadAuthorizer() {

		Authorizer auth = new Authorizer();

		auth.setUploadToken(FusionConfig.getInstance().getAccount().config.qiniu_upload_token);

		return auth;
	}

	public void saveAccountFromServer(Account account) {

		if (null == account) {

			Log.e(TAG, "create acccount error");

			return;
		}

		TheOneApp.getSharedPref().put(SharedprefsKey.USER_ID_KEY,
				account.profile._id);

		if (!TextUtils.isEmpty(account.profile.phone_no)) {

			account.loginAccount = account.profile.phone_no;

		} else if (!TextUtils.isEmpty(account.profile.email)) {

			account.loginAccount = account.profile.email;
		}

		account.profile.avatar_url = HttpUtil
				.addUserAvatarUrlWH(account.profile.avatar_url);

		account.isLogin = true;

		mAccount = account;

		AccountDbAdapter.getInstance().deleteAllAccount();

		AccountDbAdapter.getInstance().insertAccount(account);

		FilterDbAdapter.getInstance().deleteFilter();

		FilterDbAdapter.getInstance().insertFilter(account.filter);

		if (null != account.privacy) {

			SettingDbAdapter.getInstance().insert(
					SettingKey.ONLY_FOLLOWING_CHAT_KEY,
					account.privacy.can_chat_if_followed);

			SettingDbAdapter.getInstance().insert(
					SettingKey.ONLY_FOLLOWING_GROUP_CHAT_KEY,
					account.privacy.can_invited_if_followed);

			SettingDbAdapter.getInstance().insert(
					SettingKey.FIND_ME_FROM_PHONE_NUMBER_KEY,
					account.privacy.can_found_by_phone);
		}
	}

	public Account getAccount() {

		if (null == mAccount || TextUtils.isEmpty(mAccount.token.access_token)) {

			Log.e(TAG, "in method getAccount, mAccount is null");

			mAccount = AccountDbAdapter.getInstance().getCurrentAccount(
					getUserId());
		}

		return mAccount;
	}

	public void reloadAccount() {
		mAccount = AccountDbAdapter.getInstance()
				.getCurrentAccount(getUserId());
	}

	public User getMyUser() {

		if (null == mMyUser) {

			mMyUser = UserDbAdapter.getInstance().getUserByUserId(getUserId());
		}

		return mMyUser;
	}

	public void reloadMyUser() {
		mMyUser = UserDbAdapter.getInstance().getUserByUserId(getUserId());
	}

	public String getUserId() {

		return TheOneApp.getSharedPref().getString(SharedprefsKey.USER_ID_KEY,
				"");
	}

	public static boolean isLogin() {

		boolean isLogin = false;

		String userId = FusionConfig.getInstance().getUserId();

		if (TextUtils.isEmpty(userId)) {

			return isLogin;
		}

		Account account = FusionConfig.getInstance().getAccount();

		if (null == account) {

			return isLogin;
		}

		if (account.isLogin
				&& null != account.profile
				&& !TextUtils.isEmpty(account.profile.character)
				&& !TextUtils.isEmpty(account.profile.avatar_url)
				&& !FusionConfig.DEFAULT_AVATAR_URL
						.equals(account.profile.avatar_url)) {

			isLogin = true;
		}

		return isLogin;
	}

	public void clear() {
		mAccount = null;
	}

	/**
	 * get clientVersion
	 * 
	 * @return the clientVersion
	 */
	public String getClientVersion() {
		return clientVersion;
	}

	public static void logoutAccount(String userId, boolean isUnauthorized) {

		String requestId = StringUtil.getRequestSerial();

		AccountDbAdapter.getInstance().loginOutAccount(userId);

		if (!isUnauthorized) {

			new AccountRequester(requestId, new IHttpListener() {

				public void onResult(Result result) {
				}

			}).logout();
		}
	}

	public static void showFollowButton(User user, Button followButton) {

		if (null == user || null == followButton) {
			return;
		}

		if (user.is_following && user.is_followed_by) {

			followButton.setText(TheOneApp.getContext().getString(
					R.string.all_follow));

			followButton.setTextColor(TheOneApp.getContext().getResources()
					.getColor(R.color.black));

			followButton.setBackgroundDrawable(TheOneApp.getContext()
					.getResources()
					.getDrawable(R.drawable.home_attention_highlight_btn));

		} else if (user.is_following) {

			followButton.setText(TheOneApp.getContext().getString(
					R.string.unfollow));

			followButton.setTextColor(TheOneApp.getContext().getResources()
					.getColor(R.color.black));

			followButton.setBackgroundDrawable(TheOneApp.getContext()
					.getResources()
					.getDrawable(R.drawable.home_attention_highlight_btn));

		} else {

			followButton.setText(TheOneApp.getContext().getString(
					R.string.follow));

			followButton.setTextColor(TheOneApp.getContext().getResources()
					.getColor(R.color.white));

			followButton.setBackgroundDrawable(TheOneApp.getContext()
					.getResources().getDrawable(R.drawable.home_attention_btn));
		}
	}

	public static void clickFollowButton(IUserLogic mIUserLogic, User user,
			Button followButton) {

		if (null == user || null == followButton || null == mIUserLogic) {
			return;
		}

		if (TheOneApp.getContext().getString(R.string.follow)
				.equals((String) followButton.getText().toString())) {

			if (user.is_followed_by) {

				followButton.setText(TheOneApp.getContext().getString(
						R.string.all_follow));

			} else {
				followButton.setText(TheOneApp.getContext().getString(
						R.string.unfollow));
			}

			followButton.setTextColor(TheOneApp.getContext().getResources()
					.getColor(R.color.black));

			followButton.setBackgroundDrawable(TheOneApp.getContext()
					.getResources()
					.getDrawable(R.drawable.home_attention_highlight_btn));

			isFollowUser(mIUserLogic, user, true);

		} else {

			followButton.setText(TheOneApp.getContext().getString(
					R.string.follow));

			followButton.setTextColor(TheOneApp.getContext().getResources()
					.getColor(R.color.white));

			followButton.setBackgroundDrawable(TheOneApp.getContext()
					.getResources().getDrawable(R.drawable.home_attention_btn));

			isFollowUser(mIUserLogic, user, false);
		}
	}

	private static void isFollowUser(IUserLogic mIUserLogic, User user,
			boolean isFollow) {

		if (null == user || null == mIUserLogic) {
			return;
		}

		String action = null;

		if (isFollow) {

			user.is_following = true;

			action = FusionCode.RelationshipAction.FOLLOW_ACTION;

		} else {

			user.is_following = false;

			action = FusionCode.RelationshipAction.UNFOLLOW_ACTION;
		}

		mIUserLogic.setUserRelationship(user.userId, action);
	}
}
