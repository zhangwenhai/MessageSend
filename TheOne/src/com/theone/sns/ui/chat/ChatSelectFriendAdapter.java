package com.theone.sns.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.ui.chat.activity.SelectFriendActivity;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/10/31.
 */
public class ChatSelectFriendAdapter extends BaseAdapter {
	public static final int SELECT_FRIEND = 0;
	public static final int SEND_NAMECARD = 1;
	public static final int CREAD_GROUP = 2;
	private final Context mContext;
	private final DisplayImageOptions optionsForUserIcon;
	private int type = -1;
	private List<String> mStringList = new ArrayList<String>();
	private List<User> mList = new ArrayList<User>();
	private LayoutInflater inflater;

	public ChatSelectFriendAdapter(Context mContext, List<String> mStringList,
			DisplayImageOptions optionsForUserIcon, int type) {
		this.mContext = mContext;
		this.mStringList = mStringList;
		this.optionsForUserIcon = optionsForUserIcon;
		this.type = type;
	}

	@Override
	public int getCount() {
		return mList.size();
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
			view = inflater.inflate(R.layout.select_friend_list_item, null);
			holder = new ImageLoaderViewHolder();

			holder.imageView = (ImageView) view.findViewById(R.id.select);
			holder.imageView1 = (ImageView) view.findViewById(R.id.image_view);
			holder.mTextView = (TextView) view.findViewById(R.id.name);

			view.setTag(holder);
		} else {
			holder = (ImageLoaderViewHolder) view.getTag();
		}

		final User mUser = mList.get(i);
		switch (type) {
		case SELECT_FRIEND:
		case CREAD_GROUP: {
			if (mStringList.contains(mUser.userId)) {
				holder.imageView.setImageDrawable(mContext.getResources().getDrawable(
						R.drawable.select_friend_selected));
			} else {
				if (SelectFriendActivity.addlist.contains(mUser)) {
					holder.imageView.setImageDrawable(mContext.getResources().getDrawable(
							R.drawable.registration_check_icon));
				} else {
					holder.imageView.setImageDrawable(mContext.getResources().getDrawable(
							R.drawable.select_friends_unselect_icon));
				}
			}

			ImageLoader.getInstance().displayImage(mUser.avatar_url, holder.imageView1,
					optionsForUserIcon);
			holder.mTextView.setText(mUser.name);

			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (mStringList.contains(mUser.userId)) {
						return;
					}
					if (SelectFriendActivity.addlist.contains(mUser)) {
						SelectFriendActivity.addlist.remove(mUser);
					} else {
						SelectFriendActivity.addlist.add(mUser);
					}
					LocalBroadcastManager.getInstance(mContext).sendBroadcast(
							new Intent(FusionAction.ChatAction.UPDATE_VIEW));
				}
			});

			return view;
		}
		case SEND_NAMECARD: {
			if (SelectFriendActivity.addlist.contains(mUser)) {
				holder.imageView.setImageDrawable(mContext.getResources().getDrawable(
						R.drawable.registration_check_icon));
			} else {
				holder.imageView.setImageDrawable(mContext.getResources().getDrawable(
						R.drawable.select_friends_unselect_icon));
			}

			ImageLoader.getInstance().displayImage(mUser.avatar_url, holder.imageView1,
					optionsForUserIcon);
			holder.mTextView.setText(mUser.name);

			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (SelectFriendActivity.addlist.contains(mUser)) {
						SelectFriendActivity.addlist.remove(mUser);
					} else {
						SelectFriendActivity.addlist.add(mUser);
					}
					LocalBroadcastManager.getInstance(mContext).sendBroadcast(
							new Intent(FusionAction.ChatAction.UPDATE_VIEW));
				}
			});
			return view;
		}
		default:
			return view;
		}
	}

	public void setmList(List<User> mList) {
		this.mList = mList;
	}
}
