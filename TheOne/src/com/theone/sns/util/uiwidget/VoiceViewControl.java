package com.theone.sns.util.uiwidget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.TheOneApp;
import com.theone.sns.util.HelperFunc;

public class VoiceViewControl implements IVoiceViewControl {

	private static final String TAG = "VoiceViewControl";

	private static final float BLUE_F = 0f;
	private static final float RED_F = 1.0f;

	private View m_wholeView;
	private RadialGradientView m_gradientView;
	private SinusView m_sinusView;
	private TextView m_recordTimeTip;
	private TextView m_recordTopTip;

	private int m_centerX;
	private int m_centerY;
	private int m_radius;
	private Rect m_rect;
	private boolean m_cancel;
	private ImageView m_recordBtn;
	private TextView m_recordBtnTip;

	public VoiceViewControl() {
		m_wholeView = LayoutInflater.from(TheOneApp.getContext()).inflate(R.layout.voice_top, null);
		m_wholeView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				removeFromWindow(v);
			}
		});

		m_gradientView = (RadialGradientView) m_wholeView.findViewById(R.id.backView);
		m_sinusView = (SinusView) m_wholeView.findViewById(R.id.sinusview);
		m_recordTimeTip = (TextView) m_wholeView.findViewById(R.id.record_time_tip);
		m_recordTopTip = (TextView) m_wholeView.findViewById(R.id.record_top_tip);
	}

	@Override
	public void setRmsdB(float rmsdb, long recordLen) {
		m_sinusView.setRmsdB(rmsdb);
		m_recordTimeTip.setText(HelperFunc.getRecordingSeconds(recordLen) + "S");
	}

	public void setRecordBtnContainer(View recordBtnContainer) {
		m_recordBtn = (ImageView) recordBtnContainer.findViewById(R.id.chat_voice_btn);
		m_recordBtnTip = (TextView) recordBtnContainer.findViewById(R.id.chat_voice_btn_tip);
	}

	public void tipTooShort() {
		m_recordTopTip.setText(R.string.talkview_tooshort);
	}

	public void tipCanceled() {
	}

	@Override
	public void showRecordTip(int paddingBottom) {
		try {
			m_wholeView.setPadding(0, 0, 0, paddingBottom);
			m_wholeView.setVisibility(View.VISIBLE);

			WindowManager wm = (WindowManager) TheOneApp.getContext().getSystemService(
					Context.WINDOW_SERVICE);
			WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
			wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
			wmParams.format = PixelFormat.RGBA_8888;
			wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
					| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
			wmParams.width = WindowManager.LayoutParams.FILL_PARENT;
			wmParams.height = WindowManager.LayoutParams.FILL_PARENT;

			wm.addView(m_wholeView, wmParams);

			m_gradientView.setF(BLUE_F);
		} catch (Exception e) {
		}
	}

	@Override
	public void hideRecordTip() {
		removeFromWindow(m_wholeView);

		if (null != m_recordBtn) {
			m_recordBtn.setImageResource(R.drawable.voicebtn_hold);
		}

		if (null != m_recordBtnTip) {
			m_recordBtnTip.setText(R.string.hold_to_talk);
			m_recordBtnTip.setVisibility(View.VISIBLE);
		}
	}

	private static void removeFromWindow(View v) {
		try {
			v.setVisibility(View.GONE);
			WindowManager wm = (WindowManager) v.getContext().getApplicationContext()
					.getSystemService(Context.WINDOW_SERVICE);
			wm.removeView(v);
		} catch (Exception e) {
		}
	}

	@Override
	public void dealDownEvent(int paddingBottom, View v, MotionEvent event) {
		if (null != m_recordBtn) {
			m_recordBtn.setImageResource(R.drawable.voicebtn_hold_on);
		}

		m_rect = getRectInWindow(v);

		m_radius = v.getWidth() / 2;

		m_centerX = Math.abs(m_rect.left) + m_radius;
		m_centerY = Math.abs(m_rect.top) + m_radius;

		if (null != m_recordBtnTip) {
			m_recordBtnTip.setText(R.string.release_to_send);
			m_recordBtnTip.setVisibility(View.VISIBLE);
		}

		m_recordTopTip.setText(R.string.drag_up_cancel);
		m_cancel = false;
		m_recordTimeTip.setText("0S");
	}

	@Override
	public void dealMoveEvent(MotionEvent event) {
		double x = Math.abs(event.getRawX() - m_centerX);
		double y = Math.abs(event.getRawY() - m_centerY);

		double dis = (Math.pow(x, 2) + Math.pow(y, 2)) / (Math.pow(m_radius, 2));

		if (dis < RED_F) {
			dis = BLUE_F;
		}

		if (dis > RED_F) {
			dis = RED_F;
		}

		m_cancel = (dis >= RED_F);

		if (null != m_recordBtn) {
			m_recordBtn.setImageResource(m_cancel ? R.drawable.voicebtn_hold_off
					: R.drawable.voicebtn_hold_on);
		}

		if (null != m_recordBtnTip) {
			m_recordBtnTip.setVisibility(m_cancel ? View.INVISIBLE : View.VISIBLE);
		}

		m_recordTopTip.setText(m_cancel ? R.string.record_release_cancel : R.string.drag_up_cancel);

		m_gradientView.setF(dis);
	}

	@Override
	public boolean dealUpEvent(MotionEvent event) {
		// check whether is canceled
		return m_cancel;
	}

	private static Rect getRectInWindow(View v) {
		int[] loc = new int[2];
		v.getLocationOnScreen(loc);
		Rect r = new Rect(loc[0], loc[1], loc[0] + v.getMeasuredWidth(), loc[1]
				+ v.getMeasuredHeight());
		return r;
	}
}
