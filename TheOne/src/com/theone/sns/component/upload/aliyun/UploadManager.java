package com.theone.sns.component.upload.aliyun;

import java.io.File;

import android.content.Context;

import com.aliyun.mbaas.oss.OSSClient;
import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.OSSException;
import com.aliyun.mbaas.oss.model.TokenGenerator;
import com.aliyun.mbaas.oss.storage.OSSFile;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;

public class UploadManager {

	public static void init(Context context) {

		OSSClient.setApplicationContext(context);

		OSSClient.setTokenGenerator(new TokenGenerator() {

			@Override
			public String generateToken(String arg0, String arg1, String arg2,
					String arg3, String arg4, String arg5) {

				return null;
			}
		});
	}

	public static void doUpload(File file, SaveCallback callback) {

		OSSFile ossFile = new OSSFile("",
				new Md5FileNameGenerator().generate(file.getName()));

		ossFile.setUploadFilePath(file.getAbsolutePath(), "");

		ossFile.enableUploadCheckMd5sum();

		ossFile.uploadInBackground(new SaveCallback() {

			@Override
			public void onProgress(int arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(OSSException arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub

			}
		});
	}
}
