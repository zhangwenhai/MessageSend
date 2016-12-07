package com.theone.sns.ui.chat;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.util.ImageLoaderUtil;
import com.theone.sns.util.uiwidget.ExpressionUtil;

/**
 * Created by zhangwenhai on 2014/11/1.
 */
public class EmojiPagerAdapter extends PagerAdapter {

	private final Context mContext;
	private final EmojiOnClickCallback mEmojiOnClickCallback;
	private final DisplayImageOptions options;

	public interface EmojiOnClickCallback {
		void OnClick(int i);
	}

	public EmojiPagerAdapter(Context mContext, EmojiOnClickCallback mEmojiOnClickCallback,
			DisplayImageOptions options) {
		this.mContext = mContext;
		this.mEmojiOnClickCallback = mEmojiOnClickCallback;
		this.options = options;
	}

	@Override
	public int getCount() {
		if (ExpressionUtil.DEFAULT_SMILEY_RES_IDS.length % 21 == 0) {
			return ExpressionUtil.DEFAULT_SMILEY_RES_IDS.length / 21;
		} else {
			return ExpressionUtil.DEFAULT_SMILEY_RES_IDS.length / 21 + 1;
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object o) {
		return view == ((View) o);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View ret = LayoutInflater.from(mContext).inflate(R.layout.emoji_grid_main, null);
		GridView gv_emotion = (GridView) ret.findViewById(R.id.newsfeedpublish_emoticons);
		EmotionAdapter mEmotionAdapter = new EmotionAdapter(mContext, mEmojiOnClickCallback);
		mEmotionAdapter.setGroup(position);
		gv_emotion.setAdapter(mEmotionAdapter);
		((ViewPager) container).addView(ret);
		return ret;
	}

	private class EmotionAdapter extends BaseAdapter {

		private final Context mContext;
		private final EmojiOnClickCallback mEmojiOnClickCallback;
		private int group;

		private EmotionAdapter(Context mContext, EmojiOnClickCallback mEmojiOnClickCallback) {
			this.mContext = mContext;
			this.mEmojiOnClickCallback = mEmojiOnClickCallback;
		}

		@Override
		public int getCount() {
			if (ExpressionUtil.getInstance().getSize() - group * 21 >= 21) {
				return 21;
			} else {
				return ExpressionUtil.getInstance().getSize() - group * 21;
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
			View view;
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				view = View.inflate(mContext, R.layout.emotion_item, null);
				holder.iv_emotion = (ImageView) view.findViewById(R.id.emotcons_item_img);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			final int i = position + group * 21;
			holder.iv_emotion.setImageDrawable(mContext.getResources().getDrawable(
					ExpressionUtil.DEFAULT_SMILEY_RES_IDS[i]));
			// ImageLoaderUtil.loadImage("drawable://" +
			// ExpressionUtil.DEFAULT_SMILEY_RES_IDS[i],
			// holder.iv_emotion, options);

			// ImageLoader.getInstance().displayImage(
			// "drawable://" + ExpressionUtil.DEFAULT_SMILEY_RES_IDS[i],
			// holder.iv_emotion,
			// options);

			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (null != mEmojiOnClickCallback) {
						mEmojiOnClickCallback.OnClick(i);
					}
				}
			});
			return view;
		}

		public void setGroup(int group) {
			this.group = group;
		}
	}

	public static class ViewHolder {
		public ImageView iv_emotion;
	}
}
