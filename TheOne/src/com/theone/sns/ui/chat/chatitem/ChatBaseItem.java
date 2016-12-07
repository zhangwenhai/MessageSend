package com.theone.sns.ui.chat.chatitem;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.theone.sns.common.FusionCode;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

/**
 * Created by zhangwenhai on 2014/10/29.
 */
public class ChatBaseItem {

	protected MessageInfo mMessageInfo;
	private LayoutInflater inflater;

	public static ChatBaseItem getChatBaseItem(MessageInfo mMessageInfo, IChatLogic mIChatLogic) {
		if (null != mMessageInfo) {
			if (null == mMessageInfo.owner) {
				return new ChatHintItem(mMessageInfo);
			}
			switch (mMessageInfo.messageType) {
			case FusionCode.MessageType.TEXT: {
				return new ChatTextItem(mMessageInfo, mIChatLogic);
			}
			case FusionCode.MessageType.NAME_CARD: {
				return new ChatNameCardItem(mMessageInfo, mIChatLogic);
			}
			case FusionCode.MessageType.PHOTO: {
				return new ChatPhotoItem(mMessageInfo, mIChatLogic);
			}
			case FusionCode.MessageType.AUDIO: {
				return new ChatAudioItem(mMessageInfo, mIChatLogic);
			}
			case FusionCode.MessageType.POSITION: {
				return new ChatPositionItem(mMessageInfo, mIChatLogic);
			}
			default:
			}
		}
		return new ChatHintItem(mMessageInfo);
	}

	public View layoutInflater(LayoutInflater inflater) {
		return null;
	}

	public void bindView(View view, ImageLoaderViewHolder holder) {

	}

	public void drowView(ImageLoaderViewHolder holder, DisplayImageOptions optionsForUserIcon,
			Activity mActivity, DisplayImageOptions optionsForChatImage) {

	}

	public void setmMessageInfo(MessageInfo mMessageInfo) {
		this.mMessageInfo = mMessageInfo;
	}

	public View getView(final Activity mActivity, int i, View view, ViewGroup viewGroup,
			DisplayImageOptions optionsForUserIcon, DisplayImageOptions optionsForChatImage) {
		final ImageLoaderViewHolder holder;
		if (view == null) {
			if (null == inflater) {
				inflater = LayoutInflater.from(mActivity);
			}
			view = layoutInflater(inflater);
			holder = new ImageLoaderViewHolder();
			bindView(view, holder);
			view.setTag(holder);
		} else {
			holder = (ImageLoaderViewHolder) view.getTag();
		}
		drowView(holder, optionsForUserIcon, mActivity, optionsForChatImage);
		setListener(mActivity, view);
		return view;
	}

	protected void setListener(Activity mActivity, View view) {

	}
}
