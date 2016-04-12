package com.tony.coder.im.ui.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.tony.coder.im.view.ClearEditText;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/3/31 17:22
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class ContactFragment extends BaseFragment implements
        AdapterView.OnItemClickListener, View.OnLongClickListener {

    private ClearEditText mClearEditText;
    private TextView dialog;

    private RecyclerView list_friends;


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }
}
