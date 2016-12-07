package com.theone.sns.logic.model.chat;

import java.util.List;

import android.text.TextUtils;

import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.user.User;

@SuppressWarnings("serial")
public class GroupInfo extends BaseModel {

	/**
	 * 组ID
	 */
	public String _id;

	/**
	 * 组的创建者
	 */
	public User owner;

	/**
	 * 组名称
	 */
	public String name;

	/**
	 * 组成员
	 */
	public List<User> members;

	/**
	 * 组的最新消息
	 */
	public MessageInfo newMessage;

	/**
	 * 未读消息数
	 */
	public int unReadCount;

	/**
	 * 组是否静音
	 */
	public boolean is_muted;

	/**
	 * 是否加入该组
	 */
	public boolean is_joined;

	/**
	 * 是否置顶
	 */
	public boolean is_top = false;

	/**
	 * 是否删除
	 */
	public boolean is_delete = false;

	/**
	 * 是否是组邀请
	 */
	public boolean is_group_invite = false;

	/**
	 * 组的更新时间
	 */
	public String updateTime;

	public boolean equals(Object o) {

		if (this == o) {

			return true;
		}

		if (!(o instanceof GroupInfo) || null == o) {

			return false;
		}

		final GroupInfo other = (GroupInfo) o;

		if (TextUtils.isEmpty(other._id)) {

			return false;

		} else if (other._id.equals(this._id)) {

			return true;

		} else {

			return false;
		}
	}
}
