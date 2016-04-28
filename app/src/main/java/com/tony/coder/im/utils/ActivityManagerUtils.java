package com.tony.coder.im.utils;

import android.app.Activity;

import java.util.ArrayList;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/28 14:21
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class ActivityManagerUtils {

    private static ActivityManagerUtils activityManagerUtils;
    private ArrayList<Activity> activityList = new ArrayList<Activity>();

    private ActivityManagerUtils() {

    }

    public static ActivityManagerUtils getInstance() {
        if (null == activityManagerUtils) {
            activityManagerUtils = new ActivityManagerUtils();
        }
        return activityManagerUtils;
    }

    public Activity getTopActivity() {
        return activityList.get(activityList.size() - 1);
    }

    public void removeMainActivity() {
        Activity activity = activityList.get(activityList.size() - 2);
        if (null != activity) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
            activity = null;
        }
    }

    public void addActivity(Activity ac) {
        activityList.add(ac);
    }

    public void removeAllActivity() {
        for (Activity ac : activityList) {
            if (null != ac) {
                if (!ac.isFinishing()) {
                    ac.finish();
                }
                ac = null;
            }
        }
        activityList.clear();
    }
}
