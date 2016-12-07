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
import com.theone.sns.component.http.FailResult;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.account.IRegisterLogic;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.util.StringUtil;

public class EmailSignupActivity extends IphoneTitleActivity {

	private EditText nickNameEdit;

	private EditText passwordEdit;

	private EditText emailEdit;

	private EditText inviteCodeEdit;

	private TextView readPrivacyTextView;
	
	private TextView inviteCodeDescTextView;

	String nickNames;

	String password;

	String email;

	String inviteCode;

	private IRegisterLogic mIRegisterLogic;

	@Override
	protected void initLogics() {

		mIRegisterLogic = (IRegisterLogic) getLogicByInterfaceClass(IRegisterLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setSubContent(R.layout.email_signup_main);

		getView();

		initView();

		setListener();
	}

	private void getView() {

		nickNameEdit = (EditText) findViewById(R.id.nick_name);

		passwordEdit = (EditText) findViewById(R.id.password);

		emailEdit = (EditText) findViewById(R.id.email);

		inviteCodeEdit = (EditText) findViewById(R.id.register_invite_code);

		readPrivacyTextView = (TextView) findViewById(R.id.read_privacy);
		
		inviteCodeDescTextView = (TextView) findViewById(R.id.invite_code_desc);
	}

	private void initView() {
		setTitle(R.string.email_signup1);
		setLeftButton(R.string.Back, true, true);
		setRightButton(R.string.Done, true);
	}

	private void setListener() {

		getRightButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				nickNames = nickNameEdit.getText().toString().trim();

				password = passwordEdit.getText().toString().trim();

				email = emailEdit.getText().toString().trim();

				inviteCode = inviteCodeEdit.getText().toString().trim();

				if (TextUtils.isEmpty(nickNames)) {

					showToast(R.string.nick_name_empty);

				} else if (TextUtils.isEmpty(password)) {

					showToast(R.string.password_empty);

				} else if (password.length() < 6) {

					showToast(R.string.password_set_error);

				} else if (TextUtils.isEmpty(email)) {

					showToast(R.string.email_empty);

				} else if (!StringUtil.isEmail(email)) {

					showToast(R.string.email_error);

				} else {

					showLoadingDialog();

					if (TextUtils.isEmpty(inviteCode)) {

						mIRegisterLogic.createEmailAccount(nickNames, password,
								email, inviteCode);

					} else {

						mIRegisterLogic.verifyRegisterInviteCode(inviteCode);
					}
				}
			}
		});

		readPrivacyTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(RegisterAction.READ_PRIVACY_ACTION));
			}
		});
		
		inviteCodeDescTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				startActivity(new Intent(
						FusionAction.RegisterAction.INVITATIONCODEDETAILS_ACTION));
			};
		});
	}

	@Override
	protected void handleStateMessage(Message msg) {

		switch (msg.what) {

		case AccountMessageType.VERIFY_REGISTER_INVITE_CODE_SUCCESS: {

			mIRegisterLogic.createEmailAccount(nickNames, password, email,
					inviteCode);

			break;
		}
		case AccountMessageType.VERIFY_REGISTER_INVITE_CODE_FAIL: {

			hideLoadingDialog();

			showToast(R.string.invite_code_error);

			break;
		}
		case AccountMessageType.CREATE_EMAIL_ACCOUNT_SUCCESS: {

			hideLoadingDialog();

			setResult(RESULT_OK);

			finish();

			break;
		}
		case AccountMessageType.CREATE_EMAIL_ACCOUNT_FAIL: {

			hideLoadingDialog();

			UIObject object = (UIObject) msg.obj;

			if (null != object && null != object.mObject) {

				FailResult failResult = (FailResult) object.mObject;

				showToast(failResult.message);

			} else {

				showToast(R.string.create_email_account_error);
			}

			break;
		}
		default:
		}
	}
}
