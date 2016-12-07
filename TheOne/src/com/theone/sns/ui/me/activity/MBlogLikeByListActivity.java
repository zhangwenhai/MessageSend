package com.theone.sns.ui.me.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Message;
import android.text.TextUtils;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction.MBlogAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionMessageType.MBlogMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.model.user.User;

public class MBlogLikeByListActivity extends UserListActivity {

	private String requestId;

	private List<User> mUserList = new ArrayList<User>();

	private String mblog_id;

	@Override
	protected void sendRequest() {
		super.sendRequest();
		mblog_id = getIntent().getStringExtra(MBlogAction.MBLOG_ID);
		requestId = mIUserLogic.getLikesListByMBlogId(mblog_id, null,
				FusionCode.CommonColumnsValue.COUNT_VALUE);
	}

	@Override
	protected void setUseListTitle() {
		setTitle(R.string.like_by1);
	}

	@Override
	protected void sendAddMoreRequest() {
		mblog_id = getIntent().getStringExtra(MBlogAction.MBLOG_ID);
		if (mUserList.size() <= 0) {
			requestId = mIUserLogic.getLikesListByMBlogId(mblog_id, null,
					FusionCode.CommonColumnsValue.COUNT_VALUE);
		} else {
			requestId = mIUserLogic.getLikesListByMBlogId(mblog_id,
					mUserList.get(mUserList.size() - 1).userId,
					FusionCode.CommonColumnsValue.COUNT_VALUE);
		}
	}

	@Override
	protected void sendRefreshRequest() {
		requestId = mIUserLogic.getLikesListByMBlogId(mblog_id, null,
				FusionCode.CommonColumnsValue.COUNT_VALUE);
	}

	@Override
	protected void handleStateMessage(Message msg) {
		super.handleStateMessage(msg);
		switch (msg.what) {
		case MBlogMessageType.PULL_GET_LIKES_LIST_SUCCESS: {
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
		case MBlogMessageType.PUSH_GET_LIKES_LIST_SUCCESS: {
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
		case MBlogMessageType.GET_LIKES_LIST_FAIL: {
            if(null!=mPullToRefreshListView){
            	mPullToRefreshListView.onRefreshComplete();
            }
			break;
		}

		default:
		}
	}
}
