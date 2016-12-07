package com.theone.sns.logic.model.user;

import java.util.List;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.user.base.Count;
import com.theone.sns.logic.model.user.base.Gallery;
import com.theone.sns.logic.model.user.base.Label;

@SuppressWarnings("serial")
public class User extends BaseModel {

	/**
	 * 用户唯一标识
	 */
	@SerializedName("_id")
	public String userId;

	/**
	 * 昵称
	 */
	public String name;

	/**
	 * 别名
	 */
	public String alias;

	/**
	 * 头像URL
	 */
	public String avatar_url;

	/**
	 * 地理位置
	 */
	public List<String> location;

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
	 * 关注数,粉丝数等
	 */
	public Count count;

	/**
	 * 相册列表(和media一样,只是服务不同的接口)
	 */
	public List<Gallery> gallery;

	/**
	 * 相册列表(和gallery一样，只是服务不同的接口)
	 */
	public List<Gallery> media;

	/**
	 * 地点
	 */
	public String region;

	/**
	 * 是否密友该用户(用于他人资料)
	 */
	public boolean is_starring;

	/**
	 * 是否关注该用户
	 */
	public boolean is_following;

	/**
	 * 是否被该用户关注(他是我的粉丝)
	 */
	public boolean is_followed_by;

	/**
	 * 是否屏蔽
	 */
	public boolean is_blocking;

	/**
	 * 是否可见我的发布
	 */
	public boolean is_hidden;

	/**
	 * 年龄
	 */
	public int age;

	/**
	 * 星座
	 */
	public String sign;

	/**
	 * 身高
	 */
	public String height;

	/**
	 * 最后离开时间
	 */
	public String away_at;

	/**
	 * 交友目的
	 */
	public List<String> purposes;

	/**
	 * 兴趣爱好
	 */
	public List<String> interests;

	/**
	 * 用户兴趣爱好或者交友目的(用于他人资料)
	 */
	public List<Label> labels;

	/**
	 * 组成员是否加入该组(用户聊天列表)
	 */
	public boolean isJoined;

	/**
	 * 该用户属性什么类型(头像模式时的三种类型,微博模式的三种类型)
	 */
	public String type;

	public boolean equals(Object o) {

		if (this == o) {

			return true;
		}

		if (!(o instanceof User) || null == o) {

			return false;
		}

		final User other = (User) o;

		if (TextUtils.isEmpty(other.userId)) {

			return false;

		} else if (other.userId.equals(this.userId)) {

			return true;

		} else {

			return false;
		}
	}
}