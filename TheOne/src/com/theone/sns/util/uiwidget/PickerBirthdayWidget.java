package com.theone.sns.util.uiwidget;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.TheOneConstants;
import com.theone.sns.framework.ui.BaseActivity;
import com.theone.sns.util.uiwidget.wheel.OnWheelChangedListener;
import com.theone.sns.util.uiwidget.wheel.WheelView;
import com.theone.sns.util.uiwidget.wheel.adapters.ArrayWheelAdapter;
import com.theone.sns.util.uiwidget.wheel.adapters.NumericWheelAdapter;

public class PickerBirthdayWidget implements IPlusWidget {
	public static final int CHOOSE_TYPE_PHOTO = 0;
	public static final int CHOOSE_TYPE_CAMERA = 1;
	public static final int CHOOSE_TYPE_LOCATION = 2;
	public static final int CHOOSE_TYPE_NAMECARD = 3;
	public static final int CHOOSE_TYPE_VOICE = 4;

	private BaseActivity m_activity;
	private View m_popUpView;
	private CustomPopDialog m_popDialog;
	private ImageButtonWithText mDoneView;
	private ImageButtonWithText mCanView;
	private WheelView m_yearPicker;
	private WheelView m_monthPicker;
	private WheelView m_dayPicker;

	IPickerBirthdayCallback mPickerCallBack;

	// 1924-2002
	private static final int YEAR_MIN = 1924;
	private static final int YEAR_MAX = 2002;

	int currentYear, currentMonth, currentDay;

	public PickerBirthdayWidget(BaseActivity activity) {
		this(activity, TheOneConstants.USER_PROFILE_DEFULT_YEAR,
				TheOneConstants.USER_PROFILE_DEFULT_MONTH, TheOneConstants.USER_PROFILE_DEFULT_DAY);
	}

	public PickerBirthdayWidget(BaseActivity activity, int currentYear, int currentMonth,
			int currentDay) {
		m_activity = activity;
		this.currentDay = currentDay;
		this.currentMonth = currentMonth;
		this.currentYear = currentYear;
		init();
	}

	private void init() {
		m_popUpView = m_activity.getLayoutInflater().inflate(R.layout.picker_birthday_widget, null);
		mDoneView = (ImageButtonWithText) m_popUpView.findViewById(R.id.title_right);
		mCanView = (ImageButtonWithText) m_popUpView.findViewById(R.id.title_left);
		mDoneView.setOnlyText(R.string.Done);
		mCanView.setOnlyText(R.string.cancel);
		m_monthPicker = (WheelView) m_popUpView.findViewById(R.id.month);
		m_yearPicker = (WheelView) m_popUpView.findViewById(R.id.year);
		m_dayPicker = (WheelView) m_popUpView.findViewById(R.id.day);
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
        int year = YEAR_MIN + m_yearPicker.getCurrentItem();
        if (year < YEAR_MIN || year > YEAR_MAX) {
            return;
        }
        int month = m_monthPicker.getCurrentItem() + 1;
        int day = m_dayPicker.getCurrentItem() + 1;
        if (mPickerCallBack != null) {
            mPickerCallBack.onPickerFinish(year, month, day);
        }
    }

	protected void doPickerCancle() {
		int year = YEAR_MIN + m_yearPicker.getCurrentItem();
		if (year < YEAR_MIN || year > YEAR_MAX) {
			return;
		}
		int month = m_monthPicker.getCurrentItem() + 1;
		int day = m_dayPicker.getCurrentItem() + 1;
		if (mPickerCallBack != null) {
			mPickerCallBack.onPickerCancel(year, month, day);
		}
	}

	private void initDatePicker() {
		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateDays(m_yearPicker, m_monthPicker, m_dayPicker);
			}
		};
		int curMonth = currentMonth > 0 ? currentMonth : TheOneConstants.USER_PROFILE_DEFULT_MONTH;
		curMonth -= 1;
		String months[] = new String[] { "1", "2", "3", "4", "5", "6",
				"7", "8", "9", "10", "11", "12" };
		m_monthPicker.setViewAdapter(new DateArrayAdapter(m_activity, months, curMonth));
		m_monthPicker.setCurrentItem(curMonth);
		m_monthPicker.addChangingListener(listener);

		int curYear = (currentYear >= YEAR_MIN && currentYear <= YEAR_MAX) ? currentYear
				: TheOneConstants.USER_PROFILE_DEFULT_YEAR;
		curYear -= YEAR_MIN;
		// int curYear = 55;
		m_yearPicker.setViewAdapter(new DateNumericAdapter(m_activity, YEAR_MIN, YEAR_MAX, 0));
		m_yearPicker.setCurrentItem(curYear);
		m_yearPicker.addChangingListener(listener);

		updateDays(m_yearPicker, m_monthPicker, m_dayPicker);
		// int curDay = 12;
		int curDay = currentDay > 0 ? currentDay : TheOneConstants.USER_PROFILE_DEFULT_DAY;
		curDay -= 1;
		m_dayPicker.setCurrentItem(curDay);
	}

	private void updateDays(WheelView year, WheelView month, WheelView day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem());
		calendar.set(Calendar.MONTH, month.getCurrentItem());

		int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		day.setViewAdapter(new DateNumericAdapter(m_activity, 1, maxDays, calendar
				.get(Calendar.DAY_OF_MONTH) - 1));
		int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
		day.setCurrentItem(curDay - 1, true);
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

		void onPickerFinish(int year, int month, int day);

		void onPickerCancel(int year, int month, int day);

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
