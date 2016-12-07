package com.theone.sns.framework.model;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@SuppressWarnings("serial")
public class BaseModel implements Serializable {

	private static final String TAG = "BaseModel";

	public static BaseModel fromJson(JSONObject json, Class<?> cls) {

		if (null == json) {
			return null;
		}

		final Gson gson = new Gson();

		return (BaseModel) gson.fromJson(json.toString(), cls);
	}

	public static List<BaseModel> fromJson(JSONArray json, Class<?> cls) {

		if (null == json) {
			return null;
		}

		final Gson gson = new Gson();

		ArrayList<BaseModel> baseModelList = new ArrayList<BaseModel>();

		for (int i = 0; i < json.length(); ++i) {

			try {

				baseModelList.add((BaseModel) gson.fromJson(
						json.getJSONObject(i).toString(), cls));

			} catch (Exception e) {

				Log.e(TAG, "fromJson JSONArray error, e = " + e.getMessage());
			}
		}

		return baseModelList;
	}

	/**
	 * Transform all the property in the class
	 *
	 * @return
	 */
	public JsonElement toJson() {
		return toJson(null);
	}

	/**
	 * Transform the property in the class which contain in the |containKeys|,
	 *
	 * @param containKeys
	 *            If null, transform all the property
	 * @return
	 */
	public JsonElement toJson(final String[] containKeys) {
		final Gson gson = new GsonBuilder().addSerializationExclusionStrategy(
				new ExclusionStrategy() {

					@Override
					public boolean shouldSkipField(FieldAttributes arg0) {
						return checkPropertyNeedSkip(arg0.getName(),
								containKeys);
					}

					@Override
					public boolean shouldSkipClass(Class<?> arg0) {
						return false;
					}
				}).create();

		return gson.toJsonTree(this);
	}

	/**
	 * Transform the JSON array for |objs|. The all item property will be set
	 * into
	 *
	 * @param objs
	 * @return
	 */
	public static JsonElement toJsonArray(List<?> objs) {
		return toJsonArray(objs, null);
	}

	public static JsonElement toJsonObject(BaseModel baseModel) {
		return toJsonArray(baseModel, null);
	}

	/**
	 * Transform the JSON array for |objs|. The item property will be set into
	 * JSON when it contained in the |itemContainKeys|
	 *
	 * @param objs
	 * @param itemContainKeys
	 *            If null, will set all the property into JSON
	 * @return
	 */
	public static JsonElement toJsonArray(List<?> objs,
			final String[] itemContainKeys) {
		final Gson gson = new GsonBuilder().addSerializationExclusionStrategy(
				new ExclusionStrategy() {

					@Override
					public boolean shouldSkipField(FieldAttributes arg0) {
						return checkPropertyNeedSkip(arg0.getName(),
								itemContainKeys);
					}

					@Override
					public boolean shouldSkipClass(Class<?> arg0) {
						return false;
					}
				}).create();

		Type listType = new TypeToken<ArrayList<BaseModel>>() {
		}.getType();

		return gson.toJsonTree(objs, listType);
	}

	public static JsonElement toJsonArray(BaseModel baseModel,
			final String[] itemContainKeys) {
		final Gson gson = new GsonBuilder().addSerializationExclusionStrategy(
				new ExclusionStrategy() {

					@Override
					public boolean shouldSkipField(FieldAttributes arg0) {
						return checkPropertyNeedSkip(arg0.getName(),
								itemContainKeys);
					}

					@Override
					public boolean shouldSkipClass(Class<?> arg0) {
						return false;
					}
				}).create();

		Type objectType = new TypeToken<BaseModel>() {
		}.getType();

		return gson.toJsonTree(baseModel, objectType);
	}

	private static boolean checkPropertyNeedSkip(String checkedProperty,
			String[] containPropertyList) {
		if (null == containPropertyList) {
			return false;
		}

		for (String item : containPropertyList) {
			if (item.equals(checkedProperty)) {
				return true;
			}
		}

		return false;
	}
}
