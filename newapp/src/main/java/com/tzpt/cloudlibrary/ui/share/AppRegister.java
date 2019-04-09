package com.tzpt.cloudlibrary.ui.share;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信分享接收者
 */
public class AppRegister extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //注册微信
        final IWXAPI api = WXAPIFactory.createWXAPI(context, ShareConfig.WX_APP_KEY, true);
        api.registerApp(ShareConfig.WX_APP_KEY);
    }
}
