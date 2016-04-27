package com.tony.coder.im.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tony.coder.R;
import com.tony.coder.im.common.Constants;
import com.tony.coder.im.entity.User;
import com.tony.coder.im.utils.CommonUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.im.bean.BmobChatInstallation;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity {

    @Bind(R.id.et_username)
    EditText et_username;
    @Bind(R.id.et_password)
    EditText et_password;
    @Bind(R.id.et_email)
    EditText et_email;
    @Bind(R.id.btn_register)
    Button btn_register;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        initTopBarForLeft("注册");

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register() {
        String name = et_username.getText().toString();
        String password = et_password.getText().toString();
        String pwd_again = et_email.getText().toString();
        if (TextUtils.isEmpty(name)) {
            showToast(R.string.toast_error_username_null);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast(R.string.toast_error_password_null);
            return;
        }
        if (!pwd_again.equals(password)) {
            showToast(R.string.toast_error_comfirm_password);
            return;
        }
        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if (!isNetConnected) {
            showToast(R.string.network_tips);
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("正在注册...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        //由于每个应用的注册所需的资料都不一样，故IM sdk未提供注册方法，用户可按照bmod SDK的注册方式进行注册。
        //注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
        final User user = new User();
        user.setUsername(name);
        user.setPassword(password);
        //将user和设备id进行绑定aa
        user.setSex(true);
        user.setDeviceType("android");
        user.setInstallId(BmobChatInstallation.getInstallationId(this));
        user.signUp(RegisterActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                showToast("注册成功");
                //将设备与username进行绑定
                mUserManager.bindInstallationForRegister(user.getUsername());
                //更新地理位置信息
                updateUserLocation();
                //发送广播通知和登录页面退出
                sendBroadcast(new Intent(Constants.ACTION_REGISTER_SUCCESS_FINISH));
                //启动主页
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                BmobLog.i(s);
                showToast("注册失败：" + s);
                progressDialog.dismiss();
            }
        });
    }
}
