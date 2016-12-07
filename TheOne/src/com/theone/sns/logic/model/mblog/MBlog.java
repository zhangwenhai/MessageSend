package com.theone.sns.logic.model.mblog;

import java.util.List;

import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.mblog.base.AudioDesc;
import com.theone.sns.logic.model.mblog.base.Comment;
import com.theone.sns.logic.model.mblog.base.Photo;
import com.theone.sns.logic.model.mblog.base.Video;
import com.theone.sns.logic.model.user.User;

@SuppressWarnings("serial")
public class MBlog extends BaseModel {

	/**
	 * 微博唯一标识
	 */
	public String _id;

	/**
	 * 发表微博的用户信息
	 */
	public User owner;

	/**
	 * 微博照片
	 */
	public Photo photo;

	/**
	 * 音频描述微博照片
	 */
	public AudioDesc audio_desc;

	/**
	 * 微博视频
	 */
	public Video video;

	/**
	 * 微博描述
	 */
	public String text_desc;

	/**
	 * 微博标签
	 */
	public List<MBlogTag> tags;

	/**
	 * 微博地理位置
	 */
	public List<String> location;

	/**
	 * 是否已赞
	 */
	public boolean is_liked;

	/**
	 * 微博点赞用户
	 */
	public List<User> likes;

	/**
	 * 微博点赞人数
	 */
	public int likes_count;

	/**
	 * 微博评论
	 */
	public List<Comment> comments;

	/**
	 * 微博评论条数
	 */
	public int comments_count;

	/**
	 * 微博创建时间
	 */
	public String created_at;

	/**
	 * 微博类型(首页,附近,关注)
	 */
	public String mblog_type;
}
