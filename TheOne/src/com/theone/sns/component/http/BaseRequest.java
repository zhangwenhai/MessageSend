package com.theone.sns.component.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theone.sns.R;
import com.theone.sns.TheOneApp;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionConfig;

public class BaseRequest extends JsonHttpResponseHandler {

	private static final String TAG = "BaseRequest";

	public String path = null;

	public HttpMethod method = HttpMethod.GET;

	public String contentType = RequestParams.APPLICATION_JSON;

	public boolean needAuth = true;

	public HashMap<String, String> query = new HashMap<String, String>();

	private RequestParams formParameter = new RequestParams();

	private JsonObject jsonParameter = null;

	private JsonArray jsonArrayParameter = null;

	public IHttpListener mHttpListener = null;

	private String mLocalRequestId;

	public BaseRequest(String localRequestId, IHttpListener httpListener) {

		mHttpListener = httpListener;

		mLocalRequestId = localRequestId;
	}

	public BaseRequest start() {

		HttpManager httpManager = HttpManager.getInstance();

		switch (method) {
		case GET:
			httpManager.get(this);
			break;
		case POST:
			httpManager.post(this);
			break;
		case PUT:
			httpManager.put(this);
			break;
		case DELETE:
			httpManager.delete(this);
			break;
		default:
			break;
		}

		return this;
	}

	public void appendQuery(String key, String value) {

		if (null == query) {
			query = new HashMap<String, String>();
		}

		query.put(key, value);
	}

	public void removeQuery(String key) {

		if (null != query) {
			query.remove(key);
		}
	}

	public void putJsonParameter(String key, String value) {

		if (null == jsonParameter) {
			jsonParameter = new JsonObject();
		}

		jsonParameter.addProperty(key, value);
	}

	public void putJsonParameter(String key, boolean value) {

		if (null == jsonParameter) {
			jsonParameter = new JsonObject();
		}

		jsonParameter.addProperty(key, value);
	}

	public void putJsonParameter(String key, JsonElement value) {

		if (null == jsonParameter) {
			jsonParameter = new JsonObject();
		}

		jsonParameter.add(key, value);
	}

	public void setJsonObject(JsonObject jsonObject) {

		if (null == jsonObject) {
			return;
		}

		jsonParameter = jsonObject;
	}

	public void setJsonArray(JsonArray jsonArray) {

		if (null == jsonArray) {
			return;
		}

		jsonArrayParameter = jsonArray;
	}

	public void putFormParameter(String key, String value) {

		if (null == formParameter) {
			formParameter = new RequestParams();
		}

		try {

			formParameter.put(
					URLEncoder.encode(key, FusionConfig.ENCODE_CHARSET_NAME),
					URLEncoder.encode(value, FusionConfig.ENCODE_CHARSET_NAME));

		} catch (Exception e) {

			Log.e(TAG, "in method putFormParameter, encode error");
		}
	}

	public StringEntity parameterEntity() {

		String paraStr = "";

		if (RequestParams.APPLICATION_JSON.equals(contentType)) {

			if (null != jsonParameter) {

				paraStr = jsonParameter.toString();

			} else if (null != jsonArrayParameter) {

				paraStr = jsonArrayParameter.toString();
			}

		} else if (null != formParameter) {

			paraStr = formParameter.toString();
		}

		StringEntity se = null;

		try {

			se = new StringEntity(paraStr, "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return se;
	}

	@Override
	public void onSuccess(final int statusCode, final Header[] headers,
			final JSONObject response) {

		super.onSuccess(statusCode, headers, response);

		new Thread(new Runnable() {

			@Override
			public void run() {

				if (null != mHttpListener) {

					mHttpListener.onResult(new Result(mLocalRequestId,
							statusCode, headers, null, response));
				}
			}

		}).start();
	}

	@Override
	public void onSuccess(final int statusCode, final Header[] headers,
			final JSONArray response) {

		super.onSuccess(statusCode, headers, response);

		new Thread(new Runnable() {

			@Override
			public void run() {

				if (null != mHttpListener) {

					mHttpListener.onResult(new Result(mLocalRequestId,
							statusCode, headers, response, null));
				}
			}

		}).start();
	}

	@Override
	public void onSuccess(final int statusCode, final Header[] headers,
			String responseString) {

		super.onSuccess(statusCode, headers, responseString);

		new Thread(new Runnable() {

			@Override
			public void run() {

				if (null != mHttpListener) {

					mHttpListener.onResult(new Result(mLocalRequestId,
							statusCode, headers, null, null));
				}
			}

		}).start();
	}

	@Override
	public void onFailure(final int statusCode, final Header[] headers,
			Throwable throwable, final JSONArray errorResponse) {

		super.onFailure(statusCode, headers, throwable, errorResponse);

		if (needToRelogin(statusCode)) {
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				if (null != mHttpListener) {

					mHttpListener.onResult(new Result(mLocalRequestId,
							statusCode, headers, errorResponse, null));
				}
			}

		}).start();
	}

	@Override
	public void onFailure(final int statusCode, final Header[] headers,
			Throwable throwable, final JSONObject errorResponse) {

		super.onFailure(statusCode, headers, throwable, errorResponse);

		if (needToRelogin(statusCode)) {
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				if (null != mHttpListener) {

					mHttpListener.onResult(new Result(mLocalRequestId,
							statusCode, headers, null, errorResponse));
				}
			}

		}).start();
	}

	@Override
	public void onFailure(final int statusCode, final Header[] headers,
			String responseString, Throwable throwable) {

		super.onFailure(statusCode, headers, responseString, throwable);

		if (needToRelogin(statusCode)) {
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				if (null != mHttpListener) {

					mHttpListener.onResult(new Result(mLocalRequestId,
							statusCode, headers, null, null));
				}
			}

		}).start();
	}

	private boolean needToRelogin(int statusCode) {

		if (statusCode == 401) {

			Toast.makeText(
					TheOneApp.getContext(),
					TheOneApp.getContext().getResources()
							.getString(R.string.relogin), Toast.LENGTH_SHORT)
					.show();

			Intent intent = new Intent(
					FusionAction.TheOneApp.ACTION_CLOSE_APPLICATION);

			intent.putExtra(FusionAction.TheOneApp.UNAUTHORIZED, true);

			TheOneApp.getContext().sendBroadcast(intent);

			return true;
		}

		return false;
	}

	public enum HttpMethod {
		GET, POST, PUT, DELETE
	}

	public interface BaseParameter {

		String NEXT_MAX_ID = "next_max_id";

		String COUNT = "count";
	}

	public interface TagBaseParameter {

		String LOCATION_TAG = "location_tag";

		String TEXT_TAG = "text_tag";
	}
}
