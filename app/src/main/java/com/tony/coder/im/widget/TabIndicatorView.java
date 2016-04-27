package com.tony.coder.im.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tony.coder.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/11 10:40
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class TabIndicatorView extends RelativeLayout {

    @Bind(R.id.tab_indicator_icon)
    ImageView mTabIcon;
    @Bind(R.id.tab_indicator_hint)
    TextView mTabHint;
    @Bind(R.id.iv_tips)
    ImageView iv_tips;

    public TabIndicatorView(Context context) {
        this(context, null);
    }

    public TabIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context,R.layout.tab_indicator_view,this);
        ButterKnife.bind(this, view);
    }

    /**
     * 设置图标
     *
     * @param resId
     */
    public void setTabIcon(int resId) {
        mTabIcon.setImageResource(resId);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTabHint(String title) {
        mTabHint.setText(title);
    }

    public void setTabHint(int titleId) {
        mTabHint.setText(titleId);
    }

    public void setTabHintColor(boolean isSelected) {
        if (isSelected) {
            mTabHint.setTextColor(getResources().getColor(R.color.green));
        } else {
            mTabHint.setTextColor(getResources().getColor(R.color.grey));
        }
    }

    public void setTipsVisibility(boolean isShowTips){
        if (isShowTips){
            iv_tips.setVisibility(VISIBLE);
        }else {
            iv_tips.setVisibility(INVISIBLE);
        }
    }
}
