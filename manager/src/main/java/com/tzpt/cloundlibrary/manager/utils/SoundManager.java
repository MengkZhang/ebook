package com.tzpt.cloundlibrary.manager.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

import com.tzpt.cloundlibrary.manager.R;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by Administrator on 2017/7/16.
 */

public class SoundManager {

    private SoundPool soundPool;
    private int soundID, soundIDSucess, soundIDError, soundIDTimeOut;
    private boolean plays = false, loaded = false;
    private float actVolume, maxVolume, volume;
    private AudioManager audioManager;
    private int counter;
    private static final long VIBRATE_DURATION = 200L;
    private boolean vibrate;

    public SoundManager() {
    }

    /**
     * 初始化声音
     *
     * @param context
     */
    public void initSound(Context context) {
        if (soundPool == null) {
            soundPool = buildSoundPool(context);
        }
        try {
            soundID = soundPool.load(context, R.raw.qrcode_voice, 1);
            soundIDSucess = soundPool.load(context, R.raw.success, 1);
            soundIDError = soundPool.load(context, R.raw.error, 1);
            soundIDTimeOut = soundPool.load(context, R.raw.timeout, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        vibrate = true;
    }


    /**
     * 开启声音
     *
     * @param context
     */
    public void startPlaySound(Context context) {
        if (loaded && !plays) {
            soundPool.play(soundID, volume, volume, 1, 0, 1f);
            counter = counter++;
            plays = true;
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) ((Activity) context).getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * 停止声音或恢复可以开启声音的状态
     *
     * @param context
     */
    public void stopPlaySound(Context context) {
        if (plays) {
            soundPool.stop(soundID);
            soundID = soundPool.load(context, R.raw.qrcode_voice, counter);
            plays = false;
        }
    }

    /**
     * 初始化sound api
     *
     * @param context
     * @return
     */
    private SoundPool buildSoundPool(Context context) {
        buildBeforeAPI21(context);
        return soundPool;
    }

    /**
     * 初始化sound api before 21
     *
     * @param context
     */
    private void buildBeforeAPI21(Context context) {
        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;
        ((Activity) context).setVolumeControlStream(AudioManager.STREAM_MUSIC);
        counter = 0;
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        soundID = soundPool.load(context, R.raw.qrcode_voice, 1);
        soundIDSucess = soundPool.load(context, R.raw.success, 1);
        soundIDError = soundPool.load(context, R.raw.error, 1);
        soundIDTimeOut = soundPool.load(context, R.raw.timeout, 1);
    }


    /**
     * 开启门禁声音
     *
     * @param context
     */
    public void startPlaySoundForEntrance(Context context, int soundType) {
        if (loaded && !plays) {
            if (soundType == 0) {
                soundPool.play(soundIDSucess, volume, volume, 1, 0, 1f);
            } else if (soundType == 1) {
                soundPool.play(soundIDError, volume, volume, 1, 0, 1f);
            } else if (soundType == 2) {
                soundPool.play(soundIDTimeOut, volume, volume, 1, 0, 1f);
            }
            counter = counter++;
            plays = true;
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * 关闭门禁声音
     *
     * @param context
     * @param soundType
     */
    public void stopPlaySoundForEntrance(Context context, int soundType) {
        if (plays) {
            if (soundType == 0) {
                soundPool.stop(soundIDSucess);
                soundIDSucess = soundPool.load(context, R.raw.success, counter);
            } else if (soundType == 1) {
                soundPool.stop(soundIDError);
                soundIDError = soundPool.load(context, R.raw.error, counter);
            } else if (soundType == 2) {
                soundPool.stop(soundIDTimeOut);
                soundIDTimeOut = soundPool.load(context, R.raw.timeout, counter);
            }
            plays = false;
        }
    }

}
