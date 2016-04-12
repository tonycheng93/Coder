package com.tony.coder.im.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tony.coder.R;
import com.tony.coder.im.view.EmoticonsEditText;
import com.tony.coder.im.view.xlist.XListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.inteface.EventListener;

public class ChatActivity extends BaseActivity implements View.OnClickListener
        , XListView.IXListViewListener, EventListener {
    @Bind(R.id.mListView)
    XListView mListView;
    @Bind(R.id.btn_chat_add)
    Button btn_chat_add;
    @Bind(R.id.btn_chat_emo)
    Button btn_chat_emo;
    @Bind(R.id.edit_user_comment)
    EmoticonsEditText edit_user_comment;
    @Bind(R.id.btn_speak)
    Button btn_speak;
    @Bind(R.id.btn_chat_voice)
    Button btn_chat_voice;
    @Bind(R.id.btn_chat_keyboard)
    Button btn_chat_keyboard;
    @Bind(R.id.btn_chat_send)
    Button btn_chat_send;
    @Bind(R.id.pager_emo)
    ViewPager pager_emo;
    @Bind(R.id.layout_emo)
    LinearLayout layout_emo;
    @Bind(R.id.tv_picture)
    TextView tv_picture;
    @Bind(R.id.tv_camera)
    TextView tv_camera;
    @Bind(R.id.tv_location)
    TextView tv_location;
    @Bind(R.id.layout_more)
    LinearLayout layout_more;

    String targetId = "";
    BmobChatUser targetUser;

    private static int MsgPagerNum;

    //语音相关
    @Bind(R.id.layout_record)
    RelativeLayout layout_record;
    @Bind(R.id.iv_record)
    ImageView iv_record;
    @Bind(R.id.tv_voice_tips)
    TextView tv_voice_tips;

    private Drawable[] drawable_Anims;//话筒动画
    BmobRecordManager recordManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatManager = BmobChatManager.getInstance(this);
        MsgPagerNum = 0;
        //组装聊天对象
        targetUser = (BmobChatUser) getIntent().getSerializableExtra("user");
        targetId = targetUser.getObjectId();
        showLog("聊天对象：" + targetUser.getUsername() + ",targetId = " + targetId);
        //注册广播接收器
        initNewMessageBroadCast();

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {

    }

    private void initNewMessageBroadCast() {

    }

    private class NewBroadCastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String from = intent.getStringExtra("fromId");
            String msgId = intent.getStringExtra("msgId");
            String msgTime = intent.getStringExtra("msgTime");
            // 收到这个广播的时候，message已经在消息表中，可直接获取
            if (TextUtils.isEmpty(from) && TextUtils.isEmpty(msgId)
                    && TextUtils.isEmpty(msgTime)){
                BmobMsg msg = BmobChatManager.getInstance(ChatActivity.this).getMessage(msgId,msgTime);
                if ( ! from.equals(targetId)){// 如果不是当前正在聊天对象的消息，不处理
                    return;
                }
                //添加到当前页面

            }
        }
    }
    @Override
    public void onMessage(BmobMsg bmobMsg) {

    }

    @Override
    public void onReaded(String s, String s1) {

    }

    @Override
    public void onNetChange(boolean b) {

    }

    @Override
    public void onAddUser(BmobInvitation bmobInvitation) {

    }

    @Override
    public void onOffline() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onClick(View v) {

    }


}
