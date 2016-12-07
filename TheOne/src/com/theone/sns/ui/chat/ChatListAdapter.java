package com.theone.sns.ui.chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.logic.adapter.db.GroupDbAdapter;
import com.theone.sns.logic.adapter.db.SettingDbAdapter;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.ui.base.dialog.TheOneAlertDialog;
import com.theone.sns.util.ImageLoaderUtil;
import com.theone.sns.util.PrettyDateFormat;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/10/27.
 */
public class ChatListAdapter extends BaseAdapter {

	private final Context mContext;
	private final DisplayImageOptions optionsForUserIcon;
	private final IChatLogic mIChatLogic;
	private LayoutInflater inflater;
	private List<GroupInfo> groupList = new ArrayList<GroupInfo>();

	public ChatListAdapter(Context mContext, DisplayImageOptions optionsForUserIcon,
			IChatLogic mIChatLogic) {
		this.mContext = mContext;
		this.optionsForUserIcon = optionsForUserIcon;
		this.mIChatLogic = mIChatLogic;
	}

	@Override
	public int getCount() {
		return groupList.size() + 1;
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
				inflater = LayoutInflater.from(mContext);
			}
			view = inflater.inflate(R.layout.chat_list_item, null);
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

			holder.mLinearLayout = (LinearLayout) view.findViewById(R.id.background_view);
			holder.mTextView = (TextView) view.findViewById(R.id.chat_unread);
			holder.mTextView1 = (TextView) view.findViewById(R.id.chat_name);
			holder.mTextView2 = (TextView) view.findViewById(R.id.chat_con);
			holder.mTextView3 = (TextView) view.findViewById(R.id.chat_time);
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

