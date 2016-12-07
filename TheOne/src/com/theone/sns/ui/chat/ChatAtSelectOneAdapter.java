package com.theone.sns.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.util.uiwidget.ExpressionUtil;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2014/11/26.
 */
public class ChatAtSelectOneAdapter extends BaseAdapter {

    private final DisplayImageOptions optionsForUserIcon;
    private List<User> mlist = new ArrayList<User>();
    private Context mContext;
    private LayoutInflater inflater;

    public ChatAtSelectOneAdapter(Context mContext, DisplayImageOptions optionsForUserIcon) {
        this.mContext = mContext;
        this.optionsForUserIcon = optionsForUserIcon;
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
            view = inflater.inflate(R.layout.at_list_item, null);
            holder = new ImageLoaderViewHolder();

            holder.imageView = (ImageView) view.findViewById(R.id.chat_icon);
            holder.mTextView = (TextView) view.findViewById(R.id.chat_con);

            view.setTag(holder);
        } else {
            holder = (ImageLoaderViewHolder) view.getTag();
        }

        User mUser = mlist.get(i);
        if (null != mUser) {
            ImageLoader.getInstance().displayImage(mUser.avatar_url, holder.imageView, optionsForUserIcon);
            holder.mTextView.setText(ExpressionUtil.getInstance().strToSmiley(mUser.name));
        }
        return view;
    }

    public void setMlist(List<User> mlist) {
        this.mlist = mlist;
    }
}
