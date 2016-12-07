package com.theone.sns.util;

import android.content.Intent;

import com.theone.sns.R;
import com.theone.sns.TheOneApp;
import com.theone.sns.common.FusionAction.RegisterAction;
import com.theone.sns.ui.main.MainActivity;
import com.theone.sns.util.notifybar.NotificationBarMgr;
import com.theone.sns.util.notifybar.NotifyBarInfo;

public class NotificationBuilder {

	public static final int CHAT_TYPE = 1;

	public static void chatNotification(int unReadCount) {

		NotificationBarMgr mNotificationMgr = NotificationBarMgr
				.getInstance(TheOneApp.getContext());

		// 通知消息对象
		NotifyBarInfo notifyInfo = null;

		Intent intent = notifyIntent(RegisterAction.MAIN_ACTION);

		intent.putExtra(MainActivity.TAB_ACTIVE_INDEX, 3);

		String content = String.format(TheOneApp.getContext().getResources()
				.getString(R.string.notify_im_content), unReadCount);

		notifyInfo = conventNotification(
				TheOneApp.getContext().getResources()
						.getString(R.string.notify_im_ticker),
				content,
				TheOneApp.getContext().getResources()
						.getString(R.string.notify_im_ticker), intent,
				CHAT_TYPE);

		mNotificationMgr.dispatchNotify(notifyInfo, Boolean.TRUE);
	}

	public static void clearNotification() {

		NotificationBarMgr mNotificationMgr = NotificationBarMgr
				.getInstance(TheOneApp.getContext());

		mNotificationMgr.clearNotify(CHAT_TYPE);
	}

	private static Intent notifyIntent(String action) {

		Intent intentNotify = new Intent(action);

		intentNotify.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_NO_USER_ACTION);

		return intentNotify;
	}

	/**
	 * 构造通知消息对象
	 * 
	 * @param tickerText
	 *            通知状态栏顶部显示的内容
	 * @param contentTitle
	 *            通知状态栏下拉后,消息显示的标题
	 * @param contentText
	 *            通知状态栏下拉后,消息显示的内容
	 * @param intentNotify
	 *            点击通知消息时跳转的Intent对象
	 * @param type
	 *            通知消息的类型
	 * @return 通知消息对象
	 */
	private static NotifyBarInfo conventNotification(String tickerText,
			String contentTitle, String contentText, Intent intentNotify,
			int type) {

		NotifyBarInfo notifyInfo = new NotifyBarInfo();

		notifyInfo.tickerText = tickerText;

		notifyInfo.contentTitle = contentTitle;

		notifyInfo.contentText = contentText;

		notifyInfo.type = type;

		notifyInfo.notificationIntent = intentNotify;

		return notifyInfo;
	}
}
