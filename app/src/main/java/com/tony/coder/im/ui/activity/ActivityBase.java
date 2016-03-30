package com.tony.coder.im.ui.activity;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/3/30 15:44
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import cn.bmob.im.BmobUserManager;

/**
 * 除登陆注册和欢迎页面外继承的基类-用于检测是否有其他设备登录了同一账号
 */
public class ActivityBase extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //自动登录状态下检测是否在其他设备登录
        checkLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //锁屏状态下的检测
        checkLogin();
    }

    private void checkLogin() {
        BmobUserManager userManager = BmobUserManager.getInstance(this);
        if (userManager.getCurrentUser() == null) {
            showToast("您的账号已在其他设备上登录!");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    public void hideSoftInputView() {
        InputMethodManager manager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        if (getWindow().getAttributes().softInputMode
                != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
