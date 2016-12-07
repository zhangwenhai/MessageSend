package com.theone.sns.util.uiwidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.theone.sns.R;

public class IphoneStyleAlertDialogBuilder implements IphoneStyleImpl {

	LinearLayout m_popUpView = null;
	AlertDialog m_popup = null;
	Activity activity = null;
	TextView m_titleTextView = null;
	HashMap<Integer, Button> sortHashMap = new HashMap<Integer, Button>();

	public IphoneStyleAlertDialogBuilder(Activity activity) {
		this.activity = activity;
		initPopLayout();
		addTextView();
	}

	private void initPopLayout() {
		m_popUpView = new LinearLayout(activity);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		m_popUpView.setLayoutParams(lp);
		m_popUpView.setPadding(20, 20, 20, 20);
		m_popUpView.setOrientation(LinearLayout.VERTICAL);

	}

	@Override
	public void setTitle(String text) {
		if (m_titleTextView != null && text != null) {
			m_titleTextView.setVisibility(View.VISIBLE);
			m_titleTextView.setText(text);
		}
	}

	@Override
	public void setEmojiTitle(String text) {
		if (m_titleTextView != null && text != null) {
			m_titleTextView.setVisibility(View.VISIBLE);
			m_titleTextView.setText(text);
		}
	}

	@Override
	public void setTitle(int resId) {
		if (activity != null && m_titleTextView != null) {
			m_titleTextView.setVisibility(View.VISIBLE);
			m_titleTextView.setText(activity.getResources().getText(resId));
		}
	}

