package com.tony.coder.im.common;

import android.os.Environment;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/3/30 16:07
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class Constants {
    /**
     * 存放发送图片的目录
     */
    public static String CODER_PICTURE_PATH = Environment.getExternalStorageDirectory()
            + "/coder/image/";
    /**
     * 我的头像保存目录
     */
//    public static String MyAvatarDir = "/sdcard/coder/avatar/";
    public static String MyAvatarDir = Environment.getExternalStorageDirectory() + "/coder/avatar/";
    public static String TRUE = "true";
    public static String FALSE = "false";
    /**
     * 拍照回调
     */
    public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;//拍照修改头像
    public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;//本地相册修改头像
    public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;//系统裁剪头像

    public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;//拍照
    public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;//本地图片
    public static final int REQUESTCODE_TAKE_LOCATION = 0x000003;//位置
    public static final String EXTRA_STRING = "extra_string";

    public static final String PRE_NAME = "coder_pre";

    public static final int NUMBERS_PER_PAGE = 15;//每次请求返回评论条数
    public static final int PUBLISH_COMMENT = 1;
    public static final int SAVE_FAVOURITE = 2;
    public static final int GET_FAVOURITE = 3;
    public static final int GO_SETTINGS = 4;


    public static final String ACTION_REGISTER_SUCCESS_FINISH = "register.success.finish";//注册成功之后登陆页面退出
}
