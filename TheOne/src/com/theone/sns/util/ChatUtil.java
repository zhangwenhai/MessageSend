package com.theone.sns.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Vibrator;
import android.text.TextUtils;

import com.theone.sns.R;
import com.theone.sns.TheOneApp;
import com.theone.sns.common.FusionCode.SettingKey;
import com.theone.sns.logic.adapter.db.SettingDbAdapter;

public class ChatUtil {

	public static void messageComePlayMedia() {

		// 手机铃声
		if (SettingDbAdapter.getInstance().getBoolean(
				SettingKey.SOUND_NOTIFY_KEY, true)) {

			MediaPlayer player = MediaPlayer.create(TheOneApp.getContext(),
					R.raw.messagecome);

			player.start();

			player.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					mp.release();
				}
			});
		}

		// 手机震动
		if (SettingDbAdapter.getInstance().getBoolean(
				SettingKey.VIBRATE_NOTIFY_KEY, true)) {

			Vibrator v = (Vibrator) TheOneApp.getContext().getSystemService(
					Context.VIBRATOR_SERVICE);

			v.vibrate(500);
		}
	}

	public static boolean isBackgroundRunning() {

		String currentActivity = "";

		ActivityManager activityManager = (ActivityManager) TheOneApp
				.getContext().getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);

		if (null != runningTasks && runningTasks.size() > 0) {

			RunningTaskInfo runningTaskInfo = runningTasks.get(0);

			if (null != runningTaskInfo) {

				ComponentName component = runningTaskInfo.topActivity;

				if (null != component) {
					currentActivity = component.getClassName();
				}
			}
		}

		if (TextUtils.isEmpty(currentActivity)
				|| currentActivity.indexOf(TheOneApp.getContext()
						.getPackageName()) < 0) {
			return true;
		}

		return false;
	}
}
