package com.theone.sns.logic.model.account;

import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.account.base.FollowCount;
import com.theone.sns.logic.model.account.base.InvitedBy;

@SuppressWarnings("serial")
public class RegisterInviteCode extends BaseModel {

	public String avatar_url;

	public String code;

	public FollowCount count;

	public InvitedBy invited_by;
}
