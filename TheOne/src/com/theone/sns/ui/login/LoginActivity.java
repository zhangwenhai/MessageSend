package com.theone.sns.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionAction.RegisterAction;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType.AccountMessageType;
import com.theone.sns.logic.account.IAccountLogic;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.model.account.Account;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.util.StringUtil;

/**
 * Created by zhangwenhai on 2014/9/1.
 */
public class LoginActivity extends IphoneTitleActivity {

	private ImageView avatarImageView;
	private IAccountLogic mIAccountLogic;
	private IChatLogic mIChatLogic;
	private EditText phoneNumEdit;
	private EditText passwordEdit;

	private Account account = null;

	@Override
	protected void initLogics() {

		mIAccountLogic = (IAccountLogic) createLogicBuilder(this)
				.getLogicByInterfaceClass(IAccountLogic.class);

		mIChatLogic = (IChatLogic) createLogicBuilder(this)
				.getLogicByInterfaceClass(IChatLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.login_main);

		getView();

		initView();

		setListener();

		isLogin();
	}

	private void getView() {
		avatarImageView = (ImageView) findViewById(R.id.avatar);
		phoneNumEdit = (EditText) findViewById(R.id.iphone_num);
		passwordEdit = (EditText) findViewById(R.id.password);
	}

	private void initView() {
		setTitle(R.string.login);
		setLeftButton(R.drawable.icon_back, false, false);
		setRightButton(R.string.confirm, true);
	}

	private void setListener() {
		getRightButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				String phoneNum = phoneNumEdit.getText().toString().trim();

				String password = passwordEdit.getText().toString().trim();

				if (TextUtils.isEmpty(phoneNum)) {

					showToast(R.string.login_account_empty);

				} else if (!StringUtil.isMobile(phoneNum)
						&& !StringUtil.isEmail(phoneNum)) {

					showToast(R.string.login_account_error);

				} else if (TextUtils.isEmpty(password)) {

					showToast(R.string.password_empty);

				} else {

					hideInputWindow(view);

					showLoadingDialog(getString(R.string.dialog_login));

					mIAccountLogic.login(phoneNum, password);
				}
			}
		});

		findViewById(R.id.forget).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						startActivity(new Intent(RegisterAction.FORGET_ACTION)
								.putExtra(
										RegisterAction.EXTRA_LOGIN_FORGET_PASSWORD,
										true));
					}
				});

		phoneNumEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i2, int i3) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2,
					int i3) {
			}

			@Override
			public void afterTextChanged(Editable editable) {

				String editStr = editable.toString();

				String avatarUrl = "";

				if (null != account) {

					if (account.loginAccount.equals(editStr)) {

						avatarUrl = account.profile.avatar_url;
					}
				}

				ImageLoader.getInstance().displayImage(avatarUrl,
						avatarImageView, optionsForUserIcon);
			}
		});

	}

	private void isLogin() {

		if (FusionConfig.isLogin()) {

			startActivity(new Intent(RegisterAction.MAIN_ACTION));

			finish();

		} else {

			account = FusionConfig.getInstance().getAccount();

			if (null == account) {

				return;
			}

			String avatarUrl = account.profile.avatar_url;

			if (!TextUtils.isEmpty(avatarUrl)) {

				ImageLoader.getInstance().displayImage(avatarUrl,
						avatarImageView, optionsForUserIcon);
			}

			phoneNumEdit.setText(account.loginAccount);

			passwordEdit.requestFocus();
		}
	}

	@Override
	protected void handleStateMessage(Message msg) {

		switch (msg.what) {
		case AccountMessageType.LOGIN_SUCCESS_NEED_SET_CHARACTER: {

			hideLoadingDialog();

			startActivity(new Intent(RegisterAction.REGISTERMANIFESTO_ACTION));

			finish();

			break;
		}
		case AccountMessageType.LOGIN_SUCCESS_NEED_SET_USERINFO: {

			mIAccountLogic.getInterest();

			break;
		}
		case AccountMessageType.LOGIN_SUCCESS: {

			hideLoadingDialog();

			mIAccountLogic.getMyUserInfo();

			mIAccountLogic.getInterest();

			setResult(RESULT_OK);

			finish();

			break;
		}
		case AccountMessageType.GET_INTEREST_SUCCESS: {

			hideLoadingDialog();

			Intent intent = new Intent(
					FusionAction.RegisterAction.REGISTERUSERINFO_ACTION);

			intent.putExtra(RegisterAction.EXTRA_USER_CHARACTER, FusionConfig
					.getInstance().getAccount().profile.character);

			startActivity(intent);

			finish();

			break;
		}
		case AccountMessageType.GET_INTEREST_FAIL: {
			hideLoadingDialog();
			showToast(R.string.login_error);
			break;
		}
		case AccountMessageType.LOGIN_FAIL: {
			hideLoadingDialog();
			showToast(R.string.login_error);
			break;
		}
		default:
		}
	}
}
