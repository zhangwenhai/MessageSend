package com.theone.sns.ui.me;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
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
import com.theone.sns.component.location.gps.LocalLocation;
import com.theone.sns.component.location.gps.LocationManager;
import com.theone.sns.logic.mblog.IMBlogLogic;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.logic.model.mblog.MBlog;
import com.theone.sns.logic.model.mblog.MBlogTag;
import com.theone.sns.logic.model.mblog.base.Comment;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.ui.chat.activity.ChatActivity;
import com.theone.sns.ui.chat.activity.ForwardActivity;
import com.theone.sns.ui.mblog.GotoMblogCommentActivityOnClickListener;
import com.theone.sns.ui.publish.Tag;
import com.theone.sns.ui.publish.TagedImage;
import com.theone.sns.util.DownloadUtil;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.PrettyDateFormat;
import com.theone.sns.util.RoundProgressBar;
import com.theone.sns.util.StringUtil;
import com.theone.sns.util.uiwidget.ExpressionUtil;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.sharesdk.ShareOperation;
import cn.sharesdk.ShareUtil;

/**
 * Created by zhangwenhai on 2014/9/13.
 */
public class MBlogListAdapterTab2 extends BaseAdapter {

	private final Activity mContext;

	private final int with;

	private final DisplayImageOptions options;

	private final LocalLocation mLocation;
	private final View.OnClickListener mPmOnClickListener;

	private LayoutInflater inflater;

	private List<MBlog> mBlogList = new ArrayList<MBlog>();

	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	private MediaPlayer player;
	private String isplayingId;
	private int videoTime;
	private User pmUser;
	private IMBlogLogic mIMBlogLogic;
	public String likerequestId = "";

	public MBlogListAdapterTab2(Activity mContext, DisplayImageOptions options,
			View.OnClickListener mPmOnClickListener, IMBlogLogic mIMBlogLogic) {
		this.mContext = mContext;
		this.with = mContext.getResources().getDisplayMetrics().widthPixels;
		this.options = options;
		this.mPmOnClickListener = mPmOnClickListener;
		mLocation = LocationManager.getInstance().getLocation();
		this.mIMBlogLogic = mIMBlogLogic;
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
				inflater = LayoutInflater.from(mContext);
			}
			view = inflater.inflate(R.layout.home_page_listview_item, null);
			holder = new ImageLoaderViewHolder();
			holder.mTagedImage = (TagedImage) view.findViewById(R.id.add_label_image);
			holder.imageView12 = (ImageView) view.findViewById(R.id.audio_view);
			holder.mRoundProgressBar = (RoundProgressBar) view.findViewById(R.id.sound_playbar);
			holder.mRelativeLayout8 = (RelativeLayout) view.findViewById(R.id.download_progress);
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
			holder.imageView15 = (ImageView) view.findViewById(R.id.share);

			holder.mTextView14 = (TextView) view.findViewById(R.id.mblog_comment);

			holder.mTextView18 = (TextView) view.findViewById(R.id.text1);
			holder.mLinearLayout5 = (LinearLayout) view.findViewById(R.id.text1_view);

