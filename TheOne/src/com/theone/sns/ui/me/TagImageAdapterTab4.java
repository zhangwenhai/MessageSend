package com.theone.sns.ui.me;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.logic.model.mblog.MBlogTag;
import com.theone.sns.logic.model.user.TagThumbnail;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.HttpUtil;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/9/18.
 */
public class TagImageAdapterTab4 extends BaseAdapter {

	private final Context mContext;

	private final int with;

	private final DisplayImageOptions options;

	private final String uid;

	private List<TagThumbnail> mlist = new ArrayList<TagThumbnail>();

	private LayoutInflater inflater;

	public TagImageAdapterTab4(Context mContext, DisplayImageOptions options, String uid) {
		this.mContext = mContext;
		this.with = mContext.getResources().getDisplayMetrics().widthPixels;
		this.options = options;
		this.uid = uid;
	}

	@Override
	public int getCount() {
		if (mlist.size() % 2 == 0) {
			return mlist.size() / 2;
		} else {
			return mlist.size() / 2 + 1;
		}
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
			view = inflater.inflate(R.layout.tag_image_item, null);
			holder = new ImageLoaderViewHolder();

			holder.mTextView = (TextView) view.findViewById(R.id.text1);
			holder.mTextView1 = (TextView) view.findViewById(R.id.text2);
			holder.imageView1 = (ImageView) view.findViewById(R.id.image_big_1);
			// holder.imageView2 = (ImageView)
			// view.findViewById(R.id.image_small_1);
			// holder.imageView3 = (ImageView)
			// view.findViewById(R.id.image_small_2);
			// holder.imageView4 = (ImageView)
			// view.findViewById(R.id.image_small_3);
			holder.imageView5 = (ImageView) view.findViewById(R.id.image_big_2);
			// holder.imageView6 = (ImageView)
			// view.findViewById(R.id.image_small_4);
			// holder.imageView7 = (ImageView)
			// view.findViewById(R.id.image_small_5);
			// holder.imageView8 = (ImageView)
			// view.findViewById(R.id.image_small_6);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					(with - (int) HelperFunc.dip2px(50)) / 2,
					(with - (int) HelperFunc.dip2px(50)) / 2);
			lp.setMargins(0, 0, 0, (int) HelperFunc.dip2px(2));
			holder.imageView1.setLayoutParams(lp);
			holder.imageView5.setLayoutParams(lp);

			LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(
					(with - (int) HelperFunc.dip2px(50)) / 2,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			holder.mTextView.setLayoutParams(lp3);
			holder.mTextView1.setLayoutParams(lp3);

			LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
					(((with - (int) HelperFunc.dip2px(50)) / 2) - (int) HelperFunc.dip2px(4)) / 3,
					(((with - (int) HelperFunc.dip2px(50)) / 2) - (int) HelperFunc.dip2px(4)) / 3);
			LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
					(((with - (int) HelperFunc.dip2px(50)) / 2) - (int) HelperFunc.dip2px(4)) / 3,
					(((with - (int) HelperFunc.dip2px(50)) / 2) - (int) HelperFunc.dip2px(4)) / 3);
			lp2.setMargins(0, 0, (int) HelperFunc.dip2px(2), 0);
			// holder.imageView2.setLayoutParams(lp2);
			// holder.imageView3.setLayoutParams(lp2);
			// holder.imageView4.setLayoutParams(lp1);
			// holder.imageView6.setLayoutParams(lp2);
			// holder.imageView7.setLayoutParams(lp2);
			// holder.imageView8.setLayoutParams(lp1);
			view.setTag(holder);
		} else {
			holder = (ImageLoaderViewHolder) view.getTag();
		}

		if (null != mlist.get(i * 2).thumbnails && mlist.get(i * 2).thumbnails.size() > 0) {
			ImageLoader.getInstance().displayImage(
					HttpUtil.addGalleryUrlWH(mlist.get(i * 2).thumbnails.get(0), 250, 250),
					holder.imageView1, options);
			// if (mlist.get(i * 2).thumbnails.size() > 1)
			// ImageLoader.getInstance().displayImage(mlist.get(i *
			// 2).thumbnails.get(1),
			// holder.imageView2, options);
			// if (mlist.get(i * 2).thumbnails.size() > 2)
			// ImageLoader.getInstance().displayImage(mlist.get(i *
			// 2).thumbnails.get(2),
			// holder.imageView3, options);
			// if (mlist.get(i * 2).thumbnails.size() > 3)
			// ImageLoader.getInstance().displayImage(mlist.get(i *
			// 2).thumbnails.get(3),
			// holder.imageView4, options);
		}
		final MBlogTag mMBlogTag = new MBlogTag();
		if (null != mlist.get(i * 2).tag && null != mlist.get(i * 2).tag.text) {
			holder.mTextView.setText("#" + mlist.get(i * 2).tag.text.name);
			mMBlogTag.text = mlist.get(i * 2).tag.text;
		}
		if (null != mlist.get(i * 2).tag && null != mlist.get(i * 2).tag.location) {
			holder.mTextView.setText("#" + mlist.get(i * 2).tag.location.name);
			mMBlogTag.location = mlist.get(i * 2).tag.location;
		}
		if (null != mlist.get(i * 2).tag && null != mlist.get(i * 2).tag.user) {
			holder.mTextView.setText("#" + mlist.get(i * 2).tag.user.name);
			mMBlogTag.user = mlist.get(i * 2).tag.user;
		}
		holder.imageView1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mContext.startActivity(new Intent(FusionAction.MeAction.MBLOGLISTBYTAG_ACTION)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
						.putExtra(FusionAction.MeAction.UID, uid)
						.putExtra(FusionAction.MeAction.TAG, mMBlogTag));
			}
		});
		if (mlist.size() > i * 2 + 1) {
			final MBlogTag mMBlogTag1 = new MBlogTag();
			if (null != mlist.get(i * 2).tag && null != mlist.get(i * 2 + 1).tag.text) {
				holder.mTextView1.setText("#" + mlist.get(i * 2 + 1).tag.text.name);
				mMBlogTag1.text = mlist.get(i * 2 + 1).tag.text;
			}
			if (null != mlist.get(i * 2).tag && null != mlist.get(i * 2 + 1).tag.location) {
				holder.mTextView1.setText("#" + mlist.get(i * 2 + 1).tag.location.name);
				mMBlogTag1.location = mlist.get(i * 2 + 1).tag.location;
			}
			if (null != mlist.get(i * 2).tag && null != mlist.get(i * 2 + 1).tag.user) {
				holder.mTextView1.setText("#" + mlist.get(i * 2 + 1).tag.user.name);
				mMBlogTag1.user = mlist.get(i * 2 + 1).tag.user;
			}
			if (null != mlist.get(i * 2 + 1).thumbnails
					&& mlist.get(i * 2 + 1).thumbnails.size() > 0) {
				ImageLoader.getInstance().displayImage(
						HttpUtil.addGalleryUrlWH(mlist.get(i * 2 + 1).thumbnails.get(0), 250, 250),
						holder.imageView5, options);
				holder.imageView5.setVisibility(View.VISIBLE);
				// if (mlist.get(i * 2 + 1).thumbnails.size() > 1)
				// ImageLoader.getInstance().displayImage(mlist.get(i * 2 +
				// 1).thumbnails.get(1),
				// holder.imageView6, options);
				// if (mlist.get(i * 2 + 1).thumbnails.size() > 2)
				// ImageLoader.getInstance().displayImage(mlist.get(i * 2 +
				// 1).thumbnails.get(2),
				// holder.imageView7, options);
				// if (mlist.get(i * 2 + 1).thumbnails.size() > 3)
				// ImageLoader.getInstance().displayImage(mlist.get(i * 2 +
				// 1).thumbnails.get(3),
				// holder.imageView8, options);
			}
			holder.imageView5.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mContext.startActivity(new Intent(FusionAction.MeAction.MBLOGLISTBYTAG_ACTION)
							.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
							.putExtra(FusionAction.MeAction.UID, uid)
							.putExtra(FusionAction.MeAction.TAG, mMBlogTag1));
				}
			});
		} else {
			holder.mTextView1.setText("");
			holder.imageView5.setVisibility(View.GONE);
		}
		return view;
	}

	public void setList(List<TagThumbnail> mlist) {
		if (null != mlist)
			this.mlist = mlist;
	}
}
