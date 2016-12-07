package com.theone.sns.ui.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.logic.chat.IChatLogic;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.ui.base.IphoneTitleActivity;

/**
 * Created by zhangwenhai on 2014/11/1.
 */
public class GroupNameActivity extends IphoneTitleActivity {
	private GroupInfo mGroupInfo;
	private IChatLogic mIChatLogic;
	private EditText name;

	@Override
	protected void initLogics() {
		mIChatLogic = (IChatLogic) getLogicByInterfaceClass(IChatLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.group_name_main);

		getView();

		setView();

		setListener();
	}

	private void getView() {
		mGroupInfo = (GroupInfo) getIntent().getSerializableExtra(
				FusionAction.ChatAction.GROUP_INFO);
		if (null == mGroupInfo) {
			finish();
		}
		name = (EditText) findViewById(R.id.name);
	}

	private void setView() {
		setLeftButton(R.drawable.icon_back, false, false);
		setRightButton(R.string.confirm, true);
		name.setText(mGroupInfo.name);
		name.setSelection(mGroupInfo.name.length());
	}

	private void setListener() {
		getRightButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (name.getText().toString().equals(mGroupInfo.name)) {
					finish();
				} else {
					mIChatLogic.updateGroupName(mGroupInfo._id, name.getText().toString());
				}
			}
		});
	}

	@Override
	protected void handleStateMessage(Message msg) {
		switch (msg.what) {
		case FusionMessageType.ChatMessageType.UPDATE_GROUP_NAME_SUCCESS: {
			Intent mIntent = new Intent();
			mIntent.putExtra(FusionAction.ChatAction.GROUP_NAME, name.getText().toString());
			setResult(Activity.RESULT_OK, mIntent);
			finish();
			break;
		}
		case FusionMessageType.ChatMessageType.UPDATE_GROUP_NAME_FAIL: {
			break;
		}
		default:
		}
	}
}
