package com.theone.sns.logic.model.user.base;

import com.theone.sns.framework.model.BaseModel;

@SuppressWarnings("serial")
public class Count extends BaseModel {

	/**
	 * 关注数
	 */
	public String following;

	/**
	 * 粉丝数
	 */
	public String followed_by;

	/**
	 * 密友数
	 */
	public String starred;

	/**
	 * 照片视频数
	 */
	public String media;

	/**
	 * 评论数
	 */
	public String comments;

	/**
	 * 被赞数
	 */
	public String liked_by;
}
