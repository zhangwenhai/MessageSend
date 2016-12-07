package com.theone.sns.logic.model.chat;

import java.util.List;

import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.chat.base.SearchMessageResult;

@SuppressWarnings("serial")
public class SearchResult extends BaseModel {

	public List<GroupInfo> groupList;

	public List<SearchMessageResult> messageList;
}
