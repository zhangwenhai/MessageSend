package com.theone.sns.logic.adapter.db.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.theone.sns.common.FusionCode.CommonColumnsValue;
import com.theone.sns.component.database.TheOneDatabaseHelper.Tables;
import com.theone.sns.logic.adapter.db.GalleryDbAdapter;
import com.theone.sns.logic.adapter.db.UserDbAdapter;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.model.user.base.Count;
import com.theone.sns.logic.model.user.base.Gallery;
import com.theone.sns.util.StringUtil;

public class UserColumns {

	private static final String TAG = "UserColumns";

	public static final String ID = "_id";

	public static final String USER_ID = "userId";

	public static final String NAME = "name";

	public static final String AVATAR_URL = "avatar_url";

	public static final String LOCATION = "location";

	public static final String CHARACTER = "character";

	public static final String ROLE = "role";

	public static final String MARRIAGE = "marriage";

	public static final String BIRTHDAY = "birthday";

	public static final String GALLERY = "gallery";

	public static final String REGION = "region";

	public static final String AGE = "age";

	public static final String SIGN = "sign";

	public static final String HEIGHT = "height";

	public static final String AWAY_AT = "away_at";

	public static final String PURPOSES = "purposes";

	public static final String INTERESTS = "interests";

	public static final String COUNT_FOLLOWING = "following";

	public static final String COUNT_FOLLOWED_BY = "followed_by";

	public static final String COUNT_STARRED = "starred";

	public static final String COUNT_MEDIA = "media";

	public static final String COUNT_COMMENTS = "comments";

	public static final String COUNT_LIKED_BY = "liked_by";

	public static final String IS_JOINED = "isJoined";

	public static final String TYPE = "type";

	public static User parseCursorToUser(Cursor cursor) {

		User user = new User();

		user.userId = cursor.getString(cursor.getColumnIndex(USER_ID));

		user.name = cursor.getString(cursor.getColumnIndex(NAME));

		user.avatar_url = cursor.getString(cursor.getColumnIndex(AVATAR_URL));

		user.location = StringUtil.StringToList(cursor.getString(cursor
				.getColumnIndex(LOCATION)));

		user.character = cursor.getString(cursor.getColumnIndex(CHARACTER));

		user.role = cursor.getString(cursor.getColumnIndex(ROLE));

		user.marriage = cursor.getInt(cursor.getColumnIndex(MARRIAGE)) == CommonColumnsValue.TRUE_VALUE;

		user.birthday = cursor.getString(cursor.getColumnIndex(BIRTHDAY));

		user.count = getCount(cursor);

		user.gallery = getGallery(StringUtil.StringToList(cursor
				.getString(cursor.getColumnIndex(GALLERY))));

		user.region = cursor.getString(cursor.getColumnIndex(REGION));

		user.age = cursor.getInt(cursor.getColumnIndex(AGE));

		user.sign = cursor.getString(cursor.getColumnIndex(SIGN));

		user.height = TextUtils.isEmpty(cursor.getString(cursor
				.getColumnIndex(HEIGHT))) ? (0 + "") : (cursor.getString(cursor
				.getColumnIndex(HEIGHT)));

		user.away_at = cursor.getString(cursor.getColumnIndex(AWAY_AT));

		user.purposes = StringUtil.StringToList(cursor.getString(cursor
				.getColumnIndex(PURPOSES)));

		user.interests = StringUtil.StringToList(cursor.getString(cursor
				.getColumnIndex(INTERESTS)));

		user.isJoined = cursor.getInt(cursor.getColumnIndex(IS_JOINED)) == CommonColumnsValue.TRUE_VALUE;

		return user;
	}

