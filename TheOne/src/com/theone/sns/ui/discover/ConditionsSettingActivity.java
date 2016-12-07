package com.theone.sns.ui.discover;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode.Role;
import com.theone.sns.common.FusionCode.UserCharacter;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType.MBlogMessageType;
import com.theone.sns.logic.mblog.IMBlogLogic;
import com.theone.sns.logic.model.mblog.Filter;
import com.theone.sns.logic.model.mblog.base.Age;
import com.theone.sns.logic.model.mblog.base.Height;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;
import com.theone.sns.util.uiwidget.PickerCityWidget;
import com.theone.sns.util.uiwidget.PickerHeightRangeWidget;
import com.theone.sns.util.uiwidget.PickerHeightRangeWidget.IPickerCallback;
import com.theone.sns.util.uiwidget.swithbutton.SwitchButton;

/**
 * Created by zhangwenhai on 2014/9/9.
 */
public class ConditionsSettingActivity extends IphoneTitleActivity {
	private SwitchButton isEnabledButton;
	private SwitchButton isAvatarModeButton;
	private GridView interestsGridView;
	private LayoutInflater inflater;
	private InterestsGridViewAdapter mInterestsGridViewAdapter;
	private PickerHeightRangeWidget mPickerHeightRangeWidget;
	private PickerHeightRangeWidget mPickerAgeRangeWidget;
	private PickerCityWidget mPickerCityWidget;
	private TextView heightRangTextView;
	private TextView ageRangTextView;
	private TextView locationCityView;

	private TextView tLabel;
	private TextView pLabel;
	private TextView hLabel;
	private TextView unknowLabel;

	private boolean hasMeasured = false;

	private int height;

	private int width;

	private List<Integer> mAllRole = new ArrayList<Integer>();
	private List<Integer> mCheckRole = new ArrayList<Integer>();

	private List<String> mInterests = new ArrayList<String>();

	private IMBlogLogic mIMBlogLogic;

	private Filter mFilter;

	private View.OnClickListener roleOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (mCheckRole.contains((Integer) view.getId())) {
				mCheckRole.remove((Integer) view.getId());
			} else {
				if (mCheckRole.size() >= 4) {
					return;
				}
				mCheckRole.add((Integer) view.getId());
			}

