package com.theone.sns.logic.model.account.base;

import com.theone.sns.framework.model.BaseModel;

@SuppressWarnings("serial")
public class Privacy extends BaseModel {

	public boolean can_chat_if_followed;

	public boolean can_invited_if_followed;

	public boolean can_found_by_phone;
}
