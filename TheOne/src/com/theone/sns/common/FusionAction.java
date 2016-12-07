/*
 * 文件名: FusionAction.java
 * 描    述: 所有UI跳转界面的action定义
 * 创建人: zhouyujun
 */
package com.theone.sns.common;

/**
 * 所有UI跳转界面的action定义
 *
 * @author zhouyujun
 */
public interface FusionAction {
	/**
	 * TheOneApp应用程序
	 */
	public interface TheOneApp {

		/**
		 * 定义关闭ACTION，用于广播过滤
		 */
		String ACTION_CLOSE_APPLICATION = "com.huawei.basic.android.zone.CLOSEAPPLICATION";

		/**
         *
         */
		String HEAD_MODEL = "head_model";

		/**
         *
         */
		String HEADMODEL = "headmodel";

		/**
         *
         */
		String MBLOGMODEL = "Mblogmodel";

		/**
         *
         */
		String UPDATE = "update";

		/**
		 * 网络请求时，鉴权失败
		 */
		String UNAUTHORIZED = "unauthorized";
	}

	/**
	 * 注册
	 */
	public interface RegisterAction {

		/**
		 * 注册邀请码页面
		 */
		String REGISTER_INVITE_CODE_ACTION = "com.theone.sns.ui.register.REGISTERINVITECODE";

		/**
		 * 跳转PhoneSignup界面
		 */
		String PHONESIGNUP_ACTION = "com.theone.sns.ui.login.PHONESIGNUP";

		/**
		 * 跳转RegisterManifestoActivity界面
		 */
		String REGISTERMANIFESTO_ACTION = "com.theone.sns.ui.login.REGISTERMANIFESTO";

		/**
		 * 跳转RegisterUserInfoActivity界面
		 */
		String REGISTERUSERINFO_ACTION = "com.theone.sns.ui.login.REGISTERUSERINFO";

		/**
		 * 跳转EmailSignupActivity界面
		 */
		String EMAILSIGNUP_ACTION = "com.theone.sns.ui.login.EMAILSIGNUP";

		/**
		 * 跳转ReadPrivacyAcitivity界面
		 */
		String READ_PRIVACY_ACTION = "com.theone.sns.ui.login.READPRIVACY";

		/**
		 * 跳转MainActivity界面
		 */
		String MAIN_ACTION = "com.theone.sns.ui.main.Main";

		/**
		 * 跳转ForgetAcitivity界面
		 */
		String FORGET_ACTION = "com.theone.sns.ui.login.FORGET";

		/**
		 * 跳转VerificationAcitivity界面
		 */
		String VERIFICATION_ACTION = "com.theone.sns.ui.login.VERIFICATION";

		/**
		 * 跳转VerificationPasswordAcitivity界面
		 */
		String VERIFICATIONPASSWORDACITIVITY = "com.theone.sns.ui.login.VERIFICATIONPASSWORDACITIVITY";

		/**
		 * 跳转SetPawAcitivity界面
		 */
		String SETPAW_ACTION = "com.theone.sns.ui.login.SETPAW";

		/**
		 * 跳转MyInvitationCodeActivity界面
		 */
		String MYINVITATIONCODE_ACTION = "com.theone.sns.ui.me.activity.MYINVITATIONCODE";

		/**
		 * 跳转InvitationCodeDetailsActivity界面
		 */
		String INVITATIONCODEDETAILS_ACTION = "com.theone.sns.ui.me.activity.INVITATIONCODEDETAILS";

		/**
		 * 用户属性参数
		 */
		String EXTRA_USER_CHARACTER = "character";

		/**
		 * 忘记密码，输入的手机号码
		 */
		String EXTRA_PHONE_NUMBER = "phonenumber";

		/**
		 * 邀请码页面的昵称
		 */
		String EXTRA_REGISTER_NICKNAME = "nickname";

		/**
		 * 邀请码页面的密码
		 */
		String EXTRA_REGISTER_PASSWORD = "password";

		/**
		 * 邀请码页面的邀请码
		 */
		String EXTRA_REGISTER_REGISTER_CODE = "register_code";

		/**
		 * 登陆入口进入忘记密码流程
		 */
		String EXTRA_LOGIN_FORGET_PASSWORD = "is_login_forget_password";
	}

	/**
	 * 登陆
	 */
	public interface LoginAction {

		/**
		 * 跳转LoginActivity界面
		 */
		String LOGIN_ACTION = "com.theone.sns.ui.login.LOGIN";

		/**
		 * 跳转LeadActivity界面
		 */
		String LEAD_ACTION = "com.theone.sns.ui.login.Lead";
	}

	/**
	 * 聊天
	 */
	public interface ChatAction {

