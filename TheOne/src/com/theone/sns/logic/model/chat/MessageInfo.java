package com.theone.sns.logic.model.chat;

import java.util.List;

import android.text.TextUtils;

import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.chat.base.NameCard;
import com.theone.sns.logic.model.chat.base.Position;
import com.theone.sns.logic.model.mblog.base.AudioDesc;
import com.theone.sns.logic.model.mblog.base.Photo;
import com.theone.sns.logic.model.user.User;

@SuppressWarnings("serial")
public class MessageInfo extends BaseModel {

	/**
	 * 消息ID
	 */
	public String _id;

	/**
	 * 消息发送者
	 */
	public User owner;

	/**
	 * 消息接收者ID
	 */
	public String recipient;

	/**
	 * 文本消息
	 */
	public String text;

	/**
	 * 语音消息
	 */
	public AudioDesc audio;

	/**
	 * 照片消息
	 */
	public Photo photo;

	/**
	 * 位置消息
	 */
	public Position position;

	/**
	 * 名片消息
	 */
	public NameCard name_card;

	/**
	 * 提到了谁
	 */
	public List<User> mention;

	/**
	 * 有人提到了我
	 */
	public boolean isMentionMe = false;

	/**
	 * 自己发送多媒体消息的网络地址
	 */
	public String network_media_url;

	/**
	 * 是否已读
	 */
	public boolean isRead = false;

	/**
	 * 消息发送状态
	 */
	public int status;

	/**
	 * 多媒体是否已读
	 */
	public boolean mediaIsRead = false;

	/**
	 * 消息类型
	 */
	public int messageType;

	/**
	 * 消息创建时间
	 */
	public String created_at;

	/**
	 * 临时的ID(发消息时,用于识别消息,方便更新消息)
	 */
	public String db_id;

	public boolean equals(Object o) {

		if (this == o) {

			return true;
		}

		if (!(o instanceof MessageInfo) || null == o) {

			return false;
		}

		final MessageInfo other = (MessageInfo) o;

		if (TextUtils.isEmpty(other._id)) {

			return false;

		} else if ((other._id.equals(this._id))
				|| (other.db_id.equals(this.db_id))) {

			return true;

		} else {

			return false;
		}
	}
}
