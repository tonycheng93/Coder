package com.tony.coder.im.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tony.coder.R;
import com.tony.coder.im.entity.User;
import com.tony.coder.im.utils.ImageLoadOptions;

import java.util.List;

/**
 * 项目名称：Coder
 * 类描述：好友列表
 * 创建人：tonycheng
 * 创建时间：2016/4/15 11:48
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class UserFriendAdapter extends BaseAdapter implements SectionIndexer {
    private Context mContext;
    private List<User> data;

    public UserFriendAdapter(Context context, List<User> data) {
        mContext = context;
        this.data = data;
    }

    /**
     * 当ListView数据发生变化时，调用此方法来更新ListView
     *
     * @param list user 列表
     */
    public void updateListView(List<User> list) {
        this.data = list;
        notifyDataSetChanged();
    }

    public void remove(User user) {
        this.data.remove(user);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size() > 0 ? data.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user_friend, null);
            holder = new ViewHolder();
            holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
            holder.name = (TextView) convertView.findViewById(R.id.tv_friend_name);
            holder.avatar = (ImageView) convertView.findViewById(R.id.img_friend_avatar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        User friend = data.get(position);
        String name = friend.getUsername();
        String avatar = friend.getAvatar();

        if ( ! TextUtils.isEmpty(avatar)) {
            // TODO: 2016/4/20  ImageLoaderUtils.display(mContext, holder.avatar, avatar);
            ImageLoader.getInstance().displayImage(avatar, holder.avatar, ImageLoadOptions.getOptions());
        } else {
            holder.avatar.setImageDrawable(mContext.getResources().getDrawable(R.drawable.head));
        }
        holder.name.setText(name);

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char位置，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.alpha.setVisibility(View.VISIBLE);
            holder.alpha.setText(friend.getSortLetters());
        } else {
            holder.alpha.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView alpha;//首字母提示
        TextView name;
        ImageView avatar;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = data.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return data.get(position).getSortLetters().charAt(0);
    }
}
