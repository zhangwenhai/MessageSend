/*
 * 文件名: FusionCode.java
 * 创建人: zhouyujun
 */
package com.theone.sns.common;

public class FusionCode {

    public interface CommonColumnsValue {

        int COUNT_VALUE = 18;

        /**
         * 表中存储数据，使用数字代替布尔值，值为1时，表示true
         */
        int TRUE_VALUE = 1;

        /**
         * 表中存储数据，使用数字代替布尔值，值为0时，表示false
         */
        int FALSE_VALUE = 0;

        /**
         * 获取未读消息条数
         */
        int MESSAGE_COUNT_VALUE = 100;
    }

    public interface AccountStatusCode {

        int CHECK_PASSWORD_FAIL = 449;
    }

    public interface MBlogStatusCode {

        int GALLERY_NOT_FOUND = 404;

        int GET_MBLOG_BY_ID_FAIL = 404;

        int DELETE_MBLOG_BY_ID_FAIL = 404;
    }

    public interface MBlogListFollowType {

        String FOLLOWING = "mblog_following";
    }

    public interface FindUserGalleryType {

        String NEW_USER = "gallery_user_new";

        String HOT_USER = "gallery_user_hot";

        String NEAR_USER = "gallery_user_near";
    }

    public interface FindMBlogGalleryType {

        String NEW_MBLOG = "gallery_mblog_new";

        String HOT_MBLOG = "gallery_mblog_hot";

        String NEAR_MBLOG = "gallery_mblog_near";
    }

    public interface PublishMBlogVisibility {

        String ALL = "all";

        String STARRING = "starring";
    }

    public interface LikesAction {

        String LIKE = "like";

        String UNLIKE = "unlike";
    }

    public interface UserCharacter {

        String GAY = "G";

        String LES = "L";
    }

    public interface Role {

        String T = "T";

        String P = "P";

        String H = "H";

        String MT = "1";

        String MP = "0.5";

        String MH = "0";
    }

    public interface Relationship {

        String FOLLOWING = "following";

        String FOLLOWED_BY = "followed_by";

        String STARRING = "starring";

        String BLOCKING = "blocking";

        String LIKED_BY = "liked_by";
    }

    public interface RelationshipAction {

        String FOLLOW_ACTION = "follow";

        String UNFOLLOW_ACTION = "unfollow";

        String STAR_ACTION = "star";

        String UNSTAR_ACTION = "unstar";

        String BLOCK_ACTION = "block";

        String UNBLOCK_ACTION = "unblock";

        String HIDE_ACTION = "hide";

        String UNHIDE_ACTION = "unhide";
    }

    public interface SharedprefsKey {

        String USER_ID_KEY = "userId";
    }

    public interface SettingKey {

        String ONLY_FOLLOWING_CHAT_KEY = "setting_only_following_chat";

        String ONLY_FOLLOWING_GROUP_CHAT_KEY = "setting_only_following_group_chat";

        String FIND_ME_FROM_PHONE_NUMBER_KEY = "setting_find_me_from_phone_number";

        String NEW_MESSAGE_NOTIFY_KEY = "setting_new_message_notify";

        String SOUND_NOTIFY_KEY = "setting_sound_notify";

        String VIBRATE_NOTIFY_KEY = "setting_vibrate_notify";

        String NOTIFY_DETAIL_KEY = "setting_notify_detail";

        String NEW_MBLOG_NOTIFY_KEY = "setting_new_mblog_notify";

        String NEW_APK_CHECK_KEY = "setting_new_apk_check";

        /**
         * 组邀请未读数
         */
        String GROUP_INVITE_COUNT = "group_invite_count";

        /**
         * 最新的组邀请的组ID
         */
        String NEW_GROUP_INVITE_GROUPID = "new_group_invite_groupid";

        /**
         * 消息TAB中通知未读数
         */
        String NOTIFICATION_COUNT = "notifiaction_count";

        /**
         * 消息TAB中的通知内容
         */
        String NEW_NOTIFICATION_CONTENT = "new_notification_content";

        /**
         * 有人赞了你
         */
        String IS_LIKED_BY = "is_liked_by";

        /**
         * 有人关注了你
         */
        String IS_FOLLOWED_BY = "is_followed_by";

        /**
         * 好友发布了新微博
         */
        String IS_NEW_POST = "is_new_post";

        /**
         * 最新聊天消息ID
         */
        String NEW_MESSAGE_ID = "setting_new_message_id";
    }

    public interface MatchContactType {

        int USER = 1;

        int CONTACT = 2;

        int SEPARATORS = 3;
    }

    public interface PushType {

        String TYPE = "path";

        String BODY = "body";

        String TYPE_MESSAGE = "/messages";

        String TYPE_GROUPS = "/groups";

        String TYPE_NOTIFICATIONS = "/notifications";

        String ACTION_LIKED_BY = "liked_by";

        String ACTION_COMMENT = "commented_by";

        String ACTION_FOLLOWED_BY = "followed_by";

        String ACTION_MENTIONED_BY = "mentioned_by";

        String ACTION_NEW_POST = "new_post";
    }

    public interface GroupMemberAction {

        String INVITE = "invite";

        String JOIN = "join";

        String REMOVE = "remove";

        String QUIT = "quit";
    }

    public interface MessageType {

        int TEXT = 1;

        int AUDIO = 2;

        int PHOTO = 3;

        int NAME_CARD = 4;

        int POSITION = 5;
    }

    public interface MessageStatusType {

        int SEND_PROCESS = 1;

        int SEND_SUCCESS = 2;

        int SEND_FAIL = 3;
    }

    public interface NotifyMeType {

        int FOLLOW = 1;

        int LIKE = 2;

        int COMMENT = 3;

        int MENTION = 4;
    }
}
