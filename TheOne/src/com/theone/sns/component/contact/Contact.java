package com.theone.sns.component.contact;

import java.util.ArrayList;
import java.util.List;

import com.theone.sns.framework.model.BaseModel;

@SuppressWarnings("serial")
public class Contact extends BaseModel {

	public int personId;

	public String name;

	public List<String> emails = new ArrayList<String>();

	public List<String> phones = new ArrayList<String>();

	public void addPhone(String phone) {

		if (null == phones) {
			phones = new ArrayList<String>();
		}

		phones.add(phone);
	}

	public void addEmail(String email) {

		if (null == emails) {
			emails = new ArrayList<String>();
		}

		emails.add(email);
	}
}
