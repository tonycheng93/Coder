package com.tony.coder.im.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.tony.coder.R;
import com.tony.coder.im.ui.adapter.ViewPagerAdapter;
import com.tony.coder.im.ui.fragment.ChatFragment;
import com.tony.coder.im.ui.fragment.ContactFragment;
import com.tony.coder.im.ui.fragment.DiscoverFragment;
import com.tony.coder.im.ui.fragment.MeFragment;
import com.tony.coder.im.view.TabIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity {
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;

    private TabIndicatorView mChatIndicator;
    private TabIndicatorView mContactIndicator;
    private TabIndicatorView mDiscoverIndicator;
    private TabIndicatorView mMeIndicator;

    private List<Fragment> mFragmentList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
        mMeIndicator.setTabIcon(R.drawable.tab_icon_me_normal);
        mMeIndicator.setTabHint(R.string.home_tab_me);
    }

    private void initTab() {
        mFragmentList.add(new ChatFragment());
        mFragmentList.add(new ContactFragment());
        mFragmentList.add(new DiscoverFragment());
        mFragmentList.add(new MeFragment());

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
                    mMeIndicator.setTabIcon(R.drawable.tab_icon_me_focus);
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
                    mMeIndicator.setTabIcon(R.drawable.tab_icon_me_normal);
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
}
