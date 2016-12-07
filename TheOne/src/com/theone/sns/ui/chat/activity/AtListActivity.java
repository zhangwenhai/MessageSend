package com.theone.sns.ui.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.theone.sns.R;
import com.theone.sns.common.FusionAction;
import com.theone.sns.common.FusionConfig;
import com.theone.sns.logic.model.chat.GroupInfo;
import com.theone.sns.logic.model.user.User;
import com.theone.sns.ui.base.IphoneTitleActivity;
import com.theone.sns.ui.chat.ChatAtSelectOneAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2014/11/26.
 */
public class AtListActivity extends IphoneTitleActivity {
    private ListView selectListView;
    private GroupInfo mGroupInfo;
    private ChatAtSelectOneAdapter mChatAtSelectOneAdapter;
    private List<User> mList = new ArrayList<User>();

    @Override
    protected void initLogics() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSubContent(R.layout.at_list_main);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_behind);

        getView();

        setView();

        setListener();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_down_behind, R.anim.push_down);
    }

    private void getView() {
        mGroupInfo = (GroupInfo) getIntent().getSerializableExtra(FusionAction.ChatAction.GROUP_INFO);
        if (null == mGroupInfo || null == mGroupInfo.members) {
            finish();
        }
        selectListView = (ListView) findViewById(R.id.select_list_view);
    }

    private void setView() {
        setTitle(R.string.select_one);
        setLeftButton(R.drawable.icon_back, false, false);

        for (User mUser : mGroupInfo.members) {
            if (FusionConfig.getInstance().getUserId().equals(mUser.userId)) {
                continue;
            }
            mList.add(mUser);
        }
        mChatAtSelectOneAdapter = new ChatAtSelectOneAdapter(getApplicationContext(), optionsForUserIcon);
        mChatAtSelectOneAdapter.setMlist(mList);
        selectListView.setAdapter(mChatAtSelectOneAdapter);
    }

    private void setListener() {
        selectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent mIntent = new Intent();
                mIntent.putExtra(FusionAction.ChatAction.USER_INFO, mGroupInfo.members.get(i));
                setResult(RESULT_OK, mIntent);
                finish();
            }
        });
    }


}
