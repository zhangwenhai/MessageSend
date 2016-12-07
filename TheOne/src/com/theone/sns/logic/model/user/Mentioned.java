package com.theone.sns.logic.model.user;

import java.util.List;

import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.user.base.Gallery;

@SuppressWarnings("serial")
public class Mentioned extends BaseModel {

	public String mentioned_at;

	public String mentioned_count;

	public List<Gallery> media;
}
