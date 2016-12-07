package com.theone.sns.util.uiwidget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import com.theone.sns.R;
import com.theone.sns.util.HelperFunc;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionUtil {
	private static ExpressionUtil sInstance;

	public static ExpressionUtil getInstance() {
		return sInstance;
	}

	public static void init(Context context) {
		sInstance = new ExpressionUtil(context);
	}

	private final Context mContext;
	private final String[] mSmileyTexts;
	private final Pattern mPattern;
	private final HashMap<String, Integer> mSmileyToRes;
	private HashMap<Integer, String> mResToSmiley;

	private ExpressionUtil(Context context) {
		mContext = context;
		mSmileyTexts = mContext.getResources().getStringArray(DEFAULT_SMILEY_TEXTS);
		mSmileyToRes = buildSmileyToRes();
		mPattern = buildPattern();
	}

	public static final int[] DEFAULT_SMILEY_RES_IDS = { R.drawable.expression_fendou,
			R.drawable.expression_qiaoda, R.drawable.expression_liuhan, R.drawable.expression_suai,
			R.drawable.expression_zuichan, R.drawable.expression_shuaizai,
			R.drawable.expression_xianwen, R.drawable.expression_jushou,
			R.drawable.expression_zhadan, R.drawable.expression_ziyan,
			R.drawable.expression_shengqi, R.drawable.expression_ganga,
			R.drawable.expression_shuizhao, R.drawable.expression_aoman,
			R.drawable.expression_qiang, R.drawable.expression_koushui,
			R.drawable.expression_wunai, R.drawable.expression_weiqu, R.drawable.expression_yukuai,
			R.drawable.expression_zhayan, R.drawable.expression_maimeng,
			R.drawable.expression_kelian, R.drawable.expression_yiwen,
			R.drawable.expression_qingxia, R.drawable.expression_aixin,
			R.drawable.expression_xinsui, R.drawable.expression_penxue,
			R.drawable.expression_touyun, R.drawable.expression_chouyan,
			R.drawable.expression_tushe, R.drawable.expression_daku, R.drawable.expression_chifan,
			R.drawable.expression_shaoxiang, R.drawable.expression_danteng,
			R.drawable.expression_huoda, R.drawable.expression_bushuang,
			R.drawable.expression_ding, R.drawable.expression_ruo, R.drawable.expression_woshou,
			R.drawable.expression_ok, R.drawable.expression_gouyin, R.drawable.expression_guzhang,
			R.drawable.expression_shengli, R.drawable.expression_gongxi,
			R.drawable.expression_chajin, R.drawable.expression_aini, R.drawable.expression_no };

	public static final int DEFAULT_SMILEY_TEXTS = R.array.smiley_array;

	private HashMap<String, Integer> buildSmileyToRes() {
		if (DEFAULT_SMILEY_RES_IDS.length != mSmileyTexts.length) {
			throw new IllegalStateException("Smiley resource ID/text mismatch");
		}
		HashMap<String, Integer> smileyToRes = new HashMap<String, Integer>(mSmileyTexts.length);
		mResToSmiley = new HashMap<Integer, String>(mSmileyTexts.length);
		for (int i = 0; i < mSmileyTexts.length; i++) {
			smileyToRes.put(mSmileyTexts[i], DEFAULT_SMILEY_RES_IDS[i]);
			mResToSmiley.put(DEFAULT_SMILEY_RES_IDS[i], mSmileyTexts[i]);
		}
		return smileyToRes;
	}

	// 构建正则表达式
	private Pattern buildPattern() {
		StringBuilder patternString = new StringBuilder(mSmileyTexts.length * 3);
		patternString.append('(');
		for (String s : mSmileyTexts) {
			patternString.append(Pattern.quote(s));
			patternString.append('|');
		}
		patternString.replace(patternString.length() - 1, patternString.length(), ")");
		return Pattern.compile(patternString.toString());
	}

	// 根据文本替换成图片
	public CharSequence strToSmiley(CharSequence text) {
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		Matcher matcher = mPattern.matcher(text);
		while (matcher.find()) {
			int resId = mSmileyToRes.get(matcher.group());
			Drawable drawable = mContext.getResources().getDrawable(resId);
			drawable.setBounds(0, 0, (int) HelperFunc.dip2px(20), (int) HelperFunc.dip2px(20));// 这里设置图片的大小
			ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
			builder.setSpan(imageSpan, matcher.start(), matcher.end(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return builder;
	}

	// 根据图片替换成文本
	public CharSequence smileyToStr(int res) {
		SpannableStringBuilder builder = null;
		if (mResToSmiley.containsKey(res)) {
			builder = new SpannableStringBuilder(mResToSmiley.get(res));
		}
		return builder;
	}

	public int getSize() {
		return mSmileyToRes.size();
	}
}
