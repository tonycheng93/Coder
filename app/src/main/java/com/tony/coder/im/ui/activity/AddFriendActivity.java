package com.tony.coder.im.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.tony.coder.R;
import com.tony.coder.im.ui.adapter.AddFriendAdapter;
import com.tony.coder.im.utils.CollectionUtils;
import com.tony.coder.im.widget.xlist.XListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.task.BRequest;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * 项目名称：Coder
 * 类描述：查找好友
 * 创建人：tonycheng
 * 创建时间：2016/4/15 14:38
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class AddFriendActivity extends ActivityBase implements View.OnClickListener,
        XListView.IXListViewListener, AdapterView.OnItemClickListener {

    private EditText et_find_name;
    private Button btn_search;

    List<BmobChatUser> users = new ArrayList<>();
    private XListView mListView;
    private AddFriendAdapter mAdapter;

    private String searchName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        initView();
    }

    private void initView() {
        initTopBarForLeft("查找好友");
        et_find_name = (EditText) findViewById(R.id.et_find_name);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);

        initXListView();
    }

    private void initXListView() {
        mListView = (XListView) findViewById(R.id.list_search);
        //首先不允许加载更多
        mListView.setPullLoadEnable(false);
        //不允许下拉
        mListView.setPullRefreshEnable(false);
        //设置监听器
        mListView.setXListViewListener(this);

        mListView.PullRefreshing();

        mAdapter = new AddFriendAdapter(this, users);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    int curPage = 0;
    ProgressDialog progress;

    private void initSearchList(final boolean isUpdate) {
        if (!isUpdate) {
            progress = new ProgressDialog(AddFriendActivity.this);
            progress.setMessage("正在搜索...");
            progress.setCanceledOnTouchOutside(true);
            progress.show();
        }

        mUserManager.queryUserByPage(isUpdate, 0, searchName, new FindListener<BmobChatUser>() {
            @Override
            public void onSuccess(List<BmobChatUser> list) {
                if (CollectionUtils.isNotNull(list)) {
                    if (isUpdate) {
                        users.clear();
                    }
                    mAdapter.addAll(list);
                    if (list.size() < BRequest.QUERY_LIMIT_COUNT) {
                        mListView.setPullLoadEnable(false);
                        showToast("用户搜索完成！");
                    } else {
                        mListView.setPullLoadEnable(true);
                    }
                } else {
                    BmobLog.i("查询成功：无返回值");
                    if (users != null) {
                        users.clear();
                    }
                    showToast("用户不存在");
                }
                if (!isUpdate) {
                    progress.dismiss();
                } else {
                    refreshPull();
                }
                //这样能保证每次查询都是从头开始
                curPage = 0;
            }

            @Override
            public void onError(int i, String s) {
                BmobLog.i("查询错误：" + s);
                if (users != null) {
                    users.clear();
                }
                showToast("用户不存在！");
                mListView.setPullLoadEnable(false);
                refreshPull();
                //这样能保证每次查询都是从头开始
                curPage = 0;
            }
        });
    }

    /**
     * 查询更多
     *
     * @param page
     */
    private void queryMoreSearchList(int page) {
        mUserManager.queryUserByPage(true, page, searchName, new FindListener<BmobChatUser>() {
            @Override
            public void onSuccess(List<BmobChatUser> list) {
                if (CollectionUtils.isNotNull(list)) {
                    mAdapter.addAll(list);
                }
                refreshLoad();
            }

            @Override
            public void onError(int i, String s) {
                showLog("搜索更多用户出错：" + s);
                mListView.setPullLoadEnable(false);
                refreshLoad();
            }
        });
    }


    private void refreshLoad() {
        if (mListView.getPullRefreshing()) {
            mListView.stopLoadMore();
        }
    }

    private void refreshPull() {
        if (mListView.getPullRefreshing()) {
            mListView.stopRefresh();
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        mUserManager.querySearchTotalCount(searchName, new CountListener() {
            @Override
            public void onSuccess(int i) {
                if (i > users.size()) {
                    curPage++;
                    queryMoreSearchList(curPage);
                } else {
                    showToast("数据加载完成！");
                    mListView.setPullLoadEnable(false);
                    refreshLoad();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                showLog("查询附近的人总数失败：" + s);
                refreshLoad();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                users.clear();
                searchName = et_find_name.getText().toString();
                if (searchName != null && !searchName.equals("")) {
                    initSearchList(false);
                } else {
                    showToast("请输入用户名！");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BmobChatUser user = (BmobChatUser) mAdapter.getItem(position - 1);
        Intent intent = new Intent(this, SetMyInfoActivity.class);
        intent.putExtra("from", "add");
        intent.putExtra("username", user.getUsername());
        startAnimActivity(intent);
    }
}