	@Override
	public void addItem(int menuId, String text, View.OnClickListener listener) {
		Button btn = new Button(activity);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 20, 0, 0);
		btn.setLayoutParams(lp);
		btn.setGravity(Gravity.CENTER);
		btn.setText(text);
		int color = activity.getResources().getColor(R.color.navy_blue);
		setTextColor(btn, COLOR_BLACK);
		btn.setTextSize(18);
		btn.setOnClickListener(listener);
		setSelector(btn);
		sortHashMap.put(menuId, btn);
	}
	
	@Override
	public void addItem(int menuId, String text, int colorResId,
			View.OnClickListener listener) {
		Button btn = new Button(activity);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 20, 0, 0);
		btn.setLayoutParams(lp);
		btn.setGravity(Gravity.CENTER);
		btn.setText(text);
		int color = activity.getResources().getColor(R.color.navy_blue);
		setTextColor(btn, colorResId);
		btn.setTextSize(18);
		btn.setOnClickListener(listener);
		setSelector(btn);
		sortHashMap.put(menuId, btn);
	}

	@Override
	public void addItem(int menuId, String text, int colorResId, int textStyle,
			View.OnClickListener listener) {
		Button btn = new Button(activity);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 20, 0, 0);
		btn.setLayoutParams(lp);
		btn.setGravity(Gravity.CENTER);
		btn.setText(text);
		btn.setTextSize(18);
		setTextColor(btn, colorResId);
		setTextType(btn, textStyle);
		setSelector(btn);
		btn.setOnClickListener(listener);
		sortHashMap.put(menuId, btn);
	}

	@Override
	public void addCancelItem(int menuId) {
		Button btn = new Button(activity);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 20, 0, 0);
		btn.setLayoutParams(lp);
		btn.setGravity(Gravity.CENTER);
		btn.setText(R.string.Cancel);
		btn.setTextSize(18);
		setTextColor(btn, COLOR_BLACK);
		setTextType(btn, TEXT_TYPE_BOLD);
		setSelector(btn);
		btn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		sortHashMap.put(menuId, btn);
	}

	@Override
	public void removeItem(int menuId) {
		m_popUpView.removeViewAt(menuId);
	}

	@Override
	public void show() {
		initPopWindow();
		if (activity != null && m_popup != null) {
			m_popup.show();
		}
	}

	/**
	 * Init pop Window
	 */
	private void initPopWindow() {
		if (m_popup == null) {
			// add all view to list for sort .
			List<Entry<Integer, Button>> list = getButtonListSort(sortHashMap);
			for (int i = 0; i < list.size(); i++) {
				m_popUpView.addView(list.get(i).getValue(), i + 1);
			}
			m_popUpView.setBackgroundColor(activity.getResources().getColor(
					R.color.iphone_style_bg_color));
			m_popup = new CustomPopDialog(activity, m_popUpView);
			m_popup.setCanceledOnTouchOutside(true);
		}
	}

	/**
	 * add iPhone style alert dialog title
	 */
	private void addTextView() {
		m_titleTextView = new TextView(activity);
		// set margin top 5 bottom 10
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 5, 0, 10);
		m_titleTextView.setLayoutParams(lp);
		// set shadow
		int shadowColor = activity.getResources().getColor(R.color.black);
		// set text color
		int textColor = activity.getResources().getColor(R.color.gray_noti);
		m_titleTextView.setTextColor(textColor);
		// set gravity
		m_titleTextView.setGravity(Gravity.CENTER);
		// set text size
		m_titleTextView.setTextSize(15f);
		// set text gone
		m_titleTextView.setVisibility(View.GONE);
		// add text at LinearLayout
		m_popUpView.addView(m_titleTextView, 0);
	}

	@Override
	public void destory() {
		// TODO:destory layout.
	}

	/**
	 * set the menu item selector
	 *
	 * @param btn
	 */
	private void setSelector(Button btn) {
		StateListDrawable drawables = new StateListDrawable();
		Drawable normalDrawable = activity.getResources().getDrawable(
				R.drawable.btn_white_normal);
		Drawable selectedDrawable = activity.getResources().getDrawable(
				R.drawable.btn_white_pressed);
		drawables.addState(new int[] { android.R.attr.state_pressed },
				selectedDrawable);
		drawables.addState(new int[] {}, normalDrawable);
		btn.setBackgroundDrawable(drawables);
	}

	private void setTextType(Button btn, int type) {
		switch (type) {
		case TEXT_TYPE_DEFAULT: {
			break;
		}
		case TEXT_TYPE_BOLD: {
			btn.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
			break;
		}
		case TEXT_TYPE_BOLD_ITALIC: {
			btn.setTypeface(Typeface.DEFAULT, Typeface.BOLD_ITALIC);
			break;
		}
		case TEXT_TYPE_ITALIC: {
			btn.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
			break;
		}
		default:
			break;
		}
	}

	private void setTextColor(Button btn, int colorType) {
		// ColorStateList colorState = null;
		int color = 0;
		switch (colorType) {
		case COLOR_RED: {
			color = activity.getResources().getColor(
					R.color.iphone_style_red_color);
			break;
		}
		case COLOR_BLUE: {
			color = activity.getResources().getColor(R.color.navy_blue);
			break;
		}
		case COLOR_BLACK: {
			color = activity.getResources().getColor(R.color.black);
			break;
		}
		default:
			color = activity.getResources().getColor(R.color.black);
			break;
		}
		btn.setTextColor(color);
	}

	@Override
	public void dismiss() {
		m_popup.dismiss();
	}

	private List<Entry<Integer, Button>> getButtonListSort(
			Map<Integer, Button> sortButtonList) {
		List<Entry<Integer, Button>> sortList = new ArrayList<Entry<Integer, Button>>(
				sortButtonList.entrySet());
		// sort
		Collections.sort(sortList, new Comparator<Entry<Integer, Button>>() {
			public int compare(Entry<Integer, Button> o1,
					Entry<Integer, Button> o2) {
				return (int) (o1.getKey() - o2.getKey());
			}
		});
		return sortList;
	}

}

interface IphoneStyleImpl {

	static final int TEXT_TYPE_DEFAULT = 0;
	static final int TEXT_TYPE_BOLD = 1;
	static final int TEXT_TYPE_ITALIC = 2;
	static final int TEXT_TYPE_BOLD_ITALIC = 3;

	static final int COLOR_RED = 1;
	static final int COLOR_BLUE = 2;
	static final int COLOR_BLACK = 3;

	public void setTitle(int ResId);

	public void setTitle(String text);

	public void setEmojiTitle(String text);

	public void addItem(int menuId, String text, View.OnClickListener listener);
	
	public void addItem(int menuId, String text, int colorResId,
			View.OnClickListener listener);

	/**
	 *
	 * @param menuId
	 *            Custom unique id ,sort the item<br>
	 * @param text
	 * @param colorResId
	 *            Use IphoneStyleAlertDialogBuilder.COLOR_BLUE or
	 *            IphoneStyleAlertDialogBuilder.COLOR_RED<br>
	 * @param testStyle
	 *            Use IphoneStyleAlertDialogBuilder.TEXT_TYPE_DEFAULT or
	 *            IphoneStyleAlertDialogBuilder.TEXT_TYPE_BOLD or others<br>
	 * @param listener
	 */
	public void addItem(int menuId, String text, int colorResId, int testStyle,
			View.OnClickListener listener);

	public void addCancelItem(int menuId);

	public void removeItem(int menuId);

	public void show();

	public void dismiss();

	public void destory();

}
