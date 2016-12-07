package com.theone.sns.ui.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.logic.model.mblog.MBlog;
import com.theone.sns.logic.model.mblog.base.Photo;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.chat.ChatSelectFriendAdapter;
import com.theone.sns.util.ImageLoaderUtil;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/11/18.
 */
public class ForwardActivity extends IphoneTitleActivity {
	private final static int CREATE_GROUP = 0;
	private Button cancelBtn;
	private EditText searchBox;
	private ListView listview;
	private IChatLogic mIChatLogic;
	private String getAllJoinedGroupFromDBId;
	private List<GroupInfo> groupList = new ArrayList<GroupInfo>();
	private ForwardAdapter mForwardAdapter;
	private String createGroupId;
	private MessageInfo mMessageInfo;
	private String searchWord;

	@Override
	protected void initLogics() {
		mIChatLogic = (IChatLogic) getLogicByInterfaceClass(IChatLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.forward_main);
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_behind);

		getView();

		setView();

		setListener();

		getAllJoinedGroupFromDBId = mIChatLogic.getAllJoinedGroupFromDB();
	}

	private void getView() {
		mMessageInfo = (MessageInfo) getIntent().getSerializableExtra(
				FusionAction.ChatAction.MESSGAE_INFO);
		if (null == mMessageInfo) {
			finish();
		}
		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		searchBox = (EditText) findViewById(R.id.search_box);
		listview = (ListView) findViewById(R.id.list_view);
	}

	private void setView() {
		setTitle(R.string.forward);
		setLeftButton(R.drawable.icon_back, false, false);
		mForwardAdapter = new ForwardAdapter();
		listview.setAdapter(mForwardAdapter);
	}

	private void setListener() {
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
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
				searchWord = searchBox.getText().toString().trim();
				mForwardAdapter.showSearchResult(searchWord);
			}
		});
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
					mForwardAdapter.setmList(groupList);
					mForwardAdapter.notifyDataSetChanged();
				}
			}
			searchBox.requestFocus();
			break;
		}
		case FusionMessageType.ChatMessageType.CREATE_GROUP_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(createGroupId)
					&& createGroupId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					GroupInfo mGroupInfo = (GroupInfo) object.mObject;
					if (null == mMessageInfo.owner) {
						return;
					}
					sendMessage(mMessageInfo, mGroupInfo._id);
					setResult(RESULT_OK);
					finish();
				}
			}
			break;
		}
		}
	}

	private void sendMessage(MessageInfo mMessageInfo, String id) {
		MessageInfo mMessageInfo1 = null;
		switch (mMessageInfo.messageType) {
		case FusionCode.MessageType.TEXT: {
			mMessageInfo1 = new MessageInfo();
			mMessageInfo1.messageType = FusionCode.MessageType.TEXT;
			mMessageInfo1.text = mMessageInfo.text;
			mMessageInfo1.recipient = id;
			break;
		}
		case FusionCode.MessageType.AUDIO: {
			mMessageInfo1 = new MessageInfo();
			mMessageInfo.messageType = FusionCode.MessageType.AUDIO;
			mMessageInfo1.audio = mMessageInfo.audio;
			mMessageInfo1.recipient = id;
			User mUser = new User();
			mUser.userId = FusionConfig.getInstance().getUserId();
			mMessageInfo.owner = mUser;
			if (FusionConfig.getInstance().getUserId().equals(mMessageInfo.owner.userId)) {
				mMessageInfo1.audio.url = mMessageInfo.network_media_url;
			}
			break;
		}
		case FusionCode.MessageType.PHOTO: {
			mMessageInfo1 = new MessageInfo();
			mMessageInfo1.messageType = FusionCode.MessageType.PHOTO;
			mMessageInfo1.photo = mMessageInfo.photo;
			mMessageInfo1.recipient = id;
			User mUser = new User();
			mUser.userId = FusionConfig.getInstance().getUserId();
			mMessageInfo1.owner = mUser;
			if (FusionConfig.getInstance().getUserId().equals(mMessageInfo.owner.userId)) {
				mMessageInfo1.photo.url = mMessageInfo.network_media_url;
			}
			break;
		}
		case FusionCode.MessageType.NAME_CARD: {
			mMessageInfo1 = new MessageInfo();
			mMessageInfo1.messageType = FusionCode.MessageType.NAME_CARD;
			mMessageInfo1.name_card = mMessageInfo.name_card;
			mMessageInfo1.recipient = id;
			break;
		}
		case FusionCode.MessageType.POSITION: {
			mMessageInfo1 = new MessageInfo();
			mMessageInfo1.messageType = FusionCode.MessageType.POSITION;
			mMessageInfo1.position = mMessageInfo.position;
			User mUser = new User();
			mUser.userId = FusionConfig.getInstance().getUserId();
			mMessageInfo1.owner = mUser;
			mMessageInfo1.recipient = id;
			break;
		}
		}
		mIChatLogic.sendMessage(mMessageInfo1);
	}

	@Override
	public void finish() {
		hideInputWindow(searchBox);
		super.finish();
		overridePendingTransition(R.anim.push_down_behind, R.anim.push_down);
	}

	class ForwardAdapter extends BaseAdapter {

		private List<GroupInfo> mList = new ArrayList<GroupInfo>();
		private LayoutInflater inflater;
		private List<GroupInfo> searchList = new ArrayList<GroupInfo>();
		private boolean isSearch = false;

		@Override
		public int getCount() {
			if (isSearch)
				return searchList.size() + 1;
			return mList.size() + 1;
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
					inflater = LayoutInflater.from(ForwardActivity.this);
				}
				view = inflater.inflate(R.layout.forward_chat_list_item, null);
				holder = new ImageLoaderViewHolder();
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

				holder.mTextView1 = (TextView) view.findViewById(R.id.chat_name);
				view.setTag(holder);
			} else {
				holder = (ImageLoaderViewHolder) view.getTag();
			}

			if (i == 0) {
				holder.imageView.setVisibility(View.GONE);
				holder.mRelativeLayout.setVisibility(View.GONE);
				holder.mRelativeLayout1.setVisibility(View.GONE);
				holder.mRelativeLayout2.setVisibility(View.GONE);
				holder.mRelativeLayout3.setVisibility(View.GONE);
				holder.mRelativeLayout4.setVisibility(View.GONE);
				holder.mRelativeLayout5.setVisibility(View.GONE);
				holder.mRelativeLayout6.setVisibility(View.GONE);
				holder.mRelativeLayout7.setVisibility(View.GONE);

				holder.mTextView1.setText(getString(R.string.new_group));

				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						GroupInfo mGroupInfo = new GroupInfo();
						List<User> mlist = new ArrayList<User>();
						mGroupInfo.members = mlist;
						startActivityForResult(
								new Intent(FusionAction.ChatAction.SELECT_FRIEND_ACTION).putExtra(
										FusionAction.ChatAction.GROUP_INFO, mGroupInfo).putExtra(
										FusionAction.ChatAction.SELECT_TYPE_1,
										ChatSelectFriendAdapter.CREAD_GROUP), CREATE_GROUP);
					}
				});
				return view;
			}

			List<GroupInfo> m = new ArrayList<GroupInfo>();
			if (isSearch) {
				m.clear();
				m.addAll(searchList);
			} else {
				m.clear();
				m.addAll(mList);
			}
			final GroupInfo mGroupInfo = m.get(i - 1);
			if (!TextUtils.isEmpty(mGroupInfo.name)) {
				holder.mTextView1.setText(mGroupInfo.name);
			} else {
				holder.mTextView1.setText(getMemberName(mGroupInfo));
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
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(0).avatar_url,
							holder.imageView10, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(1).avatar_url,
							holder.imageView11, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(2).avatar_url,
							holder.imageView12, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(3).avatar_url,
							holder.imageView13, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(4).avatar_url,
							holder.imageView14, optionsForUserIcon);
					break;
				}
				case 6: {
					holder.mRelativeLayout4.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(0).avatar_url,
							holder.imageView15, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(1).avatar_url,
							holder.imageView16, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(2).avatar_url,
							holder.imageView17, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(3).avatar_url,
							holder.imageView18, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(4).avatar_url,
							holder.imageView19, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(5).avatar_url,
							holder.imageView20, optionsForUserIcon);
					break;
				}
				case 7: {
					holder.mRelativeLayout5.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(0).avatar_url,
							holder.imageView21, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(1).avatar_url,
							holder.imageView22, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(2).avatar_url,
							holder.imageView23, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(3).avatar_url,
							holder.imageView24, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(4).avatar_url,
							holder.imageView25, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(5).avatar_url,
							holder.imageView26, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(6).avatar_url,
							holder.imageView27, optionsForUserIcon);
					break;
				}
				case 8: {
					holder.mRelativeLayout6.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(0).avatar_url,
							holder.imageView28, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(1).avatar_url,
							holder.imageView29, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(2).avatar_url,
							holder.imageView30, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(3).avatar_url,
							holder.imageView31, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(4).avatar_url,
							holder.imageView32, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(5).avatar_url,
							holder.imageView33, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(6).avatar_url,
							holder.imageView34, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(7).avatar_url,
							holder.imageView35, optionsForUserIcon);
					break;
				}

				default: {
					holder.mRelativeLayout7.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(0).avatar_url,
							holder.imageView36, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(1).avatar_url,
							holder.imageView37, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(2).avatar_url,
							holder.imageView38, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(3).avatar_url,
							holder.imageView39, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(4).avatar_url,
							holder.imageView40, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(5).avatar_url,
							holder.imageView41, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(6).avatar_url,
							holder.imageView42, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(7).avatar_url,
							holder.imageView43, optionsForUserIcon);
					ImageLoader.getInstance().displayImage(mGroupInfo.members.get(8).avatar_url,
							holder.imageView44, optionsForUserIcon);
					break;
				}
				}
			}

			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (null == mMessageInfo.owner) {
						return;
					}
					sendMessage(mMessageInfo, mGroupInfo._id);
					setResult(RESULT_OK);
					finish();
				}
			});
			return view;
		}

		public String getMemberName(GroupInfo mGroupInfo) {
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

		public void setmList(List<GroupInfo> mList) {
			this.mList = mList;
		}

		private void showSearchResult(String s) {
			searchList.clear();
			if (TextUtils.isEmpty(s)) {
				isSearch = false;
			} else {
				isSearch = true;
				for (GroupInfo mGroupInfo : mList) {
					if (checkString(mGroupInfo.name).toLowerCase().indexOf(s.toLowerCase()) >= 0) {
						searchList.add(mGroupInfo);
					}
				}
			}
			notifyDataSetChanged();
		}

		private String checkString(String str) {
			if (str == null) {
				str = "";
			}
			return str;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CREATE_GROUP: {
			if (resultCode == RESULT_OK && null != data) {
				createGroupId = data.getStringExtra(FusionAction.ChatAction.CREATE_GROUP_ID);
			}
			break;
		}
		}
	}

	public static MessageInfo createMessageInfo(MBlog mBlog) {
		MessageInfo mMessageInfo = new MessageInfo();
		mMessageInfo.messageType = FusionCode.MessageType.PHOTO;
		Photo mPhoto = new Photo();
		if (null != mBlog.photo) {
			mPhoto.url = mBlog.photo.url;
			mPhoto.w = mBlog.photo.w;
			mPhoto.h = mBlog.photo.h;
			mMessageInfo.network_media_url = mBlog.photo.url;
		} else {
			if (null != mBlog.video) {
				mPhoto.url = mBlog.video.thumbnail_url;
				mPhoto.w = mBlog.video.w;
				mPhoto.h = mBlog.video.h;
				mMessageInfo.network_media_url = mBlog.video.thumbnail_url;
			}
		}
		mMessageInfo.photo = mPhoto;
		User mUser = new User();
		mUser.userId = FusionConfig.getInstance().getUserId();
		mMessageInfo.owner = mUser;
		return mMessageInfo;
	}
}