			holder.mButton = (Button) view.findViewById(R.id.follow_btn);
			holder.mButton.setVisibility(View.GONE);
			holder.mTagedImage.setLayoutParams(new RelativeLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, (int) (with - HelperFunc.dip2px(20))));
			holder.imageView11.setLayoutParams(new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT, (int) (with - HelperFunc.dip2px(20))));
			holder.mLinearLayout1 = (LinearLayout) view.findViewById(R.id.like_view);
			holder.imageView1 = (ImageView) view.findViewById(R.id.image_1);
			holder.imageView2 = (ImageView) view.findViewById(R.id.image_2);
			holder.imageView3 = (ImageView) view.findViewById(R.id.image_3);
			holder.imageView4 = (ImageView) view.findViewById(R.id.image_4);
			holder.imageView5 = (ImageView) view.findViewById(R.id.image_5);
			holder.imageView6 = (ImageView) view.findViewById(R.id.image_6);
			holder.imagenum = (TextView) view.findViewById(R.id.image_7);

			holder.mLinearLayout = (LinearLayout) view.findViewById(R.id.comment_view);

			holder.comment_image1 = (ImageView) view.findViewById(R.id.comment_image1);
			holder.comment_image2 = (ImageView) view.findViewById(R.id.comment_image2);
			holder.comment_image3 = (ImageView) view.findViewById(R.id.comment_image3);

			holder.mLinearLayout2 = (LinearLayout) view.findViewById(R.id.resend1);
			holder.mLinearLayout3 = (LinearLayout) view.findViewById(R.id.resend2);
			holder.mLinearLayout4 = (LinearLayout) view.findViewById(R.id.resend3);

			holder.mTextView15 = (TextView) view.findViewById(R.id.target_name1);
			holder.mTextView16 = (TextView) view.findViewById(R.id.target_name2);
			holder.mTextView17 = (TextView) view.findViewById(R.id.target_name3);

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
		ImageLoader.getInstance().displayImage(mBlog.owner.avatar_url, holder.imageView7, options,
				animateFirstListener);

		if (TextUtils.isEmpty(mBlog.text_desc)) {
			holder.mLinearLayout5.setVisibility(View.GONE);
		} else {
			holder.mTextView18.setText(mBlog.text_desc);
			holder.mLinearLayout5.setVisibility(View.VISIBLE);
			holder.mTextView18.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					HelperFunc.copy(mBlog.text_desc, mContext.getApplicationContext());
					Toast.makeText(mContext.getApplicationContext(), R.string.copy_mblog,
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

		holder.mTextView.setText(mBlog.owner.name);

		if (FusionCode.Role.H.equals(mBlog.owner.role)) {
			holder.imageView8.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.home_h_icon));
		} else if (FusionCode.Role.T.equals(mBlog.owner.role)) {
			holder.imageView8.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.home_t_icon));
		} else if (FusionCode.Role.P.equals(mBlog.owner.role)) {
			holder.imageView8.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.home_p_icon));
		} else if (FusionCode.Role.MH.equals(mBlog.owner.role)) {
			holder.imageView8.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.home_0_icon));
		} else if (FusionCode.Role.MT.equals(mBlog.owner.role)) {
			holder.imageView8.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.home_1_icon));
		} else if (FusionCode.Role.MP.equals(mBlog.owner.role)) {
			holder.imageView8.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.home_0_5_icon));
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

		if (mBlog.is_liked) {
			holder.mTextView12.setText("已赞");
			holder.mTextView12.setTextColor(mContext.getResources().getColor(R.color.color_439afb));
		} else {
			holder.mTextView12.setText("赞");
			holder.mTextView12.setTextColor(mContext.getResources().getColor(R.color.text_gray2));
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

		List<String> mBlogLocation = mBlog.location;

		if (null != mBlogLocation && mBlogLocation.size() == 2 && null != mLocation) {

			holder.mTextView1.setVisibility(View.VISIBLE);

			holder.mTextView1.setText(StringUtil.getDistance(Double.valueOf(mLocation.longitude),
					Double.valueOf(mLocation.latitude), Double.valueOf(mBlogLocation.get(0)),
					Double.valueOf(mBlogLocation.get(1)))
					+ mContext.getString(R.string.distance_km));
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
			ImageLoader.getInstance().displayImage(mBlog.photo.url, holder.imageView11, options,
					animateFirstListener, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String s, View view, final int i, final int i2) {
							mContext.runOnUiThread(new Runnable() {
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
								mContext.runOnUiThread(new Runnable() {
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
				ImageLoader.getInstance().displayImage(mUser.avatar_url, imageViews[j], options);
				imageViews[j].setVisibility(View.VISIBLE);
				imageViews[j].setOnClickListener(new GotoTaActivityOnClickListener(mContext,
						mBlog.likes.get(j).userId));
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
				mContext.startActivity(new Intent(MBlogAction.MBLOG_LIKEBYLIST_ACTION).putExtra(
						MBlogAction.MBLOG_ID, mBlog._id));
			}
		});

		ImageView[] imageComment = new ImageView[] { holder.comment_image1, holder.comment_image2,
				holder.comment_image3 };

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
			comment_view[n].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

				}
			});
		}

		if (mBlog.comments_count > 0) {
			int j = 0;
			for (final Comment mComment : mBlog.comments) {
				ImageLoader.getInstance().displayImage(mComment.owner.avatar_url, imageComment[j],
						options);
				imageComment[j].setOnClickListener(new GotoTaActivityOnClickListener(mContext,
						mComment.owner.userId));
				comment_name[j].setText(mComment.owner.name);
				comment_time[j].setText(PrettyDateFormat.formatISO8601Time(mComment.created_at));
				comment_con[j].setText(ExpressionUtil.getInstance().strToSmiley(mComment.text));
				comment_view[j].setVisibility(View.VISIBLE);
				comment_view[j].setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mContext.startActivity(new Intent(
								FusionAction.MBlogAction.MBLOGCOMMENT_ACTION)
								.putExtra(FusionAction.MBlogAction.MBLOG, mBlog)
								.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
								.putExtra(FusionAction.MBlogAction.MBLOG_OWNER, mComment.owner));
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
			holder.mLinearLayout.setVisibility(View.VISIBLE);
		} else {
			holder.mLinearLayout.setVisibility(View.GONE);
		}

		holder.mTagedImage.removeAllTag();
		for (MBlogTag mMBlogTag : mBlog.tags) {
			if (null != mMBlogTag.text)
				holder.mTagedImage.addTag(new Tag(mMBlogTag.id, mMBlogTag.text.name, mMBlogTag.x,
						mMBlogTag.y, Tag.LABEL_TYPE), false, mContext, scale, mMBlogTag.align,
						(int) (with - HelperFunc.dip2px(20)),
						(int) ((with - HelperFunc.dip2px(20)) * scale));
			if (null != mMBlogTag.user)
				holder.mTagedImage.addTag(new Tag(mMBlogTag.id, mMBlogTag.user.name,
						mMBlogTag.user.user_id, mMBlogTag.x, mMBlogTag.y, Tag.FIGURE_TYPE), false,
						mContext, scale, mMBlogTag.align, (int) (with - HelperFunc.dip2px(20)),
						(int) ((with - HelperFunc.dip2px(20)) * scale));
			if (null != mMBlogTag.location)
				holder.mTagedImage.addTag(new Tag(mMBlogTag.id, mMBlogTag.location.name,
						mMBlogTag.x, mMBlogTag.y, Tag.PLACE_TYPE), false, mContext, scale,
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

		if (mBlog.comments_count > 0) {
			holder.mTextView14.setText(String.format(mContext.getString(R.string.mblog_comment),
					mBlog.comments_count));
		}

		holder.mTextView13.setOnClickListener(new GotoMblogCommentActivityOnClickListener(mContext,
				mBlog));

		holder.mTextView19.setOnClickListener(mPmOnClickListener);

		holder.mTextView14.setOnClickListener(new GotoMblogCommentActivityOnClickListener(mContext,
				mBlog));
		holder.imageView7.setOnClickListener(new GotoTaActivityOnClickListener(mContext,
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
					((ImageView) view).setImageDrawable(mContext.getResources().getDrawable(
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

										new Handler(mContext.getMainLooper()).post(new Runnable() {

											@Override
											public void run() {
												holder.mRelativeLayout8.setVisibility(View.GONE);
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
													public void onCompletion(MediaPlayer mediaPlayer) {
														player.release();
														holder.mRoundProgressBar.stopCartoom();
														player = null;
														isplayingId = null;
														((ImageView) view)
																.setImageDrawable(mContext
																		.getResources()
																		.getDrawable(
																				R.drawable.home_audio_play));
													}
												});
												player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
													@Override
													public void onPrepared(MediaPlayer mediaPlayer) {
														mediaPlayer.start();
														holder.mRoundProgressBar.startCartoom(0,
																mBlog.audio_desc.duration);
														holder.mRoundProgressBar
																.setVisibility(View.VISIBLE);
														((ImageView) view)
																.setImageDrawable(mContext
																		.getResources()
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

										new Handler(mContext.getMainLooper()).post(new Runnable() {

											@Override
											public void run() {
												holder.mRelativeLayout8.setVisibility(View.GONE);
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

		holder.imageView14.setOnClickListener(new View.OnClickListener() {
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

										new Handler(mContext.getMainLooper()).post(new Runnable() {

											@Override
											public void run() {
												holder.mRelativeLayout8.setVisibility(View.GONE);
												holder.imageView14.setVisibility(View.VISIBLE);
												view.setEnabled(true);
												view.setClickable(true);
												String path = DownloadUtil.SDPATH + mBlog._id;
												videoTime = mBlog.video.duration;
												mContext.startActivity(new Intent(
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

										new Handler(mContext.getMainLooper()).post(new Runnable() {

											@Override
											public void run() {
												holder.mRelativeLayout8.setVisibility(View.GONE);
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

		holder.imageView15.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

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

									if (null != mIMBlogLogic) {
										mIMBlogLogic.deleteMBlogById(mBlog._id);
									}

									notifyDataSetChanged();
								}
							}

							@Override
							public void toChat() {
								MessageInfo mMessageInfo = ForwardActivity.createMessageInfo(mBlog);
								mContext.startActivityForResult(new Intent(
										FusionAction.ChatAction.FORWARD_ACTION).putExtra(
										FusionAction.ChatAction.MESSGAE_INFO, mMessageInfo),
										ChatActivity.SHARE_TO_CHAT);
							}
						});
			}
		});

		return view;
	}

	public void setList(List<MBlog> mBlogList) {
		if (null != mBlogList)
			this.mBlogList = mBlogList;
	}

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
}
