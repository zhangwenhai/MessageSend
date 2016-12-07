package com.theone.sns.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Hacky fix for Issue #4 and
 * http://code.google.com/p/android/issues/detail?id=18990
 * <p/>
 * ScaleGestureDetector seems to mess up the touch events, which means that
 * ViewGroups which make use of onInterceptTouchEvent throw a lot of
 * IllegalArgumentException: pointerIndex out of range.
 * <p/>
 * There's not much I can do in my code for now, but we can mask the result by
 * just catching the problem and ignoring it.
 *
 * @author Chris Banes
 */
public class HackyViewPager extends ViewPager {

    private float preX;

    public HackyViewPager(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        if (isInEditMode()) {
            return;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        boolean onInterceptTouchFlag = false;
//		try {
//			onInterceptTouchFlag = super.onInterceptTouchEvent(ev);
//			return onInterceptTouchFlag;
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//			return false;
//		} catch (ArrayIndexOutOfBoundsException e) {
//			e.printStackTrace();
//			return false;
//		}
        boolean res = super.onInterceptTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            preX = ev.getX();
        } else {
            if (Math.abs(ev.getX() - preX) > 4) {
                return true;
            } else {
                preX = ev.getX();
            }
        }
        return res;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }
}
