package com.theone.sns.logic.model.account;

import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.account.base.Config;
import com.theone.sns.logic.model.account.base.Privacy;
import com.theone.sns.logic.model.account.base.Profile;
import com.theone.sns.logic.model.account.base.Token;
import com.theone.sns.logic.model.mblog.Filter;

@SuppressWarnings("serial")
public class Account extends BaseModel {

	public Profile profile;

	public Filter filter;

	public Config config;

	public Token token;

	public Privacy privacy;

	public String loginAccount;

	public boolean isLogin = false;
}
