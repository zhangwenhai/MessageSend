package com.theone.sns.logic.model.mblog;

import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.mblog.base.LocationTag;
import com.theone.sns.logic.model.mblog.base.TextTag;
import com.theone.sns.logic.model.mblog.base.UserTag;

@SuppressWarnings("serial")
public class MBlogTag extends BaseModel {

	public String id;

	public TextTag text;

	public LocationTag location;

	public UserTag user;

	public float x;

	public float y;

	public String align;
}
