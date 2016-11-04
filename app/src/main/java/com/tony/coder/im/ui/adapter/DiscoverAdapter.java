package com.tony.coder.im.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.orhanobut.logger.Logger;
import com.tony.coder.R;
import com.tony.coder.im.CoderApplication;
import com.tony.coder.im.common.Constants;
import com.tony.coder.im.db.base.DatabaseUtil;
import com.tony.coder.im.entity.DynamicWall.DynamicWall;
import com.tony.coder.im.entity.User;
import com.tony.coder.im.sns.UserHelper;
import com.tony.coder.im.sns.tencent.TencentShare;
import com.tony.coder.im.sns.tencent.TencentShareConstants;
import com.tony.coder.im.sns.tencent.TencentShareEntity;
import com.tony.coder.im.ui.activity.CommentActivity;
import com.tony.coder.im.ui.activity.ImageBrowserActivity;
import com.tony.coder.im.ui.activity.LoginActivity;
import com.tony.coder.im.ui.adapter.base.BaseListAdapter;
import com.tony.coder.im.ui.adapter.base.ViewHolder;
import com.tony.coder.im.utils.ActivityUtil;
import com.tony.coder.im.utils.ImageLoadOptions;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/28 10:49
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class DiscoverAdapter extends BaseListAdapter<DynamicWall> {

    public static final String TAG = "DiscoverAdapter";
    public static final int SAVE_FAVORITE = 2;
    public static final int DYNAMICWALL_ALL = 1;
    private int mDynamicWallType = DYNAMICWALL_ALL;
    public static final int DYNAMICWALL_PERSON = 2;
    public static final int DYNAMICWALL_FAV = 3;
    public static final String VIEW_ID = "view_id_";

    public DiscoverAdapter(Context context, List<DynamicWall> list) {
        super(context, list);
    }

    public DiscoverAdapter(Context context, List<DynamicWall> list, int dynamicWallType) {
        super(context, list);
        this.mDynamicWallType = dynamicWallType;
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_discover, null);
        }
        TextView userName = ViewHolder.getView(convertView, R.id.user_name);
        ImageView userLogo = ViewHolder.getView(convertView, R.id.user_logo);
        ImageView favMark = ViewHolder.getView(convertView, R.id.item_action_fav);
        TextView contentText = ViewHolder.getView(convertView, R.id.content_text);
        final ImageView contentImage = ViewHolder.getView(convertView, R.id.content_image);
        final TextView love = ViewHolder.getView(convertView, R.id.item_action_love);
        final TextView hate = ViewHolder.getView(convertView, R.id.item_action_hate);
        final TextView share = ViewHolder.getView(convertView, R.id.item_action_share);
        final TextView comment = ViewHolder.getView(convertView, R.id.item_action_comment);


        final DynamicWall dynamicWall = getList().get(position);
        Logger.d(TAG, "user = " + dynamicWall.toString());

        User user = dynamicWall.getAuthor();
        if (user == null) {
            Logger.d(TAG, "User is null");
        }
        if (user.getAvatar() == null) {
            Logger.d(TAG, "User avatar is null");
        }

