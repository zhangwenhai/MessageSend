package com.theone.sns.ui.me.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionAction.CommonField;
import com.theone.sns.common.FusionAction.MeAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionCode.RelationshipAction;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.common.FusionMessageType.UserMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.component.location.gps.LocalLocation;
import com.theone.sns.component.location.gps.LocationManager;
import com.theone.sns.logic.account.IAccountLogic;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.chat.impl.ChatManager;
import com.theone.sns.logic.mblog.IMBlogLogic;
import com.theone.sns.logic.model.chat.CreateGroup;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.logic.model.chat.base.NameCard;
import com.theone.sns.logic.model.mblog.MBlog;
import com.theone.sns.logic.model.user.Mentioned;
import com.theone.sns.logic.model.user.TagThumbnail;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.model.user.base.Gallery;
import com.theone.sns.logic.model.user.base.Label;
import com.theone.sns.logic.user.IUserLogic;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.chat.activity.ChatActivity;
import com.theone.sns.ui.me.CircleByAdapterTab3;
import com.theone.sns.ui.me.MBlogListAdapterTab2;
import com.theone.sns.ui.me.PicGridAdapterTab1;
import com.theone.sns.ui.me.TagImageAdapterTab4;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.StringUtil;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;
import com.theone.sns.util.uiwidget.IphoneStyleAlertDialogBuilder;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshBase;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/9/10.
 */
public class TaActivity extends IphoneTitleActivity {

    public static final int ALIAS_REQUESTCODE = 1;

    private PullToRefreshListView mPullToRefreshListView;

    private ListView listView;

    private View taHeard;

    private LinearLayout topbarButton1;

    private LinearLayout topbarButton2;

    private LinearLayout topbarButton3;

    private LinearLayout topbarButton4;

    private BaseAdapter mAdapter;

    private LayoutInflater inflater;

    private Button followBtn;

    private Button pmBtn;

    private int with;

    private String uid;
    private User mUser;
    private IUserLogic mIUserLogic;
    private String requestId;
    private PicGridAdapterTab1 mAdapterTab1;
    private MBlogListAdapterTab2 mAdapterTab2;
    private CircleByAdapterTab3 mAdapterTab3;
    private TagImageAdapterTab4 mAdapterTab4;
    private int confirmTab = 1;
    private List<Gallery> mGalleryListTab1 = new ArrayList<Gallery>();
    private List<MBlog> mBlogListTab2 = new ArrayList<MBlog>();
    private List<Mentioned> mMentionedListTab3 = new ArrayList<Mentioned>();
    private List<TagThumbnail> mTagThumbnailListTab4 = new ArrayList<TagThumbnail>();
    private String requestIdTab1;
    private String requestIdTab2;
    private String requestIdTab3;
    private String requestIdTab4;
    private ImageView avatarImageView;
    private TextView nameTextView;
    private ImageView roleImageView;
    private ImageView xinghunImageView;
    private ImageView xingxingImageView;
    private TextView ageTextView;
    private TextView xingzuoTextView;
    private TextView heightTextView;
    private TextView distance;
    private TextView followingTextView;
    private TextView followedByTextView;
    private TextView mediaTextView;
    private TextView likedByTextView;
    private IphoneStyleAlertDialogBuilder m_cantAccessDialog;
    private String requestIdIsStar;
    private LocalLocation mLocation;
    private IChatLogic mIChatLogic;
    private String createGroupId;
    private String getGroupInfoFromDBId;
    private PmOnClickListener mPmOnClickListener;
    private GridView userPurposes;
    private InterestsGridViewAdapter mInterestsGridViewAdapter;

    private IMBlogLogic mIMBlogLogic;
    private IAccountLogic mIAccountLogic;

