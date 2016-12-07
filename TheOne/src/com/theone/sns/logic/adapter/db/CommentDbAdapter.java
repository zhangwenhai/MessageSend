package com.theone.sns.logic.adapter.db;

import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.theone.sns.TheOneApp;
import com.theone.sns.component.database.TheOneDatabaseHelper;
import com.theone.sns.component.database.URIField;
import com.theone.sns.logic.adapter.db.model.CommentColumns;
import com.theone.sns.logic.model.mblog.base.Comment;

public class CommentDbAdapter {

	private static final String TAG = "CommentDbAdapter";

	private static CommentDbAdapter sInstance;

	private ContentResolver mCr;

	private CommentDbAdapter() {

		mCr = TheOneApp.getResolver();
	}

	public static synchronized CommentDbAdapter getInstance() {

		if (null == sInstance) {

			sInstance = new CommentDbAdapter();
		}

		return sInstance;
	}

	public void insertCommentList(List<Comment> commentList) {

		if (null == commentList || commentList.isEmpty()) {

			return;
		}

		for (Comment comment : commentList) {

			insertComment(comment);
		}
	}

	public long insertComment(Comment comment) {

		long result = -1;

		if (null != comment) {

			Comment c = getCommentById(comment._id);

			if (null != c) {

				Log.e(TAG, "in method insertComment, comment has exist");

				return -1;
			}

			Uri uri = URIField.COMMENT_URI;

			ContentValues cv = CommentColumns.setValues(comment);

			Uri resultUri = mCr.insert(uri, cv);

			if (null != resultUri) {

				result = ContentUris.parseId(resultUri);
			}

		} else {
			Log.w(TAG, "insertComment fail, comment is null...");
		}

		return result;
	}

	public Comment getCommentById(String commentId) {

		Comment comment = null;

		Cursor cursor = null;

		try {

			Uri uri = URIField.COMMENT_URI;

			cursor = mCr.query(uri, null, CommentColumns.COMMENT_ID + "=?",
					new String[] { commentId }, null);

			if (null != cursor && cursor.moveToFirst()) {

				comment = CommentColumns.parseCursorToComment(cursor);
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return comment;
	}

	public int deleteCommentById(String commentId) {

		int result = -1;

		Uri uri = URIField.COMMENT_URI;

		result = mCr.delete(uri, CommentColumns.COMMENT_ID + "=?",
				new String[] { commentId });

		Log.i(TAG, "deleteTagById, result = " + result);

		return result;
	}
}
