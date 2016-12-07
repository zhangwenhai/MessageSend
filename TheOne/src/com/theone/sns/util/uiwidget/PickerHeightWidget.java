package com.theone.sns.util.uiwidget;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.TheOneConstants;
import com.theone.sns.framework.ui.BaseActivity;
import com.theone.sns.util.uiwidget.wheel.OnWheelChangedListener;
import com.theone.sns.util.uiwidget.wheel.WheelView;
import com.theone.sns.util.uiwidget.wheel.adapters.NumericWheelAdapter;

/**
 * Created by zhangwenhai on 2014/9/15.
 */
public class PickerHeightWidget implements IPlusWidget {

	private int mHeight;

	private CustomPopDialog m_popDialog;

	private BaseActivity m_activity;

	private View m_popUpView;

	private ImageButtonWithText mDoneView;

	private ImageButtonWithText mCanView;

	private WheelView m_heightPicker;

	IPickerHeightCallback mPickerCallBack;

	// 20cm-250cm
	private static final int HEIGHT_MIN = 20;

	private static final int HEIGHT_MAX = 250;

	public PickerHeightWidget(BaseActivity activity, int mHeight) {
		m_activity = activity;
		this.mHeight = mHeight;
		init();
	}

	private void init() {
		m_popUpView = m_activity.getLayoutInflater().inflate(R.layout.picker_height_widget1, null);
		mDoneView = (ImageButtonWithText) m_popUpView.findViewById(R.id.title_right);
		mCanView = (ImageButtonWithText) m_popUpView.findViewById(R.id.title_left);
		mDoneView.setOnlyText(R.string.Done);
		mCanView.setOnlyText(R.string.cancel);

		m_heightPicker = (WheelView) m_popUpView.findViewById(R.id.height);

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

	private void initDatePicker() {
		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
			}
		};
		int curMonth = mHeight > 0 ? mHeight : TheOneConstants.USER_PROFILE_DEFULT_MONTH;
		m_heightPicker.setViewAdapter(new DateNumericAdapter(m_activity, 20, 250, curMonth - 20));
		m_heightPicker.setCurrentItem(curMonth - 20);
		m_heightPicker.addChangingListener(listener);
	}

	public void setPickerCallBack(IPickerHeightCallback mPickerCallBack) {
		this.mPickerCallBack = mPickerCallBack;
	}

	protected void doPickerFinish() {
		int year = HEIGHT_MIN + m_heightPicker.getCurrentItem();
		if (year < HEIGHT_MIN || year > HEIGHT_MAX) {
			return;
		}
		int height = m_heightPicker.getCurrentItem();
		if (mPickerCallBack != null) {
			mPickerCallBack.onPickerFinish(height);
		}
	}

	protected void doPickerCancle() {
		int year = HEIGHT_MIN + m_heightPicker.getCurrentItem();
		if (year < HEIGHT_MIN || year > HEIGHT_MAX) {
			return;
		}
		int height = m_heightPicker.getCurrentItem();
		if (mPickerCallBack != null) {
			mPickerCallBack.onPickerCancel(height);
		}
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

	public interface IPickerHeightCallback {

		void onPickerFinish(int height);

		void onPickerCancel(int height);

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
}
