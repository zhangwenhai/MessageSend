package com.theone.sns.ui.chat.chatitem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.ui.base.dialog.TheOneAlertDialog;
import com.theone.sns.ui.chat.activity.ChatActivity;
import com.theone.sns.ui.me.GotoTaActivityOnClickListener;
import com.theone.sns.util.uiwidget.ExpressionUtil;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

/**
 * Created by zhangwenhai on 2014/10/29.
 */
public class ChatNameCardItem extends ChatBaseItem {

    private final IChatLogic mIChatLogic;

    protected ChatNameCardItem(MessageInfo mMessageInfo, IChatLogic mIChatLogic) {
        this.mMessageInfo = mMessageInfo;
        this.mIChatLogic = mIChatLogic;
    }

    @Override
    public View layoutInflater(LayoutInflater inflater) {
        if (FusionConfig.getInstance().getUserId().equals(mMessageInfo.owner.userId)) {
            return inflater.inflate(R.layout.chat_namecard_send, null);
        } else {
            return inflater.inflate(R.layout.chat_namecard_recv, null);
        }
    }

    @Override
    public void bindView(View view, ImageLoaderViewHolder holder) {
        holder.imageView = (ImageView) view.findViewById(R.id.userAvatar);
        holder.imageView1 = (ImageView) view.findViewById(R.id.namecard_Avatar);
        holder.mTextView = (TextView) view.findViewById(R.id.namecard_name);
        holder.mLinearLayout = (LinearLayout) view.findViewById(R.id.msgContent);
        holder.imageView2 = (ImageView) view.findViewById(R.id.iten_sent_status);

    }

    @Override
    public void drowView(final ImageLoaderViewHolder holder,
                         DisplayImageOptions optionsForUserIcon, final Activity mActivity,
                         DisplayImageOptions optionsForChatImage) {
        ImageLoader.getInstance().displayImage(mMessageInfo.owner.avatar_url, holder.imageView,
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

        if (null != mMessageInfo.name_card) {
            ImageLoader.getInstance().displayImage(mMessageInfo.name_card.avatar_url,
                    holder.imageView1, optionsForUserIcon);
            holder.mTextView.setText(ExpressionUtil.getInstance().strToSmiley(
                    mMessageInfo.name_card.name));
        }

        holder.mLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                final String[] cities = {"删除", "转发"};
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
                            default:
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        holder.mLinearLayout.setOnClickListener(new GotoTaActivityOnClickListener(mActivity,
                mMessageInfo.name_card._id));
        if (FusionConfig.getInstance().getUserId().equals(mMessageInfo.owner.userId)) {
            if (mMessageInfo.status == FusionCode.MessageStatusType.SEND_FAIL) {
                holder.imageView2.setImageResource(R.drawable.sendfail);
                holder.imageView2.setVisibility(View.VISIBLE);
                holder.imageView2.setOnClickListener(new View.OnClickListener() {
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
                holder.imageView2.setVisibility(View.GONE);
                holder.imageView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
        }
    }

    @Override
    protected void setListener(final Activity mActivity, View view) {

    }
}
