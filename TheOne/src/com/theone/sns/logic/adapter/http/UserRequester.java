package com.theone.sns.logic.adapter.http;

import java.util.List;

import com.theone.sns.common.FusionCode.FindUserGalleryType;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.component.contact.Contact;
import com.theone.sns.component.http.BaseRequest;
import com.theone.sns.component.http.IHttpListener;
import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.model.mblog.MBlogTag;
import com.theone.sns.logic.model.mblog.base.LocationTag;
import com.theone.sns.logic.model.mblog.base.TextTag;
import com.theone.sns.util.HttpUtil;

public class UserRequester extends BaseRequest {

	public interface UserSearchParameter extends BaseParameter {

		String Query = "q";
	}

	public interface RelationshipsListParameter extends BaseParameter {

		String ACTION = "action";
	}

	public interface NoteParameter extends BaseParameter {

		String ALIAS = "alias";
	}

	public interface CircleParameter extends BaseParameter {

		String Query = "q";
	}

	public interface BaseParameter {

		String NEXT_MAX_ID = "next_max_id";

		String COUNT = "count";
	}

	public UserRequester(String localRequestId, IHttpListener httpListener) {
		super(localRequestId, httpListener);
	}

	public void findUserGallery(String type, String nextMaxId, int count) {

		path = null;

		if (FindUserGalleryType.NEW_USER.equals(type)) {

			path = FusionConfig.FIND_USER_GALLERY_NEW_URL;

		} else if (FindUserGalleryType.HOT_USER.equals(type)) {

			path = FusionConfig.FIND_USER_GALLERY_HOT_URL;

		} else if (FindUserGalleryType.NEAR_USER.equals(type)) {

			path = FusionConfig.FIND_USER_GALLERY_NEAR_URL;
		}

		method = HttpMethod.GET;

		appendQuery(BaseParameter.NEXT_MAX_ID, nextMaxId);

		appendQuery(BaseParameter.COUNT, count + "");

		start();
	}

	public void getRecommendUserList() {

		path = HttpUtil.buildPath(FusionConfig.USER_RECOMMEND_URL);

		method = HttpMethod.GET;

		start();
	}

	public void getUserListBySearch(String searchName, String nextMaxId,
			int count) {

		path = HttpUtil.buildURL(FusionConfig.USER_SEARCH_URL);

		method = HttpMethod.GET;

		appendQuery(UserSearchParameter.Query, searchName);

		appendQuery(BaseParameter.NEXT_MAX_ID, nextMaxId);

		appendQuery(BaseParameter.COUNT, count + "");

		start();
	}

	public void getMatchContactList(List<Contact> contacts) {

		path = HttpUtil.buildURL(FusionConfig.USER_SEARCH_CONTACT_URL);

		method = HttpMethod.POST;

		setJsonArray(BaseModel.toJsonArray(contacts,
				new String[] { "personId" }).getAsJsonArray());

		start();
	}

	public void getUserByUserId(String userId) {

		path = HttpUtil.buildPath(FusionConfig.USER_GET_BY_ID_URL, userId);

		method = HttpMethod.GET;

		start();
	}

	public void getUserGalleryByUserId(String userId, String nextMaxId,
			int count) {

		path = HttpUtil.buildPath(FusionConfig.USER_GET_GALLERY_BY_ID_URL,
				userId);

		method = HttpMethod.GET;

		appendQuery(BaseParameter.NEXT_MAX_ID, nextMaxId);

		appendQuery(BaseParameter.COUNT, count + "");

		start();
	}

	public void getMBlogListByUserId(String userId, String nextMaxId, int count) {

		path = HttpUtil.buildPath(FusionConfig.USER_GET_MBLOGLIST_BY_ID_URL,
				userId);

		method = HttpMethod.GET;

		appendQuery(BaseParameter.NEXT_MAX_ID, nextMaxId);

		appendQuery(BaseParameter.COUNT, count + "");

		start();
	}

	public void getMBlogListByUserIdAndTag(String userId, MBlogTag tag,
			String nextMaxId, int count) {

		path = HttpUtil.buildPath(FusionConfig.USER_GET_MBLOGLIST_BY_ID_URL,
				userId);

		method = HttpMethod.GET;

		LocationTag location = tag.location;

		TextTag text = tag.text;

		if (null != location) {

			appendQuery(TagBaseParameter.LOCATION_TAG, location.name);

		} else if (null != text) {

			appendQuery(TagBaseParameter.TEXT_TAG, text.name);
		}

		appendQuery(BaseParameter.NEXT_MAX_ID, nextMaxId);

		appendQuery(BaseParameter.COUNT, count + "");

		start();
	}

	public void getMentionedGalleryListByUserId(String userId,
			String nextMaxId, int count) {

		path = HttpUtil.buildPath(FusionConfig.USER_GET_MENTIONED_BY_ID_URL,
				userId);

		method = HttpMethod.GET;

		appendQuery(BaseParameter.NEXT_MAX_ID, nextMaxId);

		appendQuery(BaseParameter.COUNT, count + "");

		start();
	}

	public void getTagThumbnailsListByUserId(String userId, String nextMaxId,
			int count) {

		path = HttpUtil.buildPath(
				FusionConfig.USER_GET_TAGTHUMBNAILS_BY_ID_URL, userId);

		method = HttpMethod.GET;

		appendQuery(BaseParameter.NEXT_MAX_ID, nextMaxId);

		appendQuery(BaseParameter.COUNT, count + "");

		start();
	}

	public void getNoteNameByUserId(String userId) {

		path = HttpUtil.buildPath(FusionConfig.USER_NOTE_OPERATION_BY_ID_URL,
				userId);

		method = HttpMethod.GET;

		start();
	}

	public void updateNoteNameByUserId(String userId, String noteName) {

		path = HttpUtil.buildPath(FusionConfig.USER_NOTE_OPERATION_BY_ID_URL,
				userId);

		method = HttpMethod.PUT;

		putJsonParameter(NoteParameter.ALIAS, noteName);

		start();
	}

	public void getRelationshipsList(String userId, String nextMaxId,
			int count, String action) {

		path = HttpUtil.buildPath(FusionConfig.USER_RELATIONSHIPS_URL, userId);

		method = HttpMethod.GET;

		appendQuery(BaseParameter.NEXT_MAX_ID, nextMaxId);

		appendQuery(BaseParameter.COUNT, count + "");

		appendQuery(RelationshipsListParameter.ACTION, action);

		start();
	}

	public void setUserRelationship(String userId, String action) {

		path = HttpUtil.buildPath(FusionConfig.USER_RELATIONSHIPS_URL, userId);

		method = HttpMethod.POST;

		putJsonParameter(RelationshipsListParameter.ACTION, action);

		start();
	}

	public void getCircleSearch(String search) {

		path = HttpUtil.buildPath(FusionConfig.USER_RELATIONSHIPS_URL,
				FusionConfig.getInstance().getUserId());

		method = HttpMethod.GET;

		appendQuery(CircleParameter.Query, search);

		start();
	}

	public void getCircleSearch(String search, String nextMaxId, int count,
			String action) {

		path = HttpUtil.buildPath(FusionConfig.USER_RELATIONSHIPS_URL,
				FusionConfig.getInstance().getUserId());

		method = HttpMethod.GET;

		appendQuery(CircleParameter.Query, search);

		appendQuery(BaseParameter.NEXT_MAX_ID, nextMaxId);

		appendQuery(BaseParameter.COUNT, count + "");

		appendQuery(RelationshipsListParameter.ACTION, action);

		start();
	}
}
