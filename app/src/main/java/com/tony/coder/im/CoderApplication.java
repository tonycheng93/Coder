package com.tony.coder.im;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tony.coder.R;
import com.tony.coder.im.entity.DynamicWall.DynamicWall;
import com.tony.coder.im.io.ACache;
import com.tony.coder.im.utils.ActivityManagerUtils;
import com.tony.coder.im.utils.CollectionUtils;
import com.tony.coder.im.utils.SharePreferenceUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/3/15 18:58
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class CoderApplication extends Application {

    public static CoderApplication mInstance;
    public LocationClient mLocationClient;
    public MyLocationListener mLocationListener;

    public static BmobGeoPoint lastPoint = null;//上一次定位到经纬度

    //社区相关
    private DynamicWall currentDynamicWall;

    //数据缓存相关
    private ACache mACache;


    @Override
    public void onCreate() {
        super.onCreate();
        // 是否开启debug模式--默认开启状态
        BmobChat.DEBUG_MODE = true;
        mInstance = this;
        init();
    }

    public ACache getCache() {
        if (mACache == null) {
            return ACache.get(getApplicationContext());
        } else {
            return mACache;
        }
    }

    public DynamicWall getCurrentDynamicWall() {
        return currentDynamicWall;
    }

    public void setCurrentDynamicWall(DynamicWall currentDynamicWall) {
        this.currentDynamicWall = currentDynamicWall;
    }

    public void addActivity(Activity activity) {
        ActivityManagerUtils.getInstance().addActivity(activity);
    }

    public void exit() {
        ActivityManagerUtils.getInstance().removeAllActivity();
    }

    private void init() {
        mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        initImageLoader(getApplicationContext());

        //若用户登陆过，则先从好友数据库中取出好友list
        if (BmobUserManager.getInstance(getApplicationContext())
                .getCurrentUser() != null) {
            //获取本地好友user list到内存中，方便以后获取好友list
            contactList = CollectionUtils.list2map(BmobDB.create(getApplicationContext()).getContactList());
        }
        initBaidu();
    }

    private void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "coderim/Cache");//获取缓存的目录地址
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5加密
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .diskCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径
                .build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * 初始化百度相关sdk initBaidumap
     */
    private void initBaidu() {
        //初始化地图SDK
        SDKInitializer.initialize(this);
        //初始化定位SDK
        initBaiduLocClient();
    }

    /**
     * 初始化百度定位sdk
     */
    private void initBaiduLocClient() {
        mLocationClient = new LocationClient(this.getApplicationContext());
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);
    }

    private Map<String, BmobChatUser> contactList = new HashMap<>();

    /**
     * 获取内存中的好友user list
     */
    public Map<String, BmobChatUser> getContactList() {
        return contactList;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, BmobChatUser> contactList) {
        if (this.contactList != null) {
            this.contactList.clear();
        }
        this.contactList = contactList;
    }

    NotificationManager mNotificationManager;

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    MediaPlayer mMediaPlayer;

    public synchronized MediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
        }
        return mMediaPlayer;
    }


    /**
     * 实现定位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();
            if (lastPoint != null) {
                if (lastPoint.getLatitude() == bdLocation.getLatitude()
                        && lastPoint.getLongitude() == bdLocation.getLongitude()) {
                    BmobLog.i("两次获取坐标相同");// 若两次请求获取到的地理位置坐标是相同的，则不再定位
                    mLocationClient.stop();
                    return;
                }
            }
            lastPoint = new BmobGeoPoint(longitude, latitude);
        }
    }

    public final String PREF_LONGTITUDE = "longtitude";// 经度
    private String longtitude = "";

    /**
     * 获取经度
     *
     * @return
     */
    public String getLongtitude() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        longtitude = preferences.getString(PREF_LONGTITUDE, "");
        return longtitude;
    }

    /**
     * 设置经度
     *
     * @param
     */
    public void setLongtitude(String lon) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        if (editor.putString(PREF_LONGTITUDE, lon).commit()) {
            longtitude = lon;
        }
    }

    public final String PREF_LATITUDE = "latitude";// 经度
    private String latitude = "";

    /**
     * 获取纬度
     *
     * @return
     */
    public String getLatitude() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        latitude = preferences.getString(PREF_LATITUDE, "");
        return latitude;
    }

    /**
     * 设置维度
     *
     * @param
     */
    public void setLatitude(String lat) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        if (editor.putString(PREF_LATITUDE, lat).commit()) {
            latitude = lat;
        }
    }

    public static CoderApplication getInstance() {
        return mInstance;
    }

    /*单例模式，才能及时返回数据*/
    SharePreferenceUtil mSpUtil;
    public static final String PREFERENCE_NAME = "_sharedinfo";

    public synchronized SharePreferenceUtil getSpUtil() {
        if (mSpUtil == null) {
            String currentId = BmobUserManager.getInstance(getApplicationContext())
                    .getCurrentUserObjectId();
            String sharedName = currentId + PREFERENCE_NAME;
            mSpUtil = new SharePreferenceUtil(this, sharedName);
        }
        return mSpUtil;
    }

    /**
     * 退出登录,清空缓存数据
     */
    public void logout() {
        BmobUserManager.getInstance(getApplicationContext()).logout();
        setContactList(null);
        setLatitude(null);
        setLongtitude(null);
    }

    public Activity getTopActivity() {
        return ActivityManagerUtils.getInstance().getTopActivity();
    }
}
