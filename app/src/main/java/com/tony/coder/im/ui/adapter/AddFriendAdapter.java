package com.tony.coder.im.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tony.coder.R;
import com.tony.coder.im.ui.adapter.base.BaseListAdapter;
import com.tony.coder.im.ui.adapter.base.ViewHolder;
import com.tony.coder.im.utils.ImageLoadOptions;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.PushListener;

/**
 * 项目名称：Coder
 * 类描述：查找好友
 * 创建人：tonycheng
 * 创建时间：2016/4/19 17:44
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class AddFriendAdapter extends BaseListAdapter<BmobChatUser> {
    public AddFriendAdapter(Context context, List<BmobChatUser> list) {
        super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_add_friend, null);
        }

        final BmobChatUser contract = getList().get(position);
        TextView name = ViewHolder.getView(convertView, R.id.name);
        ImageView iv_avatar = ViewHolder.getView(convertView, R.id.avatar);

        Button btn_add = ViewHolder.getView(convertView, R.id.btn_add);

        String avatar = contract.getAvatar();
        if (avatar != null && !avatar.equals("")) {
//    todo        ImageLoaderUtils.display(mContext, iv_avatar, avatar);
            ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions());
        } else {
            iv_avatar.setImageResource(R.drawable.default_head);
        }

        name.setText(contract.getUsername());
        btn_add.setText("添加");
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progress = new ProgressDialog(mContext);
                progress.setMessage("正在添加...");
                progress.setCanceledOnTouchOutside(false);
                progress.show();
                //发送tag请求
                BmobChatManager.getInstance(mContext).sendTagMessage(BmobConfig.TAG_ADD_CONTACT,
                        contract.getObjectId(), new PushListener() {
                            @Override
                            public void onSuccess() {
                                progress.dismiss();
                                showToast("发送请求成功，等待对方验证！");
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                progress.dismiss();
                                showToast("发送请求失败，请重新添加！");
                                showLog("发送请求失败：" + s);
                            }
                        });
            }
        });
        return convertView;
    }
}
