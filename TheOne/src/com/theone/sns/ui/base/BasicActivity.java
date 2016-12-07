/*
 * 文件名: BasicActivity.java
 * 创建人: zhouyujun
 */
package com.theone.sns.ui.base;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap.Config;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.theone.sns.R;
import com.theone.sns.TheOneApp;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.framework.logic.BaseLogicBuilder;
import com.theone.sns.framework.ui.LauncheActivity;
import com.theone.sns.logic.LogicBuilder;
import com.theone.sns.ui.base.dialog.CustomAlertDialog;
import com.theone.sns.util.StringUtil;

/**
 * UI 层基类Activity<BR>
 * 包含UI 层的公用弹出框之类
 *
 * @author zhouyujun
 */
public abstract class BasicActivity extends LauncheActivity {

	/**
	 * Log TAG
	 */
	private static final String TAG = "BaisicActivity";

	/**
	 * 1.5秒
	 */
	private static final int TOAST_TIME = 1500;

	/**
	 * Toast对象
	 */
	private Toast mToast;

	private boolean m_isActive = true;

	private final AtomicBoolean m_isLoading = new AtomicBoolean(false);

	private CustomAlertDialog m_loadingDlg = null;

	private String m_dialogMsg = null;

	/**
	 * 关闭应用程序的广播接收器
	 */
	private CloseAppBroadcastReceiver closeApp;

	protected DisplayImageOptions options;

	protected DisplayImageOptions optionsForUserIcon;

	protected DisplayImageOptions optionsForChatImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 关闭程序的广播接收
		closeApp = new CloseAppBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(FusionAction.TheOneApp.ACTION_CLOSE_APPLICATION);
		registerReceiver(closeApp, intentFilter);

