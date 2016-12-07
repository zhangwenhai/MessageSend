package com.theone.sns.ui.mblog.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionAction.MBlogAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.user.IUserLogic;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.mblog.AddListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

/**
 * Created by zhangwenhai on 2014/9/21.
 */
public class AddFriendActivity extends IphoneTitleActivity implements
        PlatformActionListener {

    private ListView addFriendList;
    private RelativeLayout phoneView;
    private RelativeLayout wechatView;
    private RelativeLayout xinlangView;
    private TextView otherRecommendationsView;
    private IUserLogic mIUserLogic;
    private String requestId;
    private List<User> mUserList = new ArrayList<User>();
    private AddListAdapter mAddListAdapter;
    private LayoutInflater inflater;
    private int with;
    private EditText searchBox;
    public String searchRequestId;
    private View addFriendHead;
    private LinearLayout headView;
    private List<User> mSearchUserList = new ArrayList<User>();
    public static boolean isTouch = false;

    private Platform plat = null;

    @Override
    protected void initLogics() {
        mIUserLogic = (IUserLogic) getLogicByInterfaceClass(IUserLogic.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSubContent(R.layout.add_friend_main);

        setPlatformInfo();

        getView();

        setView();

        setListener();

        requestId = mIUserLogic.getRecommendUserList();
    }

    private void setPlatformInfo() {
    }

    private void getView() {
        with = getResources().getDisplayMetrics().widthPixels;
        addFriendHead = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.add_friend_head, null);
        addFriendList = (ListView) findViewById(R.id.add_friend_list);

        phoneView = (RelativeLayout) addFriendHead
                .findViewById(R.id.phone_view);
        wechatView = (RelativeLayout) addFriendHead
                .findViewById(R.id.wechat_view);
        xinlangView = (RelativeLayout) addFriendHead
                .findViewById(R.id.xinlang_view);
        headView = (LinearLayout) addFriendHead.findViewById(R.id.head_view1);

        searchBox = (EditText) addFriendHead.findViewById(R.id.search_box);

        otherRecommendationsView = (TextView) addFriendHead
                .findViewById(R.id.other_recommendations);
    }

    private void setView() {
        setTitle(R.string.add_friend);
        setLeftButton(R.drawable.icon_back, false, false);

        mAddListAdapter = new AddListAdapter(
                AddFriendActivity.this.getApplicationContext(),
                optionsForUserIcon, mIUserLogic);
        addFriendList.addHeaderView(addFriendHead);
        addFriendList.setAdapter(mAddListAdapter);
    }

    private void setListener() {
        searchBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i,
                                          int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2,
                                      int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString())
                        && null != mAddListAdapter) {
                    headView.setVisibility(View.VISIBLE);
                    mAddListAdapter.setList(mUserList);
                    mAddListAdapter.notifyDataSetChanged();
                } else if (null != mAddListAdapter) {
                    searchRequestId = mIUserLogic.getUserListBySearch(
                            editable.toString(), null,
                            FusionCode.CommonColumnsValue.COUNT_VALUE);
                }
            }
        });

        phoneView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(MBlogAction.CONTACT_FRIEND_ACTION));
            }
        });

        wechatView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                plat = ShareSDK.getPlatform("Wechat");

                plat.setPlatformActionListener(AddFriendActivity.this);

                plat.share(getShareParams());
            }
        });

        xinlangView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                plat = ShareSDK.getPlatform("SinaWeibo");

                plat.setPlatformActionListener(AddFriendActivity.this);

                plat.share(getShareParams());
            }
        });

        otherRecommendationsView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showLoadingDialog();

                requestId = mIUserLogic.getRecommendUserList();
            }
        });
    }

    private ShareParams getShareParams() {

        ShareParams sp = new ShareParams();

        sp.setTitle(getString(R.string.app_name));

        sp.setShareType(Platform.SHARE_IMAGE);

        sp.setText(getString(R.string.invite_content));

        sp.setImageData(BitmapFactory.decodeResource(getResources(),
                R.drawable.share_invite_friend));

        return sp;
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideIME(searchBox, true);
    }

    @Override
    public void finish() {
        if (isTouch) {
            LocalBroadcastManager.getInstance(AddFriendActivity.this)
                    .sendBroadcast(
                            new Intent(FusionAction.TheOneApp.UPDATE));
        }
        super.finish();
    }

    @Override
    protected void handleStateMessage(Message msg) {
        hideLoadingDialog();
        switch (msg.what) {
            case FusionMessageType.UserMessageType.GET_RECOMMEND_USER_LIST_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(requestId)
                        && requestId.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        mUserList = (List<User>) object.mObject;
                        if (null == mAddListAdapter)
                            mAddListAdapter = new AddListAdapter(
                                    AddFriendActivity.this.getApplicationContext(),
                                    optionsForUserIcon, mIUserLogic);
                        mAddListAdapter.setList(mUserList);
                        mAddListAdapter.notifyDataSetChanged();
                    }
                }
                break;
            }

            case FusionMessageType.UserMessageType.PULL_SEARCH_USER_LIST_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(searchRequestId)
                        && searchRequestId.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        mSearchUserList = (List<User>) object.mObject;
                        if (null == mAddListAdapter)
                            mAddListAdapter = new AddListAdapter(
                                    AddFriendActivity.this.getApplicationContext(),
                                    optionsForUserIcon, mIUserLogic);
                        headView.setVisibility(View.GONE);
                        mAddListAdapter.setList(mSearchUserList);
                        mAddListAdapter.notifyDataSetChanged();
                    }
                }
                break;
            }

            default:
        }
    }

    @Override
    public void onCancel(Platform arg0, int arg1) {
    }

    @Override
    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
    }

    @Override
    public void onError(Platform arg0, int arg1, Throwable arg2) {

        String expName = arg2.getClass().getSimpleName();

        if ("WechatClientNotExistException".equals(expName)
                || "WechatTimelineNotSupportedException".equals(expName)
                || "WechatFavoriteNotSupportedException".equals(expName)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showToast(getResources().getString(
                            R.string.wechat_client_inavailable));
                }
            });
        }
    }

    // private class AddListAdapter extends BaseAdapter {
    //
    // private List<User> mUserList = new ArrayList<User>();
    //
    // @Override
    // public int getCount() {
    // return mUserList.size();
    // }
    //
    // @Override
    // public Object getItem(int i) {
    // return i;
    // }
    //
    // @Override
    // public long getItemId(int i) {
    // return i;
    // }
    //
    // @Override
    // public View getView(int i, View view, ViewGroup viewGroup) {
    // final ImageLoaderViewHolder holder;
    // if (view == null) {
    // if (null == inflater) {
    // inflater = LayoutInflater.from(getApplicationContext());
    // }
    // view = inflater.inflate(R.layout.add_friend_item, null);
    // holder = new ImageLoaderViewHolder();
    // holder.imageView = (ImageView) view.findViewById(R.id.avatar);
    // holder.mTextView = (TextView) view.findViewById(R.id.name);
    // holder.imageView1 = (ImageView) view.findViewById(R.id.role);
    // holder.imageView2 = (ImageView) view.findViewById(R.id.xinghun);
    // holder.imageView3 = (ImageView) view.findViewById(R.id.xingxing);
    // holder.mButton = (Button) view.findViewById(R.id.follow_btn);
    // holder.imageView4 = (ImageView) view.findViewById(R.id.image1);
    // holder.imageView5 = (ImageView) view.findViewById(R.id.image2);
    // holder.imageView6 = (ImageView) view.findViewById(R.id.image3);
    //
    // LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
    // (int) (with - HelperFunc.dip2px(24)) / 3,
    // (int) (with - HelperFunc.dip2px(24)) / 3);
    // holder.imageView4.setLayoutParams(lp);
    // holder.imageView5.setLayoutParams(lp);
    // holder.imageView6.setLayoutParams(lp);
    // view.setTag(holder);
    // } else {
    // holder = (ImageLoaderViewHolder) view.getTag();
    // }
    // final User mUser = mUserList.get(i);
    // ImageLoader.getInstance().displayImage(mUser.avatar_url,
    // holder.imageView, options);
    // holder.mTextView.setText(mUser.name);
    //
    // if (FusionCode.Role.H.equals(mUser.role)) {
    // holder.imageView1.setImageDrawable(getResources().getDrawable(
    // R.drawable.home_h_icon));
    // } else if (FusionCode.Role.T.equals(mUser.role)) {
    // holder.imageView1.setImageDrawable(getResources().getDrawable(
    // R.drawable.home_t_icon));
    // } else if (FusionCode.Role.P.equals(mUser.role)) {
    // holder.imageView1.setImageDrawable(getResources().getDrawable(
    // R.drawable.home_p_icon));
    // } else if (FusionCode.Role.MH.equals(mUser.role)) {
    // holder.imageView1.setImageDrawable(getResources().getDrawable(
    // R.drawable.home_0_5_icon));
    // } else if (FusionCode.Role.MT.equals(mUser.role)) {
    // holder.imageView1.setImageDrawable(getResources().getDrawable(
    // R.drawable.home_1_icon));
    // } else if (FusionCode.Role.MP.equals(mUser.role)) {
    // holder.imageView1.setImageDrawable(getResources().getDrawable(
    // R.drawable.home_0_icon));
    // }
    // if (mUser.marriage) {
    // holder.imageView2.setVisibility(View.VISIBLE);
    // } else {
    // holder.imageView2.setVisibility(View.GONE);
    // }
    // if (mUser.is_starring) {
    // holder.imageView3.setVisibility(View.VISIBLE);
    // } else {
    // holder.imageView3.setVisibility(View.GONE);
    // }
    // if (mUser.is_following) {
    // holder.mButton.setText(getString(R.string.unfollow));
    // holder.mButton.setTextColor(getResources().getColor(R.color.black));
    // holder.mButton.setBackgroundDrawable(getResources().getDrawable(
    // R.drawable.home_attention_highlight_btn));
    // } else {
    // holder.mButton.setText(getString(R.string.follow));
    // holder.mButton.setTextColor(getResources().getColor(R.color.white));
    // holder.mButton.setBackgroundDrawable(getResources().getDrawable(
    // R.drawable.home_attention_btn));
    // }
    // holder.mButton.setOnClickListener(new View.OnClickListener() {
    // @Override
    // public void onClick(View view) {
    // if (getString(R.string.follow).equals(
    // (String) holder.mButton.getText().toString())) {
    // holder.mButton.setText(getString(R.string.unfollow));
    // holder.mButton.setTextColor(getResources().getColor(R.color.black));
    // holder.mButton.setBackgroundDrawable(getResources().getDrawable(
    // R.drawable.home_attention_highlight_btn));
    // isFollowUser(mUser.userId, true);
    // } else {
    // holder.mButton.setText(getString(R.string.follow));
    // holder.mButton.setTextColor(getResources().getColor(R.color.white));
    // holder.mButton.setBackgroundDrawable(getResources().getDrawable(
    // R.drawable.home_attention_btn));
    // isFollowUser(mUser.userId, false);
    // }
    // }
    // });
    //
    // holder.imageView4.setVisibility(View.GONE);
    // holder.imageView5.setVisibility(View.GONE);
    // holder.imageView6.setVisibility(View.GONE);
    //
    // if (null != mUser.media && mUser.media.size() > 0 && null !=
    // mUser.media.get(0)) {
    // ImageLoader.getInstance().displayImage(mUser.media.get(0).url,
    // holder.imageView4,
    // options);
    // holder.imageView4.setVisibility(View.VISIBLE);
    // holder.imageView4.setOnClickListener(new
    // GotoMblogCommentActivityOnClickListener(
    // getApplicationContext(), mUser.media.get(0)._id));
    // }
    // if (null != mUser.media && mUser.media.size() > 1 && null !=
    // mUser.media.get(1)) {
    // ImageLoader.getInstance().displayImage(mUser.media.get(1).url,
    // holder.imageView5,
    // options);
    // holder.imageView5.setVisibility(View.VISIBLE);
    // holder.imageView5.setOnClickListener(new
    // GotoMblogCommentActivityOnClickListener(
    // getApplicationContext(), mUser.media.get(1)._id));
    // }
    // if (null != mUser.media && mUser.media.size() > 2 && null !=
    // mUser.media.get(2)) {
    // ImageLoader.getInstance().displayImage(mUser.media.get(2).url,
    // holder.imageView6,
    // options);
    // holder.imageView6.setVisibility(View.VISIBLE);
    // holder.imageView6.setOnClickListener(new
    // GotoMblogCommentActivityOnClickListener(
    // getApplicationContext(), mUser.media.get(2)._id));
    // }
    //
    // holder.imageView.setOnClickListener(new GotoTaActivityOnClickListener(
    // getApplicationContext(), mUser.userId));
    //
    // return view;
    // }
    //
    // public void setList(List<User> mUserList) {
    // this.mUserList = mUserList;
    // }
    // }

}
