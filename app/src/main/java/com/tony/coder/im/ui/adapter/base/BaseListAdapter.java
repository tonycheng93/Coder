package com.tony.coder.im.ui.adapter.base;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.im.util.BmobLog;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/11 19:01
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public abstract class BaseListAdapter<E> extends BaseAdapter {
    public List<E> mList;
    public Context mContext;
    public LayoutInflater mInflater;

    public BaseListAdapter(Context context,List<E> list) {
        super();
        mList = list;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public List<E> getList() {
        return mList;
    }

    public void setList(List<E> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void add(E e) {
        mList.add(e);
        notifyDataSetChanged();
    }

    public void addAll(List<E> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        this.mList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size() > 0 ? mList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = bindView(position, convertView, parent);
        //绑定内部点击监听
        addInternalClickListener(convertView,position,mList.get(position));
        return convertView;
    }

    public abstract View bindView(int position, View convertView, ViewGroup parent);

    // adapter中的内部点击事件
    public Map<Integer, onInternalClickListener> canClickItem;

    private void addInternalClickListener(final View itemView, final Integer position, final Object valuesMap) {
        if (canClickItem != null) {
            for (Integer key : canClickItem.keySet()) {
                View inView = itemView.findViewById(key);
                final onInternalClickListener inViewListener = canClickItem.get(key);
                if (inView != null && inViewListener != null) {
                    inView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inViewListener.OnClickListener(itemView, v, position, valuesMap);
                        }
                    });
                }
            }
        }
    }

    public void setOnInViewClickListener(Integer key, onInternalClickListener onClickListener) {
        if (canClickItem == null) {
            canClickItem = new HashMap<>();
        }
        canClickItem.put(key, onClickListener);
    }

    public interface onInternalClickListener {
        void OnClickListener(View parentView, View view, Integer position, Object values);
    }

    Toast mToast;

    public void showToast(final String text) {
        if ( ! TextUtils.isEmpty(text)) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mToast == null) {
                        mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
                    } else {
                        mToast.setText(text);
                    }
                    mToast.show();
                }
            });
        }
    }

    /**
     * 打印Log
     *
     * @param msg
     */
    public void showLog(String msg) {
        BmobLog.i(msg);
    }
}
