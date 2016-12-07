package com.theone.sns.logic.mblog.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.text.TextUtils;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.common.FusionCode.MBlogListFollowType;
import com.theone.sns.common.FusionCode.MBlogStatusCode;
import com.theone.sns.common.FusionMessageType.MBlogMessageType;
import com.theone.sns.component.http.IHttpListener;
import com.theone.sns.component.http.Result;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.framework.logic.BaseLogic;
import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.adapter.db.CommentDbAdapter;
import com.theone.sns.logic.adapter.db.FilterDbAdapter;
import com.theone.sns.logic.adapter.db.GalleryDbAdapter;
import com.theone.sns.logic.adapter.db.MBlogDbAdapter;
import com.theone.sns.logic.adapter.db.TagDbAdapter;
import com.theone.sns.logic.adapter.db.UserDbAdapter;
import com.theone.sns.logic.adapter.http.MBlogRequester;
import com.theone.sns.logic.mblog.IMBlogLogic;
import com.theone.sns.logic.model.mblog.Filter;
import com.theone.sns.logic.model.mblog.MBlog;
import com.theone.sns.logic.model.mblog.MBlogTag;
import com.theone.sns.logic.model.mblog.PublishMBlog;
import com.theone.sns.logic.model.mblog.base.Comment;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.model.user.base.Gallery;
import com.theone.sns.util.HttpUtil;
import com.theone.sns.util.StringUtil;

public class MBlogLogicImpl extends BaseLogic implements IMBlogLogic {

	private static final String TAG = "MBlogLogicImpl";

	private Object object = new Object();

	public String getFollowMBlogFromDB() {

		final String requestId = StringUtil.getRequestSerial();

		new Thread(new Runnable() {

			@Override
			public void run() {

				synchronized (object) {

					List<MBlog> mBlogList = MBlogDbAdapter.getInstance()
							.getAllMBlog(MBlogListFollowType.FOLLOWING);

					sendMessage(MBlogMessageType.GET_MBLOG_LIST_FROM_DB,
							new UIObject(requestId, mBlogList));
				}
			}

		}).start();

		return requestId;
	}

