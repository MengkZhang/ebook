package com.tzpt.cloudlibrary.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.ui.share.ShareConfig;
import com.tzpt.cloudlibrary.utils.ToastUtils;

/**
 * 微信回调界面
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //注册微信
        api = WXAPIFactory.createWXAPI(this, ShareConfig.WX_APP_KEY, true);
        try {
            api.handleIntent(getIntent(), this);
        } catch (Exception e) {
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    /**
     * 接收微信回调,
     * 微信20180831调整 cancel和complete统一返回了成功状态，因此这里不显示回调内容
     *
     * @param resp
     */
    @Override
    public void onResp(BaseResp resp) {
        //int result = 0;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //result = R.string.share_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //result = R.string.share_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //result = R.string.share_fail;
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                //result = R.string.share_fail;
                break;
            default:
                //result = R.string.share_fail;
                break;
        }
        //ToastUtils.showSingleToast(result);
        this.finish();
    }
}
