package com.theone.sns.util.uiwidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.NinePatch;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.theone.sns.R;
import com.theone.sns.TheOneApp;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.ImageLoaderUtil;

/**
 * Created by zhangwenhai on 2014/10/30.
 */
public class ChatImageViewSend extends ImageView {
	private NinePatch m_maskBmpSend;

	public ChatImageViewSend(Context context) {
		super(context);
		init();
	}

	public ChatImageViewSend(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ChatImageViewSend(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		if (m_maskBmpSend == null) {
			Bitmap original = ImageLoaderUtil.decodeResource(TheOneApp.getContext().getResources(),
					R.drawable.bubble2_mask);
			if (original != null)
				m_maskBmpSend = new NinePatch(original, original.getNinePatchChunk(), null);
		}
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		Bitmap bp = HelperFunc.drawChatBitmap(bm, m_maskBmpSend);
		BitmapDrawable bd = new BitmapDrawable(bp);
		super.setImageDrawable(bd);
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		if(drawable instanceof ColorDrawable){
			super.setImageDrawable(drawable);
			return;
		}
		BitmapDrawable bd = (BitmapDrawable) drawable;
		Bitmap bm = bd.getBitmap();
		Bitmap bp = HelperFunc.drawChatBitmap(bm, m_maskBmpSend);
		BitmapDrawable bd1 = new BitmapDrawable(bp);
		super.setImageDrawable(bd1);
	}

}
