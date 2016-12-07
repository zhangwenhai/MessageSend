package com.theone.sns.logic.chat.impl;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.theone.sns.common.FusionCode.GroupMemberAction;
import com.theone.sns.common.FusionCode.MessageStatusType;
import com.theone.sns.common.FusionCode.NotifyMeType;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType.ChatMessageType;
import com.theone.sns.component.http.IHttpListener;
import com.theone.sns.component.http.Result;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.framework.logic.BaseLogic;
import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.adapter.db.GroupDbAdapter;
import com.theone.sns.logic.adapter.db.MessageDbAdapter;
import com.theone.sns.logic.adapter.db.SettingDbAdapter;
import com.theone.sns.logic.adapter.db.model.MessageColumns;
import com.theone.sns.logic.adapter.http.ChatRequester;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.chat.IPushListener;
import com.theone.sns.logic.model.chat.CreateGroup;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.logic.model.chat.NotifyMe;
import com.theone.sns.logic.model.chat.SearchResult;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.model.user.base.Gallery;
import com.theone.sns.util.HttpUtil;
import com.theone.sns.util.PrettyDateFormat;
import com.theone.sns.util.StringUtil;

public class ChatLogicImpl extends BaseLogic implements IChatLogic {

	private static final String TAG = "ChatLogicImpl";

