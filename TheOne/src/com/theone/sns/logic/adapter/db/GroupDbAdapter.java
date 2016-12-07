package com.theone.sns.logic.adapter.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.theone.sns.TheOneApp;
import com.theone.sns.common.FusionCode.CommonColumnsValue;
import com.theone.sns.component.database.TheOneDatabaseHelper;
import com.theone.sns.component.database.URIField;
import com.theone.sns.logic.adapter.db.model.GroupColumns;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.chat.MessageInfo;
import com.theone.sns.util.PrettyDateFormat;

public class GroupDbAdapter {

	private static final String TAG = "GroupDbAdapter";

	private static GroupDbAdapter sInstance;

	private ContentResolver mCr;

	private GroupDbAdapter() {

		mCr = TheOneApp.getResolver();
	}

	public static synchronized GroupDbAdapter getInstance() {

		if (null == sInstance) {

			sInstance = new GroupDbAdapter();
		}

		return sInstance;
	}

	public long insertGroup(GroupInfo groupInfo) {

		long result = -1;

		if (null == groupInfo) {

			Log.e(TAG, "insertGroup fail, groupInfo is null");

			return result;
		}

		Uri uri = URIField.GROUP_URI;

		if (null != getGroupById(groupInfo._id)) {

			return result;
		}

		ContentValues cv = GroupColumns.setValues(groupInfo);

		Uri resultUri = mCr.insert(uri, cv);

		if (null != resultUri) {

			result = ContentUris.parseId(resultUri);
		}

		return result;
	}

