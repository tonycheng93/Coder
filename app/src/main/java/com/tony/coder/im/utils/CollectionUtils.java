package com.tony.coder.im.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bmob.im.bean.BmobChatUser;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/3/28 19:02
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class CollectionUtils {
    public static boolean isNotNull(Collection<?> collection) {
        if (collection != null && collection.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * list转map
     * 以用户名为key
     *
     * @param users
     * @return Map<String,BmobChatUser>
     */
    public static Map<String, BmobChatUser> list2map(List<BmobChatUser> users) {
        Map<String, BmobChatUser> friends = new HashMap<>();
        for (BmobChatUser user : users) {
            friends.put(user.getUsername(), user);
        }
        return friends;
    }

    /**
     * map转list
     *
     * @param maps
     * @return List<BmobChatUser>
     */
    public static List<BmobChatUser> map2list(Map<String, BmobChatUser> maps) {
        List<BmobChatUser> users = new ArrayList<>();
        Iterator<Map.Entry<String, BmobChatUser>> iterator = maps.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, BmobChatUser> entry = iterator.next();
            users.add(entry.getValue());
        }
        return users;
    }
}
