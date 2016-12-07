package com.theone.sns.ui.me.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.component.location.gps.LocalLocation;
import com.theone.sns.component.location.gps.LocationManager;
import com.theone.sns.logic.account.IAccountLogic;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.mblog.IMBlogLogic;
import com.theone.sns.logic.model.chat.CreateGroup;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.mblog.MBlog;
import com.theone.sns.logic.model.mblog.MBlogTag;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.user.IUserLogic;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.me.MBlogListAdapterTab2;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshBase;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/9/28.
 */
public class MBlogListByTagActivity extends IphoneTitleActivity {

	private PullToRefreshListView mPullToRefreshListView;

	private ListView listView;

	private LocalLocation mLocation;

	private MBlogListAdapterTab2 mAdapterTab2;

	private List<MBlog> mBlogListTab2 = new ArrayList<MBlog>();

	private IUserLogic mIUserLogic;

	private String requestId;

	private String uid;

	private MBlogTag mMBlogTag;

	private String getGroupInfoFromDBId;
	private IChatLogic mIChatLogic;
	private String createGroupId;
	private PmOnClickListener mPmOnClickListener;

	private IMBlogLogic mIMBlogLogic;
	private IAccountLogic mIAccountLogic;

	private class PmOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View view) {
            showLoadingDialog();
			List<String> mlist = new ArrayList<String>();
			mlist.add(uid);
			getGroupInfoFromDBId = mIChatLogic.getGroupInfoFromDB(mlist);
		}
	}

	@Override
	protected void initLogics() {
		mIUserLogic = (IUserLogic) getLogicByInterfaceClass(IUserLogic.class);
		mIChatLogic = (IChatLogic) getLogicByInterfaceClass(IChatLogic.class);
		mIMBlogLogic = (IMBlogLogic) getLogicByInterfaceClass(IMBlogLogic.class);
		mIAccountLogic = (IAccountLogic) getLogicByInterfaceClass(IAccountLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.mblog_list_main);

		getView();

		setView();

		setListener();
	}

	private void getView() {
		mLocation = LocationManager.getInstance().getLocation();
		uid = getIntent().getStringExtra(FusionAction.MeAction.UID);
		mMBlogTag = (MBlogTag) getIntent().getSerializableExtra(FusionAction.MeAction.TAG);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.mblog_list);
		listView = mPullToRefreshListView.getRefreshableView();
		listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
	}

	private void setView() {
		setLeftButton(R.drawable.icon_back, false, false);

		mPmOnClickListener = new PmOnClickListener();
		mAdapterTab2 = new MBlogListAdapterTab2(this, options, mPmOnClickListener, mIMBlogLogic);
		mAdapterTab2.setList(mBlogListTab2);
		listView.setAdapter(mAdapterTab2);

		requestId = mIUserLogic.getMBlogListByUserIdAndTag(uid, mMBlogTag, null,
				FusionCode.CommonColumnsValue.COUNT_VALUE);
	}

	private void setListener() {
		mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
			@Override
			public void onRefresh() {
				requestId = mIUserLogic.getMBlogListByUserIdAndTag(uid, mMBlogTag, null,
						FusionCode.CommonColumnsValue.COUNT_VALUE);
			}

			@Override
			public void onAddMore() {
				if (mBlogListTab2.size() != 0) {
					requestId = mIUserLogic.getMBlogListByUserIdAndTag(uid, mMBlogTag, null,
							FusionCode.CommonColumnsValue.COUNT_VALUE);
				} else {
					requestId = mIUserLogic.getMBlogListByUserIdAndTag(uid, mMBlogTag,
							mBlogListTab2.get(mBlogListTab2.size() - 1)._id,
							FusionCode.CommonColumnsValue.COUNT_VALUE);
				}
			}
		});
	}

	@Override
	protected void handleStateMessage(Message msg) {
		switch (msg.what) {
		case FusionMessageType.UserMessageType.PULL_MBLOG_LIST_BY_ID_AND_TAG_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mBlogListTab2 = (List<MBlog>) object.mObject;
					if (null == mAdapterTab2) {
						mAdapterTab2 = new MBlogListAdapterTab2(this, options, mPmOnClickListener,
								mIMBlogLogic);
					}
					if (null != mBlogListTab2) {
						mAdapterTab2.setList(mBlogListTab2);
						mAdapterTab2.notifyDataSetChanged();
					}
				}
			}
			if (null != mPullToRefreshListView) {
				mPullToRefreshListView.onRefreshComplete();
			}
			break;
		}
		case FusionMessageType.UserMessageType.PUSH_MBLOG_LIST_BY_ID_AND_TAG_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mBlogListTab2.addAll((List<MBlog>) object.mObject);
					if (null == mAdapterTab2) {
						mAdapterTab2 = new MBlogListAdapterTab2(this, options, mPmOnClickListener,
								mIMBlogLogic);
					}
					if (null != mBlogListTab2) {
						mAdapterTab2.setList(mBlogListTab2);
						mAdapterTab2.notifyDataSetChanged();
					}
				}
			}
			if (null != mPullToRefreshListView) {
				mPullToRefreshListView.onRefreshComplete();
			}
			break;
		}
		case FusionMessageType.UserMessageType.GET_MBLOG_LIST_BY_ID__AND_TAG_FAIL: {
			if (null != mPullToRefreshListView) {
				mPullToRefreshListView.onRefreshComplete();
			}
			break;
		}
		case FusionMessageType.ChatMessageType.CREATE_GROUP_SUCCESS: {
            hideLoadingDialog();
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(createGroupId)
					&& createGroupId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					GroupInfo mGroupInfo = (GroupInfo) object.mObject;
					startActivity(new Intent(FusionAction.ChatAction.CHAT_ACTION).putExtra(
							FusionAction.ChatAction.GROUP_INFO, mGroupInfo));
					finish();
				}
			}
			break;
		}

		case FusionMessageType.ChatMessageType.CREATE_GROUP_FAIL: {
            hideLoadingDialog();
			break;
		}

		case FusionMessageType.ChatMessageType.GET_CHAT_GROUP_FROM_DB: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(getGroupInfoFromDBId)
					&& getGroupInfoFromDBId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
                    hideLoadingDialog();
					GroupInfo mGroupInfo = (GroupInfo) object.mObject;
					startActivity(new Intent(FusionAction.ChatAction.CHAT_ACTION).putExtra(
							FusionAction.ChatAction.GROUP_INFO, mGroupInfo));
					finish();
				} else {
					CreateGroup mCreateGroup = new CreateGroup();
					mCreateGroup.name = "";
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
			if (null != object && null != mAdapterTab2
					&& !TextUtils.isEmpty(mAdapterTab2.likerequestId)
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
}
