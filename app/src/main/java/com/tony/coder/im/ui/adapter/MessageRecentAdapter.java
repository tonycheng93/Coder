package com.tony.coder.im.ui.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tony.coder.R;
import com.tony.coder.im.ui.adapter.base.ViewHolder;
import com.tony.coder.im.utils.FaceTextUtils;
import com.tony.coder.im.utils.ImageLoadOptions;
import com.tony.coder.im.utils.TimeUtil;

import java.util.List;

import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/11 19:55
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class MessageRecentAdapter extends ArrayAdapter<BmobRecent>{

    private LayoutInflater mInflater;
    private List<BmobRecent> mData;
    private Context mContext;

    public MessageRecentAdapter(Context context, int textViewResourceId, List<BmobRecent> objects) {
        super(context, textViewResourceId, objects);
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        mData = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BmobRecent item = mData.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_conversation, parent, false);
        }
        ImageView iv_recent_avatar = ViewHolder.getView(convertView, R.id.iv_recent_avatar);
        TextView tv_recent_name = ViewHolder.getView(convertView, R.id.tv_recent_name);
        TextView tv_recent_msg = ViewHolder.getView(convertView, R.id.tv_recent_msg);
        TextView tv_recent_time = ViewHolder.getView(convertView, R.id.tv_recent_time);
        TextView tv_recent_unread = ViewHolder.getView(convertView, R.id.tv_recent_unread);

        //填充数据
        String avatar = item.getAvatar();
        if (avatar != null && !avatar.equals("")) {
            // TODO: 2016/4/20  ImageLoaderUtils.display(mContext, iv_recent_avatar, avatar);
            ImageLoader.getInstance().displayImage(avatar, iv_recent_avatar, ImageLoadOptions.getOptions());
        } else {
            iv_recent_avatar.setImageResource(R.drawable.head);
        }

        tv_recent_name.setText(item.getUserName());
        tv_recent_time.setText(TimeUtil.getChatTime(item.getTime()));
        //显示内容
        if (item.getType() == BmobConfig.TYPE_TEXT) {
            SpannableString spannableString = FaceTextUtils.toSpannableString(mContext, item.getMessage());
            tv_recent_msg.setText(spannableString);
        } else if (item.getType() == BmobConfig.TYPE_IMAGE) {
            tv_recent_msg.setText("[图片]");
        } else if (item.getType() == BmobConfig.TYPE_LOCATION) {
            String all = item.getMessage();
            if (all != null && !all.equals("")) {//位置类型的信息组装格式：地理位置&维度&经度
                String address = all.split("&")[0];
                tv_recent_msg.setText("[位置]" + address);
            }
        } else if (item.getType() == BmobConfig.TYPE_VOICE) {
            tv_recent_msg.setText("[语音]");
        }

        int num = BmobDB.create(mContext).getUnreadCount(item.getTargetid());
        if (num > 0) {
            tv_recent_unread.setVisibility(View.VISIBLE);
            tv_recent_unread.setText(num + "");
        } else {
            tv_recent_unread.setVisibility(View.GONE);
        }
        return convertView;
    }
}
