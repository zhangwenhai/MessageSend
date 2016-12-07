package com.theone.sns.logic.user.impl;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.common.FusionCode.MatchContactType;
import com.theone.sns.common.FusionMessageType.MBlogMessageType;
import com.theone.sns.common.FusionMessageType.UserMessageType;
import com.theone.sns.component.contact.Contact;
import com.theone.sns.component.http.IHttpListener;
import com.theone.sns.component.http.Result;
import com.theone.sns.component.http.UIObject;
import com.theone.sns.framework.logic.BaseLogic;
import com.theone.sns.framework.model.BaseModel;
import com.theone.sns.logic.adapter.db.UserDbAdapter;
import com.theone.sns.logic.adapter.http.MBlogRequester;
import com.theone.sns.logic.adapter.http.UserRequester;
import com.theone.sns.logic.model.mblog.MBlog;
import com.theone.sns.logic.model.mblog.MBlogTag;
import com.theone.sns.logic.model.user.MatchContacts;
import com.theone.sns.logic.model.user.MatchContactsResult;
import com.theone.sns.logic.model.user.Mentioned;
import com.theone.sns.logic.model.user.Note;
import com.theone.sns.logic.model.user.TagThumbnail;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.logic.model.user.base.Gallery;
import com.theone.sns.logic.model.user.base.RegisteredUser;
import com.theone.sns.logic.user.IUserLogic;
import com.theone.sns.util.HttpUtil;
import com.theone.sns.util.StringUtil;

public class UserLogicImpl extends BaseLogic implements IUserLogic {

	private static final String TAG = "UserLogicImpl";

