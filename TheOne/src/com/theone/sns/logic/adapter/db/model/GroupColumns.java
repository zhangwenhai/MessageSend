package com.theone.sns.logic.adapter.db.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.theone.sns.common.FusionCode.CommonColumnsValue;
import com.theone.sns.component.database.TheOneDatabaseHelper.Tables;
import com.theone.sns.logic.adapter.db.MessageDbAdapter;
import com.theone.sns.logic.adapter.db.UserDbAdapter;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.util.HttpUtil;
import com.theone.sns.util.PrettyDateFormat;
import com.theone.sns.util.StringUtil;

public class GroupColumns {

	private static final String TAG = "GroupColumns";

	public static final String ID = "_id";

	public static final String GROUPID = "groupId";

	public static final String OWNERID = "ownerId";

	public static final String NAME = "name";

	public static final String MEMBERS = "members";

	public static final String NEW_MESSAGE = "new_message_id";

	public static final String IS_MUTED = "is_muted";

	public static final String IS_JOINED = "is_joined";

	public static final String IS_TOP = "is_top";

	public static final String IS_DELETE = "is_delete";

	public static final String UPDATETIME = "updateTime";

	public static GroupInfo parseCursorToGroup(Cursor cursor) {

		GroupInfo groupInfo = new GroupInfo();

		groupInfo._id = cursor.getString(cursor.getColumnIndex(GROUPID));

		groupInfo.owner = UserDbAdapter.getInstance()
				.getUser(cursor.getString(cursor.getColumnIndex(OWNERID)),
						groupInfo._id);

		groupInfo.name = cursor.getString(cursor.getColumnIndex(NAME));

		groupInfo.members = getMemberList(StringUtil.StringToList(cursor
				.getString(cursor.getColumnIndex(MEMBERS))), groupInfo._id);

		groupInfo.newMessage = getMessage(cursor.getString(cursor
				.getColumnIndex(NEW_MESSAGE)));

		groupInfo.unReadCount = MessageDbAdapter.getInstance()
				.getUnReadMessageCount(groupInfo._id);

		groupInfo.is_muted = cursor.getInt(cursor.getColumnIndex(IS_MUTED)) == CommonColumnsValue.TRUE_VALUE;

		groupInfo.is_joined = cursor.getInt(cursor.getColumnIndex(IS_JOINED)) == CommonColumnsValue.TRUE_VALUE;

		groupInfo.is_top = cursor.getInt(cursor.getColumnIndex(IS_TOP)) == CommonColumnsValue.TRUE_VALUE;

		groupInfo.is_delete = cursor.getInt(cursor.getColumnIndex(IS_DELETE)) == CommonColumnsValue.TRUE_VALUE;

		groupInfo.updateTime = cursor.getString(cursor
				.getColumnIndex(UPDATETIME));

		return groupInfo;
	}

	public static List<String> parseCursorToGroupMember(Cursor cursor) {

		return StringUtil.StringToList(cursor.getString(cursor
				.getColumnIndex(MEMBERS)));
	}

	public static ContentValues setValues(GroupInfo groupInfo) {

		ContentValues cv = new ContentValues();

		cv.put(GROUPID, groupInfo._id);

		putOwner(cv, groupInfo);

		cv.put(NAME, groupInfo.name);

		cv.put(MEMBERS, putMember(groupInfo.members, groupInfo._id));

		if (null != groupInfo.newMessage) {

			cv.put(NEW_MESSAGE, groupInfo.newMessage._id);
		}

		cv.put(IS_MUTED, groupInfo.is_muted ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		cv.put(IS_JOINED, groupInfo.is_joined ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		cv.put(IS_TOP, groupInfo.is_top ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		cv.put(IS_DELETE, groupInfo.is_delete ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		cv.put(UPDATETIME, PrettyDateFormat.getISO8601Time());

		return cv;
	}

	public static ContentValues updateValues(GroupInfo groupInfo) {

		ContentValues cv = new ContentValues();

		cv.put(NAME, groupInfo.name);

		cv.put(MEMBERS, putMember(groupInfo.members, groupInfo._id));

		cv.put(IS_MUTED, groupInfo.is_muted ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		cv.put(IS_JOINED, groupInfo.is_joined ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		return cv;
	}

	public static ContentValues updateMemberValues(GroupInfo groupInfo) {

		ContentValues cv = new ContentValues();

		cv.put(NAME, groupInfo.name);

		cv.put(MEMBERS, putMember(groupInfo.members, groupInfo._id));

		return cv;
	}

	private static MessageInfo getMessage(String messageId) {

		if (TextUtils.isEmpty(messageId)) {

			return null;
		}

		return MessageDbAdapter.getInstance().getMessageById(messageId);
	}

	private static List<User> getMemberList(List<String> idList, String type) {

		List<User> userList = new ArrayList<User>();

		if (null == idList || idList.isEmpty()) {

			return userList;
		}

		for (String id : idList) {

			User user = UserDbAdapter.getInstance().getUser(id, type);

			if (null != user) {

				userList.add(user);
			}
		}

		return userList;
	}

	private static void putOwner(ContentValues cv, GroupInfo groupInfo) {

		User owner = groupInfo.owner;

		if (null != owner) {

			cv.put(OWNERID, owner.userId);

			owner.avatar_url = HttpUtil.addUserAvatarUrlWH(owner.avatar_url);

			UserColumns.insertUserAddType(owner, groupInfo._id);
		}
	}

	private static String putMember(List<User> members, String type) {

		if (null == members || members.isEmpty()) {

			return "";
		}

		List<String> userIdList = new ArrayList<String>();

		for (User user : members) {

			user.avatar_url = HttpUtil.addUserAvatarUrlWH(user.avatar_url);

			UserColumns.insertUserAddType(user, type);

			userIdList.add(user.userId);
		}

		return StringUtil.listToString(userIdList);
	}

	public static void createGroupTable(SQLiteDatabase db) {

		StringBuffer sql = new StringBuffer();

		sql.append(" create table if not exists ").append(Tables.GROUP);
		sql.append(" ( ");

		sql.append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sql.append(GROUPID).append(" TEXT, ");
		sql.append(OWNERID).append(" TEXT, ");
		sql.append(NAME).append(" TEXT, ");
		sql.append(MEMBERS).append(" TEXT, ");
		sql.append(NEW_MESSAGE).append(" TEXT, ");
		sql.append(IS_MUTED).append(" TEXT, ");
		sql.append(IS_JOINED).append(" TEXT, ");
		sql.append(IS_TOP).append(" TEXT, ");
		sql.append(IS_DELETE).append(" TEXT, ");
		sql.append(UPDATETIME).append(" TEXT ");

		sql.append(" );");

		db.execSQL(sql.toString());

		Log.d(TAG, "create " + Tables.GROUP + " success!");
	}
}