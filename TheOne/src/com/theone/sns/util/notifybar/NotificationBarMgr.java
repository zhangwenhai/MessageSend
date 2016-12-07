package com.theone.sns.util.notifybar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.theone.sns.R;

public class NotificationBarMgr {

	private static final String TAG = NotificationBarMgr.class.getName();

	/**
	 * 单例模式
	 */
	private static NotificationBarMgr mNotificationMgr;

	/**
	 * 获取系统通知服务的关键字
	 */
	private static final String NS = Context.NOTIFICATION_SERVICE;

	/**
	 * 系统通知服务管理器对象
	 */
	private static NotificationManager mNotificationManager;

	/**
	 * 上下文
	 */
	private static Context mContext;

	private NotificationBarMgr() {
	}

	/**
	 * 获取消息通知管理的实例对象（单例）
	 * 
	 * @param context
	 *            上下文
	 * @return mNotificationMgr 消息通知管理的实例对象
	 */
	public static synchronized NotificationBarMgr getInstance(Context context) {
		mContext = context;

		// 初始化通知管理类
		initNotifyMgr();

		return mNotificationMgr;
	}

	/**
	 * 初始化通知管理类
	 */
	private static void initNotifyMgr() {
		if (null == mContext) {
			Log.e(TAG, "mContext is null");
		}

		if (null == mNotificationManager) {
			mNotificationManager = (NotificationManager) mContext
					.getSystemService(NS);
		}

		if (null == mNotificationMgr) {
			mNotificationMgr = new NotificationBarMgr();
		}
	}

	/**
	 * 发送消息通知
	 * 
	 * @param notifyInfo
	 *            消息通知对象
	 */
	public void dispatchNotify(NotifyBarInfo notifyInfo, boolean autoCancel) {
		if (null == notifyInfo) {
			Log.e(TAG, "notifyInfo is null");

			return;
		}

		dispatchNotification(notifyInfo.tickerText, notifyInfo.contentTitle,
				notifyInfo.contentText, notifyInfo.notificationIntent,
				notifyInfo.type, autoCancel);
	}

	/**
	 * 清除状态栏消息
	 * 
	 * @param notifyType
	 *            清除的消息类型
	 */
	public void clearNotify(int notifyType) {
		mNotificationManager.cancel(notifyType);
	}

	@SuppressWarnings("static-access")
	private void dispatchNotification(String tickerText, String contentTitle,
			String contentText, Intent notificationIntent, int notifyType,
			boolean autoCancel) {

		long when = System.currentTimeMillis();

		int icon = R.drawable.icon;

		Notification notification = new Notification(icon, tickerText, when);

		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(mContext, contentTitle, contentText,
				contentIntent);

		if (autoCancel) {
			notification.flags = notification.FLAG_AUTO_CANCEL;
		}

		mNotificationManager.notify(notifyType, notification);
	}
}
