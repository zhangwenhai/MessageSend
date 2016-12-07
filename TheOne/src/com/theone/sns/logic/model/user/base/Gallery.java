package com.theone.sns.logic.model.user.base;

import com.theone.sns.framework.model.BaseModel;

@SuppressWarnings("serial")
public class Gallery extends BaseModel {

	/**
	 * 微博条目ID
	 */
	public String _id;

	/**
	 * 照片URL或者视频URL
	 */
	public String url;

	/**
	 * 是否是视频
	 */
	public boolean is_video;

	/**
	 * 微博评论描述
	 */
	public String desc;

	/**
	 * 可见性(所有人,密友,关注的人的可见性)
	 */
	public String visibility;

	/**
	 * Hot,New
	 */
	public String type;
}