		options = new DisplayImageOptions.Builder()
				.bitmapConfig(Config.RGB_565)
				.showImageOnLoading(R.drawable.background)
				.showImageForEmptyUri(R.drawable.background)
				.showImageOnFail(R.drawable.background).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(10)).build();

		optionsForUserIcon = new DisplayImageOptions.Builder()
				.bitmapConfig(Config.RGB_565)
				.showImageOnLoading(R.drawable.background)
				.showImageForEmptyUri(R.drawable.home_user_icon)
				.showImageOnFail(R.drawable.home_user_icon).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();

		optionsForChatImage = new DisplayImageOptions.Builder()
				.bitmapConfig(Config.RGB_565)
				.showImageOnLoading(R.drawable.background)
				.showImageForEmptyUri(R.drawable.background)
				.showImageOnFail(R.drawable.background).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		FusionConfig.currentActivity = this;
		m_isActive = true;
		super.onResume();
	}

	@Override
	protected void initSystem(Context context) {
		FusionConfig.getInstance().setClientVersion(getAppVersionName());
	}

	/**
	 * 当焦点停留在view上时，隐藏输入法栏
	 *
	 * @param view
	 *            view
	 */
	protected void hideInputWindow(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		if (imm != null && view != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	protected void showIME(EditText edit) {
		if (edit == null)
			return;
		edit.requestFocus();
		if (getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
			InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.toggleSoftInput(0, 0);
		}
	}

	public static void hideIME(EditText edit, boolean clearFocus) {
		if (edit == null) {
			return;
		}
		if (clearFocus) {
			edit.clearFocus();
		}
		InputMethodManager imm = (InputMethodManager) TheOneApp.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
	}

	/**
	 * 获取BaseLogicBuilder单例 获得设置全局的logic建造管理对象
	 *
	 * @param context
	 *            Context
	 * @return Context
	 */
	@Override
	protected BaseLogicBuilder createLogicBuilder(Context context) {
		return LogicBuilder.getInstance(context);
	}

	/**
	 * 检查网络
	 *
	 * @return 是否连接
	 */
	public boolean checkNet() {
		// 检查网络
		ConnectivityManager manger = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manger.getActiveNetworkInfo();
		if (!(info != null && info.isConnected())) {
			showToast(R.string.login_check_network);
			return false;
		}
		return true;
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
		showLoadingDialog(getString(R.string.dialog_wait), -1, canCancel,
				backKeyFinish);
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

		post(new Runnable() {

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

					showLoadingDialogInner(updateImgResId, canCancel,
							backKeyFinish);
				}
			}
		});
	}

	private void showLoadingDialogInner(final int resImgId,
			final boolean canCancel, final boolean backKeyFinish) {

		post(new Runnable() {
			@Override
			public void run() {

				if (m_loadingDlg == null) {

					m_loadingDlg = new CustomAlertDialog(getViewRootActivity());

					m_loadingDlg.setOnKeyListener(new OnKeyListener() {

						@Override
						public boolean onKey(DialogInterface dialog,
								int keyCode, KeyEvent event) {

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

	public void hideLoadingDialog() {

		post(new Runnable() {
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

	public void hideLoadingDialogDelayed() {

		postDelayed(new Runnable() {
			public void run() {
				hideLoadingDialog();
			}
		}, 1500);
	}

	public void hideLoadingDialogDelayed(int i) {

		postDelayed(new Runnable() {
			public void run() {
				hideLoadingDialog();
			}
		}, i);
	}

	public boolean isActive() {
		return m_isActive;
	}

	protected void onDialogBack() {
		finish();
	}

	public final boolean post(Runnable r) {
		Handler h = getHandler();
		if (h == null)
			return false;
		return h.post(r);
	}

	public final boolean postDelayed(Runnable r, long delay) {
		Handler h = getHandler();
		if (h == null)
			return false;
		return h.postDelayed(r, delay);
	}

	/**
	 * 根据资源id show toast<BR>
	 *
	 * @param msgId
	 *            字符串id
	 */
	public void showToast(int msgId) {
		showToast(getResources().getString(msgId));
	}

	/**
	 * 根据字符串 show toast<BR>
	 *
	 * @param message
	 *            字符串
	 */
	public void showToast(CharSequence message) {
		if (mToast == null) {
			mToast = Toast.makeText(getApplicationContext(), message,
					Toast.LENGTH_SHORT);
		} else {
			mToast.setText(message);
		}
		mToast.show();
	}

	/**
	 * 根据资源id show toast keep 1.5s<BR>
	 *
	 * @param msgId
	 *            字符串id
	 */
	protected void showToastKeepTime(int msgId) {
		showToastKeepTime(getResources().getString(msgId));
	}

	/**
	 * 根据字符串 show toast<BR>
	 *
	 * @param message
	 *            字符串
	 */
	protected void showToastKeepTime(CharSequence message) {
		if (mToast == null) {
			mToast = Toast.makeText(getApplicationContext(), message,
					TOAST_TIME);
		} else {
			mToast.setText(message);
		}
		mToast.show();
	}

	/**
	 * 返回当前程序版本名
	 *
	 * @return 版本号
	 */
	protected String getAppVersionName() {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = getPackageManager();
			PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			Log.i(TAG, "Exception", e);
		}
		return versionName;
	}

	/**
	 * 关闭应用程序接收器
	 */
	public class CloseAppBroadcastReceiver extends BroadcastReceiver {

		/**
		 * 关闭应用程序的TAG
		 */
		private static final String TAG = "CloseAppBroadcastReceiver";

		@Override
		public void onReceive(final Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(FusionAction.TheOneApp.ACTION_CLOSE_APPLICATION)) {
				Log.d(TAG, "Close Activity "
						+ BasicActivity.this.getClass().getName());
				BasicActivity.this.finish();
			}
		}
	}

	public final Activity getViewRootActivity() {
		Activity ret = this;
		if (ret.getParent() != null) {
			ret = ret.getParent();
		}
		return ret;
	}

	@Override
	protected void onPause() {
		m_isActive = false;
		super.onPause();
	}

	/**
	 * 销毁
	 */
	@Override
	protected void onDestroy() {
		m_isActive = false;
		unregisterReceiver(closeApp);
		super.onDestroy();
	}
}
