package com.theone.sns.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.theone.sns.R;
import com.theone.sns.common.TheOneConstants;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.util.patch.Android5Patch;

import java.io.File;

public class UsePictureConfirmActivity extends IphoneTitleActivity {

	private static final String TAG = "UsePictureConfirmActivity";
	public static final String INTENT_PICTURE_PATH = "intent_picture_path";
	private File m_picFile = null;
	private ImageView m_imgView;
	private Thread m_rotateThread = null;

	public void onBackKey() {
		setResult(Activity.RESULT_CANCELED);
		finish();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.app_name);
		setLeftButton(R.string.Cancel, true, true);
		setRightButton(R.string.Use, true);
		setSubContent(R.layout.picture_viewer_new);

		String picPath = getIntent().getStringExtra(INTENT_PICTURE_PATH);

		if (picPath == null || !(m_picFile = new File(picPath)).exists()) {
			// toast(R.string.bad_picture);
			onBackKey();
			return;
		}

		getRightButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doUse();
			}
		});

		View useContainer = findViewById(R.id.use_container);
		useContainer.setVisibility(View.VISIBLE);
		useContainer.findViewById(R.id.use_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doUse();
			}

		});

		m_imgView = (ImageView) findViewById(R.id.pic_view);
		try {
			Bitmap bmp = HelperFunc
					.getScaleImage(m_picFile, TheOneConstants.CHAT_PICTURE_MAX_WIDTH,
							TheOneConstants.CHAT_PICTURE_MAX_HEIGHT);
			if (bmp == null) {
				onBackKey();
				return;
			}
			final int degree = Android5Patch.getRotate(m_picFile);
			if (degree != 0) {
				Matrix matrix = new Matrix();
				matrix.postRotate(degree);
				try {
					Bitmap newBmp = ImageLoaderUtil.createBitmap(bmp, 0, 0, bmp.getWidth(),
							bmp.getHeight(), matrix, true);
					bmp.recycle();
					bmp = newBmp;
				} catch (Exception e) {
				}

				if (m_rotateThread == null) {
					m_rotateThread = new Thread() {
						@Override
						public void run() {
							HelperFunc.rotateChatImg(m_picFile, degree);
							m_rotateThread = null;
						}
					};
					m_rotateThread.start();
				}
			}
			m_imgView.setImageBitmap(bmp);
		} catch (OutOfMemoryError e) {
			finish();
			e.printStackTrace();
		}
	}

	@Override
	protected void initLogics() {

	}

	// @Override
	// protected int getContentLayout() {
	// return R.layout.tab_content_float;
	// }

	private void doUse() {
		int tryCount = 0;
		while (m_rotateThread != null && tryCount < 100) {
			HelperFunc.safeSleep(100);
			tryCount++;
		}
		setResult(Activity.RESULT_OK);

		finish();
	}
}
