package com.theone.sns.ui.chat.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ListView;

import com.theone.sns.R;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionCode.CommonColumnsValue;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.model.chat.NotifyMe;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.chat.NotificationListAdapter;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshBase;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshListView;

/**
 * Created by zhangwenhai on 2014/11/6.
 */
public class NotificationListActivity extends IphoneTitleActivity {
	private PullToRefreshListView mPullRefreshListView;
	private ListView mListView;
	private NotificationListAdapter mNotificationListAdapter;
	private IChatLogic mIChatLogic;
	private String getNotifyActionMeId;
	private List<NotifyMe> notifyMeList = new ArrayList<NotifyMe>();

	@Override
	protected void initLogics() {
		mIChatLogic = (IChatLogic) getLogicByInterfaceClass(IChatLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.notification_list_main);

		getView();

		setView();

		setListener();
	}

	private void getView() {
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.list_view);
		mListView = mPullRefreshListView.getRefreshableView();
	}

	private void setView() {
		setTitle(R.string.notification);
		setLeftButton(R.drawable.icon_back, false, false);

		mNotificationListAdapter = new NotificationListAdapter(this, optionsForUserIcon);
		mListView.setAdapter(mNotificationListAdapter);
	}

	private void setListener() {
		mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
			@Override
			public void onRefresh() {
				getNotifyActionMeId = mIChatLogic.getNotifyActionMe(null,
						CommonColumnsValue.COUNT_VALUE);
			}

			@Override
			public void onAddMore() {
				if (notifyMeList.size() <= 0) {
					getNotifyActionMeId = mIChatLogic.getNotifyActionMe(null,
							CommonColumnsValue.COUNT_VALUE);
				} else {
					getNotifyActionMeId = mIChatLogic.getNotifyActionMe(
							notifyMeList.get(notifyMeList.size() - 1)._id,
							CommonColumnsValue.COUNT_VALUE);
				}
			}
		});

		showLoadingDialog();

		getNotifyActionMeId = mIChatLogic.getNotifyActionMe(null, CommonColumnsValue.COUNT_VALUE);
	}

	@Override
	protected void handleStateMessage(Message msg) {
		hideLoadingDialog();
		switch (msg.what) {
		case FusionMessageType.ChatMessageType.PULL_NOTIFY_ME_LIST_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(getNotifyActionMeId)
					&& getNotifyActionMeId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					notifyMeList = (List<NotifyMe>) object.mObject;
					if (null != mNotificationListAdapter) {
						mNotificationListAdapter.setNotifyMeList(notifyMeList);
						mNotificationListAdapter.notifyDataSetChanged();
					}
				}
			}
			mPullRefreshListView.onRefreshComplete();
			break;
		}

		case FusionMessageType.ChatMessageType.PUSH_NOTIFY_ME_LIST_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(getNotifyActionMeId)
					&& getNotifyActionMeId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					notifyMeList.addAll((List<NotifyMe>) object.mObject);
					if (null != mNotificationListAdapter) {
						mNotificationListAdapter.setNotifyMeList(notifyMeList);
						mNotificationListAdapter.notifyDataSetChanged();
					}
				}
			}
			mPullRefreshListView.onRefreshComplete();
			break;
		}
		case FusionMessageType.ChatMessageType.GET_NOTIFY_ME_LIST_FAIL: {
			mPullRefreshListView.onRefreshComplete();
			break;
		}

		default:
		}
	}

	@Override
	protected void onDestroy() {
		mIChatLogic.setNotifyBadge(FusionCode.SettingKey.NOTIFICATION_COUNT, 0);
		super.onDestroy();
	}
}
