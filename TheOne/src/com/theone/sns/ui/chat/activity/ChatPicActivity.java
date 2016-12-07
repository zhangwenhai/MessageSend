package com.theone.sns.ui.chat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.util.FileUtil;
import com.theone.sns.util.HackyViewPager;
import com.theone.sns.util.ImageLoaderUtil;
import com.theone.sns.util.RoundProgressBar;
import com.theone.sns.util.photoview.PhotoView;
import com.theone.sns.util.uiwidget.IphoneStyleAlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/11/12.
 */
public class ChatPicActivity extends IphoneTitleActivity {
    private final static int SAVE_PHOTO = 0;
    private static final int IPHONE_CANCEL = 1;
    private String url;
    private HackyViewPager hackyviewpager;
    private SamplePagerAdapter m_sAdapter;
    private IChatLogic mIChatLogic;
    private MessageInfo mMessageInfo;
    private String getPhotoMessageFromDBId;
    private List<MessageInfo> messageList = new ArrayList<MessageInfo>();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private int select;
    private IphoneStyleAlertDialogBuilder m_iphoneDialog;
    private static final String SAVE_PATH = Environment.getExternalStorageDirectory().getPath() + "/TheOnePhoto/";

    @Override
    protected void initLogics() {
        mIChatLogic = (IChatLogic) getLogicByInterfaceClass(IChatLogic.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSubContent(R.layout.chat_pic_main);

        getView();

        setView();

        setListener();
    }

    private void getView() {
        url = getIntent().getStringExtra(FusionAction.ChatAction.CHATPIC_URL);
        mMessageInfo = (MessageInfo) getIntent().getSerializableExtra(FusionAction.ChatAction.MESSGAE_INFO);
        if (TextUtils.isEmpty(url) || null == mMessageInfo) {
            finish();
        }
        hackyviewpager = (HackyViewPager) findViewById(R.id.hackyviewpager);
    }

    private void setView() {
        setLeftButton(R.drawable.icon_back, false, false);
        setTitle(R.string.chat_pic);
        setRightButton(R.drawable.navigation_more_icon, false);

        getPhotoMessageFromDBId = mIChatLogic.getPhotoMessageFromDB(mMessageInfo.recipient);
    }

    private void setListener() {
        hackyviewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                select = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showIphoneDialog();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!new File(SAVE_PATH).exists()) {
            new File(SAVE_PATH).mkdirs();
        }
    }

    @Override
    protected void handleStateMessage(Message msg) {
        switch (msg.what) {
            case FusionMessageType.ChatMessageType.GET_PHOTO_MESSAGE_LIST_FROM_DB: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(getPhotoMessageFromDBId)
                        && getPhotoMessageFromDBId.equals(object.mLocalRequestId))
                    if (null != object.mObject) {
                        messageList = (List<MessageInfo>) object.mObject;
                        int j = 0;
                        for (int i = 0; i < messageList.size(); i++) {
                            if (null == messageList.get(i) || null == messageList.get(i).photo || TextUtils.isEmpty(messageList.get(i).photo.url)) {
                                continue;
                            }
                            if (url.equals(messageList.get(i).photo.url)) {
                                j = i;
                                break;
                            }
                        }
                        if (null == m_sAdapter) {
                            m_sAdapter = new SamplePagerAdapter(messageList);
                            hackyviewpager.setAdapter(m_sAdapter);
                        } else {
                            m_sAdapter.setmList(messageList);
                            m_sAdapter.notifyDataSetChanged();
                        }
                        hackyviewpager.setCurrentItem(j);
                        select = j;
                    }
                break;
            }
            default:
        }
    }

    private void showIphoneDialog() {
        if (m_iphoneDialog == null) {
            m_iphoneDialog = new IphoneStyleAlertDialogBuilder(this);
            m_iphoneDialog.addItem(SAVE_PHOTO,
                    getString(R.string.save_photo4), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            m_iphoneDialog.dismiss();
                            if (null != messageList.get(select) && null != messageList.get(select).photo && !TextUtils.isEmpty(messageList.get(select).photo.url)) {
                                String fileName = "";
                                if (messageList.get(select).photo.url.startsWith("http")) {
                                    File f = ImageLoader.getInstance().getDiskCache().get(messageList.get(select).photo.url);
                                    if (null != f && f.exists()) {
                                        fileName = SAVE_PATH + System.currentTimeMillis() + ".jpg";
                                        saveImage(f.getAbsolutePath(), fileName);
                                        scanFileAsync(getApplicationContext(), fileName);
                                    } else {
                                        showToast(R.string.save_photo1);
                                        return;
                                    }
                                } else {
                                    fileName = SAVE_PATH + System.currentTimeMillis() + ".jpg";
                                    saveImage(messageList.get(select).photo.url, fileName);
                                    scanFileAsync(getApplicationContext(), fileName);
                                }
                                showToast(getResources().getString(R.string.save_photo2) + fileName);
                            } else {
                                showToast(R.string.save_photo3);
                            }
                        }
                    });
            m_iphoneDialog.addItem(IPHONE_CANCEL, getString(R.string.Cancel),
                    IphoneStyleAlertDialogBuilder.COLOR_BLUE,
                    IphoneStyleAlertDialogBuilder.TEXT_TYPE_BOLD,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            m_iphoneDialog.dismiss();
                        }
                    });
        }
        m_iphoneDialog.show();
    }

    private void scanFileAsync(Context ctx, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        ctx.sendBroadcast(scanIntent);
    }

    class SamplePagerAdapter extends PagerAdapter {
        private List<MessageInfo> mList = new ArrayList<MessageInfo>();
        private LayoutInflater inflater;

        public SamplePagerAdapter(List<MessageInfo> mList) {
            this.mList = mList;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @SuppressLint("NewApi")
        @Override
        public View instantiateItem(ViewGroup container, int position) {
            if (null == inflater) {
                inflater = LayoutInflater.from(ChatPicActivity.this);
            }
            View view = inflater.inflate(R.layout.chat_pic_item, null);
            PhotoView photoView = (PhotoView) view.findViewById(R.id.photo_view);
            final RoundProgressBar progressBar = (RoundProgressBar) view.findViewById(R.id.progressBar);

            if (null == mList.get(position) || null == mList.get(position).photo || TextUtils.isEmpty(mList.get(position).photo.url)) {
                return view;
            }
            if (mList.get(position).photo.url.startsWith("http")) {
                ImageLoader.getInstance().displayImage(mList.get(position).photo.url, photoView, optionsForChatImage, animateFirstListener, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String s, View view, final int i, final int i2) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (null == progressBar) {
                                    return;
                                }
                                if (i == i2) {
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    progressBar.setMax(i2);
                                    progressBar.setProgress(i);
                                    progressBar.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                });
            } else {
                photoView.setImageBitmap(ImageLoaderUtil.getBigBitmapByWidth(mList.get(position).photo.url, getResources()
                        .getDisplayMetrics().widthPixels, 0));
            }
            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            container.setBackgroundColor(Color.parseColor("#ffffff"));
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void setmList(List<MessageInfo> mList) {
            this.mList = mList;
        }
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

    private void saveImage(String f1, String f2) {
        FileUtil.fileCopy(f1, f2);
    }
}
