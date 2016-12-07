package com.theone.sns.component.contact;

import android.os.Process;
import android.util.Log;

import com.theone.sns.ui.base.AbstractRefreshUIThread;

public class UploadContactsThread extends AbstractRefreshUIThread {

	private static final String TAG = "UploadContactsThread";

	private static UploadContactsThread mUploadContactsThread = null;

	private UploadContactsThread() {
	}

	public static synchronized UploadContactsThread getInstance() {

		if (null == mUploadContactsThread) {

			mUploadContactsThread = new UploadContactsThread();

			mUploadContactsThread.setSleepTime(5000);
		}

		return mUploadContactsThread;
	}

	@Override
	public void loadUIData() {

		try {

			Log.d(TAG, "contact onChange,getAllContacts and upload contacts");

			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

			AndroidContactsFactory.getAllContacts(true);

		} catch (Exception e) {

			Log.e(TAG, e.getMessage());
		}
	}

	public static synchronized void exitUploadContacts() {

		if (null != mUploadContactsThread) {

			mUploadContactsThread.exitThread();

			mUploadContactsThread = null;
		}
	}
}
