package com.theone.sns.ui.chat.activity;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.TheOneApp;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.model.chat.CreateGroup;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.logic.model.chat.base.NameCard;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.user.IUserLogic;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.base.dialog.TheOneAlertDialog;
import com.theone.sns.ui.chat.ChatSelectFriendAdapter;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshBase;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshListView;
import com.theone.sns.util.uiwidget.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/10/29.
 */
public class SelectFriendActivity extends IphoneTitleActivity {
	private static final int PAGE_FOLLOW = 0;
	private static final int PAGE_STARED = 1;
	private static final int PAGE_FOLLOWEDBY = 2;
	private LocalActivityManager manager;
	private EditText searchBox;
	private TextView mfollowText;
	private TextView mstaredText;
	private TextView mfollowedByText;
	private ViewPager mViewPager;
	public int mCurrentPage;
	private List<View> list = new ArrayList<View>();
	private MyPagerAdapter mMyPagerAdapter;
	private GroupInfo mGroupInfo;
	public static List<User> addlist = new ArrayList<User>();
	private IChatLogic mIChatLogic;
	private ListView selectListView;
	private LinearLayout tabView;
	private IUserLogic mIUserLogic;
	private String getCircleSearchId;
	private List<User> mUserList = new ArrayList<User>();
	private ChatSelectFriendAdapter mChatSelectFriendAdapter;
	private List<String> mMember = new ArrayList<String>();
	private LayoutInflater inflater;
	private PhotoGridViewAdapter mPhotoGridViewAdapter;
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (FusionAction.ChatAction.UPDATE_VIEW.equals(intent.getAction())) {
				if (null != mChatSelectFriendAdapter) {
					mChatSelectFriendAdapter.notifyDataSetChanged();
					searchBox.setText("");
					if (addlist.size() > 0) {
						mHorizontalScrollView.setVisibility(View.VISIBLE);
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
								(int) ((HelperFunc.dip2px(50)) * addlist.size()
										+ (addlist.size() - 1) * (int) HelperFunc.dip2px(2) + (int) HelperFunc
										.dip2px(10)), (int) HelperFunc.dip2px(60));
						mphotoGridView.setPadding((int) HelperFunc.dip2px(5),
								(int) HelperFunc.dip2px(5), (int) HelperFunc.dip2px(5),
								(int) HelperFunc.dip2px(5));
						mphotoGridView.setLayoutParams(lp);

						if (null == mPhotoGridViewAdapter) {
							mPhotoGridViewAdapter = new PhotoGridViewAdapter();
							mphotoGridView.setAdapter(mPhotoGridViewAdapter);
						}
						mPhotoGridViewAdapter.notifyDataSetChanged();
						post(new Runnable() {
							@Override
							public void run() {
								mHorizontalScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
							}
						});
					} else {
						mHorizontalScrollView.setVisibility(View.GONE);
					}
				}
			}
		}
	};
	private PullToRefreshListView mPullToRefreshListView;
	private HorizontalScrollView mHorizontalScrollView;
	private GridView mphotoGridView;

	// private LinearLayout linear;

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
		mIChatLogic = (IChatLogic) getLogicByInterfaceClass(IChatLogic.class);
		mIUserLogic = (IUserLogic) getLogicByInterfaceClass(IUserLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.select_friend_main);
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_behind);

		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);

		if (null == inflater) {
			inflater = LayoutInflater.from(getApplicationContext());
		}

		getView();

		setView();

		setListener();

		IntentFilter filter = new IntentFilter();
		filter.addAction(FusionAction.ChatAction.UPDATE_VIEW);
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(RESULT_CANCELED);
		if (searchBox == null) {
			return;
		}
		InputMethodManager imm = (InputMethodManager) TheOneApp.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
	}

	private void getView() {
		mGroupInfo = (GroupInfo) getIntent().getSerializableExtra(
				FusionAction.ChatAction.GROUP_INFO);
		if (null == mGroupInfo) {
			finish();
		}
		addlist.clear();
		searchBox = (EditText) findViewById(R.id.search_box);
		mfollowText = (TextView) findViewById(R.id.topbar_left);
		mstaredText = (TextView) findViewById(R.id.topbar_middle);
		mfollowedByText = (TextView) findViewById(R.id.topbar_right);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.select_list_view);
		selectListView = mPullToRefreshListView.getRefreshableView();
		tabView = (LinearLayout) findViewById(R.id.tab_view);

		mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalscrollview);
		mphotoGridView = (GridView) findViewById(R.id.photo_gridView);
		// linear = (LinearLayout) findViewById(R.id.linear);

	}

	private void setView() {
		findViewById(R.id.topbar_left_button).setOnClickListener(
				new TopBarClickListener(PAGE_FOLLOW));
		findViewById(R.id.topbar_middle_button).setOnClickListener(
				new TopBarClickListener(PAGE_STARED));
		findViewById(R.id.topbar_right_button).setOnClickListener(
				new TopBarClickListener(PAGE_FOLLOWEDBY));
		changeTopBar();

		setTitle(R.string.start_chat);
		if (ChatSelectFriendAdapter.SEND_NAMECARD == getIntent().getIntExtra(
				FusionAction.ChatAction.SELECT_TYPE_1, -1)) {
			setTitle(R.string.select_friend);
		} else {
			setTitle(R.string.start_chat);
		}
		setLeftButton(R.drawable.icon_back, false, false);
		setRightButton(R.string.confirm, true);

		Intent intent = new Intent(SelectFriendActivity.this, SelectListViewActivity.class);
		intent.putExtra(FusionAction.ChatAction.SELECT_TYPE, PAGE_FOLLOW);
		intent.putExtra(FusionAction.ChatAction.GROUP_INFO, mGroupInfo);
		intent.putExtra(FusionAction.ChatAction.SELECT_TYPE_1,
				getIntent().getIntExtra(FusionAction.ChatAction.SELECT_TYPE_1, -1));
		list.add(getView("1", intent));
		Intent intent2 = new Intent(SelectFriendActivity.this, SelectListViewActivity.class);
		intent2.putExtra(FusionAction.ChatAction.SELECT_TYPE, PAGE_STARED);
		intent2.putExtra(FusionAction.ChatAction.GROUP_INFO, mGroupInfo);
		intent2.putExtra(FusionAction.ChatAction.SELECT_TYPE_1,
				getIntent().getIntExtra(FusionAction.ChatAction.SELECT_TYPE_1, -1));
		list.add(getView("2", intent2));
		Intent intent3 = new Intent(SelectFriendActivity.this, SelectListViewActivity.class);
		intent3.putExtra(FusionAction.ChatAction.SELECT_TYPE, PAGE_FOLLOWEDBY);
		intent3.putExtra(FusionAction.ChatAction.GROUP_INFO, mGroupInfo);
		intent3.putExtra(FusionAction.ChatAction.SELECT_TYPE_1,
				getIntent().getIntExtra(FusionAction.ChatAction.SELECT_TYPE_1, -1));
		list.add(getView("3", intent3));

		mMyPagerAdapter = new MyPagerAdapter(list);
		mViewPager.setAdapter(mMyPagerAdapter);
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setCurrentItem(PAGE_FOLLOW);
		UnderlinePageIndicator indicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(mViewPager);
		indicator.setFades(false);
		indicator.setOnPageChangeListener(mPageChangeListener);

		if (null == mGroupInfo || null == mGroupInfo.members) {
			finish();
		}

		for (User mUser : mGroupInfo.members) {
			if (null != mUser) {
				mMember.add(mUser.userId);
			}
		}
		mChatSelectFriendAdapter = new ChatSelectFriendAdapter(this, mMember, optionsForUserIcon,
				getIntent().getIntExtra(FusionAction.ChatAction.SELECT_TYPE_1, -1));
		selectListView.setAdapter(mChatSelectFriendAdapter);
	}

	private void setListener() {
		getRightButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (addlist.size() != 0) {
					if (ChatSelectFriendAdapter.SELECT_FRIEND == getIntent().getIntExtra(
							FusionAction.ChatAction.SELECT_TYPE_1, -1)) {
						mIChatLogic.updateGroupMember(FusionCode.GroupMemberAction.INVITE,
								mGroupInfo._id, addlist);
						setResult(Activity.RESULT_OK);
						if (null != searchBox) {
							InputMethodManager imm = (InputMethodManager) TheOneApp.getContext()
									.getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
						}
						finish();
						return;
					} else if (ChatSelectFriendAdapter.SEND_NAMECARD == getIntent().getIntExtra(
							FusionAction.ChatAction.SELECT_TYPE_1, -1)) {
						new TheOneAlertDialog.Builder(SelectFriendActivity.this)
								.setMessage(R.string.confirmation_send_friend)
								.setPositiveButton(R.string.confirm,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
												for (User mUser : addlist) {
													if (null == mUser) {
														continue;
													}
													MessageInfo mMessageInfo = new MessageInfo();
													mMessageInfo.messageType = FusionCode.MessageType.NAME_CARD;
													NameCard mNameCard = new NameCard();
													mNameCard._id = mUser.userId;
													mNameCard.avatar_url = mUser.avatar_url;
													mNameCard.location = mUser.location;
													mNameCard.name = mUser.name;
													mNameCard.role = mUser.role;
													mNameCard.marriage = mUser.marriage;
													mNameCard.region = mUser.region;
													mMessageInfo.name_card = mNameCard;
													mMessageInfo.recipient = mGroupInfo._id;
													mIChatLogic.sendMessage(mMessageInfo);
												}
												onBackPressed();
											}
										})
								.setNegativeButton(R.string.Cancel,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
											}
										}).show();
					} else if (ChatSelectFriendAdapter.CREAD_GROUP == getIntent().getIntExtra(
							FusionAction.ChatAction.SELECT_TYPE_1, -1)) {
						CreateGroup mCreateGroup = new CreateGroup();
						List<String> mList = new ArrayList<String>();
						for (User mUser : addlist) {
							if (null != mUser) {
								mList.add(mUser.userId);
							}
						}
						mCreateGroup.members = mList;
						String createGroupId = mIChatLogic.createGroup(mCreateGroup);
						setResult(RESULT_OK, new Intent().putExtra(
								FusionAction.ChatAction.CREATE_GROUP_ID, createGroupId));
						if (searchBox == null) {
							return;
						}
						InputMethodManager imm = (InputMethodManager) TheOneApp.getContext()
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
						finish();
						return;
					}
				}

			}
		});

		searchBox.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				String searchWord = searchBox.getText().toString().trim();
				if (TextUtils.isEmpty(searchWord)) {
					tabView.setVisibility(View.VISIBLE);
					mPullToRefreshListView.setVisibility(View.GONE);
				} else {
					tabView.setVisibility(View.GONE);
					getCircleSearchId = mIUserLogic.getCircleSearch(searchWord, null,
							FusionCode.CommonColumnsValue.COUNT_VALUE,
							FusionCode.Relationship.FOLLOWING);
				}
			}
		});

		getLeftButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				setResult(RESULT_CANCELED);
				onBackPressed();
			}
		});

		mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
			@Override
			public void onRefresh() {
				String searchWord = searchBox.getText().toString().trim();
				if (TextUtils.isEmpty(searchWord)) {
					tabView.setVisibility(View.VISIBLE);
					mPullToRefreshListView.setVisibility(View.GONE);
				} else {
					tabView.setVisibility(View.GONE);
					getCircleSearchId = mIUserLogic.getCircleSearch(searchWord, null,
							FusionCode.CommonColumnsValue.COUNT_VALUE,
							FusionCode.Relationship.FOLLOWING);
				}
			}

			@Override
			public void onAddMore() {
				String searchWord = searchBox.getText().toString().trim();
				if (TextUtils.isEmpty(searchWord)) {
					tabView.setVisibility(View.VISIBLE);
					mPullToRefreshListView.setVisibility(View.GONE);
				} else {
					tabView.setVisibility(View.GONE);
					if (mUserList.size() > 0) {
						getCircleSearchId = mIUserLogic.getCircleSearch(searchWord,
								mUserList.get(mUserList.size() - 1).userId,
								FusionCode.CommonColumnsValue.COUNT_VALUE,
								FusionCode.Relationship.FOLLOWING);
					} else {
						getCircleSearchId = mIUserLogic.getCircleSearch(searchWord, null,
								FusionCode.CommonColumnsValue.COUNT_VALUE,
								FusionCode.Relationship.FOLLOWING);
					}
				}
			}
		});
	}

	private void changeTopBar() {
		mfollowText.setTextColor(getResources().getColor(R.color.forward_title_normal));
		mstaredText.setTextColor(getResources().getColor(R.color.forward_title_normal));
		mfollowedByText.setTextColor(getResources().getColor(R.color.forward_title_normal));

		switch (mCurrentPage) {
		case PAGE_FOLLOW: {
			mfollowText.setTextColor(getResources().getColor(R.color.forward_title_select));
			break;
		}
		case PAGE_STARED: {
			mstaredText.setTextColor(getResources().getColor(R.color.forward_title_select));
			break;
		}
		case PAGE_FOLLOWEDBY: {
			mfollowedByText.setTextColor(getResources().getColor(R.color.forward_title_select));
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
		hideInputWindow(searchBox);
		super.finish();
		overridePendingTransition(R.anim.push_down_behind, R.anim.push_down);
	}

	@Override
	protected void handleStateMessage(Message msg) {
		switch (msg.what) {
		case FusionMessageType.UserMessageType.PULL_CIRCLE_SEARCH_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(getCircleSearchId)
					&& getCircleSearchId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mUserList = (List<User>) object.mObject;
					mChatSelectFriendAdapter.setmList(mUserList);
					mChatSelectFriendAdapter.notifyDataSetChanged();
					mPullToRefreshListView.setVisibility(View.VISIBLE);
				}
			}
			if (null != mPullToRefreshListView) {
				mPullToRefreshListView.onRefreshComplete();
			}
			break;
		}

		case FusionMessageType.UserMessageType.PUSH_CIRCLE_SEARCH_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(getCircleSearchId)
					&& getCircleSearchId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mUserList.addAll((List<User>) object.mObject);
					mChatSelectFriendAdapter.setmList(mUserList);
					selectListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
					mChatSelectFriendAdapter.notifyDataSetChanged();
					mPullToRefreshListView.setVisibility(View.VISIBLE);
				}
			}
			if (null != mPullToRefreshListView) {
				mPullToRefreshListView.onRefreshComplete();
			}
			break;
		}

		case FusionMessageType.UserMessageType.CIRCLE_SEARCH_FAIL: {
			if (null != mPullToRefreshListView) {
				mPullToRefreshListView.onRefreshComplete();
			}
			break;
		}
		default:
		}
	}

	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	private class PhotoGridViewAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		@Override
		public int getCount() {
			return addlist.size();
		}

		@Override
		public Object getItem(int i) {
			return i;
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			final ImageLoaderViewHolder holder;
			if (view == null) {
				if (null == inflater) {
					inflater = LayoutInflater.from(getApplicationContext());
				}
				view = inflater.inflate(R.layout.select_friend_gridview_item, null);
				holder = new ImageLoaderViewHolder();
				holder.imageView = (ImageView) view.findViewById(R.id.photo);
				view.setTag(holder);
			} else {
				holder = (ImageLoaderViewHolder) view.getTag();
			}

			final User mUser = addlist.get(i);
			if (null == mUser) {
				holder.imageView.setImageBitmap(null);
				holder.imageView.setOnClickListener(null);
			} else {
				ImageLoader.getInstance().displayImage(mUser.avatar_url, holder.imageView,
						optionsForUserIcon);
				holder.imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						addlist.remove(mUser);
						LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(
								new Intent(FusionAction.ChatAction.UPDATE_VIEW));
					}
				});
			}
			return view;
		}
	}
}
