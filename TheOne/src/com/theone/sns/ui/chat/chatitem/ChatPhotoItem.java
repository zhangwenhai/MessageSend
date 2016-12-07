package com.theone.sns.ui.chat.chatitem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionCode.MessageType;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.component.upload.io.IO;
import com.theone.sns.component.upload.rs.CallBack;
import com.theone.sns.component.upload.rs.CallRet;
import com.theone.sns.component.upload.rs.UploadCallRet;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.logic.model.mblog.base.Photo;
import com.theone.sns.ui.base.dialog.TheOneAlertDialog;
import com.theone.sns.ui.chat.activity.ChatActivity;
import com.theone.sns.ui.me.GotoTaActivityOnClickListener;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.HttpUtil;
import com.theone.sns.util.ImageLoaderUtil;
import com.theone.sns.util.RoundProgressBar;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/10/29.
 */
public class ChatPhotoItem extends ChatBaseItem {

    private final IChatLogic mIChatLogic;
    private ChatPhotoCallback mChatPhotoCallback;
    private boolean uploading;
    private TextView mTextView;
    private LinearLayout mLinearLayout;
    private RoundProgressBar mRoundProgressBar;
    private int with;
    private int high;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    protected ChatPhotoItem(MessageInfo mMessageInfom, IChatLogic mIChatLogic) {
        this.mMessageInfo = mMessageInfom;
        this.mIChatLogic = mIChatLogic;
    }

    @Override
    public View layoutInflater(LayoutInflater inflater) {
        if (FusionConfig.getInstance().getUserId().equals(mMessageInfo.owner.userId)) {
            return inflater.inflate(R.layout.chat_picture_send, null);
        } else {
            return inflater.inflate(R.layout.chat_picture_recv, null);
        }
    }

    @Override
    public void bindView(View view, ImageLoaderViewHolder holder) {
        holder.imageView = (ImageView) view.findViewById(R.id.userAvatar);
        holder.imageView1 = (ImageView) view.findViewById(R.id.chatPicContent);
        holder.mLinearLayout = (LinearLayout) view.findViewById(R.id.send_progress);
        holder.mTextView = (TextView) view.findViewById(R.id.progressTip);
        holder.imageView2 = (ImageView) view.findViewById(R.id.iten_sent_status);
        holder.mRoundProgressBar1 = (RoundProgressBar) view.findViewById(R.id.progressBar);
    }

    @Override
    public void drowView(final ImageLoaderViewHolder holder,
                         DisplayImageOptions optionsForUserIcon, final Activity mActivity,
                         DisplayImageOptions optionsForChatImage) {
        mTextView = holder.mTextView;
        mLinearLayout = holder.mLinearLayout;
        mRoundProgressBar = holder.mRoundProgressBar1;
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

        if (null != mMessageInfo.photo) {
            if (mMessageInfo.photo.url.startsWith("http")) {
            	String photoUrl = HttpUtil.addGalleryUrlWH(mMessageInfo.photo.url);
                ImageLoader.getInstance().displayImage(photoUrl, holder.imageView1,
                        optionsForChatImage, animateFirstListener, new ImageLoadingProgressListener() {
                            @Override
                            public void onProgressUpdate(String s, View view, final int i,
                                                         final int i2) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (null == holder.mRoundProgressBar1) {
                                            return;
                                        }
                                        if (i == i2) {
                                            holder.mRoundProgressBar1.setVisibility(View.GONE);
                                        } else {
                                            holder.mRoundProgressBar1.setMax(i2);
                                            holder.mRoundProgressBar1.setProgress(i);
                                            holder.mRoundProgressBar1.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                            }
                        });
            } else {
                holder.imageView1.setImageBitmap(ImageLoaderUtil.getBitmapByWidth(
                        mMessageInfo.photo.url, (int) HelperFunc.scale(200), 0,
                        (int) HelperFunc.scale(200)));
            }
            if (null != holder.mLinearLayout) {
                if ((mMessageInfo.status == FusionCode.MessageStatusType.SEND_PROCESS)) {
                    Bitmap bp = BitmapFactory.decodeFile(mMessageInfo.photo.url);
                    if (null != bp) {
                        with = bp.getWidth();
                        high = bp.getHeight();
                        doUpload(new File(mMessageInfo.photo.url));
                        holder.mLinearLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.mLinearLayout.setVisibility(View.GONE);
                }
            }
        }

        holder.imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.startActivity(new Intent(FusionAction.ChatAction.CHATPIC_ACTION)
                        .putExtra(FusionAction.ChatAction.CHATPIC_URL, mMessageInfo.photo.url).putExtra(FusionAction.ChatAction.MESSGAE_INFO, mMessageInfo));
            }
        });

        holder.imageView1.setOnLongClickListener(new View.OnLongClickListener() {
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
                                                if (mMessageInfo.photo.url.contains("http")) {
                                                    mIChatLogic.sendToServer(mMessageInfo);
                                                } else {
                                                    doUpload(new File(mMessageInfo.photo.url));
                                                }
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

    /**
     * 上传文件
     *
     * @param file
     */
    private void doUpload(File file) {
        if (uploading) {
            return;
        }

        IO.putFile(FusionConfig.getInstance().getUploadAuthorizer(), null, file, null,
                new CallBack() {

                    @Override
                    public void onProcess(long current, long total) {
                        int percent = (int) (current * 100 / total);
                        if (null != mTextView)
                            mTextView.setText(percent + "%");
                    }

                    @Override
                    public void onSuccess(UploadCallRet ret) {
                        uploading = false;
                        String photoUrl = FusionConfig.MEDIA_URL_PREFIX + ret.getKey();
                        mLinearLayout.setVisibility(View.GONE);

                        MessageInfo mMessageInfo1 = new MessageInfo();
                        mMessageInfo1.db_id = mMessageInfo.db_id;
                        Photo mPhoto = new Photo();
                        mPhoto.url = photoUrl;
                        mPhoto.h = high;
                        mPhoto.w = with;
                        mMessageInfo1.photo = mPhoto;
                        mMessageInfo1.recipient = mMessageInfo.recipient;
                        mMessageInfo1.messageType = MessageType.PHOTO;
                        mIChatLogic.sendToServer(mMessageInfo1);
                    }

                    @Override
                    public void onFailure(CallRet ret) {
                        if (null != mLinearLayout)
                            mLinearLayout.setVisibility(View.GONE);
                        mIChatLogic.sendFail(mMessageInfo);
                    }
                });
    }

    public static interface ChatPhotoCallback {
        void onMenuItemClick(Bitmap bitmap, ImageView mImageView);
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 0);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
