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
import com.theone.sns.logic.adapter.db.CommentDbAdapter;
import com.theone.sns.logic.adapter.db.TagDbAdapter;
import com.theone.sns.logic.adapter.db.UserDbAdapter;
import com.theone.sns.logic.model.mblog.MBlog;
import com.theone.sns.logic.model.mblog.MBlogTag;
import com.theone.sns.logic.model.mblog.base.AudioDesc;
import com.theone.sns.logic.model.mblog.base.Comment;
import com.theone.sns.logic.model.mblog.base.Photo;
import com.theone.sns.logic.model.mblog.base.Video;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.util.StringUtil;

public class MBlogColumns {

	private static final String TAG = "MBlogColumns";

	public static final String ID = "_id";

	public static final String MBLOGID = "mBlogId";

	public static final String OWNER = "ownerId";

	public static final String PHOTO_URL = "photo_url";

	public static final String PHOTO_W = "photo_w";

	public static final String PHOTO_H = "photo_h";

	public static final String AUDIO_DESC_URL = "audio_desc_url";

	public static final String AUDIO_DESC_DURATION = "audio_desc_duration";

	public static final String VIDEO_URL = "video_url";

	public static final String VIDEO_W = "video_w";

	public static final String VIDEO_H = "video_h";

	public static final String VIDEO_DURATION = "video_duration";

	public static final String TEXT_DESC = "text_desc";

	public static final String LOCATION = "location";

	public static final String IS_LIKED = "is_liked";

	public static final String LIKES_COUNT = "likes_count";

	public static final String COMMENTS_COUNT = "comments_count";

	public static final String CREATED_AT = "created_at";

	public static final String LIKES = "likes";

	public static final String TAGS = "tags";

	public static final String COMMENTS = "comments";

	public static final String MBLOG_TYPE = "mblog_type";

	public static MBlog parseCursorToMBlog(Cursor cursor) {

		MBlog mBlog = new MBlog();

		mBlog._id = cursor.getString(cursor.getColumnIndex(MBLOGID));

		mBlog.owner = UserDbAdapter.getInstance().getUserByUserId(
				cursor.getString(cursor.getColumnIndex(OWNER)));

		mBlog.text_desc = cursor.getString(cursor.getColumnIndex(TEXT_DESC));

		mBlog.is_liked = cursor.getInt(cursor.getColumnIndex(IS_LIKED)) == CommonColumnsValue.TRUE_VALUE;

		mBlog.likes_count = cursor.getInt(cursor.getColumnIndex(LIKES_COUNT));

		mBlog.comments_count = cursor.getInt(cursor
				.getColumnIndex(COMMENTS_COUNT));

		mBlog.created_at = cursor.getString(cursor.getColumnIndex(CREATED_AT));

		mBlog.photo = getPhoto(cursor);

		mBlog.audio_desc = getAudioDesc(cursor);

		mBlog.video = getVideo(cursor);

		mBlog.location = StringUtil.StringToList(cursor.getString(cursor
				.getColumnIndex(LOCATION)));

		mBlog.likes = getLikeList(StringUtil.StringToList(cursor
				.getString(cursor.getColumnIndex(LIKES))));

		mBlog.tags = getMBlogTagList(StringUtil.StringToList(cursor
				.getString(cursor.getColumnIndex(TAGS))));

		mBlog.comments = getComments(StringUtil.StringToList(cursor
				.getString(cursor.getColumnIndex(COMMENTS))));

		return mBlog;
	}

