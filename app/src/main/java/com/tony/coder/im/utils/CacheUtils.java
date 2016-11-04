package com.tony.coder.im.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/29 9:39
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class CacheUtils {
    private static final String TAG = "CacheUtils";
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    /**
     * 获取/data/data/files目录
     *
     * @param context
     * @return
     */
    public static File getFileDirectory(Context context) {
        File appCacheDir = null;
        if (appCacheDir == null) {
            appCacheDir = context.getFilesDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/files/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    public static File getCacheDirectory(Context context, boolean preferExternal, String dirName) {
        File appCacheDir = null;
        if (preferExternal && Environment.MEDIA_MOUNTED
                .equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context, dirName);
        }
        if (appCacheDir == null){
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            Logger.d("Can't define system cache directory! '%s' will be used.", cacheDirPath);
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context, String dirName) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir2 = new File(new File(dataDir, context.getPackageName()), "cache");
        File appCacheDir = new File(appCacheDir2, dirName);
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                Logger.d(TAG, "Unable to create external cache directory");
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                Logger.e(TAG, "Can't create \\\".nomedia\\\" file in application external cache directory");
            }
        }
        return appCacheDir;
    }


    private static boolean hasExternalStoragePermission(Context context) {
        int permission = context.checkCallingPermission(EXTERNAL_STORAGE_PERMISSION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }


}
