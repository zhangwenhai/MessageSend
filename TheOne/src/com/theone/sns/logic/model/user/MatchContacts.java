package com.theone.sns.logic.model.user;

import java.util.List;

import com.theone.sns.component.contact.Contact;
import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.user.base.RegisteredUser;

@SuppressWarnings("serial")
public class MatchContacts extends BaseModel {

	public List<RegisteredUser> registered;

	public List<Contact> unregistered;
}
