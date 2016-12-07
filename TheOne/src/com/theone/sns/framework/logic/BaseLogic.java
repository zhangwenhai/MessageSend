/*
 * 文件名: BaseLogic.java
 * 描    述: logic抽象类，所有的业务实现logic必须继承
 * 创建人: zhouyujun
 */
package com.theone.sns.framework.logic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * logic抽象类，所有的业务实现logic必须继承<BR>
 * 提供handler和监听数据库等其他接口实现
 * 
 * @author zhouyujun
 */
public abstract class BaseLogic implements ILogic {

	private static final String TAG = "BaseLogic";

	/**
	 * logic对象中UI监听的handler缓存集合
	 */
	private final Set<Handler> mHandlerList = new HashSet<Handler>();

	/**
	 * logic对象中UI独自监听的handler
	 */
	private Handler mHandler;

	/**
	 * 数据库Uri对应的Observer对象的集合
	 */
	private final Map<Uri, ContentObserver> mObserverCache = new HashMap<Uri, ContentObserver>();

	/**
	 * 系统的context对象
	 */
	public Context mContext;

	public Object object = new Object();

	/**
	 * 初始化方法<BR>
	 * 在被系统管理的logic在注册到LogicBuilder中后立即被调用的初始化方法。
	 * 
	 * @param context
	 *            系统的context对象
	 */
	public void init(Context context) {
		this.mContext = context;
		// 对子类定义的URI进行数据库表操作监听
		Uri[] uris = getObserverUris();
		if (uris != null) {
			for (Uri uri : uris) {
				registerObserver(uri);
			}
		}
	}

	/**
	 * 对logic增加handler<BR>
	 * 在logic对象里加入UI的handler
	 *
	 * @param handler
	 *            UI传入的handler对象
	 */
	public final void addSingleHandler(Handler handler) {
		mHandler = handler;
	}

	/**
	 * 对logic增加handler<BR>
	 * 在logic对象里加入UI的handler
	 *
	 * @param handler
	 *            UI传入的handler对象
	 */
	public final void addHandler(Handler handler) {

		synchronized (object) {

			mHandlerList.add(handler);
		}

		Log.i(TAG, "In add hander method." + this.getClass().getName()
				+ " have " + mHandlerList.size() + " hander.");
	}

	/**
	 * 对logic移除handler<BR>
	 * 在logic对象里移除UI的handler
	 *
	 * @param handler
	 *            UI传入的handler对象
	 */
	public final void removeHandler(Handler handler) {

		synchronized (object) {

			mHandlerList.remove(handler);
		}

		Log.i(TAG, "In remove hander method." + this.getClass().getName()
				+ " have " + mHandlerList.size() + " hander.");
	}

	/**
	 * 对 该logic对象定义被监听所有Uri<BR>
	 * 通过子类重载该方法，传入Uri数组来监听该logic所要监听的数据库对象， 没有重载或返回为空将不监听任何数据库表变化
	 *
	 * @return Uri数组
	 */
	protected Uri[] getObserverUris() {
		return null;
	}

	/**
	 * 对URI注册鉴定数据库表<BR>
	 * 根据URI监听数据库表的变化，放入监听对象缓存
	 *
	 * @param uri
	 *            数据库的Content Provider的 Uri
	 */
	protected final void registerObserver(final Uri uri) {
		ContentObserver observer = new ContentObserver(new Handler()) {
			public void onChange(boolean selfChange) {
				BaseLogic.this.onChangeByUri(selfChange, uri);
			}
		};
		mContext.getContentResolver().registerContentObserver(uri, true,
				observer);
		mObserverCache.put(uri, observer);
	}

	/**
	 * 对URI注册鉴定数据库表<BR>
	 * 根据URI监听数据库表的变化，放入监听对象缓存
	 *
	 * @param uri
	 *            数据库的Content Provider的 Uri
	 * @param observer
	 *            数据库的Content Provider
	 */
	protected final void registerObserver(final Uri uri,
			ContentObserver observer) {
		mContext.getContentResolver().registerContentObserver(uri, true,
				observer);
		mObserverCache.put(uri, observer);
	}

