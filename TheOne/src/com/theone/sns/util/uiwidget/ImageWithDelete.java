package com.theone.sns.util.uiwidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.theone.sns.R;
import com.theone.sns.util.RoundedImageView;

public class ImageWithDelete extends RelativeLayout {
	private ImageView m_deleteView;
	private ImageView m_imgView;
	private ImageWithDeleteCallback m_callback;
	private boolean m_canDelete = true;

	private int flag = -1;
	public final static int FLAG_DELETE_ITEM = 1;
	public final static int FLAG_ADD_ITEM = 2;

	public ImageWithDelete(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		View localView = LayoutInflater.from(paramContext).inflate(R.layout.image_with_delete,
				this, true);
		m_imgView = (ImageView) localView.findViewById(R.id.srcImage);
		m_deleteView = (ImageView) localView.findViewById(R.id.delelteImage);

		m_deleteView.setVisibility(View.GONE);
		m_imgView.setClickable(true);
		m_imgView.setFocusable(true);
		m_deleteView.setClickable(true);
		m_deleteView.setFocusable(true);

		m_imgView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				notifyClick();
			}
		});

		m_imgView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				notifyLongPress();
				return false;
			}
		});

		m_deleteView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				notifyDelete();
			}
		});
	}

	private void notifyLongPress() {
		if (m_callback != null) {
			m_callback.onLongPress(this);
		}
	}

	private void notifyClick() {
		if (m_callback != null) {
			m_callback.onClick(this);
		}
	}

	private void notifyDelete() {
		if (m_canDelete && m_callback != null) {
			m_callback.onDelete(this);
		}
	}

	public void setCallback(ImageWithDeleteCallback callback) {
		m_callback = callback;
	}

	public ImageView getImageView() {
		return m_imgView;
	}

	public void setImage(int resId) {
		m_imgView.setImageResource(resId);
	}

	public void setImage(Bitmap bmp) {
		m_imgView.setImageBitmap(bmp);
	}

	public void toNormalMode() {
		m_deleteView.setVisibility(View.GONE);
	}

	public void setCanDelete(boolean canDelete) {
		m_canDelete = canDelete;
	}

	public void toDeleteMode() {
		if (m_canDelete)
			m_deleteView.setVisibility(View.VISIBLE);
	}

	public int getItemFlag() {
		return flag;
	}

	public void setItemFlag(int flag) {
		this.flag = flag;
	}
}
