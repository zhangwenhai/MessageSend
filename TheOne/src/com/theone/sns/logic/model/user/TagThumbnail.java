package com.theone.sns.logic.model.user;

import java.util.List;

import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.user.base.TagForThumbnail;

@SuppressWarnings("serial")
public class TagThumbnail extends BaseModel {

	public TagForThumbnail tag;

	public int tagged_by;

	public List<String> thumbnails;
}
