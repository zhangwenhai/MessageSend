package com.theone.sns.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.ui.base.BasicActivity;

/**
 * Created by zhangwenhai on 2014/11/17.
 */
public class WelcomeActivity extends BasicActivity {

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			startActivity(new Intent(FusionAction.LoginAction.LEAD_ACTION));
			finish();
		}
	};

	@Override
	protected void initLogics() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_main);

		ImageLoader.getInstance().clearDiscCache();
		ImageLoader.getInstance().clearMemoryCache();

		if (FusionConfig.isLogin()) {

			startActivity(new Intent(FusionAction.LoginAction.LEAD_ACTION));

			finish();
		}

		overridePendingTransition(R.anim.welcome_start_in,
				R.anim.welcome_start_out);

		myHandler.sendEmptyMessageDelayed(0, 1500);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.welcome_end_in, R.anim.welcome_end_out);
	}
}
