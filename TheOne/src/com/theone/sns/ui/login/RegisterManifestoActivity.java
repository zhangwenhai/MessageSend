package com.theone.sns.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionAction.RegisterAction;
import com.theone.sns.common.FusionCode.UserCharacter;
import com.theone.sns.common.FusionMessageType.AccountMessageType;
import com.theone.sns.logic.account.IAccountLogic;
import com.theone.sns.ui.base.BasicActivity;
import com.theone.sns.util.uiwidget.IphoneStyleAlertDialogBuilder;

/**
 * Created by zhangwenhai on 2014/8/31.
 */
public class RegisterManifestoActivity extends BasicActivity {

	private IphoneStyleAlertDialogBuilder m_cantAccessDialog;

	private Button gayButton;

	private Button lesButton;

	public String character;

	private IAccountLogic mIAccountLogic;

	@Override
	protected void initLogics() {

		mIAccountLogic = (IAccountLogic) createLogicBuilder(this)
				.getLogicByInterfaceClass(IAccountLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_manifesto_main);

		getView();

		initView();

		setListener();
	}

	private void getView() {
		gayButton = (Button) findViewById(R.id.gay);
		lesButton = (Button) findViewById(R.id.les);
	}

	private void initView() {

	}

	private void setListener() {
		gayButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				character = UserCharacter.GAY;

				gayButton
						.setBackgroundResource(R.drawable.attribute_selection_gay_icon_highlight);

				lesButton
						.setBackgroundResource(R.drawable.attribute_selection_les_icon);

				showCantAccessDialog();
			}
		});
		lesButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				character = UserCharacter.LES;

				gayButton
						.setBackgroundResource(R.drawable.attribute_selection_gay_icon);

				lesButton
						.setBackgroundResource(R.drawable.attribute_selection_les_icon_highlight);

				showCantAccessDialog();
			}
		});
	}

	private void showCantAccessDialog() {

		if (m_cantAccessDialog != null) {

			m_cantAccessDialog.setTitle(getShowTitle());

			m_cantAccessDialog.show();

			return;
		}

		m_cantAccessDialog = new IphoneStyleAlertDialogBuilder(this);

		m_cantAccessDialog.setTitle(getShowTitle());

		m_cantAccessDialog.addItem(0, getString(R.string.confirm),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						m_cantAccessDialog.dismiss();

						showLoadingDialog();

						mIAccountLogic.updateMyCharacter(character);
					}
				});

		m_cantAccessDialog.addItem(1, getString(R.string.Cancel),
				IphoneStyleAlertDialogBuilder.COLOR_BLUE,
				IphoneStyleAlertDialogBuilder.TEXT_TYPE_BOLD,
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						m_cantAccessDialog.dismiss();
					}
				});

		m_cantAccessDialog.show();
	}

	private String getShowTitle() {

		String choose = "";

		if (UserCharacter.GAY.equals(character)) {

			choose = getString(R.string.gay);

		} else if (UserCharacter.LES.equals(character)) {

			choose = getString(R.string.les);
		}

		return String.format(getString(R.string.manifesto3), choose);
	}

	@Override
	protected void handleStateMessage(Message msg) {
		switch (msg.what) {

		case AccountMessageType.UPDATE_MY_CHARACTER_SUCCESS: {

			mIAccountLogic.getInterest();

			break;
		}
		case AccountMessageType.UPDATE_MY_CHARACTER_FAIL: {
			hideLoadingDialog();
			showToast(R.string.manifesto_error);
			break;
		}
		case AccountMessageType.GET_INTEREST_SUCCESS: {

			hideLoadingDialog();

			Intent intent = new Intent(
					FusionAction.RegisterAction.REGISTERUSERINFO_ACTION);

			intent.putExtra(RegisterAction.EXTRA_USER_CHARACTER, character);

			startActivity(intent);

			finish();

			break;
		}
		case AccountMessageType.GET_INTEREST_FAIL: {
			hideLoadingDialog();
			showToast(R.string.manifesto_error);
			break;
		}
		default:
		}
	}
}
