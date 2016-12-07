package com.theone.sns.ui.me;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.theone.sns.common.FusionAction;

/**
 * Created by zhangwenhai on 2014/9/21.
 */
public class GotoTaActivityOnClickListener implements View.OnClickListener {

	private String id;

	private Context mContext;

	public GotoTaActivityOnClickListener(Context mContext, String id) {
		this.id = id;
		this.mContext = mContext;
	}

	@Override
	public void onClick(View view) {
		mContext.startActivity(new Intent(FusionAction.MeAction.TA_ACTION).putExtra(
				FusionAction.MeAction.UID, id).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}
}