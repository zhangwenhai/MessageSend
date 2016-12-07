/**
 *
 */
package com.theone.sns.util;

import gumi.builders.UrlBuilder;

import java.util.HashMap;
import java.util.List;

import android.text.TextUtils;

import com.theone.sns.TheOneApp;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.logic.model.mblog.MBlog;
import com.theone.sns.logic.model.mblog.base.Comment;
import com.theone.sns.logic.model.user.User;

public class HttpUtil {

	private static final String IMAGEWH_SCHEME = "?imageView2/1";

	private static int mGalleryWidth = -1;

	private static int mUserAvatarWidth = -1;

	private static String getDownloadImageWHUrlParameter(int width, int height) {
		return IMAGEWH_SCHEME + "/w/" + width + "/h/" + height;
	}

	private static boolean containImageWH(String url) {

		boolean isContain = false;

		if (TextUtils.isEmpty(url)) {

			return isContain;
		}

		if (url.indexOf(IMAGEWH_SCHEME) > 0) {

			isContain = true;
		}

		return isContain;
	}

	public static String removeUrlWH(String url) {

		String orgUrl = "";

		if (TextUtils.isEmpty(url)) {

			return orgUrl;
		}

		int index = url.indexOf(IMAGEWH_SCHEME);

		if (index > 0) {
			orgUrl = url.substring(0, index);
		} else {
			orgUrl = url;
		}

		return orgUrl;
	}

	public static String addGalleryUrlWH(String url) {

		if (TextUtils.isEmpty(url) || containImageWH(url)) {

			return url;
		}

		if (-1 == mGalleryWidth) {

			mGalleryWidth = (TheOneApp.getContext().getResources()
					.getDisplayMetrics().widthPixels) / 5;
		}

		return url
				+ getDownloadImageWHUrlParameter(mGalleryWidth, mGalleryWidth);
	}

	public static String addGalleryUrlWH(String url, int w, int h) {

		if (TextUtils.isEmpty(url) || containImageWH(url)) {

			return url;
		}

		return url + getDownloadImageWHUrlParameter(w, h);
	}

	public static String addUserAvatarUrlWH(String url) {

		if (TextUtils.isEmpty(url) || containImageWH(url)) {

			return url;
		}

		if (-1 == mUserAvatarWidth) {

			mUserAvatarWidth = (TheOneApp.getContext().getResources()
					.getDisplayMetrics().widthPixels) / 8;
		}

		return url
				+ getDownloadImageWHUrlParameter(mUserAvatarWidth,
						mUserAvatarWidth);
	}

	public static String addMBlogUrlWH(int w, int h) {

		if (w <= 0 || h <= 0 || w < TheOneApp.getDeviceWidth()) {

			return "";
		}

		float scale = (float) h / (float) w;

		return getDownloadImageWHUrlParameter(TheOneApp.getDeviceWidth(),
				(int) (TheOneApp.getDeviceWidth() * scale));
	}

	public static void handleMBlogUrl(MBlog mblog) {

		if (null == mblog) {
			return;
		}

		mblog.owner.avatar_url = addUserAvatarUrlWH(mblog.owner.avatar_url);

		if (null != mblog.photo) {

			mblog.photo.url += addMBlogUrlWH(mblog.photo.w, mblog.photo.h);
		}

		List<User> likeUsers = mblog.likes;

		if (null != likeUsers && likeUsers.size() > 0) {

			for (User user : likeUsers) {

				user.avatar_url = addUserAvatarUrlWH(user.avatar_url);
			}
		}

		List<Comment> comments = mblog.comments;

		if (null != comments && comments.size() > 0) {

			for (Comment comment : comments) {

				User owner = comment.owner;

				if (null != owner) {

					owner.avatar_url = addUserAvatarUrlWH(owner.avatar_url);
				}
			}
		}
	}

	public static final String buildURL(String path) {

		UrlBuilder builder = UrlBuilder.fromString(path);
		if (null != builder.scheme && null != builder.hostName
				&& null != builder.path) {
			return path;
		}

		builder = UrlBuilder.empty().withScheme(FusionConfig.THEONE_SCHEME)
				.withHost(FusionConfig.THEONE_HOST).withPath(path);

		return builder.toString();
	}

	public static final String buildUrl(String scheme, String host, String path) {
		UrlBuilder builder = UrlBuilder.empty().withScheme(scheme)
				.withHost(host).withPath(path);

		return builder.toString();
	}

	public static final String buildPath(String pathFormat, Object... args) {
		return String.format(pathFormat, args);
	}

	public static final String appendQuery(String url,
			HashMap<String, String> query) {
		UrlBuilder builder = UrlBuilder.fromString(url);

		if (null != query) {
			for (String key : query.keySet()) {
				builder = UrlBuilder.fromString(builder.toString())
						.addParameter(key, query.get(key));
			}
		}

		return builder.toString();
	}

	public static boolean checkStatusCode(int statusCode) {

		if (200 == statusCode || 201 == statusCode || 204 == statusCode) {
			return true;
		}

		return false;
	}
}
