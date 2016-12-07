package com.theone.sns.ui.publish;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.theone.sns.R;

public class AnimatedTagLayout extends RelativeLayout {

	Context mContext;

	ImageView circleWhiteButton;

	ImageView circleBlackButton;

	void startAnimation() {
		final Animation whiteAnim1 = AnimationUtils.loadAnimation(mContext, R.anim.white_anim1);
		final Animation whiteAnim2 = AnimationUtils.loadAnimation(mContext, R.anim.white_anim2);
		final Animation whiteAnim3 = AnimationUtils.loadAnimation(mContext, R.anim.white_anim3);
		final Animation whiteAnim4 = AnimationUtils.loadAnimation(mContext, R.anim.white_anim4);

		final Animation blackAnim4 = AnimationUtils.loadAnimation(mContext, R.anim.black_anim4);
		final Animation blackAnim5 = AnimationUtils.loadAnimation(mContext, R.anim.black_anim5);
		final Animation blackAnim6 = AnimationUtils.loadAnimation(mContext, R.anim.black_anim6);
		final Animation blackAnim7 = AnimationUtils.loadAnimation(mContext, R.anim.black_anim4);
		final Animation blackAnim8 = AnimationUtils.loadAnimation(mContext, R.anim.black_anim5);

		Animation.AnimationListener animationListener = new Animation.AnimationListener() {
			final int black4Repetition = 1;
			int black4ShowTime = 0;

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (animation == whiteAnim1) {
					circleWhiteButton.startAnimation(whiteAnim2);
				} else if (animation == whiteAnim2) {
					circleWhiteButton.startAnimation(whiteAnim3);
				} else if (animation == whiteAnim3) {
					circleWhiteButton.startAnimation(whiteAnim4);
				} else if (animation == whiteAnim4) {
					circleBlackButton.startAnimation(blackAnim4);
				} else if (animation == blackAnim4) {
					circleBlackButton.startAnimation(blackAnim5);
				} else if (animation == blackAnim5) {
					circleBlackButton.startAnimation(blackAnim6);
				} else if (animation == blackAnim6) {
					circleBlackButton.startAnimation(blackAnim7);
				} else if (animation == blackAnim7) {
					circleBlackButton.startAnimation(blackAnim8);
				} else if (animation == blackAnim8) {
					circleWhiteButton.startAnimation(whiteAnim1);
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		};

		whiteAnim1.setAnimationListener(animationListener);
		whiteAnim2.setAnimationListener(animationListener);
		whiteAnim3.setAnimationListener(animationListener);
		whiteAnim4.setAnimationListener(animationListener);

		blackAnim4.setAnimationListener(animationListener);
		blackAnim5.setAnimationListener(animationListener);
		blackAnim6.setAnimationListener(animationListener);
		blackAnim7.setAnimationListener(animationListener);
		blackAnim8.setAnimationListener(animationListener);

		circleWhiteButton.startAnimation(whiteAnim1);
	}

	public AnimatedTagLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public AnimatedTagLayout(Context context, int i) {
		super(context);
		mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		circleWhiteButton = (ImageView) findViewById(R.id.animated_circle_button_white);

		circleBlackButton = (ImageView) findViewById(R.id.animated_circle_button_black);

		startAnimation();
	}
}
