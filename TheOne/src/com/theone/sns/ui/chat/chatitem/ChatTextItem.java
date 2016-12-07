package com.theone.sns.ui.chat.chatitem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.ui.base.dialog.TheOneAlertDialog;
import com.theone.sns.ui.chat.activity.ChatActivity;
import com.theone.sns.ui.me.GotoTaActivityOnClickListener;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.ImageLoaderUtil;
import com.theone.sns.util.uiwidget.ExpressionUtil;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

/**
 * Created by zhangwenhai on 2014/10/29.
 */
public class ChatTextItem extends ChatBaseItem {

    private final IChatLogic mIChatLogic;

    protected ChatTextItem(MessageInfo mMessageInfo, IChatLogic mIChatLogic) {
        this.mMessageInfo = mMessageInfo;
        this.mIChatLogic = mIChatLogic;
    }

    @Override
    public View layoutInflater(LayoutInflater inflater) {
        if (FusionConfig.getInstance().getUserId().equals(mMessageInfo.owner.userId)) {
            return inflater.inflate(R.layout.chat_text_send, null);
        } else {
            return inflater.inflate(R.layout.chat_text_recv, null);
        }
    }

    @Override
    public void bindView(View view, ImageLoaderViewHolder holder) {
        holder.imageView = (ImageView) view.findViewById(R.id.userAvatar);
        holder.mTextView = (TextView) view.findViewById(R.id.msgContent);
        holder.imageView1 = (ImageView) view.findViewById(R.id.iten_sent_status);
    }

    @Override
    public void drowView(ImageLoaderViewHolder holder, DisplayImageOptions optionsForUserIcon,
                         final Activity mActivity, DisplayImageOptions optionsForChatImage) {
        holder.mTextView.setBackgroundColor(Color.parseColor("#00000000"));
        if (FusionConfig.getInstance().getUserId().equals(mMessageInfo.owner.userId)) {
            holder.mTextView.setBackgroundResource(R.drawable.chatto_bg);
            if (mMessageInfo.status == FusionCode.MessageStatusType.SEND_FAIL) {
                holder.imageView1.setImageResource(R.drawable.sendfail);
                holder.imageView1.setVisibility(View.VISIBLE);
                holder.imageView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new TheOneAlertDialog.Builder(mActivity)
                                .setMessage(R.string.resend_chat)
                                .setPositiveButton(R.string.confirm,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                mIChatLogic.sendToServer(mMessageInfo);
                                            }
                                        })
                                .setNegativeButton(R.string.Cancel,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).show();
                    }
                });
            } else {
                holder.imageView1.setVisibility(View.GONE);
                holder.imageView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
        } else {
            holder.mTextView.setBackgroundResource(R.drawable.chatfrom_bg);
        }
        ImageLoaderUtil.loadImage(mMessageInfo.owner.avatar_url, holder.imageView,
                optionsForUserIcon);
        holder.imageView.setOnClickListener(new GotoTaActivityOnClickListener(mActivity,
                mMessageInfo.owner.userId));
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(mMessageInfo.owner.name) && !FusionConfig.getInstance().getUserId().equals(mMessageInfo.owner.userId))
                    LocalBroadcastManager.getInstance(mActivity)
                            .sendBroadcast(
                                    new Intent(FusionAction.ChatAction.AT_SOMEONE).putExtra(
                                            FusionAction.ChatAction.AT_SOMEONE,
                                            mMessageInfo.owner));
                return true;
            }
        });
        holder.mTextView.setText(ExpressionUtil.getInstance().strToSmiley(mMessageInfo.text));
    }

    @Override
    protected void setListener(final Activity mActivity, View view) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                final String[] cities = {"删除", "转发", "复制"};
                builder.setItems(cities, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: {
                                mIChatLogic.deleteMessage(mMessageInfo._id);
                                break;
                            }
                            case 1: {
                                mActivity.startActivityForResult(new Intent(
                                                FusionAction.ChatAction.FORWARD_ACTION).putExtra(
                                                FusionAction.ChatAction.MESSGAE_INFO, mMessageInfo),
                                        ChatActivity.CONTEXT_INCLUDE_CODE);
                                break;
                            }
                            case 2: {
                                HelperFunc.copy(mMessageInfo.text, mActivity.getApplicationContext());
                                break;
                            }
                            default:
                        }
                    }
                });
                builder.show();
                return false;
            }
        });
    }
}
