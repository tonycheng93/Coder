package com.tony.coder.im.view.dialog;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/3/29 15:47
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tony.coder.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 自定义对话框基类
 * 支持：对话框全屏显示控制、title显示控制，一个button或两个
 */
public abstract class DialogBase extends Dialog {

    @Bind(R.id.dialog_top)
    LinearLayout dialog_top;
    @Bind(R.id.title_red_line)
    View title_red_line;
    @Bind(R.id.dialog_title)
    TextView titleTextView;
    @Bind(R.id.dialog_message)
    TextView messageTextView;
    @Bind(R.id.dialog_custom)
    FrameLayout custom;
    @Bind(R.id.dialog_contentPanel)
    LinearLayout contentPanel;
    @Bind(R.id.dialog_customPanel)
    FrameLayout customPanel;
    @Bind(R.id.dialog_positivebutton)
    Button positiveButton;
    @Bind(R.id.dialog_negativebutton)
    Button negativeButton;
    @Bind(R.id.dialog_leftspacer)
    LinearLayout leftSpacer;
    @Bind(R.id.dialog_rightspacer)
    LinearLayout rightSpacer;


    protected OnClickListener onSuccessListener;
    protected Context mainContext;
    protected OnClickListener onCancelListener;
    protected OnDismissListener onDismissListener;

    protected View mView;
    private boolean isFullScreen = false;

    private boolean hasTitle = true;//是否有title

    private int width = 0, height = 0, x = 0, y = 0;
    private int iconTitle = 0;
    private String message, title;
    private String namePositiveButton, nameNegativeButton;
    private final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;

    private boolean isCancel = true;//默认是否可点击back按键/点击外部区域取消对话框

    public boolean isCancel() {
        return isCancel;
    }

    public void setIsCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }

    public DialogBase(Context context) {
        super(context, R.style.alert);
        this.mainContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_base);
        this.onBuilding();
        //设置标题和消息
        ButterKnife.bind(this);
        //是否有title
        if (hasTitle) {
            dialog_top.setVisibility(View.VISIBLE);
            title_red_line.setVisibility(View.VISIBLE);
        } else {
            dialog_top.setVisibility(View.GONE);
            title_red_line.setVisibility(View.GONE);
        }

        titleTextView.setText(this.getTitle());
        messageTextView.setText(this.getMessage());

        if (mView != null) {
            custom.addView(mView, new WindowManager.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            contentPanel.setVisibility(View.GONE);
        } else {
            customPanel.setVisibility(View.GONE);
        }
        //设置按钮事件监听
        if (namePositiveButton != null && nameNegativeButton.length() > 0) {
            positiveButton.setText(namePositiveButton);
            positiveButton.setOnClickListener(getPositiveButtonClickListener());
        } else {
            positiveButton.setVisibility(View.GONE);
            leftSpacer.setVisibility(View.VISIBLE);
            rightSpacer.setVisibility(View.VISIBLE);
        }
        if (nameNegativeButton != null && nameNegativeButton.length() > 0) {
            negativeButton.setText(nameNegativeButton);
            negativeButton.setOnClickListener(getNegativeButtonClickListener());
        } else {
            negativeButton.setVisibility(View.GONE);
        }
        //设置对话框的位置和大小
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        if (this.getWidth() > 0)
            params.width = this.getWidth();
        if (this.getHeight() > 0)
            params.height = this.getHeight();
        if (this.getX() > 0)
            params.width = this.getX();
        if (this.getY() > 0)
            params.height = this.getY();

        //如果设置为全屏
        if (isFullScreen) {
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
        }
        //设置点击dialog外部区域可取消
        if (isCancel) {
            setCanceledOnTouchOutside(true);
            setCancelable(true);
        } else {
            setCanceledOnTouchOutside(false);
            setCancelable(false);
        }
        getWindow().setAttributes(params);
        this.setOnDismissListener(getOnDismissListener());
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED);
    }

    /**
     * 获取OnDismiss事件监听，释放资源
     *
     * @return
     */
    private OnDismissListener getOnDismissListener() {
        return new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                DialogBase.this.onDismiss();
                DialogBase.this.setOnDismissListener(null);
                mView = null;
                mainContext = null;
                positiveButton = null;
                negativeButton = null;
                if (onDismissListener != null) {
                    onDismissListener.onDismiss(null);
                }
            }
        };
    }

    /**
     * 关闭方法，用于子类定制
     */
    protected abstract void onDismiss();


    public LinearLayout getDialog_top() {
        return dialog_top;
    }

    public void setDialog_top(LinearLayout dialog_top) {
        this.dialog_top = dialog_top;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return 对话框Y坐标
     */
    private int getY() {
        return y;
    }

    /**
     * @return 对话框X坐标
     */
    private int getX() {
        return x;
    }

    private int getHeight() {
        return height;
    }

    /**
     * @return 对话框宽度
     */
    private int getWidth() {
        return width;
    }

    /**
     * 获取取消按钮单击事件监听
     *
     * @return 取消按钮单击事件监听
     */
    protected View.OnClickListener getNegativeButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickNegativeButton();
                DialogBase.this.dismiss();
            }
        };
    }

    /**
     * 获取焦点改变事件监听，设置EditText文本默认全选
     *
     * @return 焦点改变事件监听
     */
    protected View.OnFocusChangeListener getOnFocusChangeListener() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus && view instanceof EditText) {
                    ((EditText) view).setSelection(0, ((EditText) view).getText().length());
                }
            }
        };
    }

    public OnClickListener getOnSuccessListener() {
        return onSuccessListener;
    }

    /**
     * 设置成功事件监听，用于提供给调用者的回调函数
     *
     * @param onSuccessListener 成功事件监听
     */
    public void setOnSuccessListener(OnClickListener onSuccessListener) {
        this.onSuccessListener = onSuccessListener;
    }

    /**
     * 设置关闭事件监听，用于提供给调用者的回调函数
     *
     * @param onDismissListener
     */
    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }


    public OnClickListener getOnCancelListener() {
        return onCancelListener;
    }

    /**
     * 提供给取消按钮，用于实现类定制
     *
     * @return
     */
    public void setOnCancelListener(OnClickListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    /**
     * 取消按钮单击方法，用于子类定制
     */
    protected abstract void OnClickNegativeButton();

    /**
     * 获取确认按钮单击事件监听
     *
     * @return 确认按钮单击事件监听
     */
    protected View.OnClickListener getPositiveButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OnClickPositiveButton()) {
                    DialogBase.this.dismiss();
                }
            }
        };
    }

    /**
     * 确认按钮单击方法，用于子类定制
     */
    protected abstract boolean OnClickPositiveButton();

    /**
     * @return 对话框提示信息
     */
    private String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return 对话框标题
     */
    private String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIconTitle() {
        return iconTitle;
    }

    /**
     * iconTitle 标题图标的资源Id
     * @param iconTitle
     */
    public void setIconTitle(int iconTitle) {
        this.iconTitle = iconTitle;
    }

    public View getView() {
        return mView;
    }

    /**
     * 对话框View
     * @param view
     */
    public void setView(View view) {
        mView = view;
    }

    /**
     * 是否全屏
     * @return
     */
    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setIsFullScreen(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
    }

    public boolean isHasTitle() {
        return hasTitle;
    }

    public void setHasTitle(boolean hasTitle) {
        this.hasTitle = hasTitle;
    }

    /**
     * 确认按钮名称
     * @return
     */
    public String getNamePositiveButton() {
        return namePositiveButton;
    }

    public void setNamePositiveButton(String namePositiveButton) {
        this.namePositiveButton = namePositiveButton;
    }

    /**
     * 取消按钮名称
     * @return
     */
    public String getNameNegativeButton() {
        return nameNegativeButton;
    }

    public void setNameNegativeButton(String nameNegativeButton) {
        this.nameNegativeButton = nameNegativeButton;
    }

    /**
     * 创建方法，用于子类定制创建过程
     */
    protected abstract void onBuilding();
}
