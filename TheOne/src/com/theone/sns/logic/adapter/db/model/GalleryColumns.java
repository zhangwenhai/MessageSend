package com.theone.sns.logic.adapter.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.theone.sns.common.FusionCode.CommonColumnsValue;
import com.theone.sns.component.database.TheOneDatabaseHelper.Tables;
import com.theone.sns.logic.model.user.base.Gallery;

public class GalleryColumns {

	private static final String TAG = "GalleryColumns";

	public static final String ID = "_id";

	public static final String MID = "mId";

	public static final String URL = "url";

	public static final String IS_VIDEO = "is_video";

	public static final String TYPE = "type";

	public static Gallery parseCursorToGallery(Cursor cursor) {

		Gallery gallery = new Gallery();

		gallery._id = cursor.getString(cursor.getColumnIndex(MID));

		gallery.url = cursor.getString(cursor.getColumnIndex(URL));

		gallery.is_video = cursor.getInt(cursor.getColumnIndex(IS_VIDEO)) == CommonColumnsValue.TRUE_VALUE;

		gallery.type = cursor.getString(cursor.getColumnIndex(TYPE));

		return gallery;
	}

	public static ContentValues setValues(Gallery gallery) {

		ContentValues cv = new ContentValues();

		cv.put(MID, gallery._id);

		cv.put(URL, gallery.url);

		cv.put(IS_VIDEO, gallery.is_video ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		cv.put(TYPE, gallery.type);

		return cv;
	}

	public static void createGalleryTable(SQLiteDatabase db) {

		StringBuffer sql = new StringBuffer();

		sql.append(" create table if not exists ").append(Tables.GALLERY);
		sql.append(" ( ");

		sql.append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sql.append(MID).append(" TEXT NOT NULL, ");
		sql.append(URL).append(" TEXT NOT NULL, ");
		sql.append(IS_VIDEO).append(" TEXT NOT NULL, ");
		sql.append(TYPE).append(" TEXT ");

		sql.append(" );");

		db.execSQL(sql.toString());

		Log.d(TAG, "create " + Tables.GALLERY + " success!");
	}
}