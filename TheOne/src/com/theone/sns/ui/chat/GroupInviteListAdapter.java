package com.theone.sns.ui.chat;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.ui.chat.activity.GroupInviteListActivity;
import com.theone.sns.util.ImageLoaderUtil;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

/**
 * Created by zhangwenhai on 2014/11/6.
 */
public class GroupInviteListAdapter extends BaseAdapter {
	private final DisplayImageOptions optionsForUserIcon;
	private final IChatLogic mIChatLogic;
	private final GroupInviteListActivity mGroupInviteListActivity;
	private LayoutInflater inflater;
	private List<GroupInfo> mGroupInfoList = new ArrayList<GroupInfo>();

	public GroupInviteListAdapter(GroupInviteListActivity activity, DisplayImageOptions optionsForUserIcon,
			IChatLogic mIChatLogic) {
		this.mGroupInviteListActivity = activity;
		this.optionsForUserIcon = optionsForUserIcon;
		this.mIChatLogic = mIChatLogic;
	}

	@Override
	public int getCount() {
		return mGroupInfoList.size();
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
				inflater = LayoutInflater.from(mGroupInviteListActivity);
			}
			view = inflater.inflate(R.layout.group_invite_list_item, null);
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

			holder.mTextView = (TextView) view.findViewById(R.id.chat_name);
			holder.mTextView1 = (TextView) view.findViewById(R.id.chat_con);
			holder.mTextView2 = (TextView) view.findViewById(R.id.accept);
			holder.mTextView3 = (TextView) view.findViewById(R.id.refuse);

			view.setTag(holder);
		} else {
			holder = (ImageLoaderViewHolder) view.getTag();
		}

		final GroupInfo mGroupInfo = mGroupInfoList.get(i);
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
				if(mGroupInfo.members.size()>8){
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
				}
				break;
			}
			}
		}

		holder.mTextView2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				List<User> mlist = new ArrayList<User>();
				User mUser = new User();
				mUser.userId = FusionConfig.getInstance().getUserId();
				mlist.add(mUser);
				mGroupInviteListActivity.showLoadingDialog();
				mIChatLogic.updateGroupMember(FusionCode.GroupMemberAction.JOIN, mGroupInfo._id,
						mlist);
			}
		});

		holder.mTextView3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				List<User> mlist = new ArrayList<User>();
				User mUser = new User();
				mUser.userId = FusionConfig.getInstance().getUserId();
				mlist.add(mUser);
				mGroupInviteListActivity.showLoadingDialog();
				mIChatLogic.deleteUnjoinedGroup(mGroupInfo._id);
			}
		});

		return view;
	}

	public void setmGroupInfoList(List<GroupInfo> mGroupInfoList) {
		this.mGroupInfoList = mGroupInfoList;
	}

	private String getMemberName(GroupInfo mGroupInfo) {
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
