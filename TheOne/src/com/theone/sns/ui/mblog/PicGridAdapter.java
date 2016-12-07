package com.theone.sns.ui.mblog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.theone.sns.R;
import com.theone.sns.component.location.gps.LocalLocation;
import com.theone.sns.component.location.gps.LocationManager;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.model.user.base.Gallery;
import com.theone.sns.ui.me.GotoTaActivityOnClickListener;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.StringUtil;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

/**
 * Created by zhangwenhai on 2014/9/19.
 */
public class PicGridAdapter extends BaseAdapter {

    private final Context mContext;

    private final int with;

    private final int type;

    private List<User> mlist = new ArrayList<User>();

    private List<Gallery> mGlist = new ArrayList<Gallery>();

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private LayoutInflater inflater;

    private DisplayImageOptions options;

    private LocalLocation mLocation;

    public PicGridAdapter(Context mContext, DisplayImageOptions options, int type) {
        this.mContext = mContext;
        with = mContext.getResources().getDisplayMetrics().widthPixels;
        this.options = options;
        mLocation = LocationManager.getInstance().getLocation();
        this.type = type;
    }

    @Override
    public int getCount() {
        if (type == 0) {
            return mlist.size();
        } else {
            return mGlist.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ImageLoaderViewHolder holder;
        if (convertView == null) {
            if (null == inflater) {
                inflater = LayoutInflater.from(mContext);
            }
            convertView = inflater.inflate(R.layout.nearby_gridview_item, null);
            holder = new ImageLoaderViewHolder();
            holder.mRelativeLayout = (RelativeLayout) convertView
                    .findViewById(R.id.relativeLayout1);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            holder.imageView1 = (ImageView) convertView.findViewById(R.id.video_view);
            holder.mTextView = (TextView) convertView.findViewById(R.id.distance);
            holder.mTextView1 = (TextView) convertView.findViewById(R.id.type);
            holder.imageView.setLayoutParams(new FrameLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    (with - 4 - (int) HelperFunc.dip2px(10)) / 3));
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            convertView.setTag(holder);
        } else {
            holder = (ImageLoaderViewHolder) convertView.getTag();
        }

        if (type == 0) {

            User user = mlist.get(position);

            ImageLoader.getInstance().displayImage(user.avatar_url, holder.imageView, options,
                    animateFirstListener);
            
			if (mContext.getString(R.string.role_server_unknow).equals(
					mlist.get(position).role)) {
				holder.mTextView1.setText("");
			} else {
				holder.mTextView1.setText(mlist.get(position).role);
			}
            
            holder.mRelativeLayout.setVisibility(View.VISIBLE);
            holder.imageView1.setVisibility(View.GONE);
            if (null != user.location && user.location.size() == 2
                    && null != mLocation) {

                holder.mTextView.setVisibility(View.VISIBLE);

                holder.mTextView.setText(StringUtil.getDistance(
                        Double.valueOf(mLocation.longitude),
                        Double.valueOf(mLocation.latitude),
                        Double.valueOf(user.location.get(0)),
                        Double.valueOf(user.location.get(1)))
                        + mContext.getString(R.string.distance_km));
            } else {
                holder.mTextView.setVisibility(View.GONE);
            }

            convertView.setOnClickListener(new GotoTaActivityOnClickListener(mContext, mlist
                    .get(position).userId));
        } else {
            Gallery mGallery = mGlist.get(position);

            ImageLoader.getInstance().displayImage(mGallery.url, holder.imageView, options,
                    animateFirstListener);
            holder.mRelativeLayout.setVisibility(View.GONE);
            if (mGallery.is_video) {
                holder.imageView1.setVisibility(View.VISIBLE);
            } else {
                holder.imageView1.setVisibility(View.GONE);
            }
            convertView.setOnClickListener(new GotoMblogCommentActivityOnClickListener(mContext,
                    mGallery._id));
        }
        return convertView;
    }

    public void setList(List<User> mlist) {
        if (null != mlist)
            this.mlist = mlist;
    }

    public void setListGallery(List<Gallery> mlist) {
        if (null != mlist)
            this.mGlist = mlist;
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
