package com.theone.sns.ui.me.activity;

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
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionCode.RelationshipAction;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.component.location.gps.LocalLocation;
import com.theone.sns.component.location.gps.LocationManager;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.user.IUserLogic;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.me.GotoTaActivityOnClickListener;
import com.theone.sns.util.StringUtil;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshBase;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshListView;

/**
 * Created by zhangwenhai on 2014/9/25.
 */
public class UserListActivity extends IphoneTitleActivity {

	protected PullToRefreshListView mPullToRefreshListView;

	private ListView listView;

	private List<User> mUserList = new ArrayList<User>();

	protected UserListAdapter mUserListAdapter;

	private LayoutInflater inflater;

	private LocalLocation mLocation;

	public IUserLogic mIUserLogic;

	private String myUserId = FusionConfig.getInstance().getUserId();

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
		mLocation = LocationManager.getInstance().getLocation();

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.use_list);
		listView = mPullToRefreshListView.getRefreshableView();
	}

	private void setView() {
		setUseListTitle();
		setLeftButton(R.drawable.icon_back, false, false);

		mUserListAdapter = new UserListAdapter();
		mUserListAdapter.setmUserList(mUserList);
		listView.setAdapter(mUserListAdapter);

		sendRequest();
	}

	protected void sendRequest() {
		showLoadingDialog();
	}

	protected void setUseListTitle() {

	}

	private void setListener() {
		mPullToRefreshListView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
					@Override
					public void onRefresh() {
						sendRefreshRequest();
					}

					@Override
					public void onAddMore() {
						sendAddMoreRequest();
					}
				});
	}

	protected void sendAddMoreRequest() {

	}

	protected void sendRefreshRequest() {

	}

	protected void handleStateMessage(Message msg) {
		hideLoadingDialog();
	}

	protected void showButton(final User mUser,
			final ImageLoaderViewHolder holder) {

		if (mUser.userId.equals(myUserId)) {

			holder.mButton.setVisibility(View.GONE);

		} else {

			holder.mButton.setVisibility(View.VISIBLE);

			FusionConfig.showFollowButton(mUser, holder.mButton);

			holder.mButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					FusionConfig.clickFollowButton(mIUserLogic, mUser,
							holder.mButton);

					if (getString(R.string.follow).equals(
							(String) holder.mButton.getText().toString())) {

						if (mUser.marriage) {

							holder.imageView2.setVisibility(View.VISIBLE);

						} else {

							holder.imageView2.setVisibility(View.GONE);
						}

					} else {
						holder.imageView2.setVisibility(View.GONE);
					}
				}
			});
		}
	}

	protected class UserListAdapter extends BaseAdapter {

		private List<User> mUserList = new ArrayList<User>();

		@Override
		public int getCount() {
			return mUserList.size();
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
				view = inflater.inflate(R.layout.user_list_item, null);
				holder = new ImageLoaderViewHolder();
				holder.imageView = (ImageView) view
						.findViewById(R.id.image_view);
				holder.mTextView = (TextView) view.findViewById(R.id.name);
				holder.imageView1 = (ImageView) view.findViewById(R.id.role);
				holder.imageView2 = (ImageView) view.findViewById(R.id.xinghun);
				holder.imageView3 = (ImageView) view
						.findViewById(R.id.xingxing);
				holder.mButton = (Button) view.findViewById(R.id.follow_btn);

				holder.mTextView1 = (TextView) view.findViewById(R.id.age);
				holder.mTextView2 = (TextView) view.findViewById(R.id.hight);
				holder.mTextView3 = (TextView) view.findViewById(R.id.distance);
				view.setTag(holder);
			} else {
				holder = (ImageLoaderViewHolder) view.getTag();
			}

			final User mUser = mUserList.get(i);
			ImageLoader.getInstance().displayImage(mUser.avatar_url,
					holder.imageView, optionsForUserIcon);
			holder.mTextView.setText(mUser.name);

			if (!TextUtils.isEmpty(mUser.role)) {
				holder.imageView1.setVisibility(View.VISIBLE);
			}

			if (FusionCode.Role.H.equals(mUser.role)) {
				holder.imageView1.setImageDrawable(getResources().getDrawable(
						R.drawable.home_h_icon));
			} else if (FusionCode.Role.T.equals(mUser.role)) {
				holder.imageView1.setImageDrawable(getResources().getDrawable(
						R.drawable.home_t_icon));
			} else if (FusionCode.Role.P.equals(mUser.role)) {
				holder.imageView1.setImageDrawable(getResources().getDrawable(
						R.drawable.home_p_icon));
			} else if (FusionCode.Role.MH.equals(mUser.role)) {
				holder.imageView1.setImageDrawable(getResources().getDrawable(
						R.drawable.home_0_icon));
			} else if (FusionCode.Role.MT.equals(mUser.role)) {
				holder.imageView1.setImageDrawable(getResources().getDrawable(
						R.drawable.home_1_icon));
			} else if (FusionCode.Role.MP.equals(mUser.role)) {
				holder.imageView1.setImageDrawable(getResources().getDrawable(
						R.drawable.home_0_5_icon));
			} else {
				holder.imageView1.setVisibility(View.GONE);
			}

			if (mUser.marriage && mUser.is_following) {
				holder.imageView2.setVisibility(View.VISIBLE);
			} else {
				holder.imageView2.setVisibility(View.GONE);
			}
			if (mUser.is_starring) {
				holder.imageView3.setVisibility(View.VISIBLE);
			} else {
				holder.imageView3.setVisibility(View.GONE);
			}

			showButton(mUser, holder);

			holder.mTextView1.setText(mUser.age + getString(R.string.age_year));
			holder.mTextView2.setText(mUser.height
					+ getString(R.string.height_cm));

			if (null != mUser.location && mUser.location.size() == 2
					&& null != mLocation && !mUser.userId.equals(myUserId)) {

				holder.mTextView3.setVisibility(View.VISIBLE);

				holder.mTextView3.setText(StringUtil.getDistance(
						Double.valueOf(mLocation.longitude),
						Double.valueOf(mLocation.latitude),
						Double.valueOf(mUser.location.get(0)),
						Double.valueOf(mUser.location.get(1)))
						+ getString(R.string.distance_km));

			} else {

				holder.mTextView3.setVisibility(View.GONE);
			}

			holder.imageView
					.setOnClickListener(new GotoTaActivityOnClickListener(
							getApplicationContext(), mUser.userId));

			return view;
		}

		public void setmUserList(List<User> mUserList) {
			if (null != mUserList)
				this.mUserList = mUserList;
		}
	}

}