	/**
	 * 对URI解除注册鉴定数据库表<BR>
	 * 根据URI移除对数据库表变化的监听，移出监听对象缓存
	 *
	 * @param uri
	 *            数据库的Content Provider的 Uri
	 */
	protected final void unRegisterObserver(Uri uri) {
		ContentObserver observer = mObserverCache.get(uri);
		if (observer != null) {
			mContext.getContentResolver().unregisterContentObserver(observer);
			mObserverCache.remove(uri);
		}
	}

	/**
	 * 当对数据库表定义的Uri进行监听后，被回调方法<BR>
	 * 子类中需重载该方法，可以在该方法的代码中对表变化进行监听实现
	 *
	 * @param selfChange
	 *            如果是true，被监听是由于代码执行了commit造成的
	 * @param uri
	 *            被监听的Uri
	 */
	protected void onChangeByUri(boolean selfChange, Uri uri) {

	}

	/**
	 * 发送消息给UI<BR>
	 * 通过监听回调，通知在该logic对象中所有注册了handler的UI消息message对象
	 *
	 * @param what
	 *            返回的消息标识
	 * @param obj
	 *            返回的消息数据对象
	 */
	public void sendMessage(int what, Object obj) {

		synchronized (object) {

			Log.d(TAG, "mHandler=" + mHandler);
			Handler[] handlers = mHandlerList.toArray(new Handler[mHandlerList
					.size()]);
			for (Handler handler : handlers) {
				sendMsg(handler, what, obj);
			}

			if (null != mHandler) {
				sendMsg(mHandler, what, obj);
			}
		}
	}

	private void sendMsg(Handler handler, int what, Object obj) {
		if (obj == null) {
			handler.sendEmptyMessage(what);
		} else {
			Message message = new Message();
			message.what = what;
			message.obj = obj;
			handler.sendMessage(message);
		}
	}

	/**
	 * 发送无数据对象消息给UI<BR>
	 * 通过监听回调，通知在该logic对象中所有注册了handler的UI消息message对象
	 *
	 * @param what
	 *            返回的消息标识
	 */
	public void sendEmptyMessage(int what) {

		synchronized (object) {

			Handler[] handlers = mHandlerList.toArray(new Handler[mHandlerList
					.size()]);
			for (Handler handler : handlers) {
				handler.sendEmptyMessage(what);
			}

			if (null != mHandler) {
				mHandler.sendEmptyMessage(what);
			}
		}
	}

	/**
	 * 延迟发送消息给UI<BR>
	 * 通过监听回调，延迟通知在该logic对象中所有注册了handler的UI消息message对象
	 *
	 * @param what
	 *            返回的消息标识
	 * @param obj
	 *            返回的消息数据对象
	 * @param delayMillis
	 *            延迟时间，单位秒
	 */
	public void sendMessageDelayed(int what, Object obj, long delayMillis) {

		synchronized (object) {
			Handler[] handlers = mHandlerList.toArray(new Handler[mHandlerList
					.size()]);
			for (Handler handler : handlers) {
				if (!handler.hasMessages(what)) {
					Message message = new Message();
					message.what = what;
					message.obj = obj;
					handler.sendMessageDelayed(message, delayMillis);
				}
			}

			if (null != mHandler) {
				if (!mHandler.hasMessages(what)) {
					Message message = new Message();
					message.what = what;
					message.obj = obj;
					mHandler.sendMessageDelayed(message, delayMillis);
				}
			}
		}
	}

	/**
	 * 延迟发送空消息给UI<BR>
	 * 通过监听回调，延迟通知在该logic对象中所有注册了handler的UI消息message对象
	 *
	 * @param what
	 *            返回的消息标识
	 * @param delayMillis
	 *            延迟时间，单位秒
	 */
	public void sendEmptyMessageDelayed(int what, long delayMillis) {

		synchronized (object) {

			Handler[] handlers = mHandlerList.toArray(new Handler[mHandlerList
					.size()]);
			for (Handler handler : handlers) {
				if (!handler.hasMessages(what)) {
					handler.sendEmptyMessageDelayed(what, delayMillis);
				}
			}

			if (null != mHandler) {
				if (!mHandler.hasMessages(what)) {
					mHandler.sendEmptyMessageDelayed(what, delayMillis);
				}
			}
		}
	}
}
