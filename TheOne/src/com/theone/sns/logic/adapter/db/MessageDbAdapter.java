package com.theone.sns.logic.adapter.db;

import java.util.ArrayList;
import java.util.Collections;
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
import com.theone.sns.common.FusionCode.MessageType;
import com.theone.sns.component.database.TheOneDatabaseHelper;
import com.theone.sns.component.database.URIField;
import com.theone.sns.logic.adapter.db.model.MessageColumns;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.logic.model.chat.base.SearchMessageResult;

public class MessageDbAdapter {

	private static final String TAG = "MessageDbAdapter";

	private static MessageDbAdapter sInstance;

	private ContentResolver mCr;

	private MessageDbAdapter() {

		mCr = TheOneApp.getResolver();
	}

	public static synchronized MessageDbAdapter getInstance() {

		if (null == sInstance) {

			sInstance = new MessageDbAdapter();
		}

		return sInstance;
	}

	public long insertMessage(MessageInfo message) {

		long result = -1;

		if (null == message) {

			Log.e(TAG, "insertMessage fail, message is null");

			return result;
		}

		Uri uri = URIField.MESSAGE_URI;

		if (null != getMessageById(message._id)) {

			return result;
		}

		message.messageType = MessageColumns.getMessageType(message);

		ContentValues cv = MessageColumns.setValues(message);

		Uri resultUri = mCr.insert(uri, cv);

		if (null != resultUri) {

			result = ContentUris.parseId(resultUri);
		}

		return result;
	}

