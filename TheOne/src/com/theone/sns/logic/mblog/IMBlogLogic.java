package com.theone.sns.logic.mblog;

import com.theone.sns.logic.model.mblog.Filter;
import com.theone.sns.logic.model.mblog.MBlog;
import com.theone.sns.logic.model.mblog.MBlogTag;
import com.theone.sns.logic.model.mblog.PublishMBlog;

public interface IMBlogLogic {

	/**
	 * 从数据库读取微博缓存,直接展示
	 */
	String getFollowMBlogFromDB();

	/**
	 * 获取关注的好友微博列表
	 */
	String getFollowMBlogList(String nextMaxId, int count);

	/**
	 * 发布微博
	 */
	String publishMBlog(PublishMBlog publishMBlog);

	/**
	 * 从数据库获取动态筛选参数
	 */
	Filter getFilterFromDB();

	/**
	 * 更新微博筛选参数
	 */
	String updateMBlogFilter(Filter filter);

	/**
	 * 通过微博上的Tag，获取相同Tag的微博相册集合
	 */
	String tagToGallery(MBlogTag tag, String nextMaxId, int count);

	/**
	 * 从数据库获取发现TAB中的最新，最热,附近相册查询
	 */
	String findMBlogGalleryFromDB(final String type);

	/**
	 * 发现TAB中的最新，最热,附近相册查询
	 */
	String findMBlogGallery(String type, String nextMaxId, int count);

	/**
	 * 发现TAB中的搜索标签集合
	 */
	String findSearchGallery(String search, String nextMaxId, int count);

	/**
	 * 发现TAB中的搜索用户集合
	 */
	String findSearchUser(String search, String nextMaxId, int count);

	/**
	 * 搜索热门品牌标签
	 */
	String searchTags(String search, String nextMaxId, int count);

	/**
	 * 根据微博ID获取微博详情
	 */
	String getMBlogById(String mblogId);

	/**
	 * 根据微博ID删除微博
	 */
	String deleteMBlogById(String mblogId);

	/**
	 * 点赞/取消点赞
	 */
	String isLikesMBlog(String mblogId, boolean isLike, MBlog mBlog);

	/**
	 * 获取微博评论
	 */
	String getCommentsListByMBlogId(String mblogId, String nextMaxId, int count);

	/**
	 * 发表评论
	 */
	String publishComment(String mblogId, String targetUserId, String text);

	/**
	 * 删除评论
	 */
	String deleteComment(String mblogId, String commentId);
}
