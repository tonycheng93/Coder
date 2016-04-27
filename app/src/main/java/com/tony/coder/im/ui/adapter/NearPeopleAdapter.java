package com.tony.coder.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tony.coder.R;
import com.tony.coder.im.CoderApplication;
import com.tony.coder.im.entity.User;
import com.tony.coder.im.ui.adapter.base.BaseListAdapter;
import com.tony.coder.im.ui.adapter.base.ViewHolder;
import com.tony.coder.im.utils.ImageLoadOptions;

import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/19 15:17
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class NearPeopleAdapter extends BaseListAdapter<User> {
    public NearPeopleAdapter(Context context, List<User> list) {
        super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_near_people, null);
        }
        final User contract = getList().get(position);
        TextView tv_name = ViewHolder.getView(convertView, R.id.tv_name);
        TextView tv_distance = ViewHolder.getView(convertView, R.id.tv_distance);
        TextView tv_logintime = ViewHolder.getView(convertView, R.id.tv_logintime);
        ImageView iv_avatar = ViewHolder.getView(convertView, R.id.iv_avatar);

        String avatar = contract.getAvatar();
        if (avatar != null && !avatar.equals("")) {
            // TODO: 2016/4/20  ImageLoaderUtils.display(mContext, iv_avatar, avatar);
            ImageLoader.getInstance().displayImage(avatar,iv_avatar, ImageLoadOptions.getOptions());
        } else {
            iv_avatar.setImageResource(R.drawable.default_head);
        }

        BmobGeoPoint location = contract.getLocation();
        String currentLat = CoderApplication.getInstance().getLatitude();
        String currentLong = CoderApplication.getInstance().getLongtitude();
        if (location != null && !currentLat.equals("") && !currentLong.equals("")) {
            double distance = DistanceOfTwoPoint(Double.parseDouble(currentLat), Double.parseDouble(currentLong),
                    contract.getLocation().getLatitude(), contract.getLocation().getLongitude());
            tv_distance.setText(String.valueOf(distance) + "米");
        } else {
            tv_distance.setText("未知");
        }
        tv_name.setText(contract.getUsername());
        tv_logintime.setText("最近登录时间：" + contract.getUpdatedAt());
        return convertView;
    }

    private static final double EARTH_RADIUS = 6378137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，
     *
     * @param lat1
     * @param long1
     * @param lat2
     * @param long2
     * @return 距离：单位为米
     */
    private double DistanceOfTwoPoint(double lat1, double long1, double lat2, double long2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(long1) - rad(long2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
}
