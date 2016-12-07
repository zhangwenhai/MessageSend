/*
 * 文件名: ILogicBuilder.java
 * 创建人: zhouyujun
 */
package com.theone.sns.framework.logic;

import android.os.Handler;

/**
 * LogicBuilder的接口<BR>
 * 
 * @author zhouyujun
 */
public interface ILogicBuilder {

	/**
	 * 根据logic接口类返回logic对象<BR>
	 * 如果缓存没有则返回null。
	 * 
	 * @param interfaceClass
	 *            logic接口类
	 * @return logic对象
	 */
	public ILogic getLogicByInterfaceClass(Class<?> interfaceClass);

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 *
	 * @param handler
	 *            Handler
	 */
	public void addHandlerToAllLogics(Handler handler);

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 *
	 * @param handler
	 *            Handler
	 */
	public void removeHandlerToAllLogics(Handler handler);
}
