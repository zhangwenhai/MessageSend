package com.theone.sns.ui.publish.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.component.location.poi.IPoiListener;
import com.theone.sns.component.location.poi.LocalPoiInfo;
import com.theone.sns.component.location.poi.PoiSearchManager;
import com.theone.sns.logic.account.IAccountLogic;
import com.theone.sns.logic.mblog.IMBlogLogic;
import com.theone.sns.logic.model.account.Tag;
import com.theone.sns.logic.model.mblog.base.LocationTag;
import com.theone.sns.logic.model.mblog.base.TextTag;
import com.theone.sns.logic.model.mblog.base.UserTag;
import com.theone.sns.ui.base.BasicActivity;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

/**
 * Created by zhangwenhai on 2014/9/13.
 */
public class LabelListActivity extends BasicActivity {

	private ListView listview;

	private int type;

	private LayoutInflater inflater;

	private LabelListAdapter mLabelListAdapter;

	private Button cancelBtn;

	private String requestId;

	private IAccountLogic mIAccountLogic;

	private Tag mTag;

	private EditText searchBox;

	private boolean isGeo = false;
	private IMBlogLogic mIMBlogLogic;
	private String searchTagsId;

	@Override
	protected void initLogics() {
		mIAccountLogic = (IAccountLogic) getLogicByInterfaceClass(IAccountLogic.class);
		mIMBlogLogic = (IMBlogLogic) getLogicByInterfaceClass(IMBlogLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.label_list_main);
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_behind);

		getView();

