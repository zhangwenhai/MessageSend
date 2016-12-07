package com.theone.sns.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;

import com.theone.sns.TheOneApp;

@SuppressWarnings("deprecation")
public final class SMSHelper extends BroadcastReceiver {

	public static final String TAG = "SMSHelper";

	public static final String MESSAGE_STATUS_RECEIVED_ACTION = SMSHelper.class
			.getName() + ".MESSAGE_STATUS_RECEIVED";

	public static final String MESSAGE_SENT = "com.android.mms.transaction.MESSAGE_SENT";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {

			String action = intent.getAction();

			if (MESSAGE_STATUS_RECEIVED_ACTION.equals(action)) {

				Uri messageUri = intent.getData();

				Bundle bundle = intent.getExtras();

				if (bundle == null) {
					return;
				}

				byte[] pdu = (byte[]) bundle.getByteArray("pdu");

				SmsMessage message = SmsMessage.createFromPdu(pdu);

				int status = message.getStatus();

				updateMessageStatus(context, messageUri, status);

			} else if (MESSAGE_SENT.equals(action)) {

				Uri messageUri = intent.getData();

				notifySmsSentOK(context, messageUri);
			}

		} catch (Exception e) {

			Log.e(TAG, e + "");
		}
	}

	private void updateMessageStatus(Context context, Uri messageUri, int status) {

		final Uri STATUS_URI = Uri.parse("content://sms/status");

		ContentResolver contentResolver = context.getContentResolver();

		final String[] ID_PROJECTION = new String[] { "_id" };

		Cursor cursor = contentResolver.query(messageUri, ID_PROJECTION, null,
				null, null);

		if ((cursor != null) && cursor.moveToFirst()) {

			int messageId = cursor.getInt(0);

			Uri updateUri = ContentUris.withAppendedId(STATUS_URI, messageId);

			ContentValues contentValues = new ContentValues(1);

			Log.d(TAG, "updateMessageStatus status=" + status);

			contentValues.put("status", status);

			contentResolver.update(updateUri, contentValues, null, null);

		} else {

			Log.e(TAG, "Can't find message for status update: " + messageUri);
		}

		if (cursor != null) {
			cursor.close();
		}
	}

	private static final Map<Integer, Uri> sPendingMsg = new ConcurrentHashMap<Integer, Uri>(
			64);

	public static void notifySmsSentOK(Context context, Uri messageUri) {

		if (messageUri == null) {
			return;
		}

		int msgId = Integer.parseInt(messageUri.getLastPathSegment());

		Uri newSMSUri = sPendingMsg.get(msgId);

		if (newSMSUri != null) {

			ContentValues values = new ContentValues();

			values.put("type", 2);

			context.getContentResolver().update(newSMSUri, values, null, null);

			sPendingMsg.remove(msgId);
		}
	}

	private static void sendMsgAndListen(Context context, SmsManager manager,
			String contact, String message, Uri newSMSUri) {

		ArrayList<String> messages = manager.divideMessage(message);

		int messageCount = messages.size();

		ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>(
				messageCount);

		ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>(
				messageCount);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		boolean requestDeliveryReport = prefs.getBoolean(
				"pref_key_sms_delivery_reports", false);

		int msgId = Integer.parseInt(newSMSUri.getLastPathSegment());

		sPendingMsg.put(msgId, newSMSUri);

		for (int j = 0; j < messageCount; j++) {

			if (requestDeliveryReport) {

				deliveryIntents.add(PendingIntent.getBroadcast(context, 0,
						new Intent(SMSHelper.MESSAGE_STATUS_RECEIVED_ACTION,
								newSMSUri, context, SMSHelper.class), 0));
			}

			sentIntents.add(PendingIntent.getBroadcast(context, 0,
					new Intent(SMSHelper.MESSAGE_SENT, newSMSUri, context,
							SMSHelper.class), 0));
		}

		if (contact != null && messages != null) {
			manager.sendMultipartTextMessage(contact, null, messages,
					sentIntents, deliveryIntents);
		}

		Log.d(TAG, "sendMultipartTextMessage end");
	}

	private static void moveToFailed(ContentResolver cr, ContentValues values) {

		String address = values.getAsString("address");

		int thread_id = values.getAsInteger("thread_id");

		if (thread_id == -1) {
			return;
		}

		cr.insert(Uri.parse("content://sms"), values);

		values = new ContentValues();

		values.put("address", address);

		values.put("date", System.currentTimeMillis());

		values.put("read", 1);

		values.put("body", "");

		values.put("type", 5);

		values.put("thread_id", thread_id);

		Uri newUri = cr.insert(Uri.parse("content://sms"), values);

		cr.delete(newUri, null, null);
	}

	// type
	// ALL = 0;
	// INBOX = 1;
	// SENT = 2;
	// DRAFT = 3;
	// OUTBOX = 4;
	// FAILED = 5;
	// QUEUED = 6;
	public static boolean sendSMS(String sAddress, String message) {

		Context context = TheOneApp.getContext();

		ContentResolver cr = context.getContentResolver();

		Uri threadUri = Uri.parse("content://mms-sms/threadID");

		Uri.Builder builder = threadUri.buildUpon();

		builder.appendQueryParameter("recipient", sAddress);

		int thread_id = -1;

		Cursor threadCursor = cr.query(builder.build(), null, null, null, null);

		if (threadCursor == null) {
			return false;
		}

		if (threadCursor.moveToNext()) {
			thread_id = threadCursor.getInt(0);
		}

		threadCursor.close();

		Log.e(TAG, "thread_id=" + thread_id);

		Uri uriSent = Uri.parse("content://sms");

		ContentValues values = new ContentValues();

		values.put("address", sAddress);

		values.put("date", System.currentTimeMillis());

		values.put("read", 1);

		values.put("body", message);

		values.put("type", 5);

		values.put("thread_id", thread_id);

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		if (tm == null) {
			return false;
		}

		if (tm.getSimState() != TelephonyManager.SIM_STATE_READY) {

			Log.e(TAG, "sim not ready");

			moveToFailed(cr, values);

			return false;
		}

		if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_UNKNOWN) {

			Log.e(TAG,
					"tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_UNKNOWN");

			moveToFailed(cr, values);

			return false;
		}

		SmsManager manager = SmsManager.getDefault();

		if (manager == null) {

			Log.e(TAG, "SmsManager is null");

			moveToFailed(cr, values);

			return false;
		}

		values.put("type", 4);

		Uri newSMSUri = cr.insert(uriSent, values);

		values.clear();

		sendMsgAndListen(context, manager, sAddress, message, newSMSUri);

		return true;
	}

	public static final void sendSMS(Context c, final String to,
			final String content) {

		if (c == null || to == null || content == null)
			return;

		Uri smsToUri = Uri.parse("smsto:" + to);

		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

		intent.putExtra("sms_body", content);

		c.startActivity(intent);
	}
}
