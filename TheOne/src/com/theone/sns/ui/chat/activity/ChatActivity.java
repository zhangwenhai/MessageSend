package com.theone.sns.ui.chat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.common.TheOneConstants;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.component.location.poi.LocalPoiInfo;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.chat.IPushListener;
import com.theone.sns.logic.chat.impl.ChatManager;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.logic.model.chat.base.Position;
import com.theone.sns.logic.model.mblog.base.AudioDesc;
import com.theone.sns.logic.model.mblog.base.Photo;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.chat.ChatAdapter;
import com.theone.sns.ui.chat.ChatSelectFriendAdapter;
import com.theone.sns.ui.chat.EmojiPagerAdapter;
import com.theone.sns.util.DownloadUtil;
import com.theone.sns.util.FileStore;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.ImageLoaderUtil;
import com.theone.sns.util.PictureCallback;
import com.theone.sns.util.PictureHelper;
import com.theone.sns.util.PrettyDateFormat;
import com.theone.sns.util.uiwidget.ExpressionUtil;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;
import com.theone.sns.util.uiwidget.PlusTypeWidget;
import com.theone.sns.util.uiwidget.ResizeLayoutCallback;
import com.theone.sns.util.uiwidget.ResizeLinearLayout;
import com.theone.sns.util.uiwidget.VoiceViewControl;
import com.theone.sns.util.uiwidget.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/10/28.
 */
