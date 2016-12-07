package com.theone.sns.ui.publish.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionAction.PublicAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionCode.CommonColumnsValue;
import com.theone.sns.common.FusionCode.Relationship;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType.UserMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.user.IUserLogic;
import com.theone.sns.ui.base.BasicActivity;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshBase;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshListView;

public class CircleLabelListActivity extends BasicActivity {

	private PullToRefreshListView mPullToRefreshListView;

	private ListView listview;

	private LayoutInflater inflater;

	private LabelListAdapter mLabelListAdapter;

	private Button cancelBtn;

	private String requestId;

	private IUserLogic mIUserLogic;

	private List<User> mUserList = new ArrayList<User>();

	private List<User> mSearchList = new ArrayList<User>();

	private EditText searchBox;

	private String circleSearchRequestId;

	private boolean isSearch = false;

	@Override
	protected void initLogics() {

		mIUserLogic = (IUserLogic) getLogicByInterfaceClass(IUserLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_label_list_main);
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_behind);

		getView();

		setView();
	}

	private void getView() {

		int type = getIntent().getIntExtra(FusionAction.PublicAction.TYPE, -1);

		if (AddLabelActivity.FIGURE_REQUESTCODE != type) {
			finish();
			return;
		}

		inflater = LayoutInflater.from(CircleLabelListActivity.this);

		cancelBtn = (Button) findViewById(R.id.cancel_btn);

		searchBox = (EditText) findViewById(R.id.search_box);

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.listview_label);

		listview = mPullToRefreshListView.getRefreshableView();
	}

	private void setView() {

		mLabelListAdapter = new LabelListAdapter(optionsForUserIcon);

		listview.setAdapter(mLabelListAdapter);

		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

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
				if (null != mLabelListAdapter)
					mLabelListAdapter.search(editable.toString());
			}
		});

		mPullToRefreshListView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
					@Override
					public void onRefresh() {
						requestId = mIUserLogic.getRelationshipsList(
								FusionConfig.getInstance().getUserId(), null,
								FusionCode.CommonColumnsValue.COUNT_VALUE,
								Relationship.FOLLOWING);
					}

					@Override
					public void onAddMore() {

						if (isSearch) {

							if (mSearchList.size() != 0) {

								circleSearchRequestId = mIUserLogic
										.getCircleSearch(searchBox.getText()
												.toString(),
												mSearchList.get(mSearchList
														.size() - 1).userId,
												CommonColumnsValue.COUNT_VALUE,
												Relationship.FOLLOWING);
							} else {

								circleSearchRequestId = mIUserLogic
										.getCircleSearch(searchBox.getText()
												.toString(), null,
												CommonColumnsValue.COUNT_VALUE,
												Relationship.FOLLOWING);
							}

						} else {

							if (mUserList.size() != 0) {
								requestId = mIUserLogic
										.getRelationshipsList(
												FusionConfig.getInstance()
														.getUserId(),
												mUserList.get(mUserList.size() - 1).userId,
												FusionCode.CommonColumnsValue.COUNT_VALUE,
												Relationship.FOLLOWING);
							} else {
								requestId = mIUserLogic
										.getRelationshipsList(
												FusionConfig.getInstance()
														.getUserId(),
												null,
												FusionCode.CommonColumnsValue.COUNT_VALUE,
												Relationship.FOLLOWING);
							}
						}
					}
				});

		requestId = mIUserLogic.getRelationshipsList(FusionConfig.getInstance()
				.getUserId(), null, FusionCode.CommonColumnsValue.COUNT_VALUE,
				Relationship.FOLLOWING);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.push_down_behind, R.anim.push_down);
	}

	private class LabelListAdapter extends BaseAdapter {

		private List<User> mList = new ArrayList<User>();

		private List<User> searchList = new ArrayList<User>();

		private DisplayImageOptions optionsForUserIcon;

		public LabelListAdapter(DisplayImageOptions optionsForUserIcon) {

			this.optionsForUserIcon = optionsForUserIcon;
		}

		@Override
		public int getCount() {
			if (isSearch)
				return searchList.size();
			return mList.size();
		}

		@Override
		public Object getItem(int i) {
			return i;
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(final int i, View view, ViewGroup viewGroup) {
			final ImageLoaderViewHolder holder;
			if (view == null) {
				holder = new ImageLoaderViewHolder();
				view = inflater.inflate(R.layout.circle_label_list_item, null);
				holder.mTextView = (TextView) view
						.findViewById(R.id.label_name);
				holder.imageView1 = (ImageView) view
						.findViewById(R.id.image_view);

				view.setTag(holder);
			} else {
				holder = (ImageLoaderViewHolder) view.getTag();
			}

			holder.imageView1.setVisibility(View.VISIBLE);

			if (isSearch) {
				holder.mTextView.setText(searchList.get(i).name);
				ImageLoader.getInstance().displayImage(
						searchList.get(i).avatar_url, holder.imageView1,
						optionsForUserIcon);
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent mIntent = new Intent();
						if ((getString(R.string.new_add_label) + searchBox
								.getText().toString()).equals(searchList.get(i)))
							mIntent.putExtra(
									FusionAction.PublicAction.LABEL_NAME,
									searchBox.getText().toString());
						else {

							Bundle bundle = new Bundle();

							bundle.putString(PublicAction.LABEL_USER_NAME,
									searchList.get(i).name);

							bundle.putString(PublicAction.LABEL_USER_ID,
									searchList.get(i).userId);

							mIntent.putExtra(
									FusionAction.PublicAction.LABEL_NAME,
									bundle);
						}
						setResult(RESULT_OK, mIntent);
						finish();
					}
				});
			} else {
				holder.mTextView.setText(mList.get(i).name);
				ImageLoader.getInstance().displayImage(mList.get(i).avatar_url,
						holder.imageView1, optionsForUserIcon);
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent mIntent = new Intent();

						Bundle bundle = new Bundle();

						bundle.putString(PublicAction.LABEL_USER_NAME,
								mList.get(i).name);

						bundle.putString(PublicAction.LABEL_USER_ID,
								mList.get(i).userId);

						mIntent.putExtra(FusionAction.PublicAction.LABEL_NAME,
								bundle);

						setResult(RESULT_OK, mIntent);
						finish();
					}
				});
			}
			return view;
		}

		public void setUserList(List<User> userList) {

			if (null == userList || userList.size() == 0) {

				return;
			}

			mList.clear();

			mList.addAll(userList);

			if (!TextUtils.isEmpty(searchBox.getText().toString())) {
				search(searchBox.getText().toString());
			} else {
				notifyDataSetChanged();
			}
		}

		private void search(String s) {

			searchList.clear();

			if (TextUtils.isEmpty(s)) {

				isSearch = false;

			} else {

				isSearch = true;

				circleSearchRequestId = mIUserLogic.getCircleSearch(s, null,
						CommonColumnsValue.COUNT_VALUE, Relationship.FOLLOWING);
			}

			notifyDataSetChanged();
		}

		private void showSearchResult(List<User> userTag) {

			if (null == userTag || userTag.size() == 0) {
				return;
			}

			searchList = userTag;

			notifyDataSetChanged();
		}
	}

	@Override
	protected void handleStateMessage(Message msg) {
		switch (msg.what) {
		case UserMessageType.PULL_RELATIONSHIPS_LIST_BY_ID_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {

					List<User> userList = (List<User>) object.mObject;

					mUserList = userList;

					if (null == mLabelListAdapter) {
						mLabelListAdapter = new LabelListAdapter(
								optionsForUserIcon);
					}

					mLabelListAdapter.setUserList(mUserList);
				}
			}
            if(null!=mPullToRefreshListView){
            	mPullToRefreshListView.onRefreshComplete();
            }
			break;
		}
		case UserMessageType.PUSH_RELATIONSHIPS_LIST_BY_ID_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {

				if (null != object.mObject) {

					List<User> userList = (List<User>) object.mObject;

					mUserList.addAll(userList);

					if (null == mLabelListAdapter) {
						mLabelListAdapter = new LabelListAdapter(
								optionsForUserIcon);
					}

					mLabelListAdapter.setUserList(mUserList);
				}
			}
            if(null!=mPullToRefreshListView){
            	mPullToRefreshListView.onRefreshComplete();
            }
			break;
		}
		case UserMessageType.GET_RELATIONSHIPS_LIST_BY_ID_FAIL: {
            if(null!=mPullToRefreshListView){
            	mPullToRefreshListView.onRefreshComplete();
            }
			break;
		}
		case UserMessageType.PULL_CIRCLE_SEARCH_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(circleSearchRequestId)
					&& circleSearchRequestId.equals(object.mLocalRequestId)) {

				if (null == mLabelListAdapter) {
					mLabelListAdapter = new LabelListAdapter(optionsForUserIcon);
				}

				mSearchList.clear();

				mSearchList.addAll((List<User>) object.mObject);

				mLabelListAdapter.showSearchResult(mSearchList);
			}
            if(null!=mPullToRefreshListView){
            	mPullToRefreshListView.onRefreshComplete();
            }
			break;
		}
		case UserMessageType.PUSH_CIRCLE_SEARCH_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(circleSearchRequestId)
					&& circleSearchRequestId.equals(object.mLocalRequestId)) {

				if (null == mLabelListAdapter) {
					mLabelListAdapter = new LabelListAdapter(optionsForUserIcon);
				}

				mSearchList.addAll((List<User>) object.mObject);

				mLabelListAdapter.showSearchResult(mSearchList);
			}
            if(null!=mPullToRefreshListView){
            	mPullToRefreshListView.onRefreshComplete();
            }
			break;
		}
		case UserMessageType.CIRCLE_SEARCH_FAIL: {
            if(null!=mPullToRefreshListView){
            	mPullToRefreshListView.onRefreshComplete();
            }
			break;
		}
		}
	}
}
