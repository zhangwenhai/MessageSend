package com.theone.sns.logic.adapter.http;

import com.google.gson.JsonObject;
import com.theone.sns.common.FusionCode.FindMBlogGalleryType;
import com.theone.sns.common.FusionCode.LikesAction;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.component.http.BaseRequest;
import com.theone.sns.component.http.IHttpListener;
import com.theone.sns.logic.model.mblog.Filter;
import com.theone.sns.logic.model.mblog.MBlogTag;
import com.theone.sns.logic.model.mblog.PublishMBlog;
import com.theone.sns.logic.model.mblog.base.LocationTag;
import com.theone.sns.logic.model.mblog.base.TextTag;
import com.theone.sns.util.HttpUtil;

public class MBlogRequester extends BaseRequest {

	public interface LikesParameter extends BaseParameter {

		String ACTION = "action";
	}

	public interface CommentsParameter extends BaseParameter {

		String TARGET_USER = "target_user";

		String TEXT = "text";
	}

	public interface FindSearchParameter extends BaseParameter {

		String type = "type";

		String Q = "q";
	}

	public MBlogRequester(String localRequestId, IHttpListener httpListener) {

		super(localRequestId, httpListener);
	}

	public void getFollowMBlogList(String nextMaxId, int count) {

		path = HttpUtil.buildURL(FusionConfig.MBLOG_LIST_FOLLOWING_URL);

		method = HttpMethod.GET;

		appendQuery(BaseParameter.NEXT_MAX_ID, nextMaxId);

		appendQuery(BaseParameter.COUNT, count + "");

		start();
	}

	public void publishMBlog(PublishMBlog publishMBlog) {

		path = FusionConfig.MBLOG_OPERATION_URL;

		method = HttpMethod.POST;

		setJsonObject((JsonObject) publishMBlog.toJson());

		start();
	}

	public void updateMBlogFilter(Filter filter) {

		path = FusionConfig.MBLOG_FILTER_URL;

		method = HttpMethod.PUT;

		setJsonObject((JsonObject) filter.toJson());

		start();
	}

	public void tagToGallery(MBlogTag tag, String nextMaxId, int count) {

		path = FusionConfig.MBLOG_TAG_TO_GALLERY_URL;

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

	public void findMBlogGallery(String type, String nextMaxId, int count) {

		path = null;

		if (FindMBlogGalleryType.NEW_MBLOG.equals(type)) {

			path = FusionConfig.FIND_MBLOG_GALLERY_NEW_URL;

		} else if (FindMBlogGalleryType.HOT_MBLOG.equals(type)) {

			path = FusionConfig.FIND_MBLOG_GALLERY_HOT_URL;

		} else if (FindMBlogGalleryType.NEAR_MBLOG.equals(type)) {

			path = FusionConfig.FIND_MBLOG_GALLERY_NEAR_URL;
		}

		method = HttpMethod.GET;

		appendQuery(BaseParameter.NEXT_MAX_ID, nextMaxId);

		appendQuery(BaseParameter.COUNT, count + "");

		start();
	}

	public void findSearchGallery(String search, String nextMaxId, int count) {

		path = FusionConfig.FIND_SEARCH_GALLERY_URL;

		method = HttpMethod.GET;

		appendQuery(FindSearchParameter.Q, search);

		appendQuery(BaseParameter.NEXT_MAX_ID, nextMaxId);

		appendQuery(BaseParameter.COUNT, count + "");

		start();
	}

	public void findSearchUser(String search, String nextMaxId, int count) {

		path = FusionConfig.FIND_SEARCH_USER_URL;

		method = HttpMethod.GET;

		appendQuery(FindSearchParameter.Q, search);

		appendQuery(BaseParameter.NEXT_MAX_ID, nextMaxId);

		appendQuery(BaseParameter.COUNT, count + "");

		start();
	}

	public void searchTags(String search, String nextMaxId, int count) {

		path = FusionConfig.SEARCH_TAGS_URL;

		method = HttpMethod.GET;

		appendQuery(FindSearchParameter.Q, search);

		appendQuery(BaseParameter.NEXT_MAX_ID, nextMaxId);

		appendQuery(BaseParameter.COUNT, count + "");

		start();
	}

	public void getMBlogById(String mblogId) {

		path = HttpUtil.buildPath(FusionConfig.MBLOG_ID_OPERATION_URL, mblogId);

		method = HttpMethod.GET;

		start();
	}

	public void deleteMBlogById(String mblogId) {

		path = HttpUtil.buildPath(FusionConfig.MBLOG_ID_OPERATION_URL, mblogId);

		method = HttpMethod.DELETE;

		start();
	}

	public void getLikesListByMBlogId(String mblogId, String nextMaxId,
			int count) {

		path = HttpUtil.buildPath(FusionConfig.MBLOG_LIKES_OPERATION_URL,
				mblogId);

		method = HttpMethod.GET;

		appendQuery(BaseParameter.NEXT_MAX_ID, nextMaxId);

		appendQuery(BaseParameter.COUNT, count + "");

		start();
	}

	public void isLikesMBlog(String mblogId, boolean isLike) {

		path = HttpUtil.buildPath(FusionConfig.MBLOG_LIKES_OPERATION_URL,
				mblogId);

		method = HttpMethod.POST;

		putJsonParameter(LikesParameter.ACTION, (isLike ? LikesAction.LIKE
				: LikesAction.UNLIKE));

		start();
	}

	public void getCommentsListByMBlogId(String mblogId, String nextMaxId,
			int count) {

		path = HttpUtil.buildPath(FusionConfig.MBLOG_COMMENTS_OPERATION_URL,
				mblogId);

		method = HttpMethod.GET;

		appendQuery(BaseParameter.NEXT_MAX_ID, nextMaxId);

		appendQuery(BaseParameter.COUNT, count + "");

		start();
	}

	public void publishComment(String mblogId, String targetUserId, String text) {

		path = HttpUtil.buildPath(FusionConfig.MBLOG_COMMENTS_OPERATION_URL,
				mblogId);

		method = HttpMethod.POST;

		putJsonParameter(CommentsParameter.TARGET_USER, targetUserId);

		putJsonParameter(CommentsParameter.TEXT, text);

		start();
	}

	public void deleteComment(String mblogId, String commentId) {

		path = HttpUtil.buildPath(FusionConfig.MBLOG_DELETE_COMMENTS_URL,
				new Object[] { mblogId, commentId });

		method = HttpMethod.DELETE;

		start();
	}
}