		setView();
	}

	private void getView() {
		type = getIntent().getIntExtra(FusionAction.PublicAction.TYPE, -1);
		if (-1 == type) {
			finish();
			return;
		}
		inflater = LayoutInflater.from(LabelListActivity.this);
		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		searchBox = (EditText) findViewById(R.id.search_box);
		listview = (ListView) findViewById(R.id.listview_label);
	}

	private void setView() {
		mLabelListAdapter = new LabelListAdapter();
		listview.setAdapter(mLabelListAdapter);

		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		searchBox.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (null != mLabelListAdapter)
					mLabelListAdapter.showSearchResult(editable.toString(), type);
			}
		});

		if (type == AddLabelActivity.PLACE_REQUESTCODE) {
			PoiSearchManager.getInstance().geoResult(new IPoiListener() {
				@Override
				public void onResult(boolean result, List<LocalPoiInfo> poiInfoList) {
					if (result && !isGeo) {
						isGeo = true;
						List<String> mList = new ArrayList<String>();
						for (LocalPoiInfo mLocalPoiInfo : poiInfoList) {
							mList.add(mLocalPoiInfo.name);
						}
						if (null != mLabelListAdapter) {
							mLabelListAdapter.isSearch = false;
							mLabelListAdapter.setmList(mList);
							mLabelListAdapter.notifyDataSetChanged();
						}
					}
				}
			});
		} else {
			requestId = mIAccountLogic.getMyAllTags();
		}

	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.push_down_behind, R.anim.push_down);
	}

	private class LabelListAdapter extends BaseAdapter {

		private List<String> mList = new ArrayList<String>();

		private List<String> searchList = new ArrayList<String>();

		private boolean isSearch = false;

		@Override
		public int getCount() {
			if (isSearch)
				return searchList.size();
			return mList.size();
		}

		public void setmList(List<String> mList) {
			this.mList = mList;
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
				holder = new ImageLoaderViewHolder();
				view = inflater.inflate(R.layout.label_list_item, null);
				holder.mTextView = (TextView) view.findViewById(R.id.label_name);
				view.setTag(holder);
			} else {
				holder = (ImageLoaderViewHolder) view.getTag();
			}

			if (isSearch) {
				holder.mTextView.setText(searchList.get(i));
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent mIntent = new Intent();
						if ((getString(R.string.new_add_label) + searchBox.getText().toString())
								.equals(searchList.get(i)))
							mIntent.putExtra(FusionAction.PublicAction.LABEL_NAME, searchBox
									.getText().toString());
						else {
							mIntent.putExtra(FusionAction.PublicAction.LABEL_NAME,
									searchList.get(i));
						}
						setResult(RESULT_OK, mIntent);
						finish();
					}
				});
			} else {
				holder.mTextView.setText(mList.get(i));
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent mIntent = new Intent();
						mIntent.putExtra(FusionAction.PublicAction.LABEL_NAME, mList.get(i));
						setResult(RESULT_OK, mIntent);
						finish();
					}
				});
			}
			return view;
		}

		public void setmTag(Tag mTag) {
			mList.clear();
			if (type == AddLabelActivity.LABEL_REQUESTCODE && null != mTag.text_tags)
				for (TextTag mTextTag : mTag.text_tags) {
					if (null != mTextTag)
						mList.add(mTextTag.name);
				}
			if (type == AddLabelActivity.PLACE_REQUESTCODE && null != mTag.location_tags)
				for (LocationTag mLocationTag : mTag.location_tags) {
					if (null != mLocationTag)
						mList.add(mLocationTag.name);
				}
			if (type == AddLabelActivity.FIGURE_REQUESTCODE && null != mTag.user_tags) {
				for (UserTag mUserTag : mTag.user_tags) {
					if (null != mUserTag)
						mList.add(mUserTag.name);
				}
			}

			if (!TextUtils.isEmpty(searchBox.getText().toString())) {
				showSearchResult(searchBox.getText().toString(), type);
			} else {
				notifyDataSetChanged();
			}
		}

		private void showSearchResult(String s, int type) {
			if (type == AddLabelActivity.LABEL_REQUESTCODE) {
				searchList.clear();
				if (TextUtils.isEmpty(s)) {
					isSearch = false;
				} else {
					isSearch = true;
					for (String st : mList) {
						if (checkString(st).toLowerCase().indexOf(s.toLowerCase()) >= 0) {
							searchList.add(st);
						}
					}
					searchList.add(0, getString(R.string.new_add_label)
							+ searchBox.getText().toString());
					searchTagsId = mIMBlogLogic.searchTags(s, null, Integer.MAX_VALUE);
				}
				notifyDataSetChanged();
			}
			if (type == AddLabelActivity.PLACE_REQUESTCODE) {
				searchList.clear();
				if (TextUtils.isEmpty(s)) {
					isSearch = false;
				} else {
					isSearch = true;
					PoiSearchManager.getInstance().nearbySearch(s, new IPoiListener() {
						@Override
						public void onResult(boolean result, List<LocalPoiInfo> poiInfoList) {
							if (result) {
								for (LocalPoiInfo mLocalPoiInfo : poiInfoList) {
									searchList.add(mLocalPoiInfo.name);
								}

								runOnUiThread(new Runnable() {
									public void run() {
										notifyDataSetChanged();
									}
								});
							}
						}
					});
					searchList.add(0, getString(R.string.new_add_label)
							+ searchBox.getText().toString());
				}
				notifyDataSetChanged();
			}
		}

		public List<String> getSearchList() {
			return searchList;
		}
	}

	private String checkString(String str) {
		if (str == null) {
			str = "";
		}
		return str;
	}

	@Override
	protected void handleStateMessage(Message msg) {
		switch (msg.what) {
		case FusionMessageType.AccountMessageType.GET_MY_ALL_TAGS_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mTag = (Tag) object.mObject;
					if (null == mLabelListAdapter)
						mLabelListAdapter = new LabelListAdapter();
					mLabelListAdapter.setmTag(mTag);
				}
			}
			break;
		}
		case FusionMessageType.MBlogMessageType.PULL_SEARCH_TAGS_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(searchTagsId)
					&& searchTagsId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					List<String> mlist = (List<String>) object.mObject;
					mLabelListAdapter.getSearchList().addAll(mlist);
					mLabelListAdapter.notifyDataSetChanged();
				}
			}
			break;
		}
		}
	}
}
