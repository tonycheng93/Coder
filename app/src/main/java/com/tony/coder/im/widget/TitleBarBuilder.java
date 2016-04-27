package com.tony.coder.im.widget;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tony.coder.R;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/22 10:22
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class TitleBarBuilder {
    private View viewTitle;
    private TextView tvTitle;
    private ImageView ivLeft;
    private ImageView ivRight;
    private TextView tvLeft;
    private TextView tvRight;

    public TitleBarBuilder(Activity context) {
        viewTitle = context.findViewById(R.id.rl_titlebar);
        tvTitle = (TextView) viewTitle.findViewById(R.id.titlebar_tv);
        ivLeft = (ImageView) viewTitle.findViewById(R.id.titlebar_iv_left);
        ivRight = (ImageView) viewTitle.findViewById(R.id.titlebar_iv_right);
        tvLeft = (TextView) viewTitle.findViewById(R.id.titlebar_tv_left);
        tvRight = (TextView) viewTitle.findViewById(R.id.titlebar_tv_right);
    }

    public TitleBarBuilder(View context) {
        viewTitle = context.findViewById(R.id.rl_titlebar);
        tvTitle = (TextView) viewTitle.findViewById(R.id.titlebar_tv);
        ivLeft = (ImageView) viewTitle.findViewById(R.id.titlebar_iv_left);
        ivRight = (ImageView) viewTitle.findViewById(R.id.titlebar_iv_right);
        tvLeft = (TextView) viewTitle.findViewById(R.id.titlebar_tv_left);
        tvRight = (TextView) viewTitle.findViewById(R.id.titlebar_tv_right);
    }

    //title
    public TitleBarBuilder setTitleBgRes(int resId) {
        viewTitle.setBackgroundResource(resId);
        return this;
    }

    public TitleBarBuilder setTitleText(String text) {
        tvTitle.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        tvTitle.setText(text);
        return this;
    }

    public TitleBarBuilder setTitleColor(int resId) {
        tvTitle.setTextColor(resId);
        return this;
    }

    //left
    public TitleBarBuilder setLeftImage(int resId) {
        ivLeft.setVisibility(resId > 0 ? View.VISIBLE : View.GONE);
        ivLeft.setImageResource(resId);
        return this;
    }

    public TitleBarBuilder setLeftText(String text) {
        tvLeft.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        tvLeft.setText(text);
        return this;
    }

    public TitleBarBuilder setLeftOnClickListener(View.OnClickListener listener) {
        if (ivLeft.getVisibility() == View.VISIBLE) {
            ivLeft.setOnClickListener(listener);
        } else if (tvLeft.getVisibility() == View.VISIBLE) {
            tvLeft.setOnClickListener(listener);
        }
        return this;
    }

    //right
    public TitleBarBuilder setRightImage(int resId) {
        ivRight.setVisibility(resId > 0 ? View.VISIBLE : View.GONE);
        ivRight.setImageResource(resId);
        return this;
    }

    public TitleBarBuilder setRightText(String text) {
        tvRight.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        tvRight.setText(text);
        return this;
    }

    public TitleBarBuilder setRightOnClickListener(View.OnClickListener listener) {
        if (ivRight.getVisibility() == View.VISIBLE) {
            ivRight.setOnClickListener(listener);
        } else if (tvRight.getVisibility() == View.VISIBLE) {
            tvRight.setOnClickListener(listener);
        }
        return this;
    }

    public View build() {
        return viewTitle;
    }
}
