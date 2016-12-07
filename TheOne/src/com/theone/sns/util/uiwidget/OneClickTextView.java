package com.theone.sns.util.uiwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class OneClickTextView extends TextView {
	private boolean m_bLongClicked = false;

	public OneClickTextView(Context context) {
		super(context);
	}

	public OneClickTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public OneClickTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean performLongClick() {
		m_bLongClicked = super.performLongClick();
		return m_bLongClicked;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (MotionEvent.ACTION_DOWN == event.getAction()) {
			m_bLongClicked = false;
		}
		return super.onTouchEvent(event);
	}

	public boolean isLongClicked() {
		return m_bLongClicked;
	}
}
