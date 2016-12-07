package com.theone.sns.ui.me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.sharesdk.ShareUtil;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.account.IRegisterLogic;
import com.theone.sns.logic.model.account.RegisterInviteCode;
import com.theone.sns.ui.base.IphoneTitleActivity;

/**
 * Created by zhangwenhai on 2014/11/13.
 */
public class MyInvitationCodeActivity extends IphoneTitleActivity {
	private TextView invitationCode;
	private ImageView userAvatar;
	private Button shareBtn;
	private TextView fansi1;
	private TextView fansi2;
	private TextView inviteFrom;
	private IRegisterLogic mIRegisterLogic;
	private String requestId;

	@Override
	protected void initLogics() {
		mIRegisterLogic = (IRegisterLogic) getLogicByInterfaceClass(IRegisterLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.my_invitation_code_main);

		getView();

		setView();

		setListener();

		sendRequest();
	}

	private void getView() {
		invitationCode = (TextView) findViewById(R.id.invitation_code);
		userAvatar = (ImageView) findViewById(R.id.user_avatar);
		shareBtn = (Button) findViewById(R.id.share_btn);
		fansi1 = (TextView) findViewById(R.id.fansi1);
		fansi2 = (TextView) findViewById(R.id.fansi2);
		inviteFrom = (TextView) findViewById(R.id.invite_code_from);
	}

	private void setView() {
		setTitle(R.string.get_register_invite_code);
		setLeftButton(R.drawable.icon_back, false, false);
		setRightButton(R.drawable.navigation_more_icon, false);

		ImageLoader.getInstance().displayImage(
				FusionConfig.getInstance().getMyUser().avatar_url, userAvatar,
				optionsForUserIcon);
	}

	private void setListener() {
		shareBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ShareUtil.showShareInviteCode(invitationCode.getText() + "");
			}
		});

		getRightButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(
						FusionAction.RegisterAction.INVITATIONCODEDETAILS_ACTION));
			}
		});
	}

	private void sendRequest() {
		requestId = mIRegisterLogic.getRegisterInviteCode();
	}

	@Override
	protected void handleStateMessage(Message msg) {
		switch (msg.what) {
		case FusionMessageType.AccountMessageType.GET_REGISTER_INVITE_CODE_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					RegisterInviteCode registerInviteCode = (RegisterInviteCode) object.mObject;
					if (null != registerInviteCode) {
						invitationCode.setText(registerInviteCode.code);
						invitationCode.setTextScaleX(1.1f);
						if (null != registerInviteCode.count) {
							fansi1.setText(registerInviteCode.count.used_by_1);
							fansi2.setText(registerInviteCode.count.used_by_2);
						}

						if (null != registerInviteCode.invited_by) {

							String inviteBy = String
									.format(getResources()
											.getString(
													R.string.my_register_invite_code_from),
											new Object[] {
													registerInviteCode.invited_by.code,
													registerInviteCode.invited_by.name });

							inviteFrom.setText(inviteBy);
						}
					}
				}
			}
			break;
		}
		default:
		}
	}
}
