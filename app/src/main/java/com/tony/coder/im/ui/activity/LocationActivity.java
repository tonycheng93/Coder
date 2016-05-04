package com.tony.coder.im.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.tony.coder.R;
import com.tony.coder.im.widget.HeaderLayout;

import cn.bmob.im.util.BmobLog;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/12 19:19
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class LocationActivity extends BaseActivity implements OnGetGeoCoderResultListener {

    //定位相关
    LocationClient mLocationClient;
    public LocationListener mListener = new LocationListener();

    MapView mMapView;
    BaiduMap mBaiduMap;

    private BaiduReceiver mReceiver;//注册广播接收器，用于监听网络以及验证key

    /* 搜索模块，因为百度定位sdk能够得到经纬度，但是却无法得到具体的详细地址，
     因此需要采取反编码方式去搜索此经纬度代表的地址*/
    GeoCoder mSearch = null;

    static BDLocation lastLocation = null;

    BitmapDescriptor bdgeo = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initBaiduMap();
    }

    private void initBaiduMap() {
        //地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //设置缩放级别
        mBaiduMap.setMaxAndMinZoomLevel(18, 13);
        //注册 SDK 广播监听者
        IntentFilter filter = new IntentFilter();
        filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        mReceiver = new BaiduReceiver();
        registerReceiver(mReceiver, filter);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        if (type.equals("select")) {//选择发送位置
            initTopBarForBoth("位置", R.drawable.btn_login_selector, "发送",
                    new HeaderLayout.onRightImageButtonClickListener() {
                        @Override
                        public void onClick() {
                            gotoChatPage();
                        }
                    });
            mHeaderLayout.getRightImageButton().setEnabled(false);
            initLocClient();
        } else {//查看当前位置
            initTopBarForLeft("位置");
            Bundle extras = intent.getExtras();
            LatLng latLng = new LatLng(extras.getDouble("latitude"), extras.getDouble("longtitude"));//维度在前，经度在后
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
            //显示当前位置图标
            OverlayOptions options = new MarkerOptions().position(latLng).icon(bdgeo).zIndex(9);
            mBaiduMap.addOverlay(options);
        }
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
    }

    private void initLocClient() {
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(
                MyLocationConfigeration.LocationMode.NORMAL, true, null));
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(mListener);
        LocationClientOption option = new LocationClientOption();
        option.setProdName("coderim");//设置产品线
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        option.setIgnoreKillProcess(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.requestLocation();

            if (lastLocation != null) {
                //显示在地图上
                LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                MapStatusUpdate statusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(statusUpdate);
            }
        }
    }

    /**
     * 回到聊天界面
     */
    private void gotoChatPage() {
        if (lastLocation != null) {
            Intent intent = new Intent();
            intent.putExtra("y", lastLocation.getLongitude());//经度
            intent.putExtra("x", lastLocation.getLatitude());//维度
            intent.putExtra("address", lastLocation.getAddrStr());
            setResult(RESULT_OK, intent);
            this.finish();
        } else {
            showToast("获取地理位置信息失败！");
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            showToast("抱歉，未能找到结果");
            return;
        }
        BmobLog.i("反编码得到的地址：" + result.getAddress());
        lastLocation.setAddrStr(result.getAddress());
    }

    /**
     * 定位SDK监听函数
     */
    public class LocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //map view 销毁后不在处理新接收的位置
            if (bdLocation == null || mMapView == null) {
                return;
            }
            if (lastLocation != null) {
                if (lastLocation.getLatitude() == bdLocation.getLatitude()
                        && lastLocation.getLongitude() == bdLocation.getLongitude()) {
                    BmobLog.i("获取坐标相同");// 若两次请求获取到的地理位置坐标是相同的，则不再定位
                    mLocationClient.stop();
                    return;
                }
            }
            lastLocation = bdLocation;

            BmobLog.i("longtitude = " + bdLocation.getLongitude() +
                    ",latitude = " + bdLocation.getLatitude() + ",地址 = " + lastLocation.getAddrStr());

            MyLocationData locationData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    //此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100)
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(locationData);
            LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            String address = bdLocation.getAddrStr();
            if (address != null && !address.equals("")) {
                lastLocation.setAddrStr(address);
            } else {
                //反Geo搜索
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
            }

            MapStatusUpdate statusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.animateMapStatus(statusUpdate);
            //设置按钮可点击
            mHeaderLayout.getRightImageButton().setEnabled(true);
        }
    }

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class BaiduReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                showToast("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
            } else if (action.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                showToast("网络出错！");
            }
        }
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
        lastLocation = null;
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            //退出时销毁定位
            mLocationClient.stop();
        }
        //关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
//        mMapView.onDestroy();// TODO: 2016/4/29 这个地方会出现crash
        mMapView = null;
        //取消监听SDK广播
        unregisterReceiver(mReceiver);
        super.onDestroy();
        //回收bitmap资源
        bdgeo.recycle();
    }
}
