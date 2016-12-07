package com.theone.sns.util.uiwidget;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.TheOneApp;
import com.theone.sns.ui.chat.activity.ChatActivity;
import com.theone.sns.util.HelperFunc;

public final class PlusTypeWidget implements IPlusWidget {
	public static final int CHOOSE_TYPE_PHOTO = 0;
	public static final int CHOOSE_TYPE_CAMERA = 1;
	public static final int CHOOSE_TYPE_LOCATION = 2;
	public static final int CHOOSE_TYPE_NAMECARD = 3;

	private ChatActivity m_activity;
	private View m_popUpView;
	private CustomPopDialog m_popDialog;
	private View m_gapView;

	private int measuredHeight;

	public static interface MenuCallback {
		void onMenuItemClick(int callbackId);
	}

	private MenuCallback m_callBack;

	private final int size = 5;

	private final int[] icons = new int[] { R.drawable.chat_send_picture, R.drawable.chat_camera,
			R.drawable.chat_send_location, R.drawable.chat_send_business_card1 };

	private final int[] texts = new int[] { R.string.plus1, R.string.plus2, R.string.plus3,
			R.string.plus4 };

	private boolean enableVoip;
	private PlusAdapter mPlusAdapter;

	public PlusTypeWidget(ChatActivity activity, boolean enable, MenuCallback m_callBack) {
		m_activity = activity;
		enableVoip = enable;
		this.m_callBack = m_callBack;
		init();
	}

	private void init() {
		m_popUpView = m_activity.getLayoutInflater()
				.inflate(R.layout.chatplus_menu_container, null);
		m_gapView = m_popUpView.findViewById(R.id.chat_plus_gap);
		GridView gridView = (GridView) m_popUpView.findViewById(R.id.chat_plus_gridview);

		mPlusAdapter = new PlusAdapter();
		gridView.setAdapter(mPlusAdapter);

		m_popDialog = new CustomPopDialog(m_activity, m_popUpView);
		m_popDialog.setCancelable(true);
		m_popUpView.findViewById(R.id.action_cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hide();
			}
		});
	}

	private void measuredHeight(View view) {
		m_popUpView.getLayoutParams().height = 1000;
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		measuredHeight = view.getMeasuredHeight();

		if (enableVoip) {
			measuredHeight = (int) (measuredHeight + 15 * TheOneApp.getContext().getResources()
					.getDisplayMetrics().scaledDensity);
		}
	}

	@Override
	public void show(int newHeight) {
		if (null != m_popDialog) {
			m_popDialog.show();
		}

		if (measuredHeight == 0) {
			measuredHeight(m_popUpView);
		}

		Log.d("TAG", measuredHeight + " m_popUpView");
		if (null != m_popUpView) {

			if (newHeight > HelperFunc.scale(213)) {
				if (newHeight < measuredHeight) {
					int itemSize = size;
					if (!enableVoip) {
						itemSize -= 1;
					}
					if (itemSize > 4) {
						newHeight = measuredHeight;
					}
				}
			} else {
				newHeight = measuredHeight;
			}
			m_gapView.setVisibility(View.GONE);
			m_popUpView.getLayoutParams().height = newHeight;
			m_popUpView.requestLayout();
		}
	}

	@Override
	public void hide() {
		if (null != m_popDialog) {
			m_popDialog.dismiss();
		}
	}

	private class PlusAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		@Override
		public int getCount() {
			return icons.length;
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
		public View getView(final int i, View view, ViewGroup viewGroup) {
			final ImageLoaderViewHolder holder;
			if (view == null) {
				if (null == inflater) {
					inflater = LayoutInflater.from(m_activity);
				}
				view = inflater.inflate(R.layout.menu_grid_item, null);
				holder = new ImageLoaderViewHolder();
				holder.imageView = (ImageView) view.findViewById(R.id.grid_menu_icon);
				holder.mTextView = (TextView) view.findViewById(R.id.grid_menu_text);
				view.setTag(holder);
			} else {
				holder = (ImageLoaderViewHolder) view.getTag();
			}

			holder.imageView.setImageDrawable(m_activity.getResources().getDrawable(icons[i]));
			holder.mTextView.setText(m_activity.getString(texts[i]));

			final int j = i;
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					m_callBack.onMenuItemClick(j);
				}
			});

			return view;
		}
	}
}
