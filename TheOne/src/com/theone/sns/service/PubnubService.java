package com.theone.sns.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;
import com.theone.sns.common.FusionCode.PushType;
import com.theone.sns.common.FusionCode.SettingKey;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.adapter.db.GroupDbAdapter;
import com.theone.sns.logic.adapter.db.SettingDbAdapter;
import com.theone.sns.logic.chat.IPushListener;
import com.theone.sns.logic.chat.impl.ChatManager;
import com.theone.sns.logic.model.account.Account;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.logic.model.chat.PushNotify;

public class PubnubService extends Service {

	private static final String TAG = "PubnubService";

	private String channel = "";

	private Pubnub pubnub = null;

	private PowerManager.WakeLock wl = null;

	private ChatManager chatManager;

	private boolean isConnection = false;

	public void onCreate() {

		super.onCreate();

		Log.i(TAG, "PubnubService created...");

		newWakeLock();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.i(TAG, "PubnubService onStartCommand...");

		if (null == pubnub) {

			startPubnub();

		} else {

			String userId = FusionConfig.getInstance().getUserId();

			if (TextUtils.isEmpty(channel)
					|| (!TextUtils.isEmpty(channel) && !channel.equals(userId))
					|| !isConnection) {

				pubnub.unsubscribeAll();

				startPubnub();
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	private void startPubnub() {

		if (FusionConfig.isLogin() && init()) {

			subscribe();
		}
	}

	private boolean init() {

		Account account = FusionConfig.getInstance().getAccount();

		String userId = FusionConfig.getInstance().getUserId();

		if (null == account || null == account.config
				|| TextUtils.isEmpty(account.config.pubnub_sub_key)
				|| TextUtils.isEmpty(userId)) {

			Log.e(TAG, "init punbnub service error, account is null");

			return false;
		}

		String pubnubKey = account.config.pubnub_sub_key;

		pubnub = new Pubnub(pubnubKey, pubnubKey);

		channel = userId;

		chatManager = ChatManager.getInstance();

		chatManager.syncGroups();

		return true;
	}

	private void subscribe() {

		try {

			pubnub.unsubscribe(channel);

			pubnub.subscribe(new String[] { channel }, new Callback() {

				@Override
				public void connectCallback(String arg0, Object arg1) {
					super.connectCallback(arg0, arg1);
					Log.i(TAG, "connect success");
					isConnection = true;
				}

				@Override
				public void disconnectCallback(String arg0, Object arg1) {
					super.disconnectCallback(arg0, arg1);
					Log.i(TAG, "disconnectCallback");
					isConnection = false;
				}

				@Override
				public void reconnectCallback(String arg0, Object arg1) {
					super.reconnectCallback(arg0, arg1);
					Log.i(TAG, "reconnectCallback");
					isConnection = true;
				}

				@Override
				public void successCallback(String channel, Object message) {

					Log.i(TAG, message.toString());

					try {

						JSONObject jsonObj = new JSONObject(message.toString());

						if (!jsonObj.has(PushType.TYPE)) {

							Log.e(TAG, "unknow pubnub push type");

							return;
						}

						String pushType = jsonObj.getString(PushType.TYPE);

						JSONArray object = jsonObj.getJSONArray(PushType.BODY);

						if (null == object || TextUtils.isEmpty(pushType)) {

							Log.e(TAG, "push is null");

							return;
						}

						if (PushType.TYPE_MESSAGE.equals(pushType)) {

							pushMessage(object);

						} else if (PushType.TYPE_GROUPS.equals(pushType)) {

							pushGroup(object);

						} else if (PushType.TYPE_NOTIFICATIONS.equals(pushType)) {

							pushNotification(object);
						}

					} catch (Exception e) {

						Log.e(TAG,
								"pubnub successCallback parse error"
										+ e.getMessage());
					}
				}
			});

		} catch (PubnubException e) {

			Log.e(TAG, "pubnub subscribe error");
		}
	}

	private void pushMessage(JSONArray jsonArray) {

		List<BaseModel> baseModelList = BaseModel.fromJson(jsonArray,
				MessageInfo.class);

		if (null != baseModelList && !baseModelList.isEmpty()) {

			for (BaseModel baseModel : baseModelList) {

				MessageInfo message = (MessageInfo) baseModel;

				chatManager.receiveMessage(message);
			}
		}
	}

	private void pushGroup(JSONArray jsonArray) {

		List<BaseModel> baseModelList = BaseModel.fromJson(jsonArray,
				GroupInfo.class);

		if (null != baseModelList && !baseModelList.isEmpty()) {

			for (BaseModel baseModel : baseModelList) {

				GroupInfo groupInfo = (GroupInfo) baseModel;

				if (null == groupInfo) {

					return;
				}

				if (-1 == GroupDbAdapter.getInstance().insertGroup(groupInfo)) {

					GroupDbAdapter.getInstance().updateGroup(groupInfo);
				}

				if (!groupInfo.is_joined) {

					setGroupInviteItem(groupInfo._id);

					chatManager.callBackPushListener(
							IPushListener.GROUP_INVITE, groupInfo);

				} else {

					chatManager.callBackPushListener(
							IPushListener.GROUP_CHANGE, groupInfo);
				}
			}
		}
	}

	private void pushNotification(JSONArray object) {

		List<BaseModel> baseModelList = BaseModel.fromJson(object,
				PushNotify.class);

		List<PushNotify> pushNotifyList = new ArrayList<PushNotify>();

		if (null != baseModelList && !baseModelList.isEmpty()) {

			for (BaseModel baseModel : baseModelList) {

				PushNotify pushNotify = (PushNotify) baseModel;

				pushNotifyList.add(pushNotify);

				String action = pushNotify.action;

				if (PushType.ACTION_LIKED_BY.equals(action)) {

					setNewNotify(SettingKey.IS_LIKED_BY);

				} else if (PushType.ACTION_FOLLOWED_BY.equals(action)) {

					setNewNotify(SettingKey.IS_FOLLOWED_BY);

				} else if (PushType.ACTION_NEW_POST.equals(action)) {

					boolean isNewMblogNotify = SettingDbAdapter.getInstance()
							.getBoolean(SettingKey.NEW_MBLOG_NOTIFY_KEY, true);

					if (isNewMblogNotify) {

						setNewNotify(SettingKey.IS_NEW_POST);
					}
				}
			}
		}

		int size = pushNotifyList.size();

		if (size > 0) {

			setNotifyItem(size, pushNotifyList.get(size - 1).desc);

			chatManager.callBackPushListener(IPushListener.NOTIFICATION, null);
		}
	}

	private void setNewNotify(String key) {

		SettingDbAdapter.getInstance().insert(key, true);
	}

	private void setNotifyItem(int count, String desc) {

		SettingDbAdapter.getInstance().insert(
				SettingKey.NOTIFICATION_COUNT,
				SettingDbAdapter.getInstance().getInt(
						SettingKey.NOTIFICATION_COUNT)
						+ count);

		if (!TextUtils.isEmpty(desc)) {

			SettingDbAdapter.getInstance().insert(
					SettingKey.NEW_NOTIFICATION_CONTENT, desc);
		}
	}

	private void setGroupInviteItem(String groupId) {

		SettingDbAdapter.getInstance().insert(
				SettingKey.GROUP_INVITE_COUNT,
				SettingDbAdapter.getInstance().getInt(
						SettingKey.GROUP_INVITE_COUNT) + 1);

		SettingDbAdapter.getInstance().insert(
				SettingKey.NEW_GROUP_INVITE_GROUPID, groupId);
	}

	private void newWakeLock() {

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SubscribeAtBoot");

		if (wl != null) {

			wl.acquire();

			Log.i(TAG, "Partial Wake Lock : " + wl.isHeld());
		}
	}

	private void releaseWakeLock() {

		if (wl != null) {

			Log.i(TAG, "Release Wake Lock : " + wl.isHeld());

			wl.release();

			wl = null;
		}
	}

	@Override
	public void onDestroy() {

		releaseWakeLock();

		if (null != pubnub) {

			pubnub.unsubscribeAll();

			pubnub = null;
		}

		isConnection = false;

		Log.i(TAG, "PubnubService destroyed...");

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
