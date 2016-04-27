package com.tony.coder.im.ui.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tony.coder.R;
import com.tony.coder.im.common.Constants;
import com.tony.coder.im.entity.User;
import com.tony.coder.im.utils.CommonUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.et_username)
    EditText et_userName;
    @Bind(R.id.et_password)
    EditText et_password;
    @Bind(R.id.btn_login)
    Button btn_login;
    @Bind(R.id.btn_register)
    TextView btn_register;

    private MyBroadcastReceiver mReceiver = new MyBroadcastReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        //注册退出广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_REGISTER_SUCCESS_FINISH);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onClick(View view) {
        if (view == btn_register) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        } else {
            boolean isNetConnected = CommonUtils.isNetworkAvailable(LoginActivity.this);
            if (!isNetConnected) {
                showToast(R.string.network_tips);
                return;
            }
            login();
        }
    }

    private void login() {
        String name = et_userName.getText().toString();
        String password = et_password.getText().toString();
        if (TextUtils.isEmpty(name)) {
            showToast(R.string.toast_error_username_null);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast(R.string.toast_error_password_null);
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("正在登录...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        User user = new User();
        user.setUsername(name);
        user.setPassword(password);
        mUserManager.login(user, new SaveListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setMessage("正在获取好友列表...");
                    }
                });
                //更新用户的地理位置以及好友的资料
                updateUserInfos();
                progressDialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                progressDialog.dismiss();
                BmobLog.i(s);
                showToast(s);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null &&
                    Constants.ACTION_REGISTER_SUCCESS_FINISH.equals(intent.getAction())) {
                finish();
            }
        }
    }
}
