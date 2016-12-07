package com.theone.sns.ui.publish.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionAction.PublicAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.common.FusionMessageType;
import com.theone.sns.component.location.gps.LocalLocation;
import com.theone.sns.component.location.gps.LocationManager;
import com.theone.sns.component.upload.io.IO;
import com.theone.sns.component.upload.rs.CallBack;
import com.theone.sns.component.upload.rs.CallRet;
import com.theone.sns.component.upload.rs.UploadCallRet;
import com.theone.sns.logic.mblog.IMBlogLogic;
import com.theone.sns.logic.model.mblog.PublishMBlog;
import com.theone.sns.logic.model.mblog.base.AudioDesc;
import com.theone.sns.logic.model.mblog.base.Photo;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.util.ImageLoaderUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenhai on 2014/9/15.
 */
public class SendActivity extends IphoneTitleActivity {

	private static final String TAG = SendActivity.class.getSimpleName();

	private ImageView mImageView;
	private TextView allTextView;
	private TextView friendTextView;
	private TextView publishCircleTextView;
	private TextView publishxlTextView;
	private String filePath;
	private IMBlogLogic mIMBlogLogic;
	private PublishMBlog mPublishMBlog;
	private boolean uploading;
	private String photoUrl;
	private String recordUrl;
	private String requestId;
	public String videoUrl;
	private TextView recorderTxtTextView;

	private List<String> chooseShareList = new ArrayList<String>();
	private String publishCircleStr = "";
	private String publishxlStr = "";

