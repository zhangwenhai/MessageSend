package cn.sharesdk;

import java.io.File;

import net.hockeyapp.android.FeedbackManager;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.TheOneApp;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType.MBlogMessageType;

public class ShareUtil {

	private static final String TAG = "ShareUtil";

	public static void showShare(String userId, String filePath, String desc,
			ShareOperation mBlogOperation) {

		if (FusionConfig.getInstance().getUserId().equals(userId)) {

			ShareUtil.showMeShare(filePath, desc, mBlogOperation);

		} else {

			ShareUtil.showOtherShare(filePath, desc, mBlogOperation);
		}
	}

	public static void showOtherShare(String filePath, String desc,
			ShareOperation mBlogOperation) {

		final OnekeyShare oks = getOnekeyShare(filePath, desc, mBlogOperation);

		oks.setCustomerLogo(BitmapFactory.decodeResource(TheOneApp.getContext()
				.getResources(), R.drawable.share_jubao_icon), TheOneApp
				.getContext().getString(R.string.share_report),
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						FeedbackManager.showFeedbackActivity(TheOneApp
								.getContext());

						oks.onFinish();
					}
				});

		oks.show(TheOneApp.getContext());
	}

	public static void showMeShare(String filePath, String desc,
			final ShareOperation mBlogOperation) {

		if (null == mBlogOperation) {

			Log.e(TAG, "showMeShare fail, parameter error");

			return;
		}

		final OnekeyShare oks = getOnekeyShare(filePath, desc, mBlogOperation);

		oks.setCustomerLogo(BitmapFactory.decodeResource(TheOneApp.getContext()
				.getResources(), R.drawable.share_delete_icon), TheOneApp
				.getContext().getString(R.string.share_delete),
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {

						oks.onFinish();

						mBlogOperation.deleteMBlog();

						if (null != mBlogOperation.mHandler) {
							Message msg = new Message();
							msg.what = MBlogMessageType.DELETE_MBLOG_BY_ID;
							msg.obj = mBlogOperation.mblog_id;
							mBlogOperation.mHandler
									.sendMessageDelayed(msg, 500);
						}
					}
				});

		oks.show(TheOneApp.getContext());
	}

	public static void showShareInviteCode(String registerInviteCode) {

		ShareSDK.initSDK(TheOneApp.getContext());

		final OnekeyShare oks = new OnekeyShare();

		oks.disableSSOWhenAuthorize();

		oks.setSilent(true);

		oks.setNotification(R.drawable.notification_icon, TheOneApp
				.getContext().getString(R.string.app_name));

		String inviteCode = String.format(TheOneApp.getContext().getResources()
				.getString(R.string.share_invite_code),
				new Object[] { registerInviteCode });

		oks.setText(inviteCode);

		oks.show(TheOneApp.getContext());
	}

	private static OnekeyShare getOnekeyShare(String filePath, String desc,
			final ShareOperation mBlogOperation) {

		ShareSDK.initSDK(TheOneApp.getContext());

		final OnekeyShare oks = new OnekeyShare();

		oks.disableSSOWhenAuthorize();

		oks.setSilent(true);

		oks.setNotification(R.drawable.notification_icon, TheOneApp
				.getContext().getString(R.string.app_name));

		oks.setCustomerLogo(BitmapFactory.decodeResource(TheOneApp.getContext()
				.getResources(), R.drawable.share_theone_icon), TheOneApp
				.getContext().getString(R.string.share_chat),
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						mBlogOperation.toChat();

						oks.onFinish();
					}
				});

		oks.setText(desc);

		oks.setImagePath(filePath);

		return oks;
	}

	public static String getImagePath(String url) {

		String imagePath = "";

		if (TextUtils.isEmpty(url)) {

			return imagePath;
		}

		File imageFile = ImageLoader.getInstance().getDiskCache().get(url);

		if (null != imageFile && imageFile.exists()) {

			imagePath = imageFile.getAbsolutePath();
		}

		return imagePath;
	}
}
