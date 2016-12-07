package com.theone.sns;

import java.io.File;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import cn.sharesdk.framework.ShareSDK;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.theone.sns.component.contact.UploadContactsThread;
import com.theone.sns.component.database.sharedprefs.SharedPref;
import com.theone.sns.component.location.gps.LocationManager;
import com.theone.sns.component.location.poi.PoiSearchManager;
import com.theone.sns.service.TheOneService;
import com.theone.sns.util.DownloadUtil;
import com.theone.sns.util.uiwidget.ExpressionUtil;

public class TheOneApp extends Application {

	private static final String TAG = TheOneApp.class.getSimpleName();

	/**
	 * TheOneApp应用的context
	 */
	private static Context mContext;

	/**
	 * 当前设备宽度
	 */
	private static int deviceWidth;

	/**
	 * 当前设备高度
	 */
	private static int deviceHeight;

	/**
	 * 当前设备分辨率
	 */
	private static float density;

	public static Context getContext() {
		return mContext;
	}

	public static int getDeviceWidth() {

		if (deviceWidth <= 0) {

			deviceWidth = mContext.getResources().getDisplayMetrics().widthPixels;
		}

		return deviceWidth;
	}

	public static int getDeviceHeight() {

		if (deviceHeight <= 0) {

			deviceHeight = mContext.getResources().getDisplayMetrics().heightPixels;
		}

		return deviceHeight;
	}

	public static float getDensity() {

		if (density <= 0) {

			density = mContext.getResources().getDisplayMetrics().density;
		}

		return density;
	}

	/**
	 * 初始化一些参数
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		mContext = getApplicationContext();

		SDKInitializer.initialize(this);

		PoiSearchManager.getInstance();

		LocationManager.getInstance().initLocation();

		LocationManager.getInstance().start();

		ShareSDK.initSDK(this);

		deviceWidth = getResources().getDisplayMetrics().widthPixels;

		deviceHeight = getResources().getDisplayMetrics().heightPixels;

		density = getResources().getDisplayMetrics().density;

		File file = new File(Environment.getDataDirectory() + "/data/"
				+ getContext().getPackageName() + File.separator + "imagecache");

		// 如果不存在,创建文件目录
		if (!file.exists()) {
			if (!file.mkdirs()) {
				Log.e(TAG, "dirs file fail");
			}
		}

		file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + "/theone/imagecache");

		// 如果不存在,创建文件目录
		if (!file.exists()) {
			if (!file.mkdirs()) {
				Log.e(TAG, "dirs file fail");
			}
		}

		initImageLoader(getApplicationContext());

		UploadContactsThread.getInstance().startQuery();

		// 初始化表情
		ExpressionUtil.init(this);

		DownloadUtil.createSDDir();

		// 开启服务
		startService(new Intent(this, TheOneService.class));
	}

	/**
	 *
	 * 停止的时候<BR>
	 * [功能详细描述]
	 *
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {
		super.onTerminate();
		// 开启服务
		stopService(new Intent(this, TheOneService.class));

		Log.d(TAG, "onTerminate");
	}

	@Override
	public void onLowMemory() {
		ImageLoader.getInstance().clearMemoryCache();
	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 1)
				.denyCacheImageMultipleSizesInMemory().threadPoolSize(3)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				.memoryCache(new WeakMemoryCache())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}

	public static ContentResolver getResolver() {
		return mContext.getContentResolver();
	}

	public static SharedPref getSharedPref() {
		return SharedPref.getInstance(mContext);
	}

	public static void setContext(Context context) {
		mContext = context;
	}
}
