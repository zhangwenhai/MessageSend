package com.theone.sns.ui.mblog.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionAction.MBlogAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.component.location.gps.ILocationListener;
import com.theone.sns.component.location.gps.LocalLocation;
import com.theone.sns.component.location.gps.LocationManager;
import com.theone.sns.logic.account.IAccountLogic;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.mblog.IMBlogLogic;
import com.theone.sns.logic.model.chat.CreateGroup;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.logic.model.mblog.MBlog;
import com.theone.sns.logic.model.mblog.MBlogTag;
import com.theone.sns.logic.model.mblog.base.Comment;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.user.IUserLogic;
import com.theone.sns.ui.base.IphoneTitleFragment;
import com.theone.sns.ui.chat.activity.ChatActivity;
import com.theone.sns.ui.chat.activity.ForwardActivity;
import com.theone.sns.ui.mblog.GotoMblogCommentActivityOnClickListener;
import com.theone.sns.ui.me.GotoTaActivityOnClickListener;
import com.theone.sns.ui.publish.Tag;
import com.theone.sns.ui.publish.TagedImage;
import com.theone.sns.util.DownloadUtil;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.PrettyDateFormat;
import com.theone.sns.util.RoundProgressBar;
import com.theone.sns.util.StringUtil;
import com.theone.sns.util.uiwidget.ExpressionUtil;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;
import com.theone.sns.util.uiwidget.IphoneStyleAlertDialogBuilder;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshBase;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.sharesdk.ShareOperation;
import cn.sharesdk.ShareUtil;

/**
 * Created by zhangwenhai on 2014/9/2.
 */
public class MblogmodelFragment extends IphoneTitleFragment {

	private LayoutInflater inflater;

	private ListView listView;

	private int with;

	private PullToRefreshListView mPullToRefreshListView;

	private HomePageListAdapter mHomePageListAdapter;

	private IMBlogLogic mIMBlogLogic;

	private List<MBlog> mBlogList = new ArrayList<MBlog>();

	private String requestId;

	private String likerequestId;

	private String requestDBId;

	private View rootView;

	private IAccountLogic mIAccountLogic;

	public IUserLogic mIUserLogic;

	private LocalLocation mLocation;

