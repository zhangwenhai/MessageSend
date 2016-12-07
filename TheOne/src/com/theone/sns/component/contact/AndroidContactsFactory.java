package com.theone.sns.component.contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.SuppressLint;
import android.database.ContentObserver;
import android.util.Log;

@SuppressLint("UseSparseArrays")
public class AndroidContactsFactory {

	private static final String TAG = "AndroidContactsFactory";

	private static List<Contact> contactList = new ArrayList<Contact>();

	private static final AtomicBoolean mHasLoadContacts = new AtomicBoolean(
			false);

	public static List<Contact> getAllContacts() {

		return getAllContacts(false);
	}

	public static List<Contact> getAllContacts(boolean reload) {

		synchronized (mHasLoadContacts) {

			try {

				if (!mHasLoadContacts.get() || reload || contactList.isEmpty()) {

					mHasLoadContacts.set(false);

					contactList.clear();

					Map<Integer, Contact> contactsMap = new HashMap<Integer, Contact>();

					AndroidCotactsFactory_2_0.getAllContacts(contactsMap);

					if (null != contactsMap && contactsMap.size() > 0) {

						Set<Entry<Integer, Contact>> contactSet = contactsMap
								.entrySet();

						for (Entry<Integer, Contact> contactEntry : contactSet) {

							contactList.add(contactEntry.getValue());
						}
					}

					mHasLoadContacts.set(true);
				}
			} catch (Exception e) {

				Log.e(TAG, e.getMessage());

				mHasLoadContacts.set(false);
			}
		}

		return contactList;
	}

	public static boolean hasLoadEnd() {

		return mHasLoadContacts.get() && contactList.size() > 0;
	}

	public static void clear() {

		mHasLoadContacts.set(false);

		contactList.clear();

		AndroidCotactsFactory_2_0.unRegisterContactsContentObserver();
	}
}

class AndroidContactsObserver extends ContentObserver {

	public AndroidContactsObserver() {
		super(null);
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);

		UploadContactsThread.getInstance().startQuery();
	}
}
