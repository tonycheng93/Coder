package com.tony.coder.im.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tony.coder.R;
import com.tony.coder.im.CoderApplication;
import com.tony.coder.im.ui.activity.BlackListActivity;
import com.tony.coder.im.ui.activity.LoginActivity;
import com.tony.coder.im.ui.activity.SetMyInfoActivity;
import com.tony.coder.im.utils.SharePreferenceUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.im.BmobUserManager;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/3/31 21:06
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class MeFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.btn_logout)
    Button btn_logout;
    @Bind(R.id.tv_set_name)
    TextView tv_set_name;
    @Bind(R.id.layout_info)
    RelativeLayout layout_info;
    @Bind(R.id.rl_switch_notification)
    RelativeLayout rl_switch_notification;
    @Bind(R.id.rl_switch_voice)
    RelativeLayout rl_switch_voice;
    @Bind(R.id.rl_switch_vibrate)
    RelativeLayout rl_switch_vibrate;
    @Bind(R.id.layout_blacklist)
    RelativeLayout layout_blacklist;
    @Bind(R.id.iv_open_notification)
    ImageView iv_open_notification;
    @Bind(R.id.iv_close_notification)
    ImageView iv_close_notification;
    @Bind(R.id.iv_open_voice)
    ImageView iv_open_voice;
    @Bind(R.id.iv_close_voice)
    ImageView iv_close_voice;
    @Bind(R.id.iv_open_vibrate)
    ImageView iv_open_vibrate;
    @Bind(R.id.iv_close_vibrate)
    ImageView iv_close_vibrate;
    @Bind(R.id.view1)
    View line1;
    @Bind(R.id.view2)
    View line2;

    SharePreferenceUtil mSharePreferenceUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharePreferenceUtil = mApplication.getSpUtil();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        tv_set_name.setText(BmobUserManager.getInstance(getActivity()).getCurrentUser().getUsername());
    }

    private void initView() {
        initTopBarForOnlyTitle("设置");

        rl_switch_notification.setOnClickListener(this);
        rl_switch_voice.setOnClickListener(this);
        rl_switch_vibrate.setOnClickListener(this);

        boolean isAllowNotify = mSharePreferenceUtil.isAllowPushNotify();

        if (isAllowNotify) {
            iv_open_notification.setVisibility(View.VISIBLE);
            iv_close_notification.setVisibility(View.INVISIBLE);
        } else {
            iv_open_notification.setVisibility(View.INVISIBLE);
            iv_close_notification.setVisibility(View.VISIBLE);
        }

        boolean isAllowVoice = mSharePreferenceUtil.isAllowVoice();
        if (isAllowVoice) {
            iv_open_voice.setVisibility(View.VISIBLE);
            iv_close_voice.setVisibility(View.INVISIBLE);
        } else {
            iv_open_voice.setVisibility(View.INVISIBLE);
            iv_close_voice.setVisibility(View.VISIBLE);
        }

        boolean isAllowVibrate = mSharePreferenceUtil.isAllowVibrate();
        if (isAllowVibrate) {
            iv_open_vibrate.setVisibility(View.VISIBLE);
            iv_close_vibrate.setVisibility(View.INVISIBLE);
        } else {
            iv_open_vibrate.setVisibility(View.VISIBLE);
            iv_close_vibrate.setVisibility(View.INVISIBLE);
        }

        btn_logout.setOnClickListener(this);
        layout_info.setOnClickListener(this);
        layout_blacklist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_blacklist://启动到黑名单页面
                startAnimActivity(new Intent(getActivity(), BlackListActivity.class));
                break;
            case R.id.layout_info://启动到个人资料页面
                Intent intent = new Intent(getActivity(), SetMyInfoActivity.class);
                intent.putExtra("from", "me");
                startActivity(intent);
                break;
            case R.id.btn_logout:
                CoderApplication.getInstance().logout();
                getActivity().finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.rl_switch_notification:
                if (iv_open_notification.getVisibility() == View.VISIBLE) {
                    iv_open_notification.setVisibility(View.INVISIBLE);
                    iv_close_notification.setVisibility(View.VISIBLE);
                    mSharePreferenceUtil.setPushNotifyEnable(false);
                    rl_switch_vibrate.setVisibility(View.GONE);
                    rl_switch_voice.setVisibility(View.GONE);
                    line1.setVisibility(View.GONE);
                    line2.setVisibility(View.GONE);
                } else {
                    iv_open_notification.setVisibility(View.VISIBLE);
                    iv_close_notification.setVisibility(View.INVISIBLE);
                    mSharePreferenceUtil.setPushNotifyEnable(true);
                    rl_switch_vibrate.setVisibility(View.VISIBLE);
                    rl_switch_voice.setVisibility(View.VISIBLE);
                    line1.setVisibility(View.VISIBLE);
                    line2.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rl_switch_voice:
                if (iv_open_voice.getVisibility() == View.VISIBLE){
                    iv_open_voice.setVisibility(View.INVISIBLE);
                    iv_close_voice.setVisibility(View.VISIBLE);
                    mSharePreferenceUtil.setAllowVoiceEnable(false);
                }else {
                    iv_open_voice.setVisibility(View.VISIBLE);
                    iv_close_voice.setVisibility(View.INVISIBLE);
                    mSharePreferenceUtil.setAllowVoiceEnable(true);
                }
                break;
            case R.id.rl_switch_vibrate:
                if (iv_open_vibrate.getVisibility() == View.VISIBLE) {
                    iv_open_vibrate.setVisibility(View.INVISIBLE);
                    iv_close_vibrate.setVisibility(View.VISIBLE);
                    mSharePreferenceUtil.setAllowVibrateEnable(false);
                } else {
                    iv_open_vibrate.setVisibility(View.VISIBLE);
                    iv_close_vibrate.setVisibility(View.INVISIBLE);
                    mSharePreferenceUtil.setAllowVibrateEnable(true);
                }
                break;
        }
    }
}
