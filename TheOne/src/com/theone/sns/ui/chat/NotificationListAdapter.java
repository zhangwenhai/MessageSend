package com.theone.sns.ui.chat;

import java.util.ArrayList;
import java.util.List;

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
import com.theone.sns.common.FusionCode;
import com.theone.sns.logic.model.chat.NotifyMe;
import com.theone.sns.ui.mblog.GotoMblogCommentActivityOnClickListener;
import com.theone.sns.ui.me.GotoTaActivityOnClickListener;
import com.theone.sns.util.PrettyDateFormat;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

/**
 * Created by zhangwenhai on 2014/11/6.
 */
public class NotificationListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater inflater;
	private List<NotifyMe> notifyMeList = new ArrayList<NotifyMe>();
	private DisplayImageOptions optionsForUserIcon;

	public NotificationListAdapter(Context mContext, DisplayImageOptions optionsForUserIcon) {
		this.mContext = mContext;
		this.optionsForUserIcon = optionsForUserIcon;
	}

	@Override
	public int getCount() {
		return notifyMeList.size();
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
			view = inflater.inflate(R.layout.notification_list_item, null);
			holder = new ImageLoaderViewHolder();

			holder.imageView = (ImageView) view.findViewById(R.id.chat_icon);
			holder.mTextView = (TextView) view.findViewById(R.id.chat_name);
			holder.mTextView1 = (TextView) view.findViewById(R.id.chat_con);
			holder.mTextView2 = (TextView) view.findViewById(R.id.chat_time);
			holder.imageView1 = (ImageView) view.findViewById(R.id.mblog_view);

			view.setTag(holder);
		} else {
			holder = (ImageLoaderViewHolder) view.getTag();
		}

		NotifyMe mNotifyMe = notifyMeList.get(i);

		if (null != mNotifyMe.owner) {
			ImageLoader.getInstance().displayImage(mNotifyMe.owner.avatar_url, holder.imageView,
					optionsForUserIcon);
			holder.mTextView.setText(mNotifyMe.owner.name);
		}
		
		holder.mTextView2.setText(PrettyDateFormat.formatISO8601Time(mNotifyMe.created_at));

		switch (mNotifyMe.type) {
		case FusionCode.NotifyMeType.COMMENT: {
			holder.mTextView1.setText("评论:"+mNotifyMe.comment.get(0).desc);
			ImageLoader.getInstance().displayImage(mNotifyMe.comment.get(0).url, holder.imageView1,
					optionsForUserIcon);
			view.setOnClickListener(new GotoMblogCommentActivityOnClickListener(mContext,
					mNotifyMe.comment.get(0)._id));
			break;
		}
		case FusionCode.NotifyMeType.FOLLOW: {
			holder.mTextView1.setText("关注了你");
			ImageLoader.getInstance().displayImage(mNotifyMe.follow.get(0).avatar_url,
					holder.imageView1, optionsForUserIcon);
			view.setOnClickListener(new GotoTaActivityOnClickListener(mContext, mNotifyMe.owner.userId));
			break;
		}
		case FusionCode.NotifyMeType.LIKE: {
			holder.mTextView1.setText("赞了你的发布");
			ImageLoader.getInstance().displayImage(mNotifyMe.like.get(0).url, holder.imageView1,
					optionsForUserIcon);
			view.setOnClickListener(new GotoMblogCommentActivityOnClickListener(mContext,
					mNotifyMe.like.get(0)._id));
			break;
		}
		case FusionCode.NotifyMeType.MENTION: {
			holder.mTextView1.setText("@了你,快去看看吧");
			ImageLoader.getInstance().displayImage(mNotifyMe.mention.get(0).url, holder.imageView1,
					optionsForUserIcon);
			view.setOnClickListener(new GotoMblogCommentActivityOnClickListener(mContext,
					mNotifyMe.mention.get(0)._id));
			break;
		}
		default:
		}
		return view;
	}

	public void setNotifyMeList(List<NotifyMe> notifyMeList) {
		this.notifyMeList = notifyMeList;
	}
}
