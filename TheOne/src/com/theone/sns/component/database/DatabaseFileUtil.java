package com.theone.sns.component.database;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.theone.sns.TheOneApp;

@SuppressLint("SdCardPath")
public class DatabaseFileUtil {

	private static final String TAG = "DatabaseFileUtil";

	public static final String FOLDER_INTERNAL_ROOT = "/data/data/";

	public static final String FOLDER_INTERNAL_USERDATA = "/theonedata/";

	public static String getDBPath(String name, String userId) {

		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(userId)) {

			return null;
		}

		String dbPath = getUserDataDir(userId);

		if (dbPath == null) {

			return null;
		}

		dbPath += name;

		return dbPath;
	}

	private static String getUserDataDir(String userId) {

		Context context = TheOneApp.getContext();

		if (TextUtils.isEmpty(userId) || null == context) {

			return null;
		}

		String path = getTheOneInternalRootDir() + FOLDER_INTERNAL_USERDATA
				+ userId + "/";

		return getPath(path);
	}

	private static String getTheOneInternalRootDir() {

		Context context = TheOneApp.getContext();

		if (null == context) {

			return null;
		}

		String path = FOLDER_INTERNAL_ROOT + context.getPackageName();

		return getPath(path);
	}

	private static String getPath(String dir) {

		File folder = new File(dir);

		if (!folder.exists()) {

			folder.mkdirs();
		}

		Log.d(TAG, "path = " + dir);

		return dir;
	}
}
