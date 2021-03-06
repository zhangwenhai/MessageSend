package com.theone.sns.ui.me.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionCode.RelationshipAction;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

/**
 * Created by zhangwenhai on 2014/9/26.
 */
public class StarringListActivity extends UserListActivity {

	private String requestId;

	private List<User> mUserList = new ArrayList<User>();

	private String uid;

	@Override
	protected void sendRequest() {
		super.sendRequest();
		getStarringList();
	}
	
	private void getStarringList() {
		uid = getIntent().getStringExtra(FusionAction.MeAction.UID);
		if (TextUtils.isEmpty(uid)) {
			requestId = mIUserLogic.getRelationshipsList(FusionConfig
					.getInstance().getUserId(), null,
					FusionCode.CommonColumnsValue.COUNT_VALUE,
					FusionCode.Relationship.STARRING);
		} else {
			requestId = mIUserLogic.getRelationshipsList(uid, null,
					FusionCode.CommonColumnsValue.COUNT_VALUE,
					FusionCode.Relationship.STARRING);
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
			getStarringList();
		} else {
			if (TextUtils.isEmpty(uid)) {
				requestId = mIUserLogic
						.getRelationshipsList(FusionConfig.getInstance().getUserId(),
								mUserList.get(mUserList.size() - 1).userId,
								FusionCode.CommonColumnsValue.COUNT_VALUE,
								FusionCode.Relationship.STARRING);
			} else {
				requestId = mIUserLogic
						.getRelationshipsList(uid, mUserList.get(mUserList.size() - 1).userId,
								FusionCode.CommonColumnsValue.COUNT_VALUE,
								FusionCode.Relationship.STARRING);
			}
		}
	}

	@Override
	protected void sendRefreshRequest() {
		getStarringList();
	}

	@Override
	protected void showButton(final User mUser, final ImageLoaderViewHolder holder) {

		if (mUser.is_starring) {
			holder.mButton.setText(getString(R.string.unstar));
			holder.mButton.setTextColor(getResources().getColor(R.color.black));
			holder.mButton.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.home_attention_highlight_btn));
		} else {
			holder.mButton.setText(getString(R.string.star));
			holder.mButton.setTextColor(getResources().getColor(R.color.white));
			holder.mButton.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.home_attention_btn));
		}

		holder.mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (getString(R.string.star).equals((String) holder.mButton.getText().toString())) {
					holder.mButton.setText(getString(R.string.unstar));
					holder.mButton.setTextColor(getResources().getColor(R.color.black));
					holder.mButton.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.home_attention_highlight_btn));
					buttonAction(mUser.userId, true);
				} else {
					holder.mButton.setText(getString(R.string.star));
					holder.mButton.setTextColor(getResources().getColor(R.color.white));
					holder.mButton.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.home_attention_btn));
					buttonAction(mUser.userId, false);
				}
			}
		});
	}

	protected void buttonAction(String userId, boolean isAction) {

		String action = null;

		if (isAction) {

			action = RelationshipAction.STAR_ACTION;

		} else {

			action = RelationshipAction.UNSTAR_ACTION;
		}

		mIUserLogic.setUserRelationship(userId, action);
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
