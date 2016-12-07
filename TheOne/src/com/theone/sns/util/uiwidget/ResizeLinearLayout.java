package com.theone.sns.util.uiwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ResizeLinearLayout extends LinearLayout {

	private ResizeLayoutCallback m_callback = null;

	public ResizeLinearLayout(Context context) {
		super(context);
	}

	public ResizeLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setCallback(ResizeLayoutCallback callback) {
		m_callback = callback;
	}

	@Override
	protected void onSizeChanged(int w, final int h, int oldw, final int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (null != m_callback) {
			m_callback.onSizeChanged(w, h, oldw, oldh);
		}
	}
}
