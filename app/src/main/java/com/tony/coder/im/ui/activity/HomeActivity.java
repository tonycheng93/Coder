package com.tony.coder.im.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.tony.coder.R;
import com.tony.coder.im.CoderApplication;
import com.tony.coder.im.receiver.MessageReceiver;
import com.tony.coder.im.ui.adapter.ViewPagerAdapter;
import com.tony.coder.im.ui.fragment.ChatFragment;
import com.tony.coder.im.ui.fragment.ContactFragment;
import com.tony.coder.im.ui.fragment.DiscoverFragment;
import com.tony.coder.im.ui.fragment.MeFragment;
import com.tony.coder.im.widget.TabIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;

public class HomeActivity extends ActivityBase implements EventListener {
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;

    private TabIndicatorView mChatIndicator;
    private TabIndicatorView mContactIndicator;
    private TabIndicatorView mDiscoverIndicator;
    private TabIndicatorView mMeIndicator;

    private List<Fragment> mFragmentList = new ArrayList<>();

    private int currentTabIndex;

    ChatFragment chatFragment;
    ContactFragment contactFragment;
    DiscoverFragment discoverFragment;
    MeFragment meFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
        //如果你觉得检测服务比较耗流量和电量，你也可以去掉这句话-同时还有onDestory方法里面的stopPollService方法
        BmobChat.getInstance(this).startPollService(20);
        //开启广播接收器
        initNewMessageBroadCast();
        initTagMessageBroadCast();


        ButterKnife.bind(this);

        initIndicator();

        initTab();