		/**
		 * 跳转ChatInfoActivity界面
		 */
		String CHATINFO_ACTION = "com.theone.sns.ui.chat.CHATINFO";

		/**
		 * 跳转ChatActivity界面
		 */
		String CHAT_ACTION = "com.theone.sns.ui.chat.CHAT";

		/**
		 * 跳转GroupNameActivity界面
		 */
		String GROUPNAME_ACTION = "com.theone.sns.ui.chat.GROUPNAME";

		/**
		 * 跳转SelectFriendActivity界面
		 */
		String SELECT_FRIEND_ACTION = "com.theone.sns.ui.chat.SELECTFRIEND";

		/**
		 * 跳转GroupInviteListActivity界面
		 */
		String GROUPINVITELIST_ACTION = "com.theone.sns.ui.chat.activity.GROUPINVITELIST";

		/**
		 * 跳转NotificationListActivity界面
		 */
		String NOTIFICATIONLIST_ACTION = "com.theone.sns.ui.chat.activity.NOTIFICATIONLIST";

		/**
		 * 跳转SelectLocationActivity界面
		 */
		String SELECT_LOCATION_ACTION = "com.theone.sns.ui.chat.activity.SELECTLOCATION";

		/**
		 * 跳转ShowLocationActivity界面
		 */
		String SHOW_LOCATION_ACTION = "com.theone.sns.ui.chat.activity.SHOWLOCATION";

		/**
		 * 跳转ChatPicActivity界面
		 */
		String CHATPIC_ACTION = "com.theone.sns.ui.chat.activity.CHATPIC";

		/**
		 * 跳转ForwardActivity界面
		 */
		String FORWARD_ACTION = "com.theone.sns.ui.chat.activity.FORWARD";

		/**
		 * 跳转AtListActivity界面
		 */
		String ATLIST_ACTION = "com.theone.sns.ui.chat.activity.ATLIST";

		/**
		 * 跳转群聊人员展示界面
		 */
		String CHATUSERLIST_ACTION = "com.theone.sns.ui.chat.activity.CHATUSERLIST";

		String SELECT_TYPE = "select_type";

		String GROUP_INFO = "group_info";

		String GROUP_NAME = "group_name";

		String SELECT_TYPE_1 = "select_type_1";

		String UPDATE_VIEW = "update_view";

		String LONGITUDE = "longitude";

		String LATITUDE = "latitude";

		String LOCATION_INFO = "location_info";

		String CHATPIC_URL = "chatpic_url";

		String MESSGAE_ID = "messgae_id";

		String MESSGAE_INFO = "messgae_info";

		String CREATE_GROUP_ID = "createGroupId";

		String AT_SOMEONE = "@";

		String USER_INFO = "user_info";
	}

	/**
	 * 发现
	 */
	public interface DiscoverAction {

		/**
		 * 跳转DiscoverActivity界面
		 */
		String DISCOVER_SEARCH_ACTION = "com.theone.sns.ui.discover.DISCOVER";

		/**
		 * 跳转ConditionsSettingActivity界面
		 */
		String CONDITION_ACTION = "com.theone.sns.ui.discover.CONDITION";

		String SEARCH_WORD = "search_word";

		String SEARCH_TYPE = "search_type";
	}

	/**
	 * 发布
	 */
	public interface PublicAction {

		/**
		 * 跳转PublicActivity界面
		 */
		String PUBLIC_ACTION = "com.theone.sns.ui.publish.PUBLIC";

		String PUBLIC_TYPE = "public_type";

		int TYPE_VIDEO = 1;

		int TYPE_PHOTO = 2;

		int TYPE_ALBUM = 3;

		/**
		 * 跳转AddLabelActivity界面
		 */
		String ADDLABEL_ACTION = "com.theone.sns.ui.publish.ADDLABEL";

		String FILE_PATH = "file_path";

		String AUDIO_PATH = "audio_path";

		String AUDIO_TIME = "audio_time";

		String VIDEO_PATH = "video_path";

		String VIDEO_TIME = "video_time";

		String FILE_ANGLE = "file_angle";

		String CAMERA_TYPE = "camera_type";

		String LABEL_NAME = "label_name";

		String LABEL_USER_NAME = "label_user_name";

		String PHOTO_FORM_AU = "photo_form_au";

		String SHARE_PLATFORM = "share_platform";

		String LABEL_USER_ID = "label_user_id";

		/**
		 * 跳转LabelListActivity界面
		 */
		String LABELLIST_ACTION = "com.theone.sns.ui.publish.LABELLIST";

		/**
		 * 跳转CircleLabelListActivity界面
		 */
		String CIRCLE_LABELLIST_ACTION = "com.theone.sns.ui.publish.CIRCLELABELLIST";

