package com.tony.coder.im.ui.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.utils.L;
import com.orhanobut.logger.Logger;
import com.tony.coder.R;
import com.tony.coder.im.entity.DynamicWall.DynamicWall;
import com.tony.coder.im.entity.User;
import com.tony.coder.im.utils.CacheUtils;
import com.tony.coder.im.widget.HeaderLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 项目名称：Coder
 * 类描述：发表动态页面
 * 创建人：tonycheng
 * 创建时间：2016/4/29 9:18
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class NewDynamicWallActivity extends ActivityBase implements View.OnClickListener {

    private static final int REQUEST_CODE_ALBUM = 1;
    private static final int REQUEST_CODE_CAMERA = 2;

    private EditText content;
    private LinearLayout openLayout;
    private LinearLayout takeLayout;
    private ImageView albumPic;
    private ImageView takePic;

    private String dateTime;
    private String targetUrl = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        initView();
    }

    private void findView() {
        setContentView(R.layout.activity_new_dynamicwall);

        content = (EditText) findViewById(R.id.edit_content);
        openLayout = (LinearLayout) findViewById(R.id.open_layout);
        takeLayout = (LinearLayout) findViewById(R.id.take_layout);
        albumPic = (ImageView) findViewById(R.id.open_pic);
        takePic = (ImageView) findViewById(R.id.take_pic);
    }

    private void initView() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_VISIBLE |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initTopBarForBoth("发表动态", R.drawable.base_action_bar_true_bg_selector,
                new HeaderLayout.onRightImageButtonClickListener() {
                    @Override
                    public void onClick() {
                        String commitContent = content.getText().toString().trim();
                        if (TextUtils.isEmpty(commitContent)) {
                            showToast("内容不能为空~");
                            return;
                        }
                        if (targetUrl == null) {
                            publish(commitContent, null, true);
                        } else {
                            publish(commitContent);
                        }
                        finish();
                    }
                });
        bindEvent();
    }

    private void bindEvent() {
        openLayout.setOnClickListener(this);
        takeLayout.setOnClickListener(this);
        albumPic.setOnClickListener(this);
        takePic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_layout:
                Date date = new Date(System.currentTimeMillis());
                dateTime = date.getTime() + "";
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
                } else {
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images
                            .Media.EXTERNAL_CONTENT_URI);
                }
                startActivityForResult(intent, REQUEST_CODE_ALBUM);
                break;
            case R.id.take_layout:
                Date date1 = new Date(System.currentTimeMillis());
                dateTime = date1.getTime() + "";
                File file = new File(CacheUtils.getCacheDirectory(mContext, true, "pic") +
                        dateTime);
                if (file.exists()) {
                    file.delete();
                }
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri uri = Uri.fromFile(file);
                Logger.d("uri", "uri = " + uri);

                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                startActivityForResult(camera, REQUEST_CODE_CAMERA);
                break;
            default:
                break;
        }
    }

    /**
     * 发表带图片动态
     *
     * @param commitContent
     */
    private void publish(final String commitContent) {
        final BmobFile figureFile = new BmobFile(new File(targetUrl));
        figureFile.upload(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                Logger.d(TAG, "上传文件成功~" + figureFile.getFileUrl(mContext));
                publish(commitContent, figureFile, false);
            }

            @Override
            public void onFailure(int i, String s) {
                Logger.e(TAG, "上传文件失败~" + s);
                Log.d(TAG, "上传文件失败~" + s);//上传文件失败~BmobFile File does not exist.
            }
        });
    }

    private void publish(String commitContent, BmobFile figureFile, boolean isPrivate) {
        User user = BmobUser.getCurrentUser(this, User.class);
        DynamicWall wall = new DynamicWall();
        if (figureFile != null) {
            wall.setContentfigureurl(figureFile);
        }
        wall.setAuthor(user);
        wall.setContent(commitContent);
        wall.setLove(0);
        wall.setHate(0);
        wall.setShare(0);
        wall.setComment(0);
        wall.setPass(true);
        wall.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                showToast("发表成功~");
                Logger.d(TAG, "创建成功~");
                setResult(RESULT_OK);
            }

            @Override
            public void onFailure(int i, String s) {
                showToast("发表失败~");
                Logger.e(TAG, "创建失败~" + s);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d(TAG, "get album");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ALBUM:
                    String fileName = null;
                    if (data != null) {
                        Uri originalUri = data.getData();
                        ContentResolver contentResolver = getContentResolver();
                        Cursor cursor = contentResolver.query(originalUri, null, null, null, null);
                        if (cursor.moveToFirst()) {
                            do {
                                fileName = cursor.getString(cursor.getColumnIndex("_data"));
                                Logger.d(TAG, "get album:" + fileName);
                            } while (cursor.moveToNext());
                        }
                        Bitmap bitmap = compressImageFromFile(fileName);
                        targetUrl = saveToSdCard(bitmap);
                        albumPic.setBackgroundDrawable(new BitmapDrawable(bitmap));
                        takeLayout.setVisibility(View.GONE);
                    }
                    break;
                case REQUEST_CODE_CAMERA:
                    String files = CacheUtils.getCacheDirectory(mContext, true, "pic") + dateTime;
                    File file = new File(files);
                    if (file.exists()) {
                        Bitmap bitmap = compressImageFromFile(files);
                        targetUrl = saveToSdCard(bitmap);
                        takePic.setBackgroundDrawable(new BitmapDrawable(bitmap));
                        openLayout.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    // TODO: 2016/5/4 这个地方会导致bitmap为空 
    private Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//
        float ww = 480f;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置采样率

        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收

        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
//		return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        //其实是无效的,大家尽管尝试
        return bitmap;
    }

    public String saveToSdCard(Bitmap bitmap) {
        String files = CacheUtils.getCacheDirectory(mContext, true, "pic") + dateTime + "_11.png";
        File file = new File(files);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)) {// TODO: 2016/5/4
                // 这个地方会崩溃
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        L.i(TAG, file.getAbsolutePath());
        return file.getAbsolutePath();
    }


}
