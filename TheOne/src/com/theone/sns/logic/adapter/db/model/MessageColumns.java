package com.theone.sns.logic.adapter.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.theone.sns.common.FusionCode.CommonColumnsValue;
import com.theone.sns.common.FusionCode.MessageType;
import com.theone.sns.component.database.TheOneDatabaseHelper.Tables;
import com.theone.sns.logic.adapter.db.UserDbAdapter;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.logic.model.chat.base.NameCard;
import com.theone.sns.logic.model.chat.base.Position;
import com.theone.sns.logic.model.mblog.base.AudioDesc;
import com.theone.sns.logic.model.mblog.base.Photo;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.util.StringUtil;

public class MessageColumns {

	private static final String TAG = "MessageColumns";

	public static final String ID = "_id";

	public static final String MESSAGEID = "messageId";

	public static final String OWNER = "ownerId";

	public static final String RECIPIENT = "recipient";

	public static final String TEXT = "text";

	public static final String AUDIO_URL = "audio_url";

	public static final String AUDIO_DURATION = "audio_duration";

	public static final String PHOTO_URL = "photo_url";

	public static final String PHOTO_W = "photo_w";

	public static final String PHOTO_H = "photo_h";

	public static final String POSITION_NAME = "position_name";

	public static final String POSITION_ADDRESS = "position_address";

	public static final String POSITION_LOCATION = "position_location";

	public static final String NAME_CARD = "name_card_id";

	public static final String IS_MENTION_ME = "is_mention_me";

	public static final String MEDIA_URL = "network_media_url";

	public static final String IS_READ = "isRead";

	public static final String STATUS = "status";

	public static final String MEDIA_IS_READ = "media_is_read";

	public static final String MESSAGE_TYPE = "message_type";

	public static final String CREATETIME = "created_at";

	public static MessageInfo parseCursorToMessage(Cursor cursor) {

		MessageInfo message = new MessageInfo();

		message._id = cursor.getString(cursor.getColumnIndex(MESSAGEID));

		message.owner = UserDbAdapter.getInstance().getUserByUserId(
				cursor.getString(cursor.getColumnIndex(OWNER)));

		message.recipient = cursor.getString(cursor.getColumnIndex(RECIPIENT));

		message.messageType = cursor
				.getInt(cursor.getColumnIndex(MESSAGE_TYPE));

		getMessageContent(message, cursor);

		message.isMentionMe = cursor.getInt(cursor
				.getColumnIndex(IS_MENTION_ME)) == CommonColumnsValue.TRUE_VALUE;

		message.network_media_url = cursor.getString(cursor
				.getColumnIndex(MEDIA_URL));

		message.isRead = cursor.getInt(cursor.getColumnIndex(IS_READ)) == CommonColumnsValue.TRUE_VALUE;

		message.status = cursor.getInt(cursor.getColumnIndex(STATUS));

		message.mediaIsRead = cursor.getInt(cursor
				.getColumnIndex(MEDIA_IS_READ)) == CommonColumnsValue.TRUE_VALUE;

		message.created_at = cursor
				.getString(cursor.getColumnIndex(CREATETIME));

		message.db_id = cursor.getString(cursor.getColumnIndex(ID));

		return message;
	}

