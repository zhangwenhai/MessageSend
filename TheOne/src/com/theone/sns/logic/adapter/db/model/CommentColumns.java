package com.theone.sns.logic.adapter.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.theone.sns.component.database.TheOneDatabaseHelper.Tables;
import com.theone.sns.logic.adapter.db.UserDbAdapter;
import com.theone.sns.logic.model.mblog.base.Comment;
import com.theone.sns.logic.model.user.User;

public class CommentColumns {

	private static final String TAG = "CommentColumns";

	public static final String ID = "_id";

	public static final String COMMENT_ID = "commentId";

	public static final String OWNER_ID = "ownerId";

	public static final String TARGET_USER_ID = "targetUserId";

	public static final String TEXT = "text";

	public static final String CREATED_AT = "created_at";

	public static Comment parseCursorToComment(Cursor cursor) {

		Comment comment = new Comment();

		comment._id = cursor.getString(cursor.getColumnIndex(COMMENT_ID));

		comment.owner = getUser(cursor, OWNER_ID);

		comment.target_user = getUser(cursor, TARGET_USER_ID);

		comment.text = cursor.getString(cursor.getColumnIndex(TEXT));

		comment.created_at = cursor
				.getString(cursor.getColumnIndex(CREATED_AT));

		return comment;
	}

	public static ContentValues setValues(Comment comment) {

		ContentValues cv = new ContentValues();

		cv.put(COMMENT_ID, comment._id);

		cv.put(OWNER_ID, comment.owner.userId);

		if (null != comment.target_user) {

			cv.put(TARGET_USER_ID, comment.target_user.userId);
		}

		cv.put(TEXT, comment.text);

		cv.put(CREATED_AT, comment.created_at);

		return cv;
	}

	private static User getUser(Cursor cursor, String userIdColumn) {

		String userId = cursor.getString(cursor.getColumnIndex(userIdColumn));

		if (!TextUtils.isEmpty(userId)) {

			return UserDbAdapter.getInstance().getUserByUserId(userId);
		}

		return null;
	}

	public static void createCommentTable(SQLiteDatabase db) {

		StringBuffer sql = new StringBuffer();

		sql.append(" create table if not exists ").append(Tables.COMMENT);
		sql.append(" ( ");

		sql.append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sql.append(COMMENT_ID).append(" TEXT NOT NULL, ");
		sql.append(OWNER_ID).append(" TEXT NOT NULL, ");
		sql.append(TARGET_USER_ID).append(" TEXT, ");
		sql.append(TEXT).append(" TEXT NOT NULL, ");
		sql.append(CREATED_AT).append(" TEXT ");

		sql.append(" );");

		db.execSQL(sql.toString());

		Log.d(TAG, "create " + Tables.COMMENT + " success!");
	}
}