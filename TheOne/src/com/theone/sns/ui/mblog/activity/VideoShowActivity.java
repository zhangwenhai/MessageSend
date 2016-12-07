package com.theone.sns.ui.mblog.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.framework.ui.BaseActivity;
import com.theone.sns.ui.base.BasicActivity;
import com.theone.sns.util.RoundProgressBar;

/**
 * Created by zhangwenhai on 2014/10/18.
 */
public class VideoShowActivity extends BasicActivity {
	private VideoView mVideoView;
	private RoundProgressBar soundPlaybar;
	private ImageView videoView;
	private String path;
	private int time;
	private int with;
	private RelativeLayout videoRela;

	@Override
	protected void initLogics() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_pop);

		getView();

		path = getIntent().getStringExtra(FusionAction.MBlogAction.VIDEO_PATH);
		if (TextUtils.isEmpty(path)) {
			finish();
		}
		time = getIntent().getIntExtra(FusionAction.MBlogAction.VIDEO_TIME, 0);
		with = getResources().getDisplayMetrics().widthPixels;

//		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.FILL_PARENT, with);
//		lp.gravity = Gravity.CENTER;
//		videoRela.setLayoutParams(lp);

		videoView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mVideoView.isPlaying()) {
					mVideoView.pause();
					soundPlaybar.stopCartoom();
					videoView.setImageDrawable(getResources().getDrawable(
							R.drawable.home_video_play_icon));
					return;
				}
				mVideoView.setVideoURI(Uri.parse(path));
				mVideoView.start();
				mVideoView.requestFocus();
				soundPlaybar.startCartoom(0, time);
				videoView.setImageDrawable(getResources().getDrawable(
						R.drawable.home_video_stop_icon));
			}
		});

		findViewById(R.id.other_view).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {
				finish();
			}
		});
	}

	private void getView() {
		mVideoView = (VideoView) findViewById(R.id.video);
		soundPlaybar = (RoundProgressBar) findViewById(R.id.sound_playbar);
		videoView = (ImageView) findViewById(R.id.video_view);
		videoRela = (RelativeLayout) findViewById(R.id.video_rela);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mVideoView.setVideoURI(Uri.parse(path));
		mVideoView.start();
		mVideoView.requestFocus();
		soundPlaybar.startCartoom(0, time);
		videoView.setImageDrawable(getResources().getDrawable(R.drawable.home_video_stop_icon));
	}
}
