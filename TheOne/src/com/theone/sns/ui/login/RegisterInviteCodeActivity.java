package com.theone.sns.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionAction.RegisterAction;
import com.theone.sns.common.FusionMessageType.AccountMessageType;
import com.theone.sns.logic.account.IRegisterLogic;
import com.theone.sns.ui.base.IphoneTitleActivity;

public class RegisterInviteCodeActivity extends IphoneTitleActivity {

	private static final int PHONESIGNUPSUCCESSREQUESTCODE = 1;

	private static final int EMAILSIGNUPSUCCESSREQUESTCODE = 2;

	private IRegisterLogic mIRegisterLogic;

	private EditText nickNameEdit;

	private EditText passwordEdit;

	private EditText inviteCodeEdit;

	private TextView emailRegisterTextView;

	private TextView inviteCodeDescTextView;

	private String nickName;

	private String password;

	private String inviteCode;

	@Override
	protected void initLogics() {

		mIRegisterLogic = (IRegisterLogic) getLogicByInterfaceClass(IRegisterLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.register_invite_code_main);

		getView();

		initView();

		setListener();
	}

	private void getView() {

		nickNameEdit = (EditText) findViewById(R.id.nick_name);

		passwordEdit = (EditText) findViewById(R.id.password);

		inviteCodeEdit = (EditText) findViewById(R.id.register_invite_code);

		emailRegisterTextView = (TextView) findViewById(R.id.email_signup);

		inviteCodeDescTextView = (TextView) findViewById(R.id.invite_code_desc);
	}

	private void initView() {

		setTitle(R.string.phone_signup);

		setLeftButton(R.drawable.icon_back, false, false);

		setRightButton(R.string.next, true);
	}

	private void setListener() {

		getRightButton().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				nickName = nickNameEdit.getText().toString().trim();

				password = passwordEdit.getText().toString().trim();

				inviteCode = inviteCodeEdit.getText().toString().trim();

				if (TextUtils.isEmpty(nickName)) {

					showToast(R.string.nick_name_empty);

				} else if (TextUtils.isEmpty(password)) {

					showToast(R.string.password_empty);

				} else if (password.length() < 6) {

					showToast(R.string.password_set_error);

				} else {

					hideIME(inviteCodeEdit, true);

					if (TextUtils.isEmpty(inviteCode)) {

						startToPhoneSignUp();

					} else {

						showLoadingDialog();

						mIRegisterLogic.verifyRegisterInviteCode(inviteCode);
					}
				}
			};
		});

		emailRegisterTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				startActivityForResult(new Intent(
						FusionAction.RegisterAction.EMAILSIGNUP_ACTION),
						EMAILSIGNUPSUCCESSREQUESTCODE);
			};
		});

		inviteCodeDescTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				startActivity(new Intent(
						FusionAction.RegisterAction.INVITATIONCODEDETAILS_ACTION));
			};
		});
	}

	private void startToPhoneSignUp() {

		Intent intent = new Intent(RegisterAction.PHONESIGNUP_ACTION);

		intent.putExtra(RegisterAction.EXTRA_REGISTER_NICKNAME, nickName);

		intent.putExtra(RegisterAction.EXTRA_REGISTER_PASSWORD, password);

		intent.putExtra(RegisterAction.EXTRA_REGISTER_REGISTER_CODE, inviteCode);

		startActivityForResult(intent, PHONESIGNUPSUCCESSREQUESTCODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (RESULT_OK == resultCode) {

			if (requestCode == PHONESIGNUPSUCCESSREQUESTCODE
					|| requestCode == EMAILSIGNUPSUCCESSREQUESTCODE) {

				setResult(RESULT_OK);

				finish();
			}
		}
	}

	@Override
	protected void handleStateMessage(Message msg) {

		hideLoadingDialog();

		switch (msg.what) {

		case AccountMessageType.VERIFY_REGISTER_INVITE_CODE_SUCCESS: {

			startToPhoneSignUp();

			break;
		}
		case AccountMessageType.VERIFY_REGISTER_INVITE_CODE_FAIL: {

			showToast(R.string.invite_code_error);

			break;
		}
		default:
		}
	}
}