			initRole();
		}
	};

	@Override
	protected void initLogics() {
		mIMBlogLogic = (IMBlogLogic) getLogicByInterfaceClass(IMBlogLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.conditions_setting_main);
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_behind);

		getView();

		setView();

		initFilter();
	}

	private void initFilter() {

		mFilter = mIMBlogLogic.getFilterFromDB();

		if (null == mFilter) {
			return;
		}

		isEnabledButton.setChecked(mFilter.enabled);

		isAvatarModeButton.setChecked(mFilter.avatar_mode_enabled);

		String character = FusionConfig.getInstance().getAccount().profile.character;

		if (UserCharacter.GAY.equals(character)) {

			tLabel.setText(Role.MT);

			pLabel.setText(Role.MP);

			hLabel.setText(Role.MH);

		} else if (UserCharacter.LES.equals(character)) {

			tLabel.setText(Role.T);

			pLabel.setText(Role.P);

			hLabel.setText(Role.H);
		}

		List<String> roleList = mFilter.role;

		if (null != roleList) {

			for (Integer s : mAllRole) {

				String roleTemp = ((TextView) findViewById(s)).getText()
						.toString();

				if (roleTemp.equals(getString(R.string.role_unknow))) {

					roleTemp = getString(R.string.role_server_unknow);
				}

				for (String role : roleList) {

					if (roleTemp.equals(role)) {

						mCheckRole.add(s);

						break;
					}
				}
			}

			initRole();
		}

		if (TextUtils.isEmpty(mFilter.region)) {

			locationCityView.setText(getString(R.string.filter_unlimit));

		} else {

			locationCityView.setText(mFilter.region);
		}

		Age age = mFilter.age;

		if (null != age) {

			if (-1 == age.max && -1 == age.min) {

				ageRangTextView.setText(getString(R.string.filter_unlimit));

			} else if (0 == age.max) {

				ageRangTextView.setText(age.min + getString(R.string.filter_age_max));

			} else if (0 == age.min) {

				ageRangTextView.setText(age.max + getString(R.string.filter_age_min));

			} else {

				ageRangTextView.setText(age.min + getString(R.string.filter_age) + "-" + age.max
						+ getString(R.string.filter_age));
			}
		}

		Height height = mFilter.height;

		if (null != height) {

			if (-1 == height.max && -1 == height.min) {

				heightRangTextView.setText(getString(R.string.filter_unlimit));

			} else if (0 == height.max) {

				heightRangTextView.setText(height.min + getString(R.string.filter_height_max));

			} else if (0 == height.min) {

				heightRangTextView.setText(height.max + getString(R.string.filter_height_min));

			} else {

				heightRangTextView.setText(height.min + getString(R.string.filter_height) + "-"
						+ height.max + getString(R.string.filter_height));
			}
		}

		if (null != mFilter.interests) {
			mInterests = mFilter.interests;
		}
	}

	private void getView() {
		isEnabledButton = (SwitchButton) findViewById(R.id.row_friends_verify_required);
		isAvatarModeButton = (SwitchButton) findViewById(R.id.avatar_mode);
		heightRangTextView = (TextView) findViewById(R.id.height_rang);
		ageRangTextView = (TextView) findViewById(R.id.age_rang);
		locationCityView = (TextView) findViewById(R.id.register_city);
		interestsGridView = (GridView) findViewById(R.id.interests);

		tLabel = (TextView) findViewById(R.id.t_label);
		pLabel = (TextView) findViewById(R.id.p_label);
		hLabel = (TextView) findViewById(R.id.h_label);
		unknowLabel = (TextView) findViewById(R.id.unknow_label);

		width = getResources().getDisplayMetrics().widthPixels;

		initLabel();

		mAllRole.add(R.id.t_label);
		mAllRole.add(R.id.p_label);
		mAllRole.add(R.id.h_label);
		mAllRole.add(R.id.unknow_label);
	}

	private void initLabel() {
		tLabel.setLayoutParams(new LinearLayout.LayoutParams(
				(int) (width - HelperFunc.dip2px(60)) / 4, LinearLayout.LayoutParams.WRAP_CONTENT));
		pLabel.setLayoutParams(new LinearLayout.LayoutParams(
				(int) (width - HelperFunc.dip2px(60)) / 4, LinearLayout.LayoutParams.WRAP_CONTENT));
		hLabel.setLayoutParams(new LinearLayout.LayoutParams(
				(int) (width - HelperFunc.dip2px(60)) / 4, LinearLayout.LayoutParams.WRAP_CONTENT));
		unknowLabel.setLayoutParams(new LinearLayout.LayoutParams(
				(int) (width - HelperFunc.dip2px(60)) / 4, LinearLayout.LayoutParams.WRAP_CONTENT));

		tLabel.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_interest_btn));
		pLabel.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_interest_btn));
		hLabel.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_interest_btn));
		unknowLabel.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_interest_btn));
		
		tLabel.setTextColor(getResources().getColor(R.color.black));
		pLabel.setTextColor(getResources().getColor(R.color.black));
		hLabel.setTextColor(getResources().getColor(R.color.black));
		unknowLabel.setTextColor(getResources().getColor(R.color.black));
	}

	private void initRole() {
		for (Integer s : mAllRole) {
			if (mCheckRole.contains(s)) {
				findViewById(s).setBackgroundColor(Color.parseColor("#49C6F2"));
				((TextView) findViewById(s)).setTextColor(getResources().getColor(R.color.white));
			} else {
				findViewById(s).setBackgroundDrawable(
						getResources().getDrawable(R.drawable.profile_interest_btn));
				((TextView) findViewById(s)).setTextColor(getResources().getColor(R.color.black));
			}
		}
	}

	private Filter createFilter() {

		Filter filter = new Filter();

		filter.enabled = isEnabledButton.isChecked();

		filter.avatar_mode_enabled = isAvatarModeButton.isChecked();

		List<String> roles = new ArrayList<String>();

		for (Integer viewId : mCheckRole) {

			TextView textView = (TextView) this.findViewById(viewId);

			String role = textView.getText().toString();

			if (getString(R.string.role_unknow).equals(role)) {

				role = getString(R.string.role_server_unknow);
			}

			roles.add(role);
		}

		filter.role = roles;

		filter.online = false;

		if (getString(R.string.filter_unlimit).equals(locationCityView.getText().toString())) {

			filter.region = "";

		} else {

			filter.region = locationCityView.getText().toString();
		}

		String ageText = ageRangTextView.getText().toString();

		Age age = new Age();

		if (getString(R.string.filter_unlimit).equals(ageText)) {

			age.min = -1;

			age.max = -1;

		} else if (ageText.indexOf(getString(R.string.filter_age_min)) >= 0) {

			age.min = 0;

			age.max = 18;

		} else if (ageText.indexOf(getString(R.string.filter_age_max)) >= 0) {

			age.min = 45;

			age.max = 0;

		} else {

			String[] ages = ageText.split("-");

			age.min = Integer.valueOf(ages[0].substring(0, ages[0].length() - 1));
			age.max = Integer.valueOf(ages[1].substring(0, ages[1].length() - 1));
		}

		filter.age = age;

		String heightText = heightRangTextView.getText().toString();

		Height height = new Height();

		if (getString(R.string.filter_unlimit).equals(heightText)) {

			height.min = -1;

			height.max = -1;

		} else if (heightText.indexOf(getString(R.string.filter_height_min)) >= 0) {

			height.min = 0;

			height.max = 140;

		} else if (heightText.indexOf(getString(R.string.filter_height_max)) >= 0) {

			height.min = 200;

			height.max = 0;

		} else {

			String[] heights = heightText.split("-");

			height.min = Integer.valueOf(heights[0].substring(0, heights[0].length() - 2));
			height.max = Integer.valueOf(heights[1].substring(0, heights[1].length() - 2));
		}

		filter.height = height;

		filter.interests = mInterests;

		return filter;
	}

	private void setView() {
		setTitle(R.string.condition_setting);
		setLeftButton(R.drawable.icon_back, false, false);
		setRightButton(R.string.confirm, true);

		getRightButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				showLoadingDialog(getString(R.string.dialog_save));

				Filter mFilter = createFilter();

				mIMBlogLogic.updateMBlogFilter(mFilter);

				if (mFilter.enabled && mFilter.avatar_mode_enabled) {
					LocalBroadcastManager.getInstance(ConditionsSettingActivity.this)
							.sendBroadcast(
									new Intent(FusionAction.TheOneApp.HEAD_MODEL).putExtra(
											FusionAction.TheOneApp.HEAD_MODEL,
											FusionAction.TheOneApp.HEADMODEL));
				} else {
					LocalBroadcastManager.getInstance(ConditionsSettingActivity.this)
							.sendBroadcast(
									new Intent(FusionAction.TheOneApp.HEAD_MODEL).putExtra(
											FusionAction.TheOneApp.HEAD_MODEL,
											FusionAction.TheOneApp.MBLOGMODEL));
				}
			}
		});

		tLabel.setOnClickListener(roleOnClickListener);
		pLabel.setOnClickListener(roleOnClickListener);
		hLabel.setOnClickListener(roleOnClickListener);
		unknowLabel.setOnClickListener(roleOnClickListener);

		findViewById(R.id.height_view).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				pickerHeight();
			}
		});

		findViewById(R.id.age_view).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				pickerAge();
			}
		});

		findViewById(R.id.city_view).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				pickerCity();
			}
		});

		List<String> purposes = FusionConfig.getInstance().getAccount().config.purposes;
		List<String> interests = FusionConfig.getInstance().getAccount().config.interests;
		purposes.addAll(interests);
		if (null != purposes && purposes.size() > 0) {
			mInterestsGridViewAdapter = new InterestsGridViewAdapter();
			mInterestsGridViewAdapter.setList(purposes);
			interestsGridView.setAdapter(mInterestsGridViewAdapter);

			int hang = 0;
			if (interests.size() % 3 == 0) {
				hang = interests.size() / 3;
			} else {
				hang = interests.size() / 3 + 1;
			}

			interestsGridView.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, (int) ((hang - 1)
							* HelperFunc.dip2px(20) + HelperFunc.dip2px(20) + hang
							* HelperFunc.dip2px(40))));
		}

		findViewById(R.id.intent_rela).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (View.GONE == findViewById(R.id.grid_linear).getVisibility()) {
					findViewById(R.id.grid_linear).setVisibility(View.VISIBLE);
					((TextView) findViewById(R.id.text)).setText("收起");
					((ImageView) findViewById(R.id.image)).setImageDrawable(getResources()
							.getDrawable(R.drawable.iphone_submenu_normal_xia));
				} else {
					findViewById(R.id.grid_linear).setVisibility(View.GONE);
					((TextView) findViewById(R.id.text)).setText("展开");
					((ImageView) findViewById(R.id.image)).setImageDrawable(getResources()
							.getDrawable(R.drawable.iphone_submenu_normal_shang));
				}
			}
		});
	}

	private void pickerHeight() {
		String s = heightRangTextView.getText().toString();
		mPickerHeightRangeWidget = new PickerHeightRangeWidget(this, s, R.array.condition_height);
		mPickerHeightRangeWidget.setPickerCallBack(new IPickerCallback() {
			@Override
			public void onPickerFinish(String minHeight) {
				heightRangTextView.setText(minHeight);
				mPickerHeightRangeWidget = null;
			}

			@Override
			public void onPickerCancel(String minHeight) {
				mPickerHeightRangeWidget = null;
			}
		});
		mPickerHeightRangeWidget.show();
	}

	private void pickerAge() {
		String s = ageRangTextView.getText().toString();
		mPickerAgeRangeWidget = new PickerHeightRangeWidget(this, s, R.array.condition_age);
		mPickerAgeRangeWidget.setPickerCallBack(new IPickerCallback() {
			@Override
			public void onPickerFinish(String minHeight) {
				ageRangTextView.setText(minHeight);
				mPickerAgeRangeWidget = null;
			}

			@Override
			public void onPickerCancel(String minHeight) {
				mPickerAgeRangeWidget = null;
			}
		});
		mPickerAgeRangeWidget.show();
	}

	private void pickerCity() {
		mPickerCityWidget = new PickerCityWidget(this);
		mPickerCityWidget.setPickerCallBack(new PickerCityWidget.IPickerBirthdayCallback() {

			@Override
			public void onPickerFinish(String province, String city) {
				locationCityView.setText(city);
				mPickerCityWidget = null;
			}

			@Override
			public void onPickerCancel(String province, String city) {
				mPickerCityWidget = null;
			}
		});
		mPickerCityWidget.show();
	}

	@Override
	protected void handleStateMessage(Message msg) {

		hideLoadingDialog();

		switch (msg.what) {
		case MBlogMessageType.UPDATE_MBLOG_FILTER_SUCCESS: {

			finish();

			break;
		}
		case MBlogMessageType.UPDATE_MBLOG_FILTER_FAIL: {

			showToast(R.string.condition_setting_fail);

			break;
		}
		default:
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.push_down_behind, R.anim.push_down);
	}

	private class InterestsGridViewAdapter extends BaseAdapter {
		private List<String> mlist;

		@Override
		public int getCount() {
			return mlist.size();
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
					inflater = LayoutInflater.from(ConditionsSettingActivity.this);
				}
				convertView = inflater.inflate(R.layout.intent_gridview_item, null);
				holder = new ImageLoaderViewHolder();
				holder.mTextView = (TextView) convertView.findViewById(R.id.intent_name);
				holder.mTextView.setLayoutParams(new LinearLayout.LayoutParams(
						(int) (width - HelperFunc.dip2px(60)) / 3, (int) HelperFunc.dip2px(40)));
				convertView.setTag(holder);
			} else {
				holder = (ImageLoaderViewHolder) convertView.getTag();
			}
			holder.mTextView.setText(mlist.get(position));

			if (mInterests.contains(holder.mTextView.getText().toString())) {
				holder.mTextView.setBackgroundColor(Color.parseColor("#55C478"));
				holder.mTextView.setTextColor(getResources().getColor(R.color.white));
			} else {
				holder.mTextView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.profile_interest_btn));
				holder.mTextView.setTextColor(getResources().getColor(R.color.black));
			}

			holder.mTextView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (mInterests.contains(((TextView) view).getText().toString())) {
						mInterests.remove(((TextView) view).getText().toString());
					} else {
						mInterests.add(((TextView) view).getText().toString());
					}
					notifyDataSetChanged();
				}
			});
			return convertView;
		}

		public void setList(List<String> mlist) {
			this.mlist = mlist;
		}
	}
}
