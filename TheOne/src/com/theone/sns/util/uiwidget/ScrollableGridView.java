package com.theone.sns.util.uiwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.GridView;

public class ScrollableGridView extends GridView {

	public static final String TAG = "ScrollableGridView---------->";

	OnTouchBlankPositionListener mTouchBlankPosListener;
	boolean expanded = true;
	Context context;

	public ScrollableGridView(Context context) {
		super(context);
		this.context = context;
	}

	public ScrollableGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public ScrollableGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public boolean isExpanded() {
		return expanded;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (isExpanded()) {
			int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
					MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);

			ViewGroup.LayoutParams params = getLayoutParams();
			params.height = getMeasuredHeight();
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public void setOnTouchBlankPositionListener(OnTouchBlankPositionListener listener) {
		mTouchBlankPosListener = listener;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mTouchBlankPosListener != null) {
			if (!isEnabled()) {
				// A disabled view that is clickable still consumes the touch
				// events, it just doesn't respond to them.
				return isClickable() || isLongClickable();
			}

			if (event.getActionMasked() == MotionEvent.ACTION_UP) {
				final int motionPosition = pointToPosition((int) event.getX(), (int) event.getY());
				if (motionPosition == -1
						|| (motionPosition <= getCount() - 1 && motionPosition >= getCount() - 2)) {
					return mTouchBlankPosListener.onTouchBlankPosition();
				}
			}
		}

		return super.onTouchEvent(event);
	}

}
