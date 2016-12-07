package com.theone.sns.ui.mblog.activity;

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
import com.theone.sns.logic.model.mblog.MBlogTag;
import com.theone.sns.logic.model.mblog.base.LocationTag;
import com.theone.sns.logic.model.mblog.base.TextTag;
import com.theone.sns.logic.model.user.base.Gallery;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.mblog.PicGridAdapter;
import com.theone.sns.ui.publish.Tag;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshBase;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/10/23.
 */
public class TagSetActivity extends IphoneTitleActivity {

	private IMBlogLogic mIMBlogLogic;
	private PullToRefreshGridView mPullRefreshGridView;
	private GridView mGridView;
	private MBlogTag mMBlogTag;
	private String requestId;
	private List<Gallery> mGalleryList = new ArrayList<Gallery>();
	private PicGridAdapter mPicGridAdapter;
	private Tag mTag;

	@Override
	protected void initLogics() {
		mIMBlogLogic = (IMBlogLogic) getLogicByInterfaceClass(IMBlogLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setSubContent(R.layout.tag_set_main);

		getView();

		setView();

		requestId = mIMBlogLogic.tagToGallery(mMBlogTag, null,
				FusionCode.CommonColumnsValue.COUNT_VALUE);
	}

	private void getView() {
		mTag = (Tag) getIntent().getSerializableExtra(FusionAction.MBlogAction.MBLOG_TAG);
		if (null == mTag) {
			finish();
			return;
		}
		getMBlogTag(mTag);
		mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.grid_view);
		mGridView = mPullRefreshGridView.getRefreshableView();
	}

	private void getMBlogTag(Tag mTag) {
		mMBlogTag = new MBlogTag();
		mMBlogTag.id = mTag.id;
		if (mTag.type == Tag.LABEL_TYPE) {
			TextTag mTextTag = new TextTag();
			mTextTag.name = mTag.tagName;
			mMBlogTag.text = mTextTag;
		}
		if (mTag.type == Tag.PLACE_TYPE) {
			LocationTag mLocationTag = new LocationTag();
			mLocationTag.name = mTag.tagName;
			mMBlogTag.location = mLocationTag;
		}
		mMBlogTag.x = mTag.x;
		mMBlogTag.y = mTag.y;
	}

	private void setView() {
		if (null != mMBlogTag.text) {
			setTitle(mMBlogTag.text.name);
		}
		if (null != mMBlogTag.location) {
			setTitle(mMBlogTag.location.name);
		}
		setLeftButton(R.drawable.icon_back, false, false);
		mPullRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
			@Override
			public void onRefresh() {
				requestId = mIMBlogLogic.tagToGallery(mMBlogTag, null,
						FusionCode.CommonColumnsValue.COUNT_VALUE);
			}

			@Override
			public void onAddMore() {
				if (mGalleryList.size() > 0) {
					requestId = mIMBlogLogic.tagToGallery(mMBlogTag,
							mGalleryList.get(mGalleryList.size() - 1)._id,
							FusionCode.CommonColumnsValue.COUNT_VALUE);
				} else {
					requestId = mIMBlogLogic.tagToGallery(mMBlogTag, null,
							FusionCode.CommonColumnsValue.COUNT_VALUE);
				}
			}
		});
	}

	@Override
	protected void handleStateMessage(Message msg) {
		switch (msg.what) {
		case FusionMessageType.MBlogMessageType.PULL_TAG_TO_GALLERY_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mGalleryList = (List<Gallery>) object.mObject;
					if (null == mPicGridAdapter) {
						mPicGridAdapter = new PicGridAdapter(TagSetActivity.this, options, 1);
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
		case FusionMessageType.MBlogMessageType.PUSH_TAG_TO_GALLERY_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mGalleryList.addAll((List<Gallery>) object.mObject);
					if (null == mPicGridAdapter) {
						mPicGridAdapter = new PicGridAdapter(TagSetActivity.this, options, 1);
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
		case FusionMessageType.MBlogMessageType.TAG_TO_GALLERY_NOT_FOUND: {
			mPullRefreshGridView.onRefreshComplete();
			break;
		}

		default:
		}
	}
}
