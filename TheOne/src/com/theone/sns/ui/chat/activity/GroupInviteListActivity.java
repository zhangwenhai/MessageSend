package com.theone.sns.ui.chat.activity;

import java.util.ArrayList;
import java.util.List;

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
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.chat.IPushListener;
import com.theone.sns.logic.chat.impl.ChatManager;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.chat.GroupInviteListAdapter;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshBase;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshListView;

/**
 * Created by zhangwenhai on 2014/11/6.
 */
public class GroupInviteListActivity extends IphoneTitleActivity implements IPushListener {

	private ListView mListView;
	private GroupInviteListAdapter mGroupInviteListAdapter;
	private IChatLogic mIChatLogic;
	private String getAllUnJoinedGroupFromDBId;
	private List<GroupInfo> mGroupInfoList = new ArrayList<GroupInfo>();

	@Override
	protected void initLogics() {
		mIChatLogic = (IChatLogic) getLogicByInterfaceClass(IChatLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.group_invite_list_main);

		getView();

		setView();

		setListener();

		ChatManager.getInstance().addListener(this);
	}

	private void getView() {
		mListView = (ListView) findViewById(R.id.list_view);
	}

	private void setView() {
		setTitle(R.string.group_invite);
		setLeftButton(R.drawable.icon_back, false, false);
		setRightButton(R.string.clear, true);

		mGroupInviteListAdapter = new GroupInviteListAdapter(this, optionsForUserIcon, mIChatLogic);
		mListView.setAdapter(mGroupInviteListAdapter);
	}

	private void setListener() {
		getRightButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mGroupInfoList.size() <= 0) {
					return;
				}
				for (GroupInfo mGroupInfo : mGroupInfoList) {
					mIChatLogic.deleteUnjoinedGroup(mGroupInfo._id);
				}
				getAllUnJoinedGroupFromDBId = mIChatLogic.getAllUnJoinedGroupFromDB();
			}
		});

		getAllUnJoinedGroupFromDBId = mIChatLogic.getAllUnJoinedGroupFromDB();
	}

	@Override
	protected void handleStateMessage(Message msg) {
		hideLoadingDialog();
		switch (msg.what) {
		case FusionMessageType.ChatMessageType.GET_UNJOINED_GROUP_LIST_FROM_DB: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(getAllUnJoinedGroupFromDBId)
					&& getAllUnJoinedGroupFromDBId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mGroupInfoList = (List<GroupInfo>) object.mObject;
					if (null != mGroupInviteListAdapter) {
						mGroupInviteListAdapter.setmGroupInfoList(mGroupInfoList);
						mGroupInviteListAdapter.notifyDataSetChanged();
					}
				}
			}
			break;
		}
		case FusionMessageType.ChatMessageType.DELETE_UNJOINED_GROUP_SUCCESS: {
			getAllUnJoinedGroupFromDBId = mIChatLogic.getAllUnJoinedGroupFromDB();
			break;
		}
		default:
		}
	}

	@Override
	public void push(int what, Object object) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				hideLoadingDialog();
			}
		});
		switch (what) {
		case IPushListener.GROUP_CHANGE: {
			getAllUnJoinedGroupFromDBId = mIChatLogic.getAllUnJoinedGroupFromDB();
			break;
		}
		default:
		}
	}

	@Override
	protected void onDestroy() {
		mIChatLogic.setNotifyBadge(FusionCode.SettingKey.GROUP_INVITE_COUNT, 0);
		ChatManager.getInstance().removeListener(this);
		super.onDestroy();
	}
}
