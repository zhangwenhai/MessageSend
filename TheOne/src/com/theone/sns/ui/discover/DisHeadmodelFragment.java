package com.theone.sns.ui.discover;

import java.util.ArrayList;
import java.util.List;

import android.os.Message;
import android.text.TextUtils;

import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.mblog.IMBlogLogic;
import com.theone.sns.logic.model.user.base.Gallery;
import com.theone.sns.ui.mblog.PicGridAdapter;
import com.theone.sns.ui.mblog.activity.HeadmodelFragment;

/**
 * Created by zhangwenhai on 2014/10/9.
 */
public class DisHeadmodelFragment extends HeadmodelFragment {

	private IMBlogLogic mIMBlogLogic;

	private List<Gallery> mGalleryList = new ArrayList<Gallery>();

	@Override
	protected void initLogics() {
		mIMBlogLogic = (IMBlogLogic) getLogicByInterfaceClass(IMBlogLogic.class);
	}

	@Override
	protected void sendRrequest() {
		requestDBId = mIMBlogLogic.findMBlogGalleryFromDB(type);
		requestId = mIMBlogLogic.findMBlogGallery(type, null, FusionCode.CommonColumnsValue.COUNT_VALUE);
	}

	@Override
	protected void onRefresh() {
		requestId = mIMBlogLogic.findMBlogGallery(type, null, FusionCode.CommonColumnsValue.COUNT_VALUE);
	}

	@Override
	protected void onAddMore() {
		if (mGalleryList.size() > 0) {
			requestId = mIMBlogLogic.findMBlogGallery(type,
					mGalleryList.get(mGalleryList.size() - 1)._id,
					FusionCode.CommonColumnsValue.COUNT_VALUE);
		} else {
			requestId = mIMBlogLogic.findMBlogGallery(type, null,
					FusionCode.CommonColumnsValue.COUNT_VALUE);
		}
	}

	@Override
	protected void handleStateMessage(Message msg) {
		switch (msg.what) {
		case FusionMessageType.MBlogMessageType.GET_GALLERY_LIST_FROM_DB: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestDBId)
					&& requestDBId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mGalleryList = (List<Gallery>) object.mObject;
					if (null == mPicGridAdapter) {
						mPicGridAdapter = new PicGridAdapter(context, options, 1);
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
		case FusionMessageType.MBlogMessageType.PULL_FIND_GALLERY_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mGalleryList = (List<Gallery>) object.mObject;
					if (null == mPicGridAdapter) {
						mPicGridAdapter = new PicGridAdapter(context, options, 1);
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
		case FusionMessageType.MBlogMessageType.PUSH_FIND_GALLERY_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mGalleryList.addAll((List<Gallery>) object.mObject);
					if (null == mPicGridAdapter) {
						mPicGridAdapter = new PicGridAdapter(context, options, 1);
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
		case FusionMessageType.MBlogMessageType.FIND_GALLERY_NOT_FOUND: {
			mPullRefreshGridView.onRefreshComplete();
			break;
		}

		default:
		}
	}
}
