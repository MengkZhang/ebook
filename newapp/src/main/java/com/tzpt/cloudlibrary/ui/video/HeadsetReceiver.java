package com.tzpt.cloudlibrary.ui.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.rxbus.event.HeadsetEvent;

/**
 * 耳机拔插监听广播
 */
public class HeadsetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
            if (intent.hasExtra("state")) {
                RxBus rxBus = CloudLibraryApplication.mRxBus;
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0://未插入
                        rxBus.post(new HeadsetEvent(false));
                        break;
                    case 1://插入
                        rxBus.post(new HeadsetEvent(true));
                        break;
                }
            }
        }
    }
}
