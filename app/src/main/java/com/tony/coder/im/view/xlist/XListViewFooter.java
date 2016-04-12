package com.tony.coder.im.view.xlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tony.coder.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/11 15:18
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class XListViewFooter extends LinearLayout {

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_LOADING = 2;

    @Bind(R.id.xlistview_footer_progressbar)
    ProgressBar mProgressbar;
    @Bind(R.id.xlistview_footer_hint_textview)
    TextView mHintView;
    @Bind(R.id.xlistview_footer_content)
    RelativeLayout mContentView;

    private Context mContext;

    public XListViewFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public XListViewFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public XListViewFooter(Context context) {
        super(context);
    }

    private void initView(Context context) {
        mContext = context;
        LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.xlistview_footer, null);
        addView(moreView);

        moreView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        ButterKnife.bind(this, moreView);
    }

    public void setState(int state) {
        mHintView.setVisibility(INVISIBLE);
        mProgressbar.setVisibility(INVISIBLE);
        mHintView.setVisibility(INVISIBLE);
        if (state == STATE_READY) {
            mHintView.setVisibility(VISIBLE);
            mHintView.setText(R.string.xlistview_footer_hint_ready);
        } else if (state == STATE_LOADING) {
            mProgressbar.setVisibility(VISIBLE);
        } else {
            mHintView.setVisibility(VISIBLE);
            mHintView.setText(R.string.xlistview_footer_hint_normal);
        }
    }

    public void setBottomMargin(int height) {
        if (height < 0) return;
        LinearLayout.LayoutParams params = (LayoutParams) mContentView.getLayoutParams();
        params.bottomMargin = height;
        mContentView.setLayoutParams(params);
    }

    public int getBottomMargin() {
        LinearLayout.LayoutParams params = (LayoutParams) mContentView.getLayoutParams();
        return params.bottomMargin;
    }

    /**
     * normal status
     */
    public void normal() {
        mHintView.setVisibility(VISIBLE);
        mProgressbar.setVisibility(GONE);
    }

    /**
     * loading status
     */
    public void loading(){
        mHintView.setVisibility(GONE);
        mProgressbar.setVisibility(VISIBLE);
    }

    /**
     * hide footer when disable pull load more
     */
    public void hide(){
        LinearLayout.LayoutParams params = (LayoutParams) mContentView.getLayoutParams();
        params.height = 0;
        mContentView.setLayoutParams(params);
    }

    /**
     * show footer
     */
    public void show(){
        LinearLayout.LayoutParams params = (LayoutParams) mContentView.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mContentView.setLayoutParams(params);
    }
}
