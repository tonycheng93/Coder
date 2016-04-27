package com.tony.coder.im.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.tony.coder.R;
import com.tony.coder.im.CoderApplication;
import com.tony.coder.im.ui.activity.HomeActivity;
import com.tony.coder.im.ui.activity.NewFriendActivity;
import com.tony.coder.im.utils.CollectionUtils;
import com.tony.coder.im.utils.CommonUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnReceiveListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/3/31 10:20
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

/**
 * 推送消息接收器
 */
public class MessageReceiver extends BroadcastReceiver {

    //事件监听
    public static ArrayList<EventListener> sEventListeners = new ArrayList<>();

    public static final int NOTIFY_ID = 0x000;
    public static int mNewNum = 0;
    BmobUserManager mUserManager;
    BmobChatUser currentUser;

    //如果你想发送自定义格式的消息，请使用sendJsonMessage方法来发送Json格式的字符串，然后你按照格式自己解析并处理

    @Override
    public void onReceive(Context context, Intent intent) {
        String json = intent.getStringExtra("msg");
        BmobLog.i("收到的message = " + json);

        mUserManager = BmobUserManager.getInstance(context);
        currentUser = mUserManager.getCurrentUser();
        boolean isNetConnected = CommonUtils.isNetworkAvailable(context);
        if (isNetConnected) {
            parseMessage(context, json);
        } else {
            for (int i = 0; i < sEventListeners.size(); i++) {
                ((EventListener) sEventListeners.get(i)).onNetChange(isNetConnected);
            }
        }
    }

