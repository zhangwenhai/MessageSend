package com.theone.sns.util.uiwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.theone.sns.TheOneApp;
import com.theone.sns.util.HelperFunc;

public class SinusView extends View {

	private static final String TAG = "SinusView";
	final float SCALE = TheOneApp.getContext().getResources().getDisplayMetrics().scaledDensity;

	private static final float kMinDBvalue = -80.0f;

	// / The amplitude that is used when the incoming microphone amplitude is
	// near zero. Setting a value greater 0 provides a more vivid visualization.
	private float idleAmplitude;

	// / The phase of the sinus wave. Default: 0.
	private float phase;

	// / The frequency of the sinus wave. The higher the value, the more sinus
	// wave peaks you will have. Default: 1.5
	private float frequency;

	// / The damping factor that is used to calm the wave down after a sound
	// level peak. Default: 0.86
	private float dampingFactor;

	// / The number of additional waves in the background. The more waves, to
	// more CPU power is needed. Default: 4.
	private float waves;

	// / The actual amplitude the view is visualizing. This amplitude is based
	// on the microphone's amplitude
	private float amplitude;

	// / The damped amplitude.
	private float dampingAmplitude;

	// / The lines are joined stepwise, the more dense you draw, the more CPU
	// power is used. Default: 5.
	private float density;

	// / The phase shift that will be applied with each delivering of the
	// microphone's value. A higher value will make the waves look more nervous.
	// Default: -0.15.
	private float phaseShift;

	// / The white value of the color to draw the waves with. Default: 1.0
	// (white).
	private float whiteValue;

	// / Set to NO, if you want to stop the view to oscillate.
	private boolean oscillating;

	private int tick;

	private Paint m_paint;

	public SinusView(Context context) {
		super(context);
		init();
		setBackgroundColor(Color.TRANSPARENT);
	}

	public SinusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		setBackgroundColor(Color.TRANSPARENT);
	}

	private void init() {
		frequency = 1.5f;
		phase = 0;
		whiteValue = 1.0f;
		idleAmplitude = 0.02f;
		amplitude = idleAmplitude;
		dampingFactor = 0.86f;
		waves = 5.0f;
		phaseShift = -0.4f;
		density = HelperFunc.dip2px(1.5f);
		m_paint = new Paint();
		m_paint.setAntiAlias(true);
		m_paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		meterTable = new SoundMeterTable(kMinDBvalue, 400, 2.0f);
	}

	private void update(float value) {
		// int requiredTickes = 1; // Alter this to draw more or less often
		// tick = (tick + 1) % requiredTickes;

		// Let's use the buffer's first float value to determine the current
		// sound level.
		// float value = fabsf(*buffer[0]);

		// / If we defined the current sound level as the amplitude of the wave,
		// the wave would jitter very nervously.
		// / To avoid this, we use an inert amplitude that lifts slowly if the
		// value is currently high, and damps itself
		// / if the value decreases.
		if (value > dampingAmplitude) {
			dampingAmplitude += (Math.min(value, 1.0f) - dampingAmplitude) / 4.0f;
			// LogUtil.debug(VIEW_LOG_TAG, "dampingAmplitude 1 == " +
			// dampingAmplitude);
		} else if (value < 0.01) {
			dampingAmplitude *= dampingFactor;
			// LogUtil.debug(VIEW_LOG_TAG, "dampingAmplitude 2 == " +
			// dampingAmplitude);
		}

		phase += phaseShift;
		amplitude = Math.max(Math.min(dampingAmplitude * 20, 1.0f), idleAmplitude);
		// LogUtil.debug(VIEW_LOG_TAG, "amplitude == " + amplitude +
		// " phase == " + phase);

		postInvalidate();
	}

	private long mCurrentTime = 0;
	private SoundMeterTable meterTable;

	public void setRmsdB(float rmsdB) {
		// LogUtil.debug(VIEW_LOG_TAG, "mCurrentTime == " +
		// (System.currentTimeMillis() - mCurrentTime));
		synchronized (this) {
			mCurrentTime = System.currentTimeMillis();
			float level = meterTable.valueAt(rmsdB);
			amplitude = Math.max(level, idleAmplitude);
			// LogUtil.debug(VIEW_LOG_TAG, "amplitude == " + amplitude);
			phase += phaseShift;
			postInvalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		long mcurrentTime = System.currentTimeMillis();
		synchronized (this) {
			super.onDraw(canvas);

			canvas.save();
			m_paint.reset();
			// We draw multiple sinus waves, with equal phases but altered
			// amplitudes, multiplied by a parable function.
			for (int i = 0; i < waves + 1; i++) {
				// The first wave is drawn with a 2px stroke width, all others a
				// with 1px stroke width.
				m_paint.setStrokeWidth((i == 0) ? SCALE * 1.5f : SCALE * 0.8f);

				float halfHeight = getHeight() / 2.0f;
				float width = getWidth() * 1.0f;
				float mid = width / 2.0f;

				final float maxAmplitude = halfHeight - 4; // 4 corresponds to
															// twice
															// the stroke width

				// Progress is a value between 1.0 and -0.5, determined by the
				// current wave idx, which is used to alter the wave's
				// amplitude.
				float progress = 1.0f - (float) (i / waves);
				float normedAmplitude = (1.5f * progress - 0.5f) * amplitude;

				// Choose the color based on the progress (that is, based on the
				// wave idx)
				m_paint.setColor(Color.WHITE);
				m_paint.setAntiAlias(true);
				final float alpha = progress / 3.0f * 2.0f + 1.0f / 3.0f;
				// LogUtil.debug(VIEW_LOG_TAG, "alpha == " + alpha +
				// " normedAmplitude == " + normedAmplitude);
				m_paint.setAlpha((int) (255 * alpha));

				float startX = 0;
				float startY = 0;

				for (float x = 0; x < width + density; x += density) {

					// We use a parable to scale the sinus wave, that has its
					// peak
					// in the middle of the view.
					float scaling = (float) (-Math.pow(1.0f / mid * (x - mid), 2) + 1.0f);

					float y = (float) (scaling * maxAmplitude * normedAmplitude
							* Math.sin(2.0f * Math.PI * (1.0f * x / width) * frequency + phase) + halfHeight);

					if (x == 0) {
						startX = x;
						startY = y;
					} else {
						canvas.drawLine(startX, startY, x, y, m_paint);
						startX = x;
						startY = y;
					}
				}
			}

			canvas.restore();
		}
		// LogUtil.debug(VIEW_LOG_TAG, "onDraw mCurrentTime == " +
		// (System.currentTimeMillis() - mcurrentTime));
	}
}
