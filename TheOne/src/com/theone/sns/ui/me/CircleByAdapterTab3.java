package com.theone.sns.ui.me;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.logic.model.user.Mentioned;
import com.theone.sns.logic.model.user.base.Gallery;
import com.theone.sns.ui.mblog.PicGridAdapter;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/9/18.
 */
public class CircleByAdapterTab3 extends BaseAdapter {

	private final Context mContext;

	private final int with;

	private final DisplayImageOptions options;

	private List<Mentioned> mlist = new ArrayList<Mentioned>();

	private LayoutInflater inflater;

	public CircleByAdapterTab3(Context mContext, DisplayImageOptions options) {
		this.mContext = mContext;
		this.with = mContext.getResources().getDisplayMetrics().widthPixels;
		this.options = options;
	}

	@Override
	public int getCount() {
		return mlist.size();
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
	public View getView(int i, View view, ViewGroup viewGroup) {
		final ImageLoaderViewHolder holder;
		if (view == null) {
			if (null == inflater) {
				inflater = LayoutInflater.from(mContext);
			}
			view = inflater.inflate(R.layout.circle_user_item, null);
			holder = new ImageLoaderViewHolder();

			holder.mTextView = (TextView) view.findViewById(R.id.circle_text);
			holder.mGridView = (GridView) view.findViewById(R.id.circl_icons);

			view.setTag(holder);
		} else {
			holder = (ImageLoaderViewHolder) view.getTag();
		}

		Mentioned mMentioned = mlist.get(i);
		PicGridAdapter mPicGridAdapter = new PicGridAdapter(mContext, options, 1);
		if (null != mMentioned.media) {
			mPicGridAdapter.setListGallery(mMentioned.media);
		}
		holder.mGridView.setAdapter(mPicGridAdapter);
		holder.mTextView.setText(String.format(mContext.getResources().getString(R.string.circle),
				mMentioned.mentioned_at, mMentioned.mentioned_count));

		return view;
	}

	public void setList(List<Mentioned> mlist) {
		if (null != mlist)
			this.mlist = mlist;
	}
}
