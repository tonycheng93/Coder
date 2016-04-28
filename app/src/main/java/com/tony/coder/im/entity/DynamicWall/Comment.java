package com.tony.coder.im.entity.DynamicWall;

import com.tony.coder.im.entity.User;

import cn.bmob.v3.BmobObject;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/27 19:49
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class Comment extends BmobObject {
    private User user;
    private String commentContent;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
