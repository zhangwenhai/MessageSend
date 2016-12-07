package com.theone.sns.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.theone.sns.R;

import java.util.Timer;
import java.util.TimerTask;

public final class RoundProgressBar extends View {

    private Paint mFramePaint;

    private Paint mRoundPaints;
    private RectF mRoundOval;
    private int mPaintWidth;
    private int mPaintColor;

    private long mStartProgress;
    private long mCurProgress;
    private long mMaxProgress;

    private boolean mBRoundPaintsFill;
    private int mSidePaintInterval;

    private Paint mBottomPaint;

    private boolean mBShowBottom;

    private Handler mHandler = new Handler();

    /**
     * whether doing cartoon
     */
    private boolean mBCartoom;

    private Timer mTimer;
    private MyTimerTask mTimerTask;

    private long mSaveMax;

    /**
     * GAP
     */
    private final int mGap = 25;

    /**
     * current float progress
     */
    private long mCurrentProcess;

    public RoundProgressBar(Context context) {
        super(context);
        if (isInEditMode()) {
            return;
        }
        initParam();
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        initParam();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);

        mMaxProgress = array.getInt(R.styleable.RoundProgressBar_max, 100);
        mSaveMax = mMaxProgress;
        // whether paint mode
        mBRoundPaintsFill = array.getBoolean(R.styleable.RoundProgressBar_fill, true);
        if (mBRoundPaintsFill == false) {
            mRoundPaints.setStyle(Paint.Style.STROKE);
            mBottomPaint.setStyle(Paint.Style.STROKE);
        }

        // interval paint
        mSidePaintInterval = array.getInt(R.styleable.RoundProgressBar_Inside_Interval, 0);

        mBShowBottom = array.getBoolean(R.styleable.RoundProgressBar_Show_Bottom, true);

        mPaintWidth = array.getInt(R.styleable.RoundProgressBar_Paint_Width, 3);

        mPaintWidth = HelperFunc.scale(mPaintWidth);
        if (mBRoundPaintsFill) {
            // paint mode, set pen width 0
            mPaintWidth = 0;
        }

        mRoundPaints.setStrokeWidth(mPaintWidth);
        mBottomPaint.setStrokeWidth(mPaintWidth);

        mPaintColor = array.getColor(R.styleable.RoundProgressBar_Paint_Color, 0xffffcc00);
        mRoundPaints.setColor(mPaintColor);

        // must recycle
        array.recycle();
    }

    public void setmSidePaintInterval(int mSidePaintInterval) {
        this.mSidePaintInterval = mSidePaintInterval;
    }

    public void setmPaintColor(int mPaintColor) {
        this.mPaintColor = mPaintColor;
    }

    public void setmPaintWidth(int mPaintWidth) {
        this.mPaintWidth = mPaintWidth;
    }

    public void setmBShowBottom(boolean mBShowBottom) {
        this.mBShowBottom = mBShowBottom;
    }

    public void setmBRoundPaintsFill(boolean mBRoundPaintsFill) {
        this.mBRoundPaintsFill = mBRoundPaintsFill;
    }

    private void initParam() {
        mFramePaint = new Paint();
        mFramePaint.setAntiAlias(true);
        mFramePaint.setStyle(Paint.Style.STROKE);
        mFramePaint.setStrokeWidth(0);

        mPaintWidth = 0;
        mPaintColor = 0xffffcc00;

        mRoundPaints = new Paint();
        mRoundPaints.setAntiAlias(true);
        mRoundPaints.setStyle(Paint.Style.FILL);

        mRoundPaints.setStrokeWidth(mPaintWidth);
        mRoundPaints.setColor(mPaintColor);

        mBottomPaint = new Paint();
        mBottomPaint.setAntiAlias(true);
        mBottomPaint.setStyle(Paint.Style.FILL);
        mBottomPaint.setStrokeWidth(mPaintWidth);
        mBottomPaint.setColor(Color.GRAY);
        mStartProgress = -90;
        mCurProgress = 0;
        mMaxProgress = 100;
        mSaveMax = 100;
        mBRoundPaintsFill = true;
        mBShowBottom = true;
        mSidePaintInterval = 0;
        mRoundOval = new RectF(0, 0, 0, 0);
        mCurrentProcess = 0;
        mBCartoom = false;
    }

    public synchronized void setProgress(long progress) {
        mCurProgress = progress;
        if (mCurProgress < 0) {
            mCurProgress = 0;
        }

        if (mCurProgress > mMaxProgress) {
            mCurProgress = mMaxProgress;
        }

        invalidate();
    }

    public synchronized long getProgress() {
        return mCurProgress;
    }

    public synchronized void setMax(long max) {
        if (max <= 0) {
            return;
        }

        mMaxProgress = max;
        if (mCurProgress > max) {
            mCurProgress = max;
        }

        mSaveMax = mMaxProgress;

        invalidate();
    }

    public synchronized long getMax() {
        return mMaxProgress;
    }

    public void setPaintColor(int color) {
        mRoundPaints.setColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isInEditMode()) {
            return;
        }
        if (mSidePaintInterval != 0) {
            mRoundOval.set(mPaintWidth / 2 + mSidePaintInterval, mPaintWidth / 2
                    + mSidePaintInterval, w - mPaintWidth / 2 - mSidePaintInterval, h - mPaintWidth
                    / 2 - mSidePaintInterval);
        } else {
            int sl = getPaddingLeft();
            int sr = getPaddingRight();
            int st = getPaddingTop();
            int sb = getPaddingBottom();

            mRoundOval.set(sl + mPaintWidth / 2, st + mPaintWidth / 2, w - sr - mPaintWidth / 2, h
                    - sb - mPaintWidth / 2);
        }
    }

    public synchronized void startCartoom(long time) {
        startCartoom(0, time);
    }

    public synchronized void startCartoom(long start, long time) {
        if (time <= 0 || mBCartoom == true || start < 0 || start > time) {
            return;
        }
        mBCartoom = true;

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

        mMaxProgress = time;
        mSaveMax = mMaxProgress;

        setProgress(start);

        mCurrentProcess = start;

        mTimerTask = new MyTimerTask();

        if (null != mTimer) {
            mTimer.cancel();
            mTimer = null;
        }

        mTimer = new Timer();
        mTimer.schedule(mTimerTask, mGap, mGap);
    }

    public synchronized void stopCartoom() {
        mBCartoom = false;
        mMaxProgress = mSaveMax;

        setProgress(0);
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

        if (null != mTimer) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode()) {
            return;
        }
        if (mBShowBottom) {
            canvas.drawArc(mRoundOval, 0, 360, mBRoundPaintsFill, mBottomPaint);
        }

        float rate = (float) mCurProgress / mMaxProgress;
        float sweep = 360 * rate;
        canvas.drawArc(mRoundOval, mStartProgress, sweep, mBRoundPaintsFill, mRoundPaints);
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            updateProgress();
        }
    }

    private void updateProgress() {
        if (null != mHandler) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mBCartoom == false) {
                        return;
                    }

                    if (mCurrentProcess > mMaxProgress) {
                        mBCartoom = false;
                        mMaxProgress = mSaveMax;
                        if (mTimerTask != null) {
                            mTimerTask.cancel();
                            mTimerTask = null;
                        }
                        return;
                    }

                    mCurrentProcess += mGap;
                    setProgress(mCurrentProcess);
                }
            });
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopCartoom();
        super.onDetachedFromWindow();
    }
}
