package com.tony.coder.im.ui.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tony.coder.im.entity.FaceText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/11 19:48
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class BaseArrayListAdapter extends BaseAdapter {
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<FaceText> mDatas = new ArrayList<>();

    public BaseArrayListAdapter(Context context, FaceText... datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        if (datas != null && datas.length > 0){
            mDatas = Arrays.asList(datas);
        }
    }

    public BaseArrayListAdapter(Context context, List<FaceText> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        if (datas != null && datas.size() > 0){
            mDatas = datas;
        }
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
