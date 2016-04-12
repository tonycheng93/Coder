package com.tony.coder.im.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tony.coder.R;
import com.tony.coder.im.util.FaceTextUtils;
import com.tony.coder.im.util.TimeUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/1 14:01
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class ChatAdapterRecycler extends RecyclerView.Adapter<ChatAdapterRecycler.RecyclerViewVH> {


    private LayoutInflater mInflater;
    private Context mContext;
    private List<BmobRecent> mDatas;

    public ChatAdapterRecycler(Context context, List<BmobRecent> datas) {
        mContext = context;
        mDatas = datas;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerViewVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_conversation, parent, false);
        return new RecyclerViewVH(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewVH holder, int position) {
        BmobRecent bmobRecent = mDatas.get(position);
        holder.setData(bmobRecent);
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, position);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    public class RecyclerViewVH extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_recent_avatar)
        ImageView iv_recent_avatar;
        @Bind(R.id.tv_recent_name)
        TextView tv_recent_name;
        @Bind(R.id.tv_recent_msg)
        TextView tv_recent_msg;
        @Bind(R.id.tv_recent_time)
        TextView tv_recent_time;
        @Bind(R.id.tv_recent_unread)
        TextView tv_recent_unread;

        public RecyclerViewVH(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);
        }

        //设置数据的方法
        public void setData(BmobRecent bmobRecent) {
            if (bmobRecent.getAvatar() != null) {
                Glide.with(mContext)
                        .load(bmobRecent.getAvatar())
                        .into(iv_recent_avatar);
            } else {//显示默认头像
                iv_recent_avatar.setImageResource(R.drawable.head);
            }
            tv_recent_name.setText(bmobRecent.getUserName());
            tv_recent_time.setText(TimeUtil.getChatTime(bmobRecent.getTime()));
            //显示内容
            if (bmobRecent.getType() == BmobConfig.TYPE_TEXT) {
                SpannableString spannableString = FaceTextUtils.toSpannableString(mContext, bmobRecent.getMessage());
                tv_recent_msg.setText(spannableString);
            } else if (bmobRecent.getType() == BmobConfig.TYPE_IMAGE) {
                tv_recent_msg.setText("[图片]");
            } else if (bmobRecent.getType() == BmobConfig.TYPE_LOCATION) {
                String all = bmobRecent.getMessage();
                if (all != null && !all.equals("")) {//位置类型的信息组装格式：地理位置&维度&经度
                    String address = all.split("&")[0];
                    tv_recent_msg.setText("[位置]" + address);
                }
            } else if (bmobRecent.getType() == BmobConfig.TYPE_VOICE) {
                tv_recent_msg.setText("[语音]");
            }
            int num = BmobDB.create(mContext).getUnreadCount(bmobRecent.getTargetid());
            if (num > 0) {
                tv_recent_unread.setVisibility(View.VISIBLE);
                tv_recent_unread.setText(num + "");
            } else {
                tv_recent_unread.setVisibility(View.GONE);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * 删除一条会话
     *
     * @param position
     */
    public void remove(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }
}
