package com.theone.sns.ui.mblog.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.theone.sns.R;
import com.theone.sns.common.FusionCode.CommonColumnsValue;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.component.location.gps.ILocationListener;
import com.theone.sns.component.location.gps.LocalLocation;
import com.theone.sns.component.location.gps.LocationManager;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.user.IUserLogic;
import com.theone.sns.ui.base.BaseFragment;
import com.theone.sns.ui.mblog.PicGridAdapter;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshBase;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshGridView;

/**
 * Created by zhangwenhai on 2014/9/2.
 */
public class HeadmodelFragment extends BaseFragment {

	private LayoutInflater inflater;
	private int with;
	protected PullToRefreshGridView mPullRefreshGridView;
	protected GridView mGridView;
	protected PicGridAdapter mPicGridAdapter;
	private List<String> mlist = new ArrayList<String>();
	private List<User> mUserList = new ArrayList<User>();
	protected String requestId;
	protected String requestDBId;
	private View rootView;
	protected String type;
	private IUserLogic mIUserLogic;

	private LocalLocation mLocation;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		this.inflater = inflater;
		if (null == rootView) {
			rootView = inflater.inflate(R.layout.nearby_main, null);
			isNew = true;
			return rootView;
		}
		ViewGroup mViewGroup = (ViewGroup) rootView.getParent();
		if (null != mViewGroup) {
			mViewGroup.removeView(rootView);
		}
		isNew = false;
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (!isNew) {
			return;
		}
		getView(view);

		setView();

		sendRrequest();

		initLocation();
	}

	protected void sendRrequest() {
		
		requestDBId = mIUserLogic.findUserGalleryFromDB(type);
		
		requestId = mIUserLogic.findUserGallery(type, null, CommonColumnsValue.COUNT_VALUE);
	}

	protected void onRefresh() {
		mUserList.clear();
		requestId = mIUserLogic.findUserGallery(type, null, CommonColumnsValue.COUNT_VALUE);
	}

	protected void onAddMore() {
		if (mUserList.size() > 0) {
			requestId = mIUserLogic.findUserGallery(type,
					mUserList.get(mUserList.size() - 1).userId, CommonColumnsValue.COUNT_VALUE);
		} else {
			requestId = mIUserLogic.findUserGallery(type, null, CommonColumnsValue.COUNT_VALUE);
		}
	}

	private void getView(View view) {
		mPullRefreshGridView = (PullToRefreshGridView) view.findViewById(R.id.grid_view);
		mGridView = mPullRefreshGridView.getRefreshableView();

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
			@Override
			public void onRefresh() {
				HeadmodelFragment.this.onRefresh();
			}

			@Override
			public void onAddMore() {
				HeadmodelFragment.this.onAddMore();
			}
		});
	}

	private void setView() {
		with = getResources().getDisplayMetrics().widthPixels;

		mGridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false,
				true));
	}

	@Override
	protected void initLogics() {
		mIUserLogic = (IUserLogic) getLogicByInterfaceClass(IUserLogic.class);
	}

	private void initLocation() {

		mLocation = LocationManager.getInstance().getLocation();

		if (!FusionConfig.isInitLocation && null == mLocation) {

			LocationManager.getInstance().start(new ILocationListener() {

				@Override
				public void onResult(boolean result, LocalLocation location) {

					if (result && null != mPicGridAdapter) {

						mLocation = LocationManager.getInstance().getLocation();

						mPicGridAdapter.notifyDataSetChanged();
					}
				}
			});

			FusionConfig.isInitLocation = true;
		}
	}

	@Override
	protected void handleStateMessage(Message msg) {
		switch (msg.what) {
		case FusionMessageType.UserMessageType.GET_USER_LIST_FROM_DB: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestDBId)
					&& requestDBId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mUserList = (List<User>) object.mObject;
					if (null == mPicGridAdapter) {
						mPicGridAdapter = new PicGridAdapter(context, options,
								0);
						mPicGridAdapter.setList(mUserList);
						mGridView.setAdapter(mPicGridAdapter);
					} else {
						mPicGridAdapter.setList(mUserList);
						mPicGridAdapter.notifyDataSetChanged();
					}
				}
			}
			mPullRefreshGridView.onRefreshComplete();
			break;
		}
		case FusionMessageType.UserMessageType.PULL_USER_LIST_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mUserList = (List<User>) object.mObject;
					if (null == mPicGridAdapter) {
						mPicGridAdapter = new PicGridAdapter(context, options, 0);
						mPicGridAdapter.setList(mUserList);
						mGridView.setAdapter(mPicGridAdapter);
					} else {
						mPicGridAdapter.setList(mUserList);
						mPicGridAdapter.notifyDataSetChanged();
					}
				}
			}
			mPullRefreshGridView.onRefreshComplete();
			break;
		}
		case FusionMessageType.UserMessageType.PUSH_USER_LIST_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mUserList.addAll((List<User>) object.mObject);
					if (null == mPicGridAdapter) {
						mPicGridAdapter = new PicGridAdapter(context, options, 0);
						mPicGridAdapter.setList(mUserList);
						mGridView.setAdapter(mPicGridAdapter);
					} else {
						mPicGridAdapter.setList(mUserList);
						mPicGridAdapter.notifyDataSetChanged();
					}
				}
			}
			mPullRefreshGridView.onRefreshComplete();
			break;
		}
		case FusionMessageType.UserMessageType.GET_USER_LIST_FAIL: {
			mPullRefreshGridView.onRefreshComplete();
			break;
		}

		default:
		}
	}

	public void setType(String type) {
		this.type = type;
	}
}
