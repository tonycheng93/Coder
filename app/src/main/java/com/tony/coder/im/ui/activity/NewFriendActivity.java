package com.tony.coder.im.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tony.coder.R;
import com.tony.coder.im.ui.adapter.NewFriendAdapter;
import com.tony.coder.im.widget.dialog.DialogTips;

import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;

public class NewFriendActivity extends ActivityBase implements AdapterView.OnItemLongClickListener {


    private ListView mListView;
    private NewFriendAdapter mAdapter;

    String from = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);

        from = getIntent().getStringExtra("from");

        initView();
    }

    private void initView() {
        initTopBarForLeft("新的朋友");
        mListView = (ListView) findViewById(R.id.list_newfriend);
        mListView.setOnItemLongClickListener(this);

        mAdapter = new NewFriendAdapter(this, BmobDB.create(this).queryBmobInviteList());
        mListView.setAdapter(mAdapter);

        if (from == null) {//若来自通知栏的点击，则定位到最后一条
            mListView.setSelection(mAdapter.getCount());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        BmobInvitation invite = (BmobInvitation) mAdapter.getItem(position);
        showDeleteDialog(position, invite);
        return true;
    }

    private void showDeleteDialog(final int position, final BmobInvitation invite) {
        DialogTips dialogTips = new DialogTips(this, invite.getFromname(), "删除好友请求", "确定", true, true);
        dialogTips.setOnSuccessListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteInvite(position, invite);
            }
        });
        dialogTips.show();
        dialogTips = null;
    }

    /**
     * 删除请求
     *
     * @param position
     * @param invite
     */
    private void deleteInvite(int position, BmobInvitation invite) {
        mAdapter.remove(position);
        BmobDB.create(this).deleteInviteMsg(invite.getFromid(), Long.toString(invite.getTime()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (from == null) {
            startAnimActivity(HomeActivity.class);
        }
    }
}
