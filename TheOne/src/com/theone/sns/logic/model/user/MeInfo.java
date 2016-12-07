package com.theone.sns.logic.model.user;

import java.util.List;

import com.theone.sns.framework.model.BaseModel;

@SuppressWarnings("serial")
public class MeInfo extends BaseModel {

	/**
	 * 昵称
	 */
	public String name;

	/**
	 * 头像URL
	 */
	public String avatar_url;

	/**
	 * 属性
	 */
	public String character;

	/**
	 * 角色
	 */
	public String role;

	/**
	 * 是否有形婚意向
	 */
	public boolean marriage;

	/**
	 * 生日
	 */
	public String birthday;

	/**
	 * 地点
	 */
	public String region;

	/**
	 * 身高
	 */
	public String height;

	/**
	 * 交友目的
	 */
	public List<String> purposes;

	/**
	 * 兴趣爱好
	 */
	public List<String> interests;
}