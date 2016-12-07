package com.theone.sns.ui.mblog.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionCode.MatchContactType;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.contact.AndroidContactsFactory;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.model.user.MatchContactsResult;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.user.IUserLogic;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.me.GotoTaActivityOnClickListener;
import com.theone.sns.util.SMSHelper;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshBase;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshListView;

public class ContactFriendActivity extends IphoneTitleActivity {

	private PullToRefreshListView mPullToRefreshListView;

	private ListView listView;

	private List<MatchContactsResult> mMatchContacts = new ArrayList<MatchContactsResult>();

	protected ContactFriendAdapter mContactFriendAdapter;

	private LayoutInflater inflater;

	public IUserLogic mIUserLogic;

	private String requestId;

	@Override
	protected void initLogics() {
		mIUserLogic = (IUserLogic) getLogicByInterfaceClass(IUserLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.user_list_main);

		getView();

		setView();

		setListener();
	}

	private void getView() {

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.use_list);

		listView = mPullToRefreshListView.getRefreshableView();
	}

	private void setView() {
		setUseListTitle();
		setLeftButton(R.drawable.icon_back, false, false);

		mContactFriendAdapter = new ContactFriendAdapter();
		mContactFriendAdapter.setMatchContacts(mMatchContacts);
		listView.setAdapter(mContactFriendAdapter);

		sendRequest();
	}

	protected void sendRequest() {
		requestId = mIUserLogic.getMatchContactList(AndroidContactsFactory.getAllContacts());
	}

	protected void setUseListTitle() {
		setTitle(R.string.phone);
	}

	private void setListener() {
		mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
			@Override
			public void onRefresh() {
				sendRequest();
			}

			@Override
			public void onAddMore() {

			}
		});
	}

	@Override
	protected void handleStateMessage(Message msg) {
		switch (msg.what) {
		case FusionMessageType.UserMessageType.GET_MATCH_CONTACT_LIST_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mMatchContacts = (List<MatchContactsResult>) object.mObject;
					if (null == mContactFriendAdapter) {
						mContactFriendAdapter = new ContactFriendAdapter();
					}
					mContactFriendAdapter.setMatchContacts(mMatchContacts);
					mContactFriendAdapter.notifyDataSetChanged();
				}
			}
            if(null!=mPullToRefreshListView){
            	mPullToRefreshListView.onRefreshComplete();
            }
			break;
		}
		case FusionMessageType.UserMessageType.GET_MATCH_CONTACT_LIST_FAIL: {
            if(null!=mPullToRefreshListView){
            	mPullToRefreshListView.onRefreshComplete();
            }
			break;
		}
		default:
		}
	}

	protected class ContactFriendAdapter extends BaseAdapter {

		private List<MatchContactsResult> mMatchContacts = new ArrayList<MatchContactsResult>();

		@Override
		public int getCount() {
			return mMatchContacts.size();
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
			return mMatchContacts.get(position).type;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			final ImageLoaderViewHolder holder;
			if (view == null) {
				if (null == inflater) {
					inflater = LayoutInflater.from(getApplicationContext());
				}

				holder = new ImageLoaderViewHolder();

				switch (mMatchContacts.get(i).type) {

				case MatchContactType.SEPARATORS: {
					view = inflater.inflate(R.layout.match_contact_separator, null);
					holder.mTextView = (TextView) view.findViewById(R.id.separator_text);
					break;
				}
				case MatchContactType.USER: {

					view = inflater.inflate(R.layout.user_list_item, null);

					holder.imageView = (ImageView) view.findViewById(R.id.image_view);

					holder.mTextView = (TextView) view.findViewById(R.id.name);

					holder.mButton = (Button) view.findViewById(R.id.follow_btn);

					view.findViewById(R.id.role).setVisibility(View.GONE);

					view.findViewById(R.id.xinghun).setVisibility(View.GONE);

					view.findViewById(R.id.xingxing).setVisibility(View.GONE);

					view.findViewById(R.id.age).setVisibility(View.GONE);

					view.findViewById(R.id.hight).setVisibility(View.GONE);

					view.findViewById(R.id.distance).setVisibility(View.GONE);

					break;
				}
				case MatchContactType.CONTACT: {

					view = inflater.inflate(R.layout.contact_list_item, null);

					holder.imageView = (ImageView) view.findViewById(R.id.image_view);

					holder.mTextView = (TextView) view.findViewById(R.id.name);

					holder.mButton = (Button) view.findViewById(R.id.invite);

					break;
				}
				}

				view.setTag(holder);
			} else {
				holder = (ImageLoaderViewHolder) view.getTag();
			}

			final MatchContactsResult matchContactsResult = mMatchContacts.get(i);

			switch (mMatchContacts.get(i).type) {

			case MatchContactType.SEPARATORS: {

				holder.mTextView.setText(matchContactsResult.Separators);

				break;
			}
			case MatchContactType.USER: {

				ImageLoader.getInstance().displayImage(
						matchContactsResult.registeredUser.user.avatar_url, holder.imageView,
						options);

				holder.mTextView.setText(matchContactsResult.registeredUser.name);

				showButton(matchContactsResult.registeredUser.user, holder);

				holder.imageView.setOnClickListener(new GotoTaActivityOnClickListener(
						getApplicationContext(), matchContactsResult.registeredUser.user.userId));
				break;
			}
			case MatchContactType.CONTACT: {

				holder.mTextView.setText(matchContactsResult.contact.name);

				final List<String> phones = matchContactsResult.contact.phones;

				if (null == phones || phones.size() == 0) {

					holder.mButton.setVisibility(View.GONE);

				} else {

					holder.mButton.setVisibility(View.VISIBLE);

					holder.mButton.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							SMSHelper.sendSMS(ContactFriendActivity.this, phones.get(0),
									getString(R.string.invite_friend_detail));
						}
					});
				}

				break;
			}
			}

			return view;
		}

		public void setMatchContacts(List<MatchContactsResult> mMatchContacts) {
			if (null != mMatchContacts)
				this.mMatchContacts = mMatchContacts;
		}
	}

	protected void showButton(final User mUser,
			final ImageLoaderViewHolder holder) {

		if (FusionConfig.getInstance().getUserId().equals(mUser.userId)) {

			holder.mButton.setVisibility(View.GONE);

		} else {

			holder.mButton.setVisibility(View.VISIBLE);

			FusionConfig.showFollowButton(mUser, holder.mButton);

			holder.mButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					FusionConfig.clickFollowButton(mIUserLogic, mUser,
							holder.mButton);
				}
			});
		}
	}
}
