package com.theone.sns.ui.chat.activity;

import java.util.ArrayList;
import java.util.List;

import net.hockeyapp.android.FeedbackManager;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode.GroupMemberAction;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.base.dialog.TheOneAlertDialog;
import com.theone.sns.ui.chat.ChatSelectFriendAdapter;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;
import com.theone.sns.util.uiwidget.ImageWithDelete;
import com.theone.sns.util.uiwidget.ImageWithDeleteCallback;
import com.theone.sns.util.uiwidget.IphoneStyleAlertDialogBuilder;
import com.theone.sns.util.uiwidget.ScrollableGridView;
import com.theone.sns.util.uiwidget.swithbutton.SwitchButton;

/**
 * Created by zhangwenhai on 2014/10/28.
 */
public class ChatInfoActivity extends IphoneTitleActivity {
	public static final int GROUPNAME = 0;
	public static final int SELECT_FRIEND = 1;
	private IphoneStyleAlertDialogBuilder m_cantAccessDialog;
	private ScrollableGridView userIcons;
	private SwitchButton newMessage;
	private SwitchButton stayTop;
	private LinearLayout clearChat;
	private Button exitGroupBtn;
	private UserGridAdapter mUserGridAdapter;
	private GroupInfo mGroupInfo;
	private boolean isDeleteMode = false;
	private IChatLogic mIChatLogic;
	private LinearLayout groupNameView;
	private TextView groupName;
	private TextView groupSun;
	private LinearLayout chatMember;
	private RelativeLayout relaView;
	private LinearLayout reportView;

	@Override
	protected void initLogics() {
		mIChatLogic = (IChatLogic) getLogicByInterfaceClass(IChatLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.chat_info_main);

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
		userIcons = (ScrollableGridView) findViewById(R.id.user_icons);
		newMessage = (SwitchButton) findViewById(R.id.new_message);
		stayTop = (SwitchButton) findViewById(R.id.stay_top);
		clearChat = (LinearLayout) findViewById(R.id.clear_chat);
		exitGroupBtn = (Button) findViewById(R.id.exit_group_btn);
		groupNameView = (LinearLayout) findViewById(R.id.group_name);
		groupName = (TextView) findViewById(R.id.group_name1);
		groupSun = (TextView) findViewById(R.id.group_sun);
		chatMember = (LinearLayout) findViewById(R.id.chat_member);
		relaView = (RelativeLayout) findViewById(R.id.rela_view);
		reportView = (LinearLayout) findViewById(R.id.report_view);
	}

	private void setView() {
		setTitle(R.string.chat_info);
		setLeftButton(R.drawable.icon_back, false, false);
	}

