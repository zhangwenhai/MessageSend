package com.theone.sns.component.upload.resumableio;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.net.Uri;

import com.theone.sns.component.upload.auth.Authorizer;
import com.theone.sns.component.upload.conf.Conf;
import com.theone.sns.component.upload.resumableio.SliceUploadTask.Block;
import com.theone.sns.component.upload.rs.CallBack;
import com.theone.sns.component.upload.rs.CallRet;
import com.theone.sns.component.upload.rs.PutExtra;
import com.theone.sns.component.upload.rs.UploadTaskExecutor;
import com.theone.sns.component.upload.utils.InputStreamAt;

public class ResumableIO {

	public static UploadTaskExecutor putFile(Context mContext, Authorizer auth,
			String key, Uri uri, PutExtra extra, CallBack callback) {

		return putFile(mContext, auth, key, uri, extra, null, callback);
	}

	public static UploadTaskExecutor putFile(Context mContext, Authorizer auth,
			String key, Uri uri, PutExtra extra, List<Block> blocks,
			CallBack callback) {

		try {

			return put(auth, key, InputStreamAt.fromUri(mContext, uri), extra,
					blocks, callback);

		} catch (Exception e) {

			callback.onFailure(new CallRet(Conf.ERROR_CODE, "", e));

			return null;
		}
	}

	public static UploadTaskExecutor putFile(Authorizer auth, String key,
			File file, PutExtra extra, CallBack callback) {

		return putFile(auth, key, file, extra, null, callback);
	}

	public static UploadTaskExecutor putFile(Authorizer auth, String key,
			File file, PutExtra extra, List<Block> blocks, CallBack callback) {

		try {

			return put(auth, key, InputStreamAt.fromFile(file), extra, blocks,
					callback);

		} catch (Exception e) {

			callback.onFailure(new CallRet(Conf.ERROR_CODE, "", e));

			return null;
		}
	}

	public static UploadTaskExecutor put(Authorizer auth, String key,
			InputStreamAt input, PutExtra extra, CallBack callback) {

		return put(auth, key, input, extra, null, callback);
	}

	public static UploadTaskExecutor put(Authorizer auth, String key,
			InputStreamAt input, PutExtra extra, List<Block> blocks,
			CallBack callback) {

		try {

			SliceUploadTask task = new SliceUploadTask(auth, input, key, extra,
					callback);

			task.setLastUploadBlocks(blocks);

			task.execute();

			return new UploadTaskExecutor(task);

		} catch (Exception e) {

			callback.onFailure(new CallRet(Conf.ERROR_CODE, "", e));

			return null;
		}
	}
}