	private String isplayingId;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (FusionAction.TheOneApp.UPDATE.equals(intent.getAction())) {
				mPullToRefreshListView.setVisibility(View.VISIBLE);
				mPullToRefreshListView.setRefreshing();

				mIChatLogic.setNotifyBadge(FusionCode.SettingKey.IS_NEW_POST, false);

				requestId = mIMBlogLogic.getFollowMBlogList(null,
						FusionCode.CommonColumnsValue.COUNT_VALUE);

				MblogmodelFragment.this.context.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listView.setSelection(0);
					}
				});
			}
		}
	};
	private MediaPlayer player;
	private int videoTime;
	private String getGroupInfoFromDBId;
	private IChatLogic mIChatLogic;
	private User pmUser;
	private String createGroupId;
	private TextView text;
	private View mblogModelHead;
	private ImageView imageAvter;
	private TextView name;
	private ImageView type;
	private TextView textButton;
	private ImageView image;
	private boolean isGone = true;

	// 小鲜肉推荐的用户
	private User mUser;
	private LinearLayout linear1;
	private IphoneStyleAlertDialogBuilder m_iphoneDialog;

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 0);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	@Override
	protected void onMyCreateView() {
		setSubContent(R.layout.home_page_main);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (!isNew) {
			return;
		}
		getView(view);

		setView();

		initLocation();

		IntentFilter filter = new IntentFilter();
		filter.addAction(FusionAction.TheOneApp.UPDATE);
		LocalBroadcastManager.getInstance(context).registerReceiver(mBroadcastReceiver, filter);
	}

	private void getView(View view) {
		mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.list);
		listView = mPullToRefreshListView.getRefreshableView();
		text = (TextView) view.findViewById(R.id.text);

		mblogModelHead = LayoutInflater.from(context).inflate(R.layout.mblog_model_head, null);
		linear1 = (LinearLayout) mblogModelHead.findViewById(R.id.linear);
		imageAvter = (ImageView) mblogModelHead.findViewById(R.id.image_avter);
		name = (TextView) mblogModelHead.findViewById(R.id.name);
		type = (ImageView) mblogModelHead.findViewById(R.id.type);
		textButton = (TextView) mblogModelHead.findViewById(R.id.text);
		image = (ImageView) mblogModelHead.findViewById(R.id.image);

		requestDBId = mIMBlogLogic.getFollowMBlogFromDB();

		requestId = mIMBlogLogic
				.getFollowMBlogList(null, FusionCode.CommonColumnsValue.COUNT_VALUE);
	}

	private void setView() {
		setTitleImage(R.drawable.navigation_bar_tittle);
		setRightButton(R.drawable.navigation_add_friends_btn, false);
		m_titleLayout.setBackgroundResource(R.color.color_ffd800);

		with = getResources().getDisplayMetrics().widthPixels;

		mHomePageListAdapter = new HomePageListAdapter();
		listView.addHeaderView(mblogModelHead);
		listView.setAdapter(mHomePageListAdapter);

		setHeadView();
		// listView.setOnScrollListener(new
		// PauseOnScrollListener(ImageLoader.getInstance(), false,
		// true));

		mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
			@Override
			public void onRefresh() {
				requestId = mIMBlogLogic.getFollowMBlogList(null,
						FusionCode.CommonColumnsValue.COUNT_VALUE);
				mIChatLogic.setNotifyBadge(FusionCode.SettingKey.IS_NEW_POST, false);
			}

			@Override
			public void onAddMore() {
				if (mBlogList.size() != 0) {
					requestId = mIMBlogLogic.getFollowMBlogList(
							mBlogList.get(mBlogList.size() - 1)._id,
							FusionCode.CommonColumnsValue.COUNT_VALUE);
				} else {
					requestId = mIMBlogLogic.getFollowMBlogList(null,
							FusionCode.CommonColumnsValue.COUNT_VALUE);
				}
			}
		});

		getRightButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(FusionAction.MBlogAction.ADDFRIEND_ACTION));
			}
		});

		getTitleImage().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				listView.setSelection(0);
			}
		});

		text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(FusionAction.MBlogAction.ADDFRIEND_ACTION));
			}
		});
	}

	private void setHeadView() {
		if (isGone || null != mUser) {
			linear1.setVisibility(View.GONE);
		} else {
			linear1.setVisibility(View.VISIBLE);
			ImageLoader.getInstance()
					.displayImage(mUser.avatar_url, imageAvter, optionsForUserIcon);
			name.setText(mUser.name);

			if (FusionCode.Role.H.equals(mUser.role)) {
				type.setImageDrawable(getResources().getDrawable(R.drawable.home_h_icon));
			} else if (FusionCode.Role.T.equals(mUser.role)) {
				type.setImageDrawable(getResources().getDrawable(R.drawable.home_t_icon));
			} else if (FusionCode.Role.P.equals(mUser.role)) {
				type.setImageDrawable(getResources().getDrawable(R.drawable.home_p_icon));
			} else if (FusionCode.Role.MH.equals(mUser.role)) {
				type.setImageDrawable(getResources().getDrawable(R.drawable.home_0_icon));
			} else if (FusionCode.Role.MT.equals(mUser.role)) {
				type.setImageDrawable(getResources().getDrawable(R.drawable.home_1_icon));
			} else if (FusionCode.Role.MP.equals(mUser.role)) {
				type.setImageDrawable(getResources().getDrawable(R.drawable.home_0_5_icon));
			} else {
				type.setVisibility(View.GONE);
			}

			linear1.setOnClickListener(new GotoTaActivityOnClickListener(context, mUser.userId));
			textButton.setOnClickListener(new GotoTaActivityOnClickListener(context, mUser.userId));

			image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if (m_iphoneDialog == null) {
						m_iphoneDialog = new IphoneStyleAlertDialogBuilder(context);
						// m_iphoneDialog.addItem(IPHONE_CAMERA,
						// getString(R.string.take_photo),
						// new View.OnClickListener() {
						// @Override
						// public void onClick(View v) {
						// m_iphoneDialog.dismiss();
						// }
						// });
						// m_iphoneDialog.addItem(IPHONE_GALLERY,
						// getString(R.string.choose_photo),
						// new View.OnClickListener() {
						// @Override
						// public void onClick(View v) {
						// m_iphoneDialog.dismiss();
						// }
						// });
						// m_iphoneDialog.addItem(IPHONE_CANCEL,
						// getString(R.string.Cancel),
						// IphoneStyleAlertDialogBuilder.COLOR_BLUE,
						// IphoneStyleAlertDialogBuilder.TEXT_TYPE_BOLD,
						// new View.OnClickListener() {
						// @Override
						// public void onClick(View v) {
						// m_iphoneDialog.dismiss();
						// }
						// });
					}
					m_iphoneDialog.show();
				}
			});
		}

	}

	private void initLocation() {

		mLocation = LocationManager.getInstance().getLocation();

		if (!FusionConfig.isInitLocation && null == mLocation) {

			LocationManager.getInstance().start(new ILocationListener() {

				@Override
				public void onResult(boolean result, LocalLocation location) {

					if (result && null != mHomePageListAdapter) {

						mLocation = LocationManager.getInstance().getLocation();

						mHomePageListAdapter.notifyDataSetChanged();
					}
				}
			});

			FusionConfig.isInitLocation = true;
		}
	}

	private class HomePageListAdapter extends BaseAdapter {

		private List<MBlog> mBlogList = new ArrayList<MBlog>();

		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		public void setList(List<MBlog> mBlogList) {
			this.mBlogList = mBlogList;
		}

		@Override
		public int getCount() {
			return mBlogList.size();
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
					inflater = LayoutInflater.from(context);
				}
				view = inflater.inflate(R.layout.home_page_listview_item, null);
				holder = new ImageLoaderViewHolder();
				holder.mTagedImage = (TagedImage) view.findViewById(R.id.add_label_image);
				holder.imageView12 = (ImageView) view.findViewById(R.id.audio_view);
				holder.mRoundProgressBar = (RoundProgressBar) view.findViewById(R.id.sound_playbar);
				holder.mRelativeLayout8 = (RelativeLayout) view
						.findViewById(R.id.download_progress);
				holder.imageView14 = (ImageView) view.findViewById(R.id.video_view);
				holder.imageView11 = (ImageView) view.findViewById(R.id.image);
				holder.mRoundProgressBar1 = (RoundProgressBar) view.findViewById(R.id.progressBar);

				holder.imageView7 = (ImageView) view.findViewById(R.id.image_avter);
				holder.mTextView = (TextView) view.findViewById(R.id.name);
				holder.imageView8 = (ImageView) view.findViewById(R.id.type);
				holder.imageView9 = (ImageView) view.findViewById(R.id.xing);
				holder.imageView10 = (ImageView) view.findViewById(R.id.xingxing);
				holder.mTextView1 = (TextView) view.findViewById(R.id.distance);
				holder.mTextView2 = (TextView) view.findViewById(R.id.time);

				holder.mTextView12 = (TextView) view.findViewById(R.id.like);
				holder.mTextView13 = (TextView) view.findViewById(R.id.comment);
				holder.mTextView19 = (TextView) view.findViewById(R.id.pm);

				holder.mTextView14 = (TextView) view.findViewById(R.id.mblog_comment);

				holder.imageView13 = (ImageView) view.findViewById(R.id.share);

				holder.mTextView18 = (TextView) view.findViewById(R.id.text1);
				holder.mLinearLayout5 = (LinearLayout) view.findViewById(R.id.text1_view);

				// holder.mButton = (Button) view.findViewById(R.id.follow_btn);
				holder.mTagedImage
						.setLayoutParams(new RelativeLayout.LayoutParams(
								LinearLayout.LayoutParams.FILL_PARENT, (int) (with - HelperFunc
										.dip2px(20))));
				holder.imageView11.setLayoutParams(new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.FILL_PARENT, (int) (with - HelperFunc
								.dip2px(20))));

				holder.mLinearLayout1 = (LinearLayout) view.findViewById(R.id.like_view);
				holder.imageView1 = (ImageView) view.findViewById(R.id.image_1);
				holder.imageView2 = (ImageView) view.findViewById(R.id.image_2);
				holder.imageView3 = (ImageView) view.findViewById(R.id.image_3);
				holder.imageView4 = (ImageView) view.findViewById(R.id.image_4);
				holder.imageView5 = (ImageView) view.findViewById(R.id.image_5);
				holder.imageView6 = (ImageView) view.findViewById(R.id.image_6);
				holder.imagenum = (TextView) view.findViewById(R.id.image_7);

				holder.mLinearLayout = (LinearLayout) view.findViewById(R.id.comment_view);

				holder.mLinearLayout2 = (LinearLayout) view.findViewById(R.id.resend1);
				holder.mLinearLayout3 = (LinearLayout) view.findViewById(R.id.resend2);
				holder.mLinearLayout4 = (LinearLayout) view.findViewById(R.id.resend3);

				holder.mTextView15 = (TextView) view.findViewById(R.id.target_name1);
				holder.mTextView16 = (TextView) view.findViewById(R.id.target_name2);
				holder.mTextView17 = (TextView) view.findViewById(R.id.target_name3);

				holder.comment_image1 = (ImageView) view.findViewById(R.id.comment_image1);
				holder.comment_image2 = (ImageView) view.findViewById(R.id.comment_image2);
				holder.comment_image3 = (ImageView) view.findViewById(R.id.comment_image3);

				holder.mTextView3 = (TextView) view.findViewById(R.id.comment_name1);
				holder.mTextView4 = (TextView) view.findViewById(R.id.comment_name2);
				holder.mTextView5 = (TextView) view.findViewById(R.id.comment_name3);

				holder.mTextView6 = (TextView) view.findViewById(R.id.comment_time1);
				holder.mTextView7 = (TextView) view.findViewById(R.id.comment_time2);
				holder.mTextView8 = (TextView) view.findViewById(R.id.comment_time3);

				holder.mTextView9 = (TextView) view.findViewById(R.id.comment_con1);
				holder.mTextView10 = (TextView) view.findViewById(R.id.comment_con2);
				holder.mTextView11 = (TextView) view.findViewById(R.id.comment_con3);

				holder.mRelativeLayout1 = (RelativeLayout) view.findViewById(R.id.comment_view1);
				holder.mRelativeLayout2 = (RelativeLayout) view.findViewById(R.id.comment_view2);
				holder.mRelativeLayout3 = (RelativeLayout) view.findViewById(R.id.comment_view3);

				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						(int) ((with - HelperFunc.dip2px(80)) / 7),
						(int) ((with - HelperFunc.dip2px(80)) / 7));
				lp.setMargins((int) HelperFunc.dip2px(10), 0, 0, 0);

				RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
						(int) ((with - HelperFunc.dip2px(80)) / 7),
						(int) ((with - HelperFunc.dip2px(80)) / 7));
				lp1.setMargins((int) HelperFunc.dip2px(10), 0, 0, 0);
				holder.imageView1.setLayoutParams(lp);
				holder.imageView2.setLayoutParams(lp);
				holder.imageView3.setLayoutParams(lp);
				holder.imageView4.setLayoutParams(lp);
				holder.imageView5.setLayoutParams(lp);
				holder.imageView6.setLayoutParams(lp);
				holder.imagenum.setLayoutParams(lp);
				holder.comment_image1.setLayoutParams(lp1);
				holder.comment_image2.setLayoutParams(lp1);
				holder.comment_image3.setLayoutParams(lp1);
				view.setTag(holder);
			} else {
				holder = (ImageLoaderViewHolder) view.getTag();
			}

			final MBlog mBlog = mBlogList.get(i);
			if (null == mBlog.owner) {
				return view;
			}
			ImageLoader.getInstance().displayImage(mBlog.owner.avatar_url, holder.imageView7,
					optionsForUserIcon, animateFirstListener);

			if (TextUtils.isEmpty(mBlog.text_desc)) {
				holder.mLinearLayout5.setVisibility(View.GONE);
			} else {
				holder.mTextView18.setText(mBlog.text_desc);
				holder.mLinearLayout5.setVisibility(View.VISIBLE);
				holder.mTextView18.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View view) {
						HelperFunc.copy(mBlog.text_desc, context.getApplicationContext());
						Toast.makeText(context.getApplicationContext(), R.string.copy_mblog,
								Toast.LENGTH_LONG).show();
						return false;
					}
				});
			}

			if (FusionConfig.getInstance().getUserId().equals(mBlog.owner.userId)) {
				holder.mTextView19.setVisibility(View.GONE);
			} else {
				holder.mTextView19.setVisibility(View.VISIBLE);
			}

			if (null != mBlog.audio_desc) {
				holder.imageView12.setVisibility(View.VISIBLE);
			} else {
				holder.imageView12.setVisibility(View.GONE);
			}

			holder.mRelativeLayout8.setVisibility(View.GONE);

			if (null == isplayingId) {
				holder.mRoundProgressBar.setVisibility(View.VISIBLE);
			} else if (isplayingId.equals(mBlog._id)) {
				holder.mRoundProgressBar.setVisibility(View.VISIBLE);
			} else {
				holder.mRoundProgressBar.setVisibility(View.GONE);
			}

			if (!TextUtils.isEmpty(mBlog.owner.alias)) {
				holder.mTextView.setText(mBlog.owner.alias);
			} else {
				holder.mTextView.setText(mBlog.owner.name);
			}

			if (!TextUtils.isEmpty(mBlog.owner.role)) {
				holder.imageView8.setVisibility(View.VISIBLE);
			}

			if (FusionCode.Role.H.equals(mBlog.owner.role)) {
				holder.imageView8.setImageDrawable(getResources().getDrawable(
						R.drawable.home_h_icon));
			} else if (FusionCode.Role.T.equals(mBlog.owner.role)) {
				holder.imageView8.setImageDrawable(getResources().getDrawable(
						R.drawable.home_t_icon));
			} else if (FusionCode.Role.P.equals(mBlog.owner.role)) {
				holder.imageView8.setImageDrawable(getResources().getDrawable(
						R.drawable.home_p_icon));
			} else if (FusionCode.Role.MH.equals(mBlog.owner.role)) {
				holder.imageView8.setImageDrawable(getResources().getDrawable(
						R.drawable.home_0_icon));
			} else if (FusionCode.Role.MT.equals(mBlog.owner.role)) {
				holder.imageView8.setImageDrawable(getResources().getDrawable(
						R.drawable.home_1_icon));
			} else if (FusionCode.Role.MP.equals(mBlog.owner.role)) {
				holder.imageView8.setImageDrawable(getResources().getDrawable(
						R.drawable.home_0_5_icon));
			} else {
				holder.imageView8.setVisibility(View.GONE);
			}

			if (mBlog.owner.marriage) {
				holder.imageView9.setVisibility(View.VISIBLE);
			} else {
				holder.imageView9.setVisibility(View.GONE);
			}

			if (mBlog.owner.is_starring) {
				holder.imageView10.setVisibility(View.VISIBLE);
			} else {
				holder.imageView10.setVisibility(View.GONE);
			}

			List<String> mBlogLocation = mBlog.location;

			if (null != mBlogLocation && mBlogLocation.size() == 2 && null != mLocation) {

				holder.mTextView1.setVisibility(View.VISIBLE);

				holder.mTextView1.setText(StringUtil.getDistance(
						Double.valueOf(mLocation.longitude), Double.valueOf(mLocation.latitude),
						Double.valueOf(mBlogLocation.get(0)), Double.valueOf(mBlogLocation.get(1)))
						+ getString(R.string.distance_km));
			} else {

				holder.mTextView1.setVisibility(View.GONE);
			}

			holder.mTextView2.setText(PrettyDateFormat.formatISO8601Time(mBlog.created_at));

			float scale = 1.0f;

			if (null != mBlog.photo) {
				if (0 != mBlog.photo.h && 0 != mBlog.photo.w) {
					scale = (float) mBlog.photo.h / (float) mBlog.photo.w;
				}
				holder.mRoundProgressBar1.setVisibility(View.VISIBLE);
				holder.mRoundProgressBar1.setMax(10);
				holder.mRoundProgressBar1.setProgress(0);
				ImageLoader.getInstance().displayImage(mBlog.photo.url, holder.imageView11,
						options, animateFirstListener, new ImageLoadingProgressListener() {
							@Override
							public void onProgressUpdate(String s, View view, final int i,
									final int i2) {

								if (null == context) {
									return;
								}

								context.runOnUiThread(new Runnable() {
									@Override
									public void run() {

										if (i == i2) {
											holder.mRoundProgressBar1.setVisibility(View.GONE);
										} else {
											holder.mRoundProgressBar1.setMax(i2);
											holder.mRoundProgressBar1.setProgress(i);
											holder.mRoundProgressBar1.setVisibility(View.VISIBLE);
										}
									}
								});
							}
						});
				holder.imageView14.setVisibility(View.GONE);
				holder.mTagedImage.setLayoutParams(new RelativeLayout.LayoutParams(
						(int) (with - HelperFunc.dip2px(20)), (int) (scale * (with - HelperFunc
								.dip2px(20)))));
				holder.imageView11.setLayoutParams(new RelativeLayout.LayoutParams(
						(int) (with - HelperFunc.dip2px(20)), (int) (scale * (with - HelperFunc
								.dip2px(20)))));
			} else {
				if (null != mBlog.video) {
					if (0 != mBlog.video.h && 0 != mBlog.video.w) {
						scale = (float) mBlog.video.h / (float) mBlog.video.w;
					}
					holder.mRoundProgressBar1.setVisibility(View.VISIBLE);
					holder.mRoundProgressBar1.setMax(10);
					holder.mRoundProgressBar1.setProgress(0);
					ImageLoader.getInstance().displayImage(mBlog.video.thumbnail_url,
							holder.imageView11, options, animateFirstListener,
							new ImageLoadingProgressListener() {
								@Override
								public void onProgressUpdate(String s, View view, final int i,
										final int i2) {
									if (null == context) {
										return;
									}

									context.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											if (i == i2) {
												holder.mRoundProgressBar1.setVisibility(View.GONE);
											} else {
												holder.mRoundProgressBar1.setMax(i2);
												holder.mRoundProgressBar1.setProgress(i);
												holder.mRoundProgressBar1
														.setVisibility(View.VISIBLE);
											}
										}
									});
								}
							});
					holder.imageView14.setVisibility(View.VISIBLE);
					holder.mTagedImage.setLayoutParams(new RelativeLayout.LayoutParams(
							(int) (with - HelperFunc.dip2px(20)), (int) (scale * (with - HelperFunc
									.dip2px(20)))));
					holder.imageView11.setLayoutParams(new RelativeLayout.LayoutParams(
							(int) (with - HelperFunc.dip2px(20)), (int) (scale * (with - HelperFunc
									.dip2px(20)))));
				} else {
					holder.imageView14.setVisibility(View.GONE);
				}
			}

			ImageView[] imageViews = new ImageView[] { holder.imageView1, holder.imageView2,
					holder.imageView3, holder.imageView4, holder.imageView5, holder.imageView6 };

			holder.imageView1.setVisibility(View.GONE);
			holder.imageView2.setVisibility(View.GONE);
			holder.imageView3.setVisibility(View.GONE);
			holder.imageView4.setVisibility(View.GONE);
			holder.imageView5.setVisibility(View.GONE);
			holder.imageView6.setVisibility(View.GONE);

			if (mBlog.likes.size() > 0) {
				int j = 0;
				for (User mUser : mBlog.likes) {
					if (null == mUser) {
						continue;
					}
					ImageLoader.getInstance()
							.displayImage(mUser.avatar_url, imageViews[j], options);
					imageViews[j].setVisibility(View.VISIBLE);
					imageViews[j].setOnClickListener(new GotoTaActivityOnClickListener(context,
							mUser.userId));
					j++;
					if (j == 6) {
						break;
					}
				}
				holder.mLinearLayout1.setVisibility(View.VISIBLE);
			} else {
				holder.mLinearLayout1.setVisibility(View.GONE);
			}

			holder.imagenum.setText(mBlog.likes_count + "");
			holder.imagenum.setGravity(Gravity.CENTER);
			holder.imagenum.setBackgroundColor(Color.parseColor("#E6E6E6"));
			holder.imagenum.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					startActivity(new Intent(MBlogAction.MBLOG_LIKEBYLIST_ACTION).putExtra(
							MBlogAction.MBLOG_ID, mBlog._id));
				}
			});

			ImageView[] imageComment = new ImageView[] { holder.comment_image1,
					holder.comment_image2, holder.comment_image3 };

			TextView[] comment_name = new TextView[] { holder.mTextView3, holder.mTextView4,
					holder.mTextView5 };

			TextView[] comment_time = new TextView[] { holder.mTextView6, holder.mTextView7,
					holder.mTextView8 };

			TextView[] comment_con = new TextView[] { holder.mTextView9, holder.mTextView10,
					holder.mTextView11 };

			View[] comment_view = new View[] { holder.mRelativeLayout1, holder.mRelativeLayout2,
					holder.mRelativeLayout3 };

			TextView[] comment_target = new TextView[] { holder.mTextView15, holder.mTextView16,
					holder.mTextView17 };

			View[] comment_target_view = new View[] { holder.mLinearLayout2, holder.mLinearLayout3,
					holder.mLinearLayout4 };

			for (int n = 0; n < comment_view.length; n++) {
				comment_view[n].setVisibility(View.GONE);
				comment_view[n].setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {

					}
				});
			}

			if (mBlog.comments_count > 0) {
				int j = 0;
				for (final Comment mComment : mBlog.comments) {
					if (null != mComment.owner) {
						ImageLoader.getInstance().displayImage(mComment.owner.avatar_url,
								imageComment[j], options);
						imageComment[j].setOnClickListener(new GotoTaActivityOnClickListener(
								context, mComment.owner.userId));
						comment_name[j].setText(mComment.owner.name);
						comment_time[j].setText(PrettyDateFormat
								.formatISO8601Time(mComment.created_at));
						comment_con[j].setText(ExpressionUtil.getInstance().strToSmiley(
								mComment.text));
						comment_view[j].setVisibility(View.VISIBLE);
						comment_view[j].setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								startActivity(new Intent(
										FusionAction.MBlogAction.MBLOGCOMMENT_ACTION)
										.putExtra(FusionAction.MBlogAction.MBLOG, mBlog)
										.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
										.putExtra(FusionAction.MBlogAction.MBLOG_OWNER,
												mComment.owner));
							}
						});
						if (null != mComment.target_user) {
							comment_target[j].setText(mComment.target_user.name);
							comment_target_view[j].setVisibility(View.VISIBLE);
						} else {
							comment_target_view[j].setVisibility(View.GONE);
						}
						j++;
						if (j == 3) {
							break;
						}
					}
				}
				holder.mLinearLayout.setVisibility(View.VISIBLE);
			} else {
				holder.mLinearLayout.setVisibility(View.GONE);
			}

			holder.mTagedImage.removeAllTag();
			for (MBlogTag mMBlogTag : mBlog.tags) {
				if (null != mMBlogTag.text)
					holder.mTagedImage.addTag(new Tag(mMBlogTag.id, mMBlogTag.text.name,
							mMBlogTag.x, mMBlogTag.y, Tag.LABEL_TYPE), false, context, scale,
							mMBlogTag.align, (int) (with - HelperFunc.dip2px(20)),
							(int) ((with - HelperFunc.dip2px(20)) * scale));
				if (null != mMBlogTag.user)
					holder.mTagedImage.addTag(new Tag(mMBlogTag.id, mMBlogTag.user.name,
							mMBlogTag.user.user_id, mMBlogTag.x, mMBlogTag.y, Tag.FIGURE_TYPE),
							false, context, scale, mMBlogTag.align, (int) (with - HelperFunc
									.dip2px(20)), (int) ((with - HelperFunc.dip2px(20)) * scale));
				if (null != mMBlogTag.location)
					holder.mTagedImage.addTag(new Tag(mMBlogTag.id, mMBlogTag.location.name,
							mMBlogTag.x, mMBlogTag.y, Tag.PLACE_TYPE), false, context, scale,
							mMBlogTag.align, (int) (with - HelperFunc.dip2px(20)),
							(int) ((with - HelperFunc.dip2px(20)) * scale));
			}

			holder.mTagedImage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (((TagedImage) view).getTagState()) {
						((TagedImage) view).hideTag();
					} else {
						((TagedImage) view).showTag();
					}
				}
			});

			if (mBlog.is_liked) {
				holder.mTextView12.setText("已赞");
				holder.mTextView12.setTextColor(getResources().getColor(R.color.color_439afb));
			} else {
				holder.mTextView12.setText("赞");
				holder.mTextView12.setTextColor(getResources().getColor(R.color.text_gray2));
			}
			holder.mTextView12.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if ("赞".equals(((TextView) view).getText().toString())) {
						likerequestId = mIMBlogLogic.isLikesMBlog(mBlog._id, true, mBlog);
					} else {
						likerequestId = mIMBlogLogic.isLikesMBlog(mBlog._id, false, mBlog);
					}
				}
			});

			if (mBlog.comments_count > 0) {
				holder.mTextView14.setText(String.format(getString(R.string.mblog_comment),
						mBlog.comments_count));
			}

			holder.mTextView13.setOnClickListener(new GotoMblogCommentActivityOnClickListener(
					context, mBlog));
			holder.mTextView14.setOnClickListener(new GotoMblogCommentActivityOnClickListener(
					context, mBlog));
			holder.imageView7.setOnClickListener(new GotoTaActivityOnClickListener(context,
					mBlog.owner.userId));

			holder.imageView12.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View view) {
					if (null != player && player.isPlaying()) {
						player.stop();
						player.release();
						holder.mRoundProgressBar.stopCartoom();
						player = null;
						isplayingId = null;
						((ImageView) view).setImageDrawable(getResources().getDrawable(
								R.drawable.home_audio_play));
						return;
					}
					view.setEnabled(false);
					view.setClickable(false);
					holder.mRelativeLayout8.setVisibility(View.VISIBLE);
					holder.imageView12.setVisibility(View.GONE);
					new Thread(new Runnable() {
						@Override
						public void run() {
							DownloadUtil.downFile(mBlog.audio_desc.url, mBlog._id,
									new DownloadUtil.DownloadListener() {
										@Override
										public void onComplete() {

											if (null == context) {
												return;
											}

											context.runOnUiThread(new Runnable() {

												@Override
												public void run() {
													holder.mRelativeLayout8
															.setVisibility(View.GONE);
													holder.imageView12.setVisibility(View.VISIBLE);
													view.setEnabled(true);
													view.setClickable(true);
													if (null == player) {
														player = new MediaPlayer();
													}
													player.setAudioStreamType(AudioManager.STREAM_MUSIC);
													String path = DownloadUtil.SDPATH + mBlog._id;
													try {
														player.setDataSource(path);
														player.prepare();
													} catch (Exception e) {
														e.printStackTrace();
													}
													player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
														@Override
														public void onCompletion(
																MediaPlayer mediaPlayer) {
															player.release();
															holder.mRoundProgressBar.stopCartoom();
															player = null;
															isplayingId = null;
															((ImageView) view)
																	.setImageDrawable(getResources()
																			.getDrawable(
																					R.drawable.home_audio_play));
														}
													});
													player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
														@Override
														public void onPrepared(
																MediaPlayer mediaPlayer) {
															mediaPlayer.start();
															holder.mRoundProgressBar.startCartoom(
																	0, mBlog.audio_desc.duration);
															holder.mRoundProgressBar
																	.setVisibility(View.VISIBLE);
															((ImageView) view)
																	.setImageDrawable(getResources()
																			.getDrawable(
																					R.drawable.home_audio_stop_02));
															isplayingId = mBlog._id;
														}
													});
												}
											});
										}

										@Override
										public void onFailure() {

											if (null == context) {
												return;
											}

											context.runOnUiThread(new Runnable() {

												@Override
												public void run() {
													holder.mRelativeLayout8
															.setVisibility(View.GONE);
													holder.imageView12.setVisibility(View.VISIBLE);
													view.setEnabled(true);
													view.setClickable(true);
												}
											});
										}
									});
						}
					}).start();
				}
			});

			holder.imageView13.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View view) {

					String imagePath = "";

					if (null != mBlog.photo) {

						imagePath = ShareUtil.getImagePath(mBlog.photo.url);
					}

					ShareUtil.showShare(mBlog.owner.userId, imagePath, mBlog.text_desc,
							new ShareOperation() {

								@Override
								public void deleteMBlog() {

									if (mBlogList.contains(mBlog)) {

										mBlogList.remove(mBlog);

										mIMBlogLogic.deleteMBlogById(mBlog._id);

										mHomePageListAdapter.notifyDataSetChanged();
									}
								}

								@Override
								public void toChat() {
									MessageInfo mMessageInfo = ForwardActivity
											.createMessageInfo(mBlog);
									startActivityForResult(new Intent(
											FusionAction.ChatAction.FORWARD_ACTION).putExtra(
											FusionAction.ChatAction.MESSGAE_INFO, mMessageInfo),
											ChatActivity.SHARE_TO_CHAT);
								}
							});
				}
			});

			holder.mTextView19.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					List<String> mlist = new ArrayList<String>();
					mlist.add(mBlog.owner.userId);
					pmUser = mBlog.owner;
					getGroupInfoFromDBId = mIChatLogic.getGroupInfoFromDB(mlist);
				}
			});

			holder.imageView14.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View view) {
					view.setEnabled(false);
					view.setClickable(false);
					holder.mRelativeLayout8.setVisibility(View.VISIBLE);
					holder.imageView14.setVisibility(View.GONE);
					new Thread(new Runnable() {
						@Override
						public void run() {
							DownloadUtil.downFile(mBlog.video.url, mBlog._id,
									new DownloadUtil.DownloadListener() {
										@Override
										public void onComplete() {
											if (null == context) {
												return;
											}
											context.runOnUiThread(new Runnable() {

												@Override
												public void run() {
													holder.mRelativeLayout8
															.setVisibility(View.GONE);
													holder.imageView14.setVisibility(View.VISIBLE);
													view.setEnabled(true);
													view.setClickable(true);
													String path = DownloadUtil.SDPATH + mBlog._id;
													videoTime = mBlog.video.duration;
													startActivity(new Intent(
															FusionAction.MBlogAction.VIDEOSHOW_ACTION)
															.putExtra(
																	FusionAction.MBlogAction.VIDEO_PATH,
																	path)
															.putExtra(
																	FusionAction.MBlogAction.VIDEO_TIME,
																	videoTime));
												}
											});
										}

										@Override
										public void onFailure() {
											if (null == context) {
												return;
											}
											context.runOnUiThread(new Runnable() {

												@Override
												public void run() {
													holder.mRelativeLayout8
															.setVisibility(View.GONE);
													holder.imageView14.setVisibility(View.VISIBLE);
													view.setEnabled(true);
													view.setClickable(true);
												}
											});
										}
									});
						}
					}).start();
				}
			});

			return view;
		}
	}

	@Override
	protected void initLogics() {
		mIMBlogLogic = (IMBlogLogic) getLogicByInterfaceClass(IMBlogLogic.class);
		mIAccountLogic = (IAccountLogic) getLogicByInterfaceClass(IAccountLogic.class);
		mIUserLogic = (IUserLogic) getLogicByInterfaceClass(IUserLogic.class);
		mIChatLogic = (IChatLogic) getLogicByInterfaceClass(IChatLogic.class);
	}

	@Override
	protected void handleStateMessage(Message msg) {

		mLocation = LocationManager.getInstance().getLocation();

		switch (msg.what) {
		case FusionMessageType.MBlogMessageType.GET_MBLOG_LIST_FROM_DB: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestDBId)
					&& requestDBId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mBlogList = (List<MBlog>) object.mObject;
					if (mBlogList.size() == 0) {
						mPullToRefreshListView.setVisibility(View.GONE);
					} else {
						mPullToRefreshListView.setVisibility(View.VISIBLE);
						if (null == mHomePageListAdapter) {
							mHomePageListAdapter = new HomePageListAdapter();
							mHomePageListAdapter.setList(mBlogList);
							listView.addHeaderView(mblogModelHead);
							listView.setAdapter(mHomePageListAdapter);
						} else {
							mHomePageListAdapter.setList(mBlogList);
							mHomePageListAdapter.notifyDataSetChanged();
						}
					}
				}
			}

			if (null != mPullToRefreshListView) {
				mPullToRefreshListView.onRefreshComplete();
			}
			break;
		}
		case FusionMessageType.MBlogMessageType.PULL_GET_MBLOG_LIST_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mBlogList = (List<MBlog>) object.mObject;
					if (mBlogList.size() == 0) {
						mPullToRefreshListView.setVisibility(View.GONE);
					} else {
						mPullToRefreshListView.setVisibility(View.VISIBLE);
						if (null == mHomePageListAdapter) {
							mHomePageListAdapter = new HomePageListAdapter();
							mHomePageListAdapter.setList(mBlogList);
							listView.addHeaderView(mblogModelHead);
							listView.setAdapter(mHomePageListAdapter);
						} else {
							mHomePageListAdapter.setList(mBlogList);
							mHomePageListAdapter.notifyDataSetChanged();
						}
					}
				}
			}
			if (null != mPullToRefreshListView) {
				mPullToRefreshListView.onRefreshComplete();
			}
			break;
		}
		case FusionMessageType.MBlogMessageType.PUSH_GET_MBLOG_LIST_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mBlogList.addAll((List<MBlog>) object.mObject);
					mHomePageListAdapter.setList(mBlogList);
					mHomePageListAdapter.notifyDataSetChanged();
				}
			}
			if (null != mPullToRefreshListView) {
				mPullToRefreshListView.onRefreshComplete();
			}
			break;
		}
		case FusionMessageType.MBlogMessageType.GET_MBLOG_LIST_FAIL: {
			if (null != mPullToRefreshListView) {
				mPullToRefreshListView.onRefreshComplete();
			}
			break;
		}
		case FusionMessageType.MBlogMessageType.IS_LIKES_ACTION_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(likerequestId)
					&& likerequestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					MBlog MBlog = (MBlog) object.mObject;
					if (MBlog.is_liked) {
						MBlog.is_liked = false;
						MBlog.likes_count = MBlog.likes_count - 1;
						List<User> mUserList = new ArrayList<User>();
						for (User mUser : MBlog.likes) {
							if (!mUser.userId.equals(mIAccountLogic.getMyUserInfoFromDB().userId)) {
								mUserList.add(mUser);
							}
						}
						MBlog.likes = mUserList;
					} else {
						MBlog.is_liked = true;
						MBlog.likes_count = MBlog.likes_count + 1;
						MBlog.likes.add(0, mIAccountLogic.getMyUserInfoFromDB());
					}
					mHomePageListAdapter.notifyDataSetChanged();
				}
			}
			break;
		}
		case FusionMessageType.MBlogMessageType.IS_LIKES_ACTION_FAIL: {
			break;
		}

		case FusionMessageType.ChatMessageType.GET_CHAT_GROUP_FROM_DB: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(getGroupInfoFromDBId)
					&& getGroupInfoFromDBId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					GroupInfo mGroupInfo = (GroupInfo) object.mObject;
					startActivity(new Intent(FusionAction.ChatAction.CHAT_ACTION).putExtra(
							FusionAction.ChatAction.GROUP_INFO, mGroupInfo));
				} else {
					CreateGroup mCreateGroup = new CreateGroup();
					mCreateGroup.name = pmUser.name;
					List<String> mlists = new ArrayList<String>();
					mlists.add(pmUser.userId);
					mCreateGroup.members = mlists;
					createGroupId = mIChatLogic.createGroup(mCreateGroup);
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
			break;
		}

		case FusionMessageType.ChatMessageType.CREATE_GROUP_FAIL: {
			break;
		}
		default:
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ChatActivity.SHARE_TO_CHAT: {
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(context, R.string.share_to_chat, Toast.LENGTH_LONG).show();
			}
		}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (null != player && player.isPlaying()) {
			player.stop();
			player.release();
			player = null;
			isplayingId = null;
			return;
		}

		LocalBroadcastManager.getInstance(context).unregisterReceiver(mBroadcastReceiver);
	}
}