	private void setListener() {
		userIcons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				if (isDeleteMode) {
					isDeleteMode = false;
					mUserGridAdapter.notifyDataSetChanged();
				}
			}
		});
		groupNameView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivityForResult(new Intent(FusionAction.ChatAction.GROUPNAME_ACTION)
						.putExtra(FusionAction.ChatAction.GROUP_INFO, mGroupInfo), GROUPNAME);
			}
		});

		chatMember.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});

		relaView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(FusionAction.ChatAction.CHATUSERLIST_ACTION).putExtra(
						FusionAction.ChatAction.GROUP_INFO, mGroupInfo));
			}
		});

		clearChat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new TheOneAlertDialog.Builder(ChatInfoActivity.this)
						.setMessage(R.string.chat_delete)
						.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								mIChatLogic.deleteAllMessage(mGroupInfo._id);
							}
						})
						.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
			}
		});
		
		reportView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FeedbackManager.showFeedbackActivity(ChatInfoActivity.this);
			}
		});

		exitGroupBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				showCantAccessDialog();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();

		setMember();
		groupName.setText(mGroupInfo.name);
		newMessage.setChecked(!mGroupInfo.is_muted);
		stayTop.setChecked(mGroupInfo.is_top);
		setExitGroupBtn();
	}

	private void setMember() {
		mGroupInfo = mIChatLogic.getGroupInfoFromDB(mGroupInfo._id);
		mUserGridAdapter = new UserGridAdapter();
		if (null != mGroupInfo.members) {
			groupSun.setText(String.format(getString(R.string.group_sun), mGroupInfo.members.size()));
			mUserGridAdapter.setMlist(mGroupInfo.members);
		}
		userIcons.setAdapter(mUserGridAdapter);
        hideLoadingDialogDelayed(500);
	}
	
	private void setExitGroupBtn() {

		if (null != mGroupInfo && null != mGroupInfo.members
				&& mGroupInfo.members.size() == 2) {

			String myUserId = FusionConfig.getInstance().getUserId();

			boolean isPrivacyChat = false;

			for (User user : mGroupInfo.members) {

				if (myUserId.equals(user.userId)) {

					isPrivacyChat = true;

					break;
				}
			}

			if (isPrivacyChat) {

				exitGroupBtn.setVisibility(View.GONE);

			} else {

				exitGroupBtn.setVisibility(View.VISIBLE);
			}

		} else {

			exitGroupBtn.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void finish() {
		if (!mGroupInfo.is_muted != newMessage.isChecked()) {
			mIChatLogic.updateGroupIsMuted(mGroupInfo._id, !newMessage.isChecked());
		}
		if (mGroupInfo.is_top != stayTop.isChecked()) {
			mIChatLogic.updateGroupInTop(mGroupInfo._id, stayTop.isChecked());
		}
		super.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case GROUPNAME: {
			if (Activity.RESULT_OK == resultCode) {
				groupName.setText(data.getStringExtra(FusionAction.ChatAction.GROUP_NAME));
			}
			break;
		}
		case SELECT_FRIEND: {
			if (Activity.RESULT_OK == resultCode) {
				showLoadingDialog();
			}
			break;
		}
		default:
		}
	}

	@Override
	protected void handleStateMessage(Message msg) {
		switch (msg.what) {
		case FusionMessageType.ChatMessageType.UPDATE_MEMBER_GROUP_INFO_SUCCESS: {
			setMember();
			break;
		}
		case FusionMessageType.ChatMessageType.UPDATE_MEMBER_GROUP_INFO_FAIL: {
			hideLoadingDialog();
			break;
		}
		default:
		}
	}

	private void showCantAccessDialog() {

		if (m_cantAccessDialog != null) {
			m_cantAccessDialog.show();
			return;
		}

		m_cantAccessDialog = new IphoneStyleAlertDialogBuilder(this);

		m_cantAccessDialog.addItem(0, getString(R.string.Done), 1, new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				List<User> userList = new ArrayList<User>();

				userList.add(FusionConfig.getInstance().getMyUser());

				mIChatLogic.updateGroupMember(GroupMemberAction.QUIT, mGroupInfo._id, userList);

				HelperFunc.startTabHostPage(ChatInfoActivity.this, 3);

				finish();
			}
		});

		m_cantAccessDialog.addItem(1, getString(R.string.Cancel), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_cantAccessDialog.dismiss();
			}
		});

		m_cantAccessDialog.show();
	}

	private class UserGridAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		private List<User> mlist = new ArrayList<User>();

		@Override
		public int getCount() {
			if (isDeleteMode) {
				if (null != mGroupInfo.owner
						&& FusionConfig.getInstance().getUserId().equals(mGroupInfo.owner.userId)) {
					List<User> mlist1 = new ArrayList<User>();
					int i = 0;
					for (User mUser : mlist) {
						mlist1.add(mUser);
						i++;
						if (i == 18) {
							break;
						}
					}
					mlist = mlist1;
					return mlist.size();
				} else {
					List<User> mlist1 = new ArrayList<User>();
					int i = 0;
					for (User mUser : mlist) {
						mlist1.add(mUser);
						i++;
						if (i == 19) {
							break;
						}
					}
					mlist = mlist1;
					return mlist.size();
				}
			} else {
				if (null != mGroupInfo.owner
						&& FusionConfig.getInstance().getUserId().equals(mGroupInfo.owner.userId)) {
					List<User> mlist1 = new ArrayList<User>();
					int i = 0;
					for (User mUser : mlist) {
						mlist1.add(mUser);
						i++;
						if (i == 18) {
							break;
						}
					}
					mlist = mlist1;
					return mlist.size() + 2;
				} else {
					List<User> mlist1 = new ArrayList<User>();
					int i = 0;
					for (User mUser : mlist) {
						mlist1.add(mUser);
						i++;
						if (i == 19) {
							break;
						}
					}
					mlist = mlist1;
					return mlist.size() + 1;
				}
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
					inflater = LayoutInflater.from(ChatInfoActivity.this);
				}
				view = inflater.inflate(R.layout.chat_info_user_item, null);
				holder = new ImageLoaderViewHolder();
				holder.mRelativeLayout = (RelativeLayout) view.findViewById(R.id.user_avatar);
				holder.mTextView = (TextView) view.findViewById(R.id.user_name);
				view.setTag(holder);
			} else {
				holder = (ImageLoaderViewHolder) view.getTag();
			}

			if (i == mlist.size()) {
				ImageLoader.getInstance().displayImage("drawable://" + R.drawable.chat_detail_add,
						((ImageWithDelete) holder.mRelativeLayout).getImageView(),
						optionsForUserIcon);
				holder.mTextView.setText("");
				((ImageWithDelete) holder.mRelativeLayout).getImageView().setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								startActivityForResult(
										new Intent(FusionAction.ChatAction.SELECT_FRIEND_ACTION)
												.putExtra(FusionAction.ChatAction.GROUP_INFO,
														mGroupInfo).putExtra(
														FusionAction.ChatAction.SELECT_TYPE_1,
														ChatSelectFriendAdapter.SELECT_FRIEND),
										ChatInfoActivity.SELECT_FRIEND);
							}
						});
				return view;
			}

			if (i == mlist.size() + 1) {
				ImageLoader.getInstance().displayImage(
						"drawable://" + R.drawable.chat_detail_delete,
						((ImageWithDelete) holder.mRelativeLayout).getImageView(),
						optionsForUserIcon);
				holder.mTextView.setText("");
				((ImageWithDelete) holder.mRelativeLayout).getImageView().setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								isDeleteMode = true;
								notifyDataSetChanged();
							}
						});
				return view;
			}

			final User mUser = mlist.get(i);
			if (null != mUser) {
				ImageLoader.getInstance().displayImage(mUser.avatar_url,
						((ImageWithDelete) holder.mRelativeLayout).getImageView(),
						optionsForUserIcon);
				holder.mTextView.setText(mUser.name);
			}

			if (isDeleteMode) {
				((ImageWithDelete) holder.mRelativeLayout).toDeleteMode();
			} else {
				((ImageWithDelete) holder.mRelativeLayout).toNormalMode();
			}

			((ImageWithDelete) holder.mRelativeLayout).setCallback(new ImageWithDeleteCallback() {
				@Override
				public void onDelete(ImageWithDelete v) {
					List<User> mList = new ArrayList<User>();
					mList.add(mUser);
					mIChatLogic.updateGroupMember(GroupMemberAction.REMOVE, mGroupInfo._id, mList);
					ChatInfoActivity.this.showLoadingDialog();
				}

				@Override
				public void onClick(ImageWithDelete v) {
					if (isDeleteMode) {
						isDeleteMode = false;
						notifyDataSetChanged();
					} else {
						startActivity(new Intent(FusionAction.MeAction.TA_ACTION).putExtra(
								FusionAction.MeAction.UID, mUser.userId).setFlags(
								Intent.FLAG_ACTIVITY_NEW_TASK));
					}
				}

				@Override
				public void onLongPress(ImageWithDelete v) {

				}
			});
			return view;
		}

		public void setMlist(List<User> mlist) {
			this.mlist = mlist;
		}
	}
}
