package com.theone.sns.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction.RegisterAction;
import com.theone.sns.common.FusionMessageType.AccountMessageType;
import com.theone.sns.logic.account.IRegisterLogic;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.util.StringUtil;

/**
 * Created by zhangwenhai on 2014/9/15.
 */
public class ForgetAcitivity extends IphoneTitleActivity {

	private EditText accountEdit;

	private IRegisterLogic mIRegisterLogic;

	private boolean isLoginForgetPassword = false;

	@Override
	protected void initLogics() {
		mIRegisterLogic = (IRegisterLogic) getLogicByInterfaceClass(IRegisterLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.forget_main);

		getView();

		setView();
	}

	private void getView() {

		accountEdit = (EditText) findViewById(R.id.iphone_num);

		isLoginForgetPassword = getIntent().getBooleanExtra(
				RegisterAction.EXTRA_LOGIN_FORGET_PASSWORD, false);
	}

	private void setView() {
		setTitle(R.string.find_forget);
		setLeftButton(R.drawable.icon_back, false, false);
		setRightButton(R.string.next, true);

		getRightButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				hideInputWindow(view);

				String account = accountEdit.getText().toString().trim();

				if (TextUtils.isEmpty(account)) {

					showToast(R.string.login_account_empty);

				} else if (StringUtil.isMobile(account)) {

					showLoadingDialog();

					mIRegisterLogic.getVerifyCode(account);

				} else if (StringUtil.isEmail(account)) {

					// showLoadingDialog();
					//
					// mIRegisterLogic.emailResetPassword(account);

					showToast(R.string.email_reset_password_success);

					finish();

				} else {

					showToast(R.string.login_account_error);
				}
			}
		});
	}

	public void leftButtonBack(View view) {
		hideInputWindow(view);
	}

	@Override
	protected void handleStateMessage(Message msg) {

		hideLoadingDialog();

		switch (msg.what) {

		case AccountMessageType.GET_VERIFY_CODE_SUCCESS: {

			String account = accountEdit.getText().toString().trim();

			if (StringUtil.isEmail(account)) {

				showToast(R.string.send_reset_password_for_email_success);

				finish();

				return;
			}

			showToast(R.string.send_verify_code_for_phone_success);

			Intent intent = new Intent(RegisterAction.VERIFICATION_ACTION);

			intent.putExtra(RegisterAction.EXTRA_PHONE_NUMBER, account);

			intent.putExtra(RegisterAction.EXTRA_LOGIN_FORGET_PASSWORD,
					isLoginForgetPassword);

			startActivity(intent);

			finish();

			break;
		}
		case AccountMessageType.EMAIL_RESET_PASSWORD_SUCCESS: {

			showToast(R.string.email_reset_password_success);

			finish();

			break;
		}
		case AccountMessageType.GET_VERIFY_CODE_FAIL: {

			showToast(R.string.get_verify_code_error);

			break;
		}
		case AccountMessageType.EMAIL_RESET_PASSWORD_FAIL: {

			showToast(R.string.email_reset_password_error);

			break;
		}
		default:
		}
	}
}
