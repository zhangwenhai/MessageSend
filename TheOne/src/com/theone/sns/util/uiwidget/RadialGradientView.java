package com.theone.sns.util.uiwidget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

public final class RadialGradientView extends View {

	public float f;

	private final int[] green = { 0x73, 0xa2, 0x17 };
	private final int[] red = { 0xf7, 0x51, 0x51 };

	public RadialGradientView(Context context) {
		super(context);
	}

	public RadialGradientView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setF(double f) {
		if (getHeight() <= 0) {
			return;
		}

		int color = getColor(f);
		setBackgroundColor(color);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		if (oldw == 0 && w > 0) {
			setF(0.0f);
		}
	}

	private int getColor(double f) {
		int r = green[0] + (int) ((red[0] - green[0]) * f);
		int g = green[1] + (int) ((red[1] - green[1]) * f);
		int b = green[2] + (int) ((red[2] - green[2]) * f);

		return Color.rgb(r, g, b);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		setF(0d);
	}
}
