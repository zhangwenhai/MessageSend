package com.theone.sns.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.theone.sns.common.FusionConfig;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created by zhangwenhai on 2014/9/8.
 */
public class FileStore extends BroadcastReceiver {

	private static boolean sdcardAvailable;
	private static boolean sdcardAvailabilityDetected;

	public static boolean isSDCardAvailable() {
		if (!sdcardAvailabilityDetected) {
			sdcardAvailable = detectSDCardAvailability();
			sdcardAvailabilityDetected = true;
		}
		return sdcardAvailable;
	}

	/**
	 *
	 * @return SD is available ?
	 */
	public static synchronized boolean detectSDCardAvailability() {
		boolean result = false;
		try {
			Date now = new Date();
			long times = now.getTime();
			String fileName = "/sdcard/" + times + ".test";
			File file = new File(fileName);
			result = file.createNewFile();
			file.delete();
		} catch (Exception e) {
			// Can't create file, SD Card is not available
			e.printStackTrace();
		} finally {
			sdcardAvailabilityDetected = true;
			sdcardAvailable = result;
		}
		return result;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		sdcardAvailabilityDetected = false;
		sdcardAvailable = detectSDCardAvailability();
		sdcardAvailabilityDetected = true;
	}

	public static String genNewFilePath() {
		return genNewFilePath("");
	}

	public static String genNewFilePath(String postfix) {
		UUID a = UUID.randomUUID();
		String uuidstr = a.toString();
		String md5url = MD5Util.md5(uuidstr);
		md5url = String.valueOf(Math.abs(md5url.hashCode()) % 16) + File.separator + md5url;
		String realfile = null;

		realfile = FusionConfig.SAVE_IMAGE_EXTERNAL_PATH + md5url;

		String returnPath = null;
		if (TextUtils.isEmpty(postfix)) {
			returnPath = realfile;
		} else
			returnPath = realfile + postfix;
		try {
			File f = new File(returnPath);
			File pf = new File(f.getParent());
			pf.mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnPath;
	}
}
