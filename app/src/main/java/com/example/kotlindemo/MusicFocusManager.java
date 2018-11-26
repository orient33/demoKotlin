package com.example.kotlindemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.O)
public class MusicFocusManager implements AudioManager.OnAudioFocusChangeListener {

    private AudioManager mAudioManager;

    private AudioFocusRequest mAudioFocusRequest;


    @TargetApi(Build.VERSION_CODES.O)
    public MusicFocusManager(@NonNull Context context) {

        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        AudioAttributes mAudioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(mAudioAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(this)
                .build();

    }

    public boolean requestAudioFocus() {
        /*
         * AudioManager.AUDIOFOCUS_REQUEST_FAILED 焦点获取失败
         * AudioManager.AUDIOFOCUS_REQUEST_GRANTED 焦点获取成功-可以播放
         */
        return mAudioManager.requestAudioFocus(mAudioFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void abandonAudioFocusRequest() {
        mAudioManager.abandonAudioFocusRequest(mAudioFocusRequest);
    }


    /**
     * TODO 暂时处理这些，等Framework的统一管理文档
     */
    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.d("df", "Demo. onAudioFocusChange " + focusChange);
        switch (focusChange) {
            // 重新获得焦点
            case AudioManager.AUDIOFOCUS_GAIN:


                // 恢复音量

                break;
            // 永久丢失焦点，如被其他播放器抢占
            case AudioManager.AUDIOFOCUS_LOSS:
                break;
            // 短暂丢失焦点，如来电
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                break;
            // 瞬间丢失焦点，如通知
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // 音量减小为一半
                break;
            default:
                break;
        }
    }

}
