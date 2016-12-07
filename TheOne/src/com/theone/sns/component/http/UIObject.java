package com.theone.sns.component.http;

import java.io.Serializable;

public class UIObject implements Serializable {

	private static final long serialVersionUID = 1L;

	public String mLocalRequestId;

	public Object mObject;

	public UIObject(String localRequestId, Object object) {

		mLocalRequestId = localRequestId;

		mObject = object;
	}
}
