package com.tony.coder.im.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/18 11:17
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class CustomGridView extends GridView {
    public CustomGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomGridView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expanSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expanSpec);
    }
}
