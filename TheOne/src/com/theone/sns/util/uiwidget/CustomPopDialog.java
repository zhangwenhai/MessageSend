package com.theone.sns.util.uiwidget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.theone.sns.R;

public class CustomPopDialog extends AlertDialog {

	private View m_contentView;

	public CustomPopDialog(Context context, View contentView) {
		super(context);
		m_contentView = contentView;
		setCancelable(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(m_contentView);

		getWindow().setGravity(Gravity.BOTTOM);
		getWindow().setWindowAnimations(R.style.PopupAnimation);
		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
	}
}
