package com.theone.sns.util.uiwidget;

public class SoundMeterTable {
	float mMinDecibels;
	float mDecibelResolution;
	float mScaleFactor;
	float[] mTable;

	public SoundMeterTable(float inMinDecibels, int inTableSize, float inRoot) {
		mMinDecibels = inMinDecibels;
		mDecibelResolution = mMinDecibels / (inTableSize - 1);
		mScaleFactor = 1.0f / mDecibelResolution;

		mTable = new float[inTableSize];

		double minAmp = Math.pow(10., 0.05 * inMinDecibels);
		double ampRange = 1.0 - minAmp;
		double invAmpRange = 1.0 / ampRange;

		double rroot = 1. / inRoot;
		for (int i = 0; i < inTableSize; ++i) {
			double decibels = i * mDecibelResolution;
			double amp = Math.pow(10., 0.05 * decibels);
			double adjAmp = (amp - minAmp) * invAmpRange;
			mTable[i] = (float) Math.pow(adjAmp, rroot);
		}
	}

	public float valueAt(float inDecibels) {
		if (inDecibels < mMinDecibels)
			return 0.0f;
		if (inDecibels >= 0.)
			return 1.0f;
		int index = (int) (inDecibels * mScaleFactor);
		return mTable[index];
	}

}
