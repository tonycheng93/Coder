package com.tony.coder.im.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tony.coder.R;
import com.tony.coder.im.utils.PixelUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/3/28 20:29
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

/**
 * 自定义头部布局
 */
public class HeaderLayout extends LinearLayout {
    private LayoutInflater mInflater;
    private View mHeader;

    @Bind(R.id.header_layout_leftview_container)
    LinearLayout mLayoutLeftContainer;
    @Bind(R.id.header_layout_rightview_container)
    LinearLayout mLayoutRightContainer;
    @Bind(R.id.header_htv_subtitle)
    TextView mHtvSubTitle;


    private LinearLayout mLayoutRightImageButtonLayout;
    private Button mRightImageButton;
    private onRightImageButtonClickListener mRightImageButtonClickListener;

    private LinearLayout mLayoutLeftImageButtonLayout;
    private ImageButton mLeftImageButton;
    private onLeftImageButtonClickListener mLeftImageButtonClickListener;

    public enum HeaderStyle {//头部整体样式
        DEFAULT_TITLE, TITLE_LIFT_IMAGEBUTTON, TITLE_RIGHT_IMAGEBUTTON, TITLE_DOUBLE_IMAGEBUTTON;
    }

    public HeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderLayout(Context context) {
        this(context, null, 0);
    }

    private void init(Context context) {
        mInflater = LayoutInflater.from(context);
        mHeader = mInflater.inflate(R.layout.common_header, null);

        addView(mHeader);

        initViews();
    }

    private void initViews() {
        ButterKnife.bind(this, mHeader);
    }

    public View findViewByHeaderId(int id) {
        return mHeader.findViewById(id);
    }

    public void init(HeaderStyle headerStyle) {
        switch (headerStyle) {
            case DEFAULT_TITLE:
                defaultTitle();
                break;
            case TITLE_LIFT_IMAGEBUTTON:
                defaultTitle();
                break;
            case TITLE_RIGHT_IMAGEBUTTON:
                defaultTitle();
                break;
            case TITLE_DOUBLE_IMAGEBUTTON:
                defaultTitle();
                titleLeftImageButton();
                titleRightImageButton();
                break;
            default:
                break;
        }
    }

    //右侧自定义按钮
    private void titleRightImageButton() {
        View mRightImageButtonView = mInflater.inflate(R.layout.common_header_rightbutton, null);
        mLayoutRightContainer.addView(mRightImageButtonView);

        mLayoutRightImageButtonLayout = (LinearLayout) mRightImageButtonView.
                findViewById(R.id.header_layout_imagebuttonlayout);
        mRightImageButton = (Button) mRightImageButtonView.findViewById(
                R.id.header_ib_imagebutton);
        mLayoutRightImageButtonLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRightImageButtonClickListener != null) {
                    mRightImageButtonClickListener.onClick();
                }
            }
        });
    }

    //左侧自定义按钮
    private void titleLeftImageButton() {
        View mLeftImageButtonView = mInflater.inflate(R.layout.common_header_button, null);
        mLayoutLeftContainer.addView(mLeftImageButtonView);

        mLayoutLeftImageButtonLayout = (LinearLayout) mLeftImageButtonView.findViewById(
                R.id.header_layout_imagebuttonlayout);
        mLeftImageButton = (ImageButton) mLeftImageButtonView.findViewById(R.id.header_ib_imagebutton);
        mLayoutLeftImageButtonLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLeftImageButtonClickListener != null) {
                    mLeftImageButtonClickListener.onClick();
                }
            }
        });
    }

    //默认标题
    private void defaultTitle() {
        mLayoutLeftContainer.removeAllViews();
        mLayoutRightContainer.removeAllViews();
    }

    public void setDefaultTitle(CharSequence title) {
        if (title != null) {
            mHtvSubTitle.setText(title);
        } else {
            mHtvSubTitle.setVisibility(GONE);
        }
    }

    /**
     * 获取右边按钮
     *
     * @return
     */
    public Button getRightImageButton() {
        if (mRightImageButton != null) {
            return mRightImageButton;
        }
        return null;
    }

    public void setTitleAndRightButton(CharSequence title, int backid, String text,
                                       onRightImageButtonClickListener onRightImageButtonClickListener) {
        setDefaultTitle(title);
        mLayoutRightContainer.setVisibility(VISIBLE);
        if (mRightImageButton != null && backid > 0) {
            mRightImageButton.setWidth(PixelUtil.dp2px(45));
            mRightImageButton.setHeight(PixelUtil.dp2px(40));
            mRightImageButton.setBackgroundResource(backid);
            mRightImageButton.setText(text);
            setOnRightImageButtonClickListener(onRightImageButtonClickListener);
        }
    }

    public void setTitleAndRightImageButton(CharSequence title, int backid,
                                            onRightImageButtonClickListener onRightImageButtonClickListener) {
        setDefaultTitle(title);
        mLayoutRightContainer.setVisibility(VISIBLE);
        if (mRightImageButton != null && backid > 0) {
            mRightImageButton.setWidth(PixelUtil.dp2px(30));
            mRightImageButton.setHeight(PixelUtil.dp2px(30));
            mRightImageButton.setTextColor(getResources().getColor(R.color.transparent));
            mRightImageButton.setBackgroundResource(backid);
            setOnRightImageButtonClickListener(onRightImageButtonClickListener);
        }
    }

    public void setTitleAndLeftImageButton(CharSequence title, int id,
                                           onLeftImageButtonClickListener listener) {
        setDefaultTitle(title);
        if (mLeftImageButton != null && id > 0) {
            mLeftImageButton.setImageResource(id);
            setOnLeftImageButtonClickListener(listener);
        }
        mLayoutRightContainer.setVisibility(View.INVISIBLE);
    }

    public interface onRightImageButtonClickListener {
        void onClick();
    }

    public void setOnRightImageButtonClickListener(
            onRightImageButtonClickListener listener) {
        mRightImageButtonClickListener = listener;
    }

    public interface onLeftImageButtonClickListener {
        void onClick();
    }

    public void setOnLeftImageButtonClickListener(
            onLeftImageButtonClickListener listener) {
        mLeftImageButtonClickListener = listener;
    }
}
