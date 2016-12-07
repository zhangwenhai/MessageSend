/*
 * 文件名: URIField.java
 * 创建人: zhouyujun
 */
package com.theone.sns.component.database;

import android.net.Uri;

import com.theone.sns.component.database.TheOneDatabaseHelper.Tables;

/**
 * Provieder中要用到的URI常量集合
 * 
 * @author zhouyujun
 */
public class URIField {
	/**
	 * 系统数据库操作权限/provider权限，与AndroidManifest.xml中的provider中的配置一致
	 */
	public static final String AUTHORITY = "com.theone.baseline.database";

	/**
	 * 系统数据库操作权限URI: 系统 provider URI
	 */
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

	/**
	 * 基本数据库表操作URI: 帐号信息表
	 */
	public static final Uri ACCOUNT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + Tables.ACCOUNT);

	public static final Uri GALLERY_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + Tables.GALLERY);

	public static final Uri USER_URI = Uri.parse("content://" + AUTHORITY + "/"
			+ Tables.USER);

	public static final Uri COMMENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + Tables.COMMENT);

	public static final Uri MBLOG_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + Tables.MBLOG);

	public static final Uri MBLOG_TAG_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + Tables.MBLOG_TAG);

	public static final Uri MBLOG_FILTER_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + Tables.MBLOG_FILTER);

	public static final Uri SETTING_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + Tables.SETTING);

	public static final Uri GROUP_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + Tables.GROUP);

	public static final Uri MESSAGE_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + Tables.MESSAGE);
}
