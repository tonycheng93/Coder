package com.tony.coder.im.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.tony.coder.R;
import com.tony.coder.im.utils.PixelUtil;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/15 13:50
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

/**
 * 通讯录右侧快速滚动栏
 */
public class LetterView extends View {
    //触摸事件
    private OnTouchingLetterChangedListener mOnTouchingLetterChangedListener;
    public static String[] b = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};

    private int choose = -1;//选中
    private Paint mPaint = new Paint();

    private TextView mTextDialog;

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    /**
     * 重写onDraw()方法
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取焦点改变背景颜色
        int height = getHeight();//获取对应高度
        int width = getWidth();//获取对应宽度
        int singleHeight = height / b.length;//获取每一个字母的高度

        for (int i = 0; i < b.length; i++) {
            mPaint.setColor(getResources().getColor(R.color.color_bottom_text_normal));
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(PixelUtil.sp2px(12));
            //选中的状态
            if (i == choose) {
                mPaint.setColor(Color.parseColor("#3399ff"));
                mPaint.setFakeBoldText(true);
            }
            //x坐标等于中间字符串宽度的一半
            float xPos = width / 2 - mPaint.measureText(b[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(b[i], xPos, yPos, mPaint);
            mPaint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float y = event.getY();//点击y坐标
        int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = mOnTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * b.length);//点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackgroundDrawable(new ColorDrawable(0x00000000));
                choose = -1;
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(INVISIBLE);
                }
                break;
            default:
                //设置右侧字母列表[A,B,C,D,E....]的背景颜色
                setBackgroundResource(R.drawable.v2_sortlistview_sidebar_background);
                if (oldChoose != c) {
                    if (c >= 0 && c < b.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(b[c]);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(b[c]);
                            mTextDialog.setVisibility(VISIBLE);
                        }

                        choose = c;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 向外公开的方法
     * @param onTouchingLetterChangedListener
     */
    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener
                                                           onTouchingLetterChangedListener) {
        mOnTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }


    public LetterView(Context context) {
        super(context);
    }

    public LetterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LetterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
