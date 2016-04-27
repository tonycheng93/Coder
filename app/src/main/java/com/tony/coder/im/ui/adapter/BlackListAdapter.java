package com.tony.coder.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tony.coder.R;
import com.tony.coder.im.ui.adapter.base.BaseListAdapter;
import com.tony.coder.im.ui.adapter.base.ViewHolder;
import com.tony.coder.im.utils.ImageLoadOptions;

import java.util.List;

import cn.bmob.im.bean.BmobChatUser;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/18 21:01
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class BlackListAdapter extends BaseListAdapter<BmobChatUser> {
    public BlackListAdapter(Context context, List<BmobChatUser> list) {
        super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_blacklist, null);
        }
        BmobChatUser contract = getList().get(position);
        TextView tv_friend_name = ViewHolder.getView(convertView, R.id.tv_friend_name);
        ImageView iv_avatar = ViewHolder.getView(convertView, R.id.img_friend_avatar);
        String avatar = contract.getAvatar();

        if (avatar != null && !avatar.equals("")) {
//         todo   ImageLoaderUtils.display(mContext, iv_avatar, avatar);
            ImageLoader.getInstance().displayImage(avatar,iv_avatar, ImageLoadOptions.getOptions());
        } else {
            iv_avatar.setImageResource(R.drawable.default_head);
        }
        tv_friend_name.setText(contract.getUsername());
        return convertView;
    }
}
