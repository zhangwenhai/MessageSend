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

/**
 * Created by zhangwenhai on 2014/9/15.
 */
public class SetPawAcitivity extends IphoneTitleActivity {

	private EditText passwordEdit;

	private EditText replyPasswordEdit;

	private IAccountLogic mIAccountLogic;

	private boolean isLoginForgetPassword = false;

	@Override
	protected void initLogics() {
		mIAccountLogic = (IAccountLogic) createLogicBuilder(this)
				.getLogicByInterfaceClass(IAccountLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.set_paw_main);

		getView();

		setView();
	}

	private void getView() {

		passwordEdit = (EditText) findViewById(R.id.password);
		replyPasswordEdit = (EditText) findViewById(R.id.password_verification);

		isLoginForgetPassword = getIntent().getBooleanExtra(
				RegisterAction.EXTRA_LOGIN_FORGET_PASSWORD, false);
	}

	private void setView() {
		setTitle(R.string.reset_password);
		setLeftButton(R.drawable.icon_back, false, false);
		setRightButton(R.string.confirm, true);

		getRightButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				String password = passwordEdit.getText().toString().trim();

				String replyPassword = replyPasswordEdit.getText().toString()
						.trim();

				if (TextUtils.isEmpty(password)
						|| TextUtils.isEmpty(replyPassword)) {

					showToast(R.string.password_empty);

				} else if (!password.equals(replyPassword)) {

					showToast(R.string.password_twice_diff);

				} else if (password.length() < 6) {

					showToast(R.string.password_set_error);

				} else {

					showLoadingDialog();

					mIAccountLogic.updatePassword(password);
				}
			}
		});
	}

	@Override
	protected void handleStateMessage(Message msg) {

		hideLoadingDialog();

		switch (msg.what) {
		case AccountMessageType.UPDATE_PASSWORD_SUCCESS: {

			showToast(R.string.password_update_success);

			if (!isLoginForgetPassword) {

				sendBroadcast(new Intent(
						FusionAction.TheOneApp.ACTION_CLOSE_APPLICATION));
			}

			finish();

			break;
		}
		case AccountMessageType.UPDATE_PASSWORD_FAIL: {

			showToast(R.string.password_update_error);
			break;
		}
		default:
		}
	}
}
