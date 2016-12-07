package com.theone.sns.ui.me.activity;

import net.hockeyapp.android.FeedbackManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.ui.base.IphoneTitleActivity;

/**
 * Created by zhangwenhai on 2014/11/11.
 */
public class AboutActivity extends IphoneTitleActivity {
	private TextView mVersion;
	private TextView mVersion1;

	@Override
	protected void initLogics() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.about_main);

		getView();

		setView();

		setListener();
	}

	private void getView() {
		mVersion = (TextView) findViewById(R.id.version);
		mVersion1 = (TextView) findViewById(R.id.version1);
	}

	private void setView() {
		setTitle(R.string.setting_about);
		setLeftButton(R.drawable.icon_back, false, false);

		try {
			PackageManager pm = getPackageManager();
			PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
			mVersion1.setText(pi.versionName);
			mVersion.setText(pi.versionName);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void setListener() {
		findViewById(R.id.recommend_view).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(FusionAction.MBlogAction.ADDFRIEND_ACTION));
			}
		});

		findViewById(R.id.feedback_view).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FeedbackManager.showFeedbackActivity(AboutActivity.this);
			}
		});

	}

}
