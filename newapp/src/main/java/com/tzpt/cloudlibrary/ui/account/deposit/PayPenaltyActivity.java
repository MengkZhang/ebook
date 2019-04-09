package com.tzpt.cloudlibrary.ui.account.deposit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.AliPayResultBean;
import com.tzpt.cloudlibrary.bean.WXPayResultBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.utils.Utils;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 交罚金
 */
public class PayPenaltyActivity extends BaseActivity implements
        PayPenaltyContract.View {

    private static final String LIB_CODE = "lib_code";
    private static final int SDK_PAY_FLAG = 1;
    private static final String APP_ID = "wxfe0e5fda7c64f6e3";

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PayPenaltyActivity.class);
        context.startActivity(intent);
    }

    public static void startActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, PayPenaltyActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForResult(Activity activity, String libCode, int requestCode) {
        Intent intent = new Intent(activity, PayPenaltyActivity.class);
        intent.putExtra(LIB_CODE, libCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.progress_layout)
    LoadingProgressView mProgressView;
    @BindView(R.id.pay_penalty_tv)
    TextView mPayPenaltyTv;
    @BindView(R.id.pay_penalty_btn)
    Button mPayPenaltyBtn;
    @BindView(R.id.pay_deposit_wechat_ck)
    CheckBox mPayDepositWechatCk;
    @BindView(R.id.pay_deposit_alipay_ck)
    CheckBox mPayDepositAlipayCk;

    private PayPenaltyPresenter mPresenter;
    private IWXAPI mWXApi;

    private String mAliPayInfo;

    private String mLibCode;

    @OnClick({R.id.titlebar_left_btn, R.id.pay_penalty_btn, R.id.pay_deposit_wechat_pay_ll,
            R.id.pay_deposit_alipay_ll})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.pay_deposit_wechat_pay_ll:
                mPayDepositWechatCk.setChecked(true);
                mPayDepositAlipayCk.setChecked(false);
                break;
            case R.id.pay_deposit_alipay_ll:
                mPayDepositWechatCk.setChecked(false);
                mPayDepositAlipayCk.setChecked(true);
                break;
            case R.id.pay_penalty_btn:
                if (mPayDepositWechatCk.isChecked()) {
                    if (mWXApi.isWXAppInstalled()) {
                        mPresenter.requestWeChatPay(Utils.getIP(this));
                    } else {
                        ToastUtils.showSingleToast("未安装微信客户端！");
                    }
                } else {
                    mPresenter.requestAlipay(Utils.getIP(this));
                }
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    @SuppressWarnings("unchecked")
                    AliPayResultBean payResult = new AliPayResultBean((Map<String, String>) msg.obj);

                    //对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")
                            || TextUtils.equals(resultStatus, "8000")
                            || TextUtils.equals(resultStatus, "6004")) {
                        mPresenter.requestAliPayResult();
                    } else if (TextUtils.equals(resultStatus, "6001")) {

                    } else {
                        showFinishMsgDialog(R.string.pay_failed);
                    }
                    break;

            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_pay_penalty;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("交罚金");
    }

    @Override
    public void initDatas() {
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);//支付宝沙箱环境

        EventBus.getDefault().register(this);

        mWXApi = WXAPIFactory.createWXAPI(this, APP_ID);
        mWXApi.registerApp(APP_ID);

        mLibCode = getIntent().getStringExtra(LIB_CODE);
        mPresenter = new PayPenaltyPresenter();
        mPresenter.attachView(this);

        mPresenter.handleUserPenalty(mLibCode);
        setPenaltyText("0.00", false);
    }

    @Override
    public void configViews() {
    }

    @Override
    public void showProgressDialog() {
        mProgressView.showProgressLayout();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressView.hideProgressLayout();
    }

    @Override
    public void showFinishMsgDialog(int resId) {
        final CustomDialog customDialog = new CustomDialog(this, R.style.DialogTheme);
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(false);
        customDialog.setButtonTextConfirmOrYes(true);
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
                finish();
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
            }
        });
        customDialog.setText(getString(resId));
        customDialog.show();
    }

    /**
     * 处理罚金提示
     *
     * @param payPenalty 罚金金额
     */
    @Override
    public void showPayPenaltyDialog(String payPenalty) {
        final CustomDialog customDialog = new CustomDialog(this, R.style.DialogTheme);
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(false);
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
                if (!TextUtils.isEmpty(mLibCode)) {
                    finish();
                }
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
            }
        });
        customDialog.setText(getString(R.string.had_pay_penalty, payPenalty));
        customDialog.show();
    }

    @Override
    public void showMsgDialog(int resId) {
        final CustomDialog customDialog = new CustomDialog(this, R.style.DialogTheme);
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(false);
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
            }
        });
        customDialog.setText(getString(resId));
        customDialog.show();
    }

    /**
     * 设置欠罚金金额
     *
     * @param penaltyText  罚金金额
     * @param submitEnable 提交按钮控制
     */
    @Override
    public void setPenaltyText(String penaltyText, boolean submitEnable) {
        mPayPenaltyTv.setText(getString(R.string.Owe_fine, penaltyText));
        if (submitEnable) {
            mPayPenaltyBtn.setEnabled(true);
            mPayPenaltyBtn.setClickable(true);
        } else {
            mPayPenaltyBtn.setEnabled(false);
            mPayPenaltyBtn.setClickable(false);
        }
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
    }

    @Override
    public void wxPayReq(String appid, String partnerid, String prepayid, String packageName, String noncestr, String timestamp, String sign) {
        PayReq req = new PayReq();
        req.appId = appid;
        req.partnerId = partnerid;
        req.prepayId = prepayid;
        req.packageValue = packageName;
        req.nonceStr = noncestr;
        req.timeStamp = timestamp;
        req.sign = sign;

        mWXApi.registerApp(APP_ID);
        mWXApi.sendReq(req);
    }

    @Override
    public void aliPayReq(String aliPayInfo) {
        mAliPayInfo = aliPayInfo;
        pay();
    }

    public void pay() {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayPenaltyActivity.this);
                // 调用支付接口，获取支付结果
                Map<String, String> result = alipay.payV2(mAliPayInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 接收微信支付结果
     *
     * @param wxPayResult
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveWXPayResult(WXPayResultBean wxPayResult) {
        if (wxPayResult != null) {
            switch (wxPayResult.code) {
                case 0:
                    mPresenter.requestWXPayResult();
                    break;
                case -1:
                    showFinishMsgDialog(R.string.pay_failed);
                    break;
                case -2:
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }

    /**
     * 如果交钱成功，则返回退押金刷新，用户押金信息
     */
    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPresenter.releaseHandler();
        mPresenter.detachView();
        mPresenter = null;
    }
}
