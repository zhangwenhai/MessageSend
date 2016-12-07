package com.theone.sns.ui.me.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.model.user.User;

/**
 * Created by zhangwenhai on 2014/9/26.
 */
public class FollowListActivity extends UserListActivity {

	private String requestId;

	private List<User> mUserList = new ArrayList<User>();

	private String uid;

	@Override
	protected void sendRequest() {
		super.sendRequest();
		getFollowList();
	}
	
	private void getFollowList() {
		uid = getIntent().getStringExtra(FusionAction.MeAction.UID);
		if (TextUtils.isEmpty(uid)) {
			requestId = mIUserLogic.getRelationshipsList(FusionConfig
					.getInstance().getUserId(), null,
					FusionCode.CommonColumnsValue.COUNT_VALUE,
					FusionCode.Relationship.FOLLOWING);
		} else {
			requestId = mIUserLogic.getRelationshipsList(uid, null,
					FusionCode.CommonColumnsValue.COUNT_VALUE,
					FusionCode.Relationship.FOLLOWING);
		}
	}

	@Override
	protected void setUseListTitle() {
		m_titleView.setVisibility(View.GONE);
	}

	@Override
	protected void sendAddMoreRequest() {
		uid = getIntent().getStringExtra(FusionAction.MeAction.UID);
		if (mUserList.size() <= 0) {
			getFollowList();
		} else {
			if (TextUtils.isEmpty(uid)) {
				requestId = mIUserLogic.getRelationshipsList(
						FusionConfig.getInstance().getUserId(),
						mUserList.get(mUserList.size() - 1).userId,
						FusionCode.CommonColumnsValue.COUNT_VALUE,
						FusionCode.Relationship.FOLLOWING);
			} else {
				requestId = mIUserLogic.getRelationshipsList(uid,
						mUserList.get(mUserList.size() - 1).userId,
						FusionCode.CommonColumnsValue.COUNT_VALUE,
						FusionCode.Relationship.FOLLOWING);
			}
		}
	}

	@Override
	protected void sendRefreshRequest() {
		getFollowList();
	}

	@Override
	protected void handleStateMessage(Message msg) {
		super.handleStateMessage(msg);
		switch (msg.what) {
		case FusionMessageType.UserMessageType.PULL_RELATIONSHIPS_LIST_BY_ID_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mUserList = (List<User>) object.mObject;
					if (null == mUserListAdapter)
						mUserListAdapter = new UserListAdapter();
					mUserListAdapter.setmUserList(mUserList);
					mUserListAdapter.notifyDataSetChanged();
				}
			}
            if(null!=mPullToRefreshListView){
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
					if (null == mUserListAdapter)
						mUserListAdapter = new UserListAdapter();
					mUserListAdapter.setmUserList(mUserList);
					mUserListAdapter.notifyDataSetChanged();
				}
			}
            if(null!=mPullToRefreshListView){
            	mPullToRefreshListView.onRefreshComplete();
            }
			break;
		}
		case FusionMessageType.UserMessageType.GET_RELATIONSHIPS_LIST_BY_ID_FAIL: {
            if(null!=mPullToRefreshListView){
            	mPullToRefreshListView.onRefreshComplete();
            }
			break;
		}

		default:
		}
	}
}