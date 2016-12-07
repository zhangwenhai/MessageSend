package com.theone.sns.ui.chat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.ui.chat.chatitem.ChatBaseItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/10/29.
 */
public class ChatAdapter extends BaseAdapter {

	private final DisplayImageOptions optionsForChatImage;
	private final Activity mActivity;
	private final IChatLogic mIChatLogic;
	private final DisplayImageOptions optionsForUserIcon;
	private List<MessageInfo> messageList = new ArrayList<MessageInfo>();
	private LayoutInflater inflater;

	public ChatAdapter(Activity mActivity, DisplayImageOptions optionsForChatImage,
			IChatLogic mIChatLogic, DisplayImageOptions optionsForUserIcon) {
		this.mActivity = mActivity;
		this.optionsForChatImage = optionsForChatImage;
		this.optionsForUserIcon = optionsForUserIcon;
		this.mIChatLogic = mIChatLogic;
	}

	@Override
	public int getCount() {
		return messageList.size();
	}

	@Override
	public Object getItem(int i) {
		return i;
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public int getViewTypeCount() {
		return 8;
	}

	@Override
	public int getItemViewType(int position) {
		return getType(position);
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ChatBaseItem mChatBaseItem = ChatBaseItem.getChatBaseItem(messageList.get(i), mIChatLogic);
		view = mChatBaseItem.getView(mActivity, i, view, viewGroup, optionsForUserIcon,
				optionsForChatImage);
		return view;
	}

	public void setMessageList(List<MessageInfo> messageList) {
		this.messageList = messageList;
	}

	private int getType(int position) {
		if (messageList.size() > position) {
			MessageInfo mMessageInfo = messageList.get(position);
			if (null != mMessageInfo && null != mMessageInfo.owner) {
				switch (mMessageInfo.messageType) {
				case FusionCode.MessageType.TEXT: {
					if (FusionConfig.getInstance().getUserId().equals(mMessageInfo.owner.userId)) {
						return 0;
					} else {
						return 1;
					}
				}
				case FusionCode.MessageType.NAME_CARD: {
					if (FusionConfig.getInstance().getUserId().equals(mMessageInfo.owner.userId)) {
						return 2;
					} else {
						return 3;
					}
				}
				case FusionCode.MessageType.PHOTO: {
					if (FusionConfig.getInstance().getUserId().equals(mMessageInfo.owner.userId)) {
						return 4;
					} else {
						return 5;
					}
				}
				case FusionCode.MessageType.AUDIO: {
					if (FusionConfig.getInstance().getUserId().equals(mMessageInfo.owner.userId)) {
						return 6;
					} else {
						return 7;
					}
				}
				default:
				}
			}
		}
		return -1;
	}
}