	@Override
	public String createGroup(CreateGroup createGroup) {

		String requestId = StringUtil.getRequestSerial();

		String meUserId = FusionConfig.getInstance().getUserId();

		if (null == createGroup || null == createGroup.members) {

			Log.e(TAG, "in method createGroup, createGroup paramter error");

			return requestId;
		}

		if (createGroup.members.size() == 1
				&& createGroup.members.get(0).equals(meUserId)) {

			Log.e(TAG, "in method createGroup, can not chat with me");

			return requestId;
		}

		new ChatRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					BaseModel mBaseModel = BaseModel.fromJson(
							result.jsonObject, GroupInfo.class);

					GroupInfo groupInfo = (GroupInfo) mBaseModel;

					GroupInfo groupInfoDB = GroupDbAdapter.getInstance()
							.getGroupById(groupInfo._id);

					if (null != groupInfoDB) {

						if (groupInfoDB.is_delete) {

							GroupDbAdapter.getInstance().updateGroupForDelete(
									groupInfoDB._id, false);
						}

					} else {

						GroupDbAdapter.getInstance().insertGroup(groupInfo);
					}

					ChatManager.getInstance().callBackPushListener(
							IPushListener.GROUP_CHANGE, groupInfo);

					sendMessage(ChatMessageType.CREATE_GROUP_SUCCESS,
							new UIObject(result.localRequestId, groupInfo));

				} else {

					sendEmptyMessage(ChatMessageType.CREATE_GROUP_FAIL);
				}
			}

		}).createGroup(createGroup);

		return requestId;
	}

	@Override
	public String sendMessage(MessageInfo message) {

		sendInLocal(message);

		return sendToServer(message);
	}

	@Override
	public void sendInLocal(MessageInfo message) {

		if (null == message) {

			Log.e(TAG, "sendInLocal paramter error");

			return;
		}

		processMessage(message);
	}

	@Override
	public String sendToServer(final MessageInfo message) {

		String requestId = StringUtil.getRequestSerial();

		if (null == message || TextUtils.isEmpty(message.db_id)) {

			Log.e(TAG, "sendToServer paramter error");

			return requestId;
		}

		new ChatRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					BaseModel mBaseModel = BaseModel.fromJson(
							result.jsonObject, MessageInfo.class);

					MessageInfo message_server = (MessageInfo) mBaseModel;

					setMessageValue(message_server, message);

					MessageDbAdapter.getInstance().updateMessageForSend(
							message_server);

					GroupDbAdapter.getInstance().updateGroupNewMessage(
							message_server.recipient, message_server);

					ChatManager.getInstance().callBackPushListener(
							IPushListener.MESSAGE_UPDATE, message_server);

				} else {

					sendFail(message);
				}
			}

		}).sendMessage(message);

		return requestId;
	}

	public void sendFail(MessageInfo message) {

		message.status = MessageStatusType.SEND_FAIL;

		MessageDbAdapter.getInstance().updateMessageStatusByDBIDForSend(
				message.db_id, message.status);

		ChatManager.getInstance().callBackPushListener(
				IPushListener.MESSAGE_UPDATE, message);
	}

	@Override
	public String syncGroups() {

		String requestId = StringUtil.getRequestSerial();

		new ChatRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					ChatManager chatManager = ChatManager.getInstance();

					String meUserId = FusionConfig.getInstance().getUserId();

					List<GroupInfo> groupList = new ArrayList<GroupInfo>();

					List<BaseModel> baseModelList = BaseModel.fromJson(
							result.jsonArray, GroupInfo.class);

					List<GroupInfo> groupDBList = GroupDbAdapter.getInstance()
							.getAllGroup();

					if (null != baseModelList && !baseModelList.isEmpty()) {

						for (BaseModel baseModel : baseModelList) {

							GroupInfo group = (GroupInfo) baseModel;

							if (chatManager.isOnlyMeGroup(meUserId, group)) {

								continue;
							}

							chatManager.syncToDB(groupDBList, group);

							groupList.add(group);
						}

						chatManager.deleteInvalidGroup(groupDBList, groupList);

					} else {

						GroupDbAdapter.getInstance().deleteAllGroup();
					}

					sendMessage(ChatMessageType.SYNC_ALL_GROUP_SUCCESS,
							new UIObject(result.localRequestId, groupList));

				} else {

					sendEmptyMessage(ChatMessageType.SYNC_ALL_GROUP_FAIL);
				}
			}

		}).syncGroups();

		return requestId;
	}

	@Override
	public String updateGroupName(final String groupId, final String name) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(name)) {

			Log.e(TAG, "in method updateGroupName paramter error");

			return requestId;
		}

		new ChatRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					GroupDbAdapter.getInstance().updateGroupName(groupId, name);

					sendEmptyMessage(ChatMessageType.UPDATE_GROUP_NAME_SUCCESS);

				} else {

					sendEmptyMessage(ChatMessageType.UPDATE_GROUP_NAME_FAIL);
				}
			}

		}).updateGroupName(groupId, name);

		return requestId;
	}

	@Override
	public String updateGroupIsMuted(final String groupId, final boolean isMuted) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(groupId)) {

			Log.e(TAG, "in method updateGroupIsMuted paramter error");

			return requestId;
		}

		new ChatRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					GroupDbAdapter.getInstance().updateGroupIsMuted(groupId,
							isMuted);
				}

				GroupInfo groupInfo = GroupDbAdapter.getInstance()
						.getGroupById(groupId);

				ChatManager.getInstance().callBackPushListener(
						IPushListener.GROUP_CHANGE, groupInfo);
			}

		}).updateGroupIsMuted(groupId, isMuted);

		return requestId;
	}

	@Override
	public String deleteUnjoinedGroup(final String groupId) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(groupId)) {

			Log.e(TAG, "in method deleteUnjoinedGroup paramter error");

			return requestId;
		}

		new ChatRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					GroupDbAdapter.getInstance().deleteGroupByGroupIdForQuit(
							groupId);

					sendEmptyMessage(ChatMessageType.DELETE_UNJOINED_GROUP_SUCCESS);

				} else {

					sendEmptyMessage(ChatMessageType.DELETE_UNJOINED_GROUP_FAIL);
				}
			}

		}).deleteUnjoinedGroup(groupId);

		return requestId;
	}

	@Override
	public String updateGroupMember(final String action, final String groupId,
			final List<User> userList) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(action) || TextUtils.isEmpty(groupId)
				|| null == userList || userList.isEmpty()) {

			Log.e(TAG, "in method updateGroupMember paramter error");

			return requestId;
		}

		new ChatRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					if (GroupMemberAction.QUIT.equals(action)) {

						GroupDbAdapter.getInstance()
								.deleteGroupByGroupIdForQuit(groupId);
					} else {

						BaseModel mBaseModel = BaseModel.fromJson(
								result.jsonObject, GroupInfo.class);

						GroupInfo groupInfo = (GroupInfo) mBaseModel;

						if (-1 == GroupDbAdapter.getInstance().insertGroup(
								groupInfo)) {

							GroupDbAdapter.getInstance().updateGroupMember(
									groupInfo);
						}
					}

					GroupInfo groupInfo = GroupDbAdapter.getInstance()
							.getGroupById(groupId);

					ChatManager.getInstance().callBackPushListener(
							IPushListener.GROUP_CHANGE, groupInfo);

					sendEmptyMessage(ChatMessageType.UPDATE_MEMBER_GROUP_INFO_SUCCESS);

				} else {

					sendEmptyMessage(ChatMessageType.UPDATE_MEMBER_GROUP_INFO_FAIL);
				}
			}

		}).updateGroupMember(action, groupId, getUserIds(userList));

		return requestId;
	}

	@Override
	public String updateMessageIsRead(final String groupId) {

		final String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(groupId)) {

			Log.e(TAG, "updateGroupMessageIsRead fail,groupId is null");
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				MessageDbAdapter.getInstance().updateMessageHasRead(groupId);

				GroupInfo groupInfo = GroupDbAdapter.getInstance()
						.getGroupById(groupId);

				ChatManager.getInstance().getAllUnReadMessageCount();

				ChatManager.getInstance().callBackPushListener(
						IPushListener.GROUP_CHANGE, groupInfo);
			}

		}).start();

		return requestId;
	}

	@Override
	public long updateMediaMessageIsRead(String messageId) {

		long result = -1;

		if (TextUtils.isEmpty(messageId)) {

			Log.e(TAG, "updateMediaMessageIsRead fail,messageId is null");

			return result;
		}

		result = MessageDbAdapter.getInstance().updateMediaMessageHasRead(
				messageId);

		return result;
	}

	@Override
	public void updateGroupInTop(final String groupId, final boolean isTop) {

		if (TextUtils.isEmpty(groupId)) {

			Log.e(TAG, "in method updateGroupInTop paramter error");
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				GroupDbAdapter.getInstance().updateGroupInTop(groupId, isTop);

				GroupInfo groupInfo = GroupDbAdapter.getInstance()
						.getGroupById(groupId);

				ChatManager.getInstance().callBackPushListener(
						IPushListener.GROUP_CHANGE, groupInfo);
			}

		}).start();
	}

	@Override
	public void updateGroupForDelete(final String groupId,
			final boolean isDelete) {

		if (TextUtils.isEmpty(groupId)) {

			Log.e(TAG, "in method updateGroupForDelete paramter error");

			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				GroupDbAdapter.getInstance().updateGroupForDelete(groupId,
						isDelete);

				ChatManager.getInstance().getAllUnReadMessageCount();
			}

		}).start();
	}

	@Override
	public String deleteMessage(final String messageId) {

		final String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(messageId)) {

			Log.e(TAG, "in method deleteMessage paramter error");

		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				MessageInfo messageInfo = MessageDbAdapter.getInstance()
						.getMessageById(messageId);

				if (null != messageInfo) {

					MessageDbAdapter.getInstance().deleteMessage(messageId);

					ChatManager.getInstance()
							.callBackPushListener(IPushListener.MESSAGE_DELETE,
									messageInfo.recipient);
				}
			}

		}).start();

		return requestId;
	}

	@Override
	public String deleteAllMessage(final String groupId) {

		final String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(groupId)) {

			Log.e(TAG, "in method deleteAllMessage paramter error");

		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				MessageDbAdapter.getInstance().deleteAllMessage(groupId);

				ChatManager.getInstance().callBackPushListener(
						IPushListener.MESSAGE_DELETE, groupId);
			}

		}).start();

		return requestId;
	}

	@Override
	public String getGroupInfoFromDB(final List<String> members) {

		final String requestId = StringUtil.getRequestSerial();

		if (null == members || members.size() != 1) {

			return requestId;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				String meUserId = FusionConfig.getInstance().getUserId();

				GroupInfo groupInfo = null;

				List<GroupInfo> groupList = GroupDbAdapter.getInstance()
						.getAllGroupIsJoined(true);

				if (null != groupList && groupList.size() > 0) {

					for (GroupInfo group : groupList) {

						List<User> userList = group.members;

						if (null == userList || userList.size() != 2) {

							continue;
						}

						User member1 = userList.get(0);

						User member2 = userList.get(1);

						if ((meUserId.equals(member1.userId) || meUserId
								.equals(member2.userId))
								&& (members.get(0).equals(member1.userId) || members
										.get(0).equals(member2.userId))
								&& !meUserId.equals(members.get(0))) {

							groupInfo = group;

							if (groupInfo.is_delete) {

								GroupDbAdapter.getInstance()
										.updateGroupForDelete(groupInfo._id,
												false);

								groupInfo.is_delete = false;
							}

							break;
						}
					}
				}

				sendMessage(ChatMessageType.GET_CHAT_GROUP_FROM_DB,
						new UIObject(requestId, groupInfo));
			}

		}).start();

		return requestId;
	}

	@Override
	public String getMessageFromDB(final String recipient,
			final String messageTime) {

		final String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(recipient)) {

			return requestId;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				List<MessageInfo> messageList = MessageDbAdapter.getInstance()
						.getAllMessage(recipient, messageTime);

				sendMessage(ChatMessageType.GET_MESSAGE_LIST_FROM_DB,
						new UIObject(requestId, messageList));
			}

		}).start();

		return requestId;
	}

	@Override
	public String getPhotoMessageFromDB(final String recipient) {

		final String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(recipient)) {

			return requestId;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				List<MessageInfo> messageList = MessageDbAdapter.getInstance()
						.getAllPhotoMessage(recipient);

				sendMessage(ChatMessageType.GET_PHOTO_MESSAGE_LIST_FROM_DB,
						new UIObject(requestId, messageList));
			}

		}).start();

		return requestId;
	}

	@Override
	public String getAllJoinedGroupFromDB() {

		final String requestId = StringUtil.getRequestSerial();

		new Thread(new Runnable() {

			@Override
			public void run() {

				List<GroupInfo> groupList = GroupDbAdapter.getInstance()
						.getJoinedNoDeleteGroup();

				sendMessage(ChatMessageType.GET_JOINED_GROUP_LIST_FROM_DB,
						new UIObject(requestId, groupList));
			}

		}).start();

		return requestId;
	}

	@Override
	public String getAllUnJoinedGroupFromDB() {

		final String requestId = StringUtil.getRequestSerial();

		new Thread(new Runnable() {

			@Override
			public void run() {

				List<GroupInfo> groupList = GroupDbAdapter.getInstance()
						.getAllGroupIsJoined(false);

				sendMessage(ChatMessageType.GET_UNJOINED_GROUP_LIST_FROM_DB,
						new UIObject(requestId, groupList));
			}

		}).start();

		return requestId;
	}

	@Override
	public GroupInfo getGroupInfoFromDB(String groupId) {

		if (TextUtils.isEmpty(groupId)) {

			Log.e(TAG, "getGroupInfoFromDB error, group id is null");

			return new GroupInfo();
		}

		return GroupDbAdapter.getInstance().getGroupById(groupId);
	}

	@Override
	public String getNotifyActionMe(final String nextMaxId, int count) {

		String requestId = StringUtil.getRequestSerial();

		new ChatRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<NotifyMe> notifyMeList = new ArrayList<NotifyMe>();

					List<BaseModel> baseModelList = BaseModel.fromJson(
							result.jsonArray, NotifyMe.class);

					if (null != baseModelList && !baseModelList.isEmpty()) {

						for (BaseModel baseModel : baseModelList) {

							NotifyMe notifyMe = (NotifyMe) baseModel;

							setNotifyMeType(notifyMe);

							if (-1 != notifyMe.type) {

								notifyMeList.add(notifyMe);
							}
						}
					}

					if (TextUtils.isEmpty(nextMaxId)) {

						sendMessage(
								ChatMessageType.PULL_NOTIFY_ME_LIST_SUCCESS,
								new UIObject(result.localRequestId,
										notifyMeList));

					} else {

						sendMessage(
								ChatMessageType.PUSH_NOTIFY_ME_LIST_SUCCESS,
								new UIObject(result.localRequestId,
										notifyMeList));
					}
				} else {

					sendEmptyMessage(ChatMessageType.GET_NOTIFY_ME_LIST_FAIL);
				}
			}

			private void setNotifyMeType(NotifyMe notifyMe) {

				if (null != notifyMe.owner) {

					notifyMe.owner.avatar_url = HttpUtil
							.addUserAvatarUrlWH(notifyMe.owner.avatar_url);
				}

				if (null != notifyMe.follow && !notifyMe.follow.isEmpty()) {

					notifyMe.type = NotifyMeType.FOLLOW;

					List<User> users = notifyMe.follow;

					for (User user : users) {

						user.avatar_url = HttpUtil
								.addUserAvatarUrlWH(user.avatar_url);
					}

				} else {

					List<Gallery> gallerys = null;

					if (null != notifyMe.like && !notifyMe.like.isEmpty()) {

						notifyMe.type = NotifyMeType.LIKE;

						gallerys = notifyMe.like;

					} else if (null != notifyMe.comment
							&& !notifyMe.comment.isEmpty()) {

						notifyMe.type = NotifyMeType.COMMENT;

						gallerys = notifyMe.comment;

					} else if (null != notifyMe.mention
							&& !notifyMe.mention.isEmpty()) {

						notifyMe.type = NotifyMeType.MENTION;

						gallerys = notifyMe.mention;
					}

					if (null != gallerys) {

						for (Gallery gallery : gallerys) {

							gallery.url = HttpUtil
									.addUserAvatarUrlWH(gallery.url);
						}
					}
				}
			}

		}).getNotifyActionMe(nextMaxId, count);

		return requestId;
	}

	@Override
	public String searchChat(final String search) {

		final String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(search)) {

			return requestId;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				SearchResult searchResult = new SearchResult();

				searchResult.groupList = searchGroup();

				searchResult.messageList = MessageDbAdapter.getInstance()
						.searchMessage(search);

				sendMessage(ChatMessageType.SEARCH_CHAT_FROM_DB, new UIObject(
						requestId, searchResult));
			}

			private List<GroupInfo> searchGroup() {

				List<GroupInfo> groupListResult = new ArrayList<GroupInfo>();

				List<GroupInfo> groupList = GroupDbAdapter.getInstance()
						.getJoinedNoDeleteGroup();

				for (GroupInfo group : groupList) {

					String groupName = group.name;

					if (!TextUtils.isEmpty(groupName)
							&& groupName.indexOf(search) != -1) {

						groupListResult.add(group);

						continue;
					}

					List<User> members = group.members;

					if (null == members || members.isEmpty()) {

						continue;
					}

					for (User member : members) {

						String userName = member.name;

						if (!TextUtils.isEmpty(userName)
								&& userName.indexOf(search) != -1) {

							groupListResult.add(group);

							break;
						}
					}
				}

				return groupListResult;
			}

		}).start();

		return requestId;
	}

	@Override
	public void setNotifyBadge(final String key, final boolean value) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				SettingDbAdapter.getInstance().insert(key, value);

				ChatManager.getInstance().callBackPushListener(
						IPushListener.NOTIFICATION, null);
			}
		}).start();
	}

	@Override
	public void setNotifyBadge(final String key, final int value) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				SettingDbAdapter.getInstance().insert(key, value);

				ChatManager.getInstance().getAllUnReadMessageCount();
			}
		}).start();
	}

	@Override
	public void setNotifyBadge(final String key, final String value) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				SettingDbAdapter.getInstance().insert(key, value);

				ChatManager.getInstance().getAllUnReadMessageCount();
			}
		}).start();
	}

	private MessageInfo processMessage(MessageInfo message) {

		message.owner = FusionConfig.getInstance().getMyUser();

		message.isRead = true;

		message.status = MessageStatusType.SEND_PROCESS;

		message.mediaIsRead = true;

		message.messageType = MessageColumns.getMessageType(message);

		message.created_at = PrettyDateFormat.getISO8601Time();

		message.db_id = MessageDbAdapter.getInstance().insertMessage(message)
				+ "";

		ChatManager.getInstance().callBackPushListener(
				IPushListener.MESSAGE_ADD, message);

		return message;
	}

	private void setMessageValue(MessageInfo message_server,
			MessageInfo messageLocal) {

		message_server.isRead = true;

		message_server.mediaIsRead = true;

		message_server.status = MessageStatusType.SEND_SUCCESS;

		message_server.messageType = MessageColumns
				.getMessageType(message_server);

		message_server.db_id = messageLocal.db_id;
	}

	private List<String> getUserIds(List<User> userList) {

		List<String> userIds = new ArrayList<String>();

		for (User user : userList) {

			userIds.add(user.userId);
		}

		return userIds;
	}
}
