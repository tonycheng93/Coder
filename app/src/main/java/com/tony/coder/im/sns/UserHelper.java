package com.tony.coder.im.sns;

import android.content.Context;

import com.tony.coder.im.CoderApplication;
import com.tony.coder.im.entity.User;

import cn.bmob.v3.BmobUser;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/28 17:37
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class UserHelper {

    public static User getCurrentUser(Context context) {
        User user = BmobUser.getCurrentUser(context, User.class);
        if (user != null) {
            return user;
        }
        return null;
    }

    public static User getCurrentUser() {
        User user = BmobUser.getCurrentUser(CoderApplication.getInstance(), User.class);
        if (user != null) {
            return user;
        }
        return null;
    }

    public static String getUserId() {
        return getCurrentUser().getObjectId();
    }
}
