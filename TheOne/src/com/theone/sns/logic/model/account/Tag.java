package com.theone.sns.logic.model.account;

import java.util.List;

import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.mblog.base.LocationTag;
import com.theone.sns.logic.model.mblog.base.TextTag;
import com.theone.sns.logic.model.mblog.base.UserTag;

@SuppressWarnings("serial")
public class Tag extends BaseModel {

	public List<TextTag> text_tags;

	public List<LocationTag> location_tags;

	public List<UserTag> user_tags;
}
