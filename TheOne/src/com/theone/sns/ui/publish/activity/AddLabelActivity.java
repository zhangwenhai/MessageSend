package com.theone.sns.ui.publish.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionAction.PublicAction;
import com.theone.sns.component.location.gps.LocationManager;
import com.theone.sns.logic.account.IAccountLogic;
import com.theone.sns.logic.model.mblog.MBlogTag;
import com.theone.sns.logic.model.mblog.PublishMBlog;
import com.theone.sns.logic.model.mblog.base.LocationTag;
import com.theone.sns.logic.model.mblog.base.Photo;
import com.theone.sns.logic.model.mblog.base.TextTag;
import com.theone.sns.logic.model.mblog.base.UserTag;
import com.theone.sns.logic.model.mblog.base.Video;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.publish.Tag;
import com.theone.sns.ui.publish.TagedImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/9/9.
 */
public class AddLabelActivity extends IphoneTitleActivity {

	public static final int LABEL_REQUESTCODE = 0;

	public static final int PLACE_REQUESTCODE = 1;

	public static final int FIGURE_REQUESTCODE = 2;

	private TagedImage addLabelImage;

	private String filePath;

	private int with;

	private PopupWindow popupWindow;

	private View popView;

	private long time;

	private IAccountLogic mIAccountLogic;

	private String requestId;
	private float x;
	private float y;
	private Bitmap bitmap;
	private LinearLayout linear;
	private int height;
	private float scale;

	@Override
	protected void initLogics() {
		mIAccountLogic = (IAccountLogic) getLogicByInterfaceClass(IAccountLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.add_label_main);

		getView();

		setView();

		LocationManager.getInstance().start();
	}

