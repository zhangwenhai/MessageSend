package com.theone.sns.ui.discover;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.mblog.IMBlogLogic;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.user.IUserLogic;
import com.theone.sns.ui.base.BasicActivity;
import com.theone.sns.ui.mblog.AddListAdapter;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshBase;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshListView;

/**
 * Created by zhangwenhai on 2014/10/21.
 */
public class DiscoverListViewActivity extends BasicActivity {
	private IMBlogLogic mIMBlogLogic;
	private PullToRefreshListView mPullRefreshListView;
	private ListView mListView;
	private String searchWord;
	private String requestId;
	private List<User> mUserList = new ArrayList<User>();
	private AddListAdapter mAddListAdapter;
	private IUserLogic mIUserLogic;
	private View heardView;
	private TextView text;

	@Override
	protected void initLogics() {
		mIMBlogLogic = (IMBlogLogic) getLogicByInterfaceClass(IMBlogLogic.class);
		mIUserLogic = (IUserLogic) getLogicByInterfaceClass(IUserLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discover_search_item2_main);

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.list_view);
		mListView = mPullRefreshListView.getRefreshableView();

		heardView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dis_list_heard,
				null);
		text = (TextView) heardView.findViewById(R.id.text);

		mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
			@Override
			public void onRefresh() {
				requestId = mIMBlogLogic.findSearchUser(searchWord, null,
						FusionCode.CommonColumnsValue.COUNT_VALUE);
			}

			@Override
			public void onAddMore() {
				if (mUserList.size() > 0) {
					requestId = mIMBlogLogic.findSearchUser(searchWord,
							mUserList.get(mUserList.size() - 1).userId,
							FusionCode.CommonColumnsValue.COUNT_VALUE);
				} else {
					requestId = mIMBlogLogic.findSearchUser(searchWord, null,
							FusionCode.CommonColumnsValue.COUNT_VALUE);
				}
			}
		});

		searchWord = getIntent().getStringExtra(FusionAction.DiscoverAction.SEARCH_WORD);
		if (TextUtils.isEmpty(searchWord)) {
			finish();
		}

		requestId = mIMBlogLogic.findSearchUser(searchWord, null,
				FusionCode.CommonColumnsValue.COUNT_VALUE);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void handleStateMessage(Message msg) {
		switch (msg.what) {
		case FusionMessageType.MBlogMessageType.PULL_SEARCH_USER_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mUserList = (List<User>) object.mObject;
					if (null == mAddListAdapter) {
						mAddListAdapter = new AddListAdapter(
								DiscoverListViewActivity.this.getApplicationContext(), optionsForUserIcon,
								mIUserLogic);
						mListView.addHeaderView(heardView);
						text.setText(String.format(getString(R.string.find_user), mUserList.size(),
								searchWord));
						mAddListAdapter.setList(mUserList);
						mListView.setAdapter(mAddListAdapter);
					} else {
						text.setText(String.format(getString(R.string.find_user), mUserList.size(),
								searchWord));
						mAddListAdapter.setList(mUserList);
						mAddListAdapter.notifyDataSetChanged();
					}
				}
			}
			mPullRefreshListView.onRefreshComplete();
			break;
		}
		case FusionMessageType.MBlogMessageType.PUSH_SEARCH_USER_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mUserList.addAll((List<User>) object.mObject);
					if (null == mAddListAdapter) {
						mAddListAdapter = new AddListAdapter(
								DiscoverListViewActivity.this.getApplicationContext(), optionsForUserIcon,
								mIUserLogic);
						mListView.addHeaderView(heardView);
						text.setText(String.format(getString(R.string.find_user), mUserList.size(),
								searchWord));
						mAddListAdapter.setList(mUserList);
						mListView.setAdapter(mAddListAdapter);
					} else {
						text.setText(String.format(getString(R.string.find_user), mUserList.size(),
								searchWord));
						mAddListAdapter.setList(mUserList);
						mAddListAdapter.notifyDataSetChanged();
					}
				}
			}
			mPullRefreshListView.onRefreshComplete();
			break;
		}
		case FusionMessageType.MBlogMessageType.SEARCH_USER_NOT_FOUND: {
			mPullRefreshListView.onRefreshComplete();
			break;
		}

		default:
		}
	}
}