	@Override
	public String getFollowMBlogList(final String nextMaxId, int count) {

		String requestId = StringUtil.getRequestSerial();

		if (0 == count) {

			Log.e(TAG, "in method getFollowMBlogList paramter error");

			return requestId;
		}

		if (TextUtils.isEmpty(nextMaxId)) {
			ImageLoader.getInstance().clearMemoryCache();
		}

		new MBlogRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					final List<MBlog> mblogList = new ArrayList<MBlog>();

					List<BaseModel> baseModelList = BaseModel.fromJson(
							result.jsonArray, MBlog.class);

					if (null != baseModelList && !baseModelList.isEmpty()) {

						for (BaseModel baseModel : baseModelList) {

							MBlog mblog = (MBlog) baseModel;

							mblog.mblog_type = MBlogListFollowType.FOLLOWING;

							HttpUtil.handleMBlogUrl(mblog);

							mblogList.add(mblog);
						}
					}

					cacheMblog(nextMaxId, mblogList);

					if (TextUtils.isEmpty(nextMaxId)) {

						sendMessage(
								MBlogMessageType.PULL_GET_MBLOG_LIST_SUCCESS,
								new UIObject(result.localRequestId, mblogList));

					} else {

						sendMessage(
								MBlogMessageType.PUSH_GET_MBLOG_LIST_SUCCESS,
								new UIObject(result.localRequestId, mblogList));
					}

				} else {

					sendEmptyMessage(MBlogMessageType.GET_MBLOG_LIST_FAIL);
				}
			}

		}).getFollowMBlogList(nextMaxId, count);

		return requestId;
	}

	private void cacheMblog(final String nextMaxId, final List<MBlog> mblogList) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				synchronized (object) {

					if (TextUtils.isEmpty(nextMaxId)) {

						deleteAllMBlog();

						List<MBlog> mlist = new ArrayList<MBlog>();

						mlist.addAll(mblogList);

						for (MBlog mblog : mlist) {

							MBlogDbAdapter.getInstance().insertMBlog(mblog);
						}
					}
				}
			}
		}).start();
	}

	private void deleteAllMBlog() {

		List<MBlog> mBlogList = MBlogDbAdapter.getInstance().getAllMBlog(
				MBlogListFollowType.FOLLOWING);

		if (null == mBlogList || mBlogList.isEmpty()) {

			return;
		}

		MBlogDbAdapter.getInstance().deleteAllMBlog(
				MBlogListFollowType.FOLLOWING);

		UserDbAdapter.getInstance().deleteAllUserByType(
				MBlogListFollowType.FOLLOWING);

		for (MBlog mBlog : mBlogList) {

			List<Comment> comments = mBlog.comments;

			if (null != comments && !comments.isEmpty()) {

				for (Comment comment : comments) {

					CommentDbAdapter.getInstance().deleteCommentById(
							comment._id);
				}
			}

			List<MBlogTag> tags = mBlog.tags;

			if (null != tags && !tags.isEmpty()) {

				for (MBlogTag tag : tags) {

					TagDbAdapter.getInstance().deleteTagById(tag.id);
				}
			}
		}
	}

	@Override
	public String publishMBlog(PublishMBlog publishMBlog) {

		String requestId = StringUtil.getRequestSerial();

		if (null == publishMBlog) {

			Log.e(TAG, "in method publishMBlog, publishMBlog paramter error");

			return requestId;
		}

		new MBlogRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					sendEmptyMessage(MBlogMessageType.PUBLISH_MBLOG_SUCCESS);

				} else {

					sendEmptyMessage(MBlogMessageType.PUBLISH_MBLOG_FAIL);
				}
			}

		}).publishMBlog(publishMBlog);

		return requestId;
	}

	@Override
	public Filter getFilterFromDB() {

		return FilterDbAdapter.getInstance().getFilter();
	}

	@Override
	public String updateMBlogFilter(final Filter filter) {

		String requestId = StringUtil.getRequestSerial();

		if (null == filter) {

			Log.e(TAG, "in method updateMBlogFilter, filter paramter error");

			return requestId;
		}

		new MBlogRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					sendEmptyMessage(MBlogMessageType.UPDATE_MBLOG_FILTER_SUCCESS);

					FilterDbAdapter.getInstance().deleteFilter();

					FilterDbAdapter.getInstance().insertFilter(filter);

				} else {

					sendEmptyMessage(MBlogMessageType.UPDATE_MBLOG_FILTER_FAIL);
				}
			}

		}).updateMBlogFilter(filter);

		return requestId;
	}

	@Override
	public String tagToGallery(MBlogTag tag, final String nextMaxId, int count) {

		String requestId = StringUtil.getRequestSerial();

		if (null == tag || (null == tag.location && null == tag.text)
				|| 0 == count) {

			Log.e(TAG, "in method tagToGallery, tag paramter error");

			return requestId;
		}

		new MBlogRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<Gallery> galleryList = new ArrayList<Gallery>();

					List<BaseModel> baseModelList = BaseModel.fromJson(
							result.jsonArray, Gallery.class);

					if (null != baseModelList && !baseModelList.isEmpty()) {

						for (BaseModel baseModel : baseModelList) {

							Gallery gallery = (Gallery) baseModel;

							gallery.url = HttpUtil.addGalleryUrlWH(gallery.url);

							galleryList.add(gallery);
						}
					}

					if (TextUtils.isEmpty(nextMaxId)) {

						sendMessage(
								MBlogMessageType.PULL_TAG_TO_GALLERY_SUCCESS,
								new UIObject(result.localRequestId, galleryList));

					} else {

						sendMessage(
								MBlogMessageType.PUSH_TAG_TO_GALLERY_SUCCESS,
								new UIObject(result.localRequestId, galleryList));
					}

				} else if (MBlogStatusCode.GALLERY_NOT_FOUND == result.statusCode) {

					sendEmptyMessage(MBlogMessageType.TAG_TO_GALLERY_NOT_FOUND);
				}
			}
		}).tagToGallery(tag, nextMaxId, count);

		return requestId;
	}

	public String findMBlogGalleryFromDB(final String type) {

		final String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(type)) {

			return requestId;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				List<Gallery> galleryList = GalleryDbAdapter.getInstance()
						.getAllGallery(type);

				sendMessage(MBlogMessageType.GET_GALLERY_LIST_FROM_DB,
						new UIObject(requestId, galleryList));
			}

		}).start();

		return requestId;
	}

	@Override
	public String findMBlogGallery(final String type, final String nextMaxId,
			int count) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(type) || 0 == count) {

			Log.e(TAG, "in method findMBlogGallery, paramter error");

			return requestId;
		}

		if (TextUtils.isEmpty(nextMaxId)) {
			ImageLoader.getInstance().clearMemoryCache();
		}

		new MBlogRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<Gallery> galleryList = new ArrayList<Gallery>();

					List<BaseModel> baseModelList = BaseModel.fromJson(
							result.jsonArray, Gallery.class);

					if (null != baseModelList && !baseModelList.isEmpty()) {

						if (TextUtils.isEmpty(nextMaxId)) {

							GalleryDbAdapter.getInstance().deleteAllGallery(
									type);
						}

						for (BaseModel baseModel : baseModelList) {

							Gallery gallery = (Gallery) baseModel;

							gallery.url = HttpUtil.addGalleryUrlWH(gallery.url);

							gallery.type = type;

							galleryList.add(gallery);

							if (TextUtils.isEmpty(nextMaxId)) {

								GalleryDbAdapter.getInstance().insertGallery(
										gallery);
							}
						}
					}

					if (TextUtils.isEmpty(nextMaxId)) {

						sendMessage(
								MBlogMessageType.PULL_FIND_GALLERY_SUCCESS,
								new UIObject(result.localRequestId, galleryList));

					} else {

						sendMessage(
								MBlogMessageType.PUSH_FIND_GALLERY_SUCCESS,
								new UIObject(result.localRequestId, galleryList));
					}

				} else if (MBlogStatusCode.GALLERY_NOT_FOUND == result.statusCode) {

					sendEmptyMessage(MBlogMessageType.FIND_GALLERY_NOT_FOUND);
				}
			}

		}).findMBlogGallery(type, nextMaxId, count);

		return requestId;
	}

	@Override
	public String findSearchGallery(String search, final String nextMaxId,
			int count) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(search) || 0 == count) {

			Log.e(TAG, "in method findSearchGallery, paramter error");

			return requestId;
		}

		if (TextUtils.isEmpty(nextMaxId)) {
			ImageLoader.getInstance().clearMemoryCache();
		}

		new MBlogRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<Gallery> galleryList = new ArrayList<Gallery>();

					List<BaseModel> baseModelList = BaseModel.fromJson(
							result.jsonArray, Gallery.class);

					if (null != baseModelList && !baseModelList.isEmpty()) {

						for (BaseModel baseModel : baseModelList) {

							Gallery gallery = (Gallery) baseModel;

							gallery.url = HttpUtil.addGalleryUrlWH(gallery.url);

							galleryList.add(gallery);
						}
					}

					if (TextUtils.isEmpty(nextMaxId)) {

						sendMessage(
								MBlogMessageType.PULL_SEARCH_GALLERY_SUCCESS,
								new UIObject(result.localRequestId, galleryList));

					} else {

						sendMessage(
								MBlogMessageType.PUSH_SEARCH_GALLERY_SUCCESS,
								new UIObject(result.localRequestId, galleryList));
					}

				} else if (MBlogStatusCode.GALLERY_NOT_FOUND == result.statusCode) {

					sendEmptyMessage(MBlogMessageType.SEARCH_GALLERY_NOT_FOUND);
				}
			}
		}).findSearchGallery(search, nextMaxId, count);

		return requestId;
	}

	@Override
	public String findSearchUser(String search, final String nextMaxId,
			int count) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(search) || 0 == count) {

			Log.e(TAG, "in method findSearchUser, paramter error");

			return requestId;
		}

		new MBlogRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<User> userList = new ArrayList<User>();

					List<BaseModel> baseModelList = BaseModel.fromJson(
							result.jsonArray, User.class);

					if (null != baseModelList && !baseModelList.isEmpty()) {

						for (BaseModel baseModel : baseModelList) {

							User user = (User) baseModel;

							user.avatar_url = HttpUtil
									.addUserAvatarUrlWH(user.avatar_url);

							userList.add(user);
						}
					}

					if (TextUtils.isEmpty(nextMaxId)) {

						sendMessage(MBlogMessageType.PULL_SEARCH_USER_SUCCESS,
								new UIObject(result.localRequestId, userList));

					} else {

						sendMessage(MBlogMessageType.PUSH_SEARCH_USER_SUCCESS,
								new UIObject(result.localRequestId, userList));
					}

				} else if (MBlogStatusCode.GALLERY_NOT_FOUND == result.statusCode) {

					sendEmptyMessage(MBlogMessageType.SEARCH_USER_NOT_FOUND);
				}
			}
		}).findSearchUser(search, nextMaxId, count);

		return requestId;
	}

	@Override
	public String searchTags(String search, final String nextMaxId, int count) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(search) || 0 == count) {

			Log.e(TAG, "in method searchTags, paramter error");

			return requestId;
		}

		new MBlogRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<String> resultList = new ArrayList<String>();

					JSONArray resutls = result.jsonArray;

					if (null == resutls) {

						sendEmptyMessage(MBlogMessageType.SEARCH_TAGS_NOT_FOUND);

						return;
					}

					for (int i = 0; i < resutls.length(); i++) {

						try {
							resultList.add(resutls.getString(i));
						} catch (Exception e) {
							Log.e(TAG, "parse error");
						}
					}

					if (TextUtils.isEmpty(nextMaxId)) {

						sendMessage(MBlogMessageType.PULL_SEARCH_TAGS_SUCCESS,
								new UIObject(result.localRequestId, resultList));

					} else {

						sendMessage(MBlogMessageType.PUSH_SEARCH_TAGS_SUCCESS,
								new UIObject(result.localRequestId, resultList));
					}

				} else if (MBlogStatusCode.GALLERY_NOT_FOUND == result.statusCode) {

					sendEmptyMessage(MBlogMessageType.SEARCH_TAGS_NOT_FOUND);
				}
			}
		}).searchTags(search, nextMaxId, count);

		return requestId;
	}

	@Override
	public String getMBlogById(String mblogId) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(mblogId)) {

			Log.e(TAG, "in method getMBlogById, id is null");

			return requestId;
		}

		ImageLoader.getInstance().clearMemoryCache();

		new MBlogRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					MBlog mblog = (MBlog) BaseModel.fromJson(result.jsonObject,
							MBlog.class);

					if (null != mblog) {

						HttpUtil.handleMBlogUrl(mblog);

						sendMessage(MBlogMessageType.GET_MBLOG_BY_ID_SUCCESS,
								new UIObject(result.localRequestId, mblog));

					} else {

						sendEmptyMessage(MBlogMessageType.GET_MBLOG_BY_ID_FAIL);
					}

				} else if (MBlogStatusCode.GET_MBLOG_BY_ID_FAIL == result.statusCode) {

					sendEmptyMessage(MBlogMessageType.GET_MBLOG_BY_ID_FAIL);
				}
			}

		}).getMBlogById(mblogId.trim());

		return requestId;
	}

	@Override
	public String deleteMBlogById(String mblogId) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(mblogId)) {

			Log.e(TAG, "in method deleteMBlogById, id is null");

			return requestId;
		}

		new MBlogRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					sendEmptyMessage(MBlogMessageType.DELETE_MBLOG_BY_ID_SUCCESS);

				} else if (MBlogStatusCode.DELETE_MBLOG_BY_ID_FAIL == result.statusCode) {

					sendEmptyMessage(MBlogMessageType.DELETE_MBLOG_BY_ID_FAIL);
				}
			}

		}).deleteMBlogById(mblogId.trim());

		return requestId;
	}

	@Override
	public String isLikesMBlog(String mblogId, boolean isLike, final MBlog mBlog) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(mblogId)) {

			Log.e(TAG, "in method isLikesMBlog paramter error");

			return requestId;
		}

		new MBlogRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					sendMessage(MBlogMessageType.IS_LIKES_ACTION_SUCCESS,
							new UIObject(result.localRequestId, mBlog));

				} else {

					sendMessage(MBlogMessageType.IS_LIKES_ACTION_FAIL,
							new UIObject(result.localRequestId, mBlog));
				}
			}

		}).isLikesMBlog(mblogId, isLike);

		return requestId;
	}

	@Override
	public String getCommentsListByMBlogId(String mblogId,
			final String nextMaxId, int count) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(mblogId) || 0 == count) {

			Log.e(TAG, "in method getCommentsListByMBlogId paramter error");

			return requestId;
		}

		new MBlogRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<Comment> commentList = new ArrayList<Comment>();

					List<BaseModel> baseModelList = BaseModel.fromJson(
							result.jsonArray, Comment.class);

					if (null != baseModelList && !baseModelList.isEmpty()) {

						for (BaseModel baseModel : baseModelList) {

							Comment comment = (Comment) baseModel;

							if (null != comment.owner) {

								comment.owner.avatar_url = HttpUtil
										.addUserAvatarUrlWH(comment.owner.avatar_url);
							}

							commentList.add(comment);
						}
					}

					if (TextUtils.isEmpty(nextMaxId)) {

						sendMessage(
								MBlogMessageType.PULL_MBLOG_COMMENTS_LIST_SUCCESS,
								new UIObject(result.localRequestId, commentList));

					} else {

						sendMessage(
								MBlogMessageType.PUSH_MBLOG_COMMENTS_LIST_SUCCESS,
								new UIObject(result.localRequestId, commentList));
					}

				} else {

					sendEmptyMessage(MBlogMessageType.GET_MBLOG_COMMENTS_LIST_FAIL);
				}
			}

		}).getCommentsListByMBlogId(mblogId, nextMaxId, count);

		return requestId;
	}

	@Override
	public String publishComment(String mblogId, String targetUserId,
			String text) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(mblogId) || TextUtils.isEmpty(text)) {

			Log.e(TAG, "in method publishComment paramter error");

			return requestId;
		}

		new MBlogRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					Comment comment = new Comment();

					try {

						comment = (Comment) BaseModel.fromJson(
								result.jsonObject, Comment.class);

					} catch (Exception e) {

						Log.e(TAG, "server bug");
					}

					if (null != comment) {

						sendMessage(
								MBlogMessageType.PUBLISH_MBLOG_COMMENTS_SUCCESS,
								new UIObject(result.localRequestId, comment));

					} else {

						sendEmptyMessage(MBlogMessageType.PUBLISH_MBLOG_COMMENTS_FAIL);
					}

				} else {

					sendEmptyMessage(MBlogMessageType.PUBLISH_MBLOG_COMMENTS_FAIL);
				}
			}

		}).publishComment(mblogId, targetUserId, text.trim());

		return requestId;
	}

	@Override
	public String deleteComment(String mblogId, String commentId) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(mblogId) || TextUtils.isEmpty(commentId)) {

			Log.e(TAG, "in method deleteComment paramter error");

			return requestId;
		}

		new MBlogRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					sendEmptyMessage(MBlogMessageType.DELETE_MBLOG_COMMENTS_SUCCESS);

				} else {

					sendEmptyMessage(MBlogMessageType.DELETE_MBLOG_COMMENTS_FAIL);
				}
			}

		}).deleteComment(mblogId, commentId);

		return requestId;
	}
}
