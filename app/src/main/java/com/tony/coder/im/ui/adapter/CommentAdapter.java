package com.tony.coder.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.tony.coder.R;
import com.tony.coder.im.entity.DynamicWall.Comment;
import com.tony.coder.im.ui.adapter.base.BaseListAdapter;
import com.tony.coder.im.ui.adapter.base.ViewHolder;

import java.util.List;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/29 17:33
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class CommentAdapter extends BaseListAdapter<Comment> {

    private static final String TAG = "CommentAdapter";

    public CommentAdapter(Context context, List<Comment> list) {
        super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_comment, null);
        }

        TextView userName = ViewHolder.getView(convertView, R.id.userName_comment);
        TextView commentContent = ViewHolder.getView(convertView, R.id.content_comment);
        TextView index = ViewHolder.getView(convertView, R.id.index_comment);

        final Comment comment = getList().get(position);

        if (comment.getUser() != null) {
            userName.setText(comment.getUser().getNick());
            Logger.d(TAG, "Name = " + comment.getUser().getNick());
        } else {
            userName.setText("好友");
        }

        index.setText((position + 1) + "楼");
        commentContent.setText(comment.getCommentContent());
        return convertView;
    }
}
