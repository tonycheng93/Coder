package com.tony.coder.im.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.tony.coder.R;
import com.tony.coder.im.CoderApplication;
import com.tony.coder.im.entity.User;
import com.tony.coder.im.ui.activity.AddFriendActivity;
import com.tony.coder.im.ui.activity.NearPeopleActivity;
import com.tony.coder.im.ui.activity.NewFriendActivity;
import com.tony.coder.im.ui.activity.SetMyInfoActivity;
import com.tony.coder.im.ui.adapter.UserFriendAdapter;
import com.tony.coder.im.utils.CharacterParser;
import com.tony.coder.im.utils.CollectionUtils;
import com.tony.coder.im.utils.PinyinComparator;
import com.tony.coder.im.widget.ClearEditText;
import com.tony.coder.im.widget.LetterView;
import com.tony.coder.im.widget.TitleBarBuilder;
import com.tony.coder.im.widget.dialog.DialogTips;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

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
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ClearEditText mClearEditText;
    private TextView dialog;

    private ListView list_friends;
    LetterView right_letter;

    private UserFriendAdapter userAdapter;//好友

    List<User> friends = new ArrayList<>();

    private InputMethodManager mInputMethodManager;
    /**
     * 汉字转拼音的类
     */
    private CharacterParser mCharacterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator mPinyinComparator;

    private ImageView iv_msg_tips;


    private LinearLayout layout_new;//新朋友
    private LinearLayout layout_near;//附近的人

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        init();
        Logger.d("TAG", "onActivityCreated");
    }

    private void init() {
        mCharacterParser = CharacterParser.getInstance();
        mPinyinComparator = new PinyinComparator();
       /* initTopBarForRight("联系人", R.drawable.base_action_bar_add_bg_selector, new
                HeaderLayout.onRightImageButtonClickListener() {
                    @Override
                    public void onClick() {
                        startAnimActivity(AddFriendActivity.class);
                    }
                });*/

        new TitleBarBuilder(getActivity())
                .setTitleText("联系人")
                .setRightImage(R.drawable.base_action_bar_add_bg_selector)
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startAnimActivity(AddFriendActivity.class);
                    }
                })
                .build();

        initListView();
        initRightLetterView();
        initEditText();
    }

    private void initListView() {
        list_friends = (ListView) findViewById(R.id.list_friends);
        RelativeLayout headView = (RelativeLayout) mInflater.inflate(R.layout.include_new_friend, null);

        iv_msg_tips = (ImageView) headView.findViewById(R.id.iv_msg_tips);
        layout_new = (LinearLayout) headView.findViewById(R.id.layout_new);
        layout_near = (LinearLayout) headView.findViewById(R.id.layout_near);

        layout_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewFriendActivity.class);
                intent.putExtra("from", "contact");
                startAnimActivity(intent);
            }
        });

        layout_near.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NearPeopleActivity.class);
                startAnimActivity(intent);
            }
        });

        list_friends.addHeaderView(headView);
        userAdapter = new UserFriendAdapter(getActivity(), friends);
        list_friends.setAdapter(userAdapter);
        list_friends.setOnItemClickListener(this);
        list_friends.setOnItemLongClickListener(this);

        list_friends.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //隐藏软键盘
                if (getActivity().getWindow().getAttributes().softInputMode
                        != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getActivity().getCurrentFocus() != null) {
                        mInputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            queryMyfriends();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    /**
     * 获取好友列表
     */
    private void queryMyfriends() {
        //是否有新的好友请求
        if (BmobDB.create(getActivity()).hasNewInvite()) {
            iv_msg_tips.setVisibility(View.VISIBLE);
        } else {
            iv_msg_tips.setVisibility(View.GONE);
        }
        //在这里再做一次本地的好友数据库的检查，是为了本地好友数据库中已经添加了对方，但是界面却没有显示出来的问题
        // 重新设置下内存中保存的好友列表
        CoderApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(getActivity())
                .getContactList()));
        Map<String, BmobChatUser> users = CoderApplication.getInstance().getContactList();
        //组装新的User
        filledData(CollectionUtils.map2list(users));
        if (userAdapter == null) {
            userAdapter = new UserFriendAdapter(getActivity(), friends);
            list_friends.setAdapter(userAdapter);
        } else {
            userAdapter.notifyDataSetChanged();
        }
    }

    private boolean hidden;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

    public void refresh() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    queryMyfriends();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRightLetterView() {
        right_letter = (LetterView) findViewById(R.id.right_letter);
        dialog = (TextView) findViewById(R.id.dialog);
        right_letter.setTextView(dialog);
        right_letter.setOnTouchingLetterChangedListener(new LetterListViewListener());
    }

    private class LetterListViewListener implements LetterView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(String s) {
            //该字母首次出现的位置
            int position = userAdapter.getPositionForSection(s.charAt(0));
            if (position != -1) {
                list_friends.setSelection(position);
            }
        }
    }

    private void initEditText() {
        mClearEditText = (ClearEditText) findViewById(R.id.et_msg_search);
        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<User> filterDataList = new ArrayList<>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDataList = friends;
        } else {
            filterDataList.clear();
            for (User sortModel : friends) {
                String name = sortModel.getUsername();
                if (name != null) {
                    if (name.indexOf(filterStr.toString()) != -1
                            || mCharacterParser.getSelling(name).startsWith(
                            filterStr.toString())) {
                        filterDataList.add(sortModel);
                    }
                }
            }
        }
        //根据a-z排序
        Collections.sort(filterDataList, mPinyinComparator);
        userAdapter.updateListView(filterDataList);
    }

    private void filledData(List<BmobChatUser> datas) {
        friends.clear();
        int total = datas.size();
        for (int i = 0; i < total; i++) {
            BmobChatUser user = datas.get(i);
            User sortModel = new User();
            sortModel.setAvatar(user.getAvatar());
            sortModel.setNick(user.getNick());
            sortModel.setUsername(user.getUsername());
            sortModel.setObjectId(user.getObjectId());
            sortModel.setContacts(user.getContacts());
            //汉字转换成拼音
            String userName = sortModel.getUsername();
            //若没有userName
            if (userName != null) {
                String pinyin = mCharacterParser.getSelling(sortModel.getUsername());
                String sortString = pinyin.substring(0, 1).toUpperCase();
                //正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    sortModel.setSortLetters(sortString.toUpperCase());
                } else {
                    sortModel.setSortLetters("#");
                }
            } else {
                sortModel.setSortLetters("#");
            }
            friends.add(sortModel);
        }
        //根据a-z排序
        Collections.sort(friends, mPinyinComparator);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        User user = (User) userAdapter.getItem(position - 1);
        //先进入好友的详细资料页面
        Intent intent = new Intent(getActivity(), SetMyInfoActivity.class);
        intent.putExtra("from", "other");
        intent.putExtra("username", user.getUsername());
        startAnimActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        User user = (User) userAdapter.getItem(position - 1);
        showDeleteDialog(user);
        return true;
    }

    public void showDeleteDialog(final User user) {
        DialogTips dialogTips = new DialogTips(getActivity(), user.getUsername(), "删除联系人",
                "确定", true, true);
        //设置成功事件
        dialogTips.setOnSuccessListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteContact(user);
            }
        });
        //显示确认对话框
        dialogTips.show();
        dialogTips = null;
    }

    /**
     * 删除联系人
     *
     * @param user
     */
    private void deleteContact(final User user) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在删除...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        mUserManager.deleteContact(user.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                showToast("删除成功");
                //删除内存
                CoderApplication.getInstance().getContactList().remove(user.getUsername());
                //更新界面
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        userAdapter.remove(user);
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                showToast("删除失败:" + s);
                progressDialog.dismiss();
            }
        });
    }
}
