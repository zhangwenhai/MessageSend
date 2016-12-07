package com.theone.sns.ui.chat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.ListView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.user.IUserLogic;
import com.theone.sns.ui.base.BasicActivity;
import com.theone.sns.ui.chat.ChatSelectFriendAdapter;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshBase;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/10/29.
 */
public class SelectListViewActivity extends BasicActivity {

    private int selectType;
    private ListView selectFriendListView;
    private GroupInfo mGroupInfo;
    private IUserLogic mIUserLogic;
    private String requestId;
    private List<User> mUserList = new ArrayList<User>();
    private List<String> mMember = new ArrayList<String>();
    private ChatSelectFriendAdapter mChatSelectFriendAdapter;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (FusionAction.ChatAction.UPDATE_VIEW.equals(intent.getAction())) {
                if (null != mChatSelectFriendAdapter) {
                    mChatSelectFriendAdapter.notifyDataSetChanged();
                }
            }
        }
    };
    private PullToRefreshListView mPullToRefreshListView;

    @Override
    protected void initLogics() {
        mIUserLogic = (IUserLogic) getLogicByInterfaceClass(IUserLogic.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_friend_list_main);

        getView();

        setView();

        setListener();

        IntentFilter filter = new IntentFilter();
        filter.addAction(FusionAction.ChatAction.UPDATE_VIEW);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    }

    private void getView() {
        selectType = getIntent().getIntExtra(FusionAction.ChatAction.SELECT_TYPE, -1);
        if (-1 == selectType) {
            finish();
        }
        mGroupInfo = (GroupInfo) getIntent().getSerializableExtra(
                FusionAction.ChatAction.GROUP_INFO);
        if (null == mGroupInfo || null == mGroupInfo.members) {
            finish();
        }

        for (User mUser : mGroupInfo.members) {
            if (null != mUser) {
                mMember.add(mUser.userId);
            }
        }

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.select_friend_list_view);
        selectFriendListView = mPullToRefreshListView.getRefreshableView();
    }

    private void setView() {
        switch (selectType) {
            case 0: {
                requestId = mIUserLogic.getRelationshipsList(FusionConfig.getInstance().getUserId(),
                        null, FusionCode.CommonColumnsValue.COUNT_VALUE,
                        FusionCode.Relationship.FOLLOWING);
                break;
            }
            case 1: {
                requestId = mIUserLogic.getRelationshipsList(FusionConfig.getInstance().getUserId(),
                        null, FusionCode.CommonColumnsValue.COUNT_VALUE,
                        FusionCode.Relationship.STARRING);
                break;
            }
            case 2: {
                requestId = mIUserLogic.getRelationshipsList(FusionConfig.getInstance().getUserId(),
                        null, FusionCode.CommonColumnsValue.COUNT_VALUE,
                        FusionCode.Relationship.FOLLOWED_BY);
                break;
            }
            default:
        }

        mChatSelectFriendAdapter = new ChatSelectFriendAdapter(this, mMember, optionsForUserIcon,
                getIntent().getIntExtra(FusionAction.ChatAction.SELECT_TYPE_1, -1));
        selectFriendListView.setAdapter(mChatSelectFriendAdapter);
    }

    private void setListener() {
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (selectType) {
                    case 0: {
                        requestId = mIUserLogic.getRelationshipsList(FusionConfig.getInstance().getUserId(),
                                null, FusionCode.CommonColumnsValue.COUNT_VALUE,
                                FusionCode.Relationship.FOLLOWING);
                        break;
                    }
                    case 1: {
                        requestId = mIUserLogic.getRelationshipsList(FusionConfig.getInstance().getUserId(),
                                null, FusionCode.CommonColumnsValue.COUNT_VALUE,
                                FusionCode.Relationship.STARRING);
                        break;
                    }
                    case 2: {
                        requestId = mIUserLogic.getRelationshipsList(FusionConfig.getInstance().getUserId(),
                                null, FusionCode.CommonColumnsValue.COUNT_VALUE,
                                FusionCode.Relationship.FOLLOWED_BY);
                        break;
                    }
                    default:
                }
            }

            @Override
            public void onAddMore() {
                switch (selectType) {
                    case 0: {
                        if (mUserList.size() > 0) {
                            requestId = mIUserLogic.getRelationshipsList(FusionConfig.getInstance().getUserId(),
                                    mUserList.get(mUserList.size() - 1).userId, FusionCode.CommonColumnsValue.COUNT_VALUE,
                                    FusionCode.Relationship.FOLLOWING);
                        } else {
                            requestId = mIUserLogic.getRelationshipsList(FusionConfig.getInstance().getUserId(),
                                    null, FusionCode.CommonColumnsValue.COUNT_VALUE,
                                    FusionCode.Relationship.FOLLOWING);
                        }
                        break;
                    }
                    case 1: {
                        if (mUserList.size() > 0) {
                            requestId = mIUserLogic.getRelationshipsList(FusionConfig.getInstance().getUserId(),
                                    mUserList.get(mUserList.size() - 1).userId, FusionCode.CommonColumnsValue.COUNT_VALUE,
                                    FusionCode.Relationship.STARRING);
                        } else {
                            requestId = mIUserLogic.getRelationshipsList(FusionConfig.getInstance().getUserId(),
                                    null, FusionCode.CommonColumnsValue.COUNT_VALUE,
                                    FusionCode.Relationship.STARRING);
                        }
                        break;
                    }
                    case 2: {
                        if (mUserList.size() > 0) {
                            requestId = mIUserLogic.getRelationshipsList(FusionConfig.getInstance().getUserId(),
                                    mUserList.get(mUserList.size() - 1).userId, FusionCode.CommonColumnsValue.COUNT_VALUE,
                                    FusionCode.Relationship.FOLLOWED_BY);
                        } else {
                            requestId = mIUserLogic.getRelationshipsList(FusionConfig.getInstance().getUserId(),
                                    null, FusionCode.CommonColumnsValue.COUNT_VALUE,
                                    FusionCode.Relationship.FOLLOWED_BY);
                        }
                        break;
                    }
                    default:
                }
            }
        });
    }

    @Override
    protected void handleStateMessage(Message msg) {
        switch (msg.what) {
            case FusionMessageType.UserMessageType.PULL_RELATIONSHIPS_LIST_BY_ID_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(requestId)
                        && requestId.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        mUserList = (List<User>) object.mObject;
                        if (null == mChatSelectFriendAdapter) {
                            mChatSelectFriendAdapter = new ChatSelectFriendAdapter(this, mMember,
                                    optionsForUserIcon, getIntent().getIntExtra(
                                    FusionAction.ChatAction.SELECT_TYPE_1, -1));
                            selectFriendListView.setAdapter(mChatSelectFriendAdapter);
                        }
                        mChatSelectFriendAdapter.setmList(mUserList);
                        mChatSelectFriendAdapter.notifyDataSetChanged();
                    }
                }
                if (null != mPullToRefreshListView) {
                    mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }
            case FusionMessageType.UserMessageType.PUSH_RELATIONSHIPS_LIST_BY_ID_SUCCESS: {
                UIObject object = (UIObject) msg.obj;
                if (null != object && !TextUtils.isEmpty(requestId)
                        && requestId.equals(object.mLocalRequestId)) {
                    if (null != object.mObject) {
                        mUserList.addAll((List<User>) object.mObject);
                        if (null == mChatSelectFriendAdapter) {
                            mChatSelectFriendAdapter = new ChatSelectFriendAdapter(this, mMember,
                                    optionsForUserIcon, getIntent().getIntExtra(
                                    FusionAction.ChatAction.SELECT_TYPE_1, -1));
                            selectFriendListView.setAdapter(mChatSelectFriendAdapter);
                        }
                        mChatSelectFriendAdapter.setmList(mUserList);
                        mChatSelectFriendAdapter.notifyDataSetChanged();
                    }
                }
                if (null != mPullToRefreshListView) {
                    mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }
            case FusionMessageType.UserMessageType.GET_RELATIONSHIPS_LIST_BY_ID_FAIL: {
                if (null != mPullToRefreshListView) {
                    mPullToRefreshListView.onRefreshComplete();
                }
                break;
            }
            default:
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
