package com.theone.sns.logic.model.account.base;

import com.theone.sns.framework.model.BaseModel;

@SuppressWarnings("serial")
public class Profile extends BaseModel {

	public String _id;

	public String name;

	public String phone_no;

	public String email;

	public String avatar_url;

	public String character;

	public boolean phone_verified;

	public boolean email_verified;
}
