package com.tony.coder.im.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.tony.coder.R;
import com.tony.coder.im.ui.adapter.ImageBrowserAdapter;
import com.tony.coder.im.widget.CustomViewPager;

import java.util.ArrayList;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/12 19:06
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class ImageBrowserActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    private CustomViewPager mSvpPager;
    private ImageBrowserAdapter mAdapter;
    private int mPosition;

    private ArrayList<String> mPhotos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showpicture);

        init();
        initViews();
    }

    private void initViews() {
        mSvpPager = (CustomViewPager) findViewById(R.id.pagerview);
        mAdapter = new ImageBrowserAdapter(this,mPhotos);
        mSvpPager.setAdapter(mAdapter);
        mSvpPager.setCurrentItem(mPosition, false);
        mSvpPager.setOnPageChangeListener(this);
    }

    private void init() {
        mPhotos = getIntent().getStringArrayListExtra("photos");
        mPosition = getIntent().getIntExtra("position", 0);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
