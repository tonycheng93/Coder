package com.tony.coder.im.ui.adapter;

/**
 * 项目名称：Coder
 * 类描述：
 * 创建人：tonycheng
 * 创建时间：2016/4/14 20:12
 * 邮箱：tonycheng93@outlook.com
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;

import com.tony.coder.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.util.BmobLog;
import cn.bmob.im.util.BmobUtils;

/**
 * 播放录音文件
 */
public class NewRecordPlayClickListener implements View.OnClickListener {
    BmobMsg message;
    ImageView iv_voice;
    private AnimationDrawable anim = null;
    Context context;
    String currentObjectId = "";
    MediaPlayer mediaPlayer = null;
    public static boolean isPlaying = false;
    public static NewRecordPlayClickListener currentPlayListener = null;
    static BmobMsg currentMsg = null;//用于区分两个不同语音的播放

    BmobUserManager userManager;

    public NewRecordPlayClickListener(Context context, BmobMsg message, ImageView iv_voice) {
        this.context = context;
        this.message = message;
        this.iv_voice = iv_voice;
        currentMsg = message;
        currentPlayListener = this;
        currentObjectId = BmobUserManager.getInstance(context).getCurrentUserObjectId();
        userManager = BmobUserManager.getInstance(context);
    }

    /**
     * 播放语音
     *
     * @param filePath
     * @param isUseSpeaker
     */
    public void startPlayRecord(String filePath, boolean isUseSpeaker) {
        if (!(new File(filePath).exists())) {
            return;
        }
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();
        if (isUseSpeaker) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        } else {
            audioManager.setSpeakerphoneOn(false);//关闭扬声器
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }
        try {
            mediaPlayer.reset();
            // 单独使用此方法会报错播放错误:setDataSourceFD failed.: status=0x80000000
            // mediaPlayer.setDataSource(filePath);
            // 因此采用此方式会避免这种错误
            FileInputStream fis = new FileInputStream(new File(filePath));
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isPlaying = true;
                    currentMsg = message;
                    mp.start();
                    startRecordAnimation();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayRecord();
                }
            });
            currentPlayListener = this;
        } catch (Exception e) {
            BmobLog.i("播放错误：" + e.getMessage());
        }
    }

    /**
     * 停止播放
     */
    public void stopPlayRecord() {
        stopRecordAnimation();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        isPlaying = false;
    }

    /**
     * 停止播放动画
     */
    private void stopRecordAnimation() {
        if (message.getBelongId().equals(currentObjectId)) {
            iv_voice.setImageResource(R.drawable.voice_left3);
        } else {
            iv_voice.setImageResource(R.drawable.voice_right3);
        }
        if (anim != null) {
            anim.stop();
        }
    }

    /**
     * 开启播放动画
     */
    private void startRecordAnimation() {
        if (message.getBelongId().equals(currentObjectId)) {
            iv_voice.setImageResource(R.drawable.anim_chat_voice_right);
        } else {
            iv_voice.setImageResource(R.drawable.anim_chat_voice_left);
        }
        anim = (AnimationDrawable) iv_voice.getDrawable();
        anim.start();
    }

    @Override
    public void onClick(View v) {
        if (isPlaying) {
            currentPlayListener.stopPlayRecord();
            if (currentMsg != null
                    && currentMsg.hashCode() == message.hashCode()) {
                currentMsg = null;
                return;
            }
        }
        BmobLog.i("voice", "点击事件");
        if (message.getBelongId().equals(currentObjectId)) {//如果是自己发送的语音消息，则播放本地地址
            String localPath = message.getContent().split("&")[0];
            startPlayRecord(localPath, true);
        } else {//如果是收到的消息，则需要先下载后播放
            String localPath = getDownLoadFilePath(message);
            BmobLog.i("voice", "收到的语音存储的地址：" + localPath);
            startPlayRecord(localPath, true);
        }
    }

    private String getDownLoadFilePath(BmobMsg message) {
        String accountDir = BmobUtils.string2MD5(userManager.getCurrentUserObjectId());
        File dir = new File(BmobConfig.BMOB_VOICE_DIR + File.separator + accountDir +
                File.separator + message.getBelongId());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //当前用户的目录下面存放录音文件
        File audioFile = new File(dir.getAbsolutePath() + File.separator + message.getMsgTime() + ".amr");
        try {
            if (!audioFile.exists()) {
                audioFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return audioFile.getAbsolutePath();
    }
}
