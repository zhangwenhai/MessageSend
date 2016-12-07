package com.theone.sns.logic.model.mblog.base;

import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.user.User;

@SuppressWarnings("serial")
public class Comment extends BaseModel {

	/**
	 * 评论唯一标识
	 */
	public String _id;

	/**
	 * 评论者
	 */
	public User owner;

	/**
	 * 被评论对象
	 */
	public User target_user;

	/**
	 * 评论内容
	 */
	public String text;

	/**
	 * 评论创建时间
	 */
	public String created_at;
}