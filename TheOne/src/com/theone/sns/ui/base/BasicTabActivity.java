/*
 * 文件名: BasicTabActivity.java
 * 创建人: zhouyujun
 */
package com.theone.sns.ui.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.theone.sns.common.FusionAction;
import com.theone.sns.framework.ui.BaseTabActivity;

/**
 * tab activity 基类
 * 
 * @author zhouyujun
 */
public abstract class BasicTabActivity extends BaseTabActivity {

	/**
	 * 关闭应用程序的广播接收器
	 */
	private CloseAppBroadcastReceiver closeApp;

	/**
	 * 启动
	 * 
	 * @param savedInstanceState
	 *            Bundle
	 * @see com.huawei.basic.android.im.framework.ui.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 关闭程序的广播接收
		closeApp = new CloseAppBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(FusionAction.TheOneApp.ACTION_CLOSE_APPLICATION);
		registerReceiver(closeApp, intentFilter);
	}

	/**
	 * 销毁
	 *
	 * @see com.huawei.basic.android.zone.framework.ui.BaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		unregisterReceiver(closeApp);
		super.onDestroy();
	}

	/**
	 * 关闭应用程序接收器
	 */
	public class CloseAppBroadcastReceiver extends BroadcastReceiver {
		private static final String TAG = "CloseAppBroadcastReceiver";

		@Override
		public void onReceive(final Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(FusionAction.TheOneApp.ACTION_CLOSE_APPLICATION)) {
				Log.d(TAG, "Close Activity "
						+ BasicTabActivity.this.getClass().getName());
				BasicTabActivity.this.finish();
			}
		}
	}

	/**
	 * 初始化logic的方法，由子类实现<BR>
	 * 在该方法里通过getLogicByInterfaceClass获取logic对象
	 * 
	 * @see com.huawei.basic.android.im.framework.ui.BaseTabActivity#initLogics()
	 */
	@Override
	protected void initLogics() {
		// TODO Auto-generated method stub
	}
}
