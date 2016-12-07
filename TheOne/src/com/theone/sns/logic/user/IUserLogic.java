package com.theone.sns.logic.user;

import java.util.List;

import com.theone.sns.component.contact.Contact;
import com.theone.sns.logic.model.mblog.MBlogTag;

public interface IUserLogic {

	/**
	 * 从数据库读取头像模式缓存,直接展示
	 */
	String findUserGalleryFromDB(String type);

	/**
	 * 头像模式下,发现TAB中的最新，最热,附近用户查询
	 */
	String findUserGallery(String type, String nextMaxId, int count);

	/**
	 * 获取系统推荐的好友列表
	 */
	String getRecommendUserList();

	/**
	 * 根据微博ID获取该微博的点赞列表
	 */
	String getLikesListByMBlogId(String mblogId, String nextMaxId, int count);

	/**
	 * 搜索用户(用户名,手机号,邮箱)
	 */
	String getUserListBySearch(String searchName, String nextMaxId, int count);

	/**
	 * 上传用户通讯录，获取用户是否注册应用等信息
	 */
	String getMatchContactList(List<Contact> contacts);

	/**
	 * 根据用户ID获取用户信息
	 */
	String getUserByUserId(String userId);

	/**
	 * 根据用户ID获取该用户相册列表
	 */
	String getUserGalleryByUserId(String userId, String nextMaxId, int count);

	/**
	 * 根据用户ID获取该用户微博列表
	 */
	String getMBlogListByUserId(String userId, String nextMaxId, int count);

	/**
	 * 根据用户ID和Tag获取该用户微博列表
	 */
	String getMBlogListByUserIdAndTag(String userId, MBlogTag tag,
			String nextMaxId, int count);

	/**
	 * 获取圈我的列表
	 */
	String getMentionedGalleryListByUserId(String userId, String nextMaxId,
			int count);

	/**
	 * 根据签分类获取缩略图标
	 */
	String getTagThumbnailsListByUserId(String userId, String nextMaxId,
			int count);

	/**
	 * 根据用户ID获取用户备注名
	 */
	String getAliasByUserId(String userId);

	/**
	 * 根据用户ID更新用户备注名
	 */
	String updateAliasByUserId(String userId, String alias);

	/**
	 * 列出用户关系，包含关注、粉丝、密友、屏蔽
	 */
	String getRelationshipsList(String userId, String nextMaxId, int count,
			String action);

	/**
	 * 设置用户关系
	 */
	String setUserRelationship(String userId, String action);

	/**
	 * 从关注,粉丝中搜索圈人用户
	 */
	String getCircleSearch(String search, String nextMaxId, int count,
			String action);
	
	/**
	 * 从关注,粉丝中搜索圈人用户
	 */
	String getCircleSearch(String search);
}
