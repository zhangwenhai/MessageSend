/*
 * 文件名: TheOneProvider.java
 * 创建人: zhouyujun
 */
package com.theone.sns.component.database;

import java.util.Vector;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.theone.sns.TheOneApp;
import com.theone.sns.component.database.TheOneDatabaseHelper.Tables;

/**
 * SQLiteContentProvider子类，具体实现查询语句的类。
 * 
 * @author zhouyujun
 */
public class TheOneProvider extends SQLiteContentProvider {
	/**
	 * 打印log信息时传入的标志
	 */
	private static final String TAG = "TheOneProvider";

	/**
	 * URI匹配值: 帐号信息表URI
	 */
	public static final int ACCOUNT = 0x00000010;

	public static final int GALLERY = 0x00000020;

	public static final int USER = 0x00000030;

	public static final int COMMENT = 0x00000040;

	public static final int MBLOG = 0x00000050;

	public static final int MBLOG_TAG = 0x00000060;

	public static final int MBLOG_FILTER = 0x00000070;

	public static final int SETTING = 0x00000080;

	public static final int GROUP = 0x00000090;

	public static final int MESSAGE = 0x00000100;

	/**
	 * 需要通知的uri集合
	 */
	private Vector<Uri> mNotifyChangeUri;

	/**
	 * URI键值队
	 */
	private static final UriMatcher URIMATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {

		URIMATCHER.addURI(URIField.AUTHORITY, Tables.ACCOUNT, ACCOUNT);

		URIMATCHER.addURI(URIField.AUTHORITY, Tables.GALLERY, GALLERY);

		URIMATCHER.addURI(URIField.AUTHORITY, Tables.USER, USER);

		URIMATCHER.addURI(URIField.AUTHORITY, Tables.COMMENT, COMMENT);

		URIMATCHER.addURI(URIField.AUTHORITY, Tables.MBLOG, MBLOG);

		URIMATCHER.addURI(URIField.AUTHORITY, Tables.MBLOG_TAG, MBLOG_TAG);

		URIMATCHER
				.addURI(URIField.AUTHORITY, Tables.MBLOG_FILTER, MBLOG_FILTER);

		URIMATCHER.addURI(URIField.AUTHORITY, Tables.SETTING, SETTING);

		URIMATCHER.addURI(URIField.AUTHORITY, Tables.GROUP, GROUP);

		URIMATCHER.addURI(URIField.AUTHORITY, Tables.MESSAGE, MESSAGE);
	}

	/**
	 * 创建关联 provider对象入口
	 *
	 * @return boolean
	 */
	@Override
	public boolean onCreate() {

		Log.d(TAG, "TheOneProvider--->onCreate()");

		return initialize();
	}

	/**
	 * 返回一个DatabaseHelper实例
	 *
	 * 子类重写方法
	 *
	 * @param context
	 *            Context
	 * @return DatabaseHelper
	 */
	@Override
	protected SQLiteDatabase getSQLiteDatabase(Context context) {
		return TheOneDatabaseHelper.getSQLiteDatabase();
	}

	/**
	 * 查询接口<BR>
	 *
	 * @param uri
	 *            Uri
	 * @param projection
	 *            String[] 查询要返回的列
	 * @param selection
	 *            String 条件
	 * @param selectionArgs
	 *            String[] 条件值
	 * @param orderBy
	 *            String 排序字段
	 * @return Cursor 返回数据cursor
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String orderBy) {

		if (!setSqliteDatabase()) {
			return null;
		}

		Cursor cursor = null;

		int match = URIMATCHER.match(uri);
		switch (match) {
		default:
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(getTableNameByMatchCode(match));
			cursor = qb.query(mSqliteDatabase, projection, selection,
					selectionArgs, null, null, orderBy, null);
			break;
		}

		return cursor;
	}

	/**
	 *
	 * 事务中对insert的处理 子类重写方法
	 *
	 * @param uri
	 *            插入的地址描述
	 * @param values
	 *            插入的值
	 * @return 插入后的uri描述
	 */
	@Override
	protected Uri insertInTransaction(Uri uri, ContentValues values) {
		Uri resultUri = null;
		TableHandler tableHandler = getTableHandlerByUri(uri);

		if (null != tableHandler && null != values) {
			long rowId = tableHandler.insert(mSqliteDatabase, null, values);

			if (rowId > 0) {
				resultUri = ContentUris.withAppendedId(uri, rowId);
			}
		} else {
			Log.w(TAG, "insertInTransaction()  fail, uri: " + uri
					+ ", uri or tableName is null...");
		}

		return resultUri;
	}

	/**
	 * 修改接口<BR>
	 *
	 * @param uri
	 *            Uri
	 * @param values
	 *            ContentValues 需要更新的数据
	 * @param whereClause
	 *            String 条件
	 * @param whereArgs
	 *            String[] 条件值
	 * @return count 更新的数目
	 */
	@Override
	protected int updateInTransaction(Uri uri, ContentValues values,
			String selection, String[] selectionArgs) {
		int count = 0;
		TableHandler tableHandler = getTableHandlerByUri(uri);
		if (null != tableHandler && null != values) {
			count = tableHandler.update(mSqliteDatabase, values, selection,
					selectionArgs);
		}

		if (count > 0) {
			switch (URIMATCHER.match(uri)) {
			default:
				break;
			}
		}

		return count;
	}

