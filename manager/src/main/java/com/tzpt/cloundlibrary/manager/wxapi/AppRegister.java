package com.tzpt.cloundlibrary.manager.wxapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by Administrator on 2018/1/11.
 */

public class AppRegister extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final IWXAPI api = WXAPIFactory.createWXAPI(context, null);

        // 将该app注册到微信
        // "wxfe0e5fda7c64f6e3";正式服务器
        // "wxbf71708d20976b86";测试服务器
        api.registerApp("wxfe0e5fda7c64f6e3");

    }
}
