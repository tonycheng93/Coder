package com.tony.coder.im.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ImageLoadOptions {

    public static DisplayImageOptions getOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.ic_image_loading)//设置图片在下载期间显示的图片
//                .showImageForEmptyUri(R.drawable.default_head)//设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.drawable.ic_image_loadfail)//设置图片加载/解码过程中错误的时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.EXACTLY)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                .considerExifParams(true)
//                .delayBeforeLoading(int delayInMills)//设置图片下载前的延迟
//        .preProcessor(BitmapProcessor preProcessor)//设置图片加入缓存前，对bitmap进行设置
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
//                .displayer(new RoundedBitmapDisplayer(20))//是否设置圆角，弧度为多少
                .displayer(new FadeInBitmapDisplayer(100))//淡入
                .displayer(new CircleBitmapDisplayer())
                .build();
        return options;
    }

    public static DisplayImageOptions getOptions(int drawableId) {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(drawableId)
                .showImageForEmptyUri(drawableId)
                .showImageOnFail(drawableId)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .build();
    }
}
