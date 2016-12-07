package com.theone.sns.util;

import android.content.Context;

import com.theone.sns.R;

import java.lang.ref.SoftReference;

public class CountryUtil {

	/**
	 * 尝试获取最大个数
	 * */
	private static final int MAX_RETRY = 3;

	private static CountryUtil mCountry;

	private Context mContext;

	/**
	 * 省份
	 * */
	private SoftReference<String[]> mGroups;

	/**
	 * 城市
	 * */
	private SoftReference<String[][]> mChildren;

	/**
	 * 获取city尝试次数
	 * */
	private int mcityTrytime = 0;

	/**
	 * 获取省份尝试次数
	 * */
	private int mProviceTrytime = 0;

	private CountryUtil() {

	}

	private CountryUtil(Context mContext) {
		this.mContext = mContext;
		initProvince();
		initCitys();
	}

	/**
	 * 初始化城市
	 * */
	private void initCitys() {

		mChildren = new SoftReference<String[][]>(new String[][] {
				getListFrom(R.array.message_buxian),
                getListFrom(R.array.message_anhui),
				getListFrom(R.array.message_beijing),
				getListFrom(R.array.message_chongqing),
				getListFrom(R.array.message_fujian),
				getListFrom(R.array.message_gansu),
				getListFrom(R.array.message_guangdong),
				getListFrom(R.array.message_guangxi),
				getListFrom(R.array.message_guizhou),
				getListFrom(R.array.message_hainan),
				getListFrom(R.array.message_hebei),
				getListFrom(R.array.message_heilongjiang),
				getListFrom(R.array.message_henan),
				getListFrom(R.array.message_xianggang),
				getListFrom(R.array.message_hubei),
				getListFrom(R.array.message_hunan),
				getListFrom(R.array.message_jiangsu),
				getListFrom(R.array.message_jiangxi),
				getListFrom(R.array.message_jilin),
				getListFrom(R.array.message_liaoning),
				getListFrom(R.array.message_aomen),
				getListFrom(R.array.message_neimenggu),
				getListFrom(R.array.message_ningxia),
				getListFrom(R.array.message_qinghai),
				getListFrom(R.array.message_shanxi_jin),
				getListFrom(R.array.message_shandong),
				getListFrom(R.array.message_shanghai),
				getListFrom(R.array.message_shanxi_shan),
				getListFrom(R.array.message_sichuan),
				getListFrom(R.array.message_taiwan),
				getListFrom(R.array.message_tianjin),
				getListFrom(R.array.message_xinjiang),
				getListFrom(R.array.message_xizang),
				getListFrom(R.array.message_yunnan),
				getListFrom(R.array.message_zhejiang),
				getListFrom(R.array.message_others) });
	}

	/**
	 * 初始化省份
	 * */
	private void initProvince() {
		mGroups = new SoftReference<String[]>(mContext.getResources()
				.getStringArray(R.array.message_province));

	}

	/**
	 *
	 * 从资源数组中获取字符串数组
	 *
	 * @param arrId
	 *            字符数组ID
	 * @return 字符数组
	 */
	public String[] getListFrom(int arrId) {
		return mContext.getResources().getStringArray(arrId);
	}

	/**
	 * 获取城市列表 尝试三次否则返回空数据
	 *
	 * @return 返回城市列表
	 * */

	public synchronized String[][] getAllCitys() {
		if (mChildren.get() == null) {
			++mcityTrytime;

			if (mcityTrytime == MAX_RETRY) {
				mcityTrytime = 0;
				return new String[][] {};
			}
			initCitys();
			return getAllCitys();
		}

		mcityTrytime = 0;
		return mChildren.get();
	}

	/**
	 * 获取省份列表
	 *
	 * @return 返回所有省份
	 * */
	public synchronized String[] getAllProvince() {
		if (mGroups.get() == null) {
			++mProviceTrytime;
			if (mProviceTrytime == MAX_RETRY) {
				mProviceTrytime = 0;
				return new String[] {};
			}
			initProvince();
			return getAllProvince();
		}

		mProviceTrytime = 0;
		return mGroups.get();
	}

	/**
	 * 获取图片加载实例对象
	 *
	 * @param mContext
	 *            上下文
	 * 
	 * @return 返回CountryUtil的实例
	 */
	public static synchronized CountryUtil getInstance(Context mContext) {
		if (null == mCountry) {
			mCountry = new CountryUtil(mContext);
		}
		return mCountry;
	}
}
