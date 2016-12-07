package com.theone.sns.logic.adapter.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.theone.sns.common.FusionCode.CommonColumnsValue;
import com.theone.sns.component.database.TheOneDatabaseHelper.Tables;
import com.theone.sns.logic.model.account.Account;
import com.theone.sns.logic.model.account.base.Config;
import com.theone.sns.logic.model.account.base.Profile;
import com.theone.sns.logic.model.account.base.Token;
import com.theone.sns.util.StringUtil;

/**
 * 帐号信息数据库字段<BR>
 */
public class AccountColumns {

	private static final String TAG = "AccountColumns";

	/**
	 * 非业务ID,自增长
	 */
	public static final String ID = "_id";

	/**
	 * 登录帐号(手机号码或者邮箱)
	 */
	public static final String LOGIN_ACCOUNT = "loginAccount";

	/**
	 * 用户ID
	 */
	public static final String USER_ID = "userId";

	/**
	 * 用户名字
	 */
	public static final String NAME = "name";

	/**
	 * 头像URL
	 */
	public static final String AVATAR_URL = "avatar_url";

	/**
	 * 用户属性
	 */
	public static final String CHARACTER = "character";

	/**
	 * 用户手机号码是否验证
	 */
	public static final String PHONE_VERIFIED = "phoneVerified";

	/**
	 * 用户邮箱是否验证
	 */
	public static final String EMAIL_VERIFIED = "emailVerified";

	/**
	 * 是否是上次用户登录的账号 (非当前用户是0，是当前用户是1)
	 */
	public static final String IS_LOGIN = "isLogin";

	/**
	 * 交友目的
	 */
	public static final String PURPOSES = "purposes";

	/**
	 * 兴趣爱好
	 */
	public static final String INTERESTS = "interests";

	/**
	 * access Token
	 */
	public static final String ACCESS_TOKEN = "accessToken";

	/**
	 * upload Token
	 */
	public static final String UPLOAD_TOKEN = "uploadToken";

	public static final String PUBNUB_SUB_KEY = "pubnubSubKey";

	/**
	 * 根据游标解析用户账号信息<BR>
	 * 
	 * @param cursor
	 *            游标对象
	 * @return 账号对象
	 */
	public static Account parseCursorToAccount(Cursor cursor) {

		Account account = new Account();

		Profile profile = new Profile();

		Token token = new Token();

		Config config = new Config();

		account.loginAccount = cursor.getString(cursor
				.getColumnIndex(LOGIN_ACCOUNT));

		account.isLogin = cursor.getInt(cursor.getColumnIndex(IS_LOGIN)) == CommonColumnsValue.TRUE_VALUE;

		profile._id = cursor.getString(cursor.getColumnIndex(USER_ID));

		profile.name = cursor.getString(cursor.getColumnIndex(NAME));

		profile.avatar_url = cursor
				.getString(cursor.getColumnIndex(AVATAR_URL));

		profile.character = cursor.getString(cursor.getColumnIndex(CHARACTER));

		profile.phone_verified = cursor.getInt(cursor
				.getColumnIndex(PHONE_VERIFIED)) == CommonColumnsValue.TRUE_VALUE;

		profile.email_verified = cursor.getInt(cursor
				.getColumnIndex(EMAIL_VERIFIED)) == CommonColumnsValue.TRUE_VALUE;

		config.qiniu_upload_token = cursor.getString(cursor
				.getColumnIndex(UPLOAD_TOKEN));

		config.pubnub_sub_key = cursor.getString(cursor
				.getColumnIndex(PUBNUB_SUB_KEY));

		config.purposes = StringUtil.StringToList(cursor.getString(cursor
				.getColumnIndex(PURPOSES)));

		config.interests = StringUtil.StringToList(cursor.getString(cursor
				.getColumnIndex(INTERESTS)));

		token.access_token = cursor.getString(cursor
				.getColumnIndex(ACCESS_TOKEN));

		account.profile = profile;

		account.token = token;

		account.config = config;

		return account;
	}

	/**
	 * 封装账号对象<BR>
	 *
	 * @param account
	 *            账号
	 * @return ContentValues
	 */
	public static ContentValues setValues(Account account) {

		ContentValues cv = new ContentValues();

		cv.put(LOGIN_ACCOUNT, account.loginAccount);

		cv.put(USER_ID, account.profile._id);

		cv.put(NAME, account.profile.name);

		cv.put(AVATAR_URL, account.profile.avatar_url);

		cv.put(CHARACTER, account.profile.character);

		cv.put(PHONE_VERIFIED,
				account.profile.phone_verified ? CommonColumnsValue.TRUE_VALUE
						: CommonColumnsValue.FALSE_VALUE);

		cv.put(EMAIL_VERIFIED,
				account.profile.email_verified ? CommonColumnsValue.TRUE_VALUE
						: CommonColumnsValue.FALSE_VALUE);

		cv.put(IS_LOGIN, account.isLogin ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		cv.put(PURPOSES, StringUtil.listToString(account.config.purposes));

		cv.put(ACCESS_TOKEN, account.token.access_token);

		cv.put(UPLOAD_TOKEN, account.config.qiniu_upload_token);

		cv.put(PUBNUB_SUB_KEY, account.config.pubnub_sub_key);

		return cv;
	}

	/**
	 * 创建"账号"数据表<BR>
	 *
	 * @param db
	 *            SQLiteDatabase对象
	 */
	public static void createAccountTable(SQLiteDatabase db) {

		StringBuffer sql = new StringBuffer();

		sql.append(" create table if not exists ").append(Tables.ACCOUNT);
		sql.append(" ( ");

		sql.append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sql.append(LOGIN_ACCOUNT).append(" TEXT, ");
		sql.append(USER_ID).append(" TEXT NOT NULL, ");
		sql.append(NAME).append(" TEXT, ");
		sql.append(AVATAR_URL).append(" TEXT, ");
		sql.append(CHARACTER).append(" TEXT, ");
		sql.append(PHONE_VERIFIED).append(" INTEGER NOT NULL DEFAULT 0, ");
		sql.append(EMAIL_VERIFIED).append(" INTEGER NOT NULL DEFAULT 0, ");
		sql.append(IS_LOGIN).append(" INTEGER NOT NULL DEFAULT 0, ");
		sql.append(PURPOSES).append(" TEXT, ");
		sql.append(INTERESTS).append(" TEXT, ");
		sql.append(ACCESS_TOKEN).append(" TEXT, ");
		sql.append(UPLOAD_TOKEN).append(" TEXT, ");
		sql.append(PUBNUB_SUB_KEY).append(" TEXT ");

		sql.append(" );");

		db.execSQL(sql.toString());

		Log.d(TAG, "create " + Tables.ACCOUNT + " success!");
	}
}