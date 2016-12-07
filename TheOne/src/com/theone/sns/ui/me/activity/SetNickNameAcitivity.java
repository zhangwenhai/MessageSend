package com.theone.sns.ui.me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction.CommonField;
import com.theone.sns.ui.base.IphoneTitleActivity;

public class SetNickNameAcitivity extends IphoneTitleActivity {

	private EditText mNickNameEdit;

	private String userName;

	@Override
	protected void initLogics() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.set_nickname);

		initValue();

		getView();

		setView();
	}

	private void initValue() {

		userName = getIntent().getStringExtra(CommonField.USER_NAME_KEY);
	}

	private void getView() {

		mNickNameEdit = (EditText) findViewById(R.id.nickname);
	}

	private void setView() {

		mNickNameEdit.setText(userName);

		mNickNameEdit.requestFocus();

		setTitle(getString(R.string.set_nickname_title));

		setLeftButton(R.drawable.icon_back, false, false);
		setRightButton(R.string.confirm, true);

		getRightButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Intent mIntent = new Intent();
				mIntent.putExtra(CommonField.USER_NAME_KEY, mNickNameEdit
						.getText().toString());
				setResult(RESULT_OK, mIntent);
				finish();
			}
		});
	}
}
