package com.theone.sns.logic.model.account.base;

import com.theone.sns.framework.model.BaseModel;

@SuppressWarnings("serial")
public class Token extends BaseModel {

	public String access_token;

	public String expires_in;

	public String token_type;

	public String scope;

	public String refresh_token;
}
