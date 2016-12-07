package com.theone.sns.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.theone.sns.common.FusionConfig;

/**
 * 监听网络连接状况，包括2G,3G，wifi
 * <p>
 */
public class NetworkReceiver extends BroadcastReceiver {
	private static final String TAG = "NetworkReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent == null) {
			return;
		}

		String action = intent.getAction();

		// 检测连接变化的action
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {

			Log.e(TAG, "ConnectivityManager.CONNECTIVITY is changed...");

			if (FusionConfig.isLogin()) {

				Intent pubnubIntent = new Intent(context, PubnubService.class);

				context.startService(pubnubIntent);
			}
		}
	}
}
