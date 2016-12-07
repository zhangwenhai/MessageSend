package com.theone.sns.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionAction.RegisterAction;
import com.theone.sns.common.FusionMessageType.AccountMessageType;
import com.theone.sns.logic.account.IAccountLogic;
import com.theone.sns.ui.base.IphoneTitleActivity;

public class VerificationPasswordAcitivity extends IphoneTitleActivity {

	private EditText verifyPasswordEdit;

	private IAccountLogic mIAccountLogic;

	@Override
	protected void initLogics() {
		mIAccountLogic = (IAccountLogic) getLogicByInterfaceClass(IAccountLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.verification_password);

		getView();

		setView();

		setListener();
	}

	private void getView() {

		verifyPasswordEdit = (EditText) findViewById(R.id.verification_num);
	}

	private void setView() {
		setTitle(R.string.reset_password);
		setLeftButton(R.drawable.icon_back, false, false);
	}

	private void setListener() {

		findViewById(R.id.validate_password).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

						String password = verifyPasswordEdit.getText()
								.toString();

						if (TextUtils.isEmpty(password)) {

							showToast(R.string.password_empty);

							return;
						}

						showLoadingDialog();

						mIAccountLogic.checkPassword(password);
					}
				});

		findViewById(R.id.forget).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						startActivity(new Intent(RegisterAction.FORGET_ACTION));
					}
				});
	}

	@Override
	protected void handleStateMessage(Message msg) {

		hideLoadingDialog();

		switch (msg.what) {

		case AccountMessageType.CHECK_PASSWORD_SUCCESS: {

			startActivity(new Intent(FusionAction.RegisterAction.SETPAW_ACTION));

			finish();

			break;
		}
		case AccountMessageType.CHECK_PASSWORD_FAIL: {

			showToast(R.string.verification_password_error);

			break;
		}
		default:
		}
	}
}
