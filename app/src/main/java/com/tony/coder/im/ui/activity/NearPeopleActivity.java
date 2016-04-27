package com.tony.coder.im.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.tony.coder.R;
import com.tony.coder.im.entity.User;
import com.tony.coder.im.ui.adapter.NearPeopleAdapter;
import com.tony.coder.im.utils.CollectionUtils;
import com.tony.coder.im.widget.xlist.XListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.task.BRequest;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/15 15:07
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class NearPeopleActivity extends ActivityBase implements XListView.IXListViewListener,
        AdapterView.OnItemClickListener {

    private XListView mListView;
    private NearPeopleAdapter mAdapter;

    String from = "";

    List<User> nears = new ArrayList<>();

    private double QUERY_KILOMETERS = 10;//默认查询10公里范围内的人

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_people);

        initView();
    }

    private void initView() {
        initTopBarForLeft("附近的人");
        initXListView();
    }

    private void initXListView() {
        mListView = (XListView) findViewById(R.id.list_near);
        mListView.setOnItemClickListener(this);
        //不允许加载更多
        mListView.setPullLoadEnable(false);
        //允许下拉
        mListView.setPullRefreshEnable(true);
        //设置监听器
        mListView.setXListViewListener(this);
        mListView.PullRefreshing();

        mAdapter = new NearPeopleAdapter(this, nears);
        mListView.setAdapter(mAdapter);

        initNearByList(false);
    }

    int curPage = 0;
    ProgressDialog progress;

    private void initNearByList(final boolean isUpdate) {
        if (!isUpdate) {
            progress = new ProgressDialog(NearPeopleActivity.this);
            progress.setMessage("正在查询附近的人...");
            progress.setCanceledOnTouchOutside(true);
            progress.show();
        }

        if (!mCoderApplication.getLatitude().equals("") && !mCoderApplication.getLongtitude().equals("")) {
            double latitude = Double.parseDouble(mCoderApplication.getLatitude());
            double longitude = Double.parseDouble(mCoderApplication.getLongtitude());

            //封装的查询方法，当进入此页面时 isUpdate为false，当下拉刷新的时候设置为true就行。
            //此方法默认每页查询10条数据,若想查询多于10条，可在查询之前设置BRequest.QUERY_LIMIT_COUNT，如：BRequest.QUERY_LIMIT_COUNT=20
            // 此方法是新增的查询指定10公里内的性别为女性的用户列表，默认包含好友列表
            //如果你不想查询性别为女的用户，可以将equalProperty设为null或者equalObj设为null即可

            mUserManager.queryKiloMetersListByPage(isUpdate, 0, "location", longitude, latitude, true, QUERY_KILOMETERS, "sex", false,
                    new FindListener<User>() {
                        @Override
                        public void onSuccess(List<User> list) {
                            if (CollectionUtils.isNotNull(list)) {
                                if (isUpdate) {
                                    nears.clear();
                                }
                                mAdapter.addAll(list);
                                if (list.size() < BRequest.QUERY_LIMIT_COUNT) {
                                    mListView.setPullLoadEnable(false);
                                    showToast("附近的人搜索完成！");
                                } else {
                                    mListView.setPullLoadEnable(true);
                                }
                            } else {
                                showToast("暂无附近的人！");
                            }
                            if (!isUpdate) {
                                progress.dismiss();
                            } else {
                                refreshPull();
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            showToast("暂无附近的人！");
                            mListView.setPullLoadEnable(false);
                            if (!isUpdate) {
                                progress.dismiss();
                            } else {
                                refreshPull();
                            }
                        }
                    });

            //此方法默认查询所有带地理位置信息的且性别为女的用户列表，如果你不想包含好友列表的话，将查询条件中的isShowFriends设置为false就行
            //userManager.queryNearByListByPage(isUpdate,0,"location", longtitude, latitude, true,"sex",false,new FindListener<User>() {

        } else {
            showToast("暂无附近的人！");
            progress.dismiss();
            refreshPull();
        }
    }

    private void queryMoreNearList(int page) {
        double latitude = Double.parseDouble(mCoderApplication.getLatitude());
        double longitude = Double.parseDouble(mCoderApplication.getLongtitude());
        //查询10公里范围内的性别为女的用户列表
        mUserManager.queryKiloMetersListByPage(true, page, "location", longitude, latitude, true, QUERY_KILOMETERS,
                "sex", false, new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> list) {
                        if (CollectionUtils.isNotNull(list)) {
                            mAdapter.addAll(list);
                        }
                        refreshLoad();
                    }

                    @Override
                    public void onError(int i, String s) {
                        showLog("查询更多附近的人出错：" + s);
                        mListView.setPullLoadEnable(false);
                        refreshLoad();
                    }
                });
        //查询全部地理位置信息且性别为女性的用户列表
        //userManager.queryNearByListByPage(true,page, "location", longtitude, latitude, true,"sex",false,new FindListener<User>() {
    }

    private void refreshLoad() {
        if (mListView.getPullRefreshing()) {
            mListView.stopRefresh();
        }
    }


    private void refreshPull() {
        if (mListView.getPullRefreshing()) {
            mListView.stopRefresh();
        }
    }

    @Override
    public void onRefresh() {
        initNearByList(true);
    }

    @Override
    public void onLoadMore() {
        double latitude = Double.parseDouble(mCoderApplication.getLatitude());
        double longitude = Double.parseDouble(mCoderApplication.getLongtitude());

        //这是查询10公里范围内的性别为女用户总数
        mUserManager.queryKiloMetersTotalCount(User.class, "location", longitude, latitude, true, QUERY_KILOMETERS,
                "sex", false, new CountListener() {

                    //这是查询附近的人且性别为女性的用户总数
                    //userManager.queryNearTotalCount(User.class, "location", longtitude, latitude, true,"sex",false,new CountListener() {
                    @Override
                    public void onSuccess(int i) {
                        if (i > nears.size()) {
                            curPage++;
                            queryMoreNearList(curPage);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User user = (User) mAdapter.getItem(position - 1);
        Intent intent = new Intent(this, SetMyInfoActivity.class);
        intent.putExtra("from", "add");
        intent.putExtra("username", user.getUsername());
        startAnimActivity(intent);
    }
}