		String TYPE = "type";

		String PUBLICMBLOG = "publicMBlog";

		/**
		 * 跳转拍照界面
		 */
		String PHOTO_ACTION = "com.theone.sns.ui.publish.PHOTO";

		/**
		 * 跳转录像界面
		 */
		String VIDEO_ACTION = "com.theone.sns.ui.publish.VIDEO";

		/**
		 * 跳转发送界面
		 */
		String SEND_ACTION = "com.theone.sns.ui.publish.SEND";

	}

	/**
	 * 发布
	 */
	public interface MBlogAction {

		/**
		 * 跳转AddFriendActivity界面
		 */
		String ADDFRIEND_ACTION = "com.theone.sns.ui.mblog.ADDFRIEND";

		/**
		 * 跳转ContactFriendActivity界面
		 */
		String CONTACT_FRIEND_ACTION = "com.theone.sns.ui.mblog.CONTACTFRIEND";

		/**
		 * 跳转MBlogCommentActivity界面
		 */
		String MBLOGCOMMENT_ACTION = "com.theone.sns.ui.mblog.activity.MBLOGCOMMENT";

		/**
		 * 跳转MBlogLikeByListActivity界面
		 */
		String MBLOG_LIKEBYLIST_ACTION = "com.theone.sns.ui.me.activity.MBLOGLIKEBYLIST";

		String MBLOG = "mblog";

		String MBLOG_ID = "mblog_id";

		String MBLOG_OWNER = "mblog_owner";

		/**
		 * 跳转TagSetActivity界面
		 */
		String TAGSET_ACTION = "com.theone.sns.ui.mblog.activity.TAGSET";

		String MBLOG_TAG = "mblog_tag";

		/**
		 * 跳转VideoShowActivity界面
		 */
		String VIDEOSHOW_ACTION = "com.theone.sns.ui.mblog.activity.VIDEOSHOW";

		String VIDEO_PATH = "video_path";

		String VIDEO_TIME = "video_time";
	}

	/**
	 * 我
	 */
	public interface MeAction {

		/**
		 * 跳转TaActivity界面
		 */
		String TA_ACTION = "com.theone.sns.ui.me.TA";

		String UID = "uid";

		String TAG = "tag";

		String USER = "user";

		/**
		 * 跳转MeSettingActivity界面
		 */
		String MESETTING_ACTION = "com.theone.sns.ui.me.activity.MESETTING";

		/**
		 * 跳转LikeByListActivity界面
		 */
		String LIKEBYLIST_ACTION = "com.theone.sns.ui.me.activity.LIKEBYLIST";

		/**
		 * 跳转FollowListActivity界面
		 */
		String FOLLOWLIST_ACTION = "com.theone.sns.ui.me.activity.FOLLOWLIST";

		/**
		 * 跳转StarringListActivity界面
		 */
		String STARRINGLIST_ACTION = "com.theone.sns.ui.me.activity.STARRINGLIST";

		/**
		 * 跳转FollowActivity界面
		 */
		String FOLLOW_ACTION = "com.theone.sns.ui.me.activity.FOLLOW";

		/**
		 * 跳转FollowedByListActivity界面
		 */
		String FOLLOWEDBYLIST_ACTION = "com.theone.sns.ui.me.activity.FOLLOWEDBYLIST";

		/**
		 * 跳转SetAliasAcitivity界面
		 */
		String ALIAS_ACTION = "com.theone.sns.ui.me.activity.SETALIAS";

		/**
		 * 跳转SetNickNameAcitivity界面
		 */
		String NICKNAME_ACTION = "com.theone.sns.ui.me.activity.SETNICKNAME";

		/**
		 * 跳转MBlogListByTagActivity界面
		 */
		String MBLOGLISTBYTAG_ACTION = "com.theone.sns.ui.me.activity.MBLOGLISTBYTAG";

		/**
		 * 跳转SettingAcitivity界面
		 */
		String SETTING_ACTION = "com.theone.sns.ui.me.activity.SETTING";

		/**
		 * 跳转AboutActivity界面
		 */
		String ABOUT_ACTION = "com.theone.sns.ui.me.activity.ABOUT";

		/**
		 * 跳转AvatarShowActivity界面
		 */
		String AVATARSHOW_ACTION = "com.theone.sns.ui.me.activity.AVATARSHOW";
	}

	/**
	 * 所有模块之间传递的公用字段
	 */
	public interface CommonField {

		String USER_ID_KEY = "user_id";

		String USER_NAME_KEY = "user_name";

		String ALIAS_KEY = "alias";

		String USER_KEY = "user";
	}
}