	public static ContentValues setValues(User user) {

		ContentValues cv = updateValues(user);

		cv.put(USER_ID, user.userId);

		cv.put(TYPE, user.type);

		cv.put(IS_JOINED, user.isJoined ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		return cv;
	}

	public static ContentValues updateValues(User user) {

		ContentValues cv = new ContentValues();

		if (!TextUtils.isEmpty(user.name)) {
			cv.put(NAME, user.name);
		}

		if (!TextUtils.isEmpty(user.avatar_url)) {
			cv.put(AVATAR_URL, user.avatar_url);
		}

		if (!TextUtils.isEmpty(user.character)) {
			cv.put(CHARACTER, user.character);
		}

		if (!TextUtils.isEmpty(user.role)) {
			cv.put(ROLE, user.role);
		}

		if (!TextUtils.isEmpty(user.birthday)) {
			cv.put(BIRTHDAY, user.birthday);
		}

		if (!TextUtils.isEmpty(user.region)) {
			cv.put(REGION, user.region);
		}

		if (0 != user.age) {
			cv.put(AGE, user.age);
		}

		if (!TextUtils.isEmpty(user.sign)) {
			cv.put(SIGN, user.sign);
		}

		if (!TextUtils.isEmpty(user.height)) {
			cv.put(HEIGHT, user.height);
		}

		if (!TextUtils.isEmpty(user.away_at)) {
			cv.put(AWAY_AT, user.away_at);
		}

		cv.put(MARRIAGE, user.marriage ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		putListString(cv, LOCATION, user.location);

		putListString(cv, PURPOSES, user.purposes);

		putListString(cv, INTERESTS, user.interests);

		putGallery(cv, user.gallery);

		putCount(cv, user.count);

		return cv;
	}

	public static void insertUserAddType(User user, String type) {

		if (null != user) {

			user.type = type;

			UserDbAdapter.getInstance().insertUser(user);
		}
	}

	private static void putGallery(ContentValues cv, List<Gallery> gallery) {

		if (null != gallery && !gallery.isEmpty()) {

			List<String> itemIds = new ArrayList<String>();

			for (Gallery g : gallery) {

				itemIds.add(g._id);
			}

			putListString(cv, GALLERY, itemIds);
		}
	}

	private static void putCount(ContentValues cv, Count count) {

		if (null != count) {

			cv.put(COUNT_FOLLOWING, count.following);

			cv.put(COUNT_FOLLOWED_BY, count.followed_by);

			cv.put(COUNT_STARRED, count.starred);

			cv.put(COUNT_MEDIA, count.media);

			cv.put(COUNT_COMMENTS, count.comments);

			cv.put(COUNT_LIKED_BY, count.liked_by);
		}
	}

	private static void putListString(ContentValues cv, String columns,
			List<String> values) {

		if (null != values && !values.isEmpty()) {

			cv.put(columns, StringUtil.listToString(values));
		}
	}

	private static List<Gallery> getGallery(List<String> galleryIdList) {

		List<Gallery> galleryList = new ArrayList<Gallery>();

		if (null == galleryIdList || galleryIdList.isEmpty()) {

			return galleryList;
		}

		for (String id : galleryIdList) {

			Gallery gallery = GalleryDbAdapter.getInstance().getGalleryById(id);

			if (null != gallery) {
				galleryList.add(gallery);
			}
		}

		return galleryList;
	}

	private static Count getCount(Cursor cursor) {

		Count count = new Count();

		count.following = TextUtils.isEmpty(cursor.getString(cursor
				.getColumnIndex(COUNT_FOLLOWING))) ? (0 + "") : (cursor
				.getString(cursor.getColumnIndex(COUNT_FOLLOWING)));

		count.followed_by = TextUtils.isEmpty(cursor.getString(cursor
				.getColumnIndex(COUNT_FOLLOWED_BY))) ? (0 + "") : (cursor
				.getString(cursor.getColumnIndex(COUNT_FOLLOWED_BY)));

		count.starred = TextUtils.isEmpty(cursor.getString(cursor
				.getColumnIndex(COUNT_STARRED))) ? (0 + "") : (cursor
				.getString(cursor.getColumnIndex(COUNT_STARRED)));

		count.media = TextUtils.isEmpty(cursor.getString(cursor
				.getColumnIndex(COUNT_MEDIA))) ? (0 + "") : (cursor
				.getString(cursor.getColumnIndex(COUNT_MEDIA)));

		count.comments = TextUtils.isEmpty(cursor.getString(cursor
				.getColumnIndex(COUNT_COMMENTS))) ? (0 + "") : (cursor
				.getString(cursor.getColumnIndex(COUNT_COMMENTS)));

		count.liked_by = TextUtils.isEmpty(cursor.getString(cursor
				.getColumnIndex(COUNT_LIKED_BY))) ? (0 + "") : (cursor
				.getString(cursor.getColumnIndex(COUNT_LIKED_BY)));

		return count;
	}

	public static void createUserTable(SQLiteDatabase db) {

		StringBuffer sql = new StringBuffer();

		sql.append(" create table if not exists ").append(Tables.USER);
		sql.append(" ( ");

		sql.append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sql.append(USER_ID).append(" TEXT NOT NULL, ");
		sql.append(NAME).append(" TEXT, ");
		sql.append(AVATAR_URL).append(" TEXT, ");
		sql.append(LOCATION).append(" TEXT, ");
		sql.append(CHARACTER).append(" TEXT, ");
		sql.append(ROLE).append(" TEXT, ");
		sql.append(MARRIAGE).append(" INTEGER NOT NULL DEFAULT 0, ");
		sql.append(BIRTHDAY).append(" TEXT, ");
		sql.append(COUNT_FOLLOWING).append(" TEXT, ");
		sql.append(COUNT_FOLLOWED_BY).append(" TEXT, ");
		sql.append(COUNT_STARRED).append(" TEXT, ");
		sql.append(COUNT_MEDIA).append(" TEXT, ");
		sql.append(COUNT_COMMENTS).append(" TEXT, ");
		sql.append(COUNT_LIKED_BY).append(" TEXT, ");
		sql.append(GALLERY).append(" TEXT, ");
		sql.append(REGION).append(" TEXT, ");
		sql.append(AGE).append(" INTEGER NOT NULL DEFAULT 0, ");
		sql.append(SIGN).append(" TEXT, ");
		sql.append(HEIGHT).append(" TEXT, ");
		sql.append(AWAY_AT).append(" TEXT, ");
		sql.append(PURPOSES).append(" TEXT, ");
		sql.append(INTERESTS).append(" TEXT, ");
		sql.append(IS_JOINED).append(" TEXT, ");
		sql.append(TYPE).append(" TEXT ");

		sql.append(" );");

		db.execSQL(sql.toString());

		Log.d(TAG, "create " + Tables.USER + " success!");
	}
}