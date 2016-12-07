package com.theone.sns.ui.base;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.theone.sns.R;
import com.theone.sns.framework.logic.ILogic;
import com.theone.sns.framework.ui.BaseActivity;
import com.theone.sns.logic.LogicBuilder;

public abstract class BaseFragment extends Fragment {

	private static final String TAG = "BaseFragment";

	protected Activity context;

	private boolean isInit = false;

	private boolean isAlive;

	private Handler mHandler = null;

	protected boolean isNew = false;

	/**
	 * 是否独自控制logic监听
	 */
	private boolean mIsPrivateHandler = false;

	/**
	 * 缓存持有的logic对象的集合
	 */
	private final List<ILogic> mLogicList = new ArrayList<ILogic>();

	protected DisplayImageOptions options;

	protected DisplayImageOptions optionsForUserIcon;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.context = activity;

		initLogicHandler();

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.background)
				.showImageForEmptyUri(R.drawable.background)
				.showImageOnFail(R.drawable.background).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new RoundedBitmapDisplayer(10)).build();

		optionsForUserIcon = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.home_user_icon)
				.showImageForEmptyUri(R.drawable.home_user_icon)
				.showImageOnFail(R.drawable.home_user_icon).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		isAlive = true;

		initLogicHandler();

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	public void onResume() {
		super.onResume();
		initLogicHandler();
	}

	@Override
	public void onDestroyView() {

		isAlive = false;
		super.onDestroyView();

		Handler handler = getHandler();
		if (handler != null) {

			if (mLogicList.size() > 0 && isPrivateHandler()) {
				for (ILogic logic : mLogicList) {
					logic.removeHandler(handler);
				}
			} else if (BaseActivity.getLogicBuilder() != null) {
				BaseActivity.getLogicBuilder()
						.removeHandlerToAllLogics(handler);

				isInit = false;

				mHandler = null;
			}
		}
	}

	public boolean isAlive() {
		return isAlive;
	}

	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	public final Handler getHandler() {

		if (mHandler == null) {
			mHandler = new Handler() {

				public void handleMessage(Message msg) {
					BaseFragment.this.handleStateMessage(msg);
				}
			};
		}
		return mHandler;
	}

	/**
	 * 初始化logic的方法，由子类实现<BR>
	 * 在该方法里通过getLogicByInterfaceClass获取logic对象
	 */
	protected abstract void initLogics();

	/**
	 * logic通过handler回调的方法<BR>
	 * 通过子类重载可以实现各个logic的sendMessage到handler里的回调方法
	 * 
	 * @param msg
	 *            Message对象
	 */
	protected void handleStateMessage(Message msg) {

	}

	/**
	 * activity是否已经初始化，加载了mLogicBuilder对象<BR>
	 * 判断activiy中是否创建了mLogicBuilder对象
	 * 
	 * @return 是否加载了mLogicBuilder
	 */
	protected final boolean isInit() {
		return BaseActivity.getLogicBuilder() != null;
	}

	/**
	 * 判断UI是否独自管理对logic的handler监听<BR>
	 * 
	 * @return 是否是私有监听的handler
	 */
	protected boolean isPrivateHandler() {
		return mIsPrivateHandler;
	}

	private void initLogicHandler() {

		if (!isInit()) {

			Log.e(TAG, "not init logic handler");

			BaseActivity.setLogicBuilder(LogicBuilder.getInstance(context));
		}

		if (isInit) {

			Log.d(TAG, "initLogicHandler has init");

			return;
		}

		isInit = true;

		if (!isPrivateHandler()) {
			BaseActivity.getLogicBuilder().addHandlerToAllLogics(getHandler());
		}

		try {
			initLogics();
		} catch (Exception e) {
			Log.e(TAG, "Init logics failed :" + e.getMessage(), e);
			return;
		}

		return;
	}

	/**
	 * 通过接口类获取logic对象<BR>
	 * 
	 * @param interfaceClass
	 *            接口类型
	 * @return logic对象
	 */
	protected final ILogic getLogicByInterfaceClass(Class<?> interfaceClass) {
		ILogic logic = BaseActivity.getLogicBuilder().getLogicByInterfaceClass(
				interfaceClass);
		if (isPrivateHandler()) {
			logic.addHandler(getHandler());
			mLogicList.add(logic);
		}
		if (logic == null) {
			Log.e(TAG, "Not found logic by interface class (" + interfaceClass
					+ ")", new Throwable());
			return null;
		}
		return logic;
	}

	protected Context getContext() {
		return context;
	}
}
