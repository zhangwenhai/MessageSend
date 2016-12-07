package com.theone.sns.component.http;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.theone.sns.TheOneApp;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.component.location.gps.LocalLocation;
import com.theone.sns.component.location.gps.LocationManager;
import com.theone.sns.logic.model.account.Account;
import com.theone.sns.util.HttpUtil;

public class HttpManager {

	private static final String TAG = "HttpManager";

	private static HttpManager mHttpManager = null;

	private AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();

	private ExecutorService httpThreadPool = Executors.newFixedThreadPool(5);

	private HttpManager() {
		mAsyncHttpClient.setThreadPool(httpThreadPool);
	}

	public static HttpManager getInstance() {
		if (null == mHttpManager) {
			mHttpManager = new HttpManager();
		}

		return mHttpManager;
	}

	public void get(BaseRequest request) {

		String url = HttpUtil.buildURL(request.path);

		setAccessToken(request);

		RequestParams params = new RequestParams(request.query);

		mAsyncHttpClient.get(TheOneApp.getContext(), url, params, request);

		Log.d(TAG, "Start GET request " + url + " with body "
				+ request.parameterEntity().toString());
	}

	public void put(BaseRequest request) {

		String url = HttpUtil.buildURL(request.path);

		setAccessToken(request);

		url = HttpUtil.appendQuery(url, request.query);

		mAsyncHttpClient.put(TheOneApp.getContext(), url,
				request.parameterEntity(), request.contentType, request);

		Log.d(TAG, "Start put request " + url + " with body "
				+ request.parameterEntity().toString());
	}

	public void post(BaseRequest request) {

		String url = HttpUtil.buildURL(request.path);

		setAccessToken(request);

		url = HttpUtil.appendQuery(url, request.query);

		mAsyncHttpClient.post(TheOneApp.getContext(), url,
				request.parameterEntity(), request.contentType, request);

		Log.d(TAG, "Start post request " + url + " with body "
				+ request.parameterEntity().toString());
	}

	public void delete(BaseRequest request) {

		String url = HttpUtil.buildURL(request.path);

		setAccessToken(request);

		RequestParams params = new RequestParams(request.query);

		mAsyncHttpClient.delete(TheOneApp.getContext(), url, null, params,
				request);

		Log.d(TAG, "Start delete request " + url + " with body "
				+ request.parameterEntity().toString());
	}

	public void setAccessToken(BaseRequest request) {

		Account account = FusionConfig.getInstance().getAccount();

		if (request.needAuth && null != account) {

			if (TextUtils.isEmpty(account.token.access_token)) {
				Log.e(TAG, "in method setAccessToken accessToken is null");
				return;
			}

			mAsyncHttpClient.addHeader(
					FusionConfig.HTTP_REQUEST_HEADER_TOKEN_KEY,
					FusionConfig.HTTP_REQUEST_HEADER_TOKEN_VALUE_PREFIX
							+ account.token.access_token);
		}

		LocalLocation localLocation = LocationManager.getInstance()
				.getLocation();

		if (null == localLocation) {

			return;
		}

		mAsyncHttpClient.addHeader(
				FusionConfig.HTTP_REQUEST_HEADER_LOCATION_KEY,
				localLocation.longitude + FusionConfig.LOCATION_SEPARATE
						+ localLocation.latitude);
	}
}
