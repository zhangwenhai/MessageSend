package com.theone.sns.ui.mblog.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.ui.base.IphoneTitleFragment;

public class MBlogFragment extends IphoneTitleFragment {

	private static final int PAGE_NEARBY = 0;

	private static final int PAGE_HOME = 1;

	private static final int PAGE_FOLLOW = 2;

	private TextView mNearbyText;
	private TextView mHomeText;
	private TextView mFollowText;
	private int mCurrentPage;
	private ViewPager mViewPager;
//	private FragmentActivity context;
//	private IMBlogLogic mIMBlogLogic;
//	private FragmentsPagerAdapter mHeadmodelPagersAdapter;
//	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (FusionAction.TheOneApp.HEAD_MODEL.equals(intent.getAction())) {
//				if (FusionAction.TheOneApp.HEADMODEL.equals(intent
//						.getStringExtra(FusionAction.TheOneApp.HEAD_MODEL))
//						&& !isHeadModel
//						&& null != mViewPager) {
//					pages.clear();
//					HeadmodelFragment nearby = new HeadmodelFragment();
//					nearby.setType(FusionCode.MBlogListType.NEAR);
//					pages.add(nearby);
//					HeadmodelFragment homepage = new HeadmodelFragment();
//					homepage.setType(FusionCode.MBlogListType.HOME);
//					pages.add(homepage);
//					HeadmodelFragment follow = new HeadmodelFragment();
//					follow.setType(FusionCode.MBlogListType.FOLLOWING);
//					pages.add(follow);
//					mHeadmodelPagersAdapter.setFragments(pages);
//					isHeadModel = true;
//				} else if (FusionAction.TheOneApp.MBLOGMODEL.equals(intent
//						.getStringExtra(FusionAction.TheOneApp.HEAD_MODEL))
//						&& isHeadModel
//						&& null != mViewPager) {
//					pages1.clear();
//					MblogmodelFragment nearby1 = new MblogmodelFragment();
//					nearby1.setType(FusionCode.MBlogListType.NEAR);
//					pages1.add(nearby1);
//					MblogmodelFragment homepage1 = new MblogmodelFragment();
//					homepage1.setType(FusionCode.MBlogListType.HOME);
//					pages1.add(homepage1);
//					MblogmodelFragment follow1 = new MblogmodelFragment();
//					follow1.setType(FusionCode.MBlogListType.FOLLOWING);
//					pages1.add(follow1);
//					mHeadmodelPagersAdapter.setFragments(pages1);
//					isHeadModel = false;
//				}
//			}
//		}
//	};
//	private boolean isHeadModel = false;
//	private ArrayList<Fragment> pages;
//	private ArrayList<Fragment> pages1;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.context = (FragmentActivity) activity;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (isNew) {
			setTitleImage(R.drawable.navigation_bar_tittle);
			setRightButton(R.drawable.navigation_add_friends_btn, false);
			// setLeftButton(R.drawable.navigation_select_btn, false, false);
			m_titleLayout.setBackgroundColor(Color.parseColor("#FFD800"));

			setView();

			initTopbar(view);
//			initContentPage(view);

			getRightButton().setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					startActivity(new Intent(FusionAction.MBlogAction.ADDFRIEND_ACTION));
				}
			});

