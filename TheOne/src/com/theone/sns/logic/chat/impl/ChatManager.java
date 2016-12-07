package com.theone.sns.logic.chat.impl;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;

import com.theone.sns.R;
import com.theone.sns.TheOneApp;
import com.theone.sns.common.FusionCode.MessageStatusType;
import com.theone.sns.common.FusionCode.SettingKey;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.component.http.IHttpListener;
import com.theone.sns.component.http.Result;
import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.adapter.db.GroupDbAdapter;
import com.theone.sns.logic.adapter.db.MessageDbAdapter;
import com.theone.sns.logic.adapter.db.SettingDbAdapter;
import com.theone.sns.logic.adapter.http.ChatRequester;
import com.theone.sns.logic.chat.IPushListener;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.ui.base.AbstractRefreshUIThread;
import com.theone.sns.util.ChatUtil;
import com.theone.sns.util.HttpUtil;
import com.theone.sns.util.NotificationBuilder;
import com.theone.sns.util.StringUtil;

public class ChatManager {

	private static ChatManager mChatManager;

	private List<IPushListener> mIMessageListenerList = new ArrayList<IPushListener>();

	private ReadMessageCountRefreshUI mReadMessageCountRefreshUI = new ReadMessageCountRefreshUI();

	private ChatManager() {
	}

	public synchronized static ChatManager getInstance() {

		if (null == mChatManager) {

			mChatManager = new ChatManager();
		}

		return mChatManager;
	}

	public synchronized void addListener(IPushListener messageListener) {

		if (null != mIMessageListenerList) {
			mIMessageListenerList.add(messageListener);
		}
	}

	public synchronized void removeListener(IPushListener messageListener) {

		if (null != mIMessageListenerList) {
			mIMessageListenerList.remove(messageListener);
		}
	}

	public synchronized void callBackPushListener(int what, Object object) {

		if (null == mIMessageListenerList || mIMessageListenerList.isEmpty()) {

			return;
		}

		for (IPushListener listener : mIMessageListenerList) {

			listener.push(what, object);
		}
	}

	public String syncGroups() {

		String requestId = StringUtil.getRequestSerial();

		new ChatRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					String meUserId = FusionConfig.getInstance().getUserId();

					List<GroupInfo> groupList = new ArrayList<GroupInfo>();

					List<BaseModel> baseModelList = BaseModel.fromJson(
							result.jsonArray, GroupInfo.class);

					List<GroupInfo> groupDBList = GroupDbAdapter.getInstance()
							.getAllGroup();

					if (null != baseModelList && !baseModelList.isEmpty()) {

						for (BaseModel baseModel : baseModelList) {

							GroupInfo group = (GroupInfo) baseModel;

							if (isOnlyMeGroup(meUserId, group)) {

								continue;
							}

							syncToDB(groupDBList, group);

							groupList.add(group);
						}

						deleteInvalidGroup(groupDBList, groupList);

					} else {

						GroupDbAdapter.getInstance().deleteAllGroup();
					}

					callBackPushListener(IPushListener.GROUP_CHANGE, null);
				}

