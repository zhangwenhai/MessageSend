package com.theone.sns.ui.base.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.theone.sns.R;

public final class CustomAlertDialog extends AlertDialog {
	private TextView m_textView;
	private CharSequence m_tip;
	private View m_progress;
	private ImageView m_imageView;
	private int m_IconId;

	public CustomAlertDialog(Context context) {
		super(context);
	}

	@Override
	public void setMessage(CharSequence message) {
		m_tip = message;
		if (null != m_textView) {
			m_textView.setText(message);
		}
	}

	public void setUpdateIcon(int resImgId) {
		m_IconId = resImgId;
		updateCustomIcon();
	}

	private void updateCustomIcon() {
		if (null == m_imageView || null == m_progress) {
			return;
		}

		if (m_IconId > 0) {
			m_imageView.setBackgroundResource(m_IconId);
			m_imageView.setVisibility(View.VISIBLE);
			m_progress.setVisibility(View.GONE);
		} else {
			m_imageView.setVisibility(View.GONE);
			m_progress.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_dialog);
		m_textView = (TextView) findViewById(R.id.alert_text);
		m_textView.setText(m_tip);
		m_progress = findViewById(R.id.alert_progress);
		m_imageView = (ImageView) findViewById(R.id.alert_icon);
		updateCustomIcon();
	}
}
