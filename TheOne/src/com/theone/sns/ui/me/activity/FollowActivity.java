package com.theone.sns.ui.me.activity;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.util.uiwidget.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/11/15.
 */
public class FollowActivity extends IphoneTitleActivity {
	private static final int PAGE_FOLLOW = 0;
	private static final int PAGE_STARD = 1;
	private LocalActivityManager manager;
	private TextView mFollowText;
	private TextView mStardText;
	private ViewPager mViewPager;
	private int mCurrentPage;
	private List<View> list = new ArrayList<View>();
	private MyPagerAdapter mMyPagerAdapter;
	private String uid;

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
	protected void initLogics() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.follow_mian);

		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);

		getView();

		setView();

		setListener();
	}

	private void getView() {
		mFollowText = (TextView) findViewById(R.id.topbar_left);
		mStardText = (TextView) findViewById(R.id.topbar_right);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
	}

	private void setView() {
		setTitle(R.string.follow);
		setLeftButton(R.drawable.icon_back, false, false);
		changeTopBar();

		uid = getIntent().getStringExtra(FusionAction.MeAction.UID);

		Intent intent = new Intent(this, FollowListActivity.class);
		if (!TextUtils.isEmpty(uid)) {
			intent.putExtra(FusionAction.MeAction.UID, uid);
		}
		list.add(getView("1", intent));
		Intent intent2 = new Intent(this, StarringListActivity.class);
		if (!TextUtils.isEmpty(uid)) {
			intent2.putExtra(FusionAction.MeAction.UID, uid);
		}
		list.add(getView("2", intent2));
		mMyPagerAdapter = new MyPagerAdapter(list);
		mViewPager.setAdapter(mMyPagerAdapter);
		mViewPager.setOffscreenPageLimit(2);
		mViewPager.setCurrentItem(PAGE_FOLLOW);
		UnderlinePageIndicator indicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(mViewPager);
		indicator.setFades(false);
		indicator.setOnPageChangeListener(mPageChangeListener);
	}

	private void setListener() {
		findViewById(R.id.topbar_left_button).setOnClickListener(
				new TopBarClickListener(PAGE_FOLLOW));
		findViewById(R.id.topbar_right_button).setOnClickListener(
				new TopBarClickListener(PAGE_STARD));
	}

	/**
	 * 通过activity获取视图
	 *
	 * @param id
	 * @param intent
	 * @return
	 */
	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}

	private void changeTopBar() {
		mFollowText.setTextColor(getResources().getColor(R.color.forward_title_normal));
		mStardText.setTextColor(getResources().getColor(R.color.forward_title_normal));

		switch (mCurrentPage) {
		case PAGE_FOLLOW: {
			mFollowText.setTextColor(getResources().getColor(R.color.forward_title_select));
			break;
		}
		case PAGE_STARD: {
			mStardText.setTextColor(getResources().getColor(R.color.forward_title_select));
			break;
		}
		default: {
			break;
		}
		}
	}

	private class MyPagerAdapter extends PagerAdapter {
		List<View> list = new ArrayList<View>();

		public MyPagerAdapter(List<View> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object o) {
			return view == o;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ViewPager pViewPager = ((ViewPager) container);
			pViewPager.addView(list.get(position));
			return list.get(position);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		public void setList(List<View> list) {
			this.list = list;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}
}
