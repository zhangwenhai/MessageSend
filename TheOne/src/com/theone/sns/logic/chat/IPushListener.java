package com.theone.sns.logic.chat;

public interface IPushListener {

	/**
	 * 新增一条消息
	 */
	int MESSAGE_ADD = 1;

	/**
	 * 新增一条消息
	 */
	int MESSAGE_UPDATE = 2;

	/**
	 * 删除消息
	 */
	int MESSAGE_DELETE = 3;

	/**
	 * 组发生变化
	 */
	int GROUP_CHANGE = 4;

	/**
	 * 组邀请
	 */
	int GROUP_INVITE = 5;

	/**
	 * 被赞,被评论,被关注
	 */
	int NOTIFICATION = 6;

	/**
	 * 消息未读数
	 */
	int UNREAD_MESSAGE_COUNT = 7;

	void push(int what, Object object);
}
