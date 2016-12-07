package com.theone.sns.logic.model.chat.base;

import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.chat.MessageInfo;

@SuppressWarnings("serial")
public class SearchMessageResult extends BaseModel {

	public GroupInfo groupInfo;

	public MessageInfo messageInfo;
}
