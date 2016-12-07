package com.theone.sns.component.location.poi;

import com.theone.sns.framework.model.BaseModel;

@SuppressWarnings("serial")
public class LocalPoiInfo extends BaseModel {

	/**
	 * 经度在数组前面
	 */
	public String longitude;

	/**
	 * 纬度在数组后面
	 */
	public String latitude;

	public String city;

	public String address;

	public String name;
}
