package com.theone.sns.util.uiwidget;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.framework.ui.BaseActivity;
import com.theone.sns.util.CountryUtil;
import com.theone.sns.util.uiwidget.wheel.OnWheelChangedListener;
import com.theone.sns.util.uiwidget.wheel.WheelView;
import com.theone.sns.util.uiwidget.wheel.adapters.ArrayWheelAdapter;
import com.theone.sns.util.uiwidget.wheel.adapters.NumericWheelAdapter;

public class PickerCityWidget implements IPlusWidget {

	private BaseActivity m_activity;
	private View m_popUpView;
	private CustomPopDialog m_popDialog;
	private ImageButtonWithText mDoneView;
	private ImageButtonWithText mCanView;

	IPickerBirthdayCallback mPickerCallBack;
	private WheelView mProvincePicker;
	private WheelView mCityPicker;

	public PickerCityWidget(BaseActivity activity) {
		m_activity = activity;
		init();
	}

	private void init() {
		m_popUpView = m_activity.getLayoutInflater().inflate(R.layout.picker_city_widget, null);
		mDoneView = (ImageButtonWithText) m_popUpView.findViewById(R.id.title_right);
		mCanView = (ImageButtonWithText) m_popUpView.findViewById(R.id.title_left);
		mDoneView.setOnlyText(R.string.Done);
		mCanView.setOnlyText(R.string.cancel);
		mProvincePicker = (WheelView) m_popUpView.findViewById(R.id.province);
		mCityPicker = (WheelView) m_popUpView.findViewById(R.id.city);
		initDatePicker();
		mDoneView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doPickerFinish();
				hide();
			}
		});
		mCanView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doPickerCancle();
				hide();
			}
		});

		m_popDialog = new CustomPopDialog(m_activity, m_popUpView);
		m_popDialog.setCancelable(true);
	}

	protected void doPickerFinish() {
		if (mPickerCallBack != null
				&& CountryUtil.getInstance(m_activity).getAllProvince().length > mProvincePicker
						.getCurrentItem()
				&& CountryUtil.getInstance(m_activity).getAllCitys()[mProvincePicker
						.getCurrentItem()].length > mCityPicker.getCurrentItem()) {
			mPickerCallBack.onPickerFinish(
					CountryUtil.getInstance(m_activity).getAllProvince()[mProvincePicker
							.getCurrentItem()],
					CountryUtil.getInstance(m_activity).getAllCitys()[mProvincePicker
							.getCurrentItem()][mCityPicker.getCurrentItem()]);
		}
	}

	protected void doPickerCancle() {
		if (mPickerCallBack != null
				&& CountryUtil.getInstance(m_activity).getAllProvince().length > mProvincePicker
						.getCurrentItem()
				&& CountryUtil.getInstance(m_activity).getAllCitys()[mProvincePicker
						.getCurrentItem()].length > mCityPicker.getCurrentItem()) {
			mPickerCallBack.onPickerCancel(
					CountryUtil.getInstance(m_activity).getAllProvince()[mProvincePicker
							.getCurrentItem()],
					CountryUtil.getInstance(m_activity).getAllCitys()[mProvincePicker
							.getCurrentItem()][mCityPicker.getCurrentItem()]);
		}
	}

	private void initDatePicker() {
		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateDays(mProvincePicker, mCityPicker);
			}
		};
		mProvincePicker.setViewAdapter(new DateArrayAdapter(m_activity, CountryUtil.getInstance(
				m_activity).getAllProvince(), 0));
		mProvincePicker.addChangingListener(listener);
		updateDays(mProvincePicker, mCityPicker);
	}

	private void updateDays(WheelView mProvincePicker, WheelView mCityPicker) {
		mCityPicker.setViewAdapter(new DateArrayAdapter(m_activity, CountryUtil.getInstance(
				m_activity).getAllCitys()[mProvincePicker.getCurrentItem()], 0));
	}

	public IPickerBirthdayCallback getmPickerCallBack() {
		return mPickerCallBack;
	}

	public void setPickerCallBack(IPickerBirthdayCallback mPickerCallBack) {
		this.mPickerCallBack = mPickerCallBack;
	}

	private class DateNumericAdapter extends NumericWheelAdapter {

		public DateNumericAdapter(Context context, int minValue, int maxValue, int current) {
			super(context, minValue, maxValue);
			setTextSize(20);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			view.setTypeface(Typeface.SANS_SERIF);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			return super.getItem(index, cachedView, parent);
		}
	}

	private class DateArrayAdapter extends ArrayWheelAdapter<String> {

		public DateArrayAdapter(Context context, String[] items, int current) {
			super(context, items);
			setTextSize(20);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			view.setTypeface(Typeface.SANS_SERIF);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			return super.getItem(index, cachedView, parent);
		}
	}

	public interface IPickerBirthdayCallback {
		void onPickerFinish(String province, String city);

		void onPickerCancel(String province, String city);
	}

	public void show() {
		show(-1);
	}

	@Override
	public void show(final int newHeight) {
		if (null != m_popDialog) {
			m_popDialog.show();
		}

	}

	@Override
	public void hide() {
		if (null != m_popDialog) {
			m_popDialog.dismiss();
		}
	}
}
