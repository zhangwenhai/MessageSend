package com.theone.sns.component.location.poi;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.theone.sns.component.location.gps.LocalLocation;
import com.theone.sns.component.location.gps.LocationManager;

public class PoiSearchManager implements OnGetPoiSearchResultListener,
		OnGetSuggestionResultListener, OnGetGeoCoderResultListener {

	private static final String TAG = "PoiSearchManager";

	private static PoiSearchManager mPOISearchManager;

	private PoiSearch mPoiSearch = null;

	private SuggestionSearch mSuggestionSearch = null;

	private GeoCoder mGeoCoder = null;

	private List<IPoiListener> mIPoiListenerList = new ArrayList<IPoiListener>();

	private PoiSearchManager() {

		initPOIManager();
	}

	public static PoiSearchManager getInstance() {

		if (null == mPOISearchManager) {

			mPOISearchManager = new PoiSearchManager();
		}

		return mPOISearchManager;
	}

	private void initPOIManager() {

		mPoiSearch = PoiSearch.newInstance();

		mPoiSearch.setOnGetPoiSearchResultListener(this);

		mSuggestionSearch = SuggestionSearch.newInstance();

		mSuggestionSearch.setOnGetSuggestionResultListener(this);

		mGeoCoder = GeoCoder.newInstance();

		mGeoCoder.setOnGetGeoCodeResultListener(this);
	}

	public void geoResult(IPoiListener poiListener) {

		LatLng latLng = getLatLng();

		if (null == latLng) {

			Log.e(TAG, "geoResult error,location is null");

			return;
		}

		addListener(poiListener);

		ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();

		reverseGeoCodeOption.location(latLng);

		mGeoCoder.reverseGeoCode(reverseGeoCodeOption);
	}

	public void geoResult(LatLng latLng, IPoiListener poiListener) {

		if (null == latLng) {

			Log.e(TAG, "geoResult error,location is null");

			return;
		}

		addListener(poiListener);

		ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();

		reverseGeoCodeOption.location(latLng);

		mGeoCoder.reverseGeoCode(reverseGeoCodeOption);
	}

	public void nearbySearch(String keyword, IPoiListener poiListener) {

		LatLng latLng = getLatLng();

		if (TextUtils.isEmpty(keyword) || null == latLng || null == poiListener) {

			Log.e(TAG, "nearbySearch error, keyword or location is null");

			return;
		}

		addListener(poiListener);

		PoiNearbySearchOption pnso = new PoiNearbySearchOption();

		pnso.location(latLng);

		pnso.radius(3000);

		pnso.keyword(keyword);

		mPoiSearch.searchNearby(pnso);
	}

	@Override
	public void onGetSuggestionResult(SuggestionResult arg0) {
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
	}

	@Override
	public void onGetPoiResult(PoiResult arg0) {

		if (null == arg0) {

			callBackListener(false, new ArrayList<LocalPoiInfo>());

			return;
		}

		if (SearchResult.ERRORNO.NO_ERROR == arg0.error) {

			List<PoiInfo> poiInfoList = arg0.getAllPoi();

			result(poiInfoList);

		} else {

			callBackListener(false, new ArrayList<LocalPoiInfo>());
		}
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {

		if (null == arg0) {

			callBackListener(false, new ArrayList<LocalPoiInfo>());

			return;
		}

		if (SearchResult.ERRORNO.NO_ERROR == arg0.error) {

			List<PoiInfo> poiInfoList = arg0.getPoiList();

			result(poiInfoList);

		} else {

			callBackListener(false, new ArrayList<LocalPoiInfo>());
		}
	}

	private void result(List<PoiInfo> poiInfoList) {

		if (null == poiInfoList || poiInfoList.isEmpty()) {

			callBackListener(true, new ArrayList<LocalPoiInfo>());

			return;
		}

		List<LocalPoiInfo> LocalPoiInfoList = new ArrayList<LocalPoiInfo>();

		for (PoiInfo poiInfo : poiInfoList) {

			LocalPoiInfo localPoiInfo = getLocalPoiInfo(poiInfo);

			if (null != localPoiInfo) {

				LocalPoiInfoList.add(localPoiInfo);
			}
		}

		callBackListener(true, LocalPoiInfoList);
	}

	public void clear() {

		if (null != mGeoCoder) {

			mGeoCoder.destroy();

			mGeoCoder = null;
		}

		if (null != mPoiSearch) {

			mPoiSearch.destroy();

			mPoiSearch = null;
		}

		if (null != mSuggestionSearch) {

			mSuggestionSearch.destroy();

			mSuggestionSearch = null;
		}

		if (null != mIPoiListenerList) {

			mIPoiListenerList.clear();
		}

		mPOISearchManager = null;
	}

	public void addListener(IPoiListener poiListener) {

		if (null != mIPoiListenerList) {
			mIPoiListenerList.add(poiListener);
		}
	}

	public void removeListener(IPoiListener poiListener) {

		if (null != mIPoiListenerList) {
			mIPoiListenerList.remove(poiListener);
		}
	}

	private void callBackListener(boolean result, List<LocalPoiInfo> poiInfoList) {

		if (null == mIPoiListenerList || mIPoiListenerList.isEmpty()) {

			return;
		}

		for (IPoiListener listener : mIPoiListenerList) {

			listener.onResult(result, poiInfoList);
		}
	}

	private LocalPoiInfo getLocalPoiInfo(PoiInfo poiInfo) {

		if (null == poiInfo) {

			return null;
		}

		LocalPoiInfo localPoiInfo = new LocalPoiInfo();

		localPoiInfo.longitude = poiInfo.location.longitude + "";

		localPoiInfo.latitude = poiInfo.location.latitude + "";

		localPoiInfo.city = poiInfo.city;

		localPoiInfo.address = poiInfo.address;

		localPoiInfo.name = poiInfo.name;

		return localPoiInfo;
	}

	private String getCity() {

		LocalLocation location = LocationManager.getInstance().getLocation();

		if (null == location) {

			Log.e(TAG, "getCity error, location is null");

			return null;
		}

		return location.city;
	}

	private LatLng getLatLng() {

		LocalLocation location = LocationManager.getInstance().getLocation();

		if (null == location) {

			Log.e(TAG, "getLatLng error, location is null");

			return null;
		}

		LatLng latLng = new LatLng(Double.valueOf(location.latitude),
				Double.valueOf(location.longitude));

		return latLng;
	}

	private void suggestSearch(String keyword) {

		LatLng latLng = getLatLng();

		String city = getCity();

		if (TextUtils.isEmpty(keyword) || TextUtils.isEmpty(city)
				|| null == latLng) {

			Log.e(TAG,
					"suggestSearch error, keyword or location or city is null");

			return;
		}

		SuggestionSearchOption sso = new SuggestionSearchOption();

		sso.location(latLng);

		sso.city(city);

		sso.keyword(keyword);

		mSuggestionSearch.requestSuggestion(sso);
	}
}
