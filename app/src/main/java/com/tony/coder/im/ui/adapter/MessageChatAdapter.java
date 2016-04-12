package com.tony.coder.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.tony.coder.R;
import com.tony.coder.im.ui.adapter.base.BaseListAdapter;

import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/12 16:47
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class MessageChatAdapter extends BaseListAdapter<BmobMsg> {
    //8中item的类型

    //文本
    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    //图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;
    //位置
    private final int TYPE_SEND_LOCATION = 4;
    private final int TYPE_RECEIVER_LOCATION = 5;
    //语音
    private final int TYPE_SEND_VOICE = 6;
    private final int TYPE_RECEIVER_VOICE = 7;

    String currentObjectId = "";

    public MessageChatAdapter(Context context, List<BmobMsg> list) {
        super(list, context);
        currentObjectId = BmobUserManager.getInstance(context).getCurrentUserObjectId();
    }

    @Override
    public int getItemViewType(int position) {
        BmobMsg msg = mList.get(position);
        if (msg.getMsgType() == BmobConfig.TYPE_IMAGE) {
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_IMAGE : TYPE_RECEIVER_IMAGE;
        } else if (msg.getMsgType() == BmobConfig.TYPE_LOCATION) {
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_LOCATION : TYPE_RECEIVER_LOCATION;
        } else if (msg.getMsgType() == BmobConfig.TYPE_VOICE) {
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_VOICE : TYPE_RECEIVER_VOICE;
        } else {
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_TXT : TYPE_RECEIVER_TXT;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 8;
    }

    private View createViewType(BmobMsg message, int position) {
        int type = message.getMsgType();
        if (type == BmobConfig.TYPE_IMAGE){//图片
//            return getItemViewType(position) == TYPE_RECEIVER_IMAGE ?
//                    mInflater.inflate(R.layout.it)
        }
        return null;
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
