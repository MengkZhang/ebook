package com.tzpt.cloudlibrary.ui.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tzpt.cloudlibrary.utils.CheckInternetUtil;

/**
 * 网络状态接收者
 */
public class CLNetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        CheckInternetUtil.checkNetWork(context, true);
    }
}
