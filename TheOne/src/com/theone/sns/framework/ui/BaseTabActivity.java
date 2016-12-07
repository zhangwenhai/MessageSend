/*
 * 文件名: BaseTabActivity.java
 * 描    述: TabActivity基类
 * 创建人: zhouyujun
 */
package com.theone.sns.framework.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.TabActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.theone.sns.framework.logic.ILogic;

/**
 * TabActivity基类
 * 
 * @author zhouyujun
 */
@SuppressWarnings("deprecation")
public abstract class BaseTabActivity extends TabActivity {

	private static final String TAG = "BaseTabActivity";

	/**
	 * 该activity持有的handler类
	 */
	private Handler mHandler = null;

	/**
	 * 是否独自控制logic监听
	 */
	private boolean isPrivateHandler = false;

	/**
	 * 缓存持有的logic对象的集合
	 */
	private final List<ILogic> mLogicList = new ArrayList<ILogic>();

	/**
	 * 退出框dialog
	 */
	private Dialog mExitDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!isInit()) {
			Log.e(TAG,
					"Launched the first should be the LauncheActivity's subclass:"
							+ this.getClass().getName(), new Throwable());
			return;
		}

		if (!isPrivateHandler()) {
			BaseActivity.getLogicBuilder().addHandlerToAllLogics(getHandler());
		}
		try {
			initLogics();
		} catch (Exception e) {
			Log.e(TAG, "Init logics failed :" + e.getMessage(), e);
		}
	}

	protected Handler getHandler() {
		if (mHandler == null) {
			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					BaseTabActivity.this.handleStateMessage(msg);
				}
			};
		}
		return mHandler;
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
		return isPrivateHandler;
	}

	/**
	 * 初始化logic的方法，由子类实现<BR>
	 * 在该方法里通过getLogicByInterfaceClass获取logic对象
	 */
	protected abstract void initLogics();

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
	 * activity的释放的方法<BR>
	 * 在这里对所有加载到logic中的handler进行释放
	 *
	 * @see android.app.ActivityGroup#onDestroy()
	 */
	protected void onDestroy() {
		Log.d(TAG, "onDestroy()");
		Handler handler = getHandler();
		if (handler != null) {
			if (mLogicList.size() > 0 && isPrivateHandler()) {
				for (ILogic logic : mLogicList) {
					logic.removeHandler(handler);
				}
			} else if (BaseActivity.getLogicBuilder() != null) {
				BaseActivity.getLogicBuilder()
						.removeHandlerToAllLogics(handler);
			}

		}

		if (null != mExitDialog && mExitDialog.isShowing()) {
			mExitDialog.dismiss();
		}

		super.onDestroy();
	}
}
