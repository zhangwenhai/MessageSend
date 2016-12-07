/*
 * 文件名: LauncheActivity.java
 * 创建人: zhouyujun
 */
package com.theone.sns.framework.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.theone.sns.framework.logic.BaseLogicBuilder;

/**
 * 系统的Activity的启动类<BR>
 * 第一个启动的Activity必须继承，而且其他Activity不要继承
 * 
 * @author zhouyujun
 */
public abstract class LauncheActivity extends BaseActivity {

	private static final String TAG = "LauncheActivity";

	/**
	 * 所有Activity的初始化方法，所有Activity的创建从这个OnCreate方法开始
	 * 
	 * @param savedInstanceState
	 *            传入的Bundle对象
	 */
	protected void onCreate(Bundle savedInstanceState) {

		// 判断activiy中是否创建了mLogicBuilder对象，如果没有创建先创建
		if (!isInit()) {
			// 初始化系统？目前没有实现体
			initSystem(LauncheActivity.this);

			// 获得设置全局的logic建造管理对象
			BaseLogicBuilder logicBuilder = createLogicBuilder(this
					.getApplicationContext());

			// 设置全局的logic建造管理类
			super.setLogicBuilder(logicBuilder);
			Log.i(TAG, "Load logic builder successful");
		}

		super.onCreate(savedInstanceState);
	}

	/**
	 * 系统的初始化方法<BR>
	 *
	 * @param context
	 *            系统的context对象
	 */
	protected abstract void initSystem(Context context);

	/**
	 * Logic建造管理类需要创建的接口<BR>
	 * 需要子类继承后，指定Logic建造管理类具体实例
	 *
	 * @param context
	 *            系统的context对象
	 * @return Logic建造管理类具体实例
	 */
	protected abstract BaseLogicBuilder createLogicBuilder(Context context);

}