	public List<GroupInfo> getAllGroup() {

		List<GroupInfo> groupList = new ArrayList<GroupInfo>();

		Cursor cursor = null;

		Uri uri = URIField.GROUP_URI;

		try {

			cursor = mCr.query(uri, null, null, null, null);

			while (cursor.moveToNext()) {

				GroupInfo groupInfo = GroupColumns.parseCursorToGroup(cursor);

				if (null != groupInfo) {

					groupList.add(groupInfo);
				}
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return groupList;
	}

	public List<GroupInfo> getAllGroupIsJoined(boolean isJoined) {

		List<GroupInfo> groupList = new ArrayList<GroupInfo>();

		Cursor cursor = null;

		Uri uri = URIField.GROUP_URI;

		try {

			cursor = mCr.query(uri, null, GroupColumns.IS_JOINED + "=?",
					new String[] { isJoined ? CommonColumnsValue.TRUE_VALUE
							+ "" : CommonColumnsValue.FALSE_VALUE + "" },
					GroupColumns.UPDATETIME + " desc");

			while (cursor.moveToNext()) {

				GroupInfo groupInfo = GroupColumns.parseCursorToGroup(cursor);

				if (null != groupInfo) {

					groupList.add(groupInfo);
				}
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return groupList;
	}

	public GroupInfo getLastestUnJoinedGroup() {

		GroupInfo group = null;

		Cursor cursor = null;

		Uri uri = URIField.GROUP_URI;

		try {

			cursor = mCr.query(uri, null, GroupColumns.IS_JOINED + "=?",
					new String[] { CommonColumnsValue.FALSE_VALUE + "" },
					GroupColumns.UPDATETIME + " desc" + " limit 1 ");

			if (null != cursor && cursor.moveToFirst()) {

				group = GroupColumns.parseCursorToGroup(cursor);

			} else {

				group = new GroupInfo();
			}

			group.is_group_invite = true;

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return group;
	}

	public List<GroupInfo> getJoinedNoDeleteGroup() {

		List<GroupInfo> groupList = new ArrayList<GroupInfo>();

		List<GroupInfo> topGroupList = getJoinedGroup(true);

		List<GroupInfo> joinedGroupList = sortGroup(getJoinedGroup(false),
				getLastestUnJoinedGroup());

		if (null != topGroupList && !topGroupList.isEmpty()) {

			groupList.addAll(topGroupList);
		}

		if (null != joinedGroupList && !joinedGroupList.isEmpty()) {

			groupList.addAll(joinedGroupList);
		}

		return groupList;
	}

	private List<GroupInfo> sortGroup(List<GroupInfo> joinedGroupList,
			GroupInfo groupInfo) {

		List<GroupInfo> groupList = new ArrayList<GroupInfo>();

		if (null == joinedGroupList || joinedGroupList.size() == 0) {

			groupList.add(groupInfo);

			return groupList;
		}

		if (TextUtils.isEmpty(groupInfo._id)
				|| TextUtils.isEmpty(groupInfo.updateTime)) {

			groupList.addAll(joinedGroupList);

			groupList.add(groupInfo);

			return groupList;
		}

		long inviteGroupTime = PrettyDateFormat
				.getTimeMillis(groupInfo.updateTime);

		boolean isSort = false;

		for (GroupInfo group : joinedGroupList) {

			if (isSort) {

				groupList.add(group);

			} else {

				long joinedTime = PrettyDateFormat
						.getTimeMillis(group.updateTime);

				if (inviteGroupTime > joinedTime) {

					isSort = true;

					groupList.add(groupInfo);

					groupList.add(group);

				} else {

					groupList.add(group);
				}
			}
		}

		return groupList;
	}

	private List<GroupInfo> getJoinedGroup(boolean isTop) {

		List<GroupInfo> groupList = new ArrayList<GroupInfo>();

		Cursor cursor = null;

		Uri uri = URIField.GROUP_URI;

		try {

			cursor = mCr.query(uri, null, GroupColumns.IS_JOINED + "=? AND "
					+ GroupColumns.IS_DELETE + "=? AND " + GroupColumns.IS_TOP
					+ "=?", new String[] {
					CommonColumnsValue.TRUE_VALUE + "",
					CommonColumnsValue.FALSE_VALUE + "",
					(isTop ? (CommonColumnsValue.TRUE_VALUE + "")
							: (CommonColumnsValue.FALSE_VALUE + "")) },
					GroupColumns.UPDATETIME + " desc");

			while (cursor.moveToNext()) {

				GroupInfo groupInfo = GroupColumns.parseCursorToGroup(cursor);

				if (null != groupInfo) {

					groupList.add(groupInfo);
				}
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return groupList;
	}

	public GroupInfo getGroupById(String groupId) {

		if (TextUtils.isEmpty(groupId)) {

			Log.e(TAG, "getGroupById fail, groupId is null");

			return null;
		}

		GroupInfo groupInfo = null;

		Cursor cursor = null;

		Uri uri = URIField.GROUP_URI;

		try {

			cursor = mCr.query(uri, null, GroupColumns.GROUPID + "=?",
					new String[] { groupId }, null);

			if (null != cursor && cursor.moveToFirst()) {

				groupInfo = GroupColumns.parseCursorToGroup(cursor);
			}

		} catch (Exception e) {
			TheOneDatabaseHelper.printException(e);
		} finally {
			TheOneDatabaseHelper.closeCursor(cursor);
		}

		return groupInfo;
	}

	public long updateGroupIsJoined(String groupId, boolean isJoined) {

		long result = -1;

		if (TextUtils.isEmpty(groupId)) {

			Log.e(TAG, "updateGroupIsJoined fail");

			return result;
		}

		Uri uri = URIField.GROUP_URI;

		ContentValues cv = new ContentValues();

		cv.put(GroupColumns.IS_JOINED, isJoined ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		result = mCr.update(uri, cv, GroupColumns.GROUPID + "=?",
				new String[] { groupId });

		return result;
	}

	public long updateGroupIsMuted(String groupId, boolean isMuted) {

		long result = -1;

		if (TextUtils.isEmpty(groupId)) {

			Log.e(TAG, "updateGroupIsMuted fail");

			return result;
		}

		Uri uri = URIField.GROUP_URI;

		ContentValues cv = new ContentValues();

		cv.put(GroupColumns.IS_MUTED, isMuted ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		result = mCr.update(uri, cv, GroupColumns.GROUPID + "=?",
				new String[] { groupId });

		return result;
	}

	public long updateGroupNewMessage(String groupId, MessageInfo message) {

		long result = -1;

		if (TextUtils.isEmpty(groupId) || null == message) {

			Log.e(TAG, "updateGroupNewMessage fail");

			return result;
		}

		Uri uri = URIField.GROUP_URI;

		ContentValues cv = new ContentValues();

		cv.put(GroupColumns.NEW_MESSAGE, message._id);

		cv.put(GroupColumns.UPDATETIME, PrettyDateFormat.getISO8601Time());

		result = mCr.update(uri, cv, GroupColumns.GROUPID + "=?",
				new String[] { groupId });

		return result;
	}

	public long updateGroupName(String groupId, String name) {

		long result = -1;

		if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(name)) {

			Log.e(TAG, "updateGroupName fail");

			return result;
		}

		Uri uri = URIField.GROUP_URI;

		ContentValues cv = new ContentValues();

		cv.put(GroupColumns.NAME, name);

		result = mCr.update(uri, cv, GroupColumns.GROUPID + "=?",
				new String[] { groupId });

		return result;
	}

	public long updateGroup(GroupInfo groupInfo) {

		long result = -1;

		if (null == groupInfo) {

			Log.e(TAG, "updateGroup fail , groupInfo is null");

			return result;
		}

		Uri uri = URIField.GROUP_URI;

		ContentValues cv = GroupColumns.updateValues(groupInfo);

		result = mCr.update(uri, cv, GroupColumns.GROUPID + "=?",
				new String[] { groupInfo._id });

		return result;
	}

	public long updateGroupMember(GroupInfo groupInfo) {

		long result = -1;

		if (null == groupInfo) {

			Log.e(TAG, "updateGroup fail , groupInfo is null");

			return result;
		}

		Uri uri = URIField.GROUP_URI;

		ContentValues cv = GroupColumns.updateMemberValues(groupInfo);

		result = mCr.update(uri, cv, GroupColumns.GROUPID + "=?",
				new String[] { groupInfo._id });

		return result;
	}

	public long updateGroupInTop(String groupId, boolean isTop) {

		long result = -1;

		if (TextUtils.isEmpty(groupId)) {

			Log.e(TAG, "updateGroupInTop fail , groupId is null");

			return result;
		}

		Uri uri = URIField.GROUP_URI;

		ContentValues cv = new ContentValues();

		cv.put(GroupColumns.IS_TOP, isTop ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		result = mCr.update(uri, cv, GroupColumns.GROUPID + "=?",
				new String[] { groupId });

		return result;
	}

	public long updateGroupForDelete(String groupId, boolean isDelete) {

		long result = -1;

		if (TextUtils.isEmpty(groupId)) {

			Log.e(TAG, "updateGroupForDelete fail , groupId is null");

			return result;
		}

		Uri uri = URIField.GROUP_URI;

		ContentValues cv = new ContentValues();

		cv.put(GroupColumns.IS_DELETE, isDelete ? CommonColumnsValue.TRUE_VALUE
				: CommonColumnsValue.FALSE_VALUE);

		result = mCr.update(uri, cv, GroupColumns.GROUPID + "=?",
				new String[] { groupId });

		if (isDelete) {

			MessageDbAdapter.getInstance().deleteAllMessage(groupId);
		}

		return result;
	}

	public long deleteGroupByGroupIdForQuit(String groupId) {

		long result = -1;

		if (TextUtils.isEmpty(groupId)) {

			return result;
		}

		Uri uri = URIField.GROUP_URI;

		result = mCr.delete(uri, GroupColumns.GROUPID + "=?",
				new String[] { groupId });

		UserDbAdapter.getInstance().deleteAllUserByType(groupId);

		MessageDbAdapter.getInstance().deleteAllMessage(groupId);

		return result;
	}

	public long deleteAllGroup() {

		long result = -1;

		Uri uri = URIField.GROUP_URI;

		result = mCr.delete(uri, null, null);

		return result;
	}
}
