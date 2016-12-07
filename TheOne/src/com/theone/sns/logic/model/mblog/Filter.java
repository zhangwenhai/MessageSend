package com.theone.sns.logic.model.mblog;

import java.util.List;

import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.mblog.base.Age;
import com.theone.sns.logic.model.mblog.base.Height;

@SuppressWarnings("serial")
public class Filter extends BaseModel {

	public boolean enabled;

	public boolean avatar_mode_enabled;

	public List<String> role;

	public List<String> purposes;

	public boolean online;

	public String region;

	public Age age;

	public Height height;

	public List<String> interests;
}