	/**
	 * 删除接口
	 *
	 * @param uri
	 *            Uri
	 * @param whereClause
	 *            String
	 * @param whereArgs
	 *            String[]
	 * @return int
	 */
	@Override
	protected int deleteInTransaction(Uri uri, String selection,
			String[] selectionArgs) {

		int count = 0;

		TableHandler tableHandler = getTableHandlerByUri(uri);

		if (null != tableHandler) {
			Log.i(TAG, "delete tableName, Uri: " + uri);

			count = tableHandler.delete(mSqliteDatabase, selection,
					selectionArgs);
			if (count > 0) {
			}
		}

		return count;
	}

	/**
	 * 当有数据内容变动时，通知变动的抽象方法 子类继承方法
	 */
	@Override
	protected void notifyChange() {
		synchronized (mNotifyChangeUri) {
			ContentResolver contentResolver = getContext().getContentResolver();
			for (Uri uri : mNotifyChangeUri) {
				Log.d(TAG, "监听到数据变化 " + uri);
				contentResolver.notifyChange(uri, null);
			}
			mNotifyChangeUri.clear();
		}
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 *
	 * @param arg0
	 * @return
	 */

	@Override
	public String getType(Uri arg0) {
		return null;
	}

	/**
	 * 具体处理数据库操作的类
	 */
	private class TableHandler {
		/**
		 * 数据表名称
		 */
		protected String mTableName;

		/**
		 *
		 * 构造方法，传入表名。<BR>
		 *
		 * @param tableName
		 *            表名
		 */
		public TableHandler(String tableName) {
			mTableName = tableName;
		}

		/**
		 * 向指定的数据库中插入数据<BR>
		 *
		 * @param db
		 *            数据库
		 * @param nullColumnHack
		 *            默认空值列
		 * @param values
		 *            插入的值
		 * @return 新插入数据的rowId
		 */
		public long insert(SQLiteDatabase db, String nullColumnHack,
				ContentValues values) {
			return db.insert(mTableName, nullColumnHack, values);
		}

		/**
		 * 删除指定数据库中的数据<BR>
		 *
		 * @param db
		 *            数据库
		 * @param whereClause
		 *            条件语句
		 * @param whereArgs
		 *            条件语句的值
		 * @return 删除的行数
		 */
		public int delete(SQLiteDatabase db, String whereClause,
				String[] whereArgs) {
			return db.delete(mTableName, whereClause, whereArgs);
		}

		/**
		 *
		 * 更新数据库中的数据<BR>
		 *
		 * @param db
		 *            数据库
		 * @param values
		 *            需要更新的数据
		 * @param whereClause
		 *            条件语句
		 * @param whereArgs
		 *            条件
		 * @return 更改的行数
		 */
		public int update(SQLiteDatabase db, ContentValues values,
				String whereClause, String[] whereArgs) {
			return db.update(mTableName, values, whereClause, whereArgs);
		}
	}

	/**
	 *
	 * 通过Uri获得处理目的数据库表的对象<BR>
	 *
	 * @param uri
	 *            数据表所在的ContentProvider地址
	 * @return 具体处理业务逻辑的handler对象
	 */
	private TableHandler getTableHandlerByUri(Uri uri) {
		String tableName = getTableNameByUri(uri);
		TableHandler tableHandler = null;
		if (null != tableName) {
			tableHandler = new TableHandler(tableName);
		}
		return tableHandler;
	}

	/**
	 * 根据Uri匹配出数据库表名<BR>
	 *
	 * @param uri
	 *            Uri
	 * @return 数据库表名
	 */
	private String getTableNameByUri(Uri uri) {
		if (uri != null) {
			final int match = URIMATCHER.match(uri);
			return getTableNameByMatchCode(match);
		}
		return null;
	}

	/**
	 * 初始化NotifyChangeUri
	 */
	private void initNotifyChangeUri() {
		mNotifyChangeUri = new Vector<Uri>();
	}

	/**
	 * 初始化<BR>
	 * 初始化URI集合，初始化各处理具体业务的handler
	 *
	 * @return 是否成功
	 */
	private boolean initialize() {

		if (null == TheOneApp.getContext()) {
			TheOneApp.setContext(this.getContext());
		}

		initNotifyChangeUri();

		return setSqliteDatabase();
	}

	/**
	 * 根据match值匹配出数据库表名
	 *
	 * @param match
	 *            match 解析URI获得的对应的整数值
	 * @return String 数据库表名
	 */
	private String getTableNameByMatchCode(int match) {
		switch (match) {
		case ACCOUNT:
			return Tables.ACCOUNT;
		case GALLERY:
			return Tables.GALLERY;
		case USER:
			return Tables.USER;
		case COMMENT:
			return Tables.COMMENT;
		case MBLOG:
			return Tables.MBLOG;
		case MBLOG_TAG:
			return Tables.MBLOG_TAG;
		case MBLOG_FILTER:
			return Tables.MBLOG_FILTER;
		case SETTING:
			return Tables.SETTING;
		case GROUP:
			return Tables.GROUP;
		case MESSAGE:
			return Tables.MESSAGE;
		default:
			return null;
		}
	}
}
