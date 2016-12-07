package com.theone.sns.ui.me.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionAction.MeAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.common.FusionMessageType.MBlogMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.account.IAccountLogic;
import com.theone.sns.logic.adapter.db.SettingDbAdapter;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.chat.IPushListener;
import com.theone.sns.logic.chat.impl.ChatManager;
import com.theone.sns.logic.mblog.IMBlogLogic;
import com.theone.sns.logic.model.mblog.MBlog;
import com.theone.sns.logic.model.user.Mentioned;
import com.theone.sns.logic.model.user.TagThumbnail;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.model.user.base.Gallery;
import com.theone.sns.logic.user.IUserLogic;
import com.theone.sns.ui.base.IphoneTitleFragment;
import com.theone.sns.ui.me.CircleByAdapterTab3;
import com.theone.sns.ui.me.MBlogListAdapterTab2;
import com.theone.sns.ui.me.PicGridAdapterTab1;
import com.theone.sns.ui.me.TagImageAdapterTab4;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshBase;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class MeFragment extends IphoneTitleFragment implements IPushListener {

    private static final int MESETTINGREQUESTCODE = 1;

    private PullToRefreshListView mPullToRefreshListView;
    private ListView listView;
    private View meHeard;
    private LinearLayout topbarButton1;
    private LinearLayout topbarButton2;
    private LinearLayout topbarButton3;
    private LinearLayout topbarButton4;
    private TextView ageTextView;
    private TextView xingzuoTextView;
    private TextView heightTextView;
    private ImageView avatarImageView;
    private TextView nameTextView;
    private ImageView roleImageView;
    private ImageView xinghunImageView;
    private IUserLogic mIUserLogic;
    private IAccountLogic mIAccountLogic;
    private String requestId;
    private User mUser;
    private TextView followingTextView;
    private TextView followedByTextView;
    private TextView mediaTextView;
    private TextView likedByTextView;
    private String requestIdTab1;
    private String requestIdTab2;
    private String requestIdTab3;
    private String requestIdTab4;
    private List<Gallery> mGalleryListTab1 = new ArrayList<Gallery>();
    private List<MBlog> mBlogListTab2 = new ArrayList<MBlog>();
    private List<Mentioned> mMentionedListTab3 = new ArrayList<Mentioned>();
    private List<TagThumbnail> mTagThumbnailListTab4 = new ArrayList<TagThumbnail>();
    private PicGridAdapterTab1 mAdapterTab1;
    private MBlogListAdapterTab2 mAdapterTab2;
    private CircleByAdapterTab3 mAdapterTab3;
    private TagImageAdapterTab4 mAdapterTab4;
    private int confirmTab = 1;
    private int with;
    private LinearLayout meSettingLinearLayout;
    private ImageView followedByBadge;
    private ImageView likedByBadge;
    private IChatLogic mIChatLogic;
    private IMBlogLogic mIMBlogLogic;

    @Override
    protected void onMyCreateView() {
        setSubContent(R.layout.me_main);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!isNew) {
            return;
        }

        getView(view);

        setView();
    }

    private void getView(View view) {
        with = getResources().getDisplayMetrics().widthPixels;

        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.list);
        listView = mPullToRefreshListView.getRefreshableView();

        meHeard = inflater.inflate(R.layout.me_heard, null);

        meSettingLinearLayout = (LinearLayout) meHeard.findViewById(R.id.me_setting);
        avatarImageView = (ImageView) meHeard.findViewById(R.id.avatar);
        nameTextView = (TextView) meHeard.findViewById(R.id.name);
        roleImageView = (ImageView) meHeard.findViewById(R.id.role);
        xinghunImageView = (ImageView) meHeard.findViewById(R.id.xinghun);
        ageTextView = (TextView) meHeard.findViewById(R.id.age);
        xingzuoTextView = (TextView) meHeard.findViewById(R.id.xingzuo);
        heightTextView = (TextView) meHeard.findViewById(R.id.height);

        followingTextView = (TextView) meHeard.findViewById(R.id.following);
        followedByTextView = (TextView) meHeard.findViewById(R.id.followed_by);
        mediaTextView = (TextView) meHeard.findViewById(R.id.media);
        likedByTextView = (TextView) meHeard.findViewById(R.id.liked_by);

        topbarButton1 = (LinearLayout) meHeard.findViewById(R.id.topbar_button1);
        topbarButton2 = (LinearLayout) meHeard.findViewById(R.id.topbar_button2);
        topbarButton3 = (LinearLayout) meHeard.findViewById(R.id.topbar_button3);
        topbarButton4 = (LinearLayout) meHeard.findViewById(R.id.topbar_button4);

        followedByBadge = (ImageView) meHeard.findViewById(R.id.followed_by_badge);
        likedByBadge = (ImageView) meHeard.findViewById(R.id.liked_by_badge);
    }

    private void setView() {
        setTitle(R.string.tab_item_me);

        setRightButton(R.drawable.navigation_more_icon, false);

        listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false,
                true));

        User myUser = mIAccountLogic.getMyUserInfoFromDB();
        if (null != myUser) {
            mUser = myUser;
            setViewUser();
        }

        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MeAction.AVATARSHOW_ACTION);
                intent.putExtra("images", mUser.avatar_url);// 非必须
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                intent.putExtra("locationX", location[0]);// 必须
                intent.putExtra("locationY", location[1]);// 必须
                intent.putExtra("width", view.getWidth());// 必须
                intent.putExtra("height", view.getHeight());// 必须
                startActivity(intent);
                context.overridePendingTransition(0, 0);
            }
        });

        requestId = mIAccountLogic.getMyUserInfo();

        requestIdTab1 = mIUserLogic.getUserGalleryByUserId(FusionConfig.getInstance().getUserId(),
                null, FusionCode.CommonColumnsValue.COUNT_VALUE);
        requestIdTab2 = mIUserLogic.getMBlogListByUserId(FusionConfig.getInstance().getUserId(),
                null, FusionCode.CommonColumnsValue.COUNT_VALUE);
        requestIdTab3 = mIUserLogic.getMentionedGalleryListByUserId(FusionConfig.getInstance()
                .getUserId(), null, FusionCode.CommonColumnsValue.COUNT_VALUE);
        requestIdTab4 = mIUserLogic.getTagThumbnailsListByUserId(FusionConfig.getInstance()
                .getUserId(), null, FusionCode.CommonColumnsValue.COUNT_VALUE);

        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestId = mIAccountLogic.getMyUserInfo();
                switch (confirmTab) {
                    case 1: {
                        requestIdTab1 = mIUserLogic.getUserGalleryByUserId(FusionConfig.getInstance()
                                .getUserId(), null, FusionCode.CommonColumnsValue.COUNT_VALUE);
                    }
                    case 2: {
                        requestIdTab2 = mIUserLogic.getMBlogListByUserId(FusionConfig.getInstance()
                                .getUserId(), null, FusionCode.CommonColumnsValue.COUNT_VALUE);
                    }
                    case 3: {
                        requestIdTab3 = mIUserLogic.getMentionedGalleryListByUserId(FusionConfig
                                        .getInstance().getUserId(), null,
                                FusionCode.CommonColumnsValue.COUNT_VALUE);
                    }
                    case 4: {
                        requestIdTab4 = mIUserLogic.getTagThumbnailsListByUserId(FusionConfig
                                        .getInstance().getUserId(), null,
                                FusionCode.CommonColumnsValue.COUNT_VALUE);
                    }
                }
            }

            @Override
            public void onAddMore() {
                switch (confirmTab) {
                    case 1: {
                        if (mGalleryListTab1.size() > 0) {
                            requestIdTab1 = mIUserLogic.getUserGalleryByUserId(FusionConfig
                                    .getInstance().getUserId(), mGalleryListTab1.get(mGalleryListTab1
                                    .size() - 1)._id, FusionCode.CommonColumnsValue.COUNT_VALUE);
                        } else {
                            requestIdTab1 = mIUserLogic.getUserGalleryByUserId(FusionConfig
                                            .getInstance().getUserId(), null,
                                    FusionCode.CommonColumnsValue.COUNT_VALUE);
                        }
                    }
                    case 2: {
                        if (mBlogListTab2.size() > 0) {
                            requestIdTab2 = mIUserLogic.getMBlogListByUserId(FusionConfig.getInstance()
                                            .getUserId(), mBlogListTab2.get(mBlogListTab2.size() - 1)._id,
                                    FusionCode.CommonColumnsValue.COUNT_VALUE);
                        } else {
                            requestIdTab2 = mIUserLogic.getMBlogListByUserId(FusionConfig.getInstance()
                                    .getUserId(), null, FusionCode.CommonColumnsValue.COUNT_VALUE);
                        }
                    }
                    case 3: {
                        if (mMentionedListTab3.size() > 0) {
                            requestIdTab3 = mIUserLogic.getMentionedGalleryListByUserId(FusionConfig
                                            .getInstance().getUserId(), null,
                                    FusionCode.CommonColumnsValue.COUNT_VALUE);
                        } else {
                            requestIdTab3 = mIUserLogic.getMentionedGalleryListByUserId(FusionConfig
                                            .getInstance().getUserId(), null,
                                    FusionCode.CommonColumnsValue.COUNT_VALUE);
                        }
                    }
                    case 4: {
                        if (mTagThumbnailListTab4.size() > 0) {
                            requestIdTab4 = mIUserLogic.getTagThumbnailsListByUserId(FusionConfig
                                            .getInstance().getUserId(), mTagThumbnailListTab4
                                            .get(mTagThumbnailListTab4.size() - 1).tag._id,
                                    FusionCode.CommonColumnsValue.COUNT_VALUE);
                        } else {
                            requestIdTab4 = mIUserLogic.getTagThumbnailsListByUserId(FusionConfig
                                            .getInstance().getUserId(), null,
                                    FusionCode.CommonColumnsValue.COUNT_VALUE);
                        }
                    }
                }
            }
        });

        meSettingLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MeAction.MESETTING_ACTION);

                startActivityForResult(intent, MESETTINGREQUESTCODE);
            }
        });

        topbarButton1.setOnClickListener(new TabOnClickListener());
        topbarButton2.setOnClickListener(new TabOnClickListener());
        topbarButton3.setOnClickListener(new TabOnClickListener());
        topbarButton4.setOnClickListener(new TabOnClickListener());
        updataListView(R.id.topbar_button1);

        meHeard.findViewById(R.id.liked_by_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FusionAction.MeAction.LIKEBYLIST_ACTION));
                mIChatLogic.setNotifyBadge(FusionCode.SettingKey.IS_LIKED_BY, false);
            }
        });

        meHeard.findViewById(R.id.following_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FusionAction.MeAction.FOLLOW_ACTION));
            }
        });
        meHeard.findViewById(R.id.followed_by_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FusionAction.MeAction.FOLLOWEDBYLIST_ACTION));
                mIChatLogic.setNotifyBadge(FusionCode.SettingKey.IS_FOLLOWED_BY, false);
            }
        });

        getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FusionAction.MeAction.SETTING_ACTION));
            }
        });

        ChatManager.getInstance().addListener(this);
    }

    private void setViewUser() {
        ImageLoader.getInstance().displayImage(mUser.avatar_url, avatarImageView, options);
        nameTextView.setText(mUser.name);

        if (!TextUtils.isEmpty(mUser.role)) {
            roleImageView.setVisibility(View.VISIBLE);
        }

        if (FusionCode.Role.H.equals(mUser.role)) {
            roleImageView.setImageDrawable(getResources().getDrawable(R.drawable.home_h_icon));
        } else if (FusionCode.Role.T.equals(mUser.role)) {
            roleImageView.setImageDrawable(getResources().getDrawable(R.drawable.home_t_icon));
        } else if (FusionCode.Role.P.equals(mUser.role)) {
            roleImageView.setImageDrawable(getResources().getDrawable(R.drawable.home_p_icon));
        } else if (FusionCode.Role.MH.equals(mUser.role)) {
            roleImageView.setImageDrawable(getResources().getDrawable(R.drawable.home_0_icon));
        } else if (FusionCode.Role.MT.equals(mUser.role)) {
            roleImageView.setImageDrawable(getResources().getDrawable(R.drawable.home_1_icon));
        } else if (FusionCode.Role.MP.equals(mUser.role)) {
            roleImageView.setImageDrawable(getResources().getDrawable(R.drawable.home_0_5_icon));
        } else {
            roleImageView.setVisibility(View.GONE);
        }

        if (mUser.marriage) {
            xinghunImageView.setVisibility(View.VISIBLE);
        } else {
            xinghunImageView.setVisibility(View.GONE);
        }
        ageTextView.setText(mUser.age + getString(R.string.age_year));
        xingzuoTextView.setText(mUser.sign);
        heightTextView.setText(mUser.height + "cm");

        if (null != mUser.count) {
            followingTextView.setText(mUser.count.following);
            followedByTextView.setText(mUser.count.followed_by);
            mediaTextView.setText(mUser.count.media);
            likedByTextView.setText(mUser.count.liked_by);
        }

        if (SettingDbAdapter.getInstance().getBoolean(FusionCode.SettingKey.IS_LIKED_BY, false)) {
            likedByBadge.setVisibility(View.VISIBLE);
        } else {
            likedByBadge.setVisibility(View.GONE);
        }

        if (SettingDbAdapter.getInstance().getBoolean(FusionCode.SettingKey.IS_FOLLOWED_BY, false)) {
            followedByBadge.setVisibility(View.VISIBLE);
        } else {
            followedByBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        ChatManager.getInstance().removeListener(this);
        super.onDestroy();
    }

    @Override
    public void push(int what, Object object) {
        switch (what) {
            case IPushListener.NOTIFICATION:
                context.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (SettingDbAdapter.getInstance().getBoolean(
                                FusionCode.SettingKey.IS_LIKED_BY, false)) {
                            likedByBadge.setVisibility(View.VISIBLE);
                        } else {
                            likedByBadge.setVisibility(View.GONE);
                        }

                        if (SettingDbAdapter.getInstance().getBoolean(
                                FusionCode.SettingKey.IS_FOLLOWED_BY, false)) {
                            followedByBadge.setVisibility(View.VISIBLE);
                        } else {
                            followedByBadge.setVisibility(View.GONE);
                        }
                    }
                });
                break;
        }
    }

    private class TabOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            topbarButton1.setBackgroundColor(Color.parseColor("#f2f2f2"));
            topbarButton2.setBackgroundColor(Color.parseColor("#f2f2f2"));
            topbarButton3.setBackgroundColor(Color.parseColor("#f2f2f2"));
            topbarButton4.setBackgroundColor(Color.parseColor("#f2f2f2"));
            view.setBackgroundColor(Color.parseColor("#FFD800"));
            updataListView(view.getId());
        }
    }

    private void updataListView(int id) {
        switch (id) {
            case R.id.topbar_button1:
                if (null == mAdapterTab1) {
                    mAdapterTab1 = new PicGridAdapterTab1(context, options);
                }
                if (null != mGalleryListTab1) {
                    mAdapterTab1.setList(mGalleryListTab1);
                    listView.setAdapter(null);
                    listView.removeHeaderView(meHeard);
                    listView.addHeaderView(meHeard);
                    listView.setAdapter(mAdapterTab1);
                }
                confirmTab = 1;
                break;
            case R.id.topbar_button2:
                if (null == mAdapterTab2) {
                    mAdapterTab2 = new MBlogListAdapterTab2(context, options, null, mIMBlogLogic);
                }
                if (null != mBlogListTab2) {
                    mAdapterTab2.setList(mBlogListTab2);
                    listView.setAdapter(null);
                    listView.removeHeaderView(meHeard);
                    listView.addHeaderView(meHeard);
                    listView.setAdapter(mAdapterTab2);
                }
                confirmTab = 2;
                break;
            case R.id.topbar_button3:
                if (null == mAdapterTab3) {
                    mAdapterTab3 = new CircleByAdapterTab3(context, options);
                }
                if (null != mMentionedListTab3) {
                    mAdapterTab3.setList(mMentionedListTab3);
                    listView.setAdapter(null);
                    listView.removeHeaderView(meHeard);
                    listView.addHeaderView(meHeard);
                    listView.setAdapter(mAdapterTab3);
                }
                confirmTab = 3;
                break;
            case R.id.topbar_button4:
                if (null == mAdapterTab4) {
                    mAdapterTab4 = new TagImageAdapterTab4(context, options, FusionConfig.getInstance()
                            .getUserId());
                }
                if (null != mTagThumbnailListTab4) {
                    mAdapterTab4.setList(mTagThumbnailListTab4);
                    listView.setAdapter(null);
                    listView.removeHeaderView(meHeard);
                    listView.addHeaderView(meHeard);
                    listView.setAdapter(mAdapterTab4);
                }
                confirmTab = 4;
                break;
        }
    }

    @Override
    protected void initLogics() {
        mIUserLogic = (IUserLogic) getLogicByInterfaceClass(IUserLogic.class);
        mIAccountLogic = (IAccountLogic) getLogicByInterfaceClass(IAccountLogic.class);
        mIChatLogic = (IChatLogic) getLogicByInterfaceClass(IChatLogic.class);
        mIMBlogLogic = (IMBlogLogic) getLogicByInterfaceClass(IMBlogLogic.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != context && context.RESULT_OK == resultCode) {
            requestId = mIAccountLogic.getMyUserInfo();
        }
    }


    @Override
    protected void handleStateMessage(Message msg) {
        switch (msg.what) {
            case FusionMessageType.AccountMessageType.GET_MYUSER_INFO_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(requestId)
                        && requestId.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        mUser = (User) object.mObject;
                        if (isAdded()) {
                            setViewUser();
                        }
                    }
                }
                if (null != mPullToRefreshListView) {
                    mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }
            case FusionMessageType.AccountMessageType.GET_MYUSER_INFO_FAIL: {
                if (null != mPullToRefreshListView) {
                    mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }
            case FusionMessageType.UserMessageType.PULL_GALLERY_LIST_BY_ID_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(requestIdTab1)
                        && requestIdTab1.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        mGalleryListTab1 = (List<Gallery>) object.mObject;
                        if (confirmTab == 1) {
                            if (null == mAdapterTab1) {
                                mAdapterTab1 = new PicGridAdapterTab1(context, options);
                            }
                            if (null != mGalleryListTab1) {
                                mAdapterTab1.setList(mGalleryListTab1);
                                listView.setAdapter(null);
                                listView.removeHeaderView(meHeard);
                                listView.addHeaderView(meHeard);
                                listView.setAdapter(mAdapterTab1);
                            }
                        }
                    }
                }
                if (null != mPullToRefreshListView) {
                    mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }
            case FusionMessageType.UserMessageType.PUSH_GALLERY_LIST_BY_ID_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(requestIdTab1)
                        && requestIdTab1.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        mGalleryListTab1.addAll((List<Gallery>) object.mObject);
                        if (confirmTab == 1) {
                            if (null == mAdapterTab1) {
                                mAdapterTab1 = new PicGridAdapterTab1(context, options);
                            }
                            int i = listView.getFirstVisiblePosition() + listView.getHeaderViewsCount();
                            if (null != mGalleryListTab1) {
                                mAdapterTab1.setList(mGalleryListTab1);
                                listView.setAdapter(null);
                                listView.removeHeaderView(meHeard);
                                listView.addHeaderView(meHeard);
                                listView.setAdapter(mAdapterTab1);
                                listView.setSelection(i);
                            }
                        }
                    }
                }
                if (null != mPullToRefreshListView) {
                    mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }
            case FusionMessageType.UserMessageType.GET_GALLERY_LIST_BY_ID_FAIL: {
                if (null != mPullToRefreshListView) {
                    mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }
            case FusionMessageType.UserMessageType.PULL_MBLOG_LIST_BY_ID_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(requestIdTab2)
                        && requestIdTab2.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        mBlogListTab2 = (List<MBlog>) object.mObject;
                        if (confirmTab == 2) {
                            if (null == mAdapterTab2) {
                                mAdapterTab2 = new MBlogListAdapterTab2(context, options, null, mIMBlogLogic);
                            }
                            if (null != mBlogListTab2) {
                                mAdapterTab2.setList(mBlogListTab2);
                                listView.setAdapter(null);
                                listView.removeHeaderView(meHeard);
                                listView.addHeaderView(meHeard);
                                listView.setAdapter(mAdapterTab2);
                            }
                        }
                    }

                }
                if (null != mPullToRefreshListView) {
                    mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }
            case FusionMessageType.UserMessageType.PUSH_MBLOG_LIST_BY_ID_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(requestIdTab2)
                        && requestIdTab2.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        mBlogListTab2.addAll((List<MBlog>) object.mObject);
                        if (confirmTab == 2) {
                            if (null == mAdapterTab2) {
                                mAdapterTab2 = new MBlogListAdapterTab2(context, options, null, mIMBlogLogic);
                            }
                            int i = listView.getFirstVisiblePosition() + listView.getHeaderViewsCount();
                            if (null != mBlogListTab2) {
                                mAdapterTab2.setList(mBlogListTab2);
                                listView.setAdapter(null);
                                listView.removeHeaderView(meHeard);
                                listView.addHeaderView(meHeard);
                                listView.setAdapter(mAdapterTab2);
                                listView.setSelection(i);
                            }
                        }
                    }

                }
                if (null != mPullToRefreshListView) {
                    mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }
            case FusionMessageType.UserMessageType.PUSH_MBLOG_LIST_BY_ID_FAIL: {
                if (null != mPullToRefreshListView) {
                    mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }
            case FusionMessageType.UserMessageType.PULL_MENTIONEDGALLERY_LIST_BY_ID_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(requestIdTab3)
                        && requestIdTab3.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        mMentionedListTab3 = (List<Mentioned>) object.mObject;
                        if (confirmTab == 3) {
                            if (null == mAdapterTab3) {
                                mAdapterTab3 = new CircleByAdapterTab3(context, options);
                            }
                            if (null != mMentionedListTab3) {
                                mAdapterTab3.setList(mMentionedListTab3);
                                listView.setAdapter(null);
                                listView.removeHeaderView(meHeard);
                                listView.addHeaderView(meHeard);
                                listView.setAdapter(mAdapterTab3);
                            }
                        }
                    }
                }
                if (null != mPullToRefreshListView) {
                    mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }
            case FusionMessageType.UserMessageType.PUSH_MENTIONEDGALLERY_LIST_BY_ID_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(requestIdTab3)
                        && requestIdTab3.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        mMentionedListTab3.addAll((List<Mentioned>) object.mObject);
                        if (confirmTab == 3) {
                            if (null == mAdapterTab3) {
                                mAdapterTab3 = new CircleByAdapterTab3(context, options);
                            }
                            int i = listView.getFirstVisiblePosition() + listView.getHeaderViewsCount();
                            if (null != mMentionedListTab3) {
                                mAdapterTab3.setList(mMentionedListTab3);
                                listView.setAdapter(null);
                                listView.removeHeaderView(meHeard);
                                listView.addHeaderView(meHeard);
                                listView.setAdapter(mAdapterTab3);
                                listView.setSelection(i);
                            }
                        }
                    }
                }
                if (null != mPullToRefreshListView) {
                    mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }
            case FusionMessageType.UserMessageType.GET_MENTIONEDGALLERY_LIST_BY_ID_FAIL: {
                if (null != mPullToRefreshListView) {
                    mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }
            case FusionMessageType.UserMessageType.PULL_TAGTHUMBNAILS_LIST_BY_ID_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(requestIdTab4)
                        && requestIdTab4.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        mTagThumbnailListTab4 = (List<TagThumbnail>) object.mObject;
                        if (confirmTab == 4) {
                            if (null == mAdapterTab4) {
                                mAdapterTab4 = new TagImageAdapterTab4(context, options, FusionConfig
                                        .getInstance().getUserId());
                            }
                            if (null != mTagThumbnailListTab4) {
                                mAdapterTab4.setList(mTagThumbnailListTab4);
                                listView.setAdapter(null);
                                listView.removeHeaderView(meHeard);
                                listView.addHeaderView(meHeard);
                                listView.setAdapter(mAdapterTab4);
                            }
                        }
                    }
                }
                if (null != mPullToRefreshListView) {
                    mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }

            case FusionMessageType.UserMessageType.PUSH_TAGTHUMBNAILS_LIST_BY_ID_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(requestIdTab4)
                        && requestIdTab4.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        mTagThumbnailListTab4.addAll((List<TagThumbnail>) object.mObject);
                        if (confirmTab == 4) {
                            if (null == mAdapterTab4) {
                                mAdapterTab4 = new TagImageAdapterTab4(context, options, FusionConfig
                                        .getInstance().getUserId());
                            }
                            int i = listView.getFirstVisiblePosition() + listView.getHeaderViewsCount();
                            if (null != mTagThumbnailListTab4) {
                                mAdapterTab4.setList(mTagThumbnailListTab4);
                                listView.setAdapter(null);
                                listView.removeHeaderView(meHeard);
                                listView.addHeaderView(meHeard);
                                listView.setAdapter(mAdapterTab4);
                                listView.setSelection(i);
                            }
                        }
                    }
                }
                if (null != mPullToRefreshListView) {
                    mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }
            case FusionMessageType.UserMessageType.GET_TAGTHUMBNAILS_LIST_BY_ID_FAIL: {
                if (null != mPullToRefreshListView) {
                    mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }

            case FusionMessageType.MBlogMessageType.IS_LIKES_ACTION_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && null!=mAdapterTab2&&!TextUtils.isEmpty(mAdapterTab2.likerequestId)
                        && mAdapterTab2.likerequestId.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        MBlog MBlog = (MBlog) object.mObject;
                        if (MBlog.is_liked) {
                            MBlog.is_liked = false;
                            MBlog.likes_count = MBlog.likes_count - 1;
                            List<User> mUserList = new ArrayList<User>();
                            for (User mUser : MBlog.likes) {
                                if (!mUser.userId.equals(mIAccountLogic.getMyUserInfoFromDB().userId)) {
                                    mUserList.add(mUser);
                                }
                            }
                            MBlog.likes = mUserList;
                        } else {
                            MBlog.is_liked = true;
                            MBlog.likes_count = MBlog.likes_count + 1;
                            MBlog.likes.add(0, mIAccountLogic.getMyUserInfoFromDB());
                        }
                        mAdapterTab2.notifyDataSetChanged();
                    }
                }
                break;
            }
            case FusionMessageType.MBlogMessageType.IS_LIKES_ACTION_FAIL: {
                break;
            }

            case MBlogMessageType.DELETE_MBLOG_BY_ID_SUCCESS: {
                requestIdTab1 = mIUserLogic.getUserGalleryByUserId(FusionConfig.getInstance().getUserId(),
                        null, FusionCode.CommonColumnsValue.COUNT_VALUE);
                break;
            }
            default:
        }

    }
}
