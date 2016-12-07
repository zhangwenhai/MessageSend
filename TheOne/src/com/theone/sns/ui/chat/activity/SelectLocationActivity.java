package com.theone.sns.ui.chat.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.theone.sns.R;
import com.theone.sns.common.FusionAction.ChatAction;
import com.theone.sns.component.location.poi.IPoiListener;
import com.theone.sns.component.location.poi.LocalPoiInfo;
import com.theone.sns.component.location.poi.PoiSearchManager;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.chat.SelectLocationAdapter;

/**
 * 此demo用来展示如何结合定位SDK实现定位<br>
 * 并使用MyLocationOverlay绘制定位位置 <br>
 * 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 */
public class SelectLocationActivity extends IphoneTitleActivity {

	// 定位相关
	LocationClient mLocClient;

	public MyLocationListenner myListener = new MyLocationListenner();

	BitmapDescriptor mCurrentMarker;

	MapView mMapView;

	BaiduMap mBaiduMap;

	// UI相关
	OnCheckedChangeListener radioButtonListener;

	IPoiListener poiListener;

	boolean isFirstLoc = true;// 是否首次定位

	private ListView listview;

	private SelectLocationAdapter mSelectLocationAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setSubContent(R.layout.select_location);

		initView();

		initMap();
	}

	private void initView() {

		setTitle(R.string.plus3);

		setLeftButton(R.drawable.icon_back, false, false);

		setRightButton(R.string.send, true);

		listview = (ListView) findViewById(R.id.location);

		mSelectLocationAdapter = new SelectLocationAdapter(this);

		listview.setAdapter(mSelectLocationAdapter);

		getRightButton().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				LocalPoiInfo localPoiInfo = mSelectLocationAdapter
						.getSelectLocation();

				if (null == localPoiInfo) {

					return;
				}

				Intent intent = new Intent();

				intent.putExtra(ChatAction.LOCATION_INFO, localPoiInfo);

				setResult(RESULT_OK, intent);

				finish();
			}
		});
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

		// 定位初始化
		mLocClient = new LocationClient(this);

		mLocClient.registerLocationListener(myListener);

		LocationClientOption option = new LocationClientOption();

		// 打开gps
		option.setOpenGps(true);

		// 设置坐标类型
		option.setCoorType("bd09ll");

		option.setScanSpan(1000);

		mLocClient.setLocOption(option);

		mLocClient.start();

		poiListener = new IPoiListener() {

			@Override
			public void onResult(boolean result, List<LocalPoiInfo> poiInfoList) {

				refreshList(poiInfoList);
			}
		};
	}

	private void refreshList(final List<LocalPoiInfo> poiInfoList) {

		if (null == poiInfoList) {

			return;
		}

		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				mSelectLocationAdapter.setLocalPoiInfoList(poiInfoList);

				mSelectLocationAdapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null) {

				return;
			}

			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();

			mBaiduMap.setMyLocationData(locData);

			if (isFirstLoc) {

				isFirstLoc = false;

				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());

				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);

				mBaiduMap.animateMapStatus(u);

				mLocClient.stop();

				PoiSearchManager.getInstance().geoResult(ll, poiListener);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
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

		// 退出时销毁定位
		mLocClient.stop();

		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);

		mMapView.onDestroy();

		mMapView = null;

		PoiSearchManager.getInstance().removeListener(poiListener);

		super.onDestroy();
	}

	@Override
	protected void initLogics() {
	}
}