//			IntentFilter filter = new IntentFilter();
//			filter.addAction(FusionAction.TheOneApp.HEAD_MODEL);
//			LocalBroadcastManager.getInstance(context).registerReceiver(mBroadcastReceiver, filter);
		}
	}

	@Override
	protected void onMyCreateView() {
		setSubContent(R.layout.mblog_page);
	}

	private void setView() {
		// getLeftButton().setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View view) {
		// startActivity(new
		// Intent(FusionAction.DiscoverAction.CONDITION_ACTION));
		// }
		// });
	}

	private void initTopbar(View view) {
		mNearbyText = (TextView) view.findViewById(R.id.topbar_left);
		mHomeText = (TextView) view.findViewById(R.id.topbar_center);
		mFollowText = (TextView) view.findViewById(R.id.topbar_right);

		view.findViewById(R.id.topbar_left_button).setOnClickListener(
				new TopBarClickListener(PAGE_NEARBY));
		view.findViewById(R.id.topbar_center_button).setOnClickListener(
				new TopBarClickListener(PAGE_HOME));
		view.findViewById(R.id.topbar_right_button).setOnClickListener(
				new TopBarClickListener(PAGE_FOLLOW));

		changeTopBar();
	}

	private void changeTopBar() {
		mNearbyText.setTextColor(getResources().getColor(R.color.forward_title_normal));
		mHomeText.setTextColor(getResources().getColor(R.color.forward_title_normal));
		mFollowText.setTextColor(getResources().getColor(R.color.forward_title_normal));

		switch (mCurrentPage) {
		case PAGE_NEARBY: {
			mNearbyText.setTextColor(getResources().getColor(R.color.forward_title_select));
			break;
		}
		case PAGE_HOME: {
			mHomeText.setTextColor(getResources().getColor(R.color.forward_title_select));
			break;
		}
		case PAGE_FOLLOW: {
			mFollowText.setTextColor(getResources().getColor(R.color.forward_title_select));
			break;
		}
		default: {
			break;
		}
		}
	}

//	private void initContentPage(View view) {
//		mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
//		pages = new ArrayList<Fragment>();
//		pages1 = new ArrayList<Fragment>();
//		// if (FilterDbAdapter.getInstance().getFilter().enabled
//		// && FilterDbAdapter.getInstance().getFilter().avatar_mode_enabled) {
//		HeadmodelFragment nearby = new HeadmodelFragment();
//		nearby.setType(FusionCode.MBlogListType.NEAR);
//		pages.add(nearby);
//		HeadmodelFragment homepage = new HeadmodelFragment();
//		homepage.setType(FusionCode.MBlogListType.HOME);
//		pages.add(homepage);
//		HeadmodelFragment follow = new HeadmodelFragment();
//		follow.setType(FusionCode.MBlogListType.FOLLOWING);
//		pages.add(follow);
//		MblogmodelFragment nearby1 = new MblogmodelFragment();
//		nearby1.setType(FusionCode.MBlogListType.NEAR);
//		pages1.add(nearby1);
//		MblogmodelFragment homepage1 = new MblogmodelFragment();
//		homepage1.setType(FusionCode.MBlogListType.HOME);
//		pages1.add(homepage1);
//		MblogmodelFragment follow1 = new MblogmodelFragment();
//		follow1.setType(FusionCode.MBlogListType.FOLLOWING);
//		pages1.add(follow1);
//		if (FilterDbAdapter.getInstance().getFilter().enabled
//				&& FilterDbAdapter.getInstance().getFilter().avatar_mode_enabled) {
//			mHeadmodelPagersAdapter = new FragmentsPagerAdapter(getChildFragmentManager(), pages);
//			mViewPager.setAdapter(mHeadmodelPagersAdapter);
//			isHeadModel = true;
//		} else {
//			mHeadmodelPagersAdapter = new FragmentsPagerAdapter(getChildFragmentManager(), pages1);
//			mViewPager.setAdapter(mHeadmodelPagersAdapter);
//			isHeadModel = false;
//		}
//		mViewPager.setOffscreenPageLimit(3);
//		mViewPager.setCurrentItem(PAGE_NEARBY);
//
//		UnderlinePageIndicator indicator = (UnderlinePageIndicator) view
//				.findViewById(R.id.indicator);
//		indicator.setViewPager(mViewPager);
//		indicator.setFades(false);
//		indicator.setOnPageChangeListener(mPageChangeListener);
//	}

	@Override
	public void onDestroy() {
		super.onDestroy();

//		LocalBroadcastManager.getInstance(context).unregisterReceiver(mBroadcastReceiver);
	}

	private final ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageSelected(int page) {
			// CocoBaseActivity.hideIME(mViewPager);
			mCurrentPage = page;
			changeTopBar();
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	private final class TopBarClickListener implements View.OnClickListener {

		private int mPage;

		public TopBarClickListener(int page) {
			mPage = page;
		}

		@Override
		public void onClick(View v) {
			mViewPager.setCurrentItem(mPage);
		}
	}

	@Override
	protected void initLogics() {
//		mIMBlogLogic = (IMBlogLogic) getLogicByInterfaceClass(IMBlogLogic.class);
	}

	@Override
	protected void handleStateMessage(Message msg) {

	}
}
