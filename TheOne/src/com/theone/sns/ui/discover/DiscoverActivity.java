package com.theone.sns.ui.discover;

import java.util.ArrayList;
import java.util.List;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.logic.mblog.IMBlogLogic;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.util.uiwidget.viewpagerindicator.UnderlinePageIndicator;

/**
 * Created by zhangwenhai on 2014/10/21.
 */
public class DiscoverActivity extends IphoneTitleActivity {
	private static final int PAGE_LABEL = 0;
	private static final int PAGE_USER = 1;
	private EditText searchBox;
	private TextView mlabelText;
	private TextView mUserText;
	private ViewPager mViewPager;
	public int mCurrentPage;
	private MyPagerAdapter mMyPagerAdapter;
	private LocalActivityManager manager;
	private IMBlogLogic mIMBlogLogic;
	private ArrayList<View> list = new ArrayList<View>();

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
		mIMBlogLogic = (IMBlogLogic) getLogicByInterfaceClass(IMBlogLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.discover_search_main);
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_behind);

		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);

		getView();

		setView();
	}

	private void getView() {
		searchBox = (EditText) findViewById(R.id.search_box);
		mlabelText = (TextView) findViewById(R.id.topbar_left);
		mUserText = (TextView) findViewById(R.id.topbar_right);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);

		findViewById(R.id.topbar_left_button).setOnClickListener(
				new TopBarClickListener(PAGE_LABEL));
		findViewById(R.id.topbar_right_button).setOnClickListener(
				new TopBarClickListener(PAGE_USER));
		changeTopBar();
	}

	private void changeTopBar() {
		mlabelText.setTextColor(getResources().getColor(R.color.forward_title_normal));
		mUserText.setTextColor(getResources().getColor(R.color.forward_title_normal));

		switch (mCurrentPage) {
		case PAGE_LABEL: {
			mlabelText.setTextColor(getResources().getColor(R.color.forward_title_select));
			break;
		}
		case PAGE_USER: {
			mUserText.setTextColor(getResources().getColor(R.color.forward_title_select));
			break;
		}
		default: {
			break;
		}
		}
	}

	private void setView() {
		setTitle(R.string.discover);
		setLeftButton(R.drawable.icon_back, false, false);
		searchBox.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (!TextUtils.isEmpty(editable)) {
					ArrayList<View> list = new ArrayList<View>();
					Intent intent = new Intent(DiscoverActivity.this,
							DiscoverGridViewActivity.class);
					intent.putExtra(FusionAction.DiscoverAction.SEARCH_WORD, editable.toString());
					list.add(getView(editable.toString() + "1", intent));
					Intent intent2 = new Intent(DiscoverActivity.this,
							DiscoverListViewActivity.class);
					intent2.putExtra(FusionAction.DiscoverAction.SEARCH_WORD, editable.toString());
					list.add(getView(editable.toString() + "2", intent2));
					mMyPagerAdapter.setList(list);
					mMyPagerAdapter.notifyDataSetChanged();
				}
			}
		});

		mMyPagerAdapter = new MyPagerAdapter(list);
		mViewPager.setAdapter(mMyPagerAdapter);
		mViewPager.setOffscreenPageLimit(2);
		mViewPager.setCurrentItem(PAGE_LABEL);
		UnderlinePageIndicator indicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(mViewPager);
		indicator.setFades(false);
		indicator.setOnPageChangeListener(mPageChangeListener);
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

	@Override
	public void finish() {
		super.finish();
		hideInputWindow(searchBox);
		overridePendingTransition(R.anim.push_down_behind, R.anim.push_down);
	}

	private class MyPagerAdapter extends PagerAdapter {
		List<View> list = new ArrayList<View>();

		public MyPagerAdapter(ArrayList<View> list) {
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
