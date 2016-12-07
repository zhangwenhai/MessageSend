package com.theone.sns.logic.model.chat.base;

import java.util.List;

import com.theone.sns.framework.model.BaseModel;

@SuppressWarnings("serial")
public class NameCard extends BaseModel {

	public String _id;

	public String name;

	public String avatar_url;

	public List<String> location;

	public String role;

	public boolean marriage;

	public String region;
}
