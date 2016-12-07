package com.theone.sns.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TheOneService extends Service {

	private static final String TAG = "TheOneService";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "on create");
	}
}
