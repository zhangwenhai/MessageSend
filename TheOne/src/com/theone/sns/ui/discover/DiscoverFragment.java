package com.theone.sns.ui.discover;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode.FindMBlogGalleryType;
import com.theone.sns.common.FusionCode.FindUserGalleryType;
import com.theone.sns.logic.adapter.db.FilterDbAdapter;
import com.theone.sns.ui.base.IphoneTitleFragment;
import com.theone.sns.ui.mblog.FragmentsPagerAdapter;
import com.theone.sns.ui.mblog.activity.HeadmodelFragment;
import com.theone.sns.util.uiwidget.viewpagerindicator.UnderlinePageIndicator;

public class DiscoverFragment extends IphoneTitleFragment {
	private static final int PAGE_HOT = 0;
	private static final int PAGE_NEW = 1;
	private static final int PAGE_NEARBY = 2;
	private TextView mHotText;
	private TextView mNewText;
	private TextView mNearbtText;
	private ViewPager mViewPager;
	private int mCurrentPage;
	private ArrayList<Fragment> pages;
	private ArrayList<Fragment> pages1;
	private FragmentsPagerAdapter mHeadmodelPagersAdapter;
	private boolean isHeadModel = false;
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (FusionAction.TheOneApp.HEAD_MODEL.equals(intent.getAction())) {
				if (FusionAction.TheOneApp.HEADMODEL.equals(intent
						.getStringExtra(FusionAction.TheOneApp.HEAD_MODEL))
						&& !isHeadModel
						&& null != mViewPager) {
					pages.clear();
					HeadmodelFragment nearby = new HeadmodelFragment();
					nearby.setType(FindUserGalleryType.HOT_USER);
					pages.add(nearby);
					HeadmodelFragment homepage = new HeadmodelFragment();
					homepage.setType(FindUserGalleryType.NEW_USER);
					pages.add(homepage);
					HeadmodelFragment follow = new HeadmodelFragment();
					follow.setType(FindUserGalleryType.NEAR_USER);
					pages.add(follow);
					mHeadmodelPagersAdapter.setFragments(pages);
					isHeadModel = true;
				} else if (FusionAction.TheOneApp.MBLOGMODEL.equals(intent
						.getStringExtra(FusionAction.TheOneApp.HEAD_MODEL))
						&& isHeadModel
						&& null != mViewPager) {
					pages1.clear();
					DisHeadmodelFragment hot = new DisHeadmodelFragment();
					hot.setType(FindMBlogGalleryType.HOT_MBLOG);
					pages1.add(hot);
					DisHeadmodelFragment NEW = new DisHeadmodelFragment();
					NEW.setType(FindMBlogGalleryType.NEW_MBLOG);
					pages1.add(NEW);
					DisHeadmodelFragment nearby1 = new DisHeadmodelFragment();
					nearby1.setType(FindMBlogGalleryType.NEAR_MBLOG);
					pages1.add(nearby1);
					mHeadmodelPagersAdapter.setFragments(pages1);
					isHeadModel = false;
				}
			}
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

	private final ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageSelected(int page) {
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

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.context = (FragmentActivity) activity;
	}

	@Override
	protected void initLogics() {
	}

