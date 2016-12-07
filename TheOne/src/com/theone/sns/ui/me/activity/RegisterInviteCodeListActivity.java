package com.theone.sns.ui.me.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.TheOneApp;
import com.theone.sns.common.FusionMessageType.AccountMessageType;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.logic.account.IRegisterLogic;
import com.theone.sns.logic.model.account.RegisterInviteCode;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.base.dialog.TheOneAlertDialog;
import com.theone.sns.util.uiwidget.ImageLoaderViewHolder;
import com.theone.sns.util.uiwidget.pulltorefresh.PullToRefreshListView;

public class RegisterInviteCodeListActivity extends IphoneTitleActivity {

	private PullToRefreshListView mPullToRefreshListView;

	private ListView listView;

	protected RegisterInviteCodeListAdapter mRegisterInviteCodeListAdapter;

	private LayoutInflater inflater;

	public IRegisterLogic mIRegisterLogic;

	public String requestId;

	@Override
	protected void initLogics() {

		mIRegisterLogic = (IRegisterLogic) getLogicByInterfaceClass(IRegisterLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setSubContent(R.layout.user_list_main);

		getView();

		setView();
	}

	private void getView() {

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.use_list);

		listView = mPullToRefreshListView.getRefreshableView();
	}

	private void setView() {

		setLeftButton(R.drawable.icon_back, false, false);

		setTitle(R.string.get_register_invite_code);

		mRegisterInviteCodeListAdapter = new RegisterInviteCodeListAdapter();

		listView.setAdapter(mRegisterInviteCodeListAdapter);

		sendRequest();
	}

	protected void sendRequest() {

		showLoadingDialog();

		requestId = mIRegisterLogic.getRegisterInviteCode();
	}

	protected class RegisterInviteCodeListAdapter extends BaseAdapter {

		private List<RegisterInviteCode> mRegisterInviteCodeList = new ArrayList<RegisterInviteCode>();

		@Override
		public int getCount() {
			return mRegisterInviteCodeList.size();
		}

		@Override
		public Object getItem(int i) {
			return i;
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			final ImageLoaderViewHolder holder;
			if (view == null) {
				if (null == inflater) {
					inflater = LayoutInflater.from(getApplicationContext());
				}
				view = inflater.inflate(R.layout.register_invite_list_item,
						null);
				holder = new ImageLoaderViewHolder();
				
				holder.mLinearLayout = (LinearLayout) view
						.findViewById(R.id.register_invite_view);
				
				holder.mTextView = (TextView) view
						.findViewById(R.id.register_invite_code);
				view.setTag(holder);
			} else {
				holder = (ImageLoaderViewHolder) view.getTag();
			}

			final RegisterInviteCode mRegisterInviteCode = mRegisterInviteCodeList
					.get(i);

			holder.mTextView.setText(mRegisterInviteCode.code);
			
			
			holder.mLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View arg0) {
					
					new TheOneAlertDialog.Builder(RegisterInviteCodeListActivity.this)
					.setMessage(R.string.copy_register_invite_code)
					.setPositiveButton(R.string.Done, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							ClipboardManager cmb = (ClipboardManager)TheOneApp.getContext().getSystemService(Context.CLIPBOARD_SERVICE);  
							cmb.setText(mRegisterInviteCode.code); 
						}
					})
					.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					}).show();
					
					return false;
				}
			});

			return view;
		}

		public void setRegisterInviteCodeList(
				List<RegisterInviteCode> mRegisterInviteCodeList) {
			if (null != mRegisterInviteCodeList)
				this.mRegisterInviteCodeList = mRegisterInviteCodeList;
		}
	}

	@Override
	protected void handleStateMessage(Message msg) {

		hideLoadingDialog();

		switch (msg.what) {

		case AccountMessageType.GET_REGISTER_INVITE_CODE_SUCCESS: {
			UIObject object = (UIObject) msg.obj;
			if (null != object && !TextUtils.isEmpty(requestId)
					&& requestId.equals(object.mLocalRequestId)) {
				if (null != object.mObject) {
					List<RegisterInviteCode> registerInviteCodeList = (List<RegisterInviteCode>) object.mObject;

					mRegisterInviteCodeListAdapter
							.setRegisterInviteCodeList(registerInviteCodeList);
					mRegisterInviteCodeListAdapter.notifyDataSetChanged();
				}
			}
			break;
		}
		default:
		}
	}
}
