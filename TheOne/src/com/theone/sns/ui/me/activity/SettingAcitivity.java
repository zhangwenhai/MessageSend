package com.theone.sns.ui.me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionAction.RegisterAction;
import com.theone.sns.common.FusionCode.SettingKey;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.logic.account.IAccountLogic;
import com.theone.sns.logic.model.account.Account;
import com.theone.sns.logic.model.account.base.Privacy;
import com.theone.sns.logic.user.ISettingLogic;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.util.StringUtil;
import com.theone.sns.util.uiwidget.IphoneStyleAlertDialogBuilder;
import com.theone.sns.util.uiwidget.swithbutton.SwitchButton;

public class SettingAcitivity extends IphoneTitleActivity {

	private IphoneStyleAlertDialogBuilder m_cantAccessDialog;

	private ISettingLogic mISettingLogic;

	private IAccountLogic mIAccountLogic;

	private SwitchButton setting_only_following_chat;

	private SwitchButton setting_only_following_group_chat;

	private SwitchButton setting_find_me_from_phone_number;

	private SwitchButton setting_new_message_notify;

	private SwitchButton setting_sound_notify;

	private SwitchButton setting_vibrate_notify;

	private SwitchButton setting_notify_detail;

	private SwitchButton setting_new_mblog_notify;

	private SwitchButton setting_new_apk_check;

	private boolean is_only_following_chat;

	private boolean is_only_following_group_chat;

	private boolean is_find_me_from_phone_number;

	private boolean is_new_message_notify;

	private boolean is_sound_notify;

	private boolean is_vibrate_notify;

	private boolean is_notify_detail;

	private boolean is_new_mblog_notify;

	private boolean is_new_apk_check;

	@Override
	protected void initLogics() {

		mISettingLogic = (ISettingLogic) getLogicByInterfaceClass(ISettingLogic.class);

		mIAccountLogic = (IAccountLogic) getLogicByInterfaceClass(IAccountLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.setting);

		initValue();

		getView();

		setView();

		setViewValue();

		ImageLoader.getInstance().clearMemoryCache();
	}

	private void initValue() {

		is_only_following_chat = mISettingLogic.getBoolean(
				SettingKey.ONLY_FOLLOWING_CHAT_KEY, true);

		is_only_following_group_chat = mISettingLogic.getBoolean(
				SettingKey.ONLY_FOLLOWING_GROUP_CHAT_KEY, true);

		is_find_me_from_phone_number = mISettingLogic.getBoolean(
				SettingKey.FIND_ME_FROM_PHONE_NUMBER_KEY, true);

		is_new_message_notify = mISettingLogic.getBoolean(
				SettingKey.NEW_MESSAGE_NOTIFY_KEY, true);

		is_sound_notify = mISettingLogic.getBoolean(
				SettingKey.SOUND_NOTIFY_KEY, true);

		is_vibrate_notify = mISettingLogic.getBoolean(
				SettingKey.VIBRATE_NOTIFY_KEY, true);

		is_notify_detail = mISettingLogic.getBoolean(
				SettingKey.NOTIFY_DETAIL_KEY, false);

		is_new_mblog_notify = mISettingLogic.getBoolean(
				SettingKey.NEW_MBLOG_NOTIFY_KEY, true);

		is_new_apk_check = mISettingLogic.getBoolean(
				SettingKey.NEW_APK_CHECK_KEY, false);
	}

