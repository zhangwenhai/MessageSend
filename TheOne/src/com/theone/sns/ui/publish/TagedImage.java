package com.theone.sns.ui.publish;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.ui.base.dialog.TheOneAlertDialog;
import com.theone.sns.ui.me.GotoTaActivityOnClickListener;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.uiwidget.IphoneStyleAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class TagedImage extends RelativeLayout {

	public List<Tag> mTagList = new ArrayList<Tag>();
	public List<View> mViewList = new ArrayList<View>();
	private Context mContext;
	private ImageView sourceImageView;
	private IphoneStyleAlertDialogBuilder mDeleteDialog;

	public TagedImage(Context context) {
		super(context);
		if (isInEditMode()) {
			return;
		}
	}

	public TagedImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (isInEditMode()) {
			return;
		}
	}

	public TagedImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode()) {
			return;
		}
		mContext = context;
	}

	public void setImageBitmap(Bitmap bitmap) {
		sourceImageView.setImageBitmap(bitmap);
	}

	public Drawable getDrawable() {
		return sourceImageView.getDrawable();
	}

	public void setImageUri(Uri imageUri) {
		sourceImageView.setImageURI(imageUri);
	}

	public void setImageDrawable(Drawable mDrawable) {
		sourceImageView.setImageDrawable(mDrawable);
	}

	public void addTag(final Tag tag, final boolean isTouch, final Activity mActivity,
			final float scale, final String align, final int imageWidth, final int imageHeight) {
		if (null == tag) {
			return;
		}
		mTagList.add(tag);
		int layoutId = -1;
		if (tag.type == Tag.PLACE_TYPE) {
			layoutId = R.layout.layout_animated_tag_1;
		} else if (tag.type == Tag.FIGURE_TYPE) {
			layoutId = R.layout.layout_animated_tag_2;
		} else {
			layoutId = R.layout.layout_animated_tag;
		}
		final RelativeLayout tagLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(
				layoutId, null);
		final TextView tag_text = (TextView) tagLayout.findViewById(R.id.tag_text);
		final TextView tag_text1 = (TextView) tagLayout.findViewById(R.id.tag_text1);

		if (TextUtils.isEmpty(align)) {
			if (tag.x > 0.5f) {
				tag_text.setVisibility(View.VISIBLE);
				tag_text1.setVisibility(View.GONE);
				tag.align = "L";
			} else {
				tag_text.setVisibility(View.GONE);
				tag_text1.setVisibility(View.VISIBLE);
				tag.align = "R";
			}
		} else {
			if (align.equals("L")) {
				tag_text.setVisibility(View.VISIBLE);
				tag_text1.setVisibility(View.GONE);
				tag.align = "L";
			} else {
				tag_text.setVisibility(View.GONE);
				tag_text1.setVisibility(View.VISIBLE);
				tag.align = "R";
			}
		}

		if (isTouch) {
			tagLayout.setOnTouchListener(new OnTouchListener() {
				public int lastX;
				public int lastY;
				public int startX;
				public int mlastX;
				public int mlastY;
				public boolean isLeft;
				private boolean isNeed;

				@Override
				public boolean onTouch(View view, MotionEvent motionEvent) {
					switch (motionEvent.getAction()) {
					case MotionEvent.ACTION_DOWN:
						lastX = (int) motionEvent.getRawX();
						startX = (int) motionEvent.getRawX();
						lastY = (int) motionEvent.getRawY();
						break;
					case MotionEvent.ACTION_MOVE:
						int dx = (int) motionEvent.getRawX() - lastX;
						int dy = (int) motionEvent.getRawY() - lastY;

						int left = view.getLeft() + dx;
						int top = view.getTop() + dy;
						int right = view.getRight() + dx;
						int bottom = view.getBottom() + dy;
						// 设置不能出界
						if (left < 0) {
							left = 0;
							right = left + view.getWidth();
							isNeed = true;
							isLeft = false;
						}

						if (right > imageWidth) {
							right = imageWidth;
							left = right - view.getWidth();
							isNeed = true;
							isLeft = true;
						}

						if (top < 0) {
							top = 0;
							bottom = top + view.getHeight();
						}

						if (bottom > imageHeight) {
							bottom = imageHeight;
							top = bottom - view.getHeight();
						}
						view.layout(left, top, right, bottom);
						lastX = (int) motionEvent.getRawX();
						lastY = (int) motionEvent.getRawY();
						mlastX = left;
						mlastY = top;
						if (isNeed) {
							RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
									LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
							lp.topMargin = mlastY;
							lp.leftMargin = mlastX;
							view.setLayoutParams(lp);
							if (isLeft) {
								tag_text.setVisibility(View.VISIBLE);
								tag_text1.setVisibility(View.GONE);
								tag.align = "L";
							} else {
								tag_text.setVisibility(View.GONE);
								tag_text1.setVisibility(View.VISIBLE);
								tag.align = "R";
							}
						}
						break;
					case MotionEvent.ACTION_UP:
						int x,
						y;
						int height = tagLayout.getMeasuredHeight();
						int width = tagLayout.getMeasuredWidth();
						if ("L".equals(tag.align)) {
							x = (int) (mlastX + width - HelperFunc.dip2px(15));
							y = (int) (mlastY + height / 2);
						} else {
							x = (int) (mlastX + HelperFunc.dip2px(15));
							y = (int) (mlastY + height / 2);
						}
						((Tag) view.getTag()).x = x * 1.0000f / (imageWidth * 1.0000f);
						((Tag) view.getTag()).y = y * 1.0000f / (imageHeight * 1.0000f);
						RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						lp.topMargin = mlastY;
						lp.leftMargin = mlastX;
						view.setLayoutParams(lp);
						if (Math.abs(motionEvent.getRawX() - startX) == 0
								|| Math.abs(motionEvent.getRawY() - lastY) == 0) {
							return false;
						}
						return true;
					}
					return false;
				}
			});

			tagLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View view) {
					new TheOneAlertDialog.Builder(mActivity)
							.setMessage(R.string.confirmation_delete)
							.setPositiveButton(R.string.delete,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											removeView(view);
											mViewList.remove(view);
											mTagList.remove((Tag) view.getTag());
										}
									})
							.setNegativeButton(R.string.Cancel,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
										}
									}).show();
				}
			});
		} else {
			if (tag.type == Tag.FIGURE_TYPE) {
				tagLayout
						.setOnClickListener(new GotoTaActivityOnClickListener(mContext, tag.userId));
			} else {
				tagLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						mContext.startActivity(new Intent(FusionAction.MBlogAction.TAGSET_ACTION)
								.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(
										FusionAction.MBlogAction.MBLOG_TAG, tag));
					}
				});
			}
		}

		tag_text.setText(" " + tag.tagName + " ");
		tag_text1.setText(" " + tag.tagName + " ");

		if (TextUtils.isEmpty(tag.tagName)) {
			tag_text.setVisibility(View.GONE);
			tag_text1.setVisibility(View.GONE);
		}

		// 增加整体布局监听
		ViewTreeObserver vto = tagLayout.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				tagLayout.getViewTreeObserver().removeOnPreDrawListener(this);
				int height = tagLayout.getMeasuredHeight();
				int width = tagLayout.getMeasuredWidth();

				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

				if (isTouch && !TextUtils.isEmpty(tag.tagName)) {
					// 设置不能出界
					if (tag.x * imageWidth + width > imageWidth) {
						tag.x = (float) (imageWidth - width) / imageWidth;
					}
					if (tag.y * imageHeight + height / 2 > imageHeight) {
						tag.y = (float) (imageHeight - height / 2) / (imageHeight);
					}
					if ("L".equals(tag.align)) {
						lp.leftMargin = (int) (tag.x * imageWidth + HelperFunc.dip2px(15) - width);
						lp.topMargin = (int) (tag.y * imageHeight - height / 2 - HelperFunc
								.dip2px(10));
					} else {
						lp.leftMargin = (int) (tag.x * imageWidth - HelperFunc.dip2px(15));
						lp.topMargin = (int) (tag.y * imageHeight - height / 2 - HelperFunc
								.dip2px(10));
					}
					int x, y;
					if ("L".equals(tag.align)) {
						x = (int) (lp.leftMargin + width - HelperFunc.dip2px(15));
						y = (int) (lp.topMargin + height / 2);
					} else {
						x = (int) (lp.leftMargin + HelperFunc.dip2px(15));
						y = (int) (lp.topMargin + height / 2);
					}
					((Tag) tagLayout.getTag()).x = x * 1.0000f / (imageWidth * 1.0000f);
					((Tag) tagLayout.getTag()).y = y * 1.0000f / (imageHeight * 1.0000f);
				} else {
					if (tag.y * imageHeight + height > imageHeight) {
						tag.y = (float) (imageHeight - height / 2) / (imageHeight);
					}
					if (tag.y * imageHeight - height < 0) {
						tag.y = (float) (height / 2) / (imageHeight);
					}
					if ("L".equals(tag.align)) {
						if (tag.x * imageWidth + HelperFunc.dip2px(15) > imageWidth) {
							tag.x = (float) (imageWidth - HelperFunc.dip2px(15)) / imageWidth;
						}
						lp.leftMargin = (int) (tag.x * imageWidth - width + HelperFunc.dip2px(15));
						lp.topMargin = (int) (tag.y * imageHeight - height / 2);
					} else {
						// 设置不能出界
						if (tag.x * imageWidth - HelperFunc.dip2px(15) < 0) {
							tag.x = (float) (HelperFunc.dip2px(15)) / imageWidth;
						}
						lp.leftMargin = (int) (tag.x * imageWidth - HelperFunc.dip2px(15));
						lp.topMargin = (int) (tag.y * imageHeight * 1.00000f - height / 2);
					}

				}
				tagLayout.setLayoutParams(lp);
				tagLayout.setVisibility(View.VISIBLE);
				tagLayout.setTag(tag);
				return false;
			}
		});
		tagLayout.setTag(tag);
		tagLayout.setVisibility(View.INVISIBLE);
		addView(tagLayout);
		mViewList.add(tagLayout);
	}

	// public void addTags(ArrayList<Tag> tags, boolean isTouch) {
	// if (null == tags)
	// return;
	//
	// for (int i = 0; i < tags.size(); i++) {
	// addTag(tags.get(i), isTouch);
	// }
	//
	// }

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (isInEditMode()) {
			return;
		}
		int len = this.getChildCount();
		for (int i = 0; i < len; i++) {
			View v = this.getChildAt(i);
			if (v instanceof ImageView) {
				sourceImageView = (ImageView) v;
				break;
			}
		}

		// sourceImageView.setLayoutParams(new
		// LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
		// imageWidth));
		// sourceImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

	}

	public void showTag() {
		for (View v : mViewList) {
			v.setVisibility(View.VISIBLE);
		}
	}

	public void hideTag() {
		for (View v : mViewList) {
			v.setVisibility(View.INVISIBLE);
		}
	}

	public boolean getTagState() {
		if (mViewList.size() == 0) {
			return true;
		}
		if (mViewList.get(0).getVisibility() == VISIBLE) {
			return true;
		} else {
			return false;
		}
	}

	public void removeTag(String id) {
		if (mViewList.size() == 0) {
			return;
		}
		List<View> mNewViewList = new ArrayList<View>(mViewList);
		for (View v : mViewList) {
			if ((((Tag) v.getTag()).id).equals(id)) {
				removeView(v);
				mNewViewList.remove(v);
				break;
			}
		}
		mViewList = mNewViewList;
		List<Tag> mNewTagList = new ArrayList<Tag>(mTagList);
		for (Tag mTag : mTagList) {
			if (mTag.id.equals(id)) {
				mNewTagList.remove(mTag);
				break;
			}
		}
		mTagList = mNewTagList;
	}

	public Tag getTag(String id) {
		Tag mTag = null;
		if (mViewList.size() == 0) {
			return mTag;
		}
		for (View v : mViewList) {
			if ((((Tag) v.getTag()).id).equals(id)) {
				mTag = (Tag) v.getTag();
				return mTag;
			}
		}
		return mTag;
	}

	public List<Tag> getTagList() {
		return mTagList;
	}

	public void removeAllTag() {
		for (View v : mViewList) {
			removeView(v);
		}
		mViewList.clear();
		mTagList.clear();
	}
}