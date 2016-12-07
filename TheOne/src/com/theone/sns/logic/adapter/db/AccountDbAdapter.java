package com.theone.sns.logic.adapter.db;

import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.theone.sns.TheOneApp;
import com.theone.sns.common.FusionCode.CommonColumnsValue;
import com.theone.sns.component.database.TheOneDatabaseHelper;
import com.theone.sns.component.database.URIField;
import com.theone.sns.logic.adapter.db.model.AccountColumns;
import com.theone.sns.logic.model.account.Account;
import com.theone.sns.util.StringUtil;

public class AccountDbAdapter {

	/**
	 * TAG
	 */
	private static final String TAG = "AccountDbAdapter";

	/**
	 * AccountAdapter对象
	 */
	private static AccountDbAdapter sInstance;

	/**
	 * 数据库表内容解释器对象
	 */
	private ContentResolver mCr;

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            上下文
	 */
	private AccountDbAdapter() {

		mCr = TheOneApp.getResolver();
	}

	/**
	 *
	 * 获取AccountAdapter对象<BR>
	 * 单例
	 *
	 * @param context
	 *            上下文
	 * @return AccountAdapter
	 */
	public static synchronized AccountDbAdapter getInstance() {

		if (null == sInstance) {

			sInstance = new AccountDbAdapter();
		}

		return sInstance;
	}

	/**
	 *
	 * 插入账号信息<BR>
	 *
	 * @param account
	 *            插入对象
	 * @return 成功：插入后记录的行数<br>
	 *         失败：-1
	 */
	public long insertAccount(Account account) {

		long result = -1;

		if (null != account) {

			Uri uri = URIField.ACCOUNT_URI;

			ContentValues cv = AccountColumns.setValues(account);

			Uri resultUri = mCr.insert(uri, cv);

			if (null != resultUri) {

				result = ContentUris.parseId(resultUri);
			}

		} else {
			Log.w(TAG, "insertAccount fail, account is null...");
		}

		return result;
	}

	public int updateAccountCharacter(String userId, String character) {

		int result = -1;

		if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(character)) {

			Log.e(TAG, "updateAccountCharacter fail, parameter is null");

			return result;
		}

		Uri uri = URIField.ACCOUNT_URI;

		ContentValues cv = new ContentValues();

		cv.put(AccountColumns.CHARACTER, character);

		result = mCr.update(uri, cv, AccountColumns.USER_ID + "=?",
				new String[] { userId });

		return result;
	}

	public int updateInterest(String userId, List<String> interests) {

		int result = -1;

		if (null == interests || interests.isEmpty()) {

			Log.e(TAG, "updateInterest fail, interests is null");

			return result;
		}

		Uri uri = URIField.ACCOUNT_URI;

		ContentValues cv = new ContentValues();

		cv.put(AccountColumns.INTERESTS, StringUtil.listToString(interests));

		result = mCr.update(uri, cv, AccountColumns.USER_ID + "=?",
				new String[] { userId });

		return result;
	}

	public int deleteAllAccount() {

		int result = -1;

		Uri uri = URIField.ACCOUNT_URI;

		result = mCr.delete(uri, null, null);

		Log.i(TAG, "deleteAllAccount, result = " + result);

		return result;
	}

	public Account getCurrentAccount(String userId) {

		Account account = null;

		if (TextUtils.isEmpty(userId)) {

			Log.e(TAG, "getCurrentAccount error, userId is null");

			return account;
		}

		Cursor cursor = null;

		Uri uri = URIField.ACCOUNT_URI;

		try {

			cursor = mCr.query(uri, null, AccountColumns.USER_ID + "=?",
					new String[] { userId }, null);

			if (null != cursor && cursor.moveToFirst()) {

				account = AccountColumns.parseCursorToAccount(cursor);
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return account;
	}

	public long loginOutAccount(String userId) {

		long result = -1;

		if (TextUtils.isEmpty(userId)) {

			Log.e(TAG, "loginOutAccount fail, userId is null");

			return result;
		}

		Uri uri = URIField.ACCOUNT_URI;

		ContentValues cv = new ContentValues();

		cv.put(AccountColumns.IS_LOGIN, CommonColumnsValue.FALSE_VALUE);

		result = mCr.update(uri, cv, AccountColumns.USER_ID + "=?",
				new String[] { userId });

		Log.i(TAG, "loginOutAccount, result = " + result);

		return result;
	}
}
