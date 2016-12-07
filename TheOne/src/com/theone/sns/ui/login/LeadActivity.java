package com.theone.sns.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionAction.RegisterAction;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.ui.base.BasicActivity;
import com.theone.sns.util.uiwidget.viewpagerindicator.CirclePageIndicator;

/**
 * Created by zhangwenhai on 14-8-30.
 */
public class LeadActivity extends BasicActivity {

	private static final String TAG = "LeadActivity";

	private static final int LOGINSUCCESSREQUESTCODE = 1;

	private static final int PHONESIGNUPSUCCESSREQUESTCODE = 2;

	private TextView phoneSignup;
	private TextView login;
	private ViewPager viewPager;
	private CirclePageIndicator m_titleIndicator;
	private int[] image = new int[] { R.drawable.theone_defult1,
			R.drawable.theone_defult2, R.drawable.theone_defult3,
			R.drawable.theone_defult4, R.drawable.theone_defult5, };

	@Override
	protected void initLogics() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lead_main);

		ImageLoader.getInstance().clearMemoryCache();
		ImageLoader.getInstance().clearDiscCache();

		getView();

		initView();

		setListener();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (FusionConfig.isLogin()) {

			startActivity(new Intent(RegisterAction.MAIN_ACTION));

			finish();
		}
	}

	private void getView() {
		phoneSignup = (TextView) findViewById(R.id.phone_signup);
		login = (TextView) findViewById(R.id.login);
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		m_titleIndicator = (CirclePageIndicator) findViewById(R.id.emoji_indicator);
	}

	private void initView() {

		viewPager.setAdapter(new ViewPagerAdapter());
		viewPager.setVerticalFadingEdgeEnabled(false);
		viewPager.setVerticalScrollBarEnabled(false);
		m_titleIndicator.setPageColor(0xff888888);
		m_titleIndicator.setFillColor(0xff646464);
		m_titleIndicator.setViewPager(viewPager);
		m_titleIndicator.setCurrentItem(0);
	}

	private void setListener() {

		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				startActivityForResult(new Intent(
						FusionAction.LoginAction.LOGIN_ACTION),
						LOGINSUCCESSREQUESTCODE);
			}
		});

		phoneSignup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				startActivityForResult(new Intent(
						RegisterAction.REGISTER_INVITE_CODE_ACTION),
						PHONESIGNUPSUCCESSREQUESTCODE);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (RESULT_OK == resultCode) {

			if (requestCode == LOGINSUCCESSREQUESTCODE) {

				startActivity(new Intent(RegisterAction.MAIN_ACTION));

			} else if (requestCode == PHONESIGNUPSUCCESSREQUESTCODE) {

				startActivity(new Intent(
						FusionAction.RegisterAction.REGISTERMANIFESTO_ACTION));
			}

			finish();
		}
	}

	class ViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return image.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object o) {
			return view == ((View) o);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View ret = LayoutInflater.from(LeadActivity.this).inflate(
					R.layout.view_item, null);
			ImageView mImageView = (ImageView) ret
					.findViewById(R.id.image_view);
			try {

				mImageView.setImageDrawable(getResources().getDrawable(
						image[position]));
			} catch (OutOfMemoryError e) {
				Log.e(TAG, "setImageDrawable OutOfMemoryError error");
				Process.killProcess(Process.myPid());
			}
			((ViewPager) container).addView(ret);
			return ret;
		}

		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
			object = null;
		}
	}
}
