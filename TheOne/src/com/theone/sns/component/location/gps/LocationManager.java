package com.theone.sns.component.location.gps;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.theone.sns.TheOneApp;

public class LocationManager implements BDLocationListener {

	private static final String TAG = "LocationManager";

	private static LocationManager mLocationManager;

	/**
	 * 定位服务
	 */
	private LocationClient mLocationClient;

	/**
	 * 定位结果
	 */
	private LocalLocation mLocation = null;

	private List<ILocationListener> mILocationListenerList = new ArrayList<ILocationListener>();

	private LocationManager() {
	}

	public static LocationManager getInstance() {

		if (null == mLocationManager) {

			mLocationManager = new LocationManager();
		}

		return mLocationManager;
	}

	public void initLocation() {

		mLocationClient = new LocationClient(TheOneApp.getContext());

		mLocationClient.registerLocationListener(this);

		mLocationClient.setLocOption(getLocationParameter());
	}

	public LocalLocation getLocation() {

		return mLocation;
	}

	public void start() {

		if (null != mLocationClient) {

			mLocationClient.start();
		}
	}

	public void start(ILocationListener listener) {

		addListener(listener);

		if (null != mLocationClient) {

			mLocationClient.start();
		}
	}

	public void stop() {

		if (null != mLocationClient) {

			mLocationClient.stop();
		}
	}

	public void addListener(ILocationListener locationListener) {

		if (null != mILocationListenerList) {
			mILocationListenerList.add(locationListener);
		}
	}

	public void removeListener(ILocationListener locationListener) {

		if (null != mILocationListenerList) {
			mILocationListenerList.remove(locationListener);
		}
	}

	@Override
	public void onReceiveLocation(BDLocation location) {

		if (null == location || (location.getLongitude() == 4.9E-324)
				|| (location.getLatitude() == 4.9E-324)) {

			Log.e(TAG, "get Location fail");

			mLocation = null;

			callBackListener(false, mLocation);

			stop();

			return;
		}

		Log.i(TAG, "get Location success");

		LocalLocation localLocation = getResult(location);

		mLocation = localLocation;

		callBackListener(true, localLocation);

		stop();
	}

	private void callBackListener(boolean result, LocalLocation location) {

		if (null == mILocationListenerList || mILocationListenerList.isEmpty()) {

			return;
		}

		for (ILocationListener listener : mILocationListenerList) {

			listener.onResult(result, location);
		}
	}

	private LocalLocation getResult(BDLocation location) {

		if (null == location) {

			Log.e(TAG, "getResult error,BDLocation is null");

			return null;
		}

		LocalLocation locallocation = new LocalLocation();

		locallocation.longitude = location.getLongitude() + "";

		locallocation.latitude = location.getLatitude() + "";

		locallocation.address = location.getAddrStr();

		locallocation.city = location.getCity();

		return locallocation;
	}

	private LocationClientOption getLocationParameter() {

		LocationClientOption option = new LocationClientOption();

		// 设置定位模式
		option.setLocationMode(LocationMode.Hight_Accuracy);

		// 返回的定位结果是百度经纬度，默认值gcj02
		option.setCoorType("bd09ll");

		// 设置发起定位请求的间隔时间为5000ms
		option.setScanSpan(3000);

		option.setIsNeedAddress(true);

		return option;
	}
}