	public String findUserGalleryFromDB(final String type) {

		final String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(type)) {

			Log.e(TAG, "getUserListFromDB error, type is null");

			return requestId;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				List<User> userList = UserDbAdapter.getInstance().getAllUser(
						type);

				sendMessage(UserMessageType.GET_USER_LIST_FROM_DB,
						new UIObject(requestId, userList));
			}

		}).start();

		return requestId;
	}

	@Override
	public String findUserGallery(final String type, final String nextMaxId,
			int count) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(type) || 0 == count) {

			Log.e(TAG, "in method findUserGallery paramter error");

			return requestId;
		}

		if (TextUtils.isEmpty(nextMaxId)) {
			ImageLoader.getInstance().clearMemoryCache();
		}

		new UserRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<User> userList = new ArrayList<User>();

					List<BaseModel> baseModelList = BaseModel.fromJson(
							result.jsonArray, User.class);

					if (null != baseModelList && !baseModelList.isEmpty()) {

						if (TextUtils.isEmpty(nextMaxId)) {

							UserDbAdapter.getInstance().deleteAllUserByType(
									type);
						}

						for (BaseModel baseModel : baseModelList) {

							User user = (User) baseModel;

							user.type = type;

							user.avatar_url = HttpUtil
									.addGalleryUrlWH(user.avatar_url);

							userList.add(user);

							if (TextUtils.isEmpty(nextMaxId)) {

								UserDbAdapter.getInstance().insertUser(user);
							}
						}
					}

					if (TextUtils.isEmpty(nextMaxId)) {

						sendMessage(UserMessageType.PULL_USER_LIST_SUCCESS,
								new UIObject(result.localRequestId, userList));

					} else {

						sendMessage(UserMessageType.PUSH_USER_LIST_SUCCESS,
								new UIObject(result.localRequestId, userList));
					}
				} else {

					sendEmptyMessage(UserMessageType.GET_USER_LIST_FAIL);
				}
			}

		}).findUserGallery(type, nextMaxId, count);

		return requestId;
	}

	@Override
	public String getRecommendUserList() {

		String requestId = StringUtil.getRequestSerial();

		new UserRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<User> mUserList = new ArrayList<User>();

					List<BaseModel> modelList = BaseModel.fromJson(
							result.jsonArray, User.class);

					if (null != modelList && !modelList.isEmpty()) {

						for (BaseModel baseModel : modelList) {

							User user = (User) baseModel;

							user.avatar_url = HttpUtil
									.addUserAvatarUrlWH(user.avatar_url);

							mUserList.add(user);
						}
					}

					sendMessage(
							UserMessageType.GET_RECOMMEND_USER_LIST_SUCCESS,
							new UIObject(result.localRequestId, mUserList));

				} else {

					sendEmptyMessage(UserMessageType.GET_RECOMMEND_USER_LIST_FAIL);
				}
			}

		}).getRecommendUserList();

		return requestId;
	}

	@Override
	public String getUserListBySearch(String searchName,
			final String nextMaxId, int count) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(searchName) || 0 == count) {

			Log.e(TAG, "in method getUserListBySearch paramter error");

			return requestId;
		}

		new UserRequester(requestId, new IHttpListener() {

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

						sendMessage(
								UserMessageType.PULL_SEARCH_USER_LIST_SUCCESS,
								new UIObject(result.localRequestId, userList));

					} else {

						sendMessage(
								UserMessageType.PUSH_SEARCH_USER_LIST_SUCCESS,
								new UIObject(result.localRequestId, userList));
					}

				} else {

					sendEmptyMessage(UserMessageType.GET_SEARCH_USER_LIST_FAIL);
				}
			}

		}).getUserListBySearch(searchName.trim(), nextMaxId, count);

		return requestId;
	}

	@Override
	public String getMatchContactList(List<Contact> contacts) {

		String requestId = StringUtil.getRequestSerial();

		if (null == contacts || contacts.isEmpty()) {

			Log.e(TAG, "in method getMatchContactList paramter error");

			return requestId;
		}

		new UserRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<MatchContactsResult> matchContactsResultList = new ArrayList<MatchContactsResult>();

					MatchContacts matchContacts = (MatchContacts) BaseModel
							.fromJson(result.jsonObject, MatchContacts.class);

					if (null != matchContacts) {

						List<RegisteredUser> registeredList = matchContacts.registered;

						List<Contact> unregisteredList = matchContacts.unregistered;

						if (null != registeredList && registeredList.size() > 0) {

							MatchContactsResult matchContactsResult = new MatchContactsResult();

							matchContactsResult.type = MatchContactType.SEPARATORS;

							matchContactsResult.Separators = mContext
									.getString(R.string.registered_friend);

							matchContactsResultList.add(matchContactsResult);

							for (RegisteredUser user : registeredList) {

								MatchContactsResult registeredResult = new MatchContactsResult();

								registeredResult.type = MatchContactType.USER;

								registeredResult.registeredUser = user;

								matchContactsResultList.add(registeredResult);
							}
						}

						if (null != unregisteredList
								&& unregisteredList.size() > 0) {

							MatchContactsResult matchContactsResult = new MatchContactsResult();

							matchContactsResult.type = MatchContactType.SEPARATORS;

							matchContactsResult.Separators = mContext
									.getString(R.string.unregistered_friend);

							matchContactsResultList.add(matchContactsResult);

							for (Contact contact : unregisteredList) {

								MatchContactsResult unregisteredResult = new MatchContactsResult();

								unregisteredResult.type = MatchContactType.CONTACT;

								unregisteredResult.contact = contact;

								matchContactsResultList.add(unregisteredResult);
							}
						}
					}

					sendMessage(UserMessageType.GET_MATCH_CONTACT_LIST_SUCCESS,
							new UIObject(result.localRequestId,
									matchContactsResultList));
				} else {

					sendEmptyMessage(UserMessageType.GET_MATCH_CONTACT_LIST_FAIL);
				}
			}

		}).getMatchContactList(contacts);

		return requestId;
	}

	@Override
	public String getUserByUserId(String userId) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(userId)) {

			Log.e(TAG, "in method getUserById paramter error");

			return requestId;
		}

		new UserRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					BaseModel mBaseModel = BaseModel.fromJson(
							result.jsonObject, User.class);

					User user = (User) mBaseModel;

					if (null != user) {

						user.avatar_url = HttpUtil
								.addUserAvatarUrlWH(user.avatar_url);

						sendMessage(UserMessageType.GET_USER_BY_ID_SUCCESS,
								new UIObject(result.localRequestId, user));

					} else {

						sendEmptyMessage(UserMessageType.GET_USER_BY_ID_FAIL);
					}

				} else {

					sendEmptyMessage(UserMessageType.GET_USER_BY_ID_FAIL);
				}
			}

		}).getUserByUserId(userId);

		return requestId;
	}

	@Override
	public String getUserGalleryByUserId(String userId, final String nextMaxId,
			int count) {

		final String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(userId) || 0 == count) {

			Log.e(TAG, "in method getUserGalleryById paramter error");

			return requestId;
		}

		new UserRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<Gallery> mGalleryList = new ArrayList<Gallery>();

					List<BaseModel> modelList = BaseModel.fromJson(
							result.jsonArray, Gallery.class);

					if (null != modelList && !modelList.isEmpty()) {

						for (BaseModel baseModel : modelList) {

							Gallery gallery = (Gallery) baseModel;

							gallery.url = HttpUtil.addGalleryUrlWH(gallery.url);

							mGalleryList.add(gallery);
						}
					}

					if (TextUtils.isEmpty(nextMaxId)) {

						sendMessage(
								UserMessageType.PULL_GALLERY_LIST_BY_ID_SUCCESS,
								new UIObject(result.localRequestId,
										mGalleryList));

					} else {

						sendMessage(
								UserMessageType.PUSH_GALLERY_LIST_BY_ID_SUCCESS,
								new UIObject(result.localRequestId,
										mGalleryList));
					}

				} else {

					sendEmptyMessage(UserMessageType.GET_GALLERY_LIST_BY_ID_FAIL);
				}
			}

		}).getUserGalleryByUserId(userId, nextMaxId, count);

		return requestId;
	}

	@Override
	public String getMBlogListByUserId(String userId, final String nextMaxId,
			int count) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(userId) || 0 == count) {

			Log.e(TAG, "in method getMBlogListById paramter error");

			return requestId;
		}

		new UserRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<MBlog> mBlogList = new ArrayList<MBlog>();

					List<BaseModel> modelList = BaseModel.fromJson(
							result.jsonArray, MBlog.class);

					if (null != modelList && !modelList.isEmpty()) {

						for (BaseModel baseModel : modelList) {

							MBlog mblog = (MBlog) baseModel;

							HttpUtil.handleMBlogUrl(mblog);

							mBlogList.add(mblog);
						}
					}

					if (TextUtils.isEmpty(nextMaxId)) {

						sendMessage(
								UserMessageType.PULL_MBLOG_LIST_BY_ID_SUCCESS,
								new UIObject(result.localRequestId, mBlogList));

					} else {

						sendMessage(
								UserMessageType.PUSH_MBLOG_LIST_BY_ID_SUCCESS,
								new UIObject(result.localRequestId, mBlogList));
					}

				} else {

					sendEmptyMessage(UserMessageType.PUSH_MBLOG_LIST_BY_ID_FAIL);
				}
			}

		}).getMBlogListByUserId(userId, nextMaxId, count);

		return requestId;
	}

	@Override
	public String getMBlogListByUserIdAndTag(String userId, MBlogTag tag,
			final String nextMaxId, int count) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(userId) || 0 == count) {

			Log.e(TAG, "in method getMBlogListByUserIdAndTag paramter error");

			return requestId;
		}

		new UserRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<MBlog> mBlogList = new ArrayList<MBlog>();

					List<BaseModel> modelList = BaseModel.fromJson(
							result.jsonArray, MBlog.class);

					if (null != modelList && !modelList.isEmpty()) {

						for (BaseModel baseModel : modelList) {

							MBlog mMBlog = (MBlog) baseModel;

							HttpUtil.handleMBlogUrl(mMBlog);

							mBlogList.add(mMBlog);
						}
					}

					if (TextUtils.isEmpty(nextMaxId)) {

						sendMessage(
								UserMessageType.PULL_MBLOG_LIST_BY_ID_AND_TAG_SUCCESS,
								new UIObject(result.localRequestId, mBlogList));

					} else {

						sendMessage(
								UserMessageType.PUSH_MBLOG_LIST_BY_ID_AND_TAG_SUCCESS,
								new UIObject(result.localRequestId, mBlogList));
					}

				} else {

					sendEmptyMessage(UserMessageType.GET_MBLOG_LIST_BY_ID__AND_TAG_FAIL);
				}
			}

		}).getMBlogListByUserIdAndTag(userId, tag, nextMaxId, count);

		return requestId;
	}

	@Override
	public String getMentionedGalleryListByUserId(String userId,
			final String nextMaxId, int count) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(userId) || 0 == count) {

			Log.e(TAG, "in method getMentionedGalleryListById paramter error");

			return requestId;
		}

		new UserRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<Mentioned> mentionedList = new ArrayList<Mentioned>();

					List<BaseModel> modelList = BaseModel.fromJson(
							result.jsonArray, Mentioned.class);

					if (null != modelList && !modelList.isEmpty()) {

						for (BaseModel baseModel : modelList) {

							Mentioned mentioned = (Mentioned) baseModel;

							List<Gallery> galleryList = mentioned.media;

							if (galleryList != null && galleryList.size() > 0) {

								for (Gallery gallery : galleryList) {

									gallery.url = HttpUtil
											.addGalleryUrlWH(gallery.url);
								}
							}

							mentionedList.add(mentioned);
						}
					}

					if (TextUtils.isEmpty(nextMaxId)) {

						sendMessage(
								UserMessageType.PULL_MENTIONEDGALLERY_LIST_BY_ID_SUCCESS,
								new UIObject(result.localRequestId,
										mentionedList));

					} else {

						sendMessage(
								UserMessageType.PUSH_MENTIONEDGALLERY_LIST_BY_ID_SUCCESS,
								new UIObject(result.localRequestId,
										mentionedList));
					}

				} else {

					sendEmptyMessage(UserMessageType.GET_MENTIONEDGALLERY_LIST_BY_ID_FAIL);
				}
			}

		}).getMentionedGalleryListByUserId(userId, nextMaxId, count);

		return requestId;
	}

	@Override
	public String getTagThumbnailsListByUserId(String userId,
			final String nextMaxId, int count) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(userId) || 0 == count) {

			Log.e(TAG, "in method getTagThumbnailsListById paramter error");

			return requestId;
		}

		new UserRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<TagThumbnail> mTagThumbnailList = new ArrayList<TagThumbnail>();

					List<BaseModel> modelList = BaseModel.fromJson(
							result.jsonArray, TagThumbnail.class);

					if (null != modelList && !modelList.isEmpty()) {

						for (BaseModel baseModel : modelList) {

							TagThumbnail mTagThumbnail = (TagThumbnail) baseModel;

							mTagThumbnailList.add(mTagThumbnail);
						}
					}

					if (TextUtils.isEmpty(nextMaxId)) {

						sendMessage(
								UserMessageType.PULL_TAGTHUMBNAILS_LIST_BY_ID_SUCCESS,
								new UIObject(result.localRequestId,
										mTagThumbnailList));

					} else {

						sendMessage(
								UserMessageType.PUSH_TAGTHUMBNAILS_LIST_BY_ID_SUCCESS,
								new UIObject(result.localRequestId,
										mTagThumbnailList));
					}

				} else {

					sendEmptyMessage(UserMessageType.GET_TAGTHUMBNAILS_LIST_BY_ID_FAIL);
				}
			}

		}).getTagThumbnailsListByUserId(userId, nextMaxId, count);

		return requestId;
	}

	@Override
	public String getAliasByUserId(String userId) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(userId)) {

			Log.e(TAG, "in method getAliasByUserId paramter error");

			return requestId;
		}

		new UserRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					sendMessage(
							UserMessageType.GET_ALIAS_BY_ID_SUCCESS,
							new UIObject(result.localRequestId, BaseModel
									.fromJson(result.jsonObject, Note.class)));

				} else {

					sendEmptyMessage(UserMessageType.GET_NOTENAME_BY_ID_FAIL);
				}
			}

		}).getNoteNameByUserId(userId);

		return requestId;
	}

	@Override
	public String updateAliasByUserId(String userId, String alias) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(userId)) {

			Log.e(TAG, "in method updateAliasByUserId paramter error");

			return requestId;
		}

		new UserRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					sendEmptyMessage(UserMessageType.UPDATE_ALIAS_BY_ID_SUCCESS);

				} else {

					sendEmptyMessage(UserMessageType.UPDATE_ALIAS_BY_ID_FAIL);
				}
			}

		}).updateNoteNameByUserId(userId, alias);

		return requestId;
	}

	@Override
	public String getRelationshipsList(String userId, final String nextMaxId,
			int count, String action) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(action)
				|| 0 == count) {

			Log.e(TAG, "in method getRelationshipsList paramter error");

			return requestId;
		}

		new UserRequester(requestId, new IHttpListener() {

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

						sendMessage(
								UserMessageType.PULL_RELATIONSHIPS_LIST_BY_ID_SUCCESS,
								new UIObject(result.localRequestId, userList));

					} else {

						sendMessage(
								UserMessageType.PUSH_RELATIONSHIPS_LIST_BY_ID_SUCCESS,
								new UIObject(result.localRequestId, userList));
					}

				} else {

					sendEmptyMessage(UserMessageType.GET_RELATIONSHIPS_LIST_BY_ID_FAIL);
				}
			}

		}).getRelationshipsList(userId, nextMaxId, count, action);

		return requestId;
	}

	@Override
	public String getLikesListByMBlogId(String mblogId, final String nextMaxId,
			int count) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(mblogId) || 0 == count) {

			Log.e(TAG, "in method getLikesListByMBlogId paramter error");

			return requestId;
		}

		new MBlogRequester(requestId, new IHttpListener() {

			@Override
			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					List<User> likeUserList = new ArrayList<User>();

					List<BaseModel> baseModelList = BaseModel.fromJson(
							result.jsonArray, User.class);

					if (null != baseModelList && !baseModelList.isEmpty()) {

						for (BaseModel baseModel : baseModelList) {

							User user = (User) baseModel;

							user.avatar_url = HttpUtil
									.addUserAvatarUrlWH(user.avatar_url);

							likeUserList.add(user);
						}
					}

					if (TextUtils.isEmpty(nextMaxId)) {

						sendMessage(
								MBlogMessageType.PULL_GET_LIKES_LIST_SUCCESS,
								new UIObject(result.localRequestId,
										likeUserList));

					} else {

						sendMessage(
								MBlogMessageType.PUSH_GET_LIKES_LIST_SUCCESS,
								new UIObject(result.localRequestId,
										likeUserList));
					}

				} else {

					sendEmptyMessage(MBlogMessageType.GET_LIKES_LIST_FAIL);
				}
			}

		}).getLikesListByMBlogId(mblogId, nextMaxId, count);

		return requestId;
	}

	@Override
	public String setUserRelationship(String userId, final String action) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(action)) {

			Log.e(TAG, "in method setUserRelationship paramter error");

			return requestId;
		}

		new UserRequester(requestId, new IHttpListener() {

			public void onResult(Result result) {

				if (HttpUtil.checkStatusCode(result.statusCode)) {

					sendMessage(UserMessageType.SET_RELATIONSHIP_BY_ID_SUCCESS,
							new UIObject(result.localRequestId, action));

				} else {

					sendEmptyMessage(UserMessageType.SET_RELATIONSHIP_BY_ID_FAIL);
				}
			}

		}).setUserRelationship(userId, action);

		return requestId;
	}

	@Override
	public String getCircleSearch(String search) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(search)) {

			Log.e(TAG, "in method getCircleSearch paramter error");

			return requestId;
		}

		new UserRequester(requestId, new IHttpListener() {

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

					sendMessage(UserMessageType.CIRCLE_SEARCH_SUCCESS,
							new UIObject(result.localRequestId, userList));
				} else {

					sendEmptyMessage(UserMessageType.CIRCLE_SEARCH_FAIL);
				}
			}

		}).getCircleSearch(search);

		return requestId;
	}

	@Override
	public String getCircleSearch(String search, final String nextMaxId,
			int count, String action) {

		String requestId = StringUtil.getRequestSerial();

		if (TextUtils.isEmpty(search)) {

			Log.e(TAG, "in method getCircleSearch paramter error");

			return requestId;
		}

		new UserRequester(requestId, new IHttpListener() {

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

						sendMessage(UserMessageType.PULL_CIRCLE_SEARCH_SUCCESS,
								new UIObject(result.localRequestId, userList));

					} else {

						sendMessage(UserMessageType.PUSH_CIRCLE_SEARCH_SUCCESS,
								new UIObject(result.localRequestId, userList));
					}

				} else {

					sendEmptyMessage(UserMessageType.CIRCLE_SEARCH_FAIL);
				}
			}

		}).getCircleSearch(search, nextMaxId, count, action);

		return requestId;
	}
}
