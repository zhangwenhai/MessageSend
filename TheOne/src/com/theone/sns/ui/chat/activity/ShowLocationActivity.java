package com.theone.sns.ui.chat.activity;

import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction.ChatAction;
import com.theone.sns.ui.base.IphoneTitleActivity;

public class ShowLocationActivity extends IphoneTitleActivity {

	MapView mMapView;

	BaiduMap mBaiduMap;

	double longitude;

	double latitude;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setSubContent(R.layout.show_location);

		initView();

		initMap();
	}

	private void initView() {

		setTitle(R.string.plus3);

		setLeftButton(R.drawable.icon_back, false, false);

		longitude = Double.valueOf(getIntent().getStringExtra(
				ChatAction.LONGITUDE));

		latitude = Double.valueOf(getIntent().getStringExtra(
				ChatAction.LATITUDE));
	}

	private void initMap() {

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);

		mBaiduMap = mMapView.getMap();

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);

		// 设置放大缩小按钮控件最高级别
		mBaiduMap.setMaxAndMinZoomLevel(mBaiduMap.getMaxZoomLevel(),
				mBaiduMap.getMaxZoomLevel());

		// 不显示放大缩小按钮控件
		mMapView.showZoomControls(false);

		MyLocationData locData = new MyLocationData.Builder()
		// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude(latitude).longitude(longitude).build();

		mBaiduMap.setMyLocationData(locData);

		LatLng ll = new LatLng(latitude, longitude);

		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);

		mBaiduMap.animateMapStatus(u);
	}

	@Override
	protected void onPause() {

		mMapView.onPause();

		super.onPause();
	}

	@Override
	public void onResume() {

		mMapView.onResume();

		super.onResume();
	}

	@Override
	protected void onDestroy() {

		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);

		mMapView.onDestroy();

		mMapView = null;

		super.onDestroy();
	}

	@Override
	protected void initLogics() {

	}
}
