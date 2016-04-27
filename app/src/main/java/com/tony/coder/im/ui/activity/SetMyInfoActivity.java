package com.tony.coder.im.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.orhanobut.logger.Logger;
import com.tony.coder.R;
import com.tony.coder.im.CoderApplication;
import com.tony.coder.im.common.Constants;
import com.tony.coder.im.entity.User;
import com.tony.coder.im.utils.CollectionUtils;
import com.tony.coder.im.utils.ImageLoadOptions;
import com.tony.coder.im.utils.PhotoUtil;
import com.tony.coder.im.widget.dialog.DialogTips;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/12 18:04
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class SetMyInfoActivity extends ActivityBase implements View.OnClickListener {
    @Bind(R.id.tv_set_name)
    TextView tv_set_name;
    @Bind(R.id.tv_set_nick)
    TextView tv_set_nick;
    @Bind(R.id.tv_set_gender)
    TextView tv_set_gender;
    @Bind(R.id.iv_set_avator)
    ImageView iv_set_avatar;
    @Bind(R.id.iv_arraw)
    ImageView iv_arraw;
    @Bind(R.id.iv_nickarraw)
    ImageView iv_nickarraw;
    @Bind(R.id.layout_all)
    LinearLayout layout_all;
    @Bind(R.id.btn_chat)
    Button btn_chat;
    @Bind(R.id.btn_back)
    Button btn_back;
    @Bind(R.id.btn_add_friend)
    Button btn_add_firend;
    @Bind(R.id.layout_head)
    RelativeLayout layout_head;
    @Bind(R.id.layout_nick)
    RelativeLayout layout_nick;
    @Bind(R.id.layout_gender)
    RelativeLayout layout_gender;
    @Bind(R.id.layout_black_tips)
    RelativeLayout layout_black_tips;

    String from = "";
    String username = "";
    User user;

    boolean isFromCamera = false;//区分拍照旋转
    int degree = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 因为魅族手机下面有三个虚拟的导航按钮，需要将其隐藏掉，
        // 不然会遮掉拍照和相册两个按钮，且在setContentView之前调用才能生效
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= 14) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        setContentView(R.layout.activity_set_info);
        ButterKnife.bind(this);
        from = getIntent().getStringExtra("from");//me add other
        username = getIntent().getStringExtra("username");

        initView();

    }

    private void initView() {
        btn_add_firend.setEnabled(false);
        btn_chat.setEnabled(false);
        btn_back.setEnabled(false);
        if (from.equals("me")) {
            initTopBarForLeft("个人资料");
            layout_head.setOnClickListener(this);
            layout_nick.setOnClickListener(this);
            layout_gender.setOnClickListener(this);

            iv_nickarraw.setVisibility(View.VISIBLE);
            iv_arraw.setVisibility(View.VISIBLE);
            btn_back.setVisibility(View.GONE);
            btn_chat.setVisibility(View.GONE);
            btn_add_firend.setVisibility(View.GONE);
        } else {
            initTopBarForLeft("详细资料");
            iv_nickarraw.setVisibility(View.INVISIBLE);
            iv_arraw.setVisibility(View.INVISIBLE);
            //不管对方是不是你的好友，均可以发送消息
            btn_chat.setVisibility(View.VISIBLE);
            btn_chat.setOnClickListener(this);
            if (from.equals("add")) {// 从附近的人列表添加好友--因为获取附近的人的方法里面有是否显示好友的情况，因此在这里需要判断下这个用户是否是自己的好友
                if (mCoderApplication.getContactList().containsKey(username)) {//是好友
                    btn_back.setVisibility(View.VISIBLE);
                    btn_back.setOnClickListener(this);
                } else {
                    btn_back.setVisibility(View.GONE);
                    btn_add_firend.setVisibility(View.VISIBLE);
                    btn_add_firend.setOnClickListener(this);
                }
            } else {//查看他人
                btn_back.setVisibility(View.VISIBLE);
                btn_back.setOnClickListener(this);
            }
            initOtherData(username);
        }
    }

    private void initMeData() {
        User user = mUserManager.getCurrentUser(User.class);
        BmobLog.i("hight = " + user.getHight() + ",sex = " + user.getSex());
        initOtherData(user.getUsername());
    }

    private void initOtherData(String username) {
        mUserManager.queryUser(username, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list != null && list.size() > 0) {
                    user = list.get(0);
                    btn_chat.setEnabled(true);
                    btn_back.setEnabled(true);
                    btn_add_firend.setEnabled(true);
                    updateUser(user);
                } else {
                    showLog("onSuccess 查无此人");
                }
            }

            @Override
            public void onError(int i, String s) {
                showLog("onError " + s);
            }
        });
    }

    private void updateUser(User user) {
        refreshAvatar(user.getAvatar());
        tv_set_name.setText(user.getUsername());
        tv_set_nick.setText(user.getNick());
        tv_set_gender.setText(user.getSex() == true ? "男" : "女");
        //检测是否为黑名单用户
        if (from.equals("other")) {
            if (BmobDB.create(this).isBlackUser(user.getUsername())) {
                btn_back.setVisibility(View.GONE);
                layout_black_tips.setVisibility(View.VISIBLE);
            } else {
                btn_back.setVisibility(View.VISIBLE);
                layout_black_tips.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 更新头像
     *
     * @param avatar
     */
    private void refreshAvatar(String avatar) {
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar,iv_set_avatar, ImageLoadOptions.getOptions());
//      todo      ImageLoaderUtils.display(this, iv_set_avatar, avatar);
        } else {
            iv_set_avatar.setImageResource(R.drawable.default_head);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (from.equals("me")) {
            initMeData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_chat://发起聊天
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("user", user);
                startAnimActivity(intent);
                finish();
                break;
            case R.id.layout_head:
                showAvatarPop();
                break;
            case R.id.layout_nick:
                startAnimActivity(UpdateInfoActivity.class);
                break;
            case R.id.layout_gender://性别
                showSexChooseDialog();
                break;
            case R.id.btn_back://黑名单
                showBlackDialog(user.getUsername());
                break;
            case R.id.btn_add_friend://添加好友
                addFriend();
                break;
            default:
                break;
        }
    }

    /**
     * 添加好友请求
     */
    private void addFriend() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在添加...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        //发送tag请求
        BmobChatManager.getInstance(this).sendTagMessage(BmobConfig.TAG_ADD_CONTACT, user.getObjectId(),
                new PushListener() {
                    @Override
                    public void onSuccess() {
                        progressDialog.dismiss();
                        showToast("发送请求成功，等待对方验证！");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        progressDialog.dismiss();
                        showToast("发送请求成功，等待对方验证！");
                        showLog("发送请求失败:" + s);

                    }
                });
    }

    String[] sexs = new String[]{"男", "女"};

    private void showSexChooseDialog() {
        new AlertDialog.Builder(this)
                .setTitle("单选框")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(sexs, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BmobLog.i("点击的按钮是：" + sexs[which]);
                        updateInfo(which);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 修改资料
     *
     * @param which
     */
    private void updateInfo(int which) {
        final User mUser = new User();
        if (which == 0) {
            mUser.setSex(true);
        } else {
            mUser.setSex(false);
        }
        updateUserData(mUser, new UpdateListener() {
            @Override
            public void onSuccess() {
                showToast("修改成功");
                tv_set_gender.setText(mUser.getSex() == true ? "男" : "女");
            }

            @Override
            public void onFailure(int i, String s) {
                showToast("onFailure:" + s);
            }
        });
    }

    private void updateUserData(User mUser, UpdateListener listener) {
        User current = (User) mUserManager.getCurrentUser(User.class);
        mUser.setObjectId(current.getObjectId());
        mUser.update(this, listener);
    }

    /**
     * 显示黑名单提示框
     *
     * @param username
     */
    private void showBlackDialog(final String username) {
        DialogTips dialogTips = new DialogTips(this, "加入黑名单", "加入黑名单，你将不再收到对方的消息，确定要继续吗？"
                , "确定", true, true);
        dialogTips.setOnSuccessListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //添加到黑名单列表
                mUserManager.addBlack(username, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        showToast("黑名单添加成功！");
                        btn_back.setVisibility(View.GONE);
                        layout_black_tips.setVisibility(View.VISIBLE);
                        //重新设置下内存中保存的好友列表
                        CoderApplication.getInstance().setContactList(CollectionUtils.list2map(
                                BmobDB.create(SetMyInfoActivity.this).getContactList()));
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        showToast("黑名单添加失败：" + s);
                    }
                });

            }
        });
        //显示确认对话框
        dialogTips.show();
        dialogTips = null;
    }

    RelativeLayout layout_choose;
    RelativeLayout layout_photo;
    PopupWindow avatorPop;

    public String filePath = "";

    private void showAvatarPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator, null);
        layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
        layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
        layout_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("点击拍照");
                layout_choose.setBackgroundColor(getResources().getColor(R.color.base_color_text_white));
                layout_photo.setBackgroundDrawable(getResources().getDrawable(R.drawable.pop_bg_press));

                File dir = new File(Constants.MyAvatarDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                //原图
                File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
                filePath = file.getAbsolutePath();//获取相片的保存路径
                Uri imageUri = Uri.fromFile(file);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, Constants.REQUESTCODE_UPLOADAVATAR_CAMERA);
            }
        });
        layout_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("点击相册");
                layout_photo.setBackgroundColor(getResources().getColor(R.color.base_color_text_white));
                layout_choose.setBackgroundDrawable(getResources().getDrawable(R.drawable.pop_bg_press));
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, Constants.REQUESTCODE_UPLOADAVATAR_LOCATION);
            }
        });

        avatorPop = new PopupWindow(view, mScreenWidth, 600);
        avatorPop.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    avatorPop.dismiss();
                    return true;
                }
                return false;
            }
        });

        avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        avatorPop.setTouchable(true);
        avatorPop.setFocusable(true);
        avatorPop.setOutsideTouchable(true);
        avatorPop.setBackgroundDrawable(new BitmapDrawable());
        //动画效果，从底部弹起
        avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
        avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
    }

    /**
     * @param uri
     * @param outputX
     * @param outputY
     * @param requestCode
     * @param isCrop
     */
    private void startIamgeAction(Uri uri, int outputX, int outputY, int requestCode, boolean isCrop) {
        Intent intent = null;
        if (isCrop) {
            intent = new Intent("com.android.camera.action.CROP");
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUESTCODE_UPLOADAVATAR_CAMERA://拍照修改头像
                if (resultCode == RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        showToast("SD卡不可用");
                        return;
                    }
                    isFromCamera = true;
                    File file = new File(filePath);
                    degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
                    Logger.i("Coder", "拍照后的角度：" + degree);
                    startIamgeAction(Uri.fromFile(file), 200, 200,
                            Constants.REQUESTCODE_UPLOADAVATAR_CROP, true);
                }
                break;
            case Constants.REQUESTCODE_UPLOADAVATAR_LOCATION://本地修改头像
                if (avatorPop != null) {
                    avatorPop.dismiss();
                }
                Uri uri = null;
                if (data == null) {
                    return;
                }
                if (resultCode == RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        showToast("SD卡不可用");
                        return;
                    }
                    isFromCamera = false;
                    uri = data.getData();
                    startIamgeAction(uri, 200, 200, Constants.REQUESTCODE_UPLOADAVATAR_CROP, true);
                } else {
                    showToast("照片获取失败");
                }
                break;
            case Constants.REQUESTCODE_UPLOADAVATAR_CROP://裁剪头像返回
                if (avatorPop != null) {
                    avatorPop.dismiss();
                }
                if (data == null) {
                    return;
                } else {
                    saveCropAvator(data);
                }
                //初始化文件路径
                filePath = "";
                //上传头像
                uploadAvatar();
                break;
            default:
                break;
        }
    }

    private void uploadAvatar() {
        BmobLog.i("头像地址：" + path);
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.upload(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                String url = bmobFile.getFileUrl(SetMyInfoActivity.this);
                //更新BmobUser对象
                updateUserAvatar(url);

            }

            @Override
            public void onFailure(int i, String s) {
                showToast("上传头像失败：" + s);
            }
        });
    }

    private void updateUserAvatar(final String url) {
        User u = new User();
        u.setAvatar(url);
        updateUserData(u, new UpdateListener() {

            @Override
            public void onSuccess() {
                showToast("头像更新成功！");
                //更新头像
                refreshAvatar(url);
            }

            @Override
            public void onFailure(int i, String s) {
                showToast("头像更新失败：" + s);
            }
        });
    }

    String path;

    /**
     * 保存剪裁的头像
     *
     * @param data
     */
    public void saveCropAvator(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            Logger.d("Coder", "avatar - bitmap" + bitmap);
            if (bitmap != null) {
//                bitmap = PhotoUtil.toRoundBitmap(bitmap);
                if (isFromCamera && degree != 0) {
                    bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
                }
                iv_set_avatar.setImageBitmap(bitmap);
                //保存图片
                String fileName = new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".png";
                path = Constants.MyAvatarDir + fileName;
                PhotoUtil.saveBitmap(Constants.MyAvatarDir, fileName, bitmap, true);
                //上传头像
                if (bitmap != null && bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    }
}
