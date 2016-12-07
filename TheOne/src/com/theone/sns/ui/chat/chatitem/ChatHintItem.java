package com.theone.sns.ui.chat.chatitem;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.theone.sns.R;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

/**
 * Created by zhangwenhai on 2014/10/29.
 */
public class ChatHintItem extends ChatBaseItem {

	protected ChatHintItem(MessageInfo mMessageInfo) {
		this.mMessageInfo = mMessageInfo;
	}

	@Override
	public View layoutInflater(LayoutInflater inflater) {
		return inflater.inflate(R.layout.list_item_time_split, null);
	}

	@Override
	public void bindView(View view, ImageLoaderViewHolder holder) {
		holder.mTextView = (TextView) view.findViewById(R.id.time_split);
	}

	@Override
	public void drowView(ImageLoaderViewHolder holder, DisplayImageOptions optionsForUserIcon,
			Activity mActivity, DisplayImageOptions optionsForChatImage) {
		holder.mTextView.setBackgroundColor(Color.parseColor("#00000000"));
		holder.mTextView.setBackgroundResource(R.drawable.chat_timestamp);
		holder.mTextView.setText(mMessageInfo.text);
	}
}