	@Override
	protected void onMyCreateView() {
		setSubContent(R.layout.discover_page);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (isNew) {
			setTitle(R.string.discover);
			setRightButton(R.drawable.search_add_friends_icon, false);

			initSearch(view);
			initTopbar(view);
			initContentPage(view);

			getRightButton().setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					startActivity(new Intent(FusionAction.MBlogAction.ADDFRIEND_ACTION));
				}
			});

			getLeftButton().setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					startActivity(new Intent(FusionAction.DiscoverAction.CONDITION_ACTION));
				}
			});

			IntentFilter filter = new IntentFilter();
			filter.addAction(FusionAction.TheOneApp.HEAD_MODEL);
			LocalBroadcastManager.getInstance(context).registerReceiver(mBroadcastReceiver, filter);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (FilterDbAdapter.getInstance().getFilter().enabled) {
			setLeftButton(R.drawable.navigation_selected_btn, false, false);
		} else {
			setLeftButton(R.drawable.navigation_select_btn, false, false);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		LocalBroadcastManager.getInstance(context).unregisterReceiver(mBroadcastReceiver);
	}

	private void initSearch(View view) {
		view.findViewById(R.id.search_box_view).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(FusionAction.DiscoverAction.DISCOVER_SEARCH_ACTION));
			}
		});
		view.findViewById(R.id.search_box).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(FusionAction.DiscoverAction.DISCOVER_SEARCH_ACTION));
			}
		});
	}

	private void initTopbar(View view) {
		mHotText = (TextView) view.findViewById(R.id.topbar_left);
		mNewText = (TextView) view.findViewById(R.id.topbar_mon);
		mNearbtText = (TextView) view.findViewById(R.id.topbar_right);

		view.findViewById(R.id.topbar_left_button).setOnClickListener(
				new TopBarClickListener(PAGE_HOT));
		view.findViewById(R.id.topbar_mon_button).setOnClickListener(
				new TopBarClickListener(PAGE_NEW));
		view.findViewById(R.id.topbar_right_button).setOnClickListener(
				new TopBarClickListener(PAGE_NEARBY));

		changeTopBar();
	}

	private void changeTopBar() {
		mHotText.setTextColor(getResources().getColor(R.color.forward_title_normal));
		mNewText.setTextColor(getResources().getColor(R.color.forward_title_normal));
		mNearbtText.setTextColor(getResources().getColor(R.color.forward_title_normal));

		switch (mCurrentPage) {
		case PAGE_HOT: {
			mHotText.setTextColor(getResources().getColor(R.color.forward_title_select));
			break;
		}
		case PAGE_NEW: {
			mNewText.setTextColor(getResources().getColor(R.color.forward_title_select));
			break;
		}
		case PAGE_NEARBY: {
			mNearbtText.setTextColor(getResources().getColor(R.color.forward_title_select));
		}
		default: {
			break;
		}
		}
	}

	private void initContentPage(View view) {
		mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
		pages = new ArrayList<Fragment>();

		pages = new ArrayList<Fragment>();
		pages1 = new ArrayList<Fragment>();
		HeadmodelFragment nearby = new HeadmodelFragment();
		nearby.setType(FindUserGalleryType.HOT_USER);
		pages.add(nearby);
		HeadmodelFragment homepage = new HeadmodelFragment();
		homepage.setType(FindUserGalleryType.NEW_USER);
		pages.add(homepage);
		HeadmodelFragment follow = new HeadmodelFragment();
		follow.setType(FindUserGalleryType.NEAR_USER);
		pages.add(follow);

		DisHeadmodelFragment hot = new DisHeadmodelFragment();
		hot.setType(FindMBlogGalleryType.HOT_MBLOG);
		pages1.add(hot);
		DisHeadmodelFragment NEW = new DisHeadmodelFragment();
		NEW.setType(FindMBlogGalleryType.NEW_MBLOG);
		pages1.add(NEW);
		DisHeadmodelFragment nearby1 = new DisHeadmodelFragment();
		nearby1.setType(FindMBlogGalleryType.NEAR_MBLOG);
		pages1.add(nearby1);

		if (FilterDbAdapter.getInstance().getFilter().enabled
				&& FilterDbAdapter.getInstance().getFilter().avatar_mode_enabled) {
			mHeadmodelPagersAdapter = new FragmentsPagerAdapter(getChildFragmentManager(), pages);
			mViewPager.setAdapter(mHeadmodelPagersAdapter);
			isHeadModel = true;
		} else {
			mHeadmodelPagersAdapter = new FragmentsPagerAdapter(getChildFragmentManager(), pages1);
			mViewPager.setAdapter(mHeadmodelPagersAdapter);
			isHeadModel = false;
		}
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setCurrentItem(PAGE_HOT);
		UnderlinePageIndicator indicator = (UnderlinePageIndicator) view
				.findViewById(R.id.indicator);
		indicator.setViewPager(mViewPager);
		indicator.setFades(false);
		indicator.setOnPageChangeListener(mPageChangeListener);
	}
}
