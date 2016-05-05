package com.tony.coder.im.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.orhanobut.logger.Logger;
import com.tony.coder.R;
import com.tony.coder.im.CoderApplication;
import com.tony.coder.im.common.Constants;
import com.tony.coder.im.db.base.DatabaseUtil;
import com.tony.coder.im.entity.DynamicWall.Comment;
import com.tony.coder.im.entity.DynamicWall.DynamicWall;
import com.tony.coder.im.entity.User;
import com.tony.coder.im.sns.tencent.TencentShare;
import com.tony.coder.im.sns.tencent.TencentShareConstants;
import com.tony.coder.im.sns.tencent.TencentShareEntity;
import com.tony.coder.im.ui.adapter.CommentAdapter;
import com.tony.coder.im.utils.ActivityUtil;
import com.tony.coder.im.utils.ImageLoadOptions;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/28 14:56
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class CommentActivity extends ActivityBase implements View.OnClickListener {

    private final static String COMMENT_ID = "comment_id_";
    private DynamicWall mDynamicWall;
    private boolean isFav = false;
    private ListView commentList;
    private TextView footer;
    private EditText commentContent;
    private Button commentCommit;
    private TextView userName;
    private TextView commentItemContent;
    private ImageView commentItemImage;
    private ImageView userLogo;
    private ImageView myFav;
    private TextView comment;
    private TextView share;
    private TextView love;
    private String commentEdit = "";
    private CommentAdapter mAdapter;
    private List<Comment> comments = new ArrayList<>();
    private int pageNum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        initView();
    }

    private void findView() {
        setContentView(R.layout.activity_comment);
        commentList = (ListView) findViewById(R.id.comment_list);
        footer = (TextView) findViewById(R.id.loadmore);

        commentContent = (EditText) findViewById(R.id.comment_content);
        commentCommit = (Button) findViewById(R.id.comment_commit);

        userName = (TextView) findViewById(R.id.user_name);
        commentItemContent = (TextView) findViewById(R.id.content_text);
        commentItemImage = (ImageView) findViewById(R.id.content_image);

        userLogo = (ImageView) findViewById(R.id.user_logo);
        myFav = (ImageView) findViewById(R.id.item_action_fav);
        comment = (TextView) findViewById(R.id.item_action_comment);
        share = (TextView) findViewById(R.id.item_action_share);
        love = (TextView) findViewById(R.id.item_action_love);
    }

    private void initView() {
        initTopBarForLeft("评论");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mDynamicWall = (DynamicWall) getIntent().getSerializableExtra("data");
        CoderApplication.getInstance().setCurrentDynamicWall(mDynamicWall);

        pageNum = 0;
        mAdapter = new CommentAdapter(CommentActivity.this, comments);
        commentList.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(commentList);
        commentList.setCacheColorHint(0);
        commentList.setScrollingCacheEnabled(false);
        commentList.setScrollContainer(false);
        commentList.setFastScrollEnabled(true);
        commentList.setSmoothScrollbarEnabled(true);
        onClickLoadMore();
        bindEvent();

        initMoodView(mDynamicWall);
    }

    private void onClickLoadMore() {
        fetchData();
    }

    private void bindEvent() {
        footer.setOnClickListener(this);
        commentCommit.setOnClickListener(this);
        userLogo.setOnClickListener(this);
        myFav.setOnClickListener(this);
        love.setOnClickListener(this);
        share.setOnClickListener(this);
        comment.setOnClickListener(this);
        commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_logo:
                onClickUserLogo();
                break;
            case R.id.loadmore:
                onClickLoadMore();
                break;
            case R.id.comment_commit:
                onClickCommit();
                break;
            case R.id.item_action_fav:
                onClickFav(v);
                break;
            case R.id.item_action_love:
                onClickLove();
                break;
            case R.id.item_action_share:
                onClickShare();
                break;
            case R.id.item_action_comment:
                onClickComment();
                break;
            default:
                break;
        }
    }

    private void onClickUserLogo() {
        //跳转到个人信息页面
        User currentUser = BmobUser.getCurrentUser(this, User.class);
        if (currentUser != null) {//已登录
            Intent intent = new Intent();
            intent.setClass(CoderApplication.getInstance().getTopActivity(), PersonalActivity.class);
            CommentActivity.this.startAnimActivity(intent);
        } else {
            showToast("请先登录~~");
            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            startActivityForResult(intent, Constants.GO_SETTINGS);
        }
    }

    private void onClickComment() {
        commentContent.requestFocus();
        InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(commentContent, 0);
    }

    private void onClickShare() {
        final TencentShare tencentShare = new TencentShare(
                CoderApplication.getInstance().getTopActivity(), getQQShareEntity(mDynamicWall));
        tencentShare.shareToQQ();
    }

    private TencentShareEntity getQQShareEntity(DynamicWall dynamicWall) {
        String img ;
        if (dynamicWall.getContentfigureurl() != null) {
            img = dynamicWall.getContentfigureurl().getFileUrl(mContext);
        } else {
            img = TencentShareConstants.DEFAULT_IMG_URL;
        }
        String summary = dynamicWall.getContent();
        TencentShareEntity entity = new TencentShareEntity(TencentShareConstants.TITLE, img, TencentShareConstants.TARGET_URL,
                summary, TencentShareConstants.COMMENT);
        return entity;
    }

    private void onClickLove() {
        User user = BmobUser.getCurrentUser(this, User.class);
        if (user == null) {
            showToast("请先登录~~");
            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            startActivity(intent);
            return;
        }
        if (mDynamicWall.isMyLove()) {
            showToast("您已经赞过了啦~~");
            return;
        }
        isFav = mDynamicWall.isMyFav();
        if (isFav) {
            mDynamicWall.setMyFav(false);
        }
        mDynamicWall.setLove(mDynamicWall.getLove() + 1);
        love.setTextColor(Color.parseColor("#D95555"));
        love.setText(mDynamicWall.getLove() + "");
        mDynamicWall.increment("love", 1);
        mDynamicWall.update(CommentActivity.this, new UpdateListener() {
            @Override
            public void onSuccess() {
                mDynamicWall.setMyFav(true);
                mDynamicWall.setMyFav(isFav);
                DatabaseUtil.getInstance(CommentActivity.this).insertFav(mDynamicWall);

                showToast("点赞成功~");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    private void onClickCommit() {
        User currentUser = BmobUser.getCurrentUser(this, User.class);
        if (currentUser != null) {
            commentEdit = commentContent.getText().toString().trim();
            if (TextUtils.isEmpty(commentEdit)) {
                showToast("评论内容不能为空~~");
                return;
            }
            //发布评论
            publishComment(currentUser, commentEdit);
        } else {
            showToast("发表评论前请先登录~~");
            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            startActivityForResult(intent, Constants.PUBLISH_COMMENT);
        }
    }

    private void publishComment(User user, final String content) {
        commentCommit.setEnabled(false);
        final Comment comment = new Comment();
        comment.setUser(user);
        comment.setCommentContent(content);
        comment.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                showToast("评论成功~~");
                if (mAdapter.getCount() < Constants.NUMBERS_PER_PAGE) {
                    mAdapter.add(comment);
                    mAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(commentList);
                    commentCommit.setEnabled(true);
                }
                commentContent.setText("");
                hideSoftInput();

                //将评论与动态绑定
                BmobRelation relation = new BmobRelation();
                relation.add(comment);
                mDynamicWall.setRelation(relation);
                mDynamicWall.update(CommentActivity.this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Logger.d(TAG, "更新评论成功~~");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Logger.e(TAG, "更新评论失败。" + s);
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                commentCommit.setEnabled(true);
                showToast("评论失败，请检查网络~~");
            }
        });
    }

    private void onClickFav(View view) {
        User user = BmobUser.getCurrentUser(this, User.class);
        if (user != null && user.getSessionToken() != null) {
            BmobRelation favRelation = new BmobRelation();
            mDynamicWall.setMyFav(!mDynamicWall.isMyFav());
            if (mDynamicWall.isMyFav()) {
                ((ImageView) view).setImageResource(R.drawable.ic_action_fav_choose);
                favRelation.add(mDynamicWall);
                showToast("收藏成功~~");
            } else {
                ((ImageView) view).setImageResource(R.drawable.ic_action_fav_normal);
                favRelation.remove(mDynamicWall);
                showToast("取消收藏~~");
            }
            user.setFavorite(favRelation);
            user.update(this, new UpdateListener() {
                @Override
                public void onSuccess() {
                    Logger.d(TAG, "收藏成功~~");
                    showToast("收藏成功~~");
                }

                @Override
                public void onFailure(int i, String s) {
                    Logger.d(TAG, "收藏失败。请检查网络~~" + s);
                    showToast("收藏失败。请检查网络~~");
                }
            });
        } else {
            //前往登陆注册页面
            showToast("请先登录~~");
            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            startActivityForResult(intent, Constants.SAVE_FAVOURITE);
        }
    }

    private void getMyFavorite() {
        User user = BmobUser.getCurrentUser(this, User.class);
        if (user != null) {
            BmobQuery<DynamicWall> query = new BmobQuery<>();
            query.addWhereRelatedTo("favorite", new BmobPointer(user));
            query.include("user");
            query.order("createdAt");
            query.setLimit(Constants.NUMBERS_PER_PAGE);
            query.findObjects(this, new FindListener<DynamicWall>() {
                @Override
                public void onSuccess(List<DynamicWall> list) {
                    Logger.d(TAG, "get fav success" + list.size());
                }

                @Override
                public void onError(int i, String s) {
                    showToast("获取收藏失败。请检查网络~~");
                }
            });
        } else {
            showToast("请先登录~~");

            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            startActivityForResult(intent, Constants.SAVE_FAVOURITE);
        }
    }

    private void hideSoftInput() {
        InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(commentContent.getWindowToken(), 0);
    }

    protected void fetchData() {
        fetchComment();
    }

    private void fetchComment() {
        BmobQuery<Comment> query = new BmobQuery<>();
        query.addWhereRelatedTo("relation", new BmobPointer(mDynamicWall));
        query.include("user");
        query.order("createdAt");
        query.setLimit(Constants.NUMBERS_PER_PAGE);
        query.setSkip(Constants.NUMBERS_PER_PAGE * (pageNum++));
        query.findObjects(this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                Logger.d(TAG, "get comment success!" + list.size());
                if (list.size() != 0 && list.get(list.size() - 1) != null) {
                    if (list.size() < Constants.NUMBERS_PER_PAGE) {
                        footer.setText("暂无更多评论~");
                    }
                    mAdapter.addAll(list);
                    mAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(commentList);
                    Logger.d(TAG, "refresh");
                } else {
                    footer.setText("暂无更多评论~");
                    pageNum--;
                }
            }

            @Override
            public void onError(int i, String s) {
                showToast("获取评论失败。请检查网络~~");
                Logger.d(TAG, "onError = " + s);
                pageNum--;
            }
        });
    }

    private void initMoodView(DynamicWall dynamicWall) {
        if (dynamicWall == null) {
            return;
        }
        userName.setText(mDynamicWall.getAuthor().getNick());
        commentItemContent.setText(mDynamicWall.getContent());
        if (mDynamicWall.getContentfigureurl() == null) {
            commentItemImage.setVisibility(View.GONE);
        } else {
            commentItemImage.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(
                    mDynamicWall.getContentfigureurl().getFileUrl(mContext) == null ? "" :
                            mDynamicWall.getContentfigureurl().getFileUrl(mContext), commentItemImage,
                    ImageLoadOptions.getOptions(R.drawable.bg_pic_loading), new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            float[] cons = ActivityUtil.getBitmapConfiguration(loadedImage, commentItemImage, 1.0f);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    (int) cons[0], (int) cons[1]);
                            params.addRule(RelativeLayout.BELOW, R.id.content_text);
                            commentItemImage.setLayoutParams(params);
                        }
                    }
            );
            commentItemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                    ArrayList<String> photos = new ArrayList<>();
                    photos.add(mDynamicWall.getContentfigureurl().getFileUrl(mContext));
                    intent.putStringArrayListExtra("photos", photos);
                    intent.putExtra("position", 0);
                    startActivity(intent);
                }
            });
        }
        love.setText(mDynamicWall.getLove() + "");
        if (mDynamicWall.isMyLove()) {
            love.setTextColor(Color.parseColor("#D95555"));
        } else {
            love.setTextColor(Color.parseColor("#000000"));
        }
        if (mDynamicWall.isMyFav()) {
            myFav.setImageResource(R.drawable.ic_action_fav_choose);
        } else {
            myFav.setImageResource(R.drawable.ic_action_fav_normal);
        }

        User user = mDynamicWall.getAuthor();
        ImageLoader.getInstance().displayImage(user.getAvatar(), userLogo, ImageLoadOptions.getOptions(),
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.PUBLISH_COMMENT:
                    //登录完成
                    commentCommit.performClick();
                    break;
                case Constants.SAVE_FAVOURITE:
                    myFav.performClick();
                    break;
                case Constants.GET_FAVOURITE:
                    break;
                case Constants.GO_SETTINGS:
                    userLogo.performClick();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 动态设置listview的高度
     * item 总布局必须是linearlayout
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 15;
        listView.setLayoutParams(params);
    }
}
