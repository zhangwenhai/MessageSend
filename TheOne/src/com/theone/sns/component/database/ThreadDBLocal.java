package com.theone.sns.component.database;

import android.database.sqlite.SQLiteDatabase;

public class ThreadDBLocal {

	public static final ThreadLocal<SQLiteDatabase> userThreadLocal = new ThreadLocal<SQLiteDatabase>();

	public static final ThreadLocal<String> uidThreadLocal = new ThreadLocal<String>();

	public static void set(SQLiteDatabase db, String uid) {

		uidThreadLocal.set(uid);

		userThreadLocal.set(db);
	}

	public static void unset() {

		userThreadLocal.remove();
	}

	public static SQLiteDatabase get(String uid) {

		String uidValue = uidThreadLocal.get();

		if (uidValue == null) {
			return null;
		}

		if (uidValue != uid) {
			return null;
		}

		return userThreadLocal.get();
	}
}
