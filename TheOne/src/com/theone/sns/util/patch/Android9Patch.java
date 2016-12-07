package com.theone.sns.util.patch;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;

@TargetApi(9)
public class Android9Patch {
	public static boolean isSDCardRemovable() {
		if (Build.VERSION.SDK_INT >= 9)
			return Environment.isExternalStorageRemovable();
		return true;
	}
}
