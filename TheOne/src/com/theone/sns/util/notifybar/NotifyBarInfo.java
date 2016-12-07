package com.theone.sns.util.notifybar;

import android.content.Intent;

public class NotifyBarInfo {
	/**
	 * 通知状态栏顶部显示的内容
	 */
	public String tickerText;

	/**
	 * 通知状态栏下拉后,消息显示的标题
	 */
	public String contentTitle;

	/**
	 * 通知状态栏下拉后,消息显示的内容
	 */
	public String contentText;

	/**
	 * 点击通知消息时跳转的Intent对象
	 */
	public Intent notificationIntent;

	/**
	 * 消息类型
	 */
	public int type;
}
