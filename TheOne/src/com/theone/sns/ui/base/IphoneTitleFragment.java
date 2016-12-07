package com.theone.sns.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.TheOneApp;
import com.theone.sns.ui.base.dialog.CustomAlertDialog;
import com.theone.sns.util.StringUtil;
import com.theone.sns.util.uiwidget.ImageButtonWithText;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class IphoneTitleFragment extends BaseFragment {
	protected Activity context;
	protected ViewGroup rootView;

	private LinearLayout m_contentLayout;
	private TextView m_title;
	private TextView m_reconnectTitle;
	private ImageButtonWithText m_leftButton;
	private ImageButtonWithText m_rightButton;
	private View m_titleView;
	protected View m_titleLayout;

	private ProgressBar m_connectProcess;
	private boolean m_rightPop;
	private boolean showConnectStatus = false;
	private final AtomicBoolean m_isLoading = new AtomicBoolean(false);
	private String m_dialogMsg = null;
	private CustomAlertDialog m_loadingDlg = null;

	/**
	 * 表示当前fragment是否被显示
	 */
	protected AtomicBoolean isOnTop = new AtomicBoolean(false);
	protected LayoutInflater inflater;
	protected boolean isNew = false;
	private ImageView titleImage;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.context = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		this.inflater = inflater;
		if (null == rootView) {
			rootView = (ViewGroup) inflater.inflate(R.layout.tab_content, null);
			m_titleLayout = rootView.findViewById(R.id.iphone_title);
			m_title = (TextView) rootView.findViewById(R.id.iphone_title_text);
			titleImage = (ImageView) rootView.findViewById(R.id.title_image);
			m_contentLayout = (LinearLayout) rootView.findViewById(R.id.iphone_title_content);
			m_leftButton = (ImageButtonWithText) rootView.findViewById(R.id.iphone_title_left);
			m_rightButton = (ImageButtonWithText) rootView.findViewById(R.id.iphone_title_right);
			m_connectProcess = (ProgressBar) rootView.findViewById(R.id.connect_progress);
			m_reconnectTitle = (TextView) rootView.findViewById(R.id.disconnet_title);
			m_leftButton.setVisibility(View.INVISIBLE);
			m_rightButton.setVisibility(View.INVISIBLE);
			onMyCreateView();
			isNew = true;
			return rootView;
		}
		ViewGroup mViewGroup = (ViewGroup) rootView.getParent();
		if (null != mViewGroup) {
			mViewGroup.removeView(rootView);
		}
		isNew = false;
		return rootView;
	}

	protected abstract void onMyCreateView();

	/**
	 * 当当前fragment被展示的时候调用
	 */
	public void onShow() {
		isOnTop.set(true);
	}

	/**
	 * 当当前fragment被切到后台时调用
	 */
	public void onHide() {
		isOnTop.set(false);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	public boolean isActive() {
		return true;
	}

	public ImageButtonWithText getLeftButton() {
		return m_leftButton;
	}

	public ImageButtonWithText getRightButton() {
		return m_rightButton;
	}

	public void setTitle(int resId) {
		if (m_title != null) {
			m_title.setVisibility(View.VISIBLE);
			m_title.setText(resId);
		}
		titleImage.setVisibility(View.GONE);
	}

	public void setTitle(CharSequence titleStr) {
		if (m_title != null) {
			m_title.setVisibility(View.VISIBLE);
			// mOnlinecount_layout.setVisibility(View.GONE);
			m_title.setText(titleStr);
		}
		titleImage.setVisibility(View.GONE);
	}

	public void setTitleWithoutChangeVisible(CharSequence titleStr) {
		if (m_title != null) {
			// mOnlinecount_layout.setVisibility(View.GONE);
			m_title.setText(titleStr);
		}
	}

	public void setTitle(Spannable titleStr) {
		if (m_title != null) {
			m_title.setVisibility(View.VISIBLE);
			// mOnlinecount_layout.setVisibility(View.GONE);
			m_title.setText(titleStr);
		}
		titleImage.setVisibility(View.GONE);
	}

	public void setTitleImage(int resId) {
		if (m_title != null) {
			m_title.setVisibility(View.GONE);
		}
		titleImage.setVisibility(View.VISIBLE);
		titleImage.setImageDrawable(getResources().getDrawable(resId));
	}

	public ImageView getTitleImage() {
		return titleImage;
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

	private View getPopAnchor() {
		if (View.VISIBLE == m_titleView.getVisibility()) {
			return getRightButton();
		}
		return null;
	}

	public void setRightButton(int resId, Boolean isString) {
		if (isString) {
			m_rightButton.setOnlyText(resId);
		} else {
			m_rightButton.setOnlyImage(resId);
		}
		m_rightButton.setVisibility(View.VISIBLE);
	}

	public View setSubContent(int viewId) {
		View contentView = context.getLayoutInflater().inflate(viewId, null);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		m_contentLayout.addView(contentView, lp);

		return contentView;
	}

	public static void hideIME(EditText edit) {
		if (edit == null) {
			return;
		}
		edit.clearFocus();
		InputMethodManager imm = (InputMethodManager) TheOneApp.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
	}

	public static void hideIME(View v) {
		if (v == null) {
			return;
		}
		InputMethodManager imm = (InputMethodManager) TheOneApp.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	public static void hideIME(EditText edit, boolean clearFocus) {
		if (edit == null) {
			return;
		}
		if (clearFocus) {
			edit.clearFocus();
		}
		InputMethodManager imm = (InputMethodManager) TheOneApp.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
	}

	public void setProgressVisible(boolean visible) {
		m_connectProcess.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	/**
	 * 隐藏iphoneTitle标题栏
	 */
	protected void hideIphoneTitle() {
		m_titleLayout.setVisibility(View.GONE);
	}

	public final void showLoadingDialog() {
		showLoadingDialog(getString(R.string.dialog_wait), -1, true, false);
	}

	public final void showForceLoadingDialog() {
		showLoadingDialog(getString(R.string.dialog_wait), -1, false, false);
	}

	public final void showLoadingDialog(String text) {
		showLoadingDialog(text, -1, true, false);
	}

	public final void showWaitDialog(boolean canCancel, boolean backKeyFinish) {
		showLoadingDialog(getString(R.string.dialog_wait), -1, canCancel, backKeyFinish);
	}

	public final void showLoadingDialogNotCancel() {
		showLoadingDialog(getString(R.string.dialog_wait), -1, false, false);
	}

	public final void showSucessDialog(String text) {
		showLoadingDialog(text, R.drawable.alertdialog_success, true, false);
	}

	public final void showFailDialog(String text) {
		showLoadingDialog(text, R.drawable.alertdialog_error, true, false);
	}

	public void showLoadingDialog(final String msg, final int updateImgResId,
			final boolean canCancel, final boolean backKeyFinish) {

		context.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				synchronized (m_isLoading) {

					if (!isActive()) {
						return;
					}

					m_isLoading.set(true);

					if (!StringUtil.equals(msg, m_dialogMsg)) {

						m_dialogMsg = msg;

						synchronized (m_isLoading) {

							if (m_isLoading.get() && m_loadingDlg != null) {

								m_loadingDlg.setMessage(m_dialogMsg);
							}
						}
					}

					showLoadingDialogInner(updateImgResId, canCancel, backKeyFinish);
				}
			}
		});
	}

	private void showLoadingDialogInner(final int resImgId, final boolean canCancel,
			final boolean backKeyFinish) {

		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (m_loadingDlg == null) {

					m_loadingDlg = new CustomAlertDialog(context);

					m_loadingDlg.setOnKeyListener(new DialogInterface.OnKeyListener() {

						@Override
						public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

							switch (keyCode) {

							case KeyEvent.KEYCODE_BACK:

								if (backKeyFinish) {

									synchronized (m_isLoading) {

										m_loadingDlg.dismiss();

										m_isLoading.set(false);

										onDialogBack();

										return true;
									}
								}
							}

							return false;
						}
					});
				}

				m_loadingDlg.setCancelable(canCancel);

				m_loadingDlg.setMessage(m_dialogMsg);

				m_loadingDlg.setUpdateIcon(resImgId);

				m_loadingDlg.show();
			}
		});
	}

	protected void onDialogBack() {
		context.finish();
	}

	public void hideLoadingDialog() {

		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				synchronized (m_isLoading) {

					if (!m_isLoading.get()) {
						return;
					}

					m_isLoading.set(false);

					if (m_loadingDlg != null) {
						m_loadingDlg.dismiss();
					}
				}
			}
		});
	}
}
