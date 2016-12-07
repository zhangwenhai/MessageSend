package com.theone.sns.logic.model.chat;

import java.util.List;

import com.theone.sns.framework.model.BaseModel;

@SuppressWarnings("serial")
public class CreateGroup extends BaseModel {

	public String name;

	public List<String> members;
}
