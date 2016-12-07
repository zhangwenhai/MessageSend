package com.theone.sns.logic.model.user.base;

import com.theone.sns.framework.model.BaseModel;

@SuppressWarnings("serial")
public class Label extends BaseModel {

	/**
	 * 交友目的或者兴趣标签名称
	 */
	public String name;

	/**
	 * 是否和我的兴趣一样
	 */
	public boolean me;
}
