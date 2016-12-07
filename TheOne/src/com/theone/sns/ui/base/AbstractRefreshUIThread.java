package com.theone.sns.ui.base;

import java.util.concurrent.atomic.AtomicLong;

import android.util.Log;

public abstract class AbstractRefreshUIThread extends Thread {

	private static String TAG = AbstractRefreshUIThread.class.getSimpleName();

	private AtomicLong mLastRefreshTime = new AtomicLong(0);

	private boolean mIsStillAlive = false;

	private int mSleepTime = 2000;

	public abstract void loadUIData();

	@Override
	public void run() {
		long lastUpdateTime;
		synchronized (mLastRefreshTime) {
			lastUpdateTime = mLastRefreshTime.get();
		}

		while (mIsStillAlive) {
			try {

				loadUIData();

				sleep(mSleepTime);

				synchronized (mLastRefreshTime) {
					if (lastUpdateTime == mLastRefreshTime.get()) {
						mLastRefreshTime.wait();
					}
					lastUpdateTime = mLastRefreshTime.get();
				}

				if (!mIsStillAlive)
					break;
			} catch (Exception e) {
				Log.e(TAG, "load ui data error");
			}
		}
	}

	public void setSleepTime(int sleepTime) {
		mSleepTime = sleepTime;
	}

	public void startQuery() {
		notifyUIDataChanged(false);
	}

	public void exitThread() {
		mIsStillAlive = false;
		notifyUIDataChanged(true);
	}

	private void notifyUIDataChanged(boolean exit) {
		if (!exit && !mIsStillAlive) {
			mIsStillAlive = true;
			start();
		}

		synchronized (mLastRefreshTime) {
			long now = System.currentTimeMillis();
			mLastRefreshTime.set(now);
			mLastRefreshTime.notify();
		}
	}
}
