package com.theone.sns.ui.discover;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.widget.GridView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.mblog.IMBlogLogic;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.model.user.base.Gallery;
import com.theone.sns.ui.base.BasicActivity;
import com.theone.sns.ui.mblog.PicGridAdapter;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshBase;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/10/21.
 */
public class DiscoverGridViewActivity extends BasicActivity {
	private IMBlogLogic mIMBlogLogic;
	private PullToRefreshGridView mPullRefreshGridView;
	private GridView mGridView;
	private String searchWord;
	private String requestId;
	private PicGridAdapter mPicGridAdapter;
	private List<Gallery> mGalleryList = new ArrayList<Gallery>();

	@Override
	protected void initLogics() {
		mIMBlogLogic = (IMBlogLogic) getLogicByInterfaceClass(IMBlogLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discover_search_item_main);

		mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.grid_view);
		mGridView = mPullRefreshGridView.getRefreshableView();

		mPullRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
			@Override
			public void onRefresh() {
				requestId = mIMBlogLogic.findSearchGallery(searchWord, null,
						FusionCode.CommonColumnsValue.COUNT_VALUE);
			}

			@Override
			public void onAddMore() {
				if (mGalleryList.size() > 0) {
					requestId = mIMBlogLogic.findSearchGallery(searchWord,
							mGalleryList.get(mGalleryList.size() - 1)._id,
							FusionCode.CommonColumnsValue.COUNT_VALUE);
				} else {
					requestId = mIMBlogLogic.findSearchGallery(searchWord, null,
							FusionCode.CommonColumnsValue.COUNT_VALUE);
				}
			}
		});

		searchWord = getIntent().getStringExtra(FusionAction.DiscoverAction.SEARCH_WORD);
		if (TextUtils.isEmpty(searchWord)) {
			finish();
		}

		requestId = mIMBlogLogic.findSearchGallery(searchWord, null,
				FusionCode.CommonColumnsValue.COUNT_VALUE);

	}

	@Override
	protected void handleStateMessage(Message msg) {
		switch (msg.what) {
		case FusionMessageType.MBlogMessageType.PULL_SEARCH_GALLERY_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mGalleryList = (List<Gallery>) object.mObject;
					if (null == mPicGridAdapter) {
						mPicGridAdapter = new PicGridAdapter(DiscoverGridViewActivity.this,
								options, 1);
						mPicGridAdapter.setListGallery(mGalleryList);
						mGridView.setAdapter(mPicGridAdapter);
					} else {
						mPicGridAdapter.setListGallery(mGalleryList);
						mPicGridAdapter.notifyDataSetChanged();
					}
				}
			}
			mPullRefreshGridView.onRefreshComplete();
			break;
		}
		case FusionMessageType.MBlogMessageType.PUSH_SEARCH_GALLERY_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mGalleryList.addAll((List<Gallery>) object.mObject);
					if (null == mPicGridAdapter) {
						mPicGridAdapter = new PicGridAdapter(DiscoverGridViewActivity.this,
								options, 1);
						mPicGridAdapter.setListGallery(mGalleryList);
						mGridView.setAdapter(mPicGridAdapter);
					} else {
						mPicGridAdapter.setListGallery(mGalleryList);
						mPicGridAdapter.notifyDataSetChanged();
					}
				}
			}
			mPullRefreshGridView.onRefreshComplete();
			break;
		}
		case FusionMessageType.MBlogMessageType.SEARCH_GALLERY_NOT_FOUND: {
			mPullRefreshGridView.onRefreshComplete();
			break;
		}

		default:
		}
	}
}
