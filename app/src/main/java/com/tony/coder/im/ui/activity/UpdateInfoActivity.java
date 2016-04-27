package com.tony.coder.im.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.tony.coder.R;
import com.tony.coder.im.entity.User;
import com.tony.coder.im.widget.TitleBarBuilder;

import cn.bmob.v3.listener.UpdateListener;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/15 17:22
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class UpdateInfoActivity extends BaseActivity {

    private EditText edit_nick;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        initView();
    }

    private void initView() {
//        initTopBarForBoth("修改昵称", R.drawable.base_action_bar_true_bg_selector,
//                new HeaderLayout.onRightImageButtonClickListener() {
//                    @Override
//                    public void onClick() {
//                        String nick = edit_nick.getText().toString();
//                        if (nick.equals("")) {
//                            showToast("请填写昵称！");
//                            return;
//                        }
//                        updateInfo(nick);
//                    }
//                });

        new TitleBarBuilder(UpdateInfoActivity.this)
                .setTitleBgRes(R.drawable.top_bar)
                .setTitleText("修改昵称")
                .setLeftImage(R.drawable.base_action_bar_back_bg_selector)
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setRightImage(R.drawable.base_action_bar_true_bg_selector)
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nick = edit_nick.getText().toString();
                        if (nick.equals("")) {
                            showToast("请填写昵称！");
                            return;
                        }
                        updateInfo(nick);
                    }
                })
                .build();
        edit_nick = (EditText) findViewById(R.id.edit_nick);
    }

    /**
     * 修改资料
     *
     * @param nick
     */
    private void updateInfo(String nick) {
        final User user = mUserManager.getCurrentUser(User.class);
        User u = new User();
        u.setNick(nick);
        u.setHight(110);
        u.setObjectId(user.getObjectId());
        u.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                final User c = mUserManager.getCurrentUser(User.class);
                showToast("修改成功：" + c.getNick() + ",height = " + c.getHight());
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                showToast("onFailure:" + s);
            }
        });
    }
}
