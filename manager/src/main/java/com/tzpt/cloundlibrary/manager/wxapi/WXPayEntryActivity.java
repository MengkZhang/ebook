package com.tzpt.cloundlibrary.manager.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tzpt.cloundlibrary.manager.bean.WXPayResultBean;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2018/1/10.
 */

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        // "wxfe0e5fda7c64f6e3";正式服务器
        // "wxbf71708d20976b86";测试服务器
        api = WXAPIFactory.createWXAPI(this, "wxfe0e5fda7c64f6e3");
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            WXPayResultBean wxPayResult = new WXPayResultBean();
            wxPayResult.code = baseResp.errCode;
            EventBus.getDefault().post(wxPayResult);
        }
        Log.e("WXPayEntryActivity", baseResp.errStr + "   " + baseResp.errCode);
        api.unregisterApp();
        finish();
    }
}
