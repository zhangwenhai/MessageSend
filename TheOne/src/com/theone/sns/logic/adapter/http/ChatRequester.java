package com.theone.sns.logic.adapter.http;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;
import com.theone.sns.common.FusionCode.CommonColumnsValue;
import com.theone.sns.common.FusionCode.MessageType;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.component.http.BaseRequest;
import com.theone.sns.component.http.IHttpListener;
import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.chat.CreateGroup;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.util.HttpUtil;

public class ChatRequester extends BaseRequest {

	private static final String TAG = "ChatRequester";

	public interface UpdateGroupParameter {

		String NAME = "name";

		String IS_MUTED = "is_muted";

		String MEMBERS = "members";

		String ACTION = "action";

		String USER = "user";
	}

	public interface MessageParameter {

		String RECIPIENT = "recipient";

		String TEXT = "text";

		String AUDIO = "audio";

		String PHOTO = "photo";

		String POSITION = "position";

		String NAME_CARD = "name_card";

		String MENTION = "mention";
	}

	public interface BaseParameter {

		String NEXT_MAX_ID = "next_max_id";

		String COUNT = "count";
	}

	public ChatRequester(String localRequestId, IHttpListener httpListener) {
		super(localRequestId, httpListener);
	}

	public void createGroup(CreateGroup createGroup) {

		path = FusionConfig.GROUP_URL;

		method = HttpMethod.POST;

		setJsonObject((JsonObject) createGroup.toJson());

		start();
	}

	public void sendMessage(MessageInfo message) {

		path = HttpUtil.buildURL(FusionConfig.GROUP_MESSAGE_URL);

		method = HttpMethod.POST;

		putJsonParameter(MessageParameter.RECIPIENT, message.recipient);

		switch (message.messageType) {

		case MessageType.TEXT: {

			putJsonParameter(MessageParameter.TEXT, message.text);

			List<String> userIds = new ArrayList<String>();

			if (null != message.mention) {

				for (User user : message.mention) {

					if (TextUtils.isEmpty(user.userId)) {

						continue;
					}

					userIds.add(user.userId);
				}

				putJsonParameter(MessageParameter.MENTION,
						BaseModel.toJsonArray(userIds));
			}

			break;
		}
		case MessageType.AUDIO: {

			putJsonParameter(MessageParameter.AUDIO, message.audio.toJson());

			break;
		}
		case MessageType.PHOTO: {

			putJsonParameter(MessageParameter.PHOTO, message.photo.toJson());

			break;
		}
		case MessageType.POSITION: {

			putJsonParameter(MessageParameter.POSITION,
					message.position.toJson());

			break;
		}
		case MessageType.NAME_CARD: {

			putJsonParameter(MessageParameter.NAME_CARD,
					message.name_card.toJson());

			break;
		}
		default: {
			Log.e(TAG, "UnKnow message , message.type = " + message.messageType);
			break;
		}
		}

		start();
	}

	public void syncMessages(String messageId) {

		path = HttpUtil.buildURL(FusionConfig.GROUP_MESSAGE_URL);

		method = HttpMethod.GET;

		if (!TextUtils.isEmpty(messageId)) {

			appendQuery(BaseParameter.NEXT_MAX_ID, messageId);

			appendQuery(BaseParameter.COUNT,
					CommonColumnsValue.MESSAGE_COUNT_VALUE + "");
		}

		start();
	}

	public void syncGroups() {

		path = FusionConfig.GROUP_URL;

		method = HttpMethod.GET;

		start();
	}

	public void updateGroupName(String groupId, String name) {

		path = HttpUtil.buildPath(FusionConfig.UPDATE_GROUP_INFO_URL, groupId);

		method = HttpMethod.PUT;

		putJsonParameter(UpdateGroupParameter.NAME, name);

		start();
	}

	public void updateGroupIsMuted(String groupId, boolean isMuted) {

		path = HttpUtil.buildPath(FusionConfig.UPDATE_GROUP_INFO_URL, groupId);

		method = HttpMethod.PUT;

		putJsonParameter(UpdateGroupParameter.IS_MUTED, isMuted);

		start();
	}

	public void deleteUnjoinedGroup(String groupId) {

		path = HttpUtil.buildPath(FusionConfig.UPDATE_GROUP_INFO_URL, groupId);

		method = HttpMethod.DELETE;

		start();
	}

	public void updateGroupMember(String action, String groupId,
			List<String> userIds) {

		path = HttpUtil
				.buildPath(FusionConfig.UPDATE_GROUP_MEMBER_URL, groupId);

		method = HttpMethod.POST;

		putJsonParameter(UpdateGroupParameter.ACTION, action);

		putJsonParameter(UpdateGroupParameter.USER,
				BaseModel.toJsonArray(userIds));

		start();
	}

	public void getNotifyActionMe(String nextMaxId, int count) {

		path = FusionConfig.GET_NOTIFY_ACTION_ME_URL;

		method = HttpMethod.GET;

		appendQuery(BaseParameter.NEXT_MAX_ID, nextMaxId);

		appendQuery(BaseParameter.COUNT, count + "");

		start();
	}
}
