package com.theone.sns.ui.me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction.CommonField;
import com.theone.sns.common.FusionMessageType.UserMessageType;
import com.theone.sns.logic.user.IUserLogic;
import com.theone.sns.ui.base.IphoneTitleActivity;

public class SetAliasAcitivity extends IphoneTitleActivity {

	private EditText mAliasCodeEdit;

	private IUserLogic mIUserLogic;

	private String userId;

	private String userName;

	private String alias;

	@Override
	protected void initLogics() {
		mIUserLogic = (IUserLogic) getLogicByInterfaceClass(IUserLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.set_alias);

		initValue();

		getView();

		setView();
	}

	private void initValue() {

		userId = getIntent().getStringExtra(CommonField.USER_ID_KEY);

		userName = getIntent().getStringExtra(CommonField.USER_NAME_KEY);
	}

	private void getView() {

		mAliasCodeEdit = (EditText) findViewById(R.id.alias);
	}

	private void setView() {

		setTitle(String.format(getString(R.string.set_alias_title), userName));

		setLeftButton(R.drawable.icon_back, false, false);

		setRightButton(R.string.confirm, true);

		getRightButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				alias = mAliasCodeEdit.getText().toString();

				mIUserLogic.updateAliasByUserId(userId, alias);
			}
		});
	}

	@Override
	protected void handleStateMessage(Message msg) {
		switch (msg.what) {
		case UserMessageType.UPDATE_ALIAS_BY_ID_SUCCESS: {
			Intent mIntent = new Intent();
			mIntent.putExtra(CommonField.ALIAS_KEY, alias);
			setResult(RESULT_OK, mIntent);
			finish();
			break;
		}
		case UserMessageType.UPDATE_ALIAS_BY_ID_FAIL: {
			showToast(R.string.set_alias_fail);
			break;
		}
		default:
		}
	}
}
