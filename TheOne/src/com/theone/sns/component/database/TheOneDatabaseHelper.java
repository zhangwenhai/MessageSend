package com.theone.sns.component.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.theone.sns.TheOneApp;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.logic.adapter.db.model.AccountColumns;
import com.theone.sns.logic.adapter.db.model.CommentColumns;
import com.theone.sns.logic.adapter.db.model.FilterColumns;
import com.theone.sns.logic.adapter.db.model.GalleryColumns;
import com.theone.sns.logic.adapter.db.model.GroupColumns;
import com.theone.sns.logic.adapter.db.model.MBlogColumns;
import com.theone.sns.logic.adapter.db.model.MessageColumns;
import com.theone.sns.logic.adapter.db.model.SettingColumns;
import com.theone.sns.logic.adapter.db.model.TagColumns;
import com.theone.sns.logic.adapter.db.model.UserColumns;

public class TheOneDatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "TheOneDatabaseHelper";

	public static String DATABASE_NAME = "theone.db";

	private static final int DATABASE_VERSION = 1;

	private final byte[] m_lock = new byte[0];

	private static Map<String, TheOneDatabaseHelper> m_dbHelperMap = new HashMap<String, TheOneDatabaseHelper>();

	@Override
	public void onCreate(SQLiteDatabase db) {

		createTables(db, 0);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		Log.d(TAG, "TheOneDatabaseHelper onUpgrade");

		try {
			createTables(db, oldVersion);

			synchronized (m_lock) {
			}

		} catch (Exception e) {
		}
	}

	private void createTables(SQLiteDatabase db, int oldVersion) {

		synchronized (m_lock) {

			try {

				AccountColumns.createAccountTable(db);

				GalleryColumns.createGalleryTable(db);

				UserColumns.createUserTable(db);

				CommentColumns.createCommentTable(db);

				MBlogColumns.createMBlogTable(db);

				TagColumns.createTagTable(db);

				FilterColumns.createFilterTable(db);

				SettingColumns.createSettingTable(db);

				GroupColumns.createGroupTable(db);

				MessageColumns.createMessageTable(db);

			} catch (Exception e) {
				Log.e(TAG, "createTable() exception:", e);
			}
		}
	}

	public interface Tables {

		public static final String ACCOUNT = "accout";

		public static final String USER = "user";

		public static final String GALLERY = "gallery";

		public static final String COMMENT = "comment";

		public static final String MBLOG = "mblog";

		public static final String MBLOG_TAG = "tag";

		public static final String MBLOG_FILTER = "filter";

		public static final String SETTING = "setting";

		public static final String GROUP = "groupinfo";

		public static final String MESSAGE = "messageinfo";
	}

	public final static SQLiteDatabase getSQLiteDatabase() {

		String userId = FusionConfig.getInstance().getUserId();

		SQLiteDatabase threadDB = ThreadDBLocal.get(userId);

		if (threadDB != null) {

			return threadDB;
		}

		TheOneDatabaseHelper dbHelper = getDatabaseHelper();

		if (dbHelper == null) {

			return null;
		}

		try {

			SQLiteDatabase db = dbHelper.getWritableDatabase();

			if (null == db) {

				return null;
			}

			ThreadDBLocal.set(db, userId);

			return db;

		} catch (Exception e) {

			m_dbHelperMap.put(userId, null);

			return null;
		}
	}

	public final static SQLiteDatabase getSQLiteDatabaseById(String uid) {

		SQLiteDatabase threadDB = ThreadDBLocal.get(uid);

		if (threadDB != null) {

			return threadDB;
		}

		TheOneDatabaseHelper dbHelper = getDatabaseHelper(uid);

		if (dbHelper == null) {

			return null;
		}

		try {

			SQLiteDatabase db = dbHelper.getWritableDatabase();

			if (null == db) {

				return null;
			}

			ThreadDBLocal.set(db, uid);

			return db;

		} catch (Exception e) {

			m_dbHelperMap.put(uid, null);

			return null;
		}
	}

	private final static TheOneDatabaseHelper getDatabaseHelper() {

		return getDatabaseHelper(FusionConfig.getInstance().getUserId());
	}

	private final static TheOneDatabaseHelper getDatabaseHelper(String userId) {

		try {
			if (TextUtils.isEmpty(userId)) {
				return null;
			}

			TheOneDatabaseHelper dbHelper = m_dbHelperMap.get(userId);

			if (dbHelper == null) {

				dbHelper = new TheOneDatabaseHelper(TheOneApp.getContext(),
						userId);

				m_dbHelperMap.put(userId, dbHelper);
			}

			return dbHelper;

		} catch (Exception e) {

			m_dbHelperMap.put(userId, null);
		}

		return null;
	}

	private TheOneDatabaseHelper(Context context, String userId) {

		super(context, DatabaseFileUtil.getDBPath(DATABASE_NAME, userId), null,
				DATABASE_VERSION);
	}

	/**
	 * 数据库操作异常控制开关。开发调试阶段打开该开关,正式上线须关闭。
	 */
	public static final boolean IS_PRINT_EXCEPTION = true;

	/**
	 * 根据异常控制开关打印异常<BR>
	 *
	 * @param e
	 *            异常
	 */
	public static void printException(Exception e) {
		if (IS_PRINT_EXCEPTION) {
			throw new RuntimeException(e);
		} else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			e.printStackTrace(ps);

			Log.e(TAG, new String(baos.toByteArray()));

			try {
				baos.close();
			} catch (IOException e1) {
				TheOneDatabaseHelper.printException(e1);
			}
		}
	}

	/**
	 * 关闭游标<BR>
	 *
	 * @param cursor
	 *            要关闭的游标对象
	 */
	public static void closeCursor(Cursor cursor) {
		if (null != cursor) {
			cursor.close();
		}
	}
}
