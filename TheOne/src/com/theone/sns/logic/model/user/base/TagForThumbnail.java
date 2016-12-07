package com.theone.sns.logic.model.user.base;

import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.mblog.base.LocationTag;
import com.theone.sns.logic.model.mblog.base.TextTag;
import com.theone.sns.logic.model.mblog.base.UserTag;

@SuppressWarnings("serial")
public class TagForThumbnail extends BaseModel {

	public String _id;

	public TextTag text;

	public LocationTag location;

	public UserTag user;
}
