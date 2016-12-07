package com.theone.sns.component.contact;

import java.util.Map;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;

import com.theone.sns.TheOneApp;

public class AndroidCotactsFactory_2_0 {

	private static AndroidContactsObserver g_observer = null;

	public static void getAllContacts(Map<Integer, Contact> contacts) {

		Cursor cur = TheOneApp
				.getContext()
				.getContentResolver()
				.query(ContactsContract.Data.CONTENT_URI,
						new String[] { Data.CONTACT_ID, Data.MIMETYPE,
								Phone.NUMBER, Email.DATA,
								CommonDataKinds.StructuredName.DISPLAY_NAME,
								CommonDataKinds.Photo.PHOTO }, null, null, null);

		if (null == cur) {

			return;
		}

		int personColumn = cur.getColumnIndex(Data.CONTACT_ID);

		int mimeColumn = cur.getColumnIndex(Data.MIMETYPE);

		int numberColumn = cur.getColumnIndex(Phone.NUMBER);

		int emailColumn = cur.getColumnIndex(Email.DATA);

		int nameColumn = cur
				.getColumnIndex(CommonDataKinds.StructuredName.DISPLAY_NAME);

		while (cur.moveToNext()) {

			String mime = cur.getString(mimeColumn);

			int personId = cur.getInt(personColumn);

			Contact contact = contacts.get(personId);

			if (contact == null) {

				contact = new Contact();

				contact.personId = personId;

				contacts.put(personId, contact);
			}

			if (mime.equals(CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {

				String phone = cur.getString(numberColumn);

				contact.addPhone(phone);

			} else if (mime.equals(CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {

				contact.addEmail(cur.getString(emailColumn));

			} else if (mime
					.equals(CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)) {

				contact.name = cur.getString(nameColumn);
			}
		}

		cur.close();

		if (g_observer == null) {

			g_observer = new AndroidContactsObserver();

			TheOneApp
					.getContext()
					.getContentResolver()
					.registerContentObserver(
							ContactsContract.Contacts.CONTENT_URI, true,
							g_observer);
		}
	}

	public static void unRegisterContactsContentObserver() {

		if (null != g_observer) {

			TheOneApp.getContext().getContentResolver()
					.unregisterContentObserver(g_observer);

			g_observer = null;
		}
	}
}
