package com.theone.sns.ui.chat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.chat.IPushListener;
import com.theone.sns.logic.chat.impl.ChatManager;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.logic.model.chat.SearchResult;
import com.theone.sns.logic.model.chat.base.SearchMessageResult;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.ui.base.AbstractRefreshUIThread;
import com.theone.sns.ui.base.IphoneTitleFragment;
import com.theone.sns.ui.base.dialog.TheOneAlertDialog;
import com.theone.sns.ui.chat.ChatListAdapter;
import com.theone.sns.ui.chat.ChatSelectFriendAdapter;
import com.theone.sns.util.ImageLoaderUtil;
import com.theone.sns.util.PrettyDateFormat;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshBase;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends IphoneTitleFragment implements IPushListener {

	private PullToRefreshListView mPullRefreshListView;
	private ListView mListView;
	private View heardView;
	private ChatListAdapter mChatListAdapter;
	private IChatLogic mIChatLogic;
	private List<GroupInfo> groupList = new ArrayList<GroupInfo>();
	private View popView;
	private PopupWindow popupWindow;
	private LinearLayout groupInvite;
	private ImageView chatIcon;
	private TextView chatName;
	private TextView chatCon;
	private TextView accept;
	private TextView refuse;
	private EditText searchBox;
	private String searchChatId;
	private SearchResult mSearchResult;
	private ChatSearchAdapter mChatSearchAdapter;
	private String syncGroupsId;
	private String getAllJoinedGroupFromDBId;
	private ChatListRefreshUI mChatListRefreshUI = new ChatListRefreshUI();
	private String createGroupId;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.context = (FragmentActivity) activity;
	}

	@Override
	protected void initLogics() {
		mIChatLogic = (IChatLogic) getLogicByInterfaceClass(IChatLogic.class);
	}

	@Override
	protected void onMyCreateView() {
		setSubContent(R.layout.chat_list_page);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (isNew) {
			getView(view);

			setView();
		}
		initPopupWindow();

		setListener();

		ChatManager.getInstance().addListener(this);
		
		if (null != mChatListRefreshUI) {
			mChatListRefreshUI.startQuery();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (null != mChatListRefreshUI) {
			mChatListRefreshUI.startQuery();
		}
	}

	private void getView(View view) {
		mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.chat_list_view);
		mListView = mPullRefreshListView.getRefreshableView();
		heardView = LayoutInflater.from(context).inflate(R.layout.chat_list_heard, null);

		groupInvite = (LinearLayout) view.findViewById(R.id.group_invite);
		chatIcon = (ImageView) view.findViewById(R.id.chat_icon);
		chatName = (TextView) view.findViewById(R.id.chat_name);
		chatCon = (TextView) view.findViewById(R.id.chat_con);
		accept = (TextView) view.findViewById(R.id.accept);
		refuse = (TextView) view.findViewById(R.id.refuse);

		searchBox = (EditText) heardView.findViewById(R.id.search_box);
	}

	private void setView() {
		setTitle(R.string.chat);
		setRightButton(R.drawable.add_chat_icon, false);

		mListView.addHeaderView(heardView);
		mChatListAdapter = new ChatListAdapter(context, optionsForUserIcon, mIChatLogic);
		mListView.setAdapter(mChatListAdapter);
	}

	private void setListener() {
		mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
			@Override
			public void onRefresh() {
				syncGroupsId = mIChatLogic.syncGroups();
			}

			@Override
			public void onAddMore() {

			}
		});

		getRightButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (null != popupWindow && !popupWindow.isShowing()) {
					popupWindow.showAtLocation(view, Gravity.RIGHT, 0, 0);
					popupWindow.update();
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
				String searchWorld = searchBox.getText().toString().trim();
				if (!TextUtils.isEmpty(searchWorld)) {
					searchChatId = mIChatLogic.searchChat(searchWorld);
				} else {
					mListView.setAdapter(null);
					mListView.removeHeaderView(heardView);
					mListView.addHeaderView(heardView);
					mListView.setAdapter(mChatListAdapter);
				}
			}
		});
	}

	private void initPopupWindow() {
		popView = LayoutInflater.from(context).inflate(R.layout.chat_pop, null);
		popView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (null != popupWindow)
					popupWindow.dismiss();
			}
		});
		popupWindow = new PopupWindow(popView, LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT, true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setHeight(getResources().getDisplayMetrics().heightPixels);
		popupWindow.setWidth(getResources().getDisplayMetrics().widthPixels);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popupWindow.setAnimationStyle(R.style.popwin_anim_style);

		popView.findViewById(R.id.group_chat).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
				GroupInfo mGroupInfo = new GroupInfo();
				List<User> mlist = new ArrayList<User>();
				mGroupInfo.members = mlist;
				startActivityForResult(
						new Intent(FusionAction.ChatAction.SELECT_FRIEND_ACTION).putExtra(
								FusionAction.ChatAction.GROUP_INFO, mGroupInfo).putExtra(
								FusionAction.ChatAction.SELECT_TYPE_1,
								ChatSelectFriendAdapter.CREAD_GROUP), ChatActivity.CREAD_GROUP);
			}
		});

		popView.findViewById(R.id.add_friend).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(FusionAction.MBlogAction.ADDFRIEND_ACTION));
				popupWindow.dismiss();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ChatActivity.CREAD_GROUP: {
			if (resultCode == Activity.RESULT_OK && null != data) {
				createGroupId = data.getStringExtra(FusionAction.ChatAction.CREATE_GROUP_ID);
				showLoadingDialog();
			}
			break;
		}
		}
	}

	@Override
	protected void handleStateMessage(Message msg) {
		switch (msg.what) {
		case FusionMessageType.ChatMessageType.GET_JOINED_GROUP_LIST_FROM_DB: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(getAllJoinedGroupFromDBId)
					&& getAllJoinedGroupFromDBId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					groupList = (List<GroupInfo>) object.mObject;
					mChatListAdapter.setGroupList(groupList);
					mChatListAdapter.notifyDataSetChanged();
				}
			}
			if (null != searchBox) {
				searchBox.requestFocus();
			}
			if (null != mPullRefreshListView) {
				mPullRefreshListView.onRefreshComplete();
			}
			break;
		}
		case FusionMessageType.ChatMessageType.SYNC_ALL_GROUP_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(syncGroupsId)
					&& syncGroupsId.equals(object.mLocalRequestId)) {
				getAllJoinedGroupFromDBId = mIChatLogic.getAllJoinedGroupFromDB();
			}
			if (null != searchBox) {
				searchBox.requestFocus();
			}
			if (null != mPullRefreshListView) {
				mPullRefreshListView.onRefreshComplete();
			}
			break;
		}
		case FusionMessageType.ChatMessageType.SYNC_ALL_GROUP_FAIL: {
			if (null != searchBox) {
				searchBox.requestFocus();
			}
			if (null != mPullRefreshListView) {
				mPullRefreshListView.onRefreshComplete();
			}
			break;
		}
		case FusionMessageType.ChatMessageType.SEARCH_CHAT_FROM_DB: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(searchChatId)
					&& searchChatId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mSearchResult = (SearchResult) object.mObject;
					List<SearchData> mSearchDataList = new ArrayList<SearchData>();
					if (null != mSearchResult.groupList && mSearchResult.groupList.size() > 0) {
						SearchData mSearchData = new SearchData(0);
						mSearchData.setText("群组");
						mSearchDataList.add(mSearchData);
						for (GroupInfo mGroupInfo : mSearchResult.groupList) {
							if (null == mGroupInfo) {
								continue;
							}
							SearchData mGroupInfoData = new SearchData(1);
							mGroupInfoData.setmGroupInfo(mGroupInfo);
							mSearchDataList.add(mGroupInfoData);
						}
					}

					if (null != mSearchResult.messageList && mSearchResult.messageList.size() > 0) {
						SearchData mSearchData = new SearchData(0);
						mSearchData.setText("消息");
						mSearchDataList.add(mSearchData);
						for (SearchMessageResult mSearchMessageResult : mSearchResult.messageList) {
							if (null == mSearchMessageResult
									|| null == mSearchMessageResult.groupInfo
									|| null == mSearchMessageResult.messageInfo) {
								continue;
							}
							SearchData mMessageData = new SearchData(2);
							mMessageData.setmGroupInfo(mSearchMessageResult.groupInfo);
							mMessageData.setmMessageInfo(mSearchMessageResult.messageInfo);
							mSearchDataList.add(mMessageData);
						}
					}

					if (null == mChatSearchAdapter) {
						mChatSearchAdapter = new ChatSearchAdapter(context, optionsForUserIcon);
					}
					mChatSearchAdapter.setmSearchDataList(mSearchDataList);
					mListView.setAdapter(null);
					mListView.removeHeaderView(heardView);
					mListView.addHeaderView(heardView);
					mListView.setAdapter(mChatSearchAdapter);
				}
				if (null != searchBox) {
					searchBox.requestFocus();
				}
			}
			break;
		}
		case FusionMessageType.ChatMessageType.CREATE_GROUP_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(createGroupId)
					&& createGroupId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					GroupInfo mGroupInfo = (GroupInfo) object.mObject;
					startActivity(new Intent(FusionAction.ChatAction.CHAT_ACTION).putExtra(
							FusionAction.ChatAction.GROUP_INFO, mGroupInfo));
				}
			}
			hideLoadingDialog();
			break;
		}
		case FusionMessageType.ChatMessageType.CREATE_GROUP_FAIL: {
			hideLoadingDialog();
			break;
		}
		}
	}

	@Override
	public void push(int what, final Object object) {
		switch (what) {
		case GROUP_CHANGE:
		case MESSAGE_ADD:
		case MESSAGE_DELETE:
		case MESSAGE_UPDATE: {
			if (null != mChatListRefreshUI) {
				mChatListRefreshUI.startQuery();
			}
			break;
		}

		case GROUP_INVITE: {
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					updateNewInvite((GroupInfo) object);
				}
			});
			if (null != mChatListRefreshUI) {
				mChatListRefreshUI.startQuery();
			}
			break;
		}
		default:
		}
	}

	class ChatListRefreshUI extends AbstractRefreshUIThread {

		@Override
		public void loadUIData() {
			getAllJoinedGroupFromDBId = mIChatLogic.getAllJoinedGroupFromDB();
		}
	}

	private void updateNewInvite(final GroupInfo mGroupInfo) {
		groupInvite.setVisibility(View.VISIBLE);

		if (null != mGroupInfo.owner) {
			ImageLoader.getInstance().displayImage(mGroupInfo.owner.avatar_url, chatIcon,
					optionsForUserIcon);
		}

		if (!TextUtils.isEmpty(mGroupInfo.name)) {
			chatCon.setText(mGroupInfo.name);
		} else {
			chatCon.setText(ChatListAdapter.getMemberName(mGroupInfo));
		}

		accept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				List<User> mlist = new ArrayList<User>();
				User mUser = new User();
				mUser.userId = FusionConfig.getInstance().getUserId();
				mlist.add(mUser);
				mIChatLogic.updateGroupMember(FusionCode.GroupMemberAction.JOIN, mGroupInfo._id,
						mlist);
				groupInvite.setVisibility(View.GONE);
			}
		});
		refuse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mIChatLogic.deleteUnjoinedGroup(mGroupInfo._id);
				groupInvite.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ChatManager.getInstance().removeListener(this);
		if (null != mChatListRefreshUI) {
			mChatListRefreshUI.exitThread();
			mChatListRefreshUI = null;
		}
	}

	class ChatSearchAdapter extends BaseAdapter {

		private final Context mContext;
		private final DisplayImageOptions optionsForUserIcon;
		private List<SearchData> mSearchDataList = new ArrayList<SearchData>();

		ChatSearchAdapter(Context mContext, DisplayImageOptions optionsForUserIcon) {
			this.mContext = mContext;
			this.optionsForUserIcon = optionsForUserIcon;
		}

		@Override
		public int getCount() {
			return mSearchDataList.size();
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
		public int getViewTypeCount() {
			return 3;
		}

		@Override
		public int getItemViewType(int position) {
			return mSearchDataList.get(position).type;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			final ImageLoaderViewHolder holder;
			if (view == null) {
				if (null == inflater) {
					inflater = LayoutInflater.from(mContext);
				}
				holder = new ImageLoaderViewHolder();
				switch (mSearchDataList.get(i).type) {
				case 0: {
					view = inflater.inflate(R.layout.chat_search_list_item_0, null);
					holder.mTextView = (TextView) view.findViewById(R.id.tag);
					break;
				}
				case 1: {
					view = inflater.inflate(R.layout.chat_search_list_item_1, null);
					holder.imageView = (ImageView) view.findViewById(R.id.chat_icon);

					holder.mRelativeLayout = (RelativeLayout) view.findViewById(R.id.chat_icon2);
					holder.mRelativeLayout1 = (RelativeLayout) view.findViewById(R.id.chat_icon3);
					holder.mRelativeLayout2 = (RelativeLayout) view.findViewById(R.id.chat_icon4);
					holder.mRelativeLayout3 = (RelativeLayout) view.findViewById(R.id.chat_icon5);
					holder.mRelativeLayout4 = (RelativeLayout) view.findViewById(R.id.chat_icon6);
					holder.mRelativeLayout5 = (RelativeLayout) view.findViewById(R.id.chat_icon7);
					holder.mRelativeLayout6 = (RelativeLayout) view.findViewById(R.id.chat_icon8);
					holder.mRelativeLayout7 = (RelativeLayout) view.findViewById(R.id.chat_icon9);

					holder.imageView1 = (ImageView) view.findViewById(R.id.chat_icon2_1);
					holder.imageView2 = (ImageView) view.findViewById(R.id.chat_icon2_2);

					holder.imageView3 = (ImageView) view.findViewById(R.id.chat_icon3_1);
					holder.imageView4 = (ImageView) view.findViewById(R.id.chat_icon3_2);
					holder.imageView5 = (ImageView) view.findViewById(R.id.chat_icon3_3);

					holder.imageView6 = (ImageView) view.findViewById(R.id.chat_icon4_1);
					holder.imageView7 = (ImageView) view.findViewById(R.id.chat_icon4_2);
					holder.imageView8 = (ImageView) view.findViewById(R.id.chat_icon4_3);
					holder.imageView9 = (ImageView) view.findViewById(R.id.chat_icon4_4);

					holder.imageView10 = (ImageView) view.findViewById(R.id.chat_icon5_1);
					holder.imageView11 = (ImageView) view.findViewById(R.id.chat_icon5_2);
					holder.imageView12 = (ImageView) view.findViewById(R.id.chat_icon5_3);
					holder.imageView13 = (ImageView) view.findViewById(R.id.chat_icon5_4);
					holder.imageView14 = (ImageView) view.findViewById(R.id.chat_icon5_5);

					holder.imageView15 = (ImageView) view.findViewById(R.id.chat_icon6_1);
					holder.imageView16 = (ImageView) view.findViewById(R.id.chat_icon6_2);
					holder.imageView17 = (ImageView) view.findViewById(R.id.chat_icon6_3);
					holder.imageView18 = (ImageView) view.findViewById(R.id.chat_icon6_4);
					holder.imageView19 = (ImageView) view.findViewById(R.id.chat_icon6_5);
					holder.imageView20 = (ImageView) view.findViewById(R.id.chat_icon6_6);

					holder.imageView21 = (ImageView) view.findViewById(R.id.chat_icon7_1);
					holder.imageView22 = (ImageView) view.findViewById(R.id.chat_icon7_2);
					holder.imageView23 = (ImageView) view.findViewById(R.id.chat_icon7_3);
					holder.imageView24 = (ImageView) view.findViewById(R.id.chat_icon7_4);
					holder.imageView25 = (ImageView) view.findViewById(R.id.chat_icon7_5);
					holder.imageView26 = (ImageView) view.findViewById(R.id.chat_icon7_6);
					holder.imageView27 = (ImageView) view.findViewById(R.id.chat_icon7_7);

					holder.imageView28 = (ImageView) view.findViewById(R.id.chat_icon8_1);
					holder.imageView29 = (ImageView) view.findViewById(R.id.chat_icon8_2);
					holder.imageView30 = (ImageView) view.findViewById(R.id.chat_icon8_3);
					holder.imageView31 = (ImageView) view.findViewById(R.id.chat_icon8_4);
					holder.imageView32 = (ImageView) view.findViewById(R.id.chat_icon8_5);
					holder.imageView33 = (ImageView) view.findViewById(R.id.chat_icon8_6);
					holder.imageView34 = (ImageView) view.findViewById(R.id.chat_icon8_7);
					holder.imageView35 = (ImageView) view.findViewById(R.id.chat_icon8_8);

					holder.imageView36 = (ImageView) view.findViewById(R.id.chat_icon9_1);
					holder.imageView37 = (ImageView) view.findViewById(R.id.chat_icon9_2);
					holder.imageView38 = (ImageView) view.findViewById(R.id.chat_icon9_3);
					holder.imageView39 = (ImageView) view.findViewById(R.id.chat_icon9_4);
					holder.imageView40 = (ImageView) view.findViewById(R.id.chat_icon9_5);
					holder.imageView41 = (ImageView) view.findViewById(R.id.chat_icon9_6);
					holder.imageView42 = (ImageView) view.findViewById(R.id.chat_icon9_7);
					holder.imageView43 = (ImageView) view.findViewById(R.id.chat_icon9_8);
					holder.imageView44 = (ImageView) view.findViewById(R.id.chat_icon9_9);

					holder.mTextView = (TextView) view.findViewById(R.id.chat_unread);
					holder.mTextView1 = (TextView) view.findViewById(R.id.chat_name);
					holder.mTextView2 = (TextView) view.findViewById(R.id.chat_con);
					holder.mTextView3 = (TextView) view.findViewById(R.id.chat_time);
					break;
				}
				case 2: {
					view = inflater.inflate(R.layout.chat_search_list_item_2, null);
					holder.imageView = (ImageView) view.findViewById(R.id.chat_icon);

					holder.mRelativeLayout = (RelativeLayout) view.findViewById(R.id.chat_icon2);
					holder.mRelativeLayout1 = (RelativeLayout) view.findViewById(R.id.chat_icon3);
					holder.mRelativeLayout2 = (RelativeLayout) view.findViewById(R.id.chat_icon4);
					holder.mRelativeLayout3 = (RelativeLayout) view.findViewById(R.id.chat_icon5);
					holder.mRelativeLayout4 = (RelativeLayout) view.findViewById(R.id.chat_icon6);
					holder.mRelativeLayout5 = (RelativeLayout) view.findViewById(R.id.chat_icon7);
					holder.mRelativeLayout6 = (RelativeLayout) view.findViewById(R.id.chat_icon8);
					holder.mRelativeLayout7 = (RelativeLayout) view.findViewById(R.id.chat_icon9);

					holder.imageView1 = (ImageView) view.findViewById(R.id.chat_icon2_1);
					holder.imageView2 = (ImageView) view.findViewById(R.id.chat_icon2_2);

					holder.imageView3 = (ImageView) view.findViewById(R.id.chat_icon3_1);
					holder.imageView4 = (ImageView) view.findViewById(R.id.chat_icon3_2);
					holder.imageView5 = (ImageView) view.findViewById(R.id.chat_icon3_3);

					holder.imageView6 = (ImageView) view.findViewById(R.id.chat_icon4_1);
					holder.imageView7 = (ImageView) view.findViewById(R.id.chat_icon4_2);
					holder.imageView8 = (ImageView) view.findViewById(R.id.chat_icon4_3);
					holder.imageView9 = (ImageView) view.findViewById(R.id.chat_icon4_4);

					holder.imageView10 = (ImageView) view.findViewById(R.id.chat_icon5_1);
					holder.imageView11 = (ImageView) view.findViewById(R.id.chat_icon5_2);
					holder.imageView12 = (ImageView) view.findViewById(R.id.chat_icon5_3);
					holder.imageView13 = (ImageView) view.findViewById(R.id.chat_icon5_4);
					holder.imageView14 = (ImageView) view.findViewById(R.id.chat_icon5_5);

					holder.imageView15 = (ImageView) view.findViewById(R.id.chat_icon6_1);
					holder.imageView16 = (ImageView) view.findViewById(R.id.chat_icon6_2);
					holder.imageView17 = (ImageView) view.findViewById(R.id.chat_icon6_3);
					holder.imageView18 = (ImageView) view.findViewById(R.id.chat_icon6_4);
					holder.imageView19 = (ImageView) view.findViewById(R.id.chat_icon6_5);
					holder.imageView20 = (ImageView) view.findViewById(R.id.chat_icon6_6);

					holder.imageView21 = (ImageView) view.findViewById(R.id.chat_icon7_1);
					holder.imageView22 = (ImageView) view.findViewById(R.id.chat_icon7_2);
					holder.imageView23 = (ImageView) view.findViewById(R.id.chat_icon7_3);
					holder.imageView24 = (ImageView) view.findViewById(R.id.chat_icon7_4);
					holder.imageView25 = (ImageView) view.findViewById(R.id.chat_icon7_5);
					holder.imageView26 = (ImageView) view.findViewById(R.id.chat_icon7_6);
					holder.imageView27 = (ImageView) view.findViewById(R.id.chat_icon7_7);

					holder.imageView28 = (ImageView) view.findViewById(R.id.chat_icon8_1);
					holder.imageView29 = (ImageView) view.findViewById(R.id.chat_icon8_2);
					holder.imageView30 = (ImageView) view.findViewById(R.id.chat_icon8_3);
					holder.imageView31 = (ImageView) view.findViewById(R.id.chat_icon8_4);
					holder.imageView32 = (ImageView) view.findViewById(R.id.chat_icon8_5);
					holder.imageView33 = (ImageView) view.findViewById(R.id.chat_icon8_6);
					holder.imageView34 = (ImageView) view.findViewById(R.id.chat_icon8_7);
					holder.imageView35 = (ImageView) view.findViewById(R.id.chat_icon8_8);

					holder.imageView36 = (ImageView) view.findViewById(R.id.chat_icon9_1);
					holder.imageView37 = (ImageView) view.findViewById(R.id.chat_icon9_2);
					holder.imageView38 = (ImageView) view.findViewById(R.id.chat_icon9_3);
					holder.imageView39 = (ImageView) view.findViewById(R.id.chat_icon9_4);
					holder.imageView40 = (ImageView) view.findViewById(R.id.chat_icon9_5);
					holder.imageView41 = (ImageView) view.findViewById(R.id.chat_icon9_6);
					holder.imageView42 = (ImageView) view.findViewById(R.id.chat_icon9_7);
					holder.imageView43 = (ImageView) view.findViewById(R.id.chat_icon9_8);
					holder.imageView44 = (ImageView) view.findViewById(R.id.chat_icon9_9);

					holder.mTextView = (TextView) view.findViewById(R.id.chat_unread);
					holder.mTextView1 = (TextView) view.findViewById(R.id.chat_name);
					holder.mTextView2 = (TextView) view.findViewById(R.id.chat_con);
					holder.mTextView3 = (TextView) view.findViewById(R.id.chat_time);
					break;
				}
				default:
				}

				view.setTag(holder);
			} else {
				holder = (ImageLoaderViewHolder) view.getTag();
			}
			final SearchData mSearchData = mSearchDataList.get(i);
			switch (mSearchData.type) {
			case 0: {
				holder.mTextView.setText(mSearchData.text);
				break;
			}
			case 1: {
				final GroupInfo mGroupInfo = mSearchData.mGroupInfo;
				if (null == mGroupInfo) {
					return view;
				}
				if (!TextUtils.isEmpty(mGroupInfo.name)) {
					holder.mTextView1.setText(mGroupInfo.name);
				} else {
					holder.mTextView1.setText(getMemberName(mGroupInfo));
				}

				if (mGroupInfo.unReadCount > 0) {
					holder.mTextView.setVisibility(View.VISIBLE);
					holder.mTextView.setText(mGroupInfo.unReadCount + "");
				} else {
					holder.mTextView.setVisibility(View.GONE);
				}

				if (null != mGroupInfo.newMessage) {
					switch (mGroupInfo.newMessage.messageType) {
					case FusionCode.MessageType.TEXT: {
						holder.mTextView2.setText(mGroupInfo.newMessage.text);
						break;
					}
					case FusionCode.MessageType.AUDIO: {
						if (null != mGroupInfo.newMessage && null != mGroupInfo.newMessage.owner) {
							holder.mTextView2
									.setText(mGroupInfo.newMessage.owner.name + ":[Audio]");
						} else {
							holder.mTextView2.setText("[Audio]");
						}
						break;
					}
					case FusionCode.MessageType.PHOTO: {
						if (null != mGroupInfo.newMessage && null != mGroupInfo.newMessage.owner) {
							holder.mTextView2
									.setText(mGroupInfo.newMessage.owner.name + ":[Photo]");
						} else {
							holder.mTextView2.setText("[Photo]");
						}
						break;
					}
					case FusionCode.MessageType.NAME_CARD: {
						if (null != mGroupInfo.newMessage && null != mGroupInfo.newMessage.owner) {
							holder.mTextView2.setText(mGroupInfo.newMessage.owner.name
									+ ":[Name Card]");
						} else {
							holder.mTextView2.setText("[Name Card]");
						}
						break;
					}
					default:
					}
				} else {
					holder.mTextView2.setText("");
				}

				if (null != mGroupInfo.newMessage) {
					holder.mTextView3.setText(PrettyDateFormat
							.formatISO8601TimeForChat(mGroupInfo.newMessage.created_at));
				} else {
					holder.mTextView3.setText("");
				}

				holder.imageView.setVisibility(View.GONE);
				holder.mRelativeLayout.setVisibility(View.GONE);
				holder.mRelativeLayout1.setVisibility(View.GONE);
				holder.mRelativeLayout2.setVisibility(View.GONE);
				holder.mRelativeLayout3.setVisibility(View.GONE);
				holder.mRelativeLayout4.setVisibility(View.GONE);
				holder.mRelativeLayout5.setVisibility(View.GONE);
				holder.mRelativeLayout6.setVisibility(View.GONE);
				holder.mRelativeLayout7.setVisibility(View.GONE);

				if (null != mGroupInfo.members) {
					switch (mGroupInfo.members.size()) {
					case 0: {
						break;
					}
					case 1: {
						holder.imageView.setVisibility(View.VISIBLE);
						ImageLoaderUtil.loadImage(mGroupInfo.members.get(0).avatar_url,
								holder.imageView, optionsForUserIcon);
						break;
					}
					case 2: {
						holder.imageView.setVisibility(View.VISIBLE);
						int j = 0;
						for (User mUser : mGroupInfo.members) {
							if (!FusionConfig.getInstance().getUserId().equals(mUser.userId)) {
								ImageLoaderUtil.loadImage(mGroupInfo.members.get(j).avatar_url,
										holder.imageView, optionsForUserIcon);
								break;
							}
							j++;
						}
						break;
					}
					case 3: {
						holder.mRelativeLayout1.setVisibility(View.VISIBLE);
						ImageLoaderUtil.loadImage(mGroupInfo.members.get(0).avatar_url,
								holder.imageView3, optionsForUserIcon);
						ImageLoaderUtil.loadImage(mGroupInfo.members.get(1).avatar_url,
								holder.imageView4, optionsForUserIcon);
						ImageLoaderUtil.loadImage(mGroupInfo.members.get(2).avatar_url,
								holder.imageView5, optionsForUserIcon);
						break;
					}
					case 4: {
						holder.mRelativeLayout2.setVisibility(View.VISIBLE);
						ImageLoaderUtil.loadImage(mGroupInfo.members.get(0).avatar_url,
								holder.imageView6, optionsForUserIcon);
						ImageLoaderUtil.loadImage(mGroupInfo.members.get(1).avatar_url,
								holder.imageView7, optionsForUserIcon);
						ImageLoaderUtil.loadImage(mGroupInfo.members.get(2).avatar_url,
								holder.imageView8, optionsForUserIcon);
						ImageLoaderUtil.loadImage(mGroupInfo.members.get(3).avatar_url,
								holder.imageView9, optionsForUserIcon);
						break;
					}
					case 5: {
						holder.mRelativeLayout3.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(0).avatar_url, holder.imageView10,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(1).avatar_url, holder.imageView11,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(2).avatar_url, holder.imageView12,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(3).avatar_url, holder.imageView13,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(4).avatar_url, holder.imageView14,
								optionsForUserIcon);
						break;
					}
					case 6: {
						holder.mRelativeLayout4.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(0).avatar_url, holder.imageView15,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(1).avatar_url, holder.imageView16,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(2).avatar_url, holder.imageView17,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(3).avatar_url, holder.imageView18,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(4).avatar_url, holder.imageView19,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(5).avatar_url, holder.imageView20,
								optionsForUserIcon);
						break;
					}
					case 7: {
						holder.mRelativeLayout5.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(0).avatar_url, holder.imageView21,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(1).avatar_url, holder.imageView22,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(2).avatar_url, holder.imageView23,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(3).avatar_url, holder.imageView24,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(4).avatar_url, holder.imageView25,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(5).avatar_url, holder.imageView26,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(6).avatar_url, holder.imageView27,
								optionsForUserIcon);
						break;
					}
					case 8: {
						holder.mRelativeLayout6.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(0).avatar_url, holder.imageView28,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(1).avatar_url, holder.imageView29,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(2).avatar_url, holder.imageView30,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(3).avatar_url, holder.imageView31,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(4).avatar_url, holder.imageView32,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(5).avatar_url, holder.imageView33,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(6).avatar_url, holder.imageView34,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(7).avatar_url, holder.imageView35,
								optionsForUserIcon);
						break;
					}

					default: {
						holder.mRelativeLayout7.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(0).avatar_url, holder.imageView36,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(1).avatar_url, holder.imageView37,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(2).avatar_url, holder.imageView38,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(3).avatar_url, holder.imageView39,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(4).avatar_url, holder.imageView40,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(5).avatar_url, holder.imageView41,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(6).avatar_url, holder.imageView42,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(7).avatar_url, holder.imageView43,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(8).avatar_url, holder.imageView44,
								optionsForUserIcon);
						break;
					}
					}
				}

				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mIChatLogic.updateMessageIsRead(mGroupInfo._id);
						mContext.startActivity(new Intent(FusionAction.ChatAction.CHAT_ACTION)
								.putExtra(FusionAction.ChatAction.GROUP_INFO, mGroupInfo));
					}
				});

				view.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View view) {
						new TheOneAlertDialog.Builder(mContext)
								.setMessage(R.string.confirmation_delete_chat)
								.setPositiveButton(R.string.delete,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
												mIChatLogic.updateGroupForDelete(mGroupInfo._id,
														true);
											}
										})
								.setNegativeButton(R.string.Cancel,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
											}
										}).show();
						return true;
					}
				});

				break;
			}
			case 2: {
				final GroupInfo mGroupInfo = mSearchData.mGroupInfo;
				if (null == mGroupInfo) {
					return view;
				}
				if (!TextUtils.isEmpty(mGroupInfo.name)) {
					holder.mTextView1.setText(mGroupInfo.name);
				} else {
					holder.mTextView1.setText(getMemberName(mGroupInfo));
				}

				if (mGroupInfo.unReadCount > 0) {
					holder.mTextView.setVisibility(View.VISIBLE);
					holder.mTextView.setText(mGroupInfo.unReadCount + "");
				} else {
					holder.mTextView.setVisibility(View.GONE);
				}

				if (null != mSearchData.mMessageInfo) {
					if (null != mSearchData.mMessageInfo.owner) {
						holder.mTextView2.setText(mSearchData.mMessageInfo.owner.name + ":"
								+ mSearchData.mMessageInfo.text);
					} else {
						holder.mTextView2.setText(mSearchData.mMessageInfo.text);
					}
					holder.mTextView3.setText(PrettyDateFormat
							.formatISO8601TimeForChat(mSearchData.mMessageInfo.created_at));
				} else {
					holder.mTextView2.setText("");
					holder.mTextView3.setText("");
				}

				holder.imageView.setVisibility(View.GONE);
				holder.mRelativeLayout.setVisibility(View.GONE);
				holder.mRelativeLayout1.setVisibility(View.GONE);
				holder.mRelativeLayout2.setVisibility(View.GONE);
				holder.mRelativeLayout3.setVisibility(View.GONE);
				holder.mRelativeLayout4.setVisibility(View.GONE);
				holder.mRelativeLayout5.setVisibility(View.GONE);
				holder.mRelativeLayout6.setVisibility(View.GONE);
				holder.mRelativeLayout7.setVisibility(View.GONE);

				if (null != mGroupInfo.members) {
					switch (mGroupInfo.members.size()) {
					case 0: {
						break;
					}
					case 1: {
						holder.imageView.setVisibility(View.VISIBLE);
						ImageLoaderUtil.loadImage(mGroupInfo.members.get(0).avatar_url,
								holder.imageView, optionsForUserIcon);
						break;
					}
					case 2: {
						holder.imageView.setVisibility(View.VISIBLE);
						int j = 0;
						for (User mUser : mGroupInfo.members) {
							if (!FusionConfig.getInstance().getUserId().equals(mUser.userId)) {
								ImageLoaderUtil.loadImage(mGroupInfo.members.get(j).avatar_url,
										holder.imageView, optionsForUserIcon);
								break;
							}
							j++;
						}
						break;
					}
					case 3: {
						holder.mRelativeLayout1.setVisibility(View.VISIBLE);
						ImageLoaderUtil.loadImage(mGroupInfo.members.get(0).avatar_url,
								holder.imageView3, optionsForUserIcon);
						ImageLoaderUtil.loadImage(mGroupInfo.members.get(1).avatar_url,
								holder.imageView4, optionsForUserIcon);
						ImageLoaderUtil.loadImage(mGroupInfo.members.get(2).avatar_url,
								holder.imageView5, optionsForUserIcon);
						break;
					}
					case 4: {
						holder.mRelativeLayout2.setVisibility(View.VISIBLE);
						ImageLoaderUtil.loadImage(mGroupInfo.members.get(0).avatar_url,
								holder.imageView6, optionsForUserIcon);
						ImageLoaderUtil.loadImage(mGroupInfo.members.get(1).avatar_url,
								holder.imageView7, optionsForUserIcon);
						ImageLoaderUtil.loadImage(mGroupInfo.members.get(2).avatar_url,
								holder.imageView8, optionsForUserIcon);
						ImageLoaderUtil.loadImage(mGroupInfo.members.get(3).avatar_url,
								holder.imageView9, optionsForUserIcon);
						break;
					}
					case 5: {
						holder.mRelativeLayout3.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(0).avatar_url, holder.imageView10,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(1).avatar_url, holder.imageView11,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(2).avatar_url, holder.imageView12,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(3).avatar_url, holder.imageView13,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(4).avatar_url, holder.imageView14,
								optionsForUserIcon);
						break;
					}
					case 6: {
						holder.mRelativeLayout4.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(0).avatar_url, holder.imageView15,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(1).avatar_url, holder.imageView16,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(2).avatar_url, holder.imageView17,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(3).avatar_url, holder.imageView18,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(4).avatar_url, holder.imageView19,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(5).avatar_url, holder.imageView20,
								optionsForUserIcon);
						break;
					}
					case 7: {
						holder.mRelativeLayout5.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(0).avatar_url, holder.imageView21,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(1).avatar_url, holder.imageView22,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(2).avatar_url, holder.imageView23,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(3).avatar_url, holder.imageView24,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(4).avatar_url, holder.imageView25,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(5).avatar_url, holder.imageView26,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(6).avatar_url, holder.imageView27,
								optionsForUserIcon);
						break;
					}
					case 8: {
						holder.mRelativeLayout6.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(0).avatar_url, holder.imageView28,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(1).avatar_url, holder.imageView29,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(2).avatar_url, holder.imageView30,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(3).avatar_url, holder.imageView31,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(4).avatar_url, holder.imageView32,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(5).avatar_url, holder.imageView33,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(6).avatar_url, holder.imageView34,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(7).avatar_url, holder.imageView35,
								optionsForUserIcon);
						break;
					}

					default: {
						holder.mRelativeLayout7.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(0).avatar_url, holder.imageView36,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(1).avatar_url, holder.imageView37,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(2).avatar_url, holder.imageView38,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(3).avatar_url, holder.imageView39,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(4).avatar_url, holder.imageView40,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(5).avatar_url, holder.imageView41,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(6).avatar_url, holder.imageView42,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(7).avatar_url, holder.imageView43,
								optionsForUserIcon);
						ImageLoader.getInstance().displayImage(
								mGroupInfo.members.get(8).avatar_url, holder.imageView44,
								optionsForUserIcon);
						break;
					}
					}
				}

				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mIChatLogic.updateMessageIsRead(mGroupInfo._id);
						mContext.startActivity(new Intent(FusionAction.ChatAction.CHAT_ACTION)
								.putExtra(FusionAction.ChatAction.GROUP_INFO, mGroupInfo).putExtra(
										FusionAction.ChatAction.MESSGAE_ID,
										mSearchData.mMessageInfo._id));
					}
				});

				view.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View view) {
						new TheOneAlertDialog.Builder(mContext)
								.setMessage(R.string.confirmation_delete_chat)
								.setPositiveButton(R.string.delete,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
												mIChatLogic.updateGroupForDelete(mGroupInfo._id,
														true);
											}
										})
								.setNegativeButton(R.string.Cancel,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
											}
										}).show();
						return true;
					}
				});

				break;
			}
			}
			return view;
		}

		public void setmSearchDataList(List<SearchData> mSearchDataList) {
			this.mSearchDataList = mSearchDataList;
		}
	}

	class SearchData {

		int type;

		String text;

		GroupInfo mGroupInfo;

		MessageInfo mMessageInfo;

		SearchData(int type) {
			this.type = type;
		}

		public void setText(String text) {
			this.text = text;
		}

		public void setmGroupInfo(GroupInfo mGroupInfo) {
			this.mGroupInfo = mGroupInfo;
		}

		public void setmMessageInfo(MessageInfo mMessageInfo) {
			this.mMessageInfo = mMessageInfo;
		}
	}

	public static String getMemberName(GroupInfo mGroupInfo) {
		if (null == mGroupInfo.members) {
			return "";
		} else {
			List<User> mlist = mGroupInfo.members;
			StringBuffer sb = new StringBuffer();
			for (User mUser : mlist) {
				if (null != mUser) {
					if (mUser.userId.equals(FusionConfig.getInstance().getUserId())) {
						continue;
					}
					sb.append(mUser.name);
					sb.append(",");
				}
			}
			if (!TextUtils.isEmpty(sb.toString())) {
				return sb.toString().substring(0, sb.toString().length() - 1);
			} else {
				return "";
			}
		}
	}
}
