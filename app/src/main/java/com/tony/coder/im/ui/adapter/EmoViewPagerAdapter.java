package com.tony.coder.im.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;

import java.util.List;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/14 15:08
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class EmoViewPagerAdapter extends PagerAdapter {

    public EmoViewPagerAdapter(List<View> views) {
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}