	private void getView() {
		filePath = getIntent().getStringExtra(FusionAction.PublicAction.FILE_PATH);
		if (TextUtils.isEmpty(filePath)) {
			finish();
		}
		with = getResources().getDisplayMetrics().widthPixels;
		height = getResources().getDisplayMetrics().heightPixels;

		bitmap = BitmapFactory.decodeFile(filePath);
		addLabelImage = (TagedImage) findViewById(R.id.add_label_image);
		linear = (LinearLayout) findViewById(R.id.linear);
		linear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				height / 2));
		linear.setGravity(Gravity.CENTER);
	}

	private void setView() {
		setTitle(R.string.add_label1);
		setRightButton(R.string.next, true);

		initPop();

		addLabelImage.setImageBitmap(bitmap);
		addLabelImage.setLayoutParams(getLayoutParams(bitmap, with, height / 2));
		scale = (bitmap.getHeight() * 1.0000f) / (bitmap.getWidth() * 1.0000f);

		// ViewTreeObserver vto = addLabelImage.getViewTreeObserver();
		// vto.addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
		// @Override
		// public void onDraw() {
		//
		// addLabelImage.getViewTreeObserver().removeOnDrawListener(this);
		// }
		// });

		addLabelImage.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				switch (motionEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					time = System.currentTimeMillis();
					x = motionEvent.getX() / view.getMeasuredWidth();
					y = motionEvent.getY() / view.getMeasuredHeight();
					addLabelImage.addTag(new Tag(time + "", "", x, y), true, AddLabelActivity.this,
							scale, "", view.getMeasuredWidth(), view.getMeasuredHeight());
					if (!popupWindow.isShowing()) {
						popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
						popupWindow.update();
					}
					break;
				default:
					break;
				}
				return false;
			}
		});

		getRightButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				List<MBlogTag> mBlogTagList = new ArrayList<MBlogTag>();
				for (Tag tag : addLabelImage.getTagList()) {
					MBlogTag mBlogTag = new MBlogTag();
					mBlogTag.x = tag.x;
					mBlogTag.y = tag.y;
					mBlogTag.align = tag.align;
					switch (tag.type) {
					case Tag.LABEL_TYPE: {
						TextTag mTextTag = new TextTag();
						mTextTag.name = tag.tagName;
						mBlogTag.text = mTextTag;
						break;
					}
					case Tag.PLACE_TYPE: {
						LocationTag mLocationTag = new LocationTag();
						mLocationTag.name = tag.tagName;
						mBlogTag.location = mLocationTag;
						break;
					}
					case Tag.FIGURE_TYPE: {
						UserTag mUserTag = new UserTag();
						mUserTag.name = tag.tagName;
						mUserTag.user_id = tag.userId;
						mBlogTag.user = mUserTag;
						break;
					}
					default:
					}
					mBlogTagList.add(mBlogTag);
				}

				if (null == mBlogTagList || mBlogTagList.size() == 0) {

					showToast(R.string.need_to_add_label);

					return;
				}

				Intent mIntent = new Intent(FusionAction.PublicAction.SEND_ACTION);
				PublishMBlog publishMBlog = new PublishMBlog();
				publishMBlog.tags = mBlogTagList;
				Photo mPhoto = new Photo();
				mPhoto.w = bitmap.getWidth();
				mPhoto.h = bitmap.getHeight();
				publishMBlog.photo = mPhoto;
				if (FusionAction.PublicAction.TYPE_VIDEO == getIntent().getIntExtra(
						PublicAction.PUBLIC_TYPE, 0)) {
					Video mVideo = new Video();
					mVideo.duration = getIntent().getIntExtra(PublicAction.VIDEO_TIME, 0);
					publishMBlog.video = mVideo;
					mIntent.putExtra(PublicAction.VIDEO_PATH,
							getIntent().getStringExtra(PublicAction.VIDEO_PATH));
				}
				mIntent.putExtra(FusionAction.PublicAction.FILE_PATH, filePath);
				mIntent.putExtra(PublicAction.PUBLIC_TYPE,
						getIntent().getIntExtra(PublicAction.PUBLIC_TYPE, 0));
				mIntent.putExtra(FusionAction.PublicAction.PUBLICMBLOG, publishMBlog);
				if (null != getIntent().getStringExtra(PublicAction.AUDIO_PATH)) {
					mIntent.putExtra(PublicAction.AUDIO_PATH,
							getIntent().getStringExtra(PublicAction.AUDIO_PATH));
					mIntent.putExtra(PublicAction.AUDIO_TIME,
							getIntent().getIntExtra(PublicAction.AUDIO_TIME, 0));
				}
				startActivity(mIntent);
			}
		});
	}

	private void initPop() {
		popView = LayoutInflater.from(AddLabelActivity.this).inflate(R.layout.add_label_pop, null);
		popView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
			}
		});
		popupWindow = new PopupWindow(popView, LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT, true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setHeight(getResources().getDisplayMetrics().heightPixels);
		popupWindow.setWidth(getResources().getDisplayMetrics().widthPixels);
		popupWindow.setFocusable(true);
		popupWindow.setAnimationStyle(R.style.popwin_anim_style);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		popView.findViewById(R.id.item1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
				startActivityForResult(new Intent(FusionAction.PublicAction.LABELLIST_ACTION)
						.putExtra(FusionAction.PublicAction.TYPE, LABEL_REQUESTCODE),
						LABEL_REQUESTCODE);
			}
		});

		popView.findViewById(R.id.item2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
				startActivityForResult(new Intent(FusionAction.PublicAction.LABELLIST_ACTION)
						.putExtra(FusionAction.PublicAction.TYPE, PLACE_REQUESTCODE),
						PLACE_REQUESTCODE);
			}
		});

		popView.findViewById(R.id.item3).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
				startActivityForResult(
						new Intent(FusionAction.PublicAction.CIRCLE_LABELLIST_ACTION).putExtra(
								FusionAction.PublicAction.TYPE, FIGURE_REQUESTCODE),
						FIGURE_REQUESTCODE);
			}
		});

		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				addLabelImage.removeTag(time + "");
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case LABEL_REQUESTCODE: {
			if (RESULT_CANCELED == resultCode || null == data) {
				addLabelImage.removeTag(time + "");
				return;
			}
			addLabelImage.removeTag(time + "");
			addLabelImage.addTag(
					new Tag(time + "", data.getStringExtra(FusionAction.PublicAction.LABEL_NAME),
							x, y, Tag.LABEL_TYPE), true, AddLabelActivity.this, scale, "",
					addLabelImage.getMeasuredWidth(), addLabelImage.getMeasuredHeight());
			break;
		}
		case PLACE_REQUESTCODE: {
			if (RESULT_CANCELED == resultCode || null == data) {
				addLabelImage.removeTag(time + "");
				return;
			}
			addLabelImage.removeTag(time + "");
			addLabelImage.addTag(
					new Tag(time + "", data.getStringExtra(FusionAction.PublicAction.LABEL_NAME),
							x, y, Tag.PLACE_TYPE), true, AddLabelActivity.this, scale, "",
					addLabelImage.getMeasuredWidth(), addLabelImage.getMeasuredHeight());
			break;
		}
		case FIGURE_REQUESTCODE: {
			if (RESULT_CANCELED == resultCode || null == data) {
				addLabelImage.removeTag(time + "");
				return;
			}
			addLabelImage.removeTag(time + "");
			Bundle bundle = (Bundle) data.getBundleExtra(FusionAction.PublicAction.LABEL_NAME);

			addLabelImage
					.addTag(new Tag(time + "", (String) bundle.get(PublicAction.LABEL_USER_NAME),
							(String) bundle.get(PublicAction.LABEL_USER_ID), x, y, Tag.FIGURE_TYPE),
							true, AddLabelActivity.this, scale, "", addLabelImage
									.getMeasuredWidth(), addLabelImage.getMeasuredHeight());
			break;
		}
		default:
		}
	}

	public static LinearLayout.LayoutParams getLayoutParams(Bitmap bitmap, int screenWidth,
			int screenHeight) {

		float rawWidth = bitmap.getWidth();
		float rawHeight = bitmap.getHeight();

		float width = 0;
		float height = 0;

		float sacy = rawWidth / rawHeight;
		float screensacy = screenWidth / screenHeight;

		height = rawHeight;
		width = rawWidth;

		if (sacy > screensacy) {
			height = (rawHeight / rawWidth) * screenWidth;
			width = screenWidth;
		} else {
			height = screenHeight;
			width = (rawWidth / rawHeight) * screenHeight;
		}

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) width,
				(int) height);

		return layoutParams;
	}
}
