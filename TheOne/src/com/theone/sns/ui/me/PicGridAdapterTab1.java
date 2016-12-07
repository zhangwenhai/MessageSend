package com.theone.sns.ui.me;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionCode.PublishMBlogVisibility;
import com.theone.sns.logic.model.user.base.Gallery;
import com.theone.sns.ui.mblog.GotoMblogCommentActivityOnClickListener;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

import java.util.List;

/**
 * Created by zhangwenhai on 2014/9/18.
 */
public class PicGridAdapterTab1 extends BaseAdapter {

    private final Context mContext;

    private final int with;

    private final DisplayImageOptions options;

    private List<Gallery> mlist;

    private LayoutInflater inflater;

    public PicGridAdapterTab1(Context mContext, DisplayImageOptions options) {
        this.mContext = mContext;
        this.with = mContext.getResources().getDisplayMetrics().widthPixels;
        this.options = options;
    }

    @Override
    public int getCount() {
        if (mlist.size() % 3 == 0) {
            return mlist.size() / 3;
        } else {
            return mlist.size() / 3 + 1;
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
            view = inflater.inflate(R.layout.me_gridview_item, null);
            holder = new ImageLoaderViewHolder();
            holder.imageView1 = (ImageView) view.findViewById(R.id.image1);
            holder.imageView2 = (ImageView) view.findViewById(R.id.image2);
            holder.imageView3 = (ImageView) view.findViewById(R.id.image3);
            holder.imageView7 = (ImageView) view.findViewById(R.id.video_view1);
            holder.imageView8 = (ImageView) view.findViewById(R.id.video_view2);
            holder.imageView9 = (ImageView) view.findViewById(R.id.video_view3);
            holder.imageView4 = (ImageView) view.findViewById(R.id.starring1);
            holder.imageView5 = (ImageView) view.findViewById(R.id.starring2);
            holder.imageView6 = (ImageView) view.findViewById(R.id.starring3);

            holder.imageView1.setLayoutParams(new FrameLayout.LayoutParams((with - (int) HelperFunc
                    .dip2px(8)) / 3, (with - (int) HelperFunc.dip2px(8)) / 3));
            holder.imageView2.setLayoutParams(new FrameLayout.LayoutParams((with - (int) HelperFunc
                    .dip2px(8)) / 3, (with - (int) HelperFunc.dip2px(8)) / 3));
            holder.imageView3.setLayoutParams(new FrameLayout.LayoutParams((with - (int) HelperFunc
                    .dip2px(8)) / 3, (with - (int) HelperFunc.dip2px(8)) / 3));

            holder.imageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.imageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.imageView3.setScaleType(ImageView.ScaleType.CENTER_CROP);

            view.setTag(holder);
        } else {
            holder = (ImageLoaderViewHolder) view.getTag();
        }

        ImageLoader.getInstance().displayImage(mlist.get(3 * i).url, holder.imageView1, options);
        holder.imageView1.setOnClickListener(new GotoMblogCommentActivityOnClickListener(mContext,
                mlist.get(3 * i)._id));

        if (mlist.get(3 * i).is_video) {
            holder.imageView7.setVisibility(View.VISIBLE);
        } else {
            holder.imageView7.setVisibility(View.GONE);
        }

        if (PublishMBlogVisibility.STARRING.equals(mlist.get(3 * i).visibility)) {
            holder.imageView4.setVisibility(View.VISIBLE);
        } else {
            holder.imageView4.setVisibility(View.GONE);
        }

        if (mlist.size() > 3 * i + 1) {
            ImageLoader.getInstance().displayImage(mlist.get(3 * i + 1).url, holder.imageView2,
                    options);
            holder.imageView2.setOnClickListener(new GotoMblogCommentActivityOnClickListener(
                    mContext, mlist.get(3 * i + 1)._id));

            if (mlist.get(3 * i + 1).is_video) {
                holder.imageView8.setVisibility(View.VISIBLE);
            } else {
                holder.imageView8.setVisibility(View.GONE);
            }

            if (PublishMBlogVisibility.STARRING
                    .equals(mlist.get(3 * i + 1).visibility)) {
                holder.imageView5.setVisibility(View.VISIBLE);
            } else {
                holder.imageView5.setVisibility(View.GONE);
            }
        }
        if (mlist.size() > 3 * i + 2) {
            ImageLoader.getInstance().displayImage(mlist.get(3 * i + 2).url, holder.imageView3,
                    options);
            holder.imageView3.setOnClickListener(new GotoMblogCommentActivityOnClickListener(
                    mContext, mlist.get(3 * i + 2)._id));

            if (mlist.get(3 * i + 2).is_video) {
                holder.imageView9.setVisibility(View.VISIBLE);
            } else {
                holder.imageView9.setVisibility(View.GONE);
            }

            if (PublishMBlogVisibility.STARRING
                    .equals(mlist.get(3 * i + 2).visibility)) {
                holder.imageView6.setVisibility(View.VISIBLE);
            } else {
                holder.imageView6.setVisibility(View.GONE);
            }
        }
        return view;
    }

    public void setList(List<Gallery> mlist) {
        if (null != mlist)
            this.mlist = mlist;
    }
}
