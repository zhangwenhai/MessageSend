package com.theone.sns.ui.me.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Message;
import android.text.TextUtils;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.model.user.User;

/**
 * Created by zhangwenhai on 2014/9/26.
 */
public class FollowedByListActivity extends UserListActivity {

	private String requestId;

	private List<User> mUserList = new ArrayList<User>();

	private String uid;

	@Override
	protected void sendRequest() {
		super.sendRequest();
		getFollowedByList();
	}
	
	private void getFollowedByList() {
		uid = getIntent().getStringExtra(FusionAction.MeAction.UID);
		if (TextUtils.isEmpty(uid)) {
			requestId = mIUserLogic.getRelationshipsList(FusionConfig
					.getInstance().getUserId(), null,
					FusionCode.CommonColumnsValue.COUNT_VALUE,
					FusionCode.Relationship.FOLLOWED_BY);
		} else {
			requestId = mIUserLogic.getRelationshipsList(uid, null,
					FusionCode.CommonColumnsValue.COUNT_VALUE,
					FusionCode.Relationship.FOLLOWED_BY);
		}
	}

	@Override
	protected void setUseListTitle() {
		setTitle(R.string.followed_by);
	}

	@Override
	protected void sendAddMoreRequest() {
		uid = getIntent().getStringExtra(FusionAction.MeAction.UID);
		if (mUserList.size() <= 0) {
			getFollowedByList();
		} else {
			if (TextUtils.isEmpty(uid)) {
				requestId = mIUserLogic.getRelationshipsList(
						FusionConfig.getInstance().getUserId(),
						mUserList.get(mUserList.size() - 1).userId,
						FusionCode.CommonColumnsValue.COUNT_VALUE,
						FusionCode.Relationship.FOLLOWED_BY);
			} else {
				requestId = mIUserLogic.getRelationshipsList(uid,
						mUserList.get(mUserList.size() - 1).userId,
						FusionCode.CommonColumnsValue.COUNT_VALUE,
						FusionCode.Relationship.FOLLOWED_BY);
			}
		}
	}

	@Override
	protected void sendRefreshRequest() {
		getFollowedByList();
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
