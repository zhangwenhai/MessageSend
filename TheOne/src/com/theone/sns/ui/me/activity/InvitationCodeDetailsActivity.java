package com.theone.sns.ui.me.activity;

import android.os.Bundle;

import com.theone.sns.R;
import com.theone.sns.ui.base.IphoneTitleActivity;

/**
 * Created by zhangwenhai on 2014/11/17.
 */
public class InvitationCodeDetailsActivity extends IphoneTitleActivity {
	@Override
	protected void initLogics() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.invitation_code_details_main);

		getView();

		setView();

		setListener();

		sendRequest();
	}

	private void getView() {

	}

	private void setView() {
		setTitle(R.string.invite_code);
		setLeftButton(R.drawable.icon_back, false, false);
	}

	private void setListener() {

	}

	private void sendRequest() {

	}
}
