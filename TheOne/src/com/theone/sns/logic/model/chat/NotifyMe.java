package com.theone.sns.logic.model.chat;

import java.util.List;

import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.model.user.base.Gallery;

@SuppressWarnings("serial")
public class NotifyMe extends BaseModel {

	public String _id;

	public User owner;

	public List<User> follow;

	public List<Gallery> like;

	public List<Gallery> comment;

	public List<Gallery> mention;

	public String created_at;

	public String updated_at;

	public int type = -1;
}
