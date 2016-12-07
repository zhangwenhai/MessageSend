package com.theone.sns.ui.me.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.theone.sns.R;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.util.HelperFunc;
import com.theone.sns.util.HttpUtil;
import com.theone.sns.util.RoundProgressBar;
import com.theone.sns.util.uiwidget.SmoothImageView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by a on 14/11/18.
 */
public class AvatarShowActivity extends IphoneTitleActivity {
    private String mUrl;
    private int mLocationX;
    private int mLocationY;
    private int mWidth;
    private int mHeight;
    private SmoothImageView imageView;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private RoundProgressBar mRoundProgressBar;

    @Override
    protected void initLogics() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getView();

        setView();

        setListener();
    }

    private void getView() {
        mUrl = getIntent().getStringExtra("images");
        if (TextUtils.isEmpty(mUrl)) {
            finish();
        }

        mUrl = HttpUtil.removeUrlWH(mUrl);

        mLocationX = getIntent().getIntExtra("locationX", 0);
        mLocationY = getIntent().getIntExtra("locationY", 0);
        mWidth = getIntent().getIntExtra("width", 0);
        mHeight = getIntent().getIntExtra("height", 0);

        RelativeLayout mRelativeLayout = new RelativeLayout(this);
        imageView = new SmoothImageView(this);
        imageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
        imageView.transformIn();
        imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mRelativeLayout.addView(imageView, lp);


        mRoundProgressBar = new RoundProgressBar(this);

        mRoundProgressBar.setmSidePaintInterval(1);
        mRoundProgressBar.setmPaintColor(0xFFFFD800);
        mRoundProgressBar.setmPaintWidth(5);
        mRoundProgressBar.setmBShowBottom(false);
        mRoundProgressBar.setmBRoundPaintsFill(false);

        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams((int) HelperFunc.dip2px(50), (int) HelperFunc.dip2px(50));
        lp1.addRule(RelativeLayout.CENTER_IN_PARENT);
        mRelativeLayout.addView(mRoundProgressBar, lp1);

        setContentView(mRelativeLayout);
    }

    private void setView() {
        setLeftButton(R.drawable.icon_back, false, false);
        ImageLoader.getInstance().displayImage(mUrl, imageView, optionsForUserIcon, animateFirstListener, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String s, View view, int i, int i2) {
                if (null == mRoundProgressBar) {
                    return;
                }
                if (i == i2) {
                    mRoundProgressBar.setVisibility(View.GONE);
                } else {
                    mRoundProgressBar.setMax(i2);
                    mRoundProgressBar.setProgress(i);
                    mRoundProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setListener() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.welcome_end_in, R.anim.welcome_end_out);
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