				new Handler(Looper.getMainLooper()).post(new Runnable() {

					@Override
					public void run() {

						syncMessages();
					}
				});
			}

		}).syncGroups();

		return requestId;
	}

	public synchronized void syncToDB(List<GroupInfo> groupDBList,
			GroupInfo group) {

		if (null != groupDBList && groupDBList.size() > 0) {

			if (groupDBList.contains(group)) {

				GroupDbAdapter.getInstance().updateGroup(group);

			} else {

				GroupDbAdapter.getInstance().insertGroup(group);
			}

		} else {

			GroupDbAdapter.getInstance().insertGroup(group);
		}
	}

	public synchronized void deleteInvalidGroup(List<GroupInfo> groupDBList,
			List<GroupInfo> groupList) {

		if (null != groupDBList && groupDBList.size() > 0) {

			String meUserId = FusionConfig.getInstance().getUserId();

			for (GroupInfo groupDB : groupDBList) {

				if (!groupList.contains(groupDB)
						|| isOnlyMeGroup(meUserId, groupDB)) {

					GroupDbAdapter.getInstance().deleteGroupByGroupIdForQuit(
							groupDB._id);
				}
			}
		}
	}

	public synchronized boolean isOnlyMeGroup(String meUserId, GroupInfo group) {

		if (null == group) {

			return true;
		}

		List<User> members = group.members;

		if (null == members || members.isEmpty()) {

			return true;
		}

		if (null != members && members.size() == 1
				&& meUserId.equals(members.get(0).userId)) {

			return true;
		}

		return false;
	}

	private synchronized String syncMessages() {

		String requestId = StringUtil.getRequestSerial();

		String newMessageId = SettingDbAdapter.getInstance().getString(
				SettingKey.NEW_MESSAGE_ID);

		new ChatRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<BaseModel> baseModelList = BaseModel.fromJson(
							result.jsonArray, MessageInfo.class);

					if (null != baseModelList && !baseModelList.isEmpty()) {

						for (BaseModel baseModel : baseModelList) {

							MessageInfo message = (MessageInfo) baseModel;

							dealMessage(message);
						}

						new Handler(Looper.getMainLooper())
								.post(new Runnable() {

									@Override
									public void run() {

										syncMessages();
									}
								});
					}
				}
			}

		}).syncMessages(newMessageId);

		return requestId;
	}

	public synchronized void receiveMessage(MessageInfo message) {

		messageNotify(dealMessage(message), message);
	}

	private GroupInfo dealMessage(MessageInfo message) {

		if (null == message) {

			return null;
		}

		SettingDbAdapter.getInstance().insert(SettingKey.NEW_MESSAGE_ID,
				message._id);

		message.isRead = false;

		message.status = MessageStatusType.SEND_SUCCESS;

		message.isMentionMe = isMentionMe(message);

		if (null != message.owner) {

			message.owner.avatar_url = HttpUtil
					.addUserAvatarUrlWH(message.owner.avatar_url);
		}

		GroupInfo groupInfo = GroupDbAdapter.getInstance().getGroupById(
				message.recipient);

		if (null != groupInfo) {

			MessageDbAdapter.getInstance().insertMessage(message);

			GroupDbAdapter.getInstance().updateGroupNewMessage(
					message.recipient, message);

			if (groupInfo.is_delete) {

				GroupDbAdapter.getInstance().updateGroupForDelete(
						message.recipient, false);
			}
		}

		return groupInfo;
	}

	private void messageNotify(GroupInfo groupInfo, MessageInfo message) {

		if (null == groupInfo || null == message) {

			return;
		}

		callBackPushListener(IPushListener.MESSAGE_ADD, message);

		getAllUnReadMessageCount();

		if (ChatUtil.isBackgroundRunning() && null != groupInfo
				&& !groupInfo.is_muted) {

			boolean settingNewMessageNotify = SettingDbAdapter.getInstance()
					.getBoolean(SettingKey.NEW_MESSAGE_NOTIFY_KEY, true);

			if (settingNewMessageNotify) {

				int unReadCount = MessageDbAdapter.getInstance()
						.getAllUnReadMessageCount();

				if (unReadCount <= 0) {
					return;
				}

				NotificationBuilder.chatNotification(unReadCount);

				ChatUtil.messageComePlayMedia();
			}
		}
	}

	private boolean isMentionMe(MessageInfo message) {

		boolean isMentionMe = false;

		if (null != message.mention && message.mention.size() > 0) {

			String meUserId = FusionConfig.getInstance().getUserId();

			for (User user : message.mention) {

				if (meUserId.equals(user.userId)) {

					isMentionMe = true;

					break;
				}
			}
		}

		return isMentionMe;
	}

	public synchronized void getAllUnReadMessageCount() {

		if (null != mReadMessageCountRefreshUI) {

			mReadMessageCountRefreshUI.startQuery();
		}
	}

	class ReadMessageCountRefreshUI extends AbstractRefreshUIThread {

		@Override
		public void loadUIData() {

			int unReadCount = MessageDbAdapter.getInstance()
					.getAllUnReadMessageCount();

			int groupInviteCount = SettingDbAdapter.getInstance().getInt(
					SettingKey.GROUP_INVITE_COUNT);

			int notificationCount = SettingDbAdapter.getInstance().getInt(
					SettingKey.NOTIFICATION_COUNT);

			int count = unReadCount + groupInviteCount + notificationCount;

			if (notificationCount == 0) {

				SettingDbAdapter.getInstance().insert(
						SettingKey.NEW_NOTIFICATION_CONTENT,
						TheOneApp.getContext().getResources()
								.getString(R.string.no_new_message));
			}

			callBackPushListener(IPushListener.UNREAD_MESSAGE_COUNT, count);

			callBackPushListener(IPushListener.GROUP_CHANGE, null);
		}
	}

	public synchronized void clear() {

		mIMessageListenerList.clear();

		mChatManager = null;

		if (null != mReadMessageCountRefreshUI) {
			mReadMessageCountRefreshUI.exitThread();
			mReadMessageCountRefreshUI = null;
		}
	}
}
