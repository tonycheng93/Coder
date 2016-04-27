package com.tony.coder.im.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/18 11:19
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class CustomViewPager extends ViewPager {

    private boolean mIsEnable = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        if (mIsEnable) {
            try {
                return super.onInterceptHoverEvent(event);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsEnable) {
            return super.onTouchEvent(ev);
        }
        return false;
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
    }

    public void setAdapter(PagerAdapter adapter, int index) {
        super.setAdapter(adapter);
        setCurrentItem(index, false);
    }

    public void setEnableTouchScroll(boolean isEnable) {
        mIsEnable = isEnable;
    }
}
