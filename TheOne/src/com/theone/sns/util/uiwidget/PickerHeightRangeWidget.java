package com.theone.sns.util.uiwidget;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.framework.ui.BaseActivity;
import com.theone.sns.util.CountryUtil;
import com.theone.sns.util.uiwidget.wheel.WheelView;
import com.theone.sns.util.uiwidget.wheel.adapters.ArrayWheelAdapter;
import com.theone.sns.util.uiwidget.wheel.adapters.NumericWheelAdapter;

/**
 * Created by zhangwenhai on 2014/9/17.
 */
public class PickerHeightRangeWidget implements IPlusWidget {

	private final BaseActivity m_activity;

	private final String currentString;
	private final int array;

	private View m_popUpView;

	private CustomPopDialog m_popDialog;

	private ImageButtonWithText mDoneView;

	private ImageButtonWithText mCanView;

	private WheelView m_startPicker;

	private IPickerCallback mPickerCallBack;

	public PickerHeightRangeWidget(BaseActivity activity, String currentString, int array) {
		m_activity = activity;
		this.currentString = currentString;
		this.array = array;
		init();
	}

	private void init() {
		m_popUpView = m_activity.getLayoutInflater().inflate(R.layout.picker_height_range_widget,
				null);
		mDoneView = (ImageButtonWithText) m_popUpView.findViewById(R.id.title_right);
		mCanView = (ImageButtonWithText) m_popUpView.findViewById(R.id.title_left);
		mDoneView.setOnlyText(R.string.Done);
		mCanView.setOnlyText(R.string.cancel);

		m_startPicker = (WheelView) m_popUpView.findViewById(R.id.start);
		initDatePicker();

		mDoneView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doPickerFinish();
				hide();
			}
		});
		mCanView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				doPickerCancle();
				hide();
			}
		});

		m_popDialog = new CustomPopDialog(m_activity, m_popUpView);
		m_popDialog.setCancelable(true);
	}

	public void setPickerCallBack(IPickerCallback mPickerCallBack) {
		this.mPickerCallBack = mPickerCallBack;
	}

	private void initDatePicker() {
		String[] s = CountryUtil.getInstance(m_activity).getListFrom(array);
		int curStart = 0;
		for (int i = 0; i < s.length; i++) {
			if (s[i].equals(currentString)) {
				curStart = i;
				break;
			}
		}
		m_startPicker.setViewAdapter(new DateArrayAdapter(m_activity, CountryUtil.getInstance(
				m_activity).getListFrom(array), 0));
		m_startPicker.setCurrentItem(curStart);
	}

	protected void doPickerFinish() {
		if (mPickerCallBack != null) {
			mPickerCallBack
					.onPickerFinish(CountryUtil.getInstance(m_activity).getListFrom(array)[m_startPicker
							.getCurrentItem()]);
		}
	}

	protected void doPickerCancle() {
		if (mPickerCallBack != null) {
			mPickerCallBack
					.onPickerCancel(CountryUtil.getInstance(m_activity).getListFrom(array)[m_startPicker
							.getCurrentItem()]);
		}
	}

	public interface IPickerCallback {

		void onPickerFinish(String minHeight);

		void onPickerCancel(String minHeight);

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

	@Override
	public void show(int newHeight) {
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

	public void show() {
		show(-1);
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
}
