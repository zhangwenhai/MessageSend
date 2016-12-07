package com.theone.sns.util.uiwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.theone.sns.R;
import com.theone.sns.util.HelperFunc;

public class EditTextWithClear extends LinearLayout {
	private EditText m_editText;
	private ImageView m_imgView;
	private boolean m_rightAlign = true;

	private final TextWatcher m_watcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			updateClearButton();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

	};

	private void reset() {
		if (m_editText != null) {
			m_editText.removeTextChangedListener(m_watcher);
			m_editText = null;
		}
		if (m_imgView != null) {
			m_imgView.setOnClickListener(null);
			m_imgView = null;
		}
		removeAllViews();
	}

	public EditTextWithClear(Context context) {
		super(context);
		if (isInEditMode()) {
			return;
		}
		m_rightAlign = true;
	}

	public EditTextWithClear(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		if (isInEditMode()) {
			return;
		}
		m_rightAlign = true;
	}

	public EditTextWithClear(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (isInEditMode()) {
			return;
		}
		m_rightAlign = true;
	}

	public void clearText() {
		if (m_editText != null)
			m_editText.setText("");
	}

	public void updateClearButton() {
		if (m_editText == null || m_imgView == null)
			return;

		if (m_editText.getText().toString().length() < 1)
			m_imgView.setVisibility(View.GONE);
		else
			m_imgView.setVisibility(View.VISIBLE);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (isInEditMode()) {
			return;
		}
		int len = this.getChildCount();
		for (int i = 0; i < len; i++) {
			View v = this.getChildAt(i);
			if (v instanceof EditText) {
				m_editText = (EditText) v;
				break;
			}
		}

		if (m_editText == null)
			return;

		m_editText.addTextChangedListener(m_watcher);
		int sysVersion = Integer.parseInt(VERSION.SDK);
		if (sysVersion >= 11) {
			m_editText.setLongClickable(true);
			m_editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {

				}

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					return false;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					return false;
				}
			});
		} else {
			m_editText.setLongClickable(false);
		}

		m_imgView = new ImageView(getContext());
		m_imgView.setImageResource(R.drawable.selector_btn_close);
		m_imgView.setVisibility(View.GONE);
		m_imgView.setClickable(true);
		m_imgView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearText();
				m_editText.requestFocus();
			}
		});
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		params.setMargins((int) HelperFunc.dip2px(10), 0, (int) HelperFunc.dip2px(10), 0);
		m_imgView.setLayoutParams(params);

		LayoutParams paramstext = (LayoutParams) m_editText.getLayoutParams();
		paramstext.gravity = Gravity.CENTER_VERTICAL;

		removeAllViews();
		if (m_rightAlign) {
			m_editText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			addView(m_editText, paramstext);
			addView(m_imgView);
		} else {
			m_editText.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
			addView(m_imgView);
			addView(m_editText, paramstext);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (isInEditMode()) {
			return;
		}
		// reset();
	}
}
