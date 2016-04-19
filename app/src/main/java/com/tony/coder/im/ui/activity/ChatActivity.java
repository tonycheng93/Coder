package com.tony.coder.im.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tony.coder.R;
import com.tony.coder.im.config.Constants;
import com.tony.coder.im.entity.FaceText;
import com.tony.coder.im.receiver.MessageReceiver;
import com.tony.coder.im.ui.adapter.EmoViewPagerAdapter;
import com.tony.coder.im.ui.adapter.EmoteAdapter;
import com.tony.coder.im.ui.adapter.MessageChatAdapter;
import com.tony.coder.im.ui.adapter.NewRecordPlayClickListener;
import com.tony.coder.im.util.CommonUtils;
import com.tony.coder.im.util.FaceTextUtils;
import com.tony.coder.im.view.EmoticonsEditText;
import com.tony.coder.im.view.HeaderLayout;
import com.tony.coder.im.view.dialog.DialogTips;
import com.tony.coder.im.view.xlist.XListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.im.inteface.UploadListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.PushListener;

public class ChatActivity extends ActivityBase implements View.OnClickListener
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

    private LinearLayout layout_add;

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
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);

        initTopBarForLeft("与" + targetUser.getUsername() + "对话");
        initBottomView();
        initXListView();
        initVoiceView();
    }

    /**
     * 初始化语音布局
     */
    private void initVoiceView() {
        btn_speak.setOnTouchListener(new VoiceTouchListenr());
        initVoiceAnimRes();
        initRecordManager();
    }

    private void initRecordManager() {
        //语音相关管理器
        recordManager = BmobRecordManager.getInstance(this);
        //设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
        recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {
            @Override
            public void onVolumnChanged(int i) {
                iv_record.setImageDrawable(drawable_Anims[i]);
            }

            @Override
            public void onTimeChanged(int recordTime, String localPath) {
                BmobLog.i("voice", "已录音长度:" + recordTime);
                if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {//一分钟结束，发送语音
                    //需要重置按钮
                    btn_speak.setPressed(false);
                    btn_speak.setClickable(false);
                    //取消录音框
                    layout_record.setVisibility(View.INVISIBLE);
                    //发送语音消息
                    sendVoiceMessage(localPath, recordTime);
                    //是为了防止过了录音时间后，会多发一条语音出去的情况。
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btn_speak.setClickable(true);
                        }
                    }, 1000);
                } else {

                }
            }
        });
    }

    private void initBottomView() {
        //最左边
        btn_chat_add.setOnClickListener(this);
        btn_chat_emo.setOnClickListener(this);
        //最右边
        btn_chat_voice.setOnClickListener(this);
        btn_chat_keyboard.setOnClickListener(this);
        btn_chat_send.setOnClickListener(this);
        //最下面
        layout_add = (LinearLayout) findViewById(R.id.layout_add);
        initAddView();
        initEmoView();

        //输入框
        edit_user_comment.setOnClickListener(this);
        edit_user_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btn_chat_send.setVisibility(View.VISIBLE);
                    btn_chat_keyboard.setVisibility(View.GONE);
                    btn_chat_voice.setVisibility(View.GONE);
                } else {
                    if (btn_chat_voice.getVisibility() != View.VISIBLE) {
                        btn_chat_voice.setVisibility(View.VISIBLE);
                        btn_chat_send.setVisibility(View.GONE);
                        btn_chat_keyboard.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    List<FaceText> emos;

    /**
     * 初始化表情布局
     */
    private void initEmoView() {
        emos = FaceTextUtils.faceTexts;

        List<View> views = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            views.add(getGridView(i));
        }
        pager_emo.setAdapter(new EmoViewPagerAdapter(views));
    }

    private View getGridView(final int i) {
        View view = View.inflate(this, R.layout.include_emo_gridview, null);
        GridView gridView = (GridView) view.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        } else if (i == 1) {
            list.addAll(emos.subList(21, emos.size()));
        }
        final EmoteAdapter gridAdapter = new EmoteAdapter(ChatActivity.this, list);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.text.toString();
                try {
                    if (edit_user_comment != null && !TextUtils.isEmpty(key)) {
                        int start = edit_user_comment.getSelectionStart();
                        CharSequence content = edit_user_comment.getText().insert(start, key);
                        edit_user_comment.setText(content);
                        //定位光标位置
                        CharSequence info = edit_user_comment.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText, start + key.length());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    private void initAddView() {
        tv_picture.setOnClickListener(this);
        tv_location.setOnClickListener(this);
        tv_camera.setOnClickListener(this);
    }

    NewBroadCastReceiver receiver;

    private void initNewMessageBroadCast() {
        //注册接受消息广播
        receiver = new NewBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        //设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(5);
        registerReceiver(receiver, intentFilter);
    }

    MessageChatAdapter mAdapter;

    private void initXListView() {
        //首页不允许加载更多
        mListView.setPullLoadEnable(false);
        //允许下拉
        mListView.setPullRefreshEnable(true);
        //设置监听器
        mListView.setXListViewListener(this);
        mListView.PullRefreshing();
        mListView.setDividerHeight(0);
        //加载数据
        initOrRefresh();
        mListView.setSelection(mAdapter.getCount() - 1);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftInputView();
                layout_more.setVisibility(View.GONE);
                layout_add.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.VISIBLE);
                btn_chat_keyboard.setVisibility(View.GONE);
                btn_chat_send.setVisibility(View.GONE);
                return false;
            }
        });
        //重发按钮的点击事件
        mAdapter.setOnInViewClickListener(R.id.iv_fail_resend, new MessageChatAdapter.onInternalClickListener() {

            @Override
            public void OnClickListener(View parentView, View view, Integer position, Object values) {
                //重发消息
                showResendDialog(parentView, view, values);
            }
        });
    }

    /**
     * 显示重发按钮
     *
     * @param parentView
     * @param view
     * @param values
     */
    private void showResendDialog(final View parentView, final View view, final Object values) {
        DialogTips dialog = new DialogTips(this, "确定重发该消息", "确定", "取消", "提示", true);
        //设置成功事件
        dialog.setOnSuccessListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_IMAGE
                        || ((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {
                    resendFileMsg(parentView, values);
                } else {
                    resendTextMsg(parentView, values);
                }
                dialogInterface.dismiss();
            }
        });
        //显示确认对话框
        dialog.show();
        dialog = null;
    }

    /**
     * 重发文本消息
     *
     * @param parentView
     * @param values
     */
    private void resendTextMsg(final View parentView, final Object values) {
        BmobChatManager.getInstance(ChatActivity.this).resendTextMessage(targetUser, (BmobMsg) values, new PushListener() {
            @Override
            public void onSuccess() {
                showLog("发送成功");
                ((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_SUCCESS);
                parentView.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
                parentView.findViewById(R.id.iv_fail_resend).setVisibility(View.INVISIBLE);
                parentView.findViewById(R.id.tv_send_status).setVisibility(View.VISIBLE);
                ((TextView) parentView.findViewById(R.id.tv_send_status)).setText("已发送");
            }

            @Override
            public void onFailure(int i, String s) {
                showLog("发送失败" + s);
                ((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_FAIL);
                parentView.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
                parentView.findViewById(R.id.iv_fail_resend).setVisibility(View.VISIBLE);
                parentView.findViewById(R.id.tv_send_status).setVisibility(View.INVISIBLE);
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 重发图片消息
     *
     * @param parentView
     * @param values
     */
    private void resendFileMsg(final View parentView, final Object values) {
        BmobChatManager.getInstance(ChatActivity.this).resendFileMessage(targetUser, (BmobMsg) values, new UploadListener() {
            @Override
            public void onStart(BmobMsg bmobMsg) {

            }

            @Override
            public void onSuccess() {
                ((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_SUCCESS);
                parentView.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
                parentView.findViewById(R.id.iv_fail_resend).setVisibility(View.INVISIBLE);
                if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {
                    parentView.findViewById(R.id.tv_send_status).setVisibility(View.GONE);
                    parentView.findViewById(R.id.tv_voice_length).setVisibility(View.VISIBLE);
                } else {
                    parentView.findViewById(R.id.tv_send_status).setVisibility(View.VISIBLE);
                    ((TextView) parentView.findViewById(R.id.tv_send_status)).setText("已发送");
                }
            }

            @Override
            public void onFailure(int i, String s) {
                ((BmobMsg) values)
                        .setStatus(BmobConfig.STATUS_SEND_FAIL);
                parentView.findViewById(R.id.progress_load).setVisibility(
                        View.INVISIBLE);
                parentView.findViewById(R.id.iv_fail_resend)
                        .setVisibility(View.VISIBLE);
                parentView.findViewById(R.id.tv_send_status)
                        .setVisibility(View.INVISIBLE);
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化语音动画资源
     */
    private void initVoiceAnimRes() {
        drawable_Anims = new Drawable[]{
                getResources().getDrawable(R.drawable.chat_icon_voice2),
                getResources().getDrawable(R.drawable.chat_icon_voice3),
                getResources().getDrawable(R.drawable.chat_icon_voice4),
                getResources().getDrawable(R.drawable.chat_icon_voice5),
                getResources().getDrawable(R.drawable.chat_icon_voice6),
        };
    }

    /**
     * 加载历史消息，从数据库中读出
     *
     * @return
     */
    private List<BmobMsg> initMsgData() {
        List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId, MsgPagerNum);
        return list;
    }

    /**
     * 界面刷新
     */
    private void initOrRefresh() {
        if (mAdapter != null) {
            if (MessageReceiver.mNewNum != 0) {//用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
                int news = MessageReceiver.mNewNum;//有可能锁屏期间，来了N条消息,因此需要倒叙显示在界面上
                int size = initMsgData().size();
                for (int i = (news - 1); i >= 0; i--) {
                    mAdapter.add(initMsgData().get(size - (i + 1)));//添加最后一条消息到界面显示
                }
                mListView.setSelection(mAdapter.getCount() - 1);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mAdapter = new MessageChatAdapter(this, initMsgData());
            mListView.setAdapter(mAdapter);
        }
    }

    private class NewBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String from = intent.getStringExtra("fromId");
            String msgId = intent.getStringExtra("msgId");
            String msgTime = intent.getStringExtra("msgTime");
            // 收到这个广播的时候，message已经在消息表中，可直接获取
            if (!TextUtils.isEmpty(from) && !TextUtils.isEmpty(msgId)
                    && !TextUtils.isEmpty(msgTime)) {
                BmobMsg msg = BmobChatManager.getInstance(ChatActivity.this).getMessage(msgId, msgTime);
                if (!from.equals(targetId)) {// 如果不是当前正在聊天对象的消息，不处理
                    return;
                }
                //添加到当前页面
                mAdapter.add(msg);
                //定位
                mListView.setSelection(mAdapter.getCount() - 1);
                //取消当前聊天对象的未读标示
                BmobDB.create(ChatActivity.this).resetUnread(targetId);
            }
            //记得把广播终结掉
            abortBroadcast();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //新消息到达，重新刷新界面
        initOrRefresh();
        MessageReceiver.sEventListeners.add(this);//监听推送的消息
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知和清空未读消息数
        BmobNotifyManager.getInstance(this).cancelNotify();
        BmobDB.create(this).resetUnread(targetId);
        //清空消息未读数-这个要在刷新之后
        MessageReceiver.mNewNum = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageReceiver.sEventListeners.remove(this);//监听推送消息
        //停止录音
        if (recordManager.isRecording()) {
            recordManager.cancelRecording();
            layout_record.setVisibility(View.GONE);
        }
        //停止播放录音
        if (NewRecordPlayClickListener.isPlaying
                && NewRecordPlayClickListener.currentPlayListener != null) {
            NewRecordPlayClickListener.currentPlayListener.stopPlayRecord();
        }
    }

    @Override
    public void onMessage(BmobMsg bmobMsg) {
        Message handlerMessage = handler.obtainMessage(NEW_MESSAGE);
        handlerMessage.obj = bmobMsg;
        handler.sendMessage(handlerMessage);
    }

    @Override
    public void onReaded(String conversionId, String msgTime) {
        //此处应该过滤掉不是和当前用户的聊天的回执消息界面的刷新
        if (conversionId.split("&")[1].equals(targetId)) {
            //修改街面上指定消息的阅读状态
            for (BmobMsg msg : mAdapter.getList()) {
                if (msg.getConversationId().equals(conversionId)
                        && msg.getMsgTime().equals(msgTime)) {
                    msg.setStatus(BmobConfig.STATUS_SEND_RECEIVERED);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        if (!isNetConnected) {
            showToast(R.string.network_tips);
        }
    }

    @Override
    public void onAddUser(BmobInvitation bmobInvitation) {

    }

    @Override
    public void onOffline() {
        showOfflineDialog(this);
    }

    @Override
    public void onRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MsgPagerNum++;
                int total = BmobDB.create(ChatActivity.this).queryChatTotalCount(targetId);
                BmobLog.i("记录总数：" + total);
                int currents = mAdapter.getCount();
                if (total <= currents) {
                    showToast("聊天记录加载完了哦！");
                } else {
                    List<BmobMsg> msgList = initMsgData();
                    mAdapter.setList(msgList);
                    mListView.setSelection(mAdapter.getCount() - currents - 1);
                }
                mListView.stopRefresh();
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (layout_more.getVisibility() == View.VISIBLE) {
                layout_more.setVisibility(View.GONE);
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideSoftInputView();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_user_comment://点击文本输入框
                mListView.setSelection(mListView.getCount() - 1);
                if (layout_more.getVisibility() == View.VISIBLE) {
                    layout_add.setVisibility(View.GONE);
                    layout_emo.setVisibility(View.GONE);
                    layout_more.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_chat_emo://点击笑脸图标
                if (layout_more.getVisibility() == View.GONE) {
                    showEditState(true);
                } else {
                    if (layout_add.getVisibility() == View.VISIBLE) {
                        layout_add.setVisibility(View.GONE);
                        layout_emo.setVisibility(View.VISIBLE);
                    } else {
                        layout_more.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.btn_chat_add://添加按钮-显示图片、拍照、位置
                if (layout_more.getVisibility() == View.GONE) {
                    layout_more.setVisibility(View.VISIBLE);
                    layout_add.setVisibility(View.VISIBLE);
                    layout_emo.setVisibility(View.GONE);
                    hideSoftInputView();
                } else {
                    if (layout_emo.getVisibility() == View.VISIBLE) {
                        layout_emo.setVisibility(View.GONE);
                        layout_add.setVisibility(View.VISIBLE);
                    } else {
                        layout_more.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.btn_chat_voice://语音按钮
                edit_user_comment.setVisibility(View.GONE);
                layout_more.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.GONE);
                btn_chat_keyboard.setVisibility(View.VISIBLE);
                btn_speak.setVisibility(View.VISIBLE);
                hideSoftInputView();
                break;
            case R.id.btn_chat_keyboard://键盘按钮，点击就弹出键盘并隐藏掉声音按钮
                showEditState(false);
                break;
            case R.id.btn_chat_send://发送文本
                final String msg = edit_user_comment.getText().toString();
                if (msg.equals("")) {
                    showToast("发送消息不能为空！");
                    return;
                }
                boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
                if (!isNetConnected) {
                    showToast(R.string.network_tips);
                }
                //组装BmobMessage对象
                BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
                message.setExtra("Bmob");
                //// 默认发送完成，将数据保存到本地消息表和最近会话表中
                mChatManager.sendTextMessage(targetUser, message);
                //刷新界面
                refreshMessage(message);
                break;
            case R.id.tv_camera://拍照
                selectImageFromCamera();
                break;
            case R.id.tv_picture://图片
                selectImageFromLocal();
                break;
            case R.id.tv_location://位置
                selectLocationFromMap();
                break;
            default:
                break;
        }
    }

    /**
     * 启动地图
     */
    private void selectLocationFromMap() {
        Intent intent = new Intent(this, LocationActivity.class);
        intent.putExtra("type", "select");
        startActivityForResult(intent, Constants.REQUESTCODE_TAKE_LOCATION);
    }

    private String localCameraPath = "";//拍照后得到的图片地址

    /**
     * 启动相机拍照
     */
    private void selectImageFromCamera() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = new File(Constants.CODER_PICTURE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jpg");
        localCameraPath = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, Constants.REQUESTCODE_TAKE_CAMERA);
    }

    /**
     * 选择图片
     */
    private void selectImageFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, Constants.REQUESTCODE_TAKE_LOCAL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUESTCODE_TAKE_CAMERA://当取到值的时候才上传path路径下的图片到服务器
                    showLog("本地图片地址:" + localCameraPath);
                    sendImageMessage(localCameraPath);
                    break;
                case Constants.REQUESTCODE_TAKE_LOCAL:
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex("_data");
                            String localSelectPath = cursor.getString(columnIndex);
                            cursor.close();
                            if (localSelectPath == null ||
                                    localSelectPath.equals("null")) {
                                showToast("找不到您想要的图片");
                                return;
                            }
                            sendImageMessage(localSelectPath);
                        }
                    }
                    break;
                case Constants.REQUESTCODE_TAKE_LOCATION://地理位置
                    double latitude = data.getDoubleExtra("x", 0);
                    double longtitude = data.getDoubleExtra("y", 0);
                    String address = data.getStringExtra("address");
                    if (address != null && !address.equals("")) {
                        sendLocationMessage(address, latitude, longtitude);
                    } else {
                        showToast("无法获取到您当前的位置信息");
                    }
                    break;
            }
        }
    }

    /**
     * 发送位置信息
     *
     * @param address
     * @param latitude
     * @param longtitude
     */
    private void sendLocationMessage(String address, double latitude, double longtitude) {
        if (layout_more.getVisibility() == View.VISIBLE) {
            layout_more.setVisibility(View.GONE);
            layout_add.setVisibility(View.GONE);
            layout_emo.setVisibility(View.GONE);
        }
        //组装BmobMessage对象
        BmobMsg message = BmobMsg.createLocationSendMsg(this, targetId, address, latitude, longtitude);
        // 默认发送完成，将数据保存到本地消息表和最近会话表中
        mChatManager.sendTextMessage(targetUser, message);
        //刷新界面
        refreshMessage(message);
    }

    /**
     * 默认先上传本地图片，之后才显示出来
     *
     * @param local
     */
    private void sendImageMessage(String local) {
        if (layout_more.getVisibility() == View.VISIBLE) {
            layout_more.setVisibility(View.GONE);
            layout_add.setVisibility(View.GONE);
            layout_emo.setVisibility(View.GONE);
        }
        mChatManager.sendImageMessage(targetUser, local, new UploadListener() {
            @Override
            public void onStart(BmobMsg bmobMsg) {
                showLog("开始上传onStart：" + bmobMsg.getContent() + ",状态：" + bmobMsg.getStatus());
                refreshMessage(bmobMsg);
            }

            @Override
            public void onSuccess() {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int i, String s) {
                showLog("上传失败 -->s:" + s);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 根据是否点击笑脸来显示文本输入框的状态
     *
     * @param isEmo
     */
    private void showEditState(boolean isEmo) {
        edit_user_comment.setVisibility(View.VISIBLE);
        btn_chat_keyboard.setVisibility(View.GONE);
        btn_chat_voice.setVisibility(View.VISIBLE);
        btn_speak.setVisibility(View.GONE);
        edit_user_comment.requestFocus();
        if (isEmo) {
            layout_more.setVisibility(View.VISIBLE);
            layout_emo.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.GONE);
            hideSoftInputView();
        } else {
            layout_more.setVisibility(View.GONE);
            showSoftInputView();
        }
    }

    /**
     * 显示软键盘
     */
    private void showSoftInputView() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .showSoftInput(edit_user_comment, 0);
            }
        }
    }

    /**
     * 长按说话
     */
    private class VoiceTouchListenr implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!CommonUtils.checkSDCard()) {
                        showToast("发送语音需要SD卡的支持");
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        layout_record.setVisibility(View.VISIBLE);
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        //开始录音
                        recordManager.startRecording(targetId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (event.getY() < 0) {
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        tv_voice_tips.setTextColor(Color.RED);
                    } else {
                        tv_voice_tips.setText(getString(R.string.voice_up_tips));
                        tv_voice_tips.setTextColor(Color.WHITE);
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    layout_record.setVisibility(View.INVISIBLE);
                    try {
                        if (event.getY() < 0) {//放弃录音
                            recordManager.cancelRecording();
                            BmobLog.i("voice", "放弃发送语音");
                        } else {
                            int recordTime = recordManager.stopRecording();
                            if (recordTime > 1) {
                                //发送语音文件
                                BmobLog.i("voice", "发送语音");
                                sendVoiceMessage(recordManager.getRecordFilePath(targetId),
                                        recordTime);
                            } else {//录音时间过短，则提示录音时间过短的提示
                                layout_record.setVisibility(View.GONE);
                                showShortToast().show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    Toast mToast;

    /**
     * 显示时间过短的通知
     */
    private Toast showShortToast() {
        if (mToast == null) {
            mToast = new Toast(this);
        }
        View view = LayoutInflater.from(this).inflate(R.layout.include_chat_voice_short, null);
        mToast.setView(view);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        return mToast;
    }

    /**
     * 发送语音消息
     *
     * @param local
     * @param length
     */
    private void sendVoiceMessage(String local, int length) {
        mChatManager.sendVoiceMessage(targetUser, local, length, new UploadListener() {
            @Override
            public void onStart(BmobMsg bmobMsg) {
                refreshMessage(bmobMsg);
            }

            @Override
            public void onSuccess() {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int i, String s) {
                showLog("上传语音失败 -->s：" + s);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void refreshMessage(BmobMsg msg) {
        //界面更新
        mAdapter.add(msg);
        mListView.setSelection(mAdapter.getCount() - 1);
        edit_user_comment.setText("");
    }

    public static final int NEW_MESSAGE = 0x001;//收到消息
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == NEW_MESSAGE) {
                BmobMsg message = (BmobMsg) msg.obj;
                String uid = message.getBelongId();
                BmobMsg m = BmobChatManager.getInstance(ChatActivity.this).getMessage(message.getConversationId(), message.getMsgTime());
                if (!uid.equals(targetId)) {//如果不是当前正在聊天的对象，不处理
                    return;
                }
                mAdapter.add(m);
                //定位
                mListView.setSelection(mAdapter.getCount() - 1);
                //取消当前聊天对象的未读标示
                BmobDB.create(ChatActivity.this).resetUnread(targetId);
            }
        }
    };
}
