package com.theone.sns.component.http;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class Result {

	public String localRequestId;

	public int statusCode;

	public Header[] headers;

	public JSONArray jsonArray;

	public JSONObject jsonObject;

	public Result(String localRequestId, int statusCode, Header[] headers,
			JSONArray jsonArray, JSONObject jsonObject) {

		this.localRequestId = localRequestId;

		this.statusCode = statusCode;

		this.headers = headers;

		this.jsonArray = jsonArray;

		this.jsonObject = jsonObject;
	}
}
