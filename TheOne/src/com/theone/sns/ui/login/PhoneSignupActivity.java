package com.theone.sns.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction.RegisterAction;
import com.theone.sns.common.FusionMessageType.AccountMessageType;
import com.theone.sns.component.http.FailResult;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.account.IRegisterLogic;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.util.StringUtil;

public class PhoneSignupActivity extends IphoneTitleActivity {

	private Button validatePhoneButton;

	private Button registerPhoneButton;

	private IRegisterLogic mIRegisterLogic;

	private EditText phoneNumEdit;

	private EditText verifyCodeEdit;

	private TextView readPrivacyTextView;

	private String nickName;

	private String password;

	private String inviteCode;

	private TimeCount time;

	@Override
	protected void initLogics() {

		mIRegisterLogic = (IRegisterLogic) getLogicByInterfaceClass(IRegisterLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.phone_signup_main);

		getView();

		initView();

		setListener();

		setValue();
	}

	private void getView() {

		validatePhoneButton = (Button) findViewById(R.id.validate_phone);

		phoneNumEdit = (EditText) findViewById(R.id.phone_num);

		verifyCodeEdit = (EditText) findViewById(R.id.validate_num);

		readPrivacyTextView = (TextView) findViewById(R.id.read_privacy);

		registerPhoneButton = (Button) findViewById(R.id.register_phone);

		time = new TimeCount(60000, 1000);
	}

	private void setValue() {

		nickName = getIntent().getStringExtra(
				RegisterAction.EXTRA_REGISTER_NICKNAME);

		password = getIntent().getStringExtra(
				RegisterAction.EXTRA_REGISTER_PASSWORD);

		inviteCode = getIntent().getStringExtra(
				RegisterAction.EXTRA_REGISTER_REGISTER_CODE);

		if (TextUtils.isEmpty(nickName) || TextUtils.isEmpty(password)) {

			finish();
		}
	}

	private void initView() {
		setTitle(R.string.phone_signup);
		setLeftButton(R.drawable.icon_back, false, false);
	}

	private void setListener() {
		validatePhoneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				String phone = phoneNumEdit.getText().toString().trim();

				if (TextUtils.isEmpty(phone)) {

					showToast(R.string.phone_number_empty);

				} else if (!StringUtil.isMobile(phone)) {

					showToast(R.string.phone_error);

				} else {

					showLoadingDialog();

					mIRegisterLogic.getVerifyCode(phone);
				}
			}
		});

		registerPhoneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				String phoneNum = phoneNumEdit.getText().toString().trim();

				String verifyCode = verifyCodeEdit.getText().toString().trim();

				if (TextUtils.isEmpty(phoneNum)) {

					showToast(R.string.phone_number_empty);

				} else if (!StringUtil.isMobile(phoneNum)) {

					showToast(R.string.phone_error);

				} else if (TextUtils.isEmpty(verifyCode)) {

					showToast(R.string.verify_code_empty);

				} else {

					showLoadingDialog();

					mIRegisterLogic.checkVerifyCodeAndCreateAccount(nickName,
							password, phoneNum.trim(), verifyCode.trim(),
							inviteCode);
				}
			}
		});

		readPrivacyTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(RegisterAction.READ_PRIVACY_ACTION));
			}
		});
	}

	@Override
	protected void handleStateMessage(Message msg) {

		hideLoadingDialog();

		switch (msg.what) {
		case AccountMessageType.GET_VERIFY_CODE_SUCCESS: {

			time.start();

			break;
		}
		case AccountMessageType.GET_VERIFY_CODE_FAIL: {

			showToast(R.string.get_verify_code_error);

			break;
		}
		case AccountMessageType.CHECK_VERIFY_CODE_AND_CREATE_ACCOUNT_SUCCESS: {

			setResult(RESULT_OK);

			finish();

			break;
		}
		case AccountMessageType.CHECK_VERIFY_CODE_AND_CREATE_ACCOUNT_FAIL: {

			UIObject object = (UIObject) msg.obj;

			if (null != object && null != object.mObject) {

				FailResult failResult = (FailResult) object.mObject;

				showToast(failResult.message);

			} else {

				showToast(R.string.verify_code_error);
			}

			break;
		}
		default:
		}
	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			validatePhoneButton.setText(getResources().getString(
					R.string.phone_signup4));
			validatePhoneButton.setEnabled(true);
			validatePhoneButton.setTextColor(getResources().getColor(
					R.color.white));
		}

		@Override
		public void onTick(long millisUntilFinished) {
			validatePhoneButton.setEnabled(false);
			validatePhoneButton.setTextColor(getResources().getColor(
					R.color.gray));
			validatePhoneButton.setText(getResources().getString(
					R.string.resend)
					+ "(" + millisUntilFinished / 1000 + ")");
		}
	}
}
