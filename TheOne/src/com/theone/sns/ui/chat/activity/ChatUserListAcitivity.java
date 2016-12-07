package com.theone.sns.ui.chat.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.base.dialog.TheOneAlertDialog;
import com.theone.sns.ui.me.GotoTaActivityOnClickListener;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2014/12/9.
 */
public class ChatUserListAcitivity extends IphoneTitleActivity {
	private ListView chatUserList;
	private EditText searchBox;
	private GroupInfo mGroupInfo;
	private IChatLogic mIChatLogic;
	private String updateGroupMemberId;
	private UserGridAdapter mUserGridAdapter;
	private String searchWord;

	@Override
	protected void initLogics() {
		mIChatLogic = (IChatLogic) getLogicByInterfaceClass(IChatLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.chat_user_list_main);

		getView();

		setView();

		setListener();
	}

	private void getView() {
		mGroupInfo = (GroupInfo) getIntent().getSerializableExtra(
				FusionAction.ChatAction.GROUP_INFO);
		if (null == mGroupInfo || null == mGroupInfo.members) {
			finish();
		}

		searchBox = (EditText) findViewById(R.id.search_box);
		chatUserList = (ListView) findViewById(R.id.chat_user_list);
	}

	private void setView() {
		setTitle(R.string.chat_member);
		setLeftButton(R.drawable.icon_back, false, false);

		mUserGridAdapter = new UserGridAdapter();
		mUserGridAdapter.setMlist(mGroupInfo.members);
		chatUserList.setAdapter(mUserGridAdapter);
	}

	private void setListener() {
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
				mUserGridAdapter.showSearchResult(searchWord);
			}
		});
	}

	@Override
	public void finish() {
		hideInputWindow(searchBox);
		super.finish();
	}

	@Override
	protected void handleStateMessage(Message msg) {
		super.handleStateMessage(msg);
		switch (msg.what) {
		case FusionMessageType.ChatMessageType.UPDATE_MEMBER_GROUP_INFO_SUCCESS: {
			if (null != mIChatLogic && null != mGroupInfo && null != chatUserList) {
				mGroupInfo = mIChatLogic.getGroupInfoFromDB(mGroupInfo._id);
				if (null == mUserGridAdapter) {
					mUserGridAdapter = new UserGridAdapter();
					chatUserList.setAdapter(mUserGridAdapter);
				}
				if (null == mGroupInfo.members) {
					return;
				}
				mUserGridAdapter.setMlist(mGroupInfo.members);
				chatUserList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
				mUserGridAdapter.notifyDataSetChanged();
			}
			hideLoadingDialog();
			break;
		}
		case FusionMessageType.ChatMessageType.UPDATE_MEMBER_GROUP_INFO_FAIL: {
			hideLoadingDialog();
			break;
		}
		}
	}

	private class UserGridAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		private List<User> mlist = new ArrayList<User>();

		private List<User> searchList = new ArrayList<User>();

		private boolean isSearch = false;

		@Override
		public int getCount() {
			if (isSearch) {
				return searchList.size();
			} else {
				return mlist.size();
			}
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
					inflater = LayoutInflater.from(ChatUserListAcitivity.this);
				}
				view = inflater.inflate(R.layout.chat_user_list_item, null);
				holder = new ImageLoaderViewHolder();
				holder.imageView = (ImageView) view.findViewById(R.id.image);
				holder.mTextView = (TextView) view.findViewById(R.id.user_name);
				view.setTag(holder);
			} else {
				holder = (ImageLoaderViewHolder) view.getTag();
			}

			List<User> m = new ArrayList<User>();
			if (isSearch) {
				m.clear();
				m.addAll(searchList);
			} else {
				m.clear();
				m.addAll(mlist);
			}
			final User mUser = m.get(i);
			if (null != mUser) {
				ImageLoader.getInstance().displayImage(mUser.avatar_url, holder.imageView,
						optionsForUserIcon);
				holder.mTextView.setText(mUser.name);
			}

			if (null != mGroupInfo.owner
					&& FusionConfig.getInstance().getUserId().equals(mGroupInfo.owner.userId)) {
				view.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View view) {
						new TheOneAlertDialog.Builder(ChatUserListAcitivity.this)
								.setMessage(R.string.delete_user)
								.setPositiveButton(R.string.delete,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
												List<User> mList = new ArrayList<User>();
												mList.add(mUser);
												updateGroupMemberId = mIChatLogic
														.updateGroupMember(
																FusionCode.GroupMemberAction.REMOVE,
																mGroupInfo._id, mList);
												ChatUserListAcitivity.this.showLoadingDialog();
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
			}

			view.setOnClickListener(new GotoTaActivityOnClickListener(ChatUserListAcitivity.this,
					mUser.userId));

			return view;
		}

		public void setMlist(List<User> mlist) {
			this.mlist = mlist;
		}

		private void showSearchResult(String s) {
			searchList.clear();
			if (TextUtils.isEmpty(s)) {
				isSearch = false;
			} else {
				isSearch = true;
				for (User mUser : mlist) {
					if (checkString(mUser.name).toLowerCase().indexOf(s.toLowerCase()) >= 0) {
						searchList.add(mUser);
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
}