	public MessageInfo getMessageById(String messageId) {

		if (TextUtils.isEmpty(messageId)) {

			Log.e(TAG, "getMessageById fail, messageId is null");

			return null;
		}

		MessageInfo message = null;

		Cursor cursor = null;

		Uri uri = URIField.MESSAGE_URI;

		try {

			cursor = mCr.query(uri, null, MessageColumns.MESSAGEID + "=?",
					new String[] { messageId }, null);

			if (null != cursor && cursor.moveToFirst()) {

				message = MessageColumns.parseCursorToMessage(cursor);
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return message;
	}

	public List<MessageInfo> getAllMessage(String recipient, String messageTime) {

		List<MessageInfo> messageList = new ArrayList<MessageInfo>();

		Cursor cursor = null;

		Cursor oldCursor = null;

		Uri uri = URIField.MESSAGE_URI;

		try {

			if (TextUtils.isEmpty(messageTime)) {

				cursor = mCr.query(uri, null, MessageColumns.RECIPIENT + "=?",
						new String[] { recipient }, MessageColumns.CREATETIME
								+ " desc" + " limit 50 ");
			} else {

				cursor = mCr.query(uri, null, MessageColumns.RECIPIENT
						+ "=? AND " + MessageColumns.CREATETIME + "<?",
						new String[] { recipient, messageTime },
						MessageColumns.CREATETIME + " desc" + " limit 50 ");

				oldCursor = mCr.query(uri, null, MessageColumns.RECIPIENT
						+ "=? AND " + MessageColumns.CREATETIME + ">=?",
						new String[] { recipient, messageTime },
						MessageColumns.CREATETIME + " asc");
			}

			while (cursor.moveToNext()) {

				MessageInfo message = MessageColumns
						.parseCursorToMessage(cursor);

				if (null != message) {

					messageList.add(message);
				}
			}

			if (messageList.size() > 0) {

				Collections.reverse(messageList);
			}

			if (null != oldCursor) {

				while (oldCursor.moveToNext()) {

					MessageInfo message = MessageColumns
							.parseCursorToMessage(oldCursor);

					if (null != message) {

						messageList.add(message);
					}
				}
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return messageList;
	}

	public List<MessageInfo> getAllPhotoMessage(String recipient) {

		List<MessageInfo> messageList = new ArrayList<MessageInfo>();

		Cursor cursor = null;

		Uri uri = URIField.MESSAGE_URI;

		try {

			cursor = mCr.query(uri, null, MessageColumns.RECIPIENT + "=? AND "
					+ MessageColumns.MESSAGE_TYPE + "=?", new String[] {
					recipient, MessageType.PHOTO + "" },
					MessageColumns.CREATETIME + " asc");

			while (cursor.moveToNext()) {

				MessageInfo message = MessageColumns
						.parseCursorToMessage(cursor);

				if (null != message) {

					messageList.add(message);
				}
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return messageList;
	}

	public List<SearchMessageResult> searchMessage(String search) {

		List<SearchMessageResult> searchMessageResultList = new ArrayList<SearchMessageResult>();

		Cursor cursor = null;

		Uri uri = URIField.MESSAGE_URI;

		try {

			cursor = mCr.query(uri, null, MessageColumns.MESSAGE_TYPE
					+ "=? AND " + MessageColumns.TEXT + " like ?",
					new String[] { MessageType.TEXT + "", "%" + search + "%" },
					MessageColumns.CREATETIME + " asc");

			while (cursor.moveToNext()) {

				MessageInfo message = MessageColumns
						.parseCursorToMessage(cursor);

				if (null != message) {

					SearchMessageResult searchMessageResult = new SearchMessageResult();

					searchMessageResult.messageInfo = message;

					searchMessageResult.groupInfo = GroupDbAdapter
							.getInstance().getGroupById(message.recipient);

					searchMessageResultList.add(searchMessageResult);
				}
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return searchMessageResultList;
	}

	public int getUnReadMessageCount(String recipient) {

		int count = 0;

		if (TextUtils.isEmpty(recipient)) {

			Log.e(TAG, "getUnReadMessageCount fail, recipient is null");

			return count;
		}

		Cursor cursor = null;

		Uri uri = URIField.MESSAGE_URI;

		try {

			cursor = mCr.query(uri, new String[] { "count(*)" },
					MessageColumns.RECIPIENT + "=? AND "
							+ MessageColumns.IS_READ + "=?", new String[] {
							recipient, CommonColumnsValue.FALSE_VALUE + "" },
					null);

			if (null != cursor && cursor.moveToNext()) {

				count = cursor.getInt(0);
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return count;
	}

	public int getAllUnReadMessageCount() {

		int count = 0;

		Cursor cursor = null;

		Uri uri = URIField.MESSAGE_URI;

		try {

			cursor = mCr.query(uri, new String[] { "count(*)" },
					MessageColumns.IS_READ + "=?",
					new String[] { CommonColumnsValue.FALSE_VALUE + "" }, null);

			if (null != cursor && cursor.moveToNext()) {

				count = cursor.getInt(0);
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return count;
	}

	public int updateMessageForSend(MessageInfo message) {

		int result = -1;

		if (null == message) {

			Log.e(TAG, "updateMessage fail, message is null");

			return result;
		}

		Uri uri = URIField.MESSAGE_URI;

		ContentValues cv = MessageColumns.updateValues(message);

		result = mCr.update(uri, cv, MessageColumns.ID + "=?",
				new String[] { message.db_id });

		return result;
	}

	public int updateMessageStatusByDBIDForSend(String db_id, int status) {

		int result = -1;

		if (TextUtils.isEmpty(db_id)) {

			Log.e(TAG, "updateMessageStatusByDBIDForSend fail, db_id is null");

			return result;
		}

		Uri uri = URIField.MESSAGE_URI;

		ContentValues cv = new ContentValues();

		cv.put(MessageColumns.STATUS, status);

		result = mCr.update(uri, cv, MessageColumns.ID + "=?",
				new String[] { db_id });

		return result;
	}

	public int updateMessageHasRead(String recipient) {

		int result = -1;

		if (TextUtils.isEmpty(recipient)) {

			Log.e(TAG, "updateMessageRead fail, recipient is null");

			return result;
		}

		Uri uri = URIField.MESSAGE_URI;

		ContentValues cv = new ContentValues();

		cv.put(MessageColumns.IS_READ, CommonColumnsValue.TRUE_VALUE);

		result = mCr.update(uri, cv, MessageColumns.RECIPIENT + "=?",
				new String[] { recipient });

		return result;
	}

	public int updateMediaMessageHasRead(String messageId) {

		int result = -1;

		if (TextUtils.isEmpty(messageId)) {

			Log.e(TAG, "updateMediaMessageRead fail, messageId is null");

			return result;
		}

		Uri uri = URIField.MESSAGE_URI;

		ContentValues cv = new ContentValues();

		cv.put(MessageColumns.MEDIA_IS_READ, CommonColumnsValue.TRUE_VALUE);

		result = mCr.update(uri, cv, MessageColumns.MESSAGEID + "=?",
				new String[] { messageId });

		return result;
	}

	public int deleteMessage(String messageId) {

		int result = -1;

		if (TextUtils.isEmpty(messageId)) {

			return result;
		}

		Uri uri = URIField.MESSAGE_URI;

		result = mCr.delete(uri, MessageColumns.MESSAGEID + "=?",
				new String[] { messageId });

		Log.i(TAG, "deleteMessage, result = " + result);

		return result;
	}

	public int deleteAllMessage(String recipient) {

		int result = -1;

		if (TextUtils.isEmpty(recipient)) {

			return result;
		}

		Uri uri = URIField.MESSAGE_URI;

		result = mCr.delete(uri, MessageColumns.RECIPIENT + "=?",
				new String[] { recipient });

		Log.i(TAG, "deleteAllMessage, result = " + result);

		return result;
	}
}
