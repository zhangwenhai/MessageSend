package com.theone.sns.ui.chat.chatitem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.component.upload.io.IO;
import com.theone.sns.component.upload.rs.CallBack;
import com.theone.sns.component.upload.rs.CallRet;
import com.theone.sns.component.upload.rs.UploadCallRet;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.logic.model.mblog.base.AudioDesc;
import com.theone.sns.ui.base.dialog.TheOneAlertDialog;
import com.theone.sns.ui.chat.activity.ChatActivity;
import com.theone.sns.ui.me.GotoTaActivityOnClickListener;
import com.theone.sns.util.DownloadUtil;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.RoundProgressBar;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

import java.io.File;

/**
 * Created by zhangwenhai on 2014/10/29.
 */
public class ChatAudioItem extends ChatBaseItem {

    private boolean uploading;
    private TextView mTextView;
    private ProgressBar mLinearLayout;
    private IChatLogic mIChatLogic;
    private MediaPlayer player;
    private boolean isplayingId = false;

    protected ChatAudioItem(MessageInfo mMessageInfo, IChatLogic mIChatLogic) {
        this.mMessageInfo = mMessageInfo;
        this.mIChatLogic = mIChatLogic;
    }

    @Override
    public View layoutInflater(LayoutInflater inflater) {
        if (FusionConfig.getInstance().getUserId().equals(mMessageInfo.owner.userId)) {
            return inflater.inflate(R.layout.chat_voice_send, null);
        } else {
            return inflater.inflate(R.layout.chat_voice_recv, null);
        }
    }

    @Override
    public void bindView(View view, ImageLoaderViewHolder holder) {
        holder.imageView = (ImageView) view.findViewById(R.id.userAvatar);
        holder.mTextView = (TextView) view.findViewById(R.id.voiceLen);
        holder.mProgressBar = (ProgressBar) view.findViewById(R.id.loading_progress);
        holder.imageView1 = (ImageView) view.findViewById(R.id.msgContent);
        holder.mRoundProgressBar = (RoundProgressBar) view.findViewById(R.id.sound_playbar);
        holder.mLinearLayout = (LinearLayout) view.findViewById(R.id.msgContentLayout);
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
                                            FusionAction.ChatAction.AT_SOMEONE, mMessageInfo.owner));
                return true;
            }
        });

        if (null != mMessageInfo.audio) {
            if (mMessageInfo.audio.url.startsWith("http")) {
                holder.mProgressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DownloadUtil.downFile(mMessageInfo.audio.url, mMessageInfo._id,
                                new DownloadUtil.DownloadListener() {
                                    @Override
                                    public void onComplete() {
                                        mActivity.runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                holder.mProgressBar.setVisibility(View.GONE);
                                                holder.imageView1
                                                        .setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(final View view) {
                                                                if (null != player
                                                                        && player.isPlaying()) {
                                                                    player.stop();
                                                                    player.release();
                                                                    holder.mRoundProgressBar
                                                                            .stopCartoom();
                                                                    player = null;
                                                                    isplayingId = false;
                                                                    ((ImageView) view)
                                                                            .setImageDrawable(mActivity
                                                                                    .getResources()
                                                                                    .getDrawable(
                                                                                            R.drawable.play_green));
                                                                    return;
                                                                }
                                                                if (null == player) {
                                                                    player = new MediaPlayer();
                                                                }
                                                                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                                                String path = DownloadUtil.SDPATH
                                                                        + mMessageInfo._id;
                                                                try {
                                                                    player.setDataSource(path);
                                                                    player.prepare();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                                    @Override
                                                                    public void onCompletion(
                                                                            MediaPlayer mediaPlayer) {
                                                                        player.release();
                                                                        holder.mRoundProgressBar
                                                                                .stopCartoom();
                                                                        player = null;
                                                                        isplayingId = false;
                                                                        ((ImageView) view)
                                                                                .setImageDrawable(mActivity
                                                                                        .getResources()
                                                                                        .getDrawable(
                                                                                                R.drawable.play_green));
                                                                    }
                                                                });
                                                                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                                    @Override
                                                                    public void onPrepared(
                                                                            MediaPlayer mediaPlayer) {
                                                                        mediaPlayer.start();
                                                                        holder.mRoundProgressBar
                                                                                .startCartoom(
                                                                                        0,
                                                                                        mMessageInfo.audio.duration);
                                                                        holder.mRoundProgressBar
                                                                                .setVisibility(View.VISIBLE);
                                                                        ((ImageView) view)
                                                                                .setImageDrawable(mActivity
                                                                                        .getResources()
                                                                                        .getDrawable(
                                                                                                R.drawable.stop_green));
                                                                        isplayingId = true;
                                                                    }
                                                                });
                                                            }
                                                        });
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure() {
                                    }
                                });
                    }
                }).start();
            } else {
                holder.mProgressBar.setVisibility(View.GONE);
                holder.imageView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        if (null != player
                                && player.isPlaying()) {
                            player.stop();
                            player.release();
                            holder.mRoundProgressBar.stopCartoom();
                            player = null;
                            isplayingId = false;
                            ((ImageView) view)
                                    .setImageDrawable(mActivity
                                            .getResources()
                                            .getDrawable(
                                                    R.drawable.play_green));
                            return;
                        }
                        if (null == player) {
                            player = new MediaPlayer();
                        }
                        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        String path = mMessageInfo.audio.url;
                        try {
                            player.setDataSource(path);
                            player.prepare();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                player.release();
                                holder.mRoundProgressBar.stopCartoom();
                                player = null;
                                isplayingId = false;
                                ((ImageView) view).setImageDrawable(mActivity.getResources()
                                        .getDrawable(R.drawable.play_green));
                            }
                        });
                        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                mediaPlayer.start();
                                holder.mRoundProgressBar.startCartoom(0,
                                        mMessageInfo.audio.duration);
                                holder.mRoundProgressBar.setVisibility(View.VISIBLE);
                                ((ImageView) view).setImageDrawable(mActivity.getResources()
                                        .getDrawable(R.drawable.stop_green));
                                isplayingId = true;
                            }
                        });
                    }
                });
            }

            if (mMessageInfo.status == FusionCode.MessageStatusType.SEND_PROCESS) {
                doUpload(new File(mMessageInfo.audio.url));
            }
            holder.mTextView.setText(getVoiceSecondsStr());
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
                                                if (mMessageInfo.audio.url.contains("http")) {
                                                    mIChatLogic.sendToServer(mMessageInfo);
                                                } else {
                                                    doUpload(new File(mMessageInfo.audio.url));
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

    private String getVoiceSecondsStr() {
        int voiceLen = getVoiceSeconds();
        StringBuilder sb = new StringBuilder();
        sb.append(voiceLen);
        sb.append("s");
        int padding = 6 + voiceLen;
        for (int i = 0; i < padding; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    private int getVoiceSeconds() {
        return HelperFunc.roundToSecond(mMessageInfo.audio.duration);
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

                        MessageInfo mMessageInfo1 = new MessageInfo();
                        mMessageInfo1.db_id = mMessageInfo.db_id;
                        AudioDesc mAudioDesc = new AudioDesc();
                        mAudioDesc.url = photoUrl;
                        mAudioDesc.duration = mMessageInfo.audio.duration;
                        mMessageInfo1.audio = mAudioDesc;
                        mMessageInfo1.recipient = mMessageInfo.recipient;
                        mMessageInfo1.messageType = FusionCode.MessageType.AUDIO;
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
}
