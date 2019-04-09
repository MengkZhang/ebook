package com.tzpt.cloudlibrary.widget.video;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.rxbus.event.EventRotationMsg;
import com.tzpt.cloudlibrary.utils.Utils;

import org.greenrobot.eventbus.EventBus;

/**
 * 视频旋转监听工具
 * Created by tonyjia on 2018/8/20.
 */
public class CLVideoRotationObserver extends ContentObserver {

    private ContentResolver mResolver;

    public CLVideoRotationObserver(Handler handler, Context context) {
        super(handler);
        mResolver = context.getContentResolver();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        int rotationStatus = getRotationStatus();
        EventBus.getDefault().post(new EventRotationMsg(rotationStatus == 1));

    }

    public void startObserver() {
        mResolver.registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false,
                this);
    }

    public void stopObserver() {
        mResolver.unregisterContentObserver(this);
    }

    //得到屏幕旋转的状态
    private int getRotationStatus() {
        int status = 0;
        try {
//            status = android.provider.Settings.System.getInt(Utils.getContext().getContentResolver(),
            status = android.provider.Settings.System.getInt(CloudLibraryApplication.getAppContext().getContentResolver(),
                    android.provider.Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return status;
    }

}
