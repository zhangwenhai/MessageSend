package com.theone.sns.ui.mblog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.user.IUserLogic;
import com.theone.sns.ui.mblog.activity.AddFriendActivity;
import com.theone.sns.ui.me.GotoTaActivityOnClickListener;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/10/22.
 */
public class AddListAdapter extends BaseAdapter {

    private final Context mContext;
    private final int with;
    private final DisplayImageOptions options;
    private final IUserLogic mIUserLogic;

    private List<User> mUserList = new ArrayList<User>();
    private LayoutInflater inflater;

    @Override
    public int getCount() {
        return mUserList.size();
    }

    public AddListAdapter(Context mContext, DisplayImageOptions options,
                          IUserLogic mIUserLogic) {
        this.mContext = mContext;
        this.with = mContext.getResources().getDisplayMetrics().widthPixels;
        this.options = options;
        this.mIUserLogic = mIUserLogic;
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
            view = inflater.inflate(R.layout.search_friend_item, null);
            holder = new ImageLoaderViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.avatar);
            holder.mTextView = (TextView) view.findViewById(R.id.name);
            holder.imageView1 = (ImageView) view.findViewById(R.id.role);
            holder.imageView2 = (ImageView) view.findViewById(R.id.xinghun);
            holder.imageView3 = (ImageView) view.findViewById(R.id.xingxing);
            holder.mButton = (Button) view.findViewById(R.id.follow_btn);
            view.setTag(holder);
        } else {
            holder = (ImageLoaderViewHolder) view.getTag();
        }
        final User mUser = mUserList.get(i);
        ImageLoader.getInstance().displayImage(mUser.avatar_url,
                holder.imageView, options);
        holder.mTextView.setText(mUser.name);

        if (!TextUtils.isEmpty(mUser.role)) {
            holder.imageView1.setVisibility(View.VISIBLE);
        }

        if (FusionCode.Role.H.equals(mUser.role)) {
            holder.imageView1.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.home_h_icon));
        } else if (FusionCode.Role.T.equals(mUser.role)) {
            holder.imageView1.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.home_t_icon));
        } else if (FusionCode.Role.P.equals(mUser.role)) {
            holder.imageView1.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.home_p_icon));
        } else if (FusionCode.Role.MH.equals(mUser.role)) {
            holder.imageView1.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.home_0_icon));
        } else if (FusionCode.Role.MT.equals(mUser.role)) {
            holder.imageView1.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.home_1_icon));
        } else if (FusionCode.Role.MP.equals(mUser.role)) {
            holder.imageView1.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.home_0_5_icon));
        } else {
            holder.imageView1.setVisibility(View.GONE);
        }

        if (mUser.marriage && mUser.is_following) {
            holder.imageView2.setVisibility(View.VISIBLE);
        } else {
            holder.imageView2.setVisibility(View.GONE);
        }
        if (mUser.is_starring) {
            holder.imageView3.setVisibility(View.VISIBLE);
        } else {
            holder.imageView3.setVisibility(View.GONE);
        }

        showButton(mUser, holder);

        holder.imageView.setOnClickListener(new GotoTaActivityOnClickListener(
                mContext, mUser.userId));

        return view;
    }

    private void showButton(final User mUser, final ImageLoaderViewHolder holder) {

        FusionConfig.showFollowButton(mUser, holder.mButton);

        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FusionConfig.clickFollowButton(mIUserLogic, mUser,
                        holder.mButton);

                if (mContext.getString(R.string.follow).equals(
                        (String) holder.mButton.getText().toString())) {

                    if (mUser.marriage) {
                        holder.imageView2.setVisibility(View.VISIBLE);
                    } else {
                        holder.imageView2.setVisibility(View.GONE);
                    }

                } else {

                    holder.imageView2.setVisibility(View.GONE);
                }

                AddFriendActivity.isTouch = true;
            }
        });
    }

    public void setList(List<User> mUserList) {
        this.mUserList = mUserList;
    }
}
