package com.theone.sns.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.theone.sns.common.FusionConfig;

public class BootReceiver extends BroadcastReceiver {

	private static final String TAG = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent arg1) {

		Log.i(TAG, "PubNub BootReceiver Starting");

		if (FusionConfig.isLogin()) {

			Intent intent = new Intent(context, PubnubService.class);

			context.startService(intent);
		}

		Log.i(TAG, "PubNub BootReceiver Started");
	}
}
