package com.theone.sns.logic.chat;

import java.util.List;

import com.theone.sns.logic.model.chat.CreateGroup;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.logic.model.user.User;

public interface IChatLogic {

	/**
	 * 创建一个聊天群组
	 */
	String createGroup(CreateGroup createGroup);

	/**
	 * 发送消息(包括本地生成数据,然后直接发送到服务器)
	 */
	String sendMessage(MessageInfo message);

	/**
	 * 发送消息时,先在本地显示，生成发送数据(返回message.db_id)
	 */
	void sendInLocal(MessageInfo message);

	/**
	 * 发消息给服务器(真正的发送), 必须先在数据库有数据message.db_id不能为空
	 */
	String sendToServer(MessageInfo message);

	/**
	 * 发消息失败处理(message.db_id不能为空)
	 */
	void sendFail(MessageInfo message);

	/**
	 * 同步当前用户的Group
	 */
	String syncGroups();

	/**
	 * 更新组名称
	 */
	String updateGroupName(String groupId, String name);

	/**
	 * 组设置是否静音(新消息通知)
	 */
	String updateGroupIsMuted(String groupId, boolean isMuted);

	/**
	 * 拒绝聊天邀请
	 */
	String deleteUnjoinedGroup(String groupId);

	/**
	 * 更新群组的成员信息
	 */
	String updateGroupMember(String action, String groupId, List<User> userList);

	/**
	 * 更新组消息为已读
	 */
	String updateMessageIsRead(String groupId);

	/**
	 * 更新多媒体已读
	 */
	long updateMediaMessageIsRead(String messageId);

	/**
	 * 置顶聊天
	 */
	void updateGroupInTop(String groupId, boolean isTop);

	/**
	 * 删除组条目
	 */
	void updateGroupForDelete(String groupId, boolean isDelete);

	/**
	 * 删除一条消息
	 */
	String deleteMessage(String messageId);

	/**
	 * 清空聊天记录
	 */
	String deleteAllMessage(String groupId);

	/**
	 * 根据成员列表获取对应的组，用于私聊
	 */
	String getGroupInfoFromDB(List<String> members);

	/**
	 * 从数据库中获取群组消息
	 */
	String getMessageFromDB(String recipient, String messageTime);

	/**
	 * 从数据库中获取群组的照片消息
	 */
	String getPhotoMessageFromDB(final String recipient);

	/**
	 * 从数据库中获取所有已经加入的组
	 */
	String getAllJoinedGroupFromDB();

	/**
	 * 从数据库中获取所有未加入的组
	 */
	String getAllUnJoinedGroupFromDB();

	/**
	 * 根据组ID查询组信息
	 */
	GroupInfo getGroupInfoFromDB(String groupId);

	/**
	 * 获取其他人对我的动态
	 */
	String getNotifyActionMe(String nextMaxId, int count);

	/**
	 * 聊天内容，成员搜索
	 */
	String searchChat(String search);

	/**
	 * 设置关注，被赞的红点是否消失
	 */
	void setNotifyBadge(String key, boolean value);

	/**
	 * 设置通知，组邀请显示数目
	 */
	void setNotifyBadge(String key, int value);

	/**
	 * 设置通知，组邀请显示内容
	 */
	void setNotifyBadge(String key, String value);
}