//        String avatarUrl = null;
//        if (user.getAvatarImg() != null) {
//            avatarUrl = user.getAvatar();
//        }
        if (user.getAvatar() != null) {
            ImageLoader.getInstance().displayImage(user.getAvatar(), userLogo, ImageLoadOptions.getOptions());
        } else {
            userLogo.setImageResource(R.drawable.head);
        }

        if (mDynamicWallType == 1 || mDynamicWallType == 3) {
            userLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2016/4/28
                }
            });
        }
        userName.setText(dynamicWall.getAuthor().getUsername());
        contentText.setText(dynamicWall.getContent());

        if (dynamicWall.getContentfigureurl() == null) {
            contentImage.setVisibility(View.GONE);
        } else {
            contentImage.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(dynamicWall.getContentfigureurl().getFileUrl(mContext)
                            == null ? "" : dynamicWall.getContentfigureurl().getFileUrl(mContext),
                    contentImage, ImageLoadOptions.getOptions(R.drawable.bg_pic_loading),
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            float[] cons = ActivityUtil.getBitmapConfiguration(loadedImage, contentImage, 1.0f);
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                    (int) cons[0], (int) cons[1]);
                            layoutParams.addRule(RelativeLayout.BELOW, R.id.content_text);
                            contentImage.setLayoutParams(layoutParams);
                        }
                    });
        }

        contentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImageBrowserActivity.class);
                ArrayList<String> photos = new ArrayList<>();
                photos.add(dynamicWall.getContentfigureurl().getFileUrl(mContext));
                intent.putStringArrayListExtra("photos", photos);
                intent.putExtra("position", 0);
                mContext.startActivity(intent);
            }
        });

        love.setText(dynamicWall.getLove() + "");
        Logger.d(TAG, "love = " + dynamicWall.getLove());
        if (dynamicWall.isMyLove()) {
            love.setTextColor(Color.parseColor("#D95555"));
        } else {
            love.setTextColor(Color.parseColor("#888888"));
        }

        if (DatabaseUtil.getInstance(mContext).isLoved(dynamicWall)) {
            dynamicWall.setMyLove(true);
            dynamicWall.setLove(dynamicWall.getLove());
            love.setTextColor(Color.parseColor("#D95555"));
            love.setText(dynamicWall.getLove() + "");
        }

        hate.setText(dynamicWall.getHate() + "");
        love.setOnClickListener(new View.OnClickListener() {

            boolean oldFav = dynamicWall.isMyFav();

            @Override
            public void onClick(View v) {
                if (dynamicWall.isMyLove()) {
                    love.setTextColor(Color.parseColor("#D95555"));
                    return;
                }

                dynamicWall.setLove(dynamicWall.getLove() + 1);
                love.setTextColor(Color.parseColor("#D95555"));
                love.setText(dynamicWall.getLove() + "");

                dynamicWall.increment("love", 1);
                if (dynamicWall.isMyFav()) {
                    dynamicWall.setMyFav(false);
                }


                dynamicWall.update(mContext, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        dynamicWall.setMyLove(true);
                        dynamicWall.setMyFav(oldFav);
                        DatabaseUtil.getInstance(mContext).insertFav(dynamicWall);
                        Logger.d(TAG, "点赞成功~");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        dynamicWall.setMyLove(true);
                        dynamicWall.setMyFav(oldFav);
                        Logger.d(TAG, "failure = " + s);
                    }
                });
            }
        });
        hate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dynamicWall.setHate(dynamicWall.getHate() + 1);
                hate.setText(dynamicWall.getHate() + "");
                dynamicWall.increment("hate", 1);
                dynamicWall.update(mContext, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        showToast("点踩成功~");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Logger.d(TAG, "failure = " + s);
                    }
                });
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TencentShare tencentShare = new TencentShare(CoderApplication.getInstance().getTopActivity(),
                        getQQShareEntity(dynamicWall));
                tencentShare.shareToQQ();
            }
        });
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserHelper.getCurrentUser() == null) {
                    showToast("请先登录~");
                    Intent intent = new Intent();
                    intent.setClass(mContext, LoginActivity.class);
                    CoderApplication.getInstance().getTopActivity().startActivity(intent);
                    return;
                }

                Intent intent = new Intent();
                intent.setClass(CoderApplication.getInstance().getTopActivity(), CommentActivity.class);
                intent.putExtra("data", dynamicWall);
                mContext.startActivity(intent);
            }
        });
        if (dynamicWall.isMyFav()) {
            favMark.setImageResource(R.drawable.ic_action_fav_choose);
        } else {
            favMark.setImageResource(R.drawable.ic_action_fav_normal);
        }
        favMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //收藏
                showToast("收藏");
                onClickFav(v, dynamicWall);
            }
        });
        return convertView;
    }

    private void onClickFav(View view, final DynamicWall dynamicWall) {
        User user = BmobUser.getCurrentUser(mContext, User.class);
        if (user != null && user.getSessionToken() != null) {
            BmobRelation favRelation = new BmobRelation();

            dynamicWall.setMyFav(!dynamicWall.isMyFav());
            if (dynamicWall.isMyFav()) {
                ((ImageView) view).setImageResource(R.drawable.ic_action_fav_choose);
                favRelation.add(dynamicWall);
                user.setFavorite(favRelation);
                showToast("收藏成功~");
                user.update(mContext, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        DatabaseUtil.getInstance(mContext).insertFav(dynamicWall);
                        Logger.d(TAG, "收藏成功~");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Logger.e(TAG, "收藏失败，请检查网络~");
                        showToast("收藏失败，请检查网络~" + s);
                    }
                });
            } else {
                ((ImageView) view).setImageResource(R.drawable.ic_action_fav_normal);
                favRelation.add(dynamicWall);
                showToast("取消收藏~");
                user.setFavorite(favRelation);
                user.update(mContext, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        DatabaseUtil.getInstance(mContext).deleteFav(dynamicWall);
                        Logger.d(TAG, "收藏成功~");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Logger.d(TAG, "收藏失败，请检查网络~");
                        showToast("收藏失败，请检查网络~" + s);
                    }
                });
            }
        }
    }

    private void getMyFavorite() {
        User user = BmobUser.getCurrentUser(mContext, User.class);
        if (user != null) {
            BmobQuery<DynamicWall> query = new BmobQuery<>();
            query.addWhereRelatedTo("favorite", new BmobPointer(user));
            query.include("user");
            query.order("createdAt");
            query.setLimit(Constants.NUMBERS_PER_PAGE);
            query.findObjects(mContext, new FindListener<DynamicWall>() {
                @Override
                public void onSuccess(List<DynamicWall> list) {
                    Logger.d(TAG, "get fav success = " + list.size());
                    showToast("fav size : " + list.size());
                }

                @Override
                public void onError(int i, String s) {
                    showToast("获取收藏失败，请检查网络~");
                }
            });
        }
    }

    private TencentShareEntity getQQShareEntity(DynamicWall wall) {
        String imgUrl = "";
        if (wall.getContentfigureurl() != null) {
            imgUrl = wall.getContentfigureurl().getFileUrl(mContext);
        } else {
            imgUrl = TencentShareConstants.DEFAULT_IMG_URL;
        }
        String summary = wall.getContent();
        TencentShareEntity entity = new TencentShareEntity(TencentShareConstants.TITLE, imgUrl,
                TencentShareConstants.WEB, summary, TencentShareConstants.COMMENT);
        return entity;
    }
}
