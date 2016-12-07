package com.theone.sns.util.uiwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.theone.sns.R;

public class ImageButtonWithText extends LinearLayout {
	private TextView m_textView;
	private ImageView m_imgView;
	private LinearLayout m_container;

	/**
	 * value:default Value
	 */
	private float m_textSize = 16;
	private Context paramContext;

	public ImageButtonWithText(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		this.paramContext = paramContext;
		m_container = (LinearLayout) LayoutInflater.from(paramContext).inflate(
				R.layout.image_button, this, true);
		m_imgView = (ImageView) m_container.findViewById(R.id.title_btn_iv);
		m_textView = (TextView) m_container.findViewById(R.id.title_btn_tv);
		TypedArray typedArray = paramContext.obtainStyledAttributes(
				paramAttributeSet, R.styleable.ImageButtonWithText);
		m_textSize = typedArray
				.getFloat(R.styleable.ImageButtonWithText_imageButtonTextSize,
						m_textSize);
		m_textView.setTextSize(m_textSize);
		this.setClickable(true);
	}

	public final void setOnlyImage(int paramInt) {
		setVisibility(View.VISIBLE);
		m_imgView.setImageResource(paramInt);
		m_imgView.setDuplicateParentStateEnabled(true);
		m_imgView.setVisibility(View.VISIBLE);
		m_textView.setVisibility(View.GONE);
	}

	public final void setOnlyText(int resid) {
		setVisibility(View.VISIBLE);
		m_textView.setText(resid);
		m_textView.setVisibility(View.VISIBLE);
		m_imgView.setVisibility(View.GONE);
	}

	public final void setOnlyText(String str) {
		setVisibility(View.VISIBLE);
		m_textView.setText(str);
		m_textView.setVisibility(View.VISIBLE);
		m_imgView.setVisibility(View.GONE);
	}

	public final void setBack(int resId) {
		setVisibility(View.VISIBLE);
		m_textView.setText(resId);
		Drawable img_off = paramContext.getResources().getDrawable(
				R.drawable.icon_back_selector);
		img_off.setBounds(0, 0, img_off.getMinimumWidth(),
				img_off.getMinimumHeight());
		m_textView.setCompoundDrawables(img_off, null, null, null);
		m_textView.setCompoundDrawablePadding(10);
		m_textView.setVisibility(View.VISIBLE);
		m_imgView.setVisibility(View.GONE);
	}

	public final void setBack(String st) {
		setVisibility(View.VISIBLE);
		m_textView.setText(st);
		Drawable img_off = paramContext.getResources().getDrawable(
				R.drawable.icon_back_selector);
		img_off.setBounds(0, 0, img_off.getMinimumWidth(),
				img_off.getMinimumHeight());
		m_textView.setCompoundDrawables(img_off, null, null, null);
		m_textView.setCompoundDrawablePadding(10);
		m_textView.setVisibility(View.VISIBLE);
		m_imgView.setVisibility(View.GONE);
	}

	public String getTitle() {
		return m_textView.getText().toString();
	}

	public void setTextColor(int color) {
		m_textView.setTextColor(color);
	}

	public void setTextEnabled(boolean enabled) {
		m_textView.setEnabled(enabled);
	}

	public void setTextViewBold() {
		m_textView.setTypeface(Typeface.DEFAULT_BOLD);
	}

	public boolean isButtonVisible() {
		return getVisibility() == View.VISIBLE
				&& (m_imgView.getVisibility() == View.VISIBLE || m_textView
						.getVisibility() == View.VISIBLE);
	}

	public void reset() {
		m_textView = null;
		m_imgView = null;
		if (m_container != null) {
			m_container.removeAllViews();
			m_container = null;
		}
		removeAllViews();
	}
}
