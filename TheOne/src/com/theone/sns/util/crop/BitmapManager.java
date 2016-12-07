package com.theone.sns.util.crop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileDescriptor;
import java.util.WeakHashMap;


/**
 * This class provides several utilities to cancel bitmap decoding.
 *
 * The function decodeFileDescriptor() is used to decode a bitmap. During
 * decoding if another thread wants to cancel it, it calls the function
 * cancelThreadDecoding() specifying the Thread which is in decoding.
 *
 * cancelThreadDecoding() is sticky until allowThreadDecoding() is called.
 */
public class BitmapManager {
	private static final String TAG = "BitmapManager";

	private static enum State {
		CANCEL, ALLOW
	}

	private static class ThreadStatus {
		public State mState = State.ALLOW;
		public BitmapFactory.Options mOptions;

		@Override
		public String toString() {
			String s;
			if (mState == State.CANCEL) {
				s = "Cancel";
			} else if (mState == State.ALLOW) {
				s = "Allow";
			} else {
				s = "?";
			}
			s = "thread state = " + s + ", options = " + mOptions;
			return s;
		}
	}

	private final WeakHashMap<Thread, ThreadStatus> mThreadStatus = new WeakHashMap<Thread, ThreadStatus>();

	private static BitmapManager sManager = null;

	private BitmapManager() {
	}

	/**
	 * Get thread status and create one if specified.
	 */
	private synchronized ThreadStatus getOrCreateThreadStatus(Thread t) {
		ThreadStatus status = mThreadStatus.get(t);
		if (status == null) {
			status = new ThreadStatus();
			mThreadStatus.put(t, status);
		}
		return status;
	}

	/**
	 * The following three methods are used to keep track of
	 * BitmapFaction.Options used for decoding and cancelling.
	 */
	private synchronized void setDecodingOptions(Thread t,
			BitmapFactory.Options options) {
		getOrCreateThreadStatus(t).mOptions = options;
	}

	synchronized void removeDecodingOptions(Thread t) {
		ThreadStatus status = mThreadStatus.get(t);
		status.mOptions = null;
	}

	/**
	 * The following three methods are used to keep track of which thread is
	 * being disabled for bitmap decoding.
	 */
	public synchronized boolean canThreadDecoding(Thread t) {
		ThreadStatus status = mThreadStatus.get(t);
		if (status == null) {
			// allow decoding by default
			return true;
		}

		boolean result = (status.mState != State.CANCEL);
		return result;
	}

	public synchronized void allowThreadDecoding(Thread t) {
		getOrCreateThreadStatus(t).mState = State.ALLOW;
	}

	public static synchronized BitmapManager instance() {
		if (sManager == null) {
			sManager = new BitmapManager();
		}
		return sManager;
	}

	/**
	 * The real place to delegate bitmap decoding to BitmapFactory.
	 */
	public Bitmap decodeFileDescriptor(FileDescriptor fd,
			BitmapFactory.Options options) {
		if (options.mCancel) {
			return null;
		}

		Thread thread = Thread.currentThread();
		if (!canThreadDecoding(thread)) {
			Log.d(TAG, "Thread " + thread + " is not allowed to decode.");
			return null;
		}

		setDecodingOptions(thread, options);
		Bitmap b = BitmapFactory.decodeFileDescriptor(fd, null, options);

		removeDecodingOptions(thread);
		return b;
	}
}
