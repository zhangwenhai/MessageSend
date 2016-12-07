package com.theone.sns.logic.model.chat.base;

import java.util.List;

import com.theone.sns.framework.model.BaseModel;

@SuppressWarnings("serial")
public class Position extends BaseModel {

	public String name;

	public String address;

	public List<String> location;
}
