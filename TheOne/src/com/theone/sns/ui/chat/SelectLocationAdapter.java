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

import com.theone.sns.R;
import com.theone.sns.component.location.poi.LocalPoiInfo;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

public class SelectLocationAdapter extends BaseAdapter {

	private List<LocalPoiInfo> mLocalPoiInfoList = new ArrayList<LocalPoiInfo>();

	private Context mContext;

	private int select_location_index = 0;

	public SelectLocationAdapter(Context context) {

		mContext = context;
	}

	@Override
	public int getCount() {

		return mLocalPoiInfoList.size();
	}

	public void setLocalPoiInfoList(List<LocalPoiInfo> mLocalPoiInfoList) {
		this.mLocalPoiInfoList = mLocalPoiInfoList;
	}

	@Override
	public Object getItem(int i) {

		return mLocalPoiInfoList.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	public LocalPoiInfo getSelectLocation() {

		if (select_location_index < 0
				|| select_location_index >= mLocalPoiInfoList.size()) {

			return null;
		}

		return mLocalPoiInfoList.get(select_location_index);
	}

	@Override
	public View getView(final int i, View view, ViewGroup viewGroup) {

		final ImageLoaderViewHolder holder;

		if (view == null) {

			holder = new ImageLoaderViewHolder();

			view = LayoutInflater.from(mContext).inflate(
					R.layout.select_location_list_item, null);

			holder.mTextView = (TextView) view.findViewById(R.id.location_name);

			holder.mTextView1 = (TextView) view
					.findViewById(R.id.location_address);

			holder.select_on = (ImageView) view
					.findViewById(R.id.select_location_imageview);

			view.setTag(holder);

		} else {

			holder = (ImageLoaderViewHolder) view.getTag();
		}

		LocalPoiInfo localPoiInfo = mLocalPoiInfoList.get(i);

		holder.mTextView.setText(localPoiInfo.name);

		holder.mTextView1.setText(localPoiInfo.address);

		if (i == select_location_index) {

			holder.select_on.setVisibility(View.VISIBLE);

		} else {

			holder.select_on.setVisibility(View.GONE);
		}

		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				select_location_index = i;

				notifyDataSetChanged();
			}
		});

		return view;
	}
}