	@Override
	protected void initLogics() {
		mIMBlogLogic = (IMBlogLogic) getLogicByInterfaceClass(IMBlogLogic.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSubContent(R.layout.send_main);

		getView();

		setView();

	}

	private void getView() {
		mImageView = (ImageView) findViewById(R.id.image);

		allTextView = (TextView) findViewById(R.id.all);
		friendTextView = (TextView) findViewById(R.id.friend);

		publishCircleTextView = (TextView) findViewById(R.id.publish_circle);
		publishxlTextView = (TextView) findViewById(R.id.publish_xl);

		recorderTxtTextView = (TextView) findViewById(R.id.recorder_txt);

		publishCircleStr = publishCircleTextView.getText().toString();
		publishxlStr = publishxlTextView.getText().toString();
	}

	private void setView() {
		setRightButton(R.string.release, true);
		setLeftButton(R.drawable.icon_back, false, false);
		filePath = getIntent().getStringExtra(FusionAction.PublicAction.FILE_PATH);
		if (TextUtils.isEmpty(filePath) || !new File(filePath).exists()) {
			finish();
		}
		mImageView.setImageBitmap(ImageLoaderUtil.decodeFile(filePath));
		mPublishMBlog = (PublishMBlog) getIntent().getSerializableExtra(
				FusionAction.PublicAction.PUBLICMBLOG);

		getRightButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				hideInputWindow(view);

				// showLoadingDialog(getString(R.string.dialog_publish_mblog));

				mPublishMBlog.text_desc = recorderTxtTextView.getText().toString();

				if (FusionAction.PublicAction.TYPE_VIDEO == getIntent().getIntExtra(
						FusionAction.PublicAction.PUBLIC_TYPE, 0)) {
					doUploadFile(new File(getIntent().getStringExtra(
							FusionAction.PublicAction.VIDEO_PATH)));
				} else if (null != getIntent().getStringExtra(FusionAction.PublicAction.AUDIO_PATH)) {
					doUploadFile(new File(getIntent().getStringExtra(
							FusionAction.PublicAction.AUDIO_PATH)));
				} else {
					doUpload(new File(filePath));
				}
			}
		});

		allTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				friendTextView.setTextColor(getResources().getColor(R.color.black));
				((TextView) view).setTextColor(Color.parseColor("#2AB558"));
				mPublishMBlog.visibility = FusionCode.PublishMBlogVisibility.ALL;
			}
		});

		friendTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				allTextView.setTextColor(getResources().getColor(R.color.black));
				((TextView) view).setTextColor(Color.parseColor("#2AB558"));
				mPublishMBlog.visibility = FusionCode.PublishMBlogVisibility.STARRING;
			}
		});

		publishCircleTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				Drawable circle_highlight = getResources().getDrawable(
						R.drawable.publish_circle_of_friends_highlight_icon);

				Drawable circle = getResources().getDrawable(
						R.drawable.publish_circle_of_friends_icon);

				if (chooseShareList.contains(publishCircleStr)) {

					chooseShareList.remove(publishCircleStr);

					publishCircleTextView.setTextColor(getResources().getColor(R.color.gray));

					publishCircleTextView.setCompoundDrawablesWithIntrinsicBounds(null, circle,
							null, null);

				} else {

					chooseShareList.add(publishCircleStr);

					publishCircleTextView.setTextColor(getResources().getColor(R.color.black));

					publishCircleTextView.setCompoundDrawablesWithIntrinsicBounds(null,
							circle_highlight, null, null);
				}
			}
		});

		publishxlTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Drawable xl_highlight = getResources().getDrawable(
						R.drawable.publish_xl_highlight_icon);

				Drawable xl = getResources().getDrawable(R.drawable.publish_xl_icon);

				if (chooseShareList.contains(publishxlStr)) {

					chooseShareList.remove(publishxlStr);

					publishxlTextView.setTextColor(getResources().getColor(R.color.gray));

					publishxlTextView.setCompoundDrawablesWithIntrinsicBounds(null, xl, null, null);

				} else {

					chooseShareList.add(publishxlStr);

					publishxlTextView.setTextColor(getResources().getColor(R.color.black));

					publishxlTextView.setCompoundDrawablesWithIntrinsicBounds(null, xl_highlight,
							null, null);
				}
			}
		});

		friendTextView.setTextColor(getResources().getColor(R.color.black));
		allTextView.setTextColor(Color.parseColor("#2AB558"));
		mPublishMBlog.visibility = FusionCode.PublishMBlogVisibility.ALL;
	}

	/**
	 * 上传文件
	 *
	 * @param file
	 */
	private void doUpload(File file) {

		if (uploading) {

			Log.e(TAG, "in method doUpload, have uploading");

			return;
		}

		uploading = true;

		IO.putFile(FusionConfig.getInstance().getUploadAuthorizer(), null, file, null,
				new CallBack() {

					@Override
					public void onProcess(long current, long total) {

						int percent = (int) (current * 100 / total);

						Log.d(TAG, "上传中: " + current + "/" + total + "  " + current / 1024 + "K/"
								+ total / 1024 + "K; " + percent + "%");

						showLoadingDialog(getString(R.string.dialog_publish_mblog) + percent + "%",
								-1, false, false);
					}

					@Override
					public void onSuccess(UploadCallRet ret) {

						uploading = false;

						if (FusionAction.PublicAction.TYPE_VIDEO == getIntent().getIntExtra(
								FusionAction.PublicAction.PUBLIC_TYPE, 0)) {

							mPublishMBlog.video.thumbnail_url = FusionConfig.MEDIA_URL_PREFIX
									+ ret.getKey();

						} else {

							photoUrl = FusionConfig.MEDIA_URL_PREFIX + ret.getKey();

							if (null == mPublishMBlog.photo) {
								Photo mPhoto = new Photo();

								mPhoto.url = photoUrl;

								mPublishMBlog.photo = mPhoto;
							} else {
								mPublishMBlog.photo.url = photoUrl;
							}
						}

						LocalLocation localLocation = LocationManager.getInstance().getLocation();

						if (null != localLocation) {

							List<String> location = new ArrayList<String>();

							location.add(localLocation.longitude);

							location.add(localLocation.latitude);

							mPublishMBlog.location = location;
						}

						requestId = mIMBlogLogic.publishMBlog(mPublishMBlog);

						Log.d(TAG, "上传成功! ret: " + ret.toString() + "  \r\n可到" + photoUrl);
					}

					@Override
					public void onFailure(CallRet ret) {

						hideLoadingDialog();

						showToast(getString(R.string.publish_mblog_fail));

						uploading = false;

						Log.e(TAG, "fail: " + ret.toString());
					}
				});
	}

	/**
	 * 上传文件
	 *
	 * @param file
	 */
	private void doUploadFile(final File file) {

		if (uploading) {

			Log.e(TAG, "in method doUpload, have uploading");

			return;
		}

		uploading = true;

		IO.putFile(FusionConfig.getInstance().getUploadAuthorizer(), null, file, null,
				new CallBack() {

					@Override
					public void onProcess(long current, long total) {

						int percent = (int) (current * 100 / total);

						Log.d(TAG, "上传中: " + current + "/" + total + "  " + current / 1024 + "K/"
								+ total / 1024 + "K; " + percent + "%");

						showLoadingDialog(getString(R.string.dialog_publish_mblog) + percent + "%",
								-1, false, false);
					}

					@Override
					public void onSuccess(UploadCallRet ret) {

						uploading = false;

						if (FusionAction.PublicAction.TYPE_VIDEO == getIntent().getIntExtra(
								FusionAction.PublicAction.PUBLIC_TYPE, 0)) {

							videoUrl = FusionConfig.MEDIA_URL_PREFIX + ret.getKey();

							mPublishMBlog.video.url = videoUrl;

							file.delete();

							doUpload(new File(filePath));

						} else {

							recordUrl = FusionConfig.MEDIA_URL_PREFIX + ret.getKey();

							AudioDesc mAudioDesc = new AudioDesc();

							mAudioDesc.url = recordUrl;

							mAudioDesc.duration = getIntent().getIntExtra(
									FusionAction.PublicAction.AUDIO_TIME, 0);

							mPublishMBlog.audio_desc = mAudioDesc;

							file.delete();

							doUpload(new File(filePath));
						}
						Log.d(TAG, "上传成功! ret: " + ret.toString() + "  \r\n可到" + photoUrl);
					}

					@Override
					public void onFailure(CallRet ret) {

						hideLoadingDialog();

						showToast(getString(R.string.publish_mblog_fail));

						uploading = false;

						file.delete();

						Log.e(TAG, "fail: " + ret.toString());
					}
				});
	}

	private void setShareData(Intent intent) {

		if (null != mPublishMBlog && null != mPublishMBlog.photo) {

			String[] plats = new String[chooseShareList.size()];

			chooseShareList.toArray(plats);

			intent.putExtra(PublicAction.SHARE_PLATFORM, plats);

			intent.putExtra(PublicAction.FILE_PATH, filePath);
		}
	}

	@Override
	protected void handleStateMessage(Message msg) {

		hideLoadingDialog();

		switch (msg.what) {
		case FusionMessageType.MBlogMessageType.PUBLISH_MBLOG_SUCCESS: {

			LocalBroadcastManager.getInstance(SendActivity.this).sendBroadcast(
					new Intent(FusionAction.TheOneApp.UPDATE));

			Intent intent = new Intent(FusionAction.RegisterAction.MAIN_ACTION)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			setShareData(intent);

			startActivity(intent);

			break;
		}
		case FusionMessageType.MBlogMessageType.PUBLISH_MBLOG_FAIL: {
			showToast(getString(R.string.publish_mblog_fail));
			break;
		}
		}
	}
}
