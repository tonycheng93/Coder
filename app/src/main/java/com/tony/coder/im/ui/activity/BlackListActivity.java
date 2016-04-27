package com.tony.coder.im.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tony.coder.R;
import com.tony.coder.im.CoderApplication;
import com.tony.coder.im.ui.adapter.BlackListAdapter;
import com.tony.coder.im.utils.CollectionUtils;
import com.tony.coder.im.widget.HeaderLayout;
import com.tony.coder.im.widget.dialog.DialogTips;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/18 20:38
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class BlackListActivity extends ActivityBase implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private BlackListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);

        initView();
    }

    private void initView() {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        initTopBarForLeft("黑名单");
        mAdapter = new BlackListAdapter(this, BmobDB.create(this).getBlackList());

        mListView = (ListView) findViewById(R.id.list_blacklist);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BmobChatUser invite = (BmobChatUser) mAdapter.getItem(position);
        showRemoveBlackDialog(position, invite);
    }

    /**
     * 移除黑名单对话框
     *
     * @param position
     * @param user
     */
    public void showRemoveBlackDialog(final int position, final BmobChatUser user) {
        DialogTips dialogTips = new DialogTips(this, "移除黑名单", "你确定将" + user.getUsername() + "移除黑名单吗？",
                "确定", true, true);
        //设置成功事件
        dialogTips.setOnSuccessListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                mAdapter.remove(position);
                mUserManager.removeBlack(user.getUsername(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        showToast("移除黑名单成功");
                        //重新设置下内存中保存的好友列表
                        CoderApplication.getInstance().setContactList(CollectionUtils.list2map(
                                BmobDB.create(getApplicationContext()).getContactList()));
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        showToast("移除黑名单失败：" + s);
                    }
                });
            }
        });
        dialogTips.show();
        dialogTips = null;
    }
}
