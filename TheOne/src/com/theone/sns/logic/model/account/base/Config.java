package com.theone.sns.logic.model.account.base;

import java.util.List;

import com.theone.sns.framework.model.BaseModel;

@SuppressWarnings("serial")
public class Config extends BaseModel {

	public String term_url;

	public String qiniu_upload_token;

	public String pubnub_sub_key;

	public List<String> purposes;

	public List<String> interests;
}
