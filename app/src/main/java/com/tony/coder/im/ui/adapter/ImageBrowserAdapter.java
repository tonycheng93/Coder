package com.tony.coder.im.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tony.coder.R;
import com.tony.coder.im.utils.ImageLoadOptions;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/19 9:19
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class ImageBrowserAdapter extends PagerAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<String> mPhotos;

    public ImageBrowserAdapter(Context context, List<String> photos) {
        this.mContext = context;
        this.mPhotos = photos;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mPhotos.size() > 0 ? mPhotos.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View imageLayout = mInflater.inflate(R.layout.item_show_picture, container, false);
        PhotoView photoView = (PhotoView) imageLayout.findViewById(R.id.photoview);
        final ProgressBar progressBar = (ProgressBar) imageLayout.findViewById(R.id.progress);
        String imageUrl = mPhotos.get(position);
        //todo ImageLoaderUtils.display(mContext, photoView, imageUrl);

        ImageLoader.getInstance().displayImage(imageUrl,photoView, ImageLoadOptions.getOptions(R.drawable.bg_pic_loading),
                new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

        container.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
