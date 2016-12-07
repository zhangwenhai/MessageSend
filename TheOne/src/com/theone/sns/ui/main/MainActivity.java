package com.theone.sns.ui.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.theone.sns.R;
import com.theone.sns.TheOneApp;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionAction.PublicAction;
import com.theone.sns.common.FusionCode;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.component.location.poi.PoiSearchManager;
import com.theone.sns.logic.adapter.db.SettingDbAdapter;
import com.theone.sns.logic.chat.IPushListener;
import com.theone.sns.logic.chat.impl.ChatManager;
import com.theone.sns.service.PubnubService;
import com.theone.sns.ui.chat.activity.ChatActivity;
import com.theone.sns.ui.chat.activity.ChatListFragment;
import com.theone.sns.ui.discover.DiscoverFragment;
import com.theone.sns.ui.login.LeadActivity;
import com.theone.sns.ui.mblog.activity.MblogmodelFragment;
import com.theone.sns.ui.me.activity.MeFragment;
import com.theone.sns.ui.publish.activity.PublishActivity;
import com.theone.sns.util.NotificationBuilder;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.UpdateManager;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.ShareSDK;

public class MainActivity extends FragmentActivity implements
        OnTabChangeListener, IPushListener {

    private static final int DEFAULT_TAB_INDEX = 0;
    public static final String TAB_ACTIVE_INDEX = "tabActiveIndex";
    private static final String TAB_SPEC_MBLOG = "TAB_SPEC_MBLOG";
    private static final String TAB_SPEC_DISCOVER = "TAB_SPEC_DISCOVER";
    private static final String TAB_SPEC_PUBLISH = "TAB_SPEC_PUBLISH";
    public static final String TAB_SPEC_CHAT = "TAB_SPEC_CHAT";
    private static final String TAB_SPEC_ME = "TAB_SPEC_ME";

    private Map<String, View> badgeMap = new HashMap<String, View>();
    private PopupWindow popupWindow;
    private FragmentTabHost mTabHost;
    private View popView;

    private CloseAppBroadcastReceiver closeApp;
    private long mLastTime;
    private long mCurTime;
    public static int statusBarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTabHost();

        initPopupWindow();

        initApplication();

        UpdateManager.register(this, FusionConfig.HOCKEYAPP_APP_ID);

        FeedbackManager.register(this, FusionConfig.HOCKEYAPP_APP_ID);

        ChatManager.getInstance().addListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationBuilder.clearNotification();
        CrashManager.register(this, FusionConfig.HOCKEYAPP_APP_ID);
        setTabBadge();
        ChatManager.getInstance().getAllUnReadMessageCount();
    }

    private void setTabBadge() {
        if (SettingDbAdapter.getInstance().getBoolean(
                FusionCode.SettingKey.IS_NEW_POST, false)) {
            badgeMap.get(TAB_SPEC_MBLOG).setVisibility(View.VISIBLE);
        } else {
            badgeMap.get(TAB_SPEC_MBLOG).setVisibility(View.GONE);
        }

        if (SettingDbAdapter.getInstance().getBoolean(
                FusionCode.SettingKey.IS_LIKED_BY, false)
                || SettingDbAdapter.getInstance().getBoolean(
                FusionCode.SettingKey.IS_FOLLOWED_BY, false)) {
            badgeMap.get(TAB_SPEC_ME).setVisibility(View.VISIBLE);
        } else {
            badgeMap.get(TAB_SPEC_ME).setVisibility(View.GONE);
        }
    }

    @Override
    public void onTabChanged(String tabId) {
    }

    private void initTabHost() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.setOnTabChangedListener(this);

        // Add tab : TheOne
        addTabView(
                TAB_SPEC_MBLOG,
                inflaterTabItemView(TAB_SPEC_MBLOG, R.string.tab_item_theone,
                        R.drawable.tab_item_theone_selector),
                MblogmodelFragment.class);

        // Add tab : Discover
        addTabView(
                TAB_SPEC_DISCOVER,
                inflaterTabItemView(TAB_SPEC_DISCOVER,
                        R.string.tab_item_discover,
                        R.drawable.tab_item_discover_selector),
                DiscoverFragment.class);

        // Add tab : Publish
        addTabView(TAB_SPEC_PUBLISH,
                inflaterTabItemView(R.drawable.tab_item_publish_selector),
                PublishActivity.class);

        // Add tab : Chat
        addTabView(
                TAB_SPEC_CHAT,
                inflaterTabItemView(TAB_SPEC_CHAT, R.string.tab_item_chat,
                        R.drawable.tab_item_chat_selector),
                ChatListFragment.class);

        // Add tab : Me
        addTabView(
                TAB_SPEC_ME,
                inflaterTabItemView(TAB_SPEC_ME, R.string.tab_item_me,
                        R.drawable.tab_item_me_selector), MeFragment.class);

        int tab = getIntent().getIntExtra(TAB_ACTIVE_INDEX, -1);
        if (tab < 0) {
            mTabHost.setCurrentTab(DEFAULT_TAB_INDEX);
        } else {
            mTabHost.setCurrentTab(tab);
        }

        mTabHost.getTabWidget().getChildTabViewAt(0)
                .setOnTouchListener(new onDoubleClick());
    }

    private void initPopupWindow() {
        popView = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.public_pop, null);
        popView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != popupWindow)
                    popupWindow.dismiss();
            }
        });
        popupWindow = new PopupWindow(popView,
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setHeight(getResources().getDisplayMetrics().heightPixels);
        popupWindow.setWidth(getResources().getDisplayMetrics().widthPixels);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);

        mTabHost.getTabWidget().getChildTabViewAt(2)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != popupWindow && !popupWindow.isShowing()) {
                            popupWindow.showAtLocation(view, Gravity.CENTER, 0,
                                    0);
                            popupWindow.update();
                        }
                    }
                });

        popView.findViewById(R.id.video).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                        startActivity(new Intent(
                                FusionAction.PublicAction.PUBLIC_ACTION)
                                .putExtra(
                                        FusionAction.PublicAction.PUBLIC_TYPE,
                                        FusionAction.PublicAction.TYPE_VIDEO));
                    }
                });

        popView.findViewById(R.id.photo).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                        startActivity(new Intent(
                                FusionAction.PublicAction.PUBLIC_ACTION)
                                .putExtra(
                                        FusionAction.PublicAction.PUBLIC_TYPE,
                                        FusionAction.PublicAction.TYPE_PHOTO));
                        Rect frame = new Rect();
                        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                        // 状态栏高度
                        statusBarHeight = frame.top;
                    }
                });

        popView.findViewById(R.id.album).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                        startActivity(new Intent(
                                FusionAction.PublicAction.PUBLIC_ACTION)
                                .putExtra(
                                        FusionAction.PublicAction.PUBLIC_TYPE,
                                        FusionAction.PublicAction.TYPE_ALBUM));
                    }
                });

    }

    private RelativeLayout inflaterTabItemView(String id, int strId, int picId) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this)
                .inflate(R.layout.tab_item_layout, null);
        TextView tabItemText = (TextView) layout
                .findViewById(R.id.tab_item_text_view);
        tabItemText.setText(strId);
        tabItemText.setBackgroundResource(picId);
        if (!id.equals(TAB_SPEC_CHAT)) {
            View badge = layout.findViewById(R.id.news);
            badgeMap.put(id, badge);
        } else {
            View badge = layout.findViewById(R.id.new1);
            badgeMap.put(id, badge);
        }
        return layout;
    }

    private RelativeLayout inflaterTabItemView(int picId) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this)
                .inflate(R.layout.tab_item_layout, null);
        TextView tabItemText = (TextView) layout
                .findViewById(R.id.tab_item_text_view);
        tabItemText.setBackgroundResource(picId);
        return layout;
    }

    private void addTabView(String spec, RelativeLayout tabItem,
                            Class<?> contentFragment) {
        mTabHost.addTab(mTabHost.newTabSpec(spec).setIndicator(tabItem),
                contentFragment, null);
    }

    @Override
    public void onBackPressed() {
        ImageLoader.getInstance().stop();
        super.onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int tab = intent.getIntExtra(TAB_ACTIVE_INDEX, -1);
        if (-1 == tab) {
            mTabHost.setCurrentTab(DEFAULT_TAB_INDEX);
        } else {
            mTabHost.setCurrentTab(tab);
        }

        share(intent);
    }

    private void share(Intent intent) {

        String filePath = intent.getStringExtra(PublicAction.FILE_PATH);

        String[] plats = intent
                .getStringArrayExtra(PublicAction.SHARE_PLATFORM);

        if (TextUtils.isEmpty(filePath) || null == plats || plats.length <= 0) {
            return;
        }

        ShareSDK.initSDK(TheOneApp.getContext());

        String wechatmoments = getResources().getString(R.string.wechatmoments);

        String sinaweibo = getResources().getString(R.string.sinaweibo);

        for (String plat : plats) {

            if (wechatmoments.equals(plat)) {

                Platform plat1 = ShareSDK.getPlatform("WechatMoments");

                plat1.share(getShareParams(filePath));

            } else if (sinaweibo.equals(plat)) {

                Platform plat2 = ShareSDK.getPlatform("SinaWeibo");

                plat2.share(getShareParams(filePath));
            }
        }
    }

    private ShareParams getShareParams(String filePath) {

        ShareParams sp = new ShareParams();

        sp.setTitle(getString(R.string.app_name));

        sp.setShareType(Platform.SHARE_IMAGE);

        sp.setImagePath(filePath);

        return sp;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(closeApp);
        super.onDestroy();
    }

    @Override
    public void push(int what, Object object) {
        switch (what) {
            case IPushListener.NOTIFICATION:
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        setTabBadge();
                        ChatManager.getInstance().getAllUnReadMessageCount();
                    }
                });
                break;
            case IPushListener.UNREAD_MESSAGE_COUNT: {
                if (null == object) {
                    return;
                }
                final int con = (Integer) object;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (con <= 0) {
                            badgeMap.get(TAB_SPEC_CHAT).setVisibility(View.GONE);
                        } else {
                            badgeMap.get(TAB_SPEC_CHAT).setVisibility(View.VISIBLE);
                            ((TextView) badgeMap.get(TAB_SPEC_CHAT)).setText(con
                                    + "");
                        }
                    }
                });
                break;
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ChatActivity.SHARE_TO_CHAT: {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, R.string.share_to_chat, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void initApplication() {

        initCloseAppBroadcastReceiver();

        initPubNubService();
    }

    private void initPubNubService() {

        Intent serviceIntent = new Intent(this, PubnubService.class);

        startService(serviceIntent);
    }

    private void initCloseAppBroadcastReceiver() {
        closeApp = new CloseAppBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FusionAction.TheOneApp.ACTION_CLOSE_APPLICATION);
        registerReceiver(closeApp, intentFilter);
    }

    public class CloseAppBroadcastReceiver extends BroadcastReceiver {

        private static final String TAG = "CloseAppBroadcastReceiver";

        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(FusionAction.TheOneApp.ACTION_CLOSE_APPLICATION)) {

                Log.e(TAG, "finish MainActivity");

                ImageLoader.getInstance().clearMemoryCache();

                NotificationBuilder.clearNotification();

                ChatManager.getInstance().clear();

                PoiSearchManager.getInstance().clear();

                FusionConfig.getInstance().clear();

                stopService(new Intent(MainActivity.this, PubnubService.class));

                if (intent.hasExtra(FusionAction.TheOneApp.UNAUTHORIZED)) {

                    FusionConfig.logoutAccount(FusionConfig.getInstance()
                            .getUserId(), true);

                } else {

                    FusionConfig.logoutAccount(FusionConfig.getInstance()
                            .getUserId(), false);
                }

                startActivity(new Intent(MainActivity.this, LeadActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_NEW_TASK));

                MainActivity.this.finish();
            }
        }
    }

    class onDoubleClick implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                mLastTime = mCurTime;
                mCurTime = System.currentTimeMillis();
                if (mCurTime - mLastTime < 300) {
                    LocalBroadcastManager.getInstance(MainActivity.this)
                            .sendBroadcast(
                                    new Intent(FusionAction.TheOneApp.UPDATE));
                }
            }
            return false;
        }
    }
}