public class ChatActivity extends IphoneTitleActivity implements ResizeLayoutCallback,
        IPushListener, PictureCallback {

    private final static int VOLUME = 1;
    public static final int LOCATION_RESULT = 2;
    private static final int AT_SELECT_USER = 3;
    public static final int SHARE_TO_CHAT = 4;
    public static final int SEND_NAMECARD_TO_CHAT = 5;
    public static final int CREAD_GROUP = 6;
    private static final int SOFTKEYPAD_MIN_HEIGHT = HelperFunc.scale(50);
    private GroupInfo mGroupInfo;
    private IChatLogic mIChatLogic;
    private String getMessageFromDBId;
    private ListView msgListView;
    private List<MessageInfo> messageList = new ArrayList<MessageInfo>();
    private ChatAdapter mChatAdapter;
    private EditText textMsgEditText;
    private ImageView sendTextBtn;
    private View headView;
    private PictureHelper m_pictureHelper = null;
    private int m_keyboardHeight;
    private List<User> mAtList = new ArrayList<User>();

    private String messageTime = "";

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (FusionAction.ChatAction.AT_SOMEONE.equals(intent.getAction())) {
                if (null != textMsgEditText) {
                    if (null == intent.getSerializableExtra(FusionAction.ChatAction.AT_SOMEONE)) {
                        return;
                    }
                    User mUser = (User) intent.getSerializableExtra(FusionAction.ChatAction.AT_SOMEONE);
                    textMsgEditText.setText(textMsgEditText.getText().append("@" + mUser.name + " "));
                    textMsgEditText.setSelection(textMsgEditText.getText().length());
                    mAtList.add(mUser);
                }
            }
        }
    };

    private final int[] icons = new int[]{R.drawable.chat_send_picture, R.drawable.chat_camera,
            R.drawable.chat_send_location, R.drawable.chat_send_business_card1};

    private final int[] texts = new int[]{R.string.plus1, R.string.plus2, R.string.plus3,
            R.string.plus4};

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VOLUME: {
                    showVolume(msg.arg1);
                }
                default:
                    break;
            }

        }

        ;
    };
    private PlusTypeWidget.MenuCallback mMenuCallback = new PlusTypeWidget.MenuCallback() {
        @Override
        public void onMenuItemClick(int callbackId) {
            switch (callbackId) {
                case PlusTypeWidget.CHOOSE_TYPE_PHOTO: {
                    m_pictureHelper.getPictureFromGallery(PictureHelper.PHOTO_PICKED_FROM_GALLERY);
                    break;
                }
                case PlusTypeWidget.CHOOSE_TYPE_CAMERA: {
                    startActivityForResult(new Intent(FusionAction.PublicAction.PHOTO_ACTION),
                            PictureHelper.PHOTO_RESULT);
                    break;
                }
                case PlusTypeWidget.CHOOSE_TYPE_LOCATION: {
                    startActivityForResult(new Intent(FusionAction.ChatAction.SELECT_LOCATION_ACTION),
                            LOCATION_RESULT);
                    break;
                }
                case PlusTypeWidget.CHOOSE_TYPE_NAMECARD: {
                    startActivity(new Intent(FusionAction.ChatAction.SELECT_FRIEND_ACTION).putExtra(
                            FusionAction.ChatAction.GROUP_INFO, mGroupInfo).putExtra(
                            FusionAction.ChatAction.SELECT_TYPE_1,
                            ChatSelectFriendAdapter.SEND_NAMECARD));
                    break;
                }
                default:
            }
            if (null != m_plusWidget) {
                m_plusWidget.hide();
            }
        }
    };
    private PlusTypeWidget m_plusWidget;
    private ImageView plusBtn;
    private int with;
    private int height;
    private LinearLayout emojiPagerView;
    private ViewPager m_pager;
    private CirclePageIndicator m_titleIndicator;
    private EmojiPagerAdapter m_adapter;
    private boolean m_isIMEOn;
    private ImageView sendVoiceBtn;
    private boolean m_inVoiceContainerMode;
    private View m_voiceBtnContainer;
    private View m_plusBtnContainer;
    private VolumeThread mVolumeThread = new VolumeThread(200);
    private VoiceViewControl m_voiceViewControl;
    private long currentTime;
    private View.OnTouchListener m_recordLister = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    if (!FileStore.isSDCardAvailable()) {
                        return false;
                    }
                    if (null == m_voiceViewControl) {
                        m_voiceViewControl = new VoiceViewControl();
                    }
                    m_voiceViewControl.setRecordBtnContainer(m_voiceBtnContainer);
                    m_voiceViewControl.showRecordTip(m_voiceBtnContainer.getLayoutParams().height);
                    m_voiceViewControl.dealDownEvent(m_voiceBtnContainer.getLayoutParams().height, v,
                            event);
                    //
                    currentTime = System.currentTimeMillis();
                    startRecord();
                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                    popupWindow.update();
                    return true;
                }

                case MotionEvent.ACTION_MOVE: {
                    m_voiceViewControl.dealMoveEvent(event);
                    break;
                }

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    stopRecord();
                    popupWindow.dismiss();
                    m_voiceViewControl.hideRecordTip();
                    if (m_voiceViewControl.dealUpEvent(event)
                            || event.getAction() == MotionEvent.ACTION_CANCEL) {

                    } else {
                        MessageInfo mMessageInfo = new MessageInfo();
                        mMessageInfo.messageType = FusionCode.MessageType.AUDIO;
                        AudioDesc mAudioDesc = new AudioDesc();
                        mAudioDesc.url = FileName;
                        mAudioDesc.duration = (int) recorderTime;
                        mMessageInfo.audio = mAudioDesc;
                        mMessageInfo.recipient = mGroupInfo._id;
                        User mUser = new User();
                        mUser.userId = FusionConfig.getInstance().getUserId();
                        mMessageInfo.owner = mUser;
                        mIChatLogic.sendInLocal(mMessageInfo);
                    }
                    break;
                }

                default:
                    break;
            }

            return false;
        }
    };
    private View popView;
    private ImageView volumeImageView;
    private PopupWindow popupWindow;
    private MediaRecorder recorder;
    private long recorderTime;
    private String FileName;
    private PlusAdapter mPlusAdapter;
    private GridView chatPlusGridview;
    private String messageId;
    private String lastId;

    @Override
    protected void initLogics() {
        mIChatLogic = (IChatLogic) getLogicByInterfaceClass(IChatLogic.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ResizeLinearLayout subContentView = (ResizeLinearLayout) setSubContent(R.layout.chat_main);
        subContentView.setCallback(this);

        getView();

        setView();

        setListener();

        ChatManager.getInstance().addListener(this);

        getMessageFromDBId = mIChatLogic.getMessageFromDB(mGroupInfo._id, messageTime);

        IntentFilter filter = new IntentFilter();
        filter.addAction(FusionAction.ChatAction.AT_SOMEONE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    }

    private void getView() {
        mGroupInfo = (GroupInfo) getIntent().getSerializableExtra(
                FusionAction.ChatAction.GROUP_INFO);
        if (null == mGroupInfo) {
            finish();
        }
        messageId = getIntent().getStringExtra(FusionAction.ChatAction.MESSGAE_ID);

        msgListView = (ListView) findViewById(R.id.msgListView);
        headView = LayoutInflater.from(this).inflate(R.layout.chat_head, null);
        textMsgEditText = (EditText) findViewById(R.id.textMsgEditText);
        plusBtn = (ImageView) findViewById(R.id.plus_btn);
        sendTextBtn = (ImageView) findViewById(R.id.sendTextBtn);
        sendVoiceBtn = (ImageView) findViewById(R.id.sendVoiceBtn);
        emojiPagerView = (LinearLayout) findViewById(R.id.emoji_pager_view);
        m_pager = (ViewPager) findViewById(R.id.emoji_pager);
        m_titleIndicator = (CirclePageIndicator) findViewById(R.id.emoji_indicator);
        m_voiceBtnContainer = findViewById(R.id.voicebtn_container);
        m_plusBtnContainer = findViewById(R.id.plusbtn_container);
        chatPlusGridview = (GridView) findViewById(R.id.chat_plus_gridview);

        with = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;
    }

    private void setView() {

        setTitle(getTitleName());
        setLeftButton(R.drawable.icon_back, false, false);
        setRightButton(R.string.setting_title, true);

        initPopupWindow();

        m_adapter = new EmojiPagerAdapter(getApplicationContext(),
                new EmojiPagerAdapter.EmojiOnClickCallback() {
                    @Override
                    public void OnClick(int i) {
                        CharSequence mCharSequence = ExpressionUtil.getInstance().smileyToStr(
                                ExpressionUtil.DEFAULT_SMILEY_RES_IDS[i]);
                        CharSequence ret = textMsgEditText.getText().toString() + mCharSequence;
                        textMsgEditText.setText(ExpressionUtil.getInstance().strToSmiley(ret));
                        textMsgEditText.setSelection(textMsgEditText.getText().toString().length());
                    }
                }, options);
        m_pager.setAdapter(m_adapter);
        m_pager.setPageMargin((int) HelperFunc.dip2px(10));
        m_pager.setVerticalFadingEdgeEnabled(false);
        m_pager.setVerticalScrollBarEnabled(false);
        m_titleIndicator.setPageColor(0xff888888);
        m_titleIndicator.setFillColor(0xff646464);
        m_titleIndicator.setViewPager(m_pager);
        m_titleIndicator.setCurrentItem(0);

        m_pictureHelper = new PictureHelper(this, this);
        msgListView.addHeaderView(headView);
        mChatAdapter = new ChatAdapter(this, optionsForChatImage, mIChatLogic, optionsForUserIcon);
        msgListView.setAdapter(mChatAdapter);
        textMsgEditText.findFocus();

        mPlusAdapter = new PlusAdapter();
        chatPlusGridview.setAdapter(mPlusAdapter);
    }

    private void setListener() {
        getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.chat_voice_btn).setOnTouchListener(m_recordLister);
        getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FusionAction.ChatAction.CHATINFO_ACTION).putExtra(
                        FusionAction.ChatAction.GROUP_INFO, mGroupInfo));
            }
        });

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // showPlusWidget();
                if (View.VISIBLE == m_plusBtnContainer.getVisibility()) {
                    hidePlusContainer(false);
                } else {
                    showPlusContainer();
                }
            }
        });

        sendTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(textMsgEditText.getText().toString())) {
                    MessageInfo mMessageInfo = new MessageInfo();
                    mMessageInfo.messageType = FusionCode.MessageType.TEXT;
                    mMessageInfo.text = textMsgEditText.getText().toString();

                    List<User> mList = new ArrayList<User>();
                    for (User mUser : mAtList) {
                        if (mMessageInfo.text.contains("@" + mUser.name)) {
                            mList.add(mUser);
                        }
                    }
                    mMessageInfo.mention = mList;

                    mMessageInfo.recipient = mGroupInfo._id;
                    mIChatLogic.sendMessage(mMessageInfo);
                    textMsgEditText.setText("");
                    mAtList.clear();
                }
            }
        });

        findViewById(R.id.showEmojiBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textMsgEditText.findFocus();
                if (emojiPagerView.isShown()) {
                    emojiPagerView.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive(textMsgEditText)) {
                        if (getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
                            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                } else {
                    emojiPagerView.setVisibility(View.VISIBLE);

                    m_voiceBtnContainer.setVisibility(View.GONE);
                    m_plusBtnContainer.setVisibility(View.GONE);
                    m_inVoiceContainerMode = false;

                    // 隐藏输入法界面
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        sendVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m_inVoiceContainerMode) {
                    hideVoiceContainer(true);
                } else {
                    showVoiceContainer();
                }
            }
        });

        textMsgEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = textMsgEditText.getText().toString();
                if (text.length() > 0) {
                    sendTextBtn.setVisibility(View.VISIBLE);
                    sendVoiceBtn.setVisibility(View.GONE);
                    if (text.endsWith("@") && null != mGroupInfo.members && mGroupInfo.members.size() > 2) {
                        startActivityForResult(new Intent(FusionAction.ChatAction.ATLIST_ACTION).putExtra(FusionAction.ChatAction.GROUP_INFO, mGroupInfo), AT_SELECT_USER);
                    }
                } else {
                    sendTextBtn.setVisibility(View.GONE);
                    sendVoiceBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        msgListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideIME();
                hideVoiceContainer(false);
                if (null != m_plusWidget) {
                    m_plusWidget.hide();
                }
                emojiPagerView.setVisibility(View.GONE);
                return false;
            }
        });

        msgListView.setRecyclerListener(new AbsListView.RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {
                if (null != view) {
                    View content = view.findViewById(R.id.chatPicContent);
                    if (null != content && content instanceof ImageView) {
                        ((ImageView) content).setImageBitmap(null);
                    }
                }
            }
        });

        msgListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                switch (i) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到顶部
                        if (msgListView.getFirstVisiblePosition() == 0) {
                            if (messageList.size() > 0) {
                                for (MessageInfo mMessageInfo : messageList) {
                                    if (!TextUtils.isEmpty(mMessageInfo.created_at)) {
                                        messageTime = mMessageInfo.created_at;
                                        lastId = mMessageInfo._id;
                                        break;
                                    }
                                }
                                getMessageFromDBId = mIChatLogic.getMessageFromDB(mGroupInfo._id, messageTime);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            }
        });

        textMsgEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
                msgListView.setSelection(messageList.size());
            }
        });
    }

    private void initPopupWindow() {
        popView = LayoutInflater.from(ChatActivity.this).inflate(R.layout.volume_pop, null);
        volumeImageView = (ImageView) popView.findViewById(R.id.volume);
        popView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != popupWindow)
                    popupWindow.dismiss();
            }
        });
        popupWindow = new PopupWindow(popView, LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setHeight(getResources().getDisplayMetrics().heightPixels);
        popupWindow.setWidth(getResources().getDisplayMetrics().widthPixels);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
    }

    private void startRecord() {
        FileName = DownloadUtil.SDPATH + System.currentTimeMillis();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        recorder.setOutputFile(FileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        recorder.getMaxAmplitude();
        mVolumeThread.startVolume();
        new Thread(mVolumeThread).start();
    }

    private void stopRecord() {
        try {
            recorder.stop();// 停止刻录
            recorder.release(); // 刻录完成一定要释放资源
            recorder = null;
            mVolumeThread.stopVolume();
            recorderTime = System.currentTimeMillis() - currentTime - 500;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showVoiceContainer() {
        emojiPagerView.setVisibility(View.GONE);
        hideIME();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        m_plusBtnContainer.setVisibility(View.GONE);

        if (m_keyboardHeight > HelperFunc.scale(213)) {
            m_voiceBtnContainer.getLayoutParams().height = m_keyboardHeight;
        }

        m_voiceBtnContainer.setVisibility(View.VISIBLE);
        m_inVoiceContainerMode = true;

        sendVoiceBtn.setImageResource(R.drawable.btn_keyboard_selector);
    }

    private void showPlusContainer() {
        emojiPagerView.setVisibility(View.GONE);
        hideIME();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        m_voiceBtnContainer.setVisibility(View.GONE);

        if (m_keyboardHeight > HelperFunc.scale(213)) {
            m_plusBtnContainer.getLayoutParams().height = m_keyboardHeight;
        }

        m_plusBtnContainer.setVisibility(View.VISIBLE);
    }

    private void hideVoiceContainer(boolean showIME) {
        if (showIME) {
            showIME();
        }

        postDelayed(new Runnable() {
            @Override
            public void run() {
                m_voiceBtnContainer.setVisibility(View.GONE);
                m_plusBtnContainer.setVisibility(View.GONE);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                m_inVoiceContainerMode = false;
                sendVoiceBtn.setImageResource(R.drawable.btn_mic_selector);
            }
        }, 150);
    }

    private void hidePlusContainer(boolean showIME) {
        if (showIME) {
            showIME();
        }

        postDelayed(new Runnable() {
            @Override
            public void run() {
                m_plusBtnContainer.setVisibility(View.GONE);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
        }, 150);
    }

    protected void showIME() {
        showIME(textMsgEditText);
    }

    protected void hideIME() {
        hideIME(textMsgEditText, false);
    }

    private void showPlusWidget() {
        if (null == m_plusWidget) {
            m_plusWidget = new PlusTypeWidget(this, false, mMenuCallback);
        }

        if (null != m_plusWidget) {
            m_plusWidget.show(m_keyboardHeight);
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (Math.abs(oldh - h) < SOFTKEYPAD_MIN_HEIGHT) {
            return;
        }

        if ((oldh > 0) && (h > 0) && (oldh > h)) {
            m_keyboardHeight = oldh - h;
        }

        m_isIMEOn = (oldh - h > SOFTKEYPAD_MIN_HEIGHT);
        if (m_isIMEOn) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    emojiPagerView.setVisibility(View.GONE);
                    getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }, 150);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideIME();
    }

    @Override
    public void push(int what, Object object) {
        switch (what) {
            case MESSAGE_ADD:
            case MESSAGE_UPDATE: {
                MessageInfo mMessageInfo = (MessageInfo) object;
                if (null != mMessageInfo && !TextUtils.isEmpty(mMessageInfo.recipient)
                        && mMessageInfo.recipient.equals((String) mGroupInfo._id)) {
                    getMessageFromDBId = mIChatLogic.getMessageFromDB(mGroupInfo._id, messageTime);
                }
                break;
            }
            case GROUP_CHANGE: {
                if (null == object) {
                    return;
                }
                GroupInfo mGroupInfo1 = (GroupInfo) object;
                if (mGroupInfo1._id.equals((String) mGroupInfo._id)) {
                    mGroupInfo = mIChatLogic.getGroupInfoFromDB(mGroupInfo1._id);
                }
                break;
            }
            case MESSAGE_DELETE: {
                if (null == object) {
                    return;
                }
                String GroupInfoId = (String) object;
                if (GroupInfoId.equals((String) mGroupInfo._id)) {
                    getMessageFromDBId = mIChatLogic.getMessageFromDB(mGroupInfo._id, messageTime);
                }
                break;
            }

            default:
        }
    }

    @Override
    protected void handleStateMessage(Message msg) {
        switch (msg.what) {
            case FusionMessageType.ChatMessageType.GET_MESSAGE_LIST_FROM_DB: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(getMessageFromDBId)
                        && getMessageFromDBId.equals(object.mLocalRequestId))
                    if (null != object.mObject) {
                        messageList = sortMessages((List<MessageInfo>) object.mObject);
                        mChatAdapter.setMessageList(messageList);
                        if (TextUtils.isEmpty(messageId)) {
                            if (!TextUtils.isEmpty(lastId)) {
                                int m = 0;
                                int n = -1;
                                for (MessageInfo mMessageInfo : messageList) {
                                    if (!TextUtils.isEmpty(mMessageInfo._id) && mMessageInfo._id.equals(lastId)) {
                                        n = m;
                                        break;
                                    }
                                    m++;
                                }
                                msgListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
                                final int finalN = n;
                                if (finalN != 0) {
                                    mChatAdapter.notifyDataSetChanged();
                                    msgListView.setSelection(finalN + msgListView.getHeaderViewsCount());
                                }
                                lastId = "";
                            } else {
                                msgListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                                mChatAdapter.notifyDataSetChanged();
                            }
                        } else {
                            mChatAdapter.notifyDataSetChanged();
                            int i = 0;
                            for (MessageInfo mMessageInfo : messageList) {
                                if (messageId.equals(mMessageInfo._id)) {
                                    break;
                                }
                                i++;
                            }
                            final int j = i;
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    msgListView.setSelection(j);
                                }
                            });
                        }
                    }
                break;
            }
            default:
        }
    }

    private List<MessageInfo> sortMessages(List<MessageInfo> messageList) {
        String prevCreateTime = "";
        List<MessageInfo> messageInfoList = new ArrayList<MessageInfo>();

        for (MessageInfo mMessageInfo : messageList) {
            if (PrettyDateFormat.isShowTimeForChat(prevCreateTime, mMessageInfo.created_at)) {
                MessageInfo mMessageInfo1 = new MessageInfo();
                mMessageInfo1.text = PrettyDateFormat.formatISO8601TimeForChat(mMessageInfo.created_at);
                messageInfoList.add(mMessageInfo1);
            }
            messageInfoList.add(mMessageInfo);
            prevCreateTime = mMessageInfo.created_at;
        }
        return messageInfoList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (m_pictureHelper == null)
            return;
        m_pictureHelper.processActivityResult(requestCode, resultCode, data);

        if (LOCATION_RESULT == requestCode && resultCode == RESULT_OK) {
            if (null == data
                    || null == data.getSerializableExtra(FusionAction.ChatAction.LOCATION_INFO)) {
                return;
            }
            LocalPoiInfo localPoiInfo = (LocalPoiInfo) data
                    .getSerializableExtra(FusionAction.ChatAction.LOCATION_INFO);
            MessageInfo mMessageInfo = new MessageInfo();
            mMessageInfo.messageType = FusionCode.MessageType.POSITION;

            Position mposition = new Position();
            mposition.name = localPoiInfo.name;
            mposition.address = localPoiInfo.address;
            List<String> mlist = new ArrayList<String>();
            mlist.add(localPoiInfo.longitude);
            mlist.add(localPoiInfo.latitude);
            mposition.location = mlist;
            mMessageInfo.position = mposition;

            User mUser = new User();
            mUser.userId = FusionConfig.getInstance().getUserId();
            mMessageInfo.owner = mUser;

            mMessageInfo.recipient = mGroupInfo._id;

            mIChatLogic.sendMessage(mMessageInfo);
        }

        if (AT_SELECT_USER == requestCode && resultCode == RESULT_OK) {
            if (null == data
                    || null == data.getSerializableExtra(FusionAction.ChatAction.USER_INFO)) {
                return;
            }
            User mUser = (User) data.getSerializableExtra(FusionAction.ChatAction.USER_INFO);
            textMsgEditText.setText(textMsgEditText.getText().append(mUser.name + " "));
            textMsgEditText.setSelection(textMsgEditText.getText().length());
            mAtList.add(mUser);
            showIME();
            hideVoiceContainer(false);
            if (null != m_plusWidget) {
                m_plusWidget.hide();
            }
            emojiPagerView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackKey();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBackKey() {
        if (m_inVoiceContainerMode) {
            hideVoiceContainer(false);
        } else if (View.VISIBLE == emojiPagerView.getVisibility()) {
            emojiPagerView.setVisibility(View.GONE);
        } else if (m_plusBtnContainer.getVisibility() == View.VISIBLE) {
            hidePlusContainer(false);
        } else {
            finish();
        }
    }

    @Override
    public void setPicture(File f) {
        if (null == f) {
            return;
        }
        MessageInfo mMessageInfo = new MessageInfo();
        mMessageInfo.messageType = FusionCode.MessageType.PHOTO;
        Photo mPhoto = new Photo();
        mPhoto.url = f.getAbsolutePath();
        mMessageInfo.photo = mPhoto;
        mMessageInfo.recipient = mGroupInfo._id;
        User mUser = new User();
        mUser.userId = FusionConfig.getInstance().getUserId();
        mMessageInfo.owner = mUser;
        mIChatLogic.sendInLocal(mMessageInfo);
    }

    @Override
    public void setPicture(File f, int angle, int camera) {
        int i = HelperFunc.readPictureDegree(f.getPath());
        Bitmap bitmap1 = null;
        if (i == 90 || i == 270) {
            bitmap1 = ImageLoaderUtil.getBitmapByWidth(f.getPath(), height, 0, with);
        } else {
            bitmap1 = ImageLoaderUtil.getBitmapByWidth(f.getPath(), with, 0, height);
        }
        int widt;
        int difference;
        Bitmap resizedBitmap = null;
        if (bitmap1.getWidth() >= bitmap1.getHeight()) {
            widt = bitmap1.getHeight();
            difference = bitmap1.getWidth() - bitmap1.getHeight();
        } else {
            widt = bitmap1.getWidth();
            difference = bitmap1.getHeight() - bitmap1.getWidth();
        }
        Matrix matrix = new Matrix();
        if (camera == 0) {
            matrix.postRotate(i + angle);
            resizedBitmap = Bitmap.createBitmap(bitmap1, 0, 0, widt, widt, matrix, true);
        } else if (camera == 1) {
            matrix.postRotate(i - angle);
            matrix.postScale(-1, 1);
            resizedBitmap = Bitmap.createBitmap(bitmap1, difference, 0, widt, widt, matrix, true);
        }
        ImageLoaderUtil.saveBitmap(resizedBitmap, f.getPath() + 1,
                TheOneConstants.CHAT_PICTURE_QUALITY);

        MessageInfo mMessageInfo = new MessageInfo();
        mMessageInfo.messageType = FusionCode.MessageType.PHOTO;
        Photo mPhoto = new Photo();
        mPhoto.url = f.getAbsolutePath() + 1;
        mMessageInfo.photo = mPhoto;
        mMessageInfo.recipient = mGroupInfo._id;
        User mUser = new User();
        mUser.userId = FusionConfig.getInstance().getUserId();
        mMessageInfo.owner = mUser;
        mIChatLogic.sendInLocal(mMessageInfo);
    }

    @Override
    public boolean saveToSDCard() {
        return false;
    }

    @Override
    public Integer[] needCropImage(File f) {
        return new Integer[]{TheOneConstants.USER_AVATAR_MAX_SIZE,
                TheOneConstants.USER_AVATAR_MAX_SIZE};
    }

    @Override
    public void onDeletePicture() {

    }

    @Override
    public void onPreviewPicture() {

    }

    @Override
    public void setOriginalPicture(File f, String originalPath) {

    }

    private String getTitleName() {

        String name = mGroupInfo.name;

        if (TextUtils.isEmpty(name)) {

            if (mGroupInfo.members.size() == 2) {

                String meUserId = FusionConfig.getInstance().getUserId();

                for (User user : mGroupInfo.members) {

                    if (!meUserId.equals(user.userId)) {

                        name = user.name;

                        break;
                    }
                }

            } else {

                name = String.format(getString(R.string.group_name_in_chat),
                        mGroupInfo.members.size());
            }
        }

        return name;
    }

    private class VolumeThread implements Runnable {
        int milliseconds = 200;
        private boolean isRun = true;

        public VolumeThread(int i) {
            milliseconds = i;
        }

        @Override
        public void run() {
            int vuSize = 0;
            while (isRun) {
                try {
                    Thread.sleep(milliseconds);

                    if (null != recorder) {
                        vuSize = 10 * recorder.getMaxAmplitude() / 32768;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = VOLUME;
                msg.arg1 = vuSize;
                mHandler.sendMessage(msg);
            }
        }

        public void stopVolume() {
            isRun = false;
        }

        public void startVolume() {
            isRun = true;
        }
    }

    private void showVolume(int arg1) {
        switch (arg1) {
            case 0: {
                volumeImageView.setImageDrawable(getResources().getDrawable(R.drawable.publish_audio));
                break;
            }
            case 1: {
                volumeImageView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.publish_audio_01));
                break;
            }
            case 2: {
                volumeImageView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.publish_audio_02));
                break;
            }
            case 3: {
                volumeImageView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.publish_audio_03));
                break;
            }
            case 4: {
                volumeImageView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.publish_audio_04));
                break;
            }
            case 5: {
                volumeImageView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.publish_audio_05));
                break;
            }
            case 6: {
                volumeImageView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.publish_audio_06));
                break;
            }
            case 7: {
                volumeImageView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.publish_audio_07));
                break;
            }
            case 8: {
                volumeImageView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.publish_audio_08));
                break;
            }
            case 9: {
                volumeImageView.setImageDrawable(getResources().getDrawable(R.drawable.publish_audio));
                break;
            }
            default:
        }
    }

    @Override
    protected void onDestroy() {
    	ImageLoader.getInstance().clearMemoryCache();
        mIChatLogic.updateMessageIsRead(mGroupInfo._id);
        HelperFunc.startTabHostPage(ChatActivity.this, 3);
        ChatManager.getInstance().removeListener(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    private class PlusAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        @Override
        public int getCount() {
            return icons.length;
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final ImageLoaderViewHolder holder;
            if (view == null) {
                if (null == inflater) {
                    inflater = LayoutInflater.from(ChatActivity.this);
                }
                view = inflater.inflate(R.layout.menu_grid_item, null);
                holder = new ImageLoaderViewHolder();
                holder.imageView = (ImageView) view.findViewById(R.id.grid_menu_icon);
                holder.mTextView = (TextView) view.findViewById(R.id.grid_menu_text);
                view.setTag(holder);
            } else {
                holder = (ImageLoaderViewHolder) view.getTag();
            }

            holder.imageView.setImageDrawable(getResources().getDrawable(icons[i]));
            holder.mTextView.setText(getString(texts[i]));

            final int j = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMenuCallback.onMenuItemClick(j);
                }
            });

            return view;
        }
    }
}
