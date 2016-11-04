package com.tony.coder.im.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tony.coder.R;
import com.tony.coder.im.CoderApplication;
import com.tony.coder.im.db.base.DatabaseUtil;
import com.tony.coder.im.entity.DynamicWall.DynamicWall;
import com.tony.coder.im.sns.UserHelper;
import com.tony.coder.im.ui.activity.CommentActivity;
import com.tony.coder.im.ui.activity.NewDynamicWallActivity;
import com.tony.coder.im.ui.adapter.DiscoverAdapter;
import com.tony.coder.im.utils.CollectionUtils;
import com.tony.coder.im.widget.xlist.XListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.im.task.BRequest;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * 项目名称：Coder
 * 类描述：社区动态fragment
 * 创建人：tonycheng
 * 创建时间：2016/3/31 21:06
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class DiscoverFragment extends BaseFragment implements XListView.IXListViewListener,
        AdapterView.OnItemClickListener {
    private static final String DISCOVER_LIST = "discover_list_";
    private BmobQuery<DynamicWall> mQuery;
    private ArrayList<DynamicWall> mListItems;
    private DiscoverAdapter mAdapter;
    private XListView mListView;
    private TextView networkTips;
    private int mPageNum;
    private ImageView mAdd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findView();
        initView();
    }

    private void initView() {
        mListItems = new ArrayList<>();
        mPageNum = 0;
        if (CoderApplication.getInstance().getCache().getAsObject(DISCOVER_LIST) != null) {
            mListItems = (ArrayList<DynamicWall>) CoderApplication.getInstance().getCache()
                    .getAsObject(DISCOVER_LIST);
            networkTips.setVisibility(View.GONE);
        }
        mQuery = new BmobQuery<>();
        mQuery.order("-createdAt");
        mQuery.setLimit(BRequest.QUERY_LIMIT_COUNT);
        mQuery.include("author");
        initTopBarForOnlyTitle("动态");
        initXListView();
        bindEvent();
    }

    private void bindEvent() {
        mListView.setOnItemClickListener(this);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimActivity(NewDynamicWallActivity.class);
            }
        });
    }

    private void initXListView() {
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mAdapter = new DiscoverAdapter(getActivity(), mListItems);
        mListView.setAdapter(mAdapter);
        initDiscoverList(false);
    }

    private void initDiscoverList(final boolean isUpdate) {
        mQuery.addWhereLessThan("createdAt", new BmobDate(new Date(System.currentTimeMillis())));
        mQuery.setSkip(BRequest.QUERY_LIMIT_COUNT * mPageNum);
        mQuery.findObjects(getActivity(), new FindListener<DynamicWall>() {
            @Override
            public void onSuccess(List<DynamicWall> list) {
                networkTips.setVisibility(View.INVISIBLE);
                if (CollectionUtils.isNotNull(list)) {
                    if (isUpdate || mPageNum == 0) {
                        mListItems.clear();
                        mAdapter.setList(mListItems);
                    }
                    if (UserHelper.getCurrentUser() != null){
                        list = DatabaseUtil.getInstance(getActivity()).setFav(list);
                    }

                    mListItems.addAll(list);
                    mAdapter.setList(mListItems);
                    if (list.size() < BRequest.QUERY_LIMIT_COUNT) {
                        mListView.setPullLoadEnable(false);
                    } else {
                        mListView.setPullLoadEnable(true);
                    }
                } else {
                    BmobLog.i("查询成功：无返回值");
                    if (mListItems != null) {
                        mListItems.clear();
                    }
                }
                if (isUpdate) {
                    refreshPull();
                }
                //这样能保证每次查询都是从头开始
                mPageNum = 0;
            }

            @Override
            public void onError(int i, String s) {
                BmobLog.i("查询错误：" + s);
                mListView.setPullLoadEnable(false);
                refreshPull();
                //这样能保证每次查询都是从头开始
                mPageNum = 0;
                showToast(R.string.network_tips);
            }
        });
    }

    private void refreshPull() {
        if (mListView.getPullRefreshing()) {
            mListView.stopRefresh();
            networkTips.setVisibility(View.INVISIBLE);
        }
    }

    private void findView() {
        mListView = (XListView) findViewById(R.id.pull_refresh_list);
        networkTips = (TextView) findViewById(R.id.fragment_networktips);
        mAdd = (ImageView) findViewById(R.id.add_photo);
    }

    @Override
    public void onRefresh() {
        mPageNum = 0;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initDiscoverList(true);
            }
        }, 700);
    }

    @Override
    public void onLoadMore() {
        mQuery.count(getActivity(), DynamicWall.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                if (i > mListItems.size()) {
                    mPageNum++;
                    mQuery.setSkip(BRequest.QUERY_LIMIT_COUNT * (mPageNum));
                    mQuery.findObjects(getActivity(), new FindListener<DynamicWall>() {
                        @Override
                        public void onSuccess(List<DynamicWall> list) {
                            if (UserHelper.getCurrentUser() != null) {
                                list = DatabaseUtil.getInstance(getActivity()).setFav(list);
                            }
                            mListItems.addAll(list);
                            mAdapter.setList(mListItems);
                            refreshLoad();
                        }

                        @Override
                        public void onError(int i, String s) {
                            BmobLog.i("查询错误：" + s);
                            mListView.setPullLoadEnable(false);
                            refreshLoad();
                        }
                    });
                } else {
                    showToast("数据加载完成~");
                    mListView.setPullLoadEnable(false);
                    refreshLoad();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                refreshLoad();
            }
        });
    }

    private void refreshLoad() {
        if (mListView.getPullLoading()) {
            mListView.stopLoadMore();
            networkTips.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), CommentActivity.class);
        intent.putExtra("data", mListItems.get(position - 1));
        startAnimActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mListItems != null) {
            CoderApplication.getInstance().getCache().put(DISCOVER_LIST, mListItems);
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String times = format.format(new Date(System.currentTimeMillis()));
        return times;
    }
}