			holder.imageView.setVisibility(View.VISIBLE);
			ImageLoaderUtil.loadImage("drawable://" + R.drawable.friend_action, holder.imageView,
					optionsForUserIcon);
			holder.mTextView1.setText(R.string.notification);
			if (0 == SettingDbAdapter.getInstance()
					.getInt(FusionCode.SettingKey.NOTIFICATION_COUNT)) {
				holder.mTextView.setVisibility(View.GONE);
			} else {
				holder.mTextView.setVisibility(View.VISIBLE);
				holder.mTextView.setText(SettingDbAdapter.getInstance().getInt(
						FusionCode.SettingKey.NOTIFICATION_COUNT)
						+ "");
			}
			holder.mTextView2.setText(SettingDbAdapter.getInstance().getString(
					FusionCode.SettingKey.NEW_NOTIFICATION_CONTENT));
			holder.mTextView3.setText("");
			holder.mLinearLayout.setBackground(mContext.getResources().getDrawable(
					R.drawable.list_item_background));
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mContext.startActivity(new Intent(
							FusionAction.ChatAction.NOTIFICATIONLIST_ACTION));
				}
			});
			return view;
		}

		final GroupInfo mGroupInfo = groupList.get(i - 1);
		if (mGroupInfo.is_group_invite) {
			holder.imageView.setVisibility(View.GONE);
			holder.mRelativeLayout.setVisibility(View.GONE);
			holder.mRelativeLayout1.setVisibility(View.GONE);
			holder.mRelativeLayout2.setVisibility(View.GONE);
			holder.mRelativeLayout3.setVisibility(View.GONE);
			holder.mRelativeLayout4.setVisibility(View.GONE);
			holder.mRelativeLayout5.setVisibility(View.GONE);
			holder.mRelativeLayout6.setVisibility(View.GONE);
			holder.mRelativeLayout7.setVisibility(View.GONE);

			holder.imageView.setVisibility(View.VISIBLE);
			ImageLoaderUtil.loadImage("drawable://" + R.drawable.message_chat_remind_icon,
					holder.imageView, optionsForUserIcon);
			holder.mTextView1.setText(R.string.chat_item_1);
			if (0 == SettingDbAdapter.getInstance()
					.getInt(FusionCode.SettingKey.GROUP_INVITE_COUNT)) {
				holder.mTextView.setVisibility(View.GONE);
			} else {
				holder.mTextView.setVisibility(View.VISIBLE);
				holder.mTextView.setText(SettingDbAdapter.getInstance().getInt(
						FusionCode.SettingKey.GROUP_INVITE_COUNT)
						+ "");
			}

			String groupId = SettingDbAdapter.getInstance().getString(
					FusionCode.SettingKey.NEW_GROUP_INVITE_GROUPID);

			GroupInfo group = GroupDbAdapter.getInstance().getGroupById(groupId);

			if (null != group && null != group.owner) {
				holder.mTextView2.setText(group.owner.name + "邀请你加入群聊");
			}

			if (0 == SettingDbAdapter.getInstance()
					.getInt(FusionCode.SettingKey.GROUP_INVITE_COUNT)) {
				holder.mTextView2.setText(mContext.getText(R.string.no_invent));
			}

			holder.mLinearLayout.setBackground(mContext.getResources().getDrawable(
					R.drawable.list_item_background));

			holder.mTextView3.setText("");
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mContext.startActivity(new Intent(
							FusionAction.ChatAction.GROUPINVITELIST_ACTION));
				}
			});
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

		if (mGroupInfo.is_top) {
			holder.mLinearLayout.setBackground(mContext.getResources().getDrawable(
					R.drawable.top_list_item_background));
		} else {
			holder.mLinearLayout.setBackground(mContext.getResources().getDrawable(
					R.drawable.list_item_background));
		}

		holder.mTextView2.setTextColor(mContext.getResources().getColor(R.color.gray));

		if (null != mGroupInfo.newMessage) {
			switch (mGroupInfo.newMessage.messageType) {
			case FusionCode.MessageType.TEXT: {
				if (mGroupInfo.newMessage.isMentionMe) {
					if (null != mGroupInfo.newMessage.owner) {
						holder.mTextView2.setText("[" + mGroupInfo.newMessage.owner.name + "提到了你]:"
								+ mGroupInfo.newMessage.text);
					} else {
						holder.mTextView2.setText("[有人提到了你]");
					}
					if (mGroupInfo.unReadCount == 0) {
						holder.mTextView2.setTextColor(mContext.getResources().getColor(
								R.color.gray));
					} else {
						holder.mTextView2.setTextColor(mContext.getResources()
								.getColor(R.color.red));
					}
				} else {
					if (null != mGroupInfo.newMessage.owner) {
						holder.mTextView2.setText(mGroupInfo.newMessage.owner.name + ":"
								+ mGroupInfo.newMessage.text);
					} else {
						holder.mTextView2.setText(mGroupInfo.newMessage.text);
					}
				}
				break;
			}
			case FusionCode.MessageType.AUDIO: {
				if (null != mGroupInfo.newMessage && null != mGroupInfo.newMessage.owner) {
					holder.mTextView2.setText(mGroupInfo.newMessage.owner.name + ":["
							+ mContext.getResources().getString(R.string.audio) + "]");
				} else {
					holder.mTextView2.setText("["
							+ mContext.getResources().getString(R.string.audio) + "]");
				}
				break;
			}
			case FusionCode.MessageType.PHOTO: {
				if (null != mGroupInfo.newMessage && null != mGroupInfo.newMessage.owner) {
					holder.mTextView2.setText(mGroupInfo.newMessage.owner.name + ":["
							+ mContext.getResources().getString(R.string.media) + "]");
				} else {
					holder.mTextView2.setText("["
							+ mContext.getResources().getString(R.string.media) + "]");
				}
				break;
			}
			case FusionCode.MessageType.NAME_CARD: {
				if (null != mGroupInfo.newMessage && null != mGroupInfo.newMessage.owner) {
					holder.mTextView2.setText(mGroupInfo.newMessage.owner.name + ":["
							+ mContext.getResources().getString(R.string.plus4) + "]");
				} else {
					holder.mTextView2.setText("["
							+ mContext.getResources().getString(R.string.plus4) + "]");
				}
				break;
			}
			case FusionCode.MessageType.POSITION: {
				if (null != mGroupInfo.newMessage && null != mGroupInfo.newMessage.owner) {
					holder.mTextView2.setText(mGroupInfo.newMessage.owner.name + ":["
							+ mContext.getResources().getString(R.string.plus3) + "]");
				} else {
					holder.mTextView2.setText("["
							+ mContext.getResources().getString(R.string.plus3) + "]");
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
				ImageLoaderUtil.loadImage(mGroupInfo.members.get(0).avatar_url, holder.imageView,
						optionsForUserIcon);
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
				ImageLoaderUtil.loadImage(mGroupInfo.members.get(0).avatar_url, holder.imageView3,
						optionsForUserIcon);
				ImageLoaderUtil.loadImage(mGroupInfo.members.get(1).avatar_url, holder.imageView4,
						optionsForUserIcon);
				ImageLoaderUtil.loadImage(mGroupInfo.members.get(2).avatar_url, holder.imageView5,
						optionsForUserIcon);
				break;
			}
			case 4: {
				holder.mRelativeLayout2.setVisibility(View.VISIBLE);
				ImageLoaderUtil.loadImage(mGroupInfo.members.get(0).avatar_url, holder.imageView6,
						optionsForUserIcon);
				ImageLoaderUtil.loadImage(mGroupInfo.members.get(1).avatar_url, holder.imageView7,
						optionsForUserIcon);
				ImageLoaderUtil.loadImage(mGroupInfo.members.get(2).avatar_url, holder.imageView8,
						optionsForUserIcon);
				ImageLoaderUtil.loadImage(mGroupInfo.members.get(3).avatar_url, holder.imageView9,
						optionsForUserIcon);
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
				mIChatLogic.updateMessageIsRead(mGroupInfo._id);
				mContext.startActivity(new Intent(FusionAction.ChatAction.CHAT_ACTION).putExtra(
						FusionAction.ChatAction.GROUP_INFO, mGroupInfo));
			}
		});

		view.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				new TheOneAlertDialog.Builder(mContext)
						.setMessage(R.string.confirmation_delete_chat)
						.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								mIChatLogic.updateGroupForDelete(mGroupInfo._id, true);
							}
						})
						.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();
				return true;
			}
		});

		return view;
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

	public void setGroupList(List<GroupInfo> groupList) {
		this.groupList = groupList;
	}
}