	private void getView() {

		Account account = FusionConfig.getInstance().getAccount();

		if (null != account && !TextUtils.isEmpty(account.loginAccount)) {

			if (StringUtil.isMobile(account.loginAccount)) {

				findViewById(R.id.setting_find_me_from_phone_number_view)
						.setVisibility(View.VISIBLE);

			} else if (StringUtil.isEmail(account.loginAccount)) {

				findViewById(R.id.setting_find_me_from_phone_number_view)
						.setVisibility(View.GONE);
			}
		}

		findViewById(R.id.update_password_view).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {

						startActivity(new Intent(
								RegisterAction.VERIFICATIONPASSWORDACITIVITY));
					}
				});

		findViewById(R.id.sign_out_btn).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						showCantAccessDialog();
					}
				});

		findViewById(R.id.get_register_invite_code_view).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {

						startActivity(new Intent(
								RegisterAction.MYINVITATIONCODE_ACTION));
					}
				});

		findViewById(R.id.setting_about_view).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						startActivity(new Intent(
								FusionAction.MeAction.ABOUT_ACTION));
					}
				});

		setting_only_following_chat = (SwitchButton) findViewById(R.id.setting_only_following_chat);

		setting_only_following_group_chat = (SwitchButton) findViewById(R.id.setting_only_following_group_chat);

		setting_find_me_from_phone_number = (SwitchButton) findViewById(R.id.setting_find_me_from_phone_number);

		setting_new_message_notify = (SwitchButton) findViewById(R.id.setting_new_message_notify);

		setting_sound_notify = (SwitchButton) findViewById(R.id.setting_sound_notify);

		setting_vibrate_notify = (SwitchButton) findViewById(R.id.setting_vibrate_notify);

		setting_notify_detail = (SwitchButton) findViewById(R.id.setting_notify_detail);

		setting_new_mblog_notify = (SwitchButton) findViewById(R.id.setting_new_mblog_notify);

		setting_new_apk_check = (SwitchButton) findViewById(R.id.setting_new_apk_check);
	}

	private void setView() {

		setTitle(getString(R.string.setting_title));

		setLeftButton(R.drawable.icon_back, false, false);
	}

	public void sendCloseApplicationBroadcast() {
		sendBroadcast(new Intent(
				FusionAction.TheOneApp.ACTION_CLOSE_APPLICATION));
	}

	private void showCantAccessDialog() {

		if (m_cantAccessDialog != null) {
			m_cantAccessDialog.show();
			return;
		}

		m_cantAccessDialog = new IphoneStyleAlertDialogBuilder(this);

		m_cantAccessDialog.addItem(0, getString(R.string.setting_logout), 1,
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						m_cantAccessDialog.dismiss();

						sendCloseApplicationBroadcast();

						finish();
					}
				});

		m_cantAccessDialog.addItem(1, getString(R.string.Cancel),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						m_cantAccessDialog.dismiss();
					}
				});

		m_cantAccessDialog.show();
	}

	private void setViewValue() {

		if (is_only_following_chat) {
			setting_only_following_chat.setChecked(true);
		} else {
			setting_only_following_chat.setChecked(false);
		}

		if (is_only_following_group_chat) {
			setting_only_following_group_chat.setChecked(true);
		} else {
			setting_only_following_group_chat.setChecked(false);
		}

		if (is_find_me_from_phone_number) {
			setting_find_me_from_phone_number.setChecked(true);
		} else {
			setting_find_me_from_phone_number.setChecked(false);
		}

		if (is_new_message_notify) {
			setting_new_message_notify.setChecked(true);
		} else {
			setting_new_message_notify.setChecked(false);
		}

		if (is_sound_notify) {
			setting_sound_notify.setChecked(true);
		} else {
			setting_sound_notify.setChecked(false);
		}

		if (is_vibrate_notify) {
			setting_vibrate_notify.setChecked(true);
		} else {
			setting_vibrate_notify.setChecked(false);
		}

		if (is_notify_detail) {
			setting_notify_detail.setChecked(true);
		} else {
			setting_notify_detail.setChecked(false);
		}

		if (is_new_mblog_notify) {
			setting_new_mblog_notify.setChecked(true);
		} else {
			setting_new_mblog_notify.setChecked(false);
		}

		if (is_new_apk_check) {
			setting_new_apk_check.setChecked(true);
		} else {
			setting_new_apk_check.setChecked(false);
		}
	}

	private void save() {

		sendPrivacy();

		if (setting_new_message_notify.isChecked() != is_new_message_notify) {

			if (setting_new_message_notify.isChecked()) {
				mISettingLogic.set(SettingKey.NEW_MESSAGE_NOTIFY_KEY, true);
			} else {
				mISettingLogic.set(SettingKey.NEW_MESSAGE_NOTIFY_KEY, false);
			}
		}

		if (setting_sound_notify.isChecked() != is_sound_notify) {

			if (setting_sound_notify.isChecked()) {
				mISettingLogic.set(SettingKey.SOUND_NOTIFY_KEY, true);
			} else {
				mISettingLogic.set(SettingKey.SOUND_NOTIFY_KEY, false);
			}
		}

		if (setting_vibrate_notify.isChecked() != is_vibrate_notify) {

			if (setting_vibrate_notify.isChecked()) {
				mISettingLogic.set(SettingKey.VIBRATE_NOTIFY_KEY, true);
			} else {
				mISettingLogic.set(SettingKey.VIBRATE_NOTIFY_KEY, false);
			}
		}

		if (setting_notify_detail.isChecked() != is_notify_detail) {

			if (setting_notify_detail.isChecked()) {
				mISettingLogic.set(SettingKey.NOTIFY_DETAIL_KEY, true);
			} else {
				mISettingLogic.set(SettingKey.NOTIFY_DETAIL_KEY, false);
			}
		}

		if (setting_new_mblog_notify.isChecked() != is_new_mblog_notify) {

			if (setting_new_mblog_notify.isChecked()) {
				mISettingLogic.set(SettingKey.NEW_MBLOG_NOTIFY_KEY, true);
			} else {
				mISettingLogic.set(SettingKey.NEW_MBLOG_NOTIFY_KEY, false);
			}
		}

		if (setting_new_apk_check.isChecked() != is_new_apk_check) {

			if (setting_new_apk_check.isChecked()) {
				mISettingLogic.set(SettingKey.NEW_APK_CHECK_KEY, true);
			} else {
				mISettingLogic.set(SettingKey.NEW_APK_CHECK_KEY, false);
			}
		}
	}

	private void sendPrivacy() {

		boolean isChange = false;

		boolean can_chat_if_followed = false;

		boolean can_invited_if_followed = false;

		boolean can_found_by_phone = false;

		if ((setting_only_following_chat.isChecked() != is_only_following_chat)
				|| (setting_only_following_group_chat.isChecked() != is_only_following_group_chat)
				|| (setting_find_me_from_phone_number.isChecked() != is_find_me_from_phone_number)) {

			isChange = true;
		}

		if (!isChange) {

			return;
		}

		if (setting_only_following_chat.isChecked()) {

			can_chat_if_followed = true;

		} else {

			can_chat_if_followed = false;
		}

		if (setting_only_following_group_chat.isChecked()) {

			can_invited_if_followed = true;

		} else {

			can_invited_if_followed = false;
		}

		if (setting_find_me_from_phone_number.isChecked()) {

			can_found_by_phone = true;

		} else {

			can_found_by_phone = false;
		}

		Privacy privacy = new Privacy();

		privacy.can_chat_if_followed = can_chat_if_followed;

		privacy.can_invited_if_followed = can_invited_if_followed;

		privacy.can_found_by_phone = can_found_by_phone;

		mIAccountLogic.updatePrivacy(privacy);
	}

	@Override
	protected void handleStateMessage(Message msg) {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		save();
	}
}
