package com.theone.sns.util.uiwidget;

import android.view.MotionEvent;
import android.view.View;

public interface IVoiceViewControl {

	void showRecordTip(int paddingBottom);

	void hideRecordTip();

	void tipCanceled();
	
	void tipTooShort();

	void setRmsdB(float rmsdb, long recordLen);

	void dealDownEvent(int paddingBottom, View v, MotionEvent event);

	void dealMoveEvent(MotionEvent event);

	boolean dealUpEvent(MotionEvent event);
}