	public static ContentValues setValues(MBlog mBlog) {

		ContentValues cv = new ContentValues();

		cv.put(MBLOGID, mBlog._id);

		putMBLogOwner(cv, OWNER, mBlog);

		if (null != mBlog.photo) {

			cv.put(PHOTO_URL, mBlog.photo.url);

			cv.put(PHOTO_W, mBlog.photo.w);

			cv.put(PHOTO_H, mBlog.photo.h);
		}

		if (null != mBlog.audio_desc) {

			cv.put(AUDIO_DESC_URL, mBlog.audio_desc.url);

			cv.put(AUDIO_DESC_DURATION, mBlog.audio_desc.duration);
		}

		if (null != mBlog.video) {

			cv.put(VIDEO_URL, mBlog.video.url);

			cv.put(VIDEO_W, mBlog.video.w);

			cv.put(VIDEO_H, mBlog.video.h);

			cv.put(VIDEO_DURATION, mBlog.video.duration);
		}

		cv.put(TEXT_DESC, mBlog.text_desc);

		putListString(cv, LOCATION, mBlog.location);

		cv.put(IS_LIKED, mBlog.is_liked ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		cv.put(LIKES_COUNT, mBlog.likes_count);

		cv.put(COMMENTS_COUNT, mBlog.comments_count);

		cv.put(CREATED_AT, mBlog.created_at);

		cv.put(TAGS, putTags(mBlog.tags));

		cv.put(LIKES, putLikes(mBlog.likes, mBlog.mblog_type));

		cv.put(COMMENTS, putComments(mBlog.comments, mBlog.mblog_type));

		cv.put(MBLOG_TYPE, mBlog.mblog_type);

		return cv;
	}

	private static String putComments(List<Comment> comments, String type) {

		if (null == comments || comments.isEmpty()) {

			return "";
		}

		List<String> commentIdList = new ArrayList<String>();

		for (Comment comment : comments) {

			CommentDbAdapter.getInstance().insertComment(comment);

			commentIdList.add(comment._id);

			UserColumns.insertUserAddType(comment.owner, type);

			UserColumns.insertUserAddType(comment.target_user, type);
		}

		return StringUtil.listToString(commentIdList);
	}

	private static String putLikes(List<User> likes, String type) {

		if (null == likes || likes.isEmpty()) {

			return "";
		}

		List<String> userIdList = new ArrayList<String>();

		for (User user : likes) {

			UserColumns.insertUserAddType(user, type);

			userIdList.add(user.userId);
		}

		return StringUtil.listToString(userIdList);
	}

	private static String putTags(List<MBlogTag> tags) {

		if (null == tags || tags.isEmpty()) {

			return "";
		}

		List<String> tagIdList = new ArrayList<String>();

		for (MBlogTag tag : tags) {

			tagIdList.add(TagDbAdapter.getInstance().insertMBlogTag(tag) + "");
		}

		return StringUtil.listToString(tagIdList);
	}

	private static void putMBLogOwner(ContentValues cv, String column,
			MBlog mBlog) {

		User owner = mBlog.owner;

		if (null != owner) {

			cv.put(column, owner.userId);
		}

		UserColumns.insertUserAddType(owner, mBlog.mblog_type);
	}

	private static List<User> getLikeList(List<String> idList) {

		List<User> userList = new ArrayList<User>();

		if (null == idList || idList.isEmpty()) {
			return userList;
		}

		for (String id : idList) {

			User user = UserDbAdapter.getInstance().getUserByUserId(id);

			if (null != user) {

				userList.add(user);
			}
		}

		return userList;
	}

	private static List<MBlogTag> getMBlogTagList(List<String> idList) {

		List<MBlogTag> mBlogTagList = new ArrayList<MBlogTag>();

		if (null == idList || idList.isEmpty()) {
			return mBlogTagList;
		}

		for (String id : idList) {

			MBlogTag mBlogTag = TagDbAdapter.getInstance().getMBlogTagById(id);

			if (null != mBlogTag) {

				mBlogTagList.add(mBlogTag);
			}
		}

		return mBlogTagList;
	}

	private static List<Comment> getComments(List<String> idList) {

		List<Comment> commentList = new ArrayList<Comment>();

		if (null == idList || idList.isEmpty()) {
			return commentList;
		}

		for (String id : idList) {

			Comment comment = CommentDbAdapter.getInstance().getCommentById(id);

			if (null != comment) {
				commentList.add(comment);
			}
		}

		return commentList;
	}

	private static Photo getPhoto(Cursor cursor) {

		Photo photo = null;

		String photoUrl = cursor.getString(cursor.getColumnIndex(PHOTO_URL));

		int photoW = cursor.getInt(cursor.getColumnIndex(PHOTO_W));

		int photoH = cursor.getInt(cursor.getColumnIndex(PHOTO_H));

		if (!TextUtils.isEmpty(photoUrl)) {

			photo = new Photo();

			photo.url = photoUrl;

			photo.w = photoW;

			photo.h = photoH;
		}

		return photo;
	}

	private static AudioDesc getAudioDesc(Cursor cursor) {

		AudioDesc audioDesc = null;

		String url = cursor.getString(cursor.getColumnIndex(AUDIO_DESC_URL));

		int duration = cursor
				.getInt(cursor.getColumnIndex(AUDIO_DESC_DURATION));

		if (!TextUtils.isEmpty(url)) {

			audioDesc = new AudioDesc();

			audioDesc.url = url;

			audioDesc.duration = duration;
		}

		return audioDesc;
	}

	private static Video getVideo(Cursor cursor) {

		Video video = null;

		String url = cursor.getString(cursor.getColumnIndex(VIDEO_URL));

		int w = cursor.getInt(cursor.getColumnIndex(VIDEO_W));

		int h = cursor.getInt(cursor.getColumnIndex(VIDEO_H));

		int duration = cursor.getInt(cursor.getColumnIndex(VIDEO_DURATION));

		if (!TextUtils.isEmpty(url)) {

			video = new Video();

			video.url = url;

			video.w = w;

			video.h = h;

			video.duration = duration;
		}

		return video;
	}

	private static void putListString(ContentValues cv, String columns,
			List<String> values) {

		if (null != values && !values.isEmpty()) {

			cv.put(columns, StringUtil.listToString(values));
		}
	}

	public static void createMBlogTable(SQLiteDatabase db) {

		StringBuffer sql = new StringBuffer();

		sql.append(" create table if not exists ").append(Tables.MBLOG);
		sql.append(" ( ");

		sql.append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sql.append(MBLOGID).append(" TEXT NOT NULL, ");
		sql.append(OWNER).append(" TEXT NOT NULL, ");
		sql.append(PHOTO_URL).append(" TEXT, ");
		sql.append(PHOTO_W).append(" TEXT, ");
		sql.append(PHOTO_H).append(" TEXT, ");
		sql.append(AUDIO_DESC_URL).append(" TEXT, ");
		sql.append(AUDIO_DESC_DURATION).append(" TEXT, ");
		sql.append(VIDEO_URL).append(" TEXT, ");
		sql.append(VIDEO_W).append(" TEXT, ");
		sql.append(VIDEO_H).append(" TEXT, ");
		sql.append(VIDEO_DURATION).append(" TEXT, ");
		sql.append(TEXT_DESC).append(" TEXT, ");
		sql.append(LOCATION).append(" TEXT, ");
		sql.append(IS_LIKED).append(" TEXT, ");
		sql.append(LIKES_COUNT).append(" TEXT, ");
		sql.append(COMMENTS_COUNT).append(" TEXT, ");
		sql.append(CREATED_AT).append(" TEXT, ");
		sql.append(LIKES).append(" TEXT, ");
		sql.append(TAGS).append(" TEXT, ");
		sql.append(COMMENTS).append(" TEXT, ");
		sql.append(MBLOG_TYPE).append(" TEXT ");

		sql.append(" );");

		db.execSQL(sql.toString());

		Log.d(TAG, "create " + Tables.MBLOG + " success!");
	}
}