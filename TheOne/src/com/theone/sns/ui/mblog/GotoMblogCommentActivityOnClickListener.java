package com.theone.sns.ui.mblog;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.theone.sns.common.FusionAction;
import com.theone.sns.logic.model.mblog.MBlog;

/**
 * Created by zhangwenhai on 2014/9/26.
 */
public class GotoMblogCommentActivityOnClickListener implements View.OnClickListener {

	private String mBlogId;

	private MBlog mBlog = null;

	private Context mContext;

	public GotoMblogCommentActivityOnClickListener(Context mContext, MBlog mBlog) {
		this.mBlog = mBlog;
		this.mContext = mContext;
	}

	public GotoMblogCommentActivityOnClickListener(Context mContext, String mBlogId) {
		this.mBlogId = mBlogId;
		this.mContext = mContext;
	}

	@Override
	public void onClick(View view) {
		if (null != mBlog) {
			mContext.startActivity(new Intent(FusionAction.MBlogAction.MBLOGCOMMENT_ACTION)
					.putExtra(FusionAction.MBlogAction.MBLOG, mBlog).setFlags(
							Intent.FLAG_ACTIVITY_NEW_TASK));
		} else {
			mContext.startActivity(new Intent(FusionAction.MBlogAction.MBLOGCOMMENT_ACTION)
					.putExtra(FusionAction.MBlogAction.MBLOG_ID, mBlogId).setFlags(
							Intent.FLAG_ACTIVITY_NEW_TASK));
		}
	}
}