    /**
     * 解析Json字符串
     *
     * @param context
     * @param json
     */
    private void parseMessage(final Context context, String json) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            String tag = BmobJsonUtil.getString(jsonObject, BmobConstant.PUSH_KEY_TAG);
            if (tag.equals(BmobConfig.TAG_OFFLINE)) {//下线通知
                if (currentUser != null) {
                    if (sEventListeners.size() > 0) {//有监听的时候传递下去
                        for (EventListener handler : sEventListeners) {
                            handler.onOffline();
                        }
                    } else {
                        //清空数据
                        CoderApplication.getInstance().logout();
                    }
                }
            } else {
                String fromId = BmobJsonUtil.getString(jsonObject, BmobConstant.PUSH_KEY_TARGETID);
                //增加消息接收方的ObjectId--目的是解决多账户登陆同一设备时，无法接收到非当前登陆用户的消息。
                final String toId = BmobJsonUtil.getString(jsonObject, BmobConstant.PUSH_KEY_TOID);
                String msgTime = BmobJsonUtil.getString(jsonObject, BmobConstant.PUSH_READED_MSGTIME);
                if (fromId != null && !BmobDB.create(context, toId).isBlackUser(fromId)) {//该消息发送方不为黑名单用户
                    if (TextUtils.isEmpty(tag)) {//不携带tag标签--此可接收陌生人的消息
                        BmobChatManager.getInstance(context).createReceiveMsg(json, new OnReceiveListener() {
                            @Override
                            public void onSuccess(BmobMsg bmobMsg) {
                                if (sEventListeners.size() > 0) {// 有监听的时候，传递下去
                                    for (int i = 0; i < sEventListeners.size(); i++) {
                                        ((EventListener) sEventListeners.get(i)).onMessage(bmobMsg);
                                    }
                                } else {
                                    boolean isAllow = CoderApplication.getInstance().getSpUtil().isAllowPushNotify();
                                    if (isAllow && currentUser != null && currentUser.getObjectId().equals(toId)) {//当前登陆用户存在并且也等于接收方id
                                        mNewNum++;
                                        showMsgNotify(context, bmobMsg);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                BmobLog.i("获取接收的消息失败：" + s);
                            }
                        });
                    } else {//带Tag标签
                        if (tag.equals(BmobConfig.TAG_ADD_CONTACT)) {
                            //保存好友请求到本地，并更新后台的未读字段
                            BmobInvitation message = BmobChatManager.getInstance(context).saveReceiveInvite(json, toId);
                            if (currentUser != null) {//有用户登录
                                if (toId.equals(currentUser.getObjectId())) {
                                    if (sEventListeners.size() > 0) {//有监听的时候传递下去
                                        for (EventListener handler : sEventListeners) {
                                            handler.onAddUser(message);
                                        }
                                    } else {
                                        showOtherNotify(context, message.getFromname(), toId, message.getFromname() + "请求添加好友",
                                                NewFriendActivity.class);
                                    }
                                }
                            }
                        } else if (tag.equals(BmobConfig.TAG_ADD_AGREE)) {
                            String username = BmobJsonUtil.getString(jsonObject, BmobConstant.PUSH_KEY_TARGETUSERNAME);
                            //收到对方的同意请求之后，就得添加对方为好友--已默认添加同意方为好友，并保存到本地好友数据库
                            BmobUserManager.getInstance(context).addContactAfterAgree(username, new FindListener<BmobChatUser>() {
                                @Override
                                public void onSuccess(List<BmobChatUser> list) {
                                    //保存到内存中
                                    CoderApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(context)
                                            .getContactList()));
                                }

                                @Override
                                public void onError(int i, String s) {

                                }
                            });
                            //显示通知
                            showOtherNotify(context, username, toId, username + "同意添加您为好友", HomeActivity.class);
                            ////创建一个临时验证会话--用于在会话界面形成初始会话
                            BmobMsg.createAndSaveRecentAfterAgree(context, json);
                        } else if (tag.equals(BmobConfig.TAG_READED)) {//已读回执
                            String conversionId = BmobJsonUtil.getString(jsonObject, BmobConstant.PUSH_READED_CONVERSIONID);
                            if (currentUser != null) {
                                //更新某条消息的状态
                                BmobChatManager.getInstance(context).updateMsgStatus(conversionId, msgTime);
                                if (toId.equals(currentUser.getObjectId())) {
                                    if (sEventListeners.size() > 0) {// 有监听的时候，传递下去--便于修改界面
                                        for (EventListener handler : sEventListeners) {
                                            handler.onReaded(conversionId, msgTime);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else {//在黑名单期间所有的消息都应该置为已读，不然等取消黑名单之后又可以查询的到
                        BmobChatManager.getInstance(context).updateMsgReaded(true,fromId,msgTime);
                    BmobLog.i("该消息发送方为黑名单用户");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            //这里截取到的有可能是web后台推送给客户端的消息，也有可能是开发者自定义发送的消息，需要开发者自行解析和处理
            BmobLog.i("parseMessage错误：" + e.getMessage());
        }
    }

    /**
     * 显示与聊天消息的通知
     *
     * @param context
     * @param msg
     */
    private void showMsgNotify(Context context, BmobMsg msg) {
        //更新通知栏
        int icon = R.drawable.ic_launcher;
        String trueMsg = "";
        if (msg.getMsgType() == BmobConfig.TYPE_TEXT && msg.getContent().contains("\\ue")) {
            trueMsg = "[表情]";
        } else if (msg.getMsgType() == BmobConfig.TYPE_IMAGE) {
            trueMsg = "[图片]";
        } else if (msg.getMsgType() == BmobConfig.TYPE_VOICE) {
            trueMsg = "[语音]";
        } else if (msg.getMsgType() == BmobConfig.TYPE_LOCATION) {
            trueMsg = "[位置]";
        } else {
            trueMsg = msg.getContent();
        }
        CharSequence tickerText = msg.getBelongUsername() + ":" + trueMsg;
        String contentTitle = msg.getBelongUsername() + "(" + mNewNum + "条新消息）";

        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean isAllowVoice = CoderApplication.getInstance().getSpUtil().isAllowVoice();
        boolean isAllowVibrate = CoderApplication.getInstance().getSpUtil().isAllowVibrate();

        BmobNotifyManager.getInstance(context).showNotifyWithExtras(isAllowVoice, isAllowVibrate, icon,
                tickerText.toString(), contentTitle, tickerText.toString(), intent);
    }

    /**
     * 显示其他tag的通知
     *
     * @param context
     * @param username
     * @param toId
     * @param ticker
     * @param clazz
     */
    public void showOtherNotify(Context context, String username, String toId, String ticker, Class<?> clazz) {
        boolean isAllow = CoderApplication.getInstance().getSpUtil().isAllowPushNotify();
        boolean isAllowVoice = CoderApplication.getInstance().getSpUtil().isAllowVoice();
        boolean isAllowVibrate = CoderApplication.getInstance().getSpUtil().isAllowVibrate();
        if (isAllow && currentUser != null && currentUser.getObjectId().equals(toId)) {
            //同时提醒通知
            BmobNotifyManager.getInstance(context).showNotify(isAllowVoice, isAllowVibrate, R.drawable.ic_launcher,
                    ticker, username, ticker.toString(), NewFriendActivity.class);
        }
    }


}
