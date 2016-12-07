package com.theone.sns.ui.mblog.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionAction.MBlogAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.common.FusionMessageType.MBlogMessageType;
import com.theone.sns.component.http.UIObject;
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
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.base.dialog.TheOneAlertDialog;
import com.theone.sns.ui.chat.EmojiPagerAdapter;
import com.theone.sns.ui.chat.activity.ChatActivity;
import com.theone.sns.ui.chat.activity.ForwardActivity;
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
import com.theone.sns.util.uiwidget.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.ShareOperation;
import cn.sharesdk.ShareUtil;

/**
 * Created by zhangwenhai on 2014/9/23.
 */
public class MBlogCommentActivity extends IphoneTitleActivity {
	private PullToRefreshListView mPullToRefreshListView;
	private ListView listView;
	private IMBlogLogic mIMBlogLogic;
	private IUserLogic mIUserLogic;
	private View mblogCommentHead;
	private int with;
	private TagedImage addLabelImage;
	private ImageView imageImageView;
	private ImageView ImageAvter;
	private TextView name;
	private ImageView type;
	private ImageView xing;
	private ImageView xingxing;
	private TextView distance;
	private TextView time;
	private TextView like;
	private ImageView share;
	private Button followBtn;
	private ImageView image1ImageView;
	private ImageView image2ImageView;
	private ImageView image3ImageView;
	private ImageView image4ImageView;
	private ImageView image5ImageView;
	private ImageView image6ImageView;
	private TextView image7TextView;
	private CommentListAdapter mCommentListAdapter;
	private String requestId;
	private String commentsRequestId;
	private MBlog mBlog = null;
	private String likerequestId;
	private IAccountLogic mIAccountLogic;
	private List<Comment> commentList = new ArrayList<Comment>();
	private LayoutInflater inflater;
	private String mBlogId;
	private EditText textEditText;
	private Button sendBtn;
	private String sendCommentsRequestId;
	private IphoneStyleAlertDialogBuilder m_cantAccessDialog;
	private LocalLocation mLocation;
	private ImageView audioView;
	private String targetUserId;
	private String showTarget = "!@#$%^";
	private MediaPlayer player;
	private RoundProgressBar soundPlaybar;
	private String isplayingId;
	private TextView text1;
	private LinearLayout text1View;
	private ImageView videoView;
	private int videoTime;
	private ImageView showEmoji;
	private ViewPager m_pager;
	private CirclePageIndicator m_titleIndicator;
	private EmojiPagerAdapter m_adapter;
	private LinearLayout emojiPagerView;

	private LinearLayout.LayoutParams lp;
	private TextView pm;
	private IChatLogic mIChatLogic;
	private String getGroupInfoFromDBId;
	private String createGroupId;
	private RelativeLayout downloadProgress;
	private String deleteCommentId;
	private String deleteMBlogId;
	private RoundProgressBar progressBar1;
	private User mUser;
	private ShareOperation mBlogOperation = new ShareOperation() {

		@Override
		public void toChat() {
			MessageInfo mMessageInfo = ForwardActivity.createMessageInfo(mBlog);
			startActivityForResult(new Intent(FusionAction.ChatAction.FORWARD_ACTION).putExtra(
					FusionAction.ChatAction.MESSGAE_INFO, mMessageInfo), ChatActivity.SHARE_TO_CHAT);
		}
	};

	@Override
	protected void initLogics() {
		mIMBlogLogic = (IMBlogLogic) getLogicByInterfaceClass(IMBlogLogic.class);
		mIAccountLogic = (IAccountLogic) getLogicByInterfaceClass(IAccountLogic.class);
		mIUserLogic = (IUserLogic) getLogicByInterfaceClass(IUserLogic.class);
		mIChatLogic = (IChatLogic) getLogicByInterfaceClass(IChatLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.mblog_comment_main);

		getView();

		setView();

		setListener();
	}

	private void getView() {
		with = getResources().getDisplayMetrics().widthPixels;

		lp = new LinearLayout.LayoutParams((int) ((with - HelperFunc.dip2px(80)) / 7),
				(int) ((with - HelperFunc.dip2px(80)) / 7));
		lp.setMargins((int) HelperFunc.dip2px(10), 0, 0, 0);

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.mblog_list);
		listView = mPullToRefreshListView.getRefreshableView();

