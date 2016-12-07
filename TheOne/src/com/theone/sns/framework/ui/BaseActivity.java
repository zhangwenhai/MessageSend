/*
 * 文件名: BaseActivity.java
 * 创建人: zhouyujun
 */
package com.theone.sns.framework.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityGroup;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.theone.sns.framework.logic.BaseLogicBuilder;
import com.theone.sns.framework.logic.ILogic;
import com.theone.sns.framework.logic.ILogicBuilder;

/**
 * Activity的抽象基类<BR>
 * 
 * @author zhouyujun
 */
public abstract class BaseActivity extends ActivityGroup {

	private static final String TAG = "BaseActivity";

	/**
	 * toast的时间定义，长时间5s
	 */
	public static final int TOAST_TIME_LONG = 5000;

	/**
	 * toast的时间定义，短时间2s
	 */
	public static final int TOAST_TIME_SHORT = 2000;

	/**
	 * 系统的所有logic的缓存创建管理类
	 */
	private static BaseLogicBuilder mLogicBuilder = null;

	/**
	 * 该activity持有的handler类
	 */
	private Handler mHandler = null;

	/**
	 * 是否独自控制logic监听
	 */
	private boolean mIsPrivateHandler = false;

	/**
	 * 是否只有当前Activity对logic监听
	 */
	private boolean mIsPrivateActivityHandler = false;

	/**
	 * 缓存持有的logic对象的集合
	 */
	private final List<ILogic> mLogicList = new ArrayList<ILogic>();

	/**
	 * Acitivity的初始化方法<BR>
	 * 
	 * @param savedInstanceState
	 *            Bundle对象
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (!isInit()) {

			Log.e(TAG,
					"Launched the first should be the LauncheActivity's subclass:"
							+ this.getClass().getName(), new Throwable());
			return;
		}

		if (!isPrivateHandler()) {
			BaseActivity.mLogicBuilder.addHandlerToAllLogics(getHandler());
		}
		try {
			initLogics();
		} catch (Exception e) {
			Toast.makeText(this.getApplicationContext(), "Init logics failed :"
					+ e.getMessage(), Toast.LENGTH_LONG);
			Log.e(TAG, "Init logics failed :" + e.getMessage(), e);
		}
	}

	/**
	 * 获取hander对象<BR>
	 *
	 * @return 返回handler对象
	 */
	protected Handler getHandler() {
		if (mHandler == null) {
			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					if (!isFinishing()) {
						BaseActivity.this.handleStateMessage(msg);
					}
				}
			};
		}

		Log.d(TAG, "mHandler=" + mHandler);
		return mHandler;
	}

	/**
	 * activity是否已经初始化，加载了mLogicBuilder对象<BR>
	 * 判断activiy中是否创建了mLogicBuilder对象
	 *
	 * @return 是否加载了mLogicBuilder
	 */
	protected final boolean isInit() {
		return BaseActivity.mLogicBuilder != null;
	}

	/**
	 * 判断UI是否独自管理对logic的handler监听<BR>
	 *
	 * @return 是否是私有监听的handler
	 */
	protected boolean isPrivateHandler() {
		return mIsPrivateHandler;
	}

	/**
	 * 判断UI设置是否只有当前Activity对logic监听<BR>
	 *
	 * @return 是否是私有监听的handler
	 */
	protected boolean isPrivateActivityHandler() {
		return mIsPrivateActivityHandler;
	}

	/**
	 * 设置UI是否独自管理对logic的handler监听,设置是否只有当前Activity对logic监听
	 *
	 * @param isPrivateHandler
	 *            设置UI是否独自管理对logic的handler监听
	 * @param isPrivateActivityHandler
	 *            设置是否只有当前Activity对logic监听
	 */
	public void setPrivateHandler(boolean isPrivateHandler,
			boolean isPrivateActivityHandler) {
		this.mIsPrivateHandler = isPrivateHandler;

		this.mIsPrivateActivityHandler = isPrivateActivityHandler;
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
		ILogic logic = mLogicBuilder.getLogicByInterfaceClass(interfaceClass);
		Log.d(TAG, "isPrivateHandler()=" + isPrivateHandler());
		Log.d(TAG, "isPrivateActivityHandler()=" + isPrivateActivityHandler());
		if (logic == null) {
			Toast.makeText(this.getApplicationContext(),
					"Not found logic by interface class (" + interfaceClass
							+ ")", Toast.LENGTH_LONG);
			Log.e(TAG, "Not found logic by interface class (" + interfaceClass
					+ ")", new Throwable());
			return null;
		}
		if (isPrivateHandler()) {
			if (isPrivateActivityHandler()) {
				logic.addSingleHandler(getHandler());
			} else {
				logic.addHandler(getHandler());
				mLogicList.add(logic);
			}
		}

		return logic;
	}

	/**
	 * 设置全局的logic建造管理类<BR>
	 *
	 * @param logicBuilder
	 *            logic建造管理类
	 */
	public static final void setLogicBuilder(BaseLogicBuilder logicBuilder) {
		BaseActivity.mLogicBuilder = logicBuilder;
	}

	/**
	 * 获取全局的LogicBuilder对象<BR>
	 *
	 * @return 返回LogicBuilder对象
	 */
	public static ILogicBuilder getLogicBuilder() {
		return BaseActivity.mLogicBuilder;
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

	public void removeHandler() {
		Handler handler = getHandler();
		Log.d(TAG, "removeHandler() handler=" + handler);
		Log.d(TAG, "isPrivateHandler() handler=" + isPrivateHandler());
		if (handler != null) {
			if (mLogicList.size() > 0 && isPrivateHandler()) {
				for (ILogic logic : mLogicList) {
					logic.removeHandler(handler);
				}
			} else if (mLogicBuilder != null) {
				mLogicBuilder.removeHandlerToAllLogics(handler);
			}
		}
		mHandler = null;
	}

	/**
	 * activity的释放的方法<BR>
	 * 在这里对所有加载到logic中的handler进行释放
	 * 
	 * @see android.app.ActivityGroup#onDestroy()
	 */
	protected void onDestroy() {
		removeHandler();
		Log.d(TAG, "onDestroy() removeHandler()!!!!!!!!!!!!!!!!!!!!!!!");
		super.onDestroy();
	}
}