        initListener();
    }

    private void initIndicator() {
        mChatIndicator = new TabIndicatorView(this);
        mChatIndicator.setTabIcon(R.drawable.tab_icon_chat_focus);
        mChatIndicator.setTabHintColor(true);
        mChatIndicator.setTabHint(R.string.home_tab_chat);

        mContactIndicator = new TabIndicatorView(this);
        mContactIndicator.setTabIcon(R.drawable.tab_icon_contact_normal);
        mContactIndicator.setTabHint(R.string.home_tab_contact);

        mDiscoverIndicator = new TabIndicatorView(this);
        mDiscoverIndicator.setTabIcon(R.drawable.tab_icon_discover_normal);
        mDiscoverIndicator.setTabHint(R.string.home_tab_discover);

        mMeIndicator = new TabIndicatorView(this);
        mMeIndicator.setTabIcon(R.drawable.tab_icon_setting_normal);
        mMeIndicator.setTabHint(R.string.home_tab_me);
    }

    private void initTab() {
        chatFragment = new ChatFragment();
        contactFragment = new ContactFragment();
        discoverFragment = new DiscoverFragment();
        meFragment = new MeFragment();
        mFragmentList.add(chatFragment);
        mFragmentList.add(contactFragment);
        mFragmentList.add(discoverFragment);
        mFragmentList.add(meFragment);

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setCustomView(mChatIndicator);
        mTabLayout.getTabAt(1).setCustomView(mContactIndicator);
        mTabLayout.getTabAt(2).setCustomView(mDiscoverIndicator);
        mTabLayout.getTabAt(3).setCustomView(mMeIndicator);
    }


    private void initListener() {
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == mTabLayout.getTabAt(0)) {
                    mChatIndicator.setTabIcon(R.drawable.tab_icon_chat_focus);
                    mChatIndicator.setTabHintColor(true);
                    mViewPager.setCurrentItem(0);
                } else if (tab == mTabLayout.getTabAt(1)) {
                    mContactIndicator.setTabIcon(R.drawable.tab_icon_contact_focus);
                    mContactIndicator.setTabHintColor(true);
                    mViewPager.setCurrentItem(1);
                } else if (tab == mTabLayout.getTabAt(2)) {
                    mDiscoverIndicator.setTabIcon(R.drawable.tab_icon_discover_focus);
                    mDiscoverIndicator.setTabHintColor(true);
                    mViewPager.setCurrentItem(2);
                } else if (tab == mTabLayout.getTabAt(3)) {
                    mMeIndicator.setTabIcon(R.drawable.tab_icon_setting_focus);
                    mMeIndicator.setTabHintColor(true);
                    mViewPager.setCurrentItem(3);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab == mTabLayout.getTabAt(0)) {
                    mChatIndicator.setTabIcon(R.drawable.tab_icon_chat_normal);
                    mChatIndicator.setTabHintColor(false);
                } else if (tab == mTabLayout.getTabAt(1)) {
                    mContactIndicator.setTabIcon(R.drawable.tab_icon_contact_normal);
                    mContactIndicator.setTabHintColor(false);
                } else if (tab == mTabLayout.getTabAt(2)) {
                    mDiscoverIndicator.setTabIcon(R.drawable.tab_icon_discover_normal);
                    mDiscoverIndicator.setTabHintColor(false);
                } else if (tab == mTabLayout.getTabAt(3)) {
                    mMeIndicator.setTabIcon(R.drawable.tab_icon_setting_normal);
                    mMeIndicator.setTabHintColor(false);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentTabIndex = position;
                if (position == 0) {
                    mTabLayout.getTabAt(position).isSelected();
                } else if (position == 1) {
                    mTabLayout.getTabAt(position).isSelected();
                } else if (position == 2) {
                    mTabLayout.getTabAt(position).isSelected();
                } else if (position == 3) {
                    mTabLayout.getTabAt(position).isSelected();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //小圆点提示
        if (BmobDB.create(this).hasUnReadMsg()) {
            mChatIndicator.setTipsVisibility(true);
        } else {
            mChatIndicator.setTipsVisibility(false);
        }
        if (BmobDB.create(this).hasNewInvite()) {
            mContactIndicator.setTipsVisibility(true);
        } else {
            mContactIndicator.setTipsVisibility(false);
        }

        MessageReceiver.sEventListeners.add(this);//监听推送事件
        //清空
        MessageReceiver.mNewNum = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageReceiver.sEventListeners.remove(this);//取消监听推送的消息
    }

    @Override
    public void onMessage(BmobMsg message) {
        refreshNewMsg(message);
    }

    /**
     * 刷新界面
     *
     * @param message
     */
    private void refreshNewMsg(BmobMsg message) {
        //声音提示
        boolean isAllowVoice = CoderApplication.getInstance().getSpUtil().isAllowVoice();
        if (isAllowVoice) {
            CoderApplication.getInstance().getMediaPlayer().start();
        }
        mChatIndicator.setTipsVisibility(true);
        //存储消息
        if (message != null) {
            BmobChatManager.getInstance(HomeActivity.this).saveReceiveMessage(true, message);
        }
        if (currentTabIndex == 0) {
            //当前页面如果是会话页面，刷新此页面
            if (chatFragment != null){
                chatFragment.refresh();
            }
        }
    }

    @Override
    public void onReaded(String s, String s1) {

    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        if (isNetConnected) {
            showToast(R.string.network_tips);
        }
    }

    @Override
    public void onAddUser(BmobInvitation message) {
        refreshInvite(message);
    }

    @Override
    public void onOffline() {
        showOfflineDialog(this);
    }

    NewBroadCastReceiver newReceiver;

    private void initNewMessageBroadCast() {
        //注册接收消息广播
        newReceiver = new NewBroadCastReceiver();
        IntentFilter filter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        //优先级要低于ChatActivity
        filter.setPriority(3);
        registerReceiver(newReceiver, filter);
    }

    /**
     * 新消息广播接收者
     */
    private class NewBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //刷新页面
            refreshNewMsg(null);
            //关闭广播
            abortBroadcast();
        }
    }

    TagBroadCastReceiver userReceiver;

    private void initTagMessageBroadCast() {
        //注册广播
        userReceiver = new TagBroadCastReceiver();
        IntentFilter filter = new IntentFilter(BmobConfig.BROADCAST_ADD_USER_MESSAGE);
        //优先级要低于ChatActivity
        filter.setPriority(3);
        registerReceiver(userReceiver, filter);
    }

    /**
     * 标签消息广播接收者
     */
    private class TagBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            BmobInvitation message = (BmobInvitation) intent.getSerializableExtra("invite");
            refreshInvite(message);
            //关闭广播
            abortBroadcast();
        }
    }

    private void refreshInvite(BmobInvitation message) {
        boolean isAllowVoice = CoderApplication.getInstance().getSpUtil().isAllowVoice();
        if (isAllowVoice) {
            CoderApplication.getInstance().getMediaPlayer().start();
        }
        mContactIndicator.setTipsVisibility(true);
        if (currentTabIndex == 1) {
            if (contactFragment != null) {
                contactFragment.refresh();
            }
        } else {
            //同时提醒通知
            String tickerText = message.getFromname() + "请求添加好友";
            boolean isAllowVibrate = CoderApplication.getInstance().getSpUtil().isAllowVibrate();
            BmobNotifyManager.getInstance(this).showNotify(isAllowVoice, isAllowVibrate,
                    R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(), NewFriendActivity.class);

        }
    }

    private static long firstTime;

    /**
     * 连续按两次返回键就退出
     */
    @Override
    public void onBackPressed() {
        if (firstTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            showToast("再按一次退出程序！");
        }
        firstTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(newReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            unregisterReceiver(userReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
