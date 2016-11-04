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
import com.tony.coder.im.CoderApplication;
import com.tony.coder.im.ui.adapter.base.BaseListAdapter;
import com.tony.coder.im.ui.adapter.base.ViewHolder;
import com.tony.coder.im.utils.CollectionUtils;
import com.tony.coder.im.utils.ImageLoadOptions;

import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/19 14:36
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class NewFriendAdapter extends BaseListAdapter<BmobInvitation> {


    public NewFriendAdapter(Context context, List<BmobInvitation> list) {
        super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_add_friend, null);
        }

        final BmobInvitation msg = getList().get(position);
        TextView name = ViewHolder.getView(convertView, R.id.name);
        ImageView iv_avatar = ViewHolder.getView(convertView, R.id.avatar);

        final Button btn_add = ViewHolder.getView(convertView, R.id.btn_add);

        String avatar = msg.getAvatar();

        if (avatar != null && !avatar.equals("")) {
            // TODO: 2016/4/20  ImageLoaderUtils.display(mContext, iv_avatar, avatar);
            ImageLoader.getInstance().displayImage(avatar,iv_avatar, ImageLoadOptions.getOptions());
        } else {
            iv_avatar.setImageResource(R.drawable.head);
        }

        int status = msg.getStatus();
        if (status == BmobConfig.INVITE_ADD_NO_VALIDATION || status == BmobConfig.INVITE_ADD_NO_VALI_RECEIVED) {
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BmobLog.i("点击同意按钮：" + msg.getFromid());
                    agreeAdd(btn_add, msg);
                }
            });
        } else if (status == BmobConfig.INVITE_ADD_AGREE) {
            btn_add.setText("已同意");
            btn_add.setBackgroundDrawable(null);
            btn_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_black));
            btn_add.setEnabled(false);
        }
        name.setText(msg.getFromname());
        return convertView;
    }

    private void agreeAdd(final Button btn_add, BmobInvitation msg) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("正在添加...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        try {
            //同意添加好友
            BmobUserManager.getInstance(mContext).agreeAddContact(msg, new UpdateListener() {
                @Override
                public void onSuccess() {
                    progressDialog.dismiss();
                    btn_add.setText("已同意");
                    btn_add.setBackgroundDrawable(null);
                    btn_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_black));
                    btn_add.setEnabled(false);
                    //保存到application中方便比较
                    CoderApplication.getInstance().setContactList(CollectionUtils.list2map(
                            BmobDB.create(mContext).getContactList()));
                }

                @Override
                public void onFailure(int i, String s) {
                    progressDialog.dismiss();
                    showToast("添加失败：" + s);
                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
        }


    }
}