		mBlog = (MBlog) getIntent().getSerializableExtra(FusionAction.MBlogAction.MBLOG);
		if (null == mBlog) {
			mBlogId = getIntent().getStringExtra(FusionAction.MBlogAction.MBLOG_ID);
			if (TextUtils.isEmpty(mBlogId))
				finish();
		}

		textEditText = (EditText) findViewById(R.id.text);
		sendBtn = (Button) findViewById(R.id.send);
		showEmoji = (ImageView) findViewById(R.id.show_emoji);
		emojiPagerView = (LinearLayout) findViewById(R.id.emoji_pager_view);
		m_pager = (ViewPager) findViewById(R.id.emoji_pager);
		m_titleIndicator = (CirclePageIndicator) findViewById(R.id.emoji_indicator);

		mblogCommentHead = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.mblog_comment_head, null);
		addLabelImage = (TagedImage) mblogCommentHead.findViewById(R.id.add_label_image);
		audioView = (ImageView) mblogCommentHead.findViewById(R.id.audio_view);
		videoView = (ImageView) mblogCommentHead.findViewById(R.id.video_view);
		soundPlaybar = (RoundProgressBar) mblogCommentHead.findViewById(R.id.sound_playbar);
		downloadProgress = (RelativeLayout) mblogCommentHead.findViewById(R.id.download_progress);
		imageImageView = (ImageView) mblogCommentHead.findViewById(R.id.image);
		ImageAvter = (ImageView) mblogCommentHead.findViewById(R.id.image_avter);
		name = (TextView) mblogCommentHead.findViewById(R.id.name);
		type = (ImageView) mblogCommentHead.findViewById(R.id.type);
		xing = (ImageView) mblogCommentHead.findViewById(R.id.xing);
		xingxing = (ImageView) mblogCommentHead.findViewById(R.id.xingxing);
		distance = (TextView) mblogCommentHead.findViewById(R.id.distance);
		time = (TextView) mblogCommentHead.findViewById(R.id.time);
		like = (TextView) mblogCommentHead.findViewById(R.id.like);
		pm = (TextView) mblogCommentHead.findViewById(R.id.pm);
		share = (ImageView) mblogCommentHead.findViewById(R.id.share);
		followBtn = (Button) mblogCommentHead.findViewById(R.id.follow_btn);
		progressBar1 = (RoundProgressBar) mblogCommentHead.findViewById(R.id.progressBar);

		text1 = (TextView) mblogCommentHead.findViewById(R.id.text1);
		text1View = (LinearLayout) mblogCommentHead.findViewById(R.id.text1_view);

		addLabelImage.setLayoutParams(new RelativeLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, (int) (with - HelperFunc.dip2px(20))));
		imageImageView.setLayoutParams(new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT, (int) (with - HelperFunc.dip2px(20))));

		image1ImageView = (ImageView) mblogCommentHead.findViewById(R.id.image_1);
		image2ImageView = (ImageView) mblogCommentHead.findViewById(R.id.image_2);
		image3ImageView = (ImageView) mblogCommentHead.findViewById(R.id.image_3);
		image4ImageView = (ImageView) mblogCommentHead.findViewById(R.id.image_4);
		image5ImageView = (ImageView) mblogCommentHead.findViewById(R.id.image_5);
		image6ImageView = (ImageView) mblogCommentHead.findViewById(R.id.image_6);
		image7TextView = (TextView) mblogCommentHead.findViewById(R.id.image_7);

		image1ImageView.setLayoutParams(lp);
		image2ImageView.setLayoutParams(lp);
		image3ImageView.setLayoutParams(lp);
		image4ImageView.setLayoutParams(lp);
		image5ImageView.setLayoutParams(lp);
		image6ImageView.setLayoutParams(lp);
		image7TextView.setLayoutParams(lp);
	}

	private void setView() {
		setTitle(R.string.mblog_details);
		setLeftButton(R.drawable.icon_back, false, false);

		mLocation = LocationManager.getInstance().getLocation();

		m_adapter = new EmojiPagerAdapter(getApplicationContext(),
				new EmojiPagerAdapter.EmojiOnClickCallback() {
					@Override
					public void OnClick(int i) {
						CharSequence mCharSequence = ExpressionUtil.getInstance().smileyToStr(
								ExpressionUtil.DEFAULT_SMILEY_RES_IDS[i]);
						CharSequence ret = textEditText.getText().toString() + mCharSequence;
						textEditText.setText(ExpressionUtil.getInstance().strToSmiley(ret));
						textEditText.setSelection(textEditText.getText().toString().length());
					}
				}, options);
		m_pager.setAdapter(m_adapter);
		m_pager.setPageMargin((int) HelperFunc.dip2px(10));
		m_pager.setVerticalFadingEdgeEnabled(false);
		m_pager.setVerticalScrollBarEnabled(false);
		m_titleIndicator.setPageColor(0xff888888);
		m_titleIndicator.setFillColor(0xff646464);
		m_titleIndicator.setViewPager(m_pager);
		m_titleIndicator.setCurrentItem(0);

		if (null != mBlog)
			setViewMBlog();
		listView.addHeaderView(mblogCommentHead);
		mCommentListAdapter = new CommentListAdapter();
		listView.setAdapter(mCommentListAdapter);

		if (null != mBlog) {
			requestId = mIMBlogLogic.getMBlogById(mBlog._id);
			commentsRequestId = mIMBlogLogic.getCommentsListByMBlogId(mBlog._id, null,
					FusionCode.CommonColumnsValue.COUNT_VALUE);
		} else {
			requestId = mIMBlogLogic.getMBlogById(mBlogId);
			commentsRequestId = mIMBlogLogic.getCommentsListByMBlogId(mBlogId, null,
					FusionCode.CommonColumnsValue.COUNT_VALUE);
		}

		if (null != getIntent().getSerializableExtra(FusionAction.MBlogAction.MBLOG_OWNER)) {
			mUser = (User) getIntent().getSerializableExtra(FusionAction.MBlogAction.MBLOG_OWNER);
			showTarget = "@" + mUser.name + " ";
			textEditText.setText(showTarget);
			textEditText.setSelection(textEditText.length());
			textEditText.requestFocus();
			targetUserId = mUser.userId;
			listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			// showIME(textEditText);
		}

	}

	private void setListener() {

		followBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (null == mBlog) {
					return;
				}

				FusionConfig.clickFollowButton(mIUserLogic, mBlog.owner, followBtn);
			}
		});

		addLabelImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (((TagedImage) view).getTagState()) {
					((TagedImage) view).hideTag();
				} else {
					((TagedImage) view).showTag();
				}
			}
		});

		like.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (null == mBlog) {
					return;
				}

				if ("赞".equals(((TextView) view).getText().toString())) {
					likerequestId = mIMBlogLogic.isLikesMBlog(mBlog._id, true, mBlog);
				} else {
					likerequestId = mIMBlogLogic.isLikesMBlog(mBlog._id, false, mBlog);
				}
			}
		});

		share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				String imagePath = "";

				if (null == mBlog) {
					return;
				}

				mBlogOperation.mHandler = getHandler();

				mBlogOperation.mblog_id = mBlog._id;

				if (null != mBlog.photo) {

					imagePath = ShareUtil.getImagePath(mBlog.photo.url);
				}

				ShareUtil.showShare(mBlog.owner.userId, imagePath, mBlog.text_desc, mBlogOperation);
			}
		});

		showEmoji.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				textEditText.findFocus();
				if (emojiPagerView.isShown()) {
					emojiPagerView.setVisibility(View.GONE);
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm.isActive(textEditText)) {
						if (getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
							imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
									InputMethodManager.HIDE_NOT_ALWAYS);
						}
					}
				} else {
					emojiPagerView.setVisibility(View.VISIBLE);
					// 隐藏输入法界面
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
			}
		});
		textEditText.findFocus();

		mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (null != mBlog) {
					commentsRequestId = mIMBlogLogic.getCommentsListByMBlogId(mBlog._id, null,
							FusionCode.CommonColumnsValue.COUNT_VALUE);
				} else {
					commentsRequestId = mIMBlogLogic.getCommentsListByMBlogId(mBlogId, null,
							FusionCode.CommonColumnsValue.COUNT_VALUE);
				}
			}

			@Override
			public void onAddMore() {
				if (commentList.size() > 0) {
					if (null != mBlog) {
						commentsRequestId = mIMBlogLogic.getCommentsListByMBlogId(mBlog._id,
								commentList.get(commentList.size() - 1)._id,
								FusionCode.CommonColumnsValue.COUNT_VALUE);
					} else {
						commentsRequestId = mIMBlogLogic.getCommentsListByMBlogId(mBlogId,
								commentList.get(commentList.size() - 1)._id,
								FusionCode.CommonColumnsValue.COUNT_VALUE);
					}
				} else {
					if (null != mBlog) {
						commentsRequestId = mIMBlogLogic.getCommentsListByMBlogId(mBlog._id, null,
								FusionCode.CommonColumnsValue.COUNT_VALUE);
					} else {
						commentsRequestId = mIMBlogLogic.getCommentsListByMBlogId(mBlogId, null,
								FusionCode.CommonColumnsValue.COUNT_VALUE);
					}
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (emojiPagerView.isShown()) {
			emojiPagerView.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
	}

	class CommentListAdapter extends BaseAdapter {

		private List<Comment> mCommentList = new ArrayList<Comment>();

		@Override
		public int getCount() {
			return mCommentList.size();
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
		public View getView(final int i, View view, ViewGroup viewGroup) {
			final ImageLoaderViewHolder holder;
			if (view == null) {
				if (null == inflater) {
					inflater = LayoutInflater.from(getApplicationContext());
				}
				view = inflater.inflate(R.layout.comment_list_item, null);
				holder = new ImageLoaderViewHolder();
				holder.imageView = (ImageView) view.findViewById(R.id.comment_image);
				holder.mTextView = (TextView) view.findViewById(R.id.comment_name);
				holder.mTextView1 = (TextView) view.findViewById(R.id.comment_time);
				holder.mTextView2 = (TextView) view.findViewById(R.id.comment_con);
				holder.mLinearLayout = (LinearLayout) view.findViewById(R.id.resend);
				holder.mTextView3 = (TextView) view.findViewById(R.id.target_name);
				holder.imageView.setLayoutParams(lp);
				view.setTag(holder);
			} else {
				holder = (ImageLoaderViewHolder) view.getTag();
			}

			ImageLoader.getInstance().displayImage(mCommentList.get(i).owner.avatar_url,
					holder.imageView, optionsForUserIcon);
			final String uid = mCommentList.get(i).owner.userId;
			final String name = mCommentList.get(i).owner.name;

			if (null == mCommentList.get(i).target_user) {
				holder.mTextView.setText(name);
				holder.mLinearLayout.setVisibility(View.GONE);
			} else {
				holder.mTextView.setText(name);
				holder.mLinearLayout.setVisibility(View.VISIBLE);
				holder.mTextView3.setText(mCommentList.get(i).target_user.name);
			}

			holder.mTextView1
					.setText(PrettyDateFormat.formatISO8601Time(mCommentList.get(i).created_at));
			holder.mTextView2.setText(ExpressionUtil.getInstance().strToSmiley(
					mCommentList.get(i).text));

			holder.imageView.setOnClickListener(new GotoTaActivityOnClickListener(
					getApplicationContext(), uid));

			view.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					if (FusionConfig.getInstance().getUserId().equals(uid)) {
						new TheOneAlertDialog.Builder(MBlogCommentActivity.this)
								.setMessage(R.string.confirmation_delete_1)
								.setPositiveButton(R.string.delete,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
												deleteCommentId = mIMBlogLogic.deleteComment(
														mBlog._id, mCommentList.get(i)._id);
											}
										})
								.setNegativeButton(R.string.Cancel,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
											}
										}).show();
					}
					return false;
				}
			});

			view.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if (!FusionConfig.getInstance().getUserId().equals(uid)) {
						showTarget = "@" + name + " ";
						if (!textEditText.getText().toString().startsWith(showTarget)) {
							String s = textEditText.getText().toString();
							textEditText.setText(showTarget + s);
							textEditText.setSelection(textEditText.length());
							textEditText.requestFocus();
							targetUserId = uid;
							return;
						}
					}
					return;
				}
			});

			return view;
		}

		public void setmCommentList(List<Comment> mCommentList) {
			this.mCommentList = mCommentList;
		}
	}

	private void setViewMBlog() {
		ImageLoader.getInstance().displayImage(mBlog.owner.avatar_url, ImageAvter,
				optionsForUserIcon);

		if (TextUtils.isEmpty(mBlog.text_desc)) {
			text1View.setVisibility(View.GONE);
		} else {
			text1.setText(mBlog.text_desc);
			text1View.setVisibility(View.VISIBLE);
			text1.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					HelperFunc.copy(mBlog.text_desc, getApplicationContext());
					Toast.makeText(getApplicationContext(), R.string.copy_mblog, Toast.LENGTH_LONG)
							.show();
					return false;
				}
			});
		}

		if (null != mBlog.audio_desc) {
			audioView.setVisibility(View.VISIBLE);
		} else {
			audioView.setVisibility(View.GONE);
		}

		downloadProgress.setVisibility(View.GONE);

		if (null == isplayingId) {
			soundPlaybar.setVisibility(View.VISIBLE);
		} else if (isplayingId.equals(mBlog._id)) {
			soundPlaybar.setVisibility(View.VISIBLE);
		} else {
			soundPlaybar.setVisibility(View.GONE);
		}

		name.setText(mBlog.owner.name);

		if (null != mBlog && FusionConfig.getInstance().getUserId().equals(mBlog.owner.userId)) {
			followBtn.setVisibility(View.GONE);
		} else {
			followBtn.setVisibility(View.VISIBLE);

			FusionConfig.showFollowButton(mBlog.owner, followBtn);
		}

		if (FusionCode.Role.H.equals(mBlog.owner.role)) {
			type.setImageDrawable(getResources().getDrawable(R.drawable.home_h_icon));
		} else if (FusionCode.Role.T.equals(mBlog.owner.role)) {
			type.setImageDrawable(getResources().getDrawable(R.drawable.home_t_icon));
		} else if (FusionCode.Role.P.equals(mBlog.owner.role)) {
			type.setImageDrawable(getResources().getDrawable(R.drawable.home_p_icon));
		} else if (FusionCode.Role.MH.equals(mBlog.owner.role)) {
			type.setImageDrawable(getResources().getDrawable(R.drawable.home_0_icon));
		} else if (FusionCode.Role.MT.equals(mBlog.owner.role)) {
			type.setImageDrawable(getResources().getDrawable(R.drawable.home_1_icon));
		} else if (FusionCode.Role.MP.equals(mBlog.owner.role)) {
			type.setImageDrawable(getResources().getDrawable(R.drawable.home_0_5_icon));
		}

		if (mBlog.owner.marriage) {
			xing.setVisibility(View.VISIBLE);
		} else {
			xing.setVisibility(View.GONE);
		}

		if (FusionConfig.getInstance().getUserId().equals(mBlog.owner.userId)) {
			pm.setVisibility(View.GONE);
		} else {
			pm.setVisibility(View.VISIBLE);
		}

		if (mBlog.owner.is_starring) {
			xingxing.setVisibility(View.VISIBLE);
		} else {
			xingxing.setVisibility(View.GONE);
		}

		if (mBlog.is_liked) {
			like.setText("已赞");
			like.setTextColor(getResources().getColor(R.color.color_439afb));
		} else {
			like.setText("赞");
			like.setTextColor(getResources().getColor(R.color.text_gray2));
		}

		List<String> mBlogLocation = mBlog.location;

		if (null != mBlogLocation && mBlogLocation.size() == 2 && null != mLocation) {

			distance.setVisibility(View.VISIBLE);

			distance.setText(StringUtil.getDistance(Double.valueOf(mLocation.longitude),
					Double.valueOf(mLocation.latitude), Double.valueOf(mBlogLocation.get(0)),
					Double.valueOf(mBlogLocation.get(1))) + getString(R.string.distance_km));
		} else {
			distance.setVisibility(View.GONE);
		}

		time.setText(PrettyDateFormat.formatISO8601Time(mBlog.created_at));

		ImageView[] imageViews = new ImageView[] { image1ImageView, image2ImageView,
				image3ImageView, image4ImageView, image5ImageView, image6ImageView };

		image1ImageView.setVisibility(View.GONE);
		image2ImageView.setVisibility(View.GONE);
		image3ImageView.setVisibility(View.GONE);
		image4ImageView.setVisibility(View.GONE);
		image5ImageView.setVisibility(View.GONE);
		image6ImageView.setVisibility(View.GONE);

		if (mBlog.likes.size() > 0) {
			int j = 0;
			for (User mUser : mBlog.likes) {
				ImageLoader.getInstance().displayImage(mUser.avatar_url, imageViews[j], options);
				imageViews[j].setVisibility(View.VISIBLE);
				imageViews[j].setOnClickListener(new GotoTaActivityOnClickListener(
						getApplicationContext(), mUser.userId));
				j++;
				if (j == 6) {
					break;
				}
			}
			mblogCommentHead.findViewById(R.id.like_view).setVisibility(View.VISIBLE);
		} else {
			mblogCommentHead.findViewById(R.id.like_view).setVisibility(View.GONE);
		}

		image7TextView.setText(mBlog.likes_count + "");
		image7TextView.setGravity(Gravity.CENTER);
		image7TextView.setBackgroundColor(Color.parseColor("#E6E6E6"));
		image7TextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(MBlogAction.MBLOG_LIKEBYLIST_ACTION).putExtra(
						MBlogAction.MBLOG_ID, mBlog._id));
			}
		});

		float scale = 1.0f;

		if (null != mBlog.photo) {
			if (0 != mBlog.photo.h && 0 != mBlog.photo.w) {
				scale = (float) mBlog.photo.h / (float) mBlog.photo.w;
			}
			ImageLoader.getInstance().displayImage(mBlog.photo.url, imageImageView, options,
					new ImageLoadingListener() {
						@Override
						public void onLoadingStarted(String s, View view) {
							progressBar1.setVisibility(View.VISIBLE);
							progressBar1.setMax(10);
							progressBar1.setProgress(0);
						}

						@Override
						public void onLoadingFailed(String s, View view, FailReason failReason) {

						}

						@Override
						public void onLoadingComplete(String s, View view, Bitmap bitmap) {

						}

						@Override
						public void onLoadingCancelled(String s, View view) {

						}
					}, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String s, View view, final int i, final int i2) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (i == i2) {
										progressBar1.setVisibility(View.GONE);
									} else {
										progressBar1.setMax(i2);
										progressBar1.setProgress(i);
										progressBar1.setVisibility(View.VISIBLE);
									}
								}
							});
						}
					});
			videoView.setVisibility(View.GONE);
			addLabelImage.setLayoutParams(new RelativeLayout.LayoutParams((int) (with - HelperFunc
					.dip2px(20)), (int) (scale * (with - HelperFunc.dip2px(20)))));
			imageImageView.setLayoutParams(new RelativeLayout.LayoutParams((int) (with - HelperFunc
					.dip2px(20)), (int) (scale * (with - HelperFunc.dip2px(20)))));
		} else {
			if (null != mBlog.video) {
				if (0 != mBlog.video.h && 0 != mBlog.video.w) {
					scale = (float) mBlog.video.h / (float) mBlog.video.w;
				}
				ImageLoader.getInstance().displayImage(mBlog.video.thumbnail_url, imageImageView,
						options, new ImageLoadingListener() {
							@Override
							public void onLoadingStarted(String s, View view) {
								progressBar1.setVisibility(View.VISIBLE);
								progressBar1.setMax(10);
								progressBar1.setProgress(0);
							}

							@Override
							public void onLoadingFailed(String s, View view, FailReason failReason) {

							}

							@Override
							public void onLoadingComplete(String s, View view, Bitmap bitmap) {

							}

							@Override
							public void onLoadingCancelled(String s, View view) {

							}
						}, new ImageLoadingProgressListener() {
							@Override
							public void onProgressUpdate(String s, View view, final int i,
									final int i2) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										if (i == i2) {
											progressBar1.setVisibility(View.GONE);
										} else {
											progressBar1.setMax(i2);
											progressBar1.setProgress(i);
											progressBar1.setVisibility(View.VISIBLE);
										}
									}
								});
							}
						});
				videoView.setVisibility(View.VISIBLE);
				addLabelImage.setLayoutParams(new RelativeLayout.LayoutParams(
						(int) (with - HelperFunc.dip2px(20)), (int) (scale * (with - HelperFunc
								.dip2px(20)))));
				imageImageView.setLayoutParams(new RelativeLayout.LayoutParams(
						(int) (with - HelperFunc.dip2px(20)), (int) (scale * (with - HelperFunc
								.dip2px(20)))));
			} else {
				videoView.setVisibility(View.GONE);
			}
		}

		addLabelImage.removeAllTag();
		for (MBlogTag mMBlogTag : mBlog.tags) {
			if (null != mMBlogTag.text)
				addLabelImage.addTag(new Tag(mMBlogTag.id, mMBlogTag.text.name, mMBlogTag.x,
						mMBlogTag.y, Tag.LABEL_TYPE), false, this, scale, mMBlogTag.align,
						(int) (with - HelperFunc.dip2px(20)),
						(int) ((with - HelperFunc.dip2px(20)) * scale));
			if (null != mMBlogTag.user)
				addLabelImage.addTag(new Tag(mMBlogTag.id, mMBlogTag.user.name,
						mMBlogTag.user.user_id, mMBlogTag.x, mMBlogTag.y, Tag.FIGURE_TYPE), false,
						this, scale, mMBlogTag.align, (int) (with - HelperFunc.dip2px(20)),
						(int) ((with - HelperFunc.dip2px(20)) * scale));
			if (null != mMBlogTag.location)
				addLabelImage.addTag(new Tag(mMBlogTag.id, mMBlogTag.location.name, mMBlogTag.x,
						mMBlogTag.y, Tag.PLACE_TYPE), false, this, scale, mMBlogTag.align,
						(int) (with - HelperFunc.dip2px(20)),
						(int) ((with - HelperFunc.dip2px(20)) * scale));
		}

		ImageAvter.setOnClickListener(new GotoTaActivityOnClickListener(getApplicationContext(),
				mBlog.owner.userId));

		pm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				List<String> mlist = new ArrayList<String>();
				mlist.add(mBlog.owner.userId);
				getGroupInfoFromDBId = mIChatLogic.getGroupInfoFromDB(mlist);
			}
		});

		audioView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				if (null != player && player.isPlaying()) {
					player.stop();
					player.release();
					soundPlaybar.stopCartoom();
					player = null;
					isplayingId = null;
					((ImageView) view).setImageDrawable(getResources().getDrawable(
							R.drawable.home_audio_play));
					return;
				}
				view.setEnabled(false);
				view.setClickable(false);
				downloadProgress.setVisibility(View.VISIBLE);
				audioView.setVisibility(View.GONE);
				new Thread(new Runnable() {
					@Override
					public void run() {
						DownloadUtil.downFile(mBlog.audio_desc.url, mBlog._id,
								new DownloadUtil.DownloadListener() {
									@Override
									public void onComplete() {

										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												downloadProgress.setVisibility(View.GONE);
												audioView.setVisibility(View.VISIBLE);
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
													public void onCompletion(MediaPlayer mediaPlayer) {
														player.release();
														soundPlaybar.stopCartoom();
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
													public void onPrepared(MediaPlayer mediaPlayer) {
														mediaPlayer.start();
														soundPlaybar.startCartoom(0,
																mBlog.audio_desc.duration);
														soundPlaybar.setVisibility(View.VISIBLE);
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

										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												downloadProgress.setVisibility(View.GONE);
												audioView.setVisibility(View.VISIBLE);
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

		videoView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				view.setEnabled(false);
				view.setClickable(false);
				downloadProgress.setVisibility(View.VISIBLE);
				videoView.setVisibility(View.GONE);
				new Thread(new Runnable() {
					@Override
					public void run() {
						DownloadUtil.downFile(mBlog.video.url, mBlog._id,
								new DownloadUtil.DownloadListener() {
									@Override
									public void onComplete() {

										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												downloadProgress.setVisibility(View.GONE);
												videoView.setVisibility(View.VISIBLE);
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

										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												downloadProgress.setVisibility(View.GONE);
												videoView.setVisibility(View.VISIBLE);
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

		sendBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!TextUtils.isEmpty(textEditText.getText().toString()))
					if (textEditText.getText().toString().startsWith(showTarget)) {
						String s = textEditText.getText().toString();
						s = s.substring(s.indexOf(showTarget) + showTarget.length());
						sendCommentsRequestId = mIMBlogLogic.publishComment(mBlog._id,
								targetUserId, s);
					} else {
						sendCommentsRequestId = mIMBlogLogic.publishComment(mBlog._id, null,
								textEditText.getText().toString());
					}
			}
		});
	}

	@Override
	protected void handleStateMessage(Message msg) {
		switch (msg.what) {
		case FusionMessageType.MBlogMessageType.GET_MBLOG_BY_ID_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					mBlog = (MBlog) object.mObject;
					setViewMBlog();
				} else {
					showToast(R.string.get_mblog_fail);
					finish();
				}
			}
			break;
		}
		case FusionMessageType.MBlogMessageType.GET_MBLOG_BY_ID_FAIL: {
			showToast(R.string.get_mblog_fail);
			finish();
			if (null != mPullToRefreshListView) {
				mPullToRefreshListView.onRefreshComplete();
			}
			break;
		}
		case FusionMessageType.MBlogMessageType.PULL_MBLOG_COMMENTS_LIST_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(commentsRequestId)
					&& commentsRequestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					commentList = (List<Comment>) object.mObject;
					mCommentListAdapter.setmCommentList(commentList);
					mCommentListAdapter.notifyDataSetChanged();
				}
			}
			if (null != mPullToRefreshListView) {
				mPullToRefreshListView.onRefreshComplete();
			}
			break;
		}

		case FusionMessageType.MBlogMessageType.PUSH_MBLOG_COMMENTS_LIST_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(commentsRequestId)
					&& commentsRequestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					commentList.addAll((List<Comment>) object.mObject);
					mCommentListAdapter.setmCommentList(commentList);
					listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
					mCommentListAdapter.notifyDataSetChanged();
				}
			}
			if (null != mPullToRefreshListView) {
				mPullToRefreshListView.onRefreshComplete();
			}
			break;
		}

		case FusionMessageType.MBlogMessageType.GET_MBLOG_COMMENTS_LIST_FAIL: {
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
					setViewMBlog();
				}
			}
			break;
		}
		case FusionMessageType.MBlogMessageType.IS_LIKES_ACTION_FAIL: {
			break;
		}
		case FusionMessageType.MBlogMessageType.PUBLISH_MBLOG_COMMENTS_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(sendCommentsRequestId)
					&& sendCommentsRequestId.equals(object.mLocalRequestId)) {
				if (null == mBlog) {
					return;
				}
				commentsRequestId = mIMBlogLogic.getCommentsListByMBlogId(mBlog._id, null,
						FusionCode.CommonColumnsValue.COUNT_VALUE);
				textEditText.setText("");
				hideInputWindow(textEditText);
				if (emojiPagerView.isShown()) {
					emojiPagerView.setVisibility(View.GONE);
				}
			}
			break;
		}
		case FusionMessageType.MBlogMessageType.PUBLISH_MBLOG_COMMENTS_FAIL: {
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
					if (null == mBlog) {
						return;
					}
					CreateGroup mCreateGroup = new CreateGroup();
					mCreateGroup.name = mBlog.owner.name;
					List<String> mlists = new ArrayList<String>();
					mlists.add(mBlog.owner.userId);
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

		case FusionMessageType.MBlogMessageType.DELETE_MBLOG_COMMENTS_SUCCESS: {
			if (null == mBlog) {
				return;
			}
			commentsRequestId = mIMBlogLogic.getCommentsListByMBlogId(mBlog._id, null,
					FusionCode.CommonColumnsValue.COUNT_VALUE);
			break;
		}

		case FusionMessageType.MBlogMessageType.DELETE_MBLOG_COMMENTS_FAIL: {
			break;
		}
		case MBlogMessageType.DELETE_MBLOG_BY_ID: {

			String mblogId = (String) msg.obj;

			if (null == mBlog) {
				return;
			}

			if (!TextUtils.isEmpty(mblogId) && mblogId.equals(mBlog._id)) {

				showLoadingDialog();

				deleteMBlogId = mIMBlogLogic.deleteMBlogById(mBlog._id);
			}

			break;
		}
		case MBlogMessageType.DELETE_MBLOG_BY_ID_SUCCESS: {
			hideLoadingDialog();
			finish();
			break;
		}
		case MBlogMessageType.DELETE_MBLOG_BY_ID_FAIL: {
			hideLoadingDialog();
			showToast(R.string.mblog_delete_fail);
			break;
		}
		default:
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ChatActivity.SHARE_TO_CHAT: {
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(this, R.string.share_to_chat, Toast.LENGTH_LONG).show();
			}
		}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (null != player && player.isPlaying()) {
			player.stop();
			player.release();
			player = null;
			isplayingId = null;
			return;
		}
	}

	@Override
	public void finish() {
		hideInputWindow(textEditText);
		super.finish();

	}
}
