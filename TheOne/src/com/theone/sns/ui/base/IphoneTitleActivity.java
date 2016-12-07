package com.theone.sns.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.util.uiwidget.ImageButtonWithText;

public abstract class IphoneTitleActivity extends BasicActivity {

	protected View m_rootView;
	private LinearLayout m_contentLayout;
	private TextView m_title;
	private ImageButtonWithText m_leftButton;
	private ImageButtonWithText m_rightButton;
	protected View m_titleView;
	private TextView m_reconnectTitle;
	private ProgressBar m_progress;
	private ProgressBar m_connectProcess;
	private ImageView m_backgrand;
	private boolean showConnectStatus = false;
	private OnClickListener m_backListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			leftButtonBack(v);
			finish();
		}
	};
	private LinearLayout iphoneTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_content);
		m_titleView = findViewById(R.id.iphone_title);
		m_contentLayout = (LinearLayout) findViewById(R.id.iphone_title_content);
		m_title = (TextView) findViewById(R.id.iphone_title_text);
		m_leftButton = (ImageButtonWithText) findViewById(R.id.iphone_title_left);
		m_rightButton = (ImageButtonWithText) findViewById(R.id.iphone_title_right);

		iphoneTitle = (LinearLayout) m_titleView
				.findViewById(R.id.iphone_title1);

		m_leftButton.setVisibility(View.INVISIBLE);
		m_rightButton.setVisibility(View.INVISIBLE);
		m_leftButton.setOnClickListener(m_backListener);

		m_rootView = findViewById(R.id.root_layout_iphone);
		m_backgrand = (ImageView) findViewById(R.id.backgrand);
		m_progress = (ProgressBar) m_titleView
				.findViewById(R.id.title_progress);
		m_reconnectTitle = (TextView) findViewById(R.id.disconnet_title);
		m_connectProcess = (ProgressBar) findViewById(R.id.connect_progress);

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public ImageButtonWithText getLeftButton() {
		return m_leftButton;
	}

	public ImageButtonWithText getRightButton() {
		return m_rightButton;
	}

	@Override
	public void setTitle(int resId) {
		if (m_title != null) {
			m_title.setText(resId);
		}
		super.setTitle(resId);
	}

	public ImageView getbackgrandView() {
		return m_backgrand;
	}

	public void setTitle(CharSequence titleStr) {
		if (m_title != null) {
			m_title.setText(titleStr);
		}
		super.setTitle(titleStr);
	}

	public void setTitle(Spannable titleStr) {
		if (m_title != null) {
			m_title.setText(titleStr);
		}
		super.setTitle(titleStr);
	}

	public TextView getTitleTextView() {
		return m_title;
	}

	public void setLeftButton(int resId, Boolean isString, Boolean isBack) {
		if (m_leftButton == null) {
			return;
		}
		if (isBack) {
			m_leftButton.setBack(resId);
		} else {
			if (isString) {
				m_leftButton.setOnlyText(resId);
			} else {
				m_leftButton.setOnlyImage(resId);
			}
		}
		m_leftButton.setVisibility(View.VISIBLE);
	}

	public void setLeftButton(String str, Boolean isBack) {
		if (m_leftButton == null)
			return;

		if (str == null)
			return;

		if (isBack) {
			m_leftButton.setBack(str);
		} else {
			m_leftButton.setOnlyText(str);
		}
		m_leftButton.setVisibility(View.VISIBLE);
	}

	public void setRightGreenButton(String str, boolean isBack) {
		if (m_rightButton == null)
			return;

		if (str == null)
			return;

		m_rightButton.setOnlyText(str);
		m_rightButton.setVisibility(View.VISIBLE);
	}

	public void setRightButton(int resId, Boolean isString) {
		if (m_rightButton == null)
			return;
		if (isString) {
			m_rightButton.setOnlyText(resId);
		} else {
			m_rightButton.setOnlyImage(resId);
		}
		m_rightButton.setVisibility(View.VISIBLE);
	}

	public void setRightTextEnabled(boolean enabled) {
		if (m_rightButton == null)
			return;
		m_rightButton.setTextEnabled(enabled);
		m_rightButton.setClickable(enabled);
		m_rightButton.setEnabled(enabled);
		m_rightButton.setClickable(enabled);
	}

	public void hideRightButton() {
		if (m_rightButton == null)
			return;

		m_rightButton.setVisibility(View.INVISIBLE);
	}

	public View setSubContent(int viewId) {
		View contentView = getViewRootActivity().getLayoutInflater().inflate(
				viewId, null);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		m_contentLayout.addView(contentView, lp);

		return contentView;
	}

	public View setSubContent(View view) {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		m_contentLayout.addView(view, lp);
		return view;
	}

	public void setShowConntectStatus(boolean showNetStatus) {
		showConnectStatus = showNetStatus;
	}
	
	public void leftButtonBack(View view){
		
	}

	// protected void showTitleProgress(final boolean show) {
	// if (m_progress == null)
	// return;
	// post(new Runnable() {
	// @Override
	// public void run() {
	// if (m_progress == null)
	// return;
	// if (show)
	// m_progress.setVisibility(View.VISIBLE);
	// else
	// m_progress.setVisibility(View.INVISIBLE);
	// }
	// });
	// }
	//
	// protected void showTitleProgress() {
	// showTitleProgress(true);
	// }
	//
	// protected void hideTitleProgress() {
	// showTitleProgress(false);
	// }

	public View getRootView() {
		return m_contentLayout.getChildAt(0);
	}

	public void setRightBoldButton(int resId, boolean isBack) {
		if (m_rightButton == null)
			return;
		m_rightButton.setOnlyText(resId);
		m_rightButton.setTextViewBold();
		m_rightButton.setVisibility(View.VISIBLE);
	}

	/**
	 * 添加显示fragment到iphone_title_content
	 *
	 * @param fragment
	 */
	public void setFragment(Fragment fragment) {
		setFragment(R.id.iphone_title_content, fragment);
	}

	/**
	 * 添加显示fragment
	 *
	 * @param containerId
	 *            fragment父容器id
	 * @param fragment
	 */
	public void setFragment(int containerId, Fragment fragment) {
		// getSupportFragmentManager().beginTransaction().replace(containerId,
		// fragment).commit();
	}
}