	public static ContentValues setValues(MessageInfo message) {

		ContentValues cv = new ContentValues();

		cv.put(MESSAGEID, message._id);

		putOwnerUser(cv, OWNER, message);

		cv.put(RECIPIENT, message.recipient);

		cv.put(MESSAGE_TYPE, message.messageType);

		setMessageContent(message, cv);

		cv.put(IS_MENTION_ME,
				message.isMentionMe ? CommonColumnsValue.TRUE_VALUE
						: CommonColumnsValue.FALSE_VALUE);

		cv.put(IS_READ, message.isRead ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		cv.put(STATUS, message.status);

		cv.put(MEDIA_IS_READ,
				message.mediaIsRead ? CommonColumnsValue.TRUE_VALUE
						: CommonColumnsValue.FALSE_VALUE);

		cv.put(CREATETIME, message.created_at);

		return cv;
	}

	public static ContentValues updateValues(MessageInfo message) {

		ContentValues cv = new ContentValues();

		cv.put(MESSAGEID, message._id);

		cv.put(STATUS, message.status);

		cv.put(CREATETIME, message.created_at);

		setMediaContent(message, cv);

		return cv;
	}

	public static int getMessageType(MessageInfo message) {

		int type = -1;

		if (!TextUtils.isEmpty(message.text)) {

			type = MessageType.TEXT;

		} else if (null != message.audio) {

			type = MessageType.AUDIO;

		} else if (null != message.photo) {

			type = MessageType.PHOTO;

		} else if (null != message.position) {

			type = MessageType.POSITION;

		} else if (null != message.name_card) {

			type = MessageType.NAME_CARD;
		}

		return type;
	}

	private static void getMessageContent(MessageInfo message, Cursor cursor) {

		switch (message.messageType) {

		case MessageType.TEXT: {

			message.text = cursor.getString(cursor.getColumnIndex(TEXT));

			break;
		}
		case MessageType.AUDIO: {

			message.audio = getAudioDesc(cursor);

			break;
		}
		case MessageType.PHOTO: {

			message.photo = getPhoto(cursor);

			break;
		}
		case MessageType.POSITION: {

			message.position = getPosition(cursor);

			break;
		}
		case MessageType.NAME_CARD: {

			message.name_card = getNameCard(cursor);

			break;
		}
		default: {
			Log.e(TAG, "UnKnow message , message.type = " + message.messageType);
			break;
		}
		}
	}

	private static void setMediaContent(MessageInfo message, ContentValues cv) {

		switch (message.messageType) {

		case MessageType.AUDIO: {

			cv.put(MEDIA_URL, message.audio.url);

			break;
		}
		case MessageType.PHOTO: {

			cv.put(MEDIA_URL, message.photo.url);

			break;
		}
		}
	}

	private static void setMessageContent(MessageInfo message, ContentValues cv) {

		switch (message.messageType) {

		case MessageType.TEXT: {

			cv.put(TEXT, message.text);

			break;
		}
		case MessageType.AUDIO: {

			cv.put(AUDIO_URL, message.audio.url);

			cv.put(AUDIO_DURATION, message.audio.duration);

			break;
		}
		case MessageType.PHOTO: {

			cv.put(PHOTO_URL, message.photo.url);

			cv.put(PHOTO_W, message.photo.w);

			cv.put(PHOTO_H, message.photo.h);

			break;
		}
		case MessageType.POSITION: {

			cv.put(POSITION_NAME, message.position.name);

			cv.put(POSITION_ADDRESS, message.position.address);

			cv.put(POSITION_LOCATION,
					StringUtil.listToString(message.position.location));

			break;
		}
		case MessageType.NAME_CARD: {

			putNameCard(cv, NAME_CARD, message);

			break;
		}
		default: {
			Log.e(TAG, "UnKnow message , message.type = " + message.messageType);
			break;
		}
		}
	}

	private static void putOwnerUser(ContentValues cv, String column,
			MessageInfo message) {

		User owner = message.owner;

		if (null != owner) {

			cv.put(column, owner.userId);

			UserColumns.insertUserAddType(owner, message.recipient);
		}
	}

	private static void putNameCard(ContentValues cv, String column,
			MessageInfo message) {

		NameCard nameCard = message.name_card;

		if (null != nameCard) {

			User user = new User();

			if (null != user) {

				user.userId = nameCard._id;

				user.name = nameCard.name;

				user.avatar_url = nameCard.avatar_url;

				user.location = nameCard.location;

				user.role = nameCard.role;

				user.marriage = nameCard.marriage;

				user.region = nameCard.region;
			}

			cv.put(column, user.userId);

			UserColumns.insertUserAddType(user, message.recipient);
		}
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

		String url = cursor.getString(cursor.getColumnIndex(AUDIO_URL));

		int duration = cursor.getInt(cursor.getColumnIndex(AUDIO_DURATION));

		if (!TextUtils.isEmpty(url)) {

			audioDesc = new AudioDesc();

			audioDesc.url = url;

			audioDesc.duration = duration;
		}

		return audioDesc;
	}

	private static NameCard getNameCard(Cursor cursor) {

		NameCard nameCard = new NameCard();

		User user = UserDbAdapter.getInstance().getUserByUserId(
				cursor.getString(cursor.getColumnIndex(NAME_CARD)));

		if (null != user) {

			nameCard._id = user.userId;

			nameCard.name = user.name;

			nameCard.avatar_url = user.avatar_url;

			nameCard.location = user.location;

			nameCard.role = user.role;

			nameCard.marriage = user.marriage;

			nameCard.region = user.region;
		}

		return nameCard;
	}

	private static Position getPosition(Cursor cursor) {

		Position position = new Position();

		position.name = cursor.getString(cursor.getColumnIndex(POSITION_NAME));

		position.address = cursor.getString(cursor
				.getColumnIndex(POSITION_ADDRESS));

		position.location = StringUtil.StringToList(cursor.getString(cursor
				.getColumnIndex(POSITION_LOCATION)));

		return position;
	}

	public static void createMessageTable(SQLiteDatabase db) {

		StringBuffer sql = new StringBuffer();

		sql.append(" create table if not exists ").append(Tables.MESSAGE);
		sql.append(" ( ");

		sql.append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sql.append(MESSAGEID).append(" TEXT, ");
		sql.append(OWNER).append(" TEXT, ");
		sql.append(RECIPIENT).append(" TEXT, ");
		sql.append(TEXT).append(" TEXT, ");
		sql.append(AUDIO_URL).append(" TEXT, ");
		sql.append(AUDIO_DURATION).append(" TEXT, ");
		sql.append(PHOTO_URL).append(" TEXT, ");
		sql.append(PHOTO_W).append(" TEXT, ");
		sql.append(PHOTO_H).append(" TEXT, ");
		sql.append(POSITION_NAME).append(" TEXT, ");
		sql.append(POSITION_ADDRESS).append(" TEXT, ");
		sql.append(POSITION_LOCATION).append(" TEXT, ");
		sql.append(NAME_CARD).append(" TEXT, ");
		sql.append(IS_MENTION_ME).append(" TEXT, ");
		sql.append(MEDIA_URL).append(" TEXT, ");
		sql.append(IS_READ).append(" TEXT, ");
		sql.append(STATUS).append(" TEXT, ");
		sql.append(MEDIA_IS_READ).append(" TEXT, ");
		sql.append(MESSAGE_TYPE).append(" TEXT, ");
		sql.append(CREATETIME).append(" TEXT ");

		sql.append(" );");

		db.execSQL(sql.toString());

		Log.d(TAG, "create " + Tables.MESSAGE + " success!");
	}
}