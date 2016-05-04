package com.tony.coder.im.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.tony.coder.R;
import com.tony.coder.im.CoderApplication;
import com.tony.coder.im.entity.User;
import com.tony.coder.im.sns.UserHelper;
import com.tony.coder.im.utils.CollectionUtils;
import com.tony.coder.im.widget.HeaderLayout;
import com.tony.coder.im.widget.dialog.DialogTips;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/3/28 20:00
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class BaseActivity extends FragmentActivity {

    public final String TAG = getClass().getName();

    BmobChatManager mChatManager;
    BmobUserManager mUserManager;

    CoderApplication mCoderApplication;
    protected HeaderLayout mHeaderLayout;

    protected Context mContext;

    protected int mScreenWidth;
    protected int mScreenHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserManager = BmobUserManager.getInstance(this);
        mChatManager = BmobChatManager.getInstance(this);
        mCoderApplication = CoderApplication.getInstance();

        mContext = this;
        mCoderApplication.addActivity(this);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }

    Toast mToast;

    public void showToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mToast == null) {
                        mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                    } else {
                        mToast.setText(text);
                    }
                    mToast.show();
                }
            });
        }
    }

    public void showToast(final int resId) {
        if (resId > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mToast == null) {
                        mToast = Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_LONG);
                    } else {
                        mToast.setText(resId);
                    }
                    mToast.show();
                }
            });
        }
    }

    /**
     * 打印log
     *
     * @param msg
     */
    public void showLog(String msg) {
        Logger.d("Coder", msg);
    }

    /**
     * 只有title initTopBarLayoutByTitle
     *
     * @param titleName
     */
    public void initTopBarForOnlyTitle(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle(titleName);
    }

    /**
     * 初始化标题栏-带左右侧按钮
     *
     * @param titleName
     * @param rightDrawableId
     * @param text
     * @param listener
     */
    public void initTopBarForBoth(String titleName, int rightDrawableId, String text,
                                  HeaderLayout.onRightImageButtonClickListener listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName, R.drawable.base_action_bar_back_bg_selector,
                new OnLeftButtonClickListener());
        mHeaderLayout.setTitleAndRightButton(titleName, rightDrawableId, text,
                listener);
    }

    public void initTopBarForBoth(String titleName, int rightDrawableId,
                                  HeaderLayout.onRightImageButtonClickListener listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.base_action_bar_back_bg_selector,
                new OnLeftButtonClickListener());
        mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId, listener);
    }

    /**
     * 只有左边按钮和Title initTopBarLayout
     *
     * @param titleName
     */
    public void initTopBarForLeft(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.base_action_bar_back_bg_selector,
                new OnLeftButtonClickListener());
    }

    public void showOfflineDialog(final Context context) {
        DialogTips dialogTips = new DialogTips(this, "您的帐号已在其它设备上登录！", "重新登录");
        //设置成功事件
        dialogTips.setOnSuccessListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                CoderApplication.getInstance().logout();
                startActivity(new Intent(context, LoginActivity.class));
                finish();
                dialogInterface.dismiss();
            }
        });
        //显示确认对话框
        dialogTips.show();
        dialogTips = null;
    }

    //左边按钮的点击事件
    private class OnLeftButtonClickListener implements HeaderLayout.onLeftImageButtonClickListener {
        @Override
        public void onClick() {
            finish();
        }
    }

    public void startAnimActivity(Class<?> clazz) {
        this.startActivity(new Intent(this, clazz));
    }

    public void startAnimActivity(Intent intent) {
        this.startActivity(intent);
    }

    /**
     * 用于登陆或者自动登陆情况下的用户资料及好友资料的检测更新
     */
    public void updateUserInfos() {
        //更新地理位置信息
        updateUserLocation();
        //查询该用户的好友列表(这个好友列表是去除黑名单用户的哦),目前支持的查询好友个数为100，
        // 如需修改请在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
        //这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
        mUserManager.queryCurrentContactList(new FindListener<BmobChatUser>() {
            @Override
            public void onSuccess(List<BmobChatUser> list) {
                //保存到application中方便比较
                CoderApplication.getInstance().setContactList(CollectionUtils.list2map(list));
            }

            @Override
            public void onError(int i, String s) {
                if (i == BmobConfig.CODE_COMMON_NONE) {
                    showLog(s);
                } else {
                    showLog("查询好友列表失败" + s);
                }
            }
        });
    }

    /**
     * 更新用户的经纬度信息
     */
    public void updateUserLocation() {
        if (CoderApplication.lastPoint != null) {
            String saveLatitude = mCoderApplication.getLatitude();
            String saveLongtitude = mCoderApplication.getLongtitude();
            String newLat = String.valueOf(CoderApplication.lastPoint.getLatitude());
            String newLon = String.valueOf(CoderApplication.lastPoint.getLongitude());
            if (!saveLatitude.equals(newLat) || saveLongtitude.equals(newLon)) {
                //只要位置有变化就更新当前位置，达到实时更新的目的
                User u = (User) mUserManager.getCurrentUser(User.class);
                final User user = new User();
                user.setLocation(CoderApplication.lastPoint);
                user.setObjectId(u.getObjectId());
                user.update(this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        CoderApplication.getInstance().setLatitude(String.valueOf(user.getLocation().getLatitude()));
                        CoderApplication.getInstance().setLongtitude(String.valueOf(user.getLocation().getLongitude()));
                        showLog("经纬度更新成功");
                    }

                    @Override
                    public void onFailure(int i, String msg) {
                        showLog("经纬度更新失败" + msg);
                    }
                });
            } else {
                showLog("用户位置未发生过变过");
            }
        }
    }

    public User getCurrentUser() {
        return UserHelper.getCurrentUser(mContext);
    }

}