    private class TabOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            topbarButton1.setBackgroundColor(Color.parseColor("#f2f2f2"));
            topbarButton2.setBackgroundColor(Color.parseColor("#f2f2f2"));
            topbarButton3.setBackgroundColor(Color.parseColor("#f2f2f2"));
            topbarButton4.setBackgroundColor(Color.parseColor("#f2f2f2"));
            view.setBackgroundColor(Color.parseColor("#ffd800"));
            updataListView(view.getId());
        }
    }

    private class PmOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            List<String> mlist = new ArrayList<String>();
            mlist.add(uid);
            getGroupInfoFromDBId = mIChatLogic.getGroupInfoFromDB(mlist);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSubContent(R.layout.ta_main);

        getView();

        setView();

        setListener();
    }

    private void getView() {
        uid = getIntent().getStringExtra(FusionAction.MeAction.UID);
        if (TextUtils.isEmpty(uid)) {
            finish();
        }
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.list);
        listView = mPullToRefreshListView.getRefreshableView();

        with = getResources().getDisplayMetrics().widthPixels;

        inflater = LayoutInflater.from(this);
        taHeard = LayoutInflater.from(this).inflate(R.layout.ta_heard, null);

        avatarImageView = (ImageView) taHeard.findViewById(R.id.avatar);
        nameTextView = (TextView) taHeard.findViewById(R.id.name);
        roleImageView = (ImageView) taHeard.findViewById(R.id.role);
        xinghunImageView = (ImageView) taHeard.findViewById(R.id.xinghun);
        xingxingImageView = (ImageView) taHeard.findViewById(R.id.xingxing);
        ageTextView = (TextView) taHeard.findViewById(R.id.age);
        xingzuoTextView = (TextView) taHeard.findViewById(R.id.xingzuo);
        heightTextView = (TextView) taHeard.findViewById(R.id.height);
        distance = (TextView) taHeard.findViewById(R.id.distance);
        userPurposes = (GridView) taHeard.findViewById(R.id.user_purposes);
        followingTextView = (TextView) taHeard.findViewById(R.id.following);
        followedByTextView = (TextView) taHeard.findViewById(R.id.followed_by);
        mediaTextView = (TextView) taHeard.findViewById(R.id.media);
        likedByTextView = (TextView) taHeard.findViewById(R.id.liked_by);

        topbarButton1 = (LinearLayout) taHeard
                .findViewById(R.id.topbar_button1);
        topbarButton2 = (LinearLayout) taHeard
                .findViewById(R.id.topbar_button2);
        topbarButton3 = (LinearLayout) taHeard
                .findViewById(R.id.topbar_button3);
        topbarButton4 = (LinearLayout) taHeard
                .findViewById(R.id.topbar_button4);
        followBtn = (Button) taHeard.findViewById(R.id.follow_btn);
        pmBtn = (Button) taHeard.findViewById(R.id.pm_btn);
    }

    private void setView() {
        setTitle(R.string.ta);
        setLeftButton(R.drawable.icon_back, false, false);

        mPmOnClickListener = new PmOnClickListener();

        if (!FusionConfig.getInstance().getUserId().equals(uid)) {
            setRightButton(R.drawable.navigation_more_icon, false);
        }

        mLocation = LocationManager.getInstance().getLocation();

        updataListView(R.id.topbar_button1);

        requestId = mIUserLogic.getUserByUserId(uid);

        requestIdTab1 = mIUserLogic.getUserGalleryByUserId(uid, null,
                FusionCode.CommonColumnsValue.COUNT_VALUE);
        requestIdTab2 = mIUserLogic.getMBlogListByUserId(uid, null,
                FusionCode.CommonColumnsValue.COUNT_VALUE);
        requestIdTab3 = mIUserLogic.getMentionedGalleryListByUserId(uid, null,
                FusionCode.CommonColumnsValue.COUNT_VALUE);
        requestIdTab4 = mIUserLogic.getTagThumbnailsListByUserId(uid, null,
                FusionCode.CommonColumnsValue.COUNT_VALUE);

        mPullToRefreshListView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        requestId = mIUserLogic.getUserByUserId(uid);
                        switch (confirmTab) {
                            case 1: {
                                requestIdTab1 = mIUserLogic.getUserGalleryByUserId(
                                        uid, null,
                                        FusionCode.CommonColumnsValue.COUNT_VALUE);
                            }
                            case 2: {
                                requestIdTab2 = mIUserLogic.getMBlogListByUserId(
                                        uid, null,
                                        FusionCode.CommonColumnsValue.COUNT_VALUE);
                            }
                            case 3: {
                                requestIdTab3 = mIUserLogic
                                        .getMentionedGalleryListByUserId(
                                                uid,
                                                null,
                                                FusionCode.CommonColumnsValue.COUNT_VALUE);
                            }
                            case 4: {
                                requestIdTab4 = mIUserLogic
                                        .getTagThumbnailsListByUserId(
                                                uid,
                                                null,
                                                FusionCode.CommonColumnsValue.COUNT_VALUE);
                            }
                        }
                    }

                    @Override
                    public void onAddMore() {
                        switch (confirmTab) {
                            case 1: {
                                if (mGalleryListTab1.size() > 0) {
                                    requestIdTab1 = mIUserLogic
                                            .getUserGalleryByUserId(
                                                    uid,
                                                    mGalleryListTab1
                                                            .get(mGalleryListTab1
                                                                    .size() - 1)._id,
                                                    FusionCode.CommonColumnsValue.COUNT_VALUE);
                                } else {
                                    requestIdTab1 = mIUserLogic
                                            .getUserGalleryByUserId(
                                                    uid,
                                                    null,
                                                    FusionCode.CommonColumnsValue.COUNT_VALUE);
                                }
                            }
                            case 2: {
                                if (mBlogListTab2.size() > 0) {
                                    requestIdTab2 = mIUserLogic
                                            .getMBlogListByUserId(
                                                    uid,
                                                    mBlogListTab2.get(mBlogListTab2
                                                            .size() - 1)._id,
                                                    FusionCode.CommonColumnsValue.COUNT_VALUE);
                                } else {
                                    requestIdTab2 = mIUserLogic
                                            .getMBlogListByUserId(
                                                    uid,
                                                    null,
                                                    FusionCode.CommonColumnsValue.COUNT_VALUE);
                                }
                            }
                            case 3: {
                                if (mMentionedListTab3.size() > 0) {
                                    requestIdTab3 = mIUserLogic
                                            .getMentionedGalleryListByUserId(
                                                    uid,
                                                    null,
                                                    FusionCode.CommonColumnsValue.COUNT_VALUE);
                                } else {
                                    requestIdTab3 = mIUserLogic
                                            .getMentionedGalleryListByUserId(
                                                    uid,
                                                    null,
                                                    FusionCode.CommonColumnsValue.COUNT_VALUE);
                                }
                            }
                            case 4: {
                                if (mTagThumbnailListTab4.size() > 0) {
                                    requestIdTab4 = mIUserLogic
                                            .getTagThumbnailsListByUserId(
                                                    uid,
                                                    mTagThumbnailListTab4
                                                            .get(mTagThumbnailListTab4
                                                                    .size() - 1).tag._id,
                                                    FusionCode.CommonColumnsValue.COUNT_VALUE);
                                } else {
                                    requestIdTab4 = mIUserLogic
                                            .getTagThumbnailsListByUserId(
                                                    uid,
                                                    null,
                                                    FusionCode.CommonColumnsValue.COUNT_VALUE);
                                }
                            }
                        }
                    }
                });
    }

    private void setListener() {
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (null == mUser) {
                    return;
                }

                FusionConfig.clickFollowButton(mIUserLogic, mUser, followBtn);

                if (getString(R.string.follow).equals(
                        (String) followBtn.getText().toString())) {

                    if (mUser.marriage) {
                        xinghunImageView.setVisibility(View.VISIBLE);
                    } else {
                        xinghunImageView.setVisibility(View.GONE);
                    }

                } else {
                    xinghunImageView.setVisibility(View.GONE);
                }
            }
        });

        topbarButton1.setOnClickListener(new TabOnClickListener());
        topbarButton2.setOnClickListener(new TabOnClickListener());
        topbarButton3.setOnClickListener(new TabOnClickListener());
        topbarButton4.setOnClickListener(new TabOnClickListener());

        likedByTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(
                        FusionAction.MeAction.LIKEBYLIST_ACTION).putExtra(
                        MeAction.UID, uid));
            }
        });

        followingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FusionAction.MeAction.FOLLOW_ACTION)
                        .putExtra(MeAction.UID, uid));
            }
        });
        followedByTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(
                        FusionAction.MeAction.FOLLOWEDBYLIST_ACTION).putExtra(
                        MeAction.UID, uid));
            }
        });
        getRightButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCantAccessDialog();
            }
        });
    }

    private void setViewUser() {

        if (null == mUser) {
            return;
        }

        ImageLoader.getInstance().displayImage(mUser.avatar_url,
                avatarImageView, options);

        if (!TextUtils.isEmpty(mUser.alias)) {
            nameTextView.setText(mUser.alias);
        } else {
            nameTextView.setText(mUser.name);
        }

        if (FusionConfig.getInstance().getUserId().equals(mUser.userId)) {

            followBtn.setVisibility(View.GONE);

            pmBtn.setVisibility(View.GONE);

        } else {

            followBtn.setVisibility(View.VISIBLE);

            pmBtn.setVisibility(View.VISIBLE);

            FusionConfig.showFollowButton(mUser, followBtn);
        }

        pmBtn.setOnClickListener(mPmOnClickListener);

        if (!TextUtils.isEmpty(mUser.role)) {
            roleImageView.setVisibility(View.VISIBLE);
        }

        if (FusionCode.Role.H.equals(mUser.role)) {
            roleImageView.setImageDrawable(getResources().getDrawable(
                    R.drawable.home_h_icon));
        } else if (FusionCode.Role.T.equals(mUser.role)) {
            roleImageView.setImageDrawable(getResources().getDrawable(
                    R.drawable.home_t_icon));
        } else if (FusionCode.Role.P.equals(mUser.role)) {
            roleImageView.setImageDrawable(getResources().getDrawable(
                    R.drawable.home_p_icon));
        } else if (FusionCode.Role.MH.equals(mUser.role)) {
            roleImageView.setImageDrawable(getResources().getDrawable(
                    R.drawable.home_0_icon));
        } else if (FusionCode.Role.MT.equals(mUser.role)) {
            roleImageView.setImageDrawable(getResources().getDrawable(
                    R.drawable.home_1_icon));
        } else if (FusionCode.Role.MP.equals(mUser.role)) {
            roleImageView.setImageDrawable(getResources().getDrawable(
                    R.drawable.home_0_5_icon));
        } else {
            roleImageView.setVisibility(View.GONE);
        }

        if (mUser.marriage && mUser.is_following) {
            xinghunImageView.setVisibility(View.VISIBLE);
        } else {
            xinghunImageView.setVisibility(View.GONE);
        }

        if (mUser.is_starring) {
            xingxingImageView.setVisibility(View.VISIBLE);
        } else {
            xingxingImageView.setVisibility(View.GONE);
        }

        ageTextView.setText(mUser.age + getString(R.string.age_year));
        xingzuoTextView.setText(mUser.sign);
        heightTextView.setText(mUser.height + "cm");

        List<String> userlocation = mUser.location;

        if (null != userlocation && userlocation.size() == 2
                && null != mLocation) {

            distance.setVisibility(View.VISIBLE);

            String dis = StringUtil.getDistance(
                    Double.valueOf(mLocation.longitude),
                    Double.valueOf(mLocation.latitude),
                    Double.valueOf(userlocation.get(0)),
                    Double.valueOf(userlocation.get(1)))
                    + getString(R.string.distance_km) + " " + mUser.region;

            distance.setText(dis);
        } else {
            distance.setVisibility(View.GONE);
        }

        if (null != mUser.count) {
            followingTextView.setText(mUser.count.following
                    + getString(R.string.follow));
            followedByTextView.setText(mUser.count.followed_by
                    + getString(R.string.followed_by));
            mediaTextView
                    .setText(mUser.count.media + getString(R.string.media));
            likedByTextView.setText(mUser.count.liked_by
                    + getString(R.string.like_by));
        }

        if (null != mUser.labels && mUser.labels.size() == 0) {
            findViewById(R.id.purposes_lin).setVisibility(View.GONE);
        } else {
//            findViewById(R.id.purposes_lin).setVisibility(View.VISIBLE);
//            purposes1.setVisibility(View.INVISIBLE);
//            purposes2.setVisibility(View.INVISIBLE);
//            purposes3.setVisibility(View.INVISIBLE);
//
//            int j = 0;
//            if (null != mUser.labels) {
//                for (Label mLabel : mUser.labels) {
//                    if (null != mLabel) {
//                        purposes[j].setText(mLabel.name);
//                        if (mLabel.me) {
//                            purposes[j].setBackgroundDrawable(getResources()
//                                    .getDrawable(
//                                            R.drawable.profile_same_interest_btn));
//                            purposes[j].setTextColor(getResources().getColor(
//                                    R.color.black));
//                            purposes[j].setVisibility(View.VISIBLE);
//                        } else {
//                            purposes[j].setBackgroundDrawable(getResources()
//                                    .getDrawable(R.drawable.profile_interest_btn));
//                            purposes[j].setTextColor(getResources().getColor(
//                                    R.color.black));
//                            purposes[j].setVisibility(View.VISIBLE);
//                        }
//                        j++;
//                        if (j == 3) {
//                            break;
//                        }
//                    }
//                }
//
//                if (mUser.labels.size() > 3) {
//                    mInterestsGridViewAdapter = new InterestsGridViewAdapter();
//                    mInterestsGridViewAdapter.setList(mUser.labels);
//                    userPurposes.setAdapter(mInterestsGridViewAdapter);
//                }
//            }

//            findViewById(R.id.show_pur).setOnClickListener(
//                    new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if (null != mUser.labels && mUser.labels.size() > 3) {
//                                if (View.GONE == userPurposes.getVisibility()) {
//                                    userPurposes.setVisibility(View.VISIBLE);
//
//                                    ((TextView) findViewById(R.id.show_pur))
//                                            .setText("收起全部");
//
//                                    ((ImageView) findViewById(R.id.image))
//                                            .setImageDrawable(getResources()
//                                                    .getDrawable(
//                                                            R.drawable.iphone_submenu_normal_xia));
//
//                                } else {
//                                    userPurposes.setVisibility(View.GONE);
//
//                                    ((TextView) findViewById(R.id.show_pur))
//                                            .setText("展开全部");
//
//                                    ((ImageView) findViewById(R.id.image))
//                                            .setImageDrawable(getResources()
//                                                    .getDrawable(
//                                                            R.drawable.iphone_submenu_normal_shang));
//                                }
//                            }
//                        }
//                    });
            mInterestsGridViewAdapter = new InterestsGridViewAdapter();
            mInterestsGridViewAdapter.setList(mUser.labels);
            userPurposes.setAdapter(mInterestsGridViewAdapter);

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
                overridePendingTransition(0, 0);
            }
        });
    }

    private void showCantAccessDialog() {

        if (null == mUser) {
            return;
        }

        m_cantAccessDialog = new IphoneStyleAlertDialogBuilder(this);

        m_cantAccessDialog.setTitle("");

        final String isStar = mUser.is_starring ? getString(R.string.unstar)
                : getString(R.string.star);

        if (mUser.is_following) {

            m_cantAccessDialog.addItem(0, getString(R.string.set_alias),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MeAction.ALIAS_ACTION);

                            intent.putExtra(CommonField.USER_ID_KEY,
                                    mUser.userId);

                            intent.putExtra(CommonField.USER_NAME_KEY,
                                    mUser.name);

                            startActivityForResult(intent, ALIAS_REQUESTCODE);

                            m_cantAccessDialog.dismiss();
                        }
                    });
        }

        if (mUser.is_following && mUser.is_followed_by) {

            m_cantAccessDialog.addItem(1, isStar, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String action = null;

                    if (isStar.equals(getString(R.string.star))) {

                        action = RelationshipAction.STAR_ACTION;

                    } else {

                        action = RelationshipAction.UNSTAR_ACTION;
                    }

                    requestIdIsStar = mIUserLogic.setUserRelationship(
                            mUser.userId, action);

                    m_cantAccessDialog.dismiss();
                }
            });
        }

        m_cantAccessDialog.addItem(2, getString(R.string.recommend_friend),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_cantAccessDialog.dismiss();

                        MessageInfo mMessageInfo = new MessageInfo();
                        mMessageInfo.messageType = FusionCode.MessageType.NAME_CARD;
                        NameCard mNameCard = new NameCard();
                        mNameCard._id = mUser.userId;
                        mNameCard.avatar_url = mUser.avatar_url;
                        mNameCard.location = mUser.location;
                        mNameCard.name = mUser.name;
                        mNameCard.role = mUser.role;
                        mNameCard.marriage = mUser.marriage;
                        mNameCard.region = mUser.region;
                        mMessageInfo.name_card = mNameCard;
                        User mUser = new User();
                        mUser.userId = FusionConfig.getInstance().getUserId();
                        mMessageInfo.owner = mUser;

                        startActivityForResult(new Intent(
                                        FusionAction.ChatAction.FORWARD_ACTION).putExtra(
                                        FusionAction.ChatAction.MESSGAE_INFO, mMessageInfo),
                                ChatActivity.SEND_NAMECARD_TO_CHAT);
                    }
                });

        m_cantAccessDialog.addItem(3, getString(R.string.report),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_cantAccessDialog.dismiss();
                    }
                });

        m_cantAccessDialog.addItem(4, getString(R.string.Cancel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_cantAccessDialog.dismiss();
                    }
                });
        m_cantAccessDialog.show();
    }

    private void updataListView(int id) {
        switch (id) {
            case R.id.topbar_button1:
                if (null == mAdapterTab1) {
                    mAdapterTab1 = new PicGridAdapterTab1(getApplicationContext(),
                            options);
                }
                if (null != mGalleryListTab1) {
                    mAdapterTab1.setList(mGalleryListTab1);
                    listView.setAdapter(null);
                    listView.removeHeaderView(taHeard);
                    listView.addHeaderView(taHeard);
                    listView.setAdapter(mAdapterTab1);
                }
                confirmTab = 1;
                break;
            case R.id.topbar_button2:
                if (null == mAdapterTab2) {
                    mAdapterTab2 = new MBlogListAdapterTab2(this, options,
                            mPmOnClickListener, mIMBlogLogic);
                }
                if (null != mBlogListTab2) {
                    mAdapterTab2.setList(mBlogListTab2);
                    listView.setAdapter(null);
                    listView.removeHeaderView(taHeard);
                    listView.addHeaderView(taHeard);
                    listView.setAdapter(mAdapterTab2);
                }
                confirmTab = 2;
                break;
            case R.id.topbar_button3:
                if (null == mAdapterTab3) {
                    mAdapterTab3 = new CircleByAdapterTab3(this, options);
                }
                if (null != mMentionedListTab3) {
                    mAdapterTab3.setList(mMentionedListTab3);
                    listView.setAdapter(null);
                    listView.removeHeaderView(taHeard);
                    listView.addHeaderView(taHeard);
                    listView.setAdapter(mAdapterTab3);
                }
                confirmTab = 3;
                break;
            case R.id.topbar_button4:
                if (null == mAdapterTab4) {
                    mAdapterTab4 = new TagImageAdapterTab4(getApplicationContext(),
                            options, uid);
                }
                if (null != mTagThumbnailListTab4) {
                    mAdapterTab4.setList(mTagThumbnailListTab4);
                    listView.setAdapter(null);
                    listView.removeHeaderView(taHeard);
                    listView.addHeaderView(taHeard);
                    listView.setAdapter(mAdapterTab4);
                }
                confirmTab = 4;
                break;
        }
    }

    @Override
    protected void handleStateMessage(Message msg) {
        switch (msg.what) {
            case FusionMessageType.UserMessageType.GET_USER_BY_ID_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(requestId)
                        && requestId.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        mUser = (User) object.mObject;
                        setViewUser();
                    }
                }
                if(null!=mPullToRefreshListView){
                	mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }
            case FusionMessageType.UserMessageType.GET_USER_BY_ID_FAIL: {
                if(null!=mPullToRefreshListView){
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
                                mAdapterTab1 = new PicGridAdapterTab1(
                                        getApplicationContext(), options);
                            }
                            if (null != mGalleryListTab1) {
                                mAdapterTab1.setList(mGalleryListTab1);
                                listView.setAdapter(null);
                                listView.removeHeaderView(taHeard);
                                listView.addHeaderView(taHeard);
                                listView.setAdapter(mAdapterTab1);
                            }
                        }
                    }
                }
                if(null!=mPullToRefreshListView){
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
                                mAdapterTab1 = new PicGridAdapterTab1(
                                        getApplicationContext(), options);
                            }
                            int i = listView.getFirstVisiblePosition() + listView.getHeaderViewsCount();
                            if (null != mGalleryListTab1) {
                                mAdapterTab1.setList(mGalleryListTab1);
                                listView.setAdapter(null);
                                listView.removeHeaderView(taHeard);
                                listView.addHeaderView(taHeard);
                                listView.setAdapter(mAdapterTab1);
                                listView.setSelection(i);
                            }
                        }
                    }
                }
                if(null!=mPullToRefreshListView){
                	mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }

            case FusionMessageType.UserMessageType.GET_GALLERY_LIST_BY_ID_FAIL: {
                if(null!=mPullToRefreshListView){
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
                                mAdapterTab2 = new MBlogListAdapterTab2(this,
                                        options, mPmOnClickListener, mIMBlogLogic);
                            }
                            if (null != mBlogListTab2) {
                                mAdapterTab2.setList(mBlogListTab2);
                                listView.setAdapter(null);
                                listView.removeHeaderView(taHeard);
                                listView.addHeaderView(taHeard);
                                listView.setAdapter(mAdapterTab2);
                            }
                        }
                    }

                }
                if(null!=mPullToRefreshListView){
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
                                mAdapterTab2 = new MBlogListAdapterTab2(this,
                                        options, mPmOnClickListener, mIMBlogLogic);
                            }
                            int i = listView.getFirstVisiblePosition() + listView.getHeaderViewsCount();
                            if (null != mBlogListTab2) {
                                mAdapterTab2.setList(mBlogListTab2);
                                listView.setAdapter(null);
                                listView.removeHeaderView(taHeard);
                                listView.addHeaderView(taHeard);
                                listView.setAdapter(mAdapterTab2);
                                listView.setSelection(i);
                            }
                        }
                    }

                }
                if(null!=mPullToRefreshListView){
                	mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }

            case FusionMessageType.UserMessageType.PUSH_MBLOG_LIST_BY_ID_FAIL: {
                if(null!=mPullToRefreshListView){
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
                                mAdapterTab3 = new CircleByAdapterTab3(
                                        getApplicationContext(), options);
                            }
                            if (null != mMentionedListTab3) {
                                mAdapterTab3.setList(mMentionedListTab3);
                                listView.setAdapter(null);
                                listView.removeHeaderView(taHeard);
                                listView.addHeaderView(taHeard);
                                listView.setAdapter(mAdapterTab3);
                            }
                        }
                    }
                }
                if(null!=mPullToRefreshListView){
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
                                mAdapterTab3 = new CircleByAdapterTab3(
                                        getApplicationContext(), options);
                            }
                            int i = listView.getFirstVisiblePosition() + listView.getHeaderViewsCount();
                            if (null != mMentionedListTab3) {
                                mAdapterTab3.setList(mMentionedListTab3);
                                listView.setAdapter(null);
                                listView.removeHeaderView(taHeard);
                                listView.addHeaderView(taHeard);
                                listView.setAdapter(mAdapterTab3);
                                listView.setSelection(i);
                            }
                        }
                    }
                }
                if(null!=mPullToRefreshListView){
                	mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }

            case FusionMessageType.UserMessageType.GET_MENTIONEDGALLERY_LIST_BY_ID_FAIL: {
                if(null!=mPullToRefreshListView){
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
                                mAdapterTab4 = new TagImageAdapterTab4(
                                        getApplicationContext(), options, uid);
                            }
                            if (null != mTagThumbnailListTab4) {
                                mAdapterTab4.setList(mTagThumbnailListTab4);
                                listView.setAdapter(null);
                                listView.removeHeaderView(taHeard);
                                listView.addHeaderView(taHeard);
                                listView.setAdapter(mAdapterTab4);
                            }
                        }
                    }
                }
                if(null!=mPullToRefreshListView){
                	mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }

            case FusionMessageType.UserMessageType.PUSH_TAGTHUMBNAILS_LIST_BY_ID_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(requestIdTab4)
                        && requestIdTab4.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        mTagThumbnailListTab4
                                .addAll((List<TagThumbnail>) object.mObject);
                        if (confirmTab == 4) {
                            if (null == mAdapterTab4) {
                                mAdapterTab4 = new TagImageAdapterTab4(
                                        getApplicationContext(), options, uid);
                            }
                            int i = listView.getFirstVisiblePosition() + listView.getHeaderViewsCount();
                            if (null != mTagThumbnailListTab4) {
                                mAdapterTab4.setList(mTagThumbnailListTab4);
                                listView.setAdapter(null);
                                listView.removeHeaderView(taHeard);
                                listView.addHeaderView(taHeard);
                                listView.setAdapter(mAdapterTab4);
                                listView.setSelection(i);
                            }
                        }
                    }
                }
                if(null!=mPullToRefreshListView){
                	mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }

            case FusionMessageType.UserMessageType.GET_TAGTHUMBNAILS_LIST_BY_ID_FAIL: {
                if(null!=mPullToRefreshListView){
                	mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }

            case UserMessageType.SET_RELATIONSHIP_BY_ID_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object) {
                    if (null != object.mObject) {
                        String action = (String) object.mObject;

                        if (!TextUtils.isEmpty(requestIdIsStar)
                                && requestIdIsStar.equals(object.mLocalRequestId)) {
                            setUser(action);
                        }
                    }
                }

                break;
            }
            case UserMessageType.SET_RELATIONSHIP_BY_ID_FAIL: {
                showToast(R.string.condition_setting_fail);
                break;
            }

            case FusionMessageType.ChatMessageType.CREATE_GROUP_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(createGroupId)
                        && createGroupId.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        GroupInfo mGroupInfo = (GroupInfo) object.mObject;
                        startActivity(new Intent(
                                FusionAction.ChatAction.CHAT_ACTION).putExtra(
                                FusionAction.ChatAction.GROUP_INFO, mGroupInfo));
                        finish();
                    }
                }
                break;
            }

            case FusionMessageType.ChatMessageType.CREATE_GROUP_FAIL: {
                break;
            }

            case FusionMessageType.ChatMessageType.GET_CHAT_GROUP_FROM_DB: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(getGroupInfoFromDBId)
                        && getGroupInfoFromDBId.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        GroupInfo mGroupInfo = (GroupInfo) object.mObject;
                        startActivity(new Intent(
                                FusionAction.ChatAction.CHAT_ACTION).putExtra(
                                FusionAction.ChatAction.GROUP_INFO, mGroupInfo));
                        finish();
                    } else {
                        CreateGroup mCreateGroup = new CreateGroup();
                        mCreateGroup.name = mUser.name;
                        List<String> mlists = new ArrayList<String>();
                        mlists.add(uid);
                        mCreateGroup.members = mlists;
                        createGroupId = mIChatLogic.createGroup(mCreateGroup);
                    }
                }
                break;
            }
            case FusionMessageType.MBlogMessageType.IS_LIKES_ACTION_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && null!=mAdapterTab2 && !TextUtils.isEmpty(mAdapterTab2.likerequestId)
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
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ALIAS_REQUESTCODE: {

                if (null == data) {
                    return;
                }

                mUser.alias = data.getStringExtra(CommonField.ALIAS_KEY);

                setViewUser();

                break;
            }
            case ChatActivity.SHARE_TO_CHAT: {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, R.string.share_to_chat, Toast.LENGTH_LONG).show();
                }
                break;
            }
            case ChatActivity.SEND_NAMECARD_TO_CHAT: {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, R.string.share_to_chat, Toast.LENGTH_LONG).show();
                }
                break;
            }

            default:
        }
    }

    private void setUser(String action) {

        if (RelationshipAction.STAR_ACTION.equals(action)) {

            mUser.is_starring = true;

        } else if (RelationshipAction.UNSTAR_ACTION.equals(action)) {

            mUser.is_starring = false;
        }
    }

    private class InterestsGridViewAdapter extends BaseAdapter {

        private boolean isOpen = false;

        private List<Label> mlist = new ArrayList<Label>();

        @Override
        public int getCount() {
            if (isOpen) {
                if (mlist.size() % 3 == 0) {
                    return mlist.size() + 3;
                } else {
                    if (mlist.size() % 3 == 1) {
                        return mlist.size() + 2;
                    } else {
                        return mlist.size() + 1;
                    }
                }
            } else {
                if (mlist.size() < 7) {
                    return mlist.size();
                } else {
                    return 6;
                }
            }
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            final ImageLoaderViewHolder holder;
            if (convertView == null) {
                if (null == inflater) {
                    inflater = LayoutInflater.from(TaActivity.this);
                }
                convertView = inflater.inflate(R.layout.intent_gridview_item,
                        null);
                holder = new ImageLoaderViewHolder();
                holder.mTextView = (TextView) convertView
                        .findViewById(R.id.intent_name);
                holder.mTextView.setPadding(0, 0, 0, 0);
                holder.mTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                convertView.setTag(holder);
            } else {
                holder = (ImageLoaderViewHolder) convertView.getTag();
            }

            holder.mTextView.setOnClickListener(null);
            if (isOpen) {
                if (mlist.size() % 3 == 0) {
                    if (position == mlist.size() || position == mlist.size() + 1) {
                        holder.mTextView.setText("");
                        holder.mTextView.setBackgroundDrawable(getResources()
                                .getDrawable(R.drawable.profile_interest_btn));
                        holder.mTextView.setTextColor(getResources().getColor(
                                R.color.black));
                        holder.mTextView.setVisibility(View.VISIBLE);
                        return convertView;
                    }
                    if (position == mlist.size() + 2) {
                        holder.mTextView.setText("收起全部");
                        holder.mTextView.setBackgroundDrawable(getResources()
                                .getDrawable(R.drawable.profile_interest_btn));
                        holder.mTextView.setTextColor(getResources().getColor(
                                R.color.color_ffd800));
                        holder.mTextView.setVisibility(View.VISIBLE);
                        holder.mTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setOpen(false);
                                notifyDataSetChanged();
                            }
                        });
                        return convertView;
                    }
                    holder.mTextView.setText(mlist.get(position).name);
                    if (mlist.get(position).me) {
                        holder.mTextView.setBackgroundDrawable(getResources()
                                .getDrawable(R.drawable.profile_same_interest_btn));
                        holder.mTextView.setTextColor(getResources().getColor(
                                R.color.black));
                        holder.mTextView.setVisibility(View.VISIBLE);
                    } else {
                        holder.mTextView.setBackgroundDrawable(getResources()
                                .getDrawable(R.drawable.profile_interest_btn));
                        holder.mTextView.setTextColor(getResources().getColor(
                                R.color.black));
                        holder.mTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mlist.size() % 3 == 1) {
                        if (position == mlist.size()) {
                            holder.mTextView.setText("");
                            holder.mTextView.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.profile_interest_btn));
                            holder.mTextView.setTextColor(getResources().getColor(
                                    R.color.black));
                            holder.mTextView.setVisibility(View.VISIBLE);
                            return convertView;
                        }
                        if (position == mlist.size() + 1) {
                            holder.mTextView.setText("收起全部");
                            holder.mTextView.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.profile_interest_btn));
                            holder.mTextView.setTextColor(getResources().getColor(
                                    R.color.color_ffd800));
                            holder.mTextView.setVisibility(View.VISIBLE);
                            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    setOpen(false);
                                    notifyDataSetChanged();
                                }
                            });
                            return convertView;
                        }
                        holder.mTextView.setText(mlist.get(position).name);
                        if (mlist.get(position).me) {
                            holder.mTextView.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.profile_same_interest_btn));
                            holder.mTextView.setTextColor(getResources().getColor(
                                    R.color.black));
                            holder.mTextView.setVisibility(View.VISIBLE);
                        } else {
                            holder.mTextView.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.profile_interest_btn));
                            holder.mTextView.setTextColor(getResources().getColor(
                                    R.color.black));
                            holder.mTextView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (position == mlist.size()) {
                            holder.mTextView.setText("收起全部");
                            holder.mTextView.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.profile_interest_btn));
                            holder.mTextView.setTextColor(getResources().getColor(
                                    R.color.color_ffd800));
                            holder.mTextView.setVisibility(View.VISIBLE);
                            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    setOpen(false);
                                    notifyDataSetChanged();
                                }
                            });
                            return convertView;
                        }
                        holder.mTextView.setText(mlist.get(position).name);
                        if (mlist.get(position).me) {
                            holder.mTextView.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.profile_same_interest_btn));
                            holder.mTextView.setTextColor(getResources().getColor(
                                    R.color.black));
                            holder.mTextView.setVisibility(View.VISIBLE);
                        } else {
                            holder.mTextView.setBackgroundDrawable(getResources()
                                    .getDrawable(R.drawable.profile_interest_btn));
                            holder.mTextView.setTextColor(getResources().getColor(
                                    R.color.black));
                            holder.mTextView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } else {
                if (mlist.size() < 7) {
                    holder.mTextView.setText(mlist.get(position).name);
                    if (mlist.get(position).me) {
                        holder.mTextView.setBackgroundDrawable(getResources()
                                .getDrawable(R.drawable.profile_same_interest_btn));
                        holder.mTextView.setTextColor(getResources().getColor(
                                R.color.black));
                        holder.mTextView.setVisibility(View.VISIBLE);
                    } else {
                        holder.mTextView.setBackgroundDrawable(getResources()
                                .getDrawable(R.drawable.profile_interest_btn));
                        holder.mTextView.setTextColor(getResources().getColor(
                                R.color.black));
                        holder.mTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (position == 5) {
                        holder.mTextView.setText("展开全部");
                        holder.mTextView.setBackgroundDrawable(getResources()
                                .getDrawable(R.drawable.profile_interest_btn));
                        holder.mTextView.setTextColor(getResources().getColor(
                                R.color.color_ffd800));
                        holder.mTextView.setVisibility(View.VISIBLE);

                        holder.mTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setOpen(true);
                                notifyDataSetChanged();
                            }
                        });
                        return convertView;
                    }
                    holder.mTextView.setText(mlist.get(position).name);
                    if (mlist.get(position).me) {
                        holder.mTextView.setBackgroundDrawable(getResources()
                                .getDrawable(R.drawable.profile_same_interest_btn));
                        holder.mTextView.setTextColor(getResources().getColor(
                                R.color.black));
                        holder.mTextView.setVisibility(View.VISIBLE);
                    } else {
                        holder.mTextView.setBackgroundDrawable(getResources()
                                .getDrawable(R.drawable.profile_interest_btn));
                        holder.mTextView.setTextColor(getResources().getColor(
                                R.color.black));
                        holder.mTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
            return convertView;
        }

        public void setList(List<Label> mlist) {
            this.mlist = mlist;
        }

        public void setOpen(boolean isOpen) {
            this.isOpen = isOpen;
        }
    }
    
	@Override
	protected void onDestroy() {
		ImageLoader.getInstance().clearMemoryCache();
		super.onDestroy();
	}
}
