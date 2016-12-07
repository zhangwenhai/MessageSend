package com.theone.sns.logic.model.user;

import com.theone.sns.component.contact.Contact;
import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.user.base.RegisteredUser;

@SuppressWarnings("serial")
public class MatchContactsResult extends BaseModel {

	public int type;

	public RegisteredUser registeredUser;

	public Contact contact;

	public String Separators;
}
