package com.tony.coder.im.view.xlist;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
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
 * 创建时间：2016/4/11 14:47
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class XListViewHeader extends LinearLayout {

    @Bind(R.id.xlistview_header_hint_textview)
    TextView mHintTextView;
    @Bind(R.id.xlistview_header_time_label)
    TextView mHeaderTimeLabel;
    @Bind(R.id.xlistview_header_time)
    TextView mHeaderTimeView;
    @Bind(R.id.xlistview_header_text)
    LinearLayout mXlistviewHeaderText;
    @Bind(R.id.xlistview_header_arrow)
    ImageView mArrowImageView;
    @Bind(R.id.xlistview_header_progressbar)
    ProgressBar mProgressBar;
    @Bind(R.id.xlistview_header_content)

    RelativeLayout mXlistviewHeaderContent;
    private LinearLayout mContainer;
    private int mState = STATE_NORMAL;

    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    private final int ROTATE_ANIM_DURATION = 180;

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;

    public XListViewHeader(Context context) {
        super(context);
        initView(context);
    }

    public XListViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public XListViewHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        //初始情况下，设置下拉刷新View高度为0
        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0);
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.xlistview_header, null);
        ButterKnife.bind(this, mContainer);

        addView(mContainer, layoutParams);
        setGravity(Gravity.BOTTOM);

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);

        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setFillAfter(true);
    }

    public void setState(int state) {
        if (state == mState) return;

        if (state == STATE_REFRESHING) {//显示进度
            mArrowImageView.clearAnimation();
            mArrowImageView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(VISIBLE);
        } else {//显示图片
            mArrowImageView.clearAnimation();
            mArrowImageView.setVisibility(INVISIBLE);
        }

        switch (state) {
            case STATE_NORMAL:
                if (mState == STATE_READY) {
                    mArrowImageView.startAnimation(mRotateDownAnim);
                }
                if (mState == STATE_REFRESHING) {
                    mArrowImageView.clearAnimation();
                }
                mHintTextView.setText(R.string.xlistview_header_hint_normal);
                break;
            case STATE_READY:
                if (mState == STATE_READY) {
                    mArrowImageView.clearAnimation();
                    mArrowImageView.startAnimation(mRotateUpAnim);
                    mHintTextView.setText(R.string.xlistview_header_hint_ready);
                }
                break;
            case STATE_REFRESHING:
                mHintTextView.setText(R.string.xlistview_header_hint_loading);
                Time time = new Time();
                time.setToNow();
                setRefreshTime(time.format("%Y-%m-%d %T"));
                break;
            default:
                break;
        }
        mState = state;
    }

    public void setRefreshTime(String time) {
        mHeaderTimeLabel.setVisibility(VISIBLE);
        mHeaderTimeView.setText(time);
    }

    public void setVisiableHeight(int height) {
        if (height < 0)
            height = 0;
        LinearLayout.LayoutParams params = (LayoutParams) mContainer.getLayoutParams();
        params.height = height;
        mContainer.setLayoutParams(params);
    }

    public int getVisiableHeight(){
        return mContainer.getHeight();
    }
}
