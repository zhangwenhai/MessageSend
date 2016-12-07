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
public class VerificationAcitivity extends IphoneTitleActivity {

	private EditText checkVerifyCodeEdit;

	private IAccountLogic mIAccountLogic;

	private String phoneNumber;

	private boolean isLoginForgetPassword = false;

	@Override
	protected void initLogics() {
		mIAccountLogic = (IAccountLogic) getLogicByInterfaceClass(IAccountLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.verification_main);

		getView();

		setView();

		initValue();
	}

	private void initValue() {

		phoneNumber = getIntent().getStringExtra(
				RegisterAction.EXTRA_PHONE_NUMBER);

		isLoginForgetPassword = getIntent().getBooleanExtra(
				RegisterAction.EXTRA_LOGIN_FORGET_PASSWORD, false);
	}

	private void getView() {

		checkVerifyCodeEdit = (EditText) findViewById(R.id.verification_num);
	}

	private void setView() {
		setTitle(R.string.verification);
		setLeftButton(R.drawable.icon_back, false, false);
		setRightButton(R.string.next, true);

		getRightButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				String verifyCode = checkVerifyCodeEdit.getText().toString()
						.trim();

				if (TextUtils.isEmpty(verifyCode)) {

					showToast(R.string.verify_code_empty);

				} else {

					mIAccountLogic.checkVerifyCode(phoneNumber, verifyCode);
				}
			}
		});
	}

	@Override
	protected void handleStateMessage(Message msg) {

		switch (msg.what) {
		case AccountMessageType.CHECK_VERIFY_CODE_SUCCESS: {
			startActivity(new Intent(FusionAction.RegisterAction.SETPAW_ACTION)
					.putExtra(RegisterAction.EXTRA_LOGIN_FORGET_PASSWORD,
							isLoginForgetPassword));
			finish();
			break;
		}
		case AccountMessageType.CHECK_VERIFY_CODE_FAIL: {
			showToast(R.string.verify_code_error);
			break;
		}
		default:
		}
	}
}
