/*
 * 文件名: LogicBuilder.java
 * 创建人: zhouyujun
 */
package com.theone.sns.logic;

import android.content.Context;

import com.theone.sns.framework.logic.BaseLogicBuilder;
import com.theone.sns.logic.account.IAccountLogic;
import com.theone.sns.logic.account.IRegisterLogic;
import com.theone.sns.logic.account.impl.AccountLogicImpl;
import com.theone.sns.logic.account.impl.RegisterLogicImpl;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.chat.impl.ChatLogicImpl;
import com.theone.sns.logic.mblog.IMBlogLogic;
import com.theone.sns.logic.mblog.impl.MBlogLogicImpl;
import com.theone.sns.logic.user.ISettingLogic;
import com.theone.sns.logic.user.IUserLogic;
import com.theone.sns.logic.user.impl.SettingLogicImpl;
import com.theone.sns.logic.user.impl.UserLogicImpl;

public class LogicBuilder extends BaseLogicBuilder {

	private static BaseLogicBuilder instance;

	/**
	 * 构造方法，继承BaseLogicBuilder的构造方法，由父类BaseLogicBuilder对所有logic进行初始化。
	 * 
	 * @param context
	 *            系统的context对象
	 */
	private LogicBuilder(Context context) {
		super(context);
	}

	/**
	 *
	 * 获取BaseLogicBuilder单例<BR>
	 * 单例模式
	 *
	 * @param context
	 *            系统的context对象
	 * @return BaseLogicBuilder 单例对象
	 */
	public static synchronized BaseLogicBuilder getInstance(Context context) {
		if (null == instance) {
			instance = new LogicBuilder(context);
		}
		return instance;
	}

	/**
	 * LogicBuidler的初始化方法，系统初始化的时候执行<BR>
	 *
	 * @param context
	 *            系统的context对象
	 */
	protected void init(Context context) {
		registerAllLogics(context);
	}

	/**
	 * 所有logic对象初始化及注册的方法<BR>
	 */
	private void registerAllLogics(Context context) {
		registerLogic(IAccountLogic.class, new AccountLogicImpl());
		registerLogic(IRegisterLogic.class, new RegisterLogicImpl());
		registerLogic(IMBlogLogic.class, new MBlogLogicImpl());
		registerLogic(IUserLogic.class, new UserLogicImpl());
		registerLogic(ISettingLogic.class, new SettingLogicImpl());
		registerLogic(IChatLogic.class, new ChatLogicImpl());
	}
}
