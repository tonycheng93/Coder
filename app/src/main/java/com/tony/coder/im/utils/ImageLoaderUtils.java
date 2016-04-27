package com.tony.coder.im.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tony.coder.R;


/**
 * 项目名称：MVPDemo
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/8 10:58
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class ImageLoaderUtils {

    public static void display(Context context, ImageView imageView, String url, int placeHolder, int error) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context)
                .load(url)
                .placeholder(placeHolder)
                .error(error)
                .crossFade()
                .into(imageView);
    }

    public static void display(Context context, ImageView imageView, String url) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_image_loadfail)
                .crossFade()
                .into(imageView);

    }
}
