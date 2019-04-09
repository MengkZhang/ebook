package com.tzpt.cloundlibrary.manager.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.bean.WXPayResultBean;
import com.tzpt.cloundlibrary.manager.ui.contract.PayDepositContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.PayDepositPresenter;
import com.tzpt.cloundlibrary.manager.utils.PayResult;
import com.tzpt.cloundlibrary.manager.utils.Utils;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 充值
 */
public class PayDepositActivity extends BaseActivity implements
        PayDepositContract.View {
    private static final int SDK_PAY_FLAG = 1;

    public static void startActivityForResult(Activity context, int requestCode) {
        Intent intent = new Intent(context, PayDepositActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.pay_deposit_layout1)
    RadioGroup mPayDepositLayout1;
    @BindView(R.id.pay_deposit_layout2)
    RadioGroup mPayDepositLayout2;
    @BindView(R.id.pay_deposit_contract_tv)
    TextView mPayDepositContractTv;
//    @BindView(R.id.pay_deposit_wechat_ck)
//    CheckBox mPayDepositWechatCk;
//    @BindView(R.id.pay_deposit_alipay_ck)
//    CheckBox mPayDepositAlipayCk;

    private double mPayDeposit = 500;
    private PayDepositPresenter mPresenter;

//    private IWXAPI mWXApi;
//    private static final String APP_ID = "wxfe0e5fda7c64f6e3";

    private String mAliPayInfo;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    dismissProgressLoading();
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);

                    //对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")
                            || TextUtils.equals(resultStatus, "8000")
                            || TextUtils.equals(resultStatus, "6004")) {
                        mPresenter.requestAliPayResult();
                    } else if (TextUtils.equals(resultStatus, "6001")) {

                    } else {
                        showDialogTip(R.string.pay_failed, false);
                    }
                    break;

            }
        }
    };

    @OnClick({R.id.titlebar_left_btn, R.id.pay_deposit_confirm_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
//            case R.id.pay_deposit_wechat_pay_ll:
//                mPayDepositWechatCk.setChecked(true);
//                mPayDepositAlipayCk.setChecked(false);
//                break;
//            case R.id.pay_deposit_alipay_ll:
//                mPayDepositWechatCk.setChecked(false);
//                mPayDepositAlipayCk.setChecked(true);
//                break;
            case R.id.pay_deposit_confirm_btn:
                mPresenter.requestAliPayInfo(mPayDeposit, Utils.getIP(this));
//                if (mPayDepositWechatCk.isChecked()) {
//                    if (isWXAppInstalledAndSupported()) {
//                        mPresenter.requestWXPayInfo(mPayDeposit, Utils.getIP(this));
//                    } else {
//                        ToastUtils.showSingleToast("未安装微信客户端！");
//                    }
//                } else {
//
//                }
                break;
        }
    }

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case R.id.pay_deposit_5000:
                    mPayDeposit = 5000;
                    mPayDepositLayout1.clearCheck();
                    group.check(R.id.pay_deposit_5000);
                    break;
                case R.id.pay_deposit_2000:
                    mPayDeposit = 2000;
                    mPayDepositLayout1.clearCheck();
                    group.check(R.id.pay_deposit_2000);
                    break;
                case R.id.pay_deposit_1000:
                    mPayDeposit = 1000;
                    mPayDepositLayout2.clearCheck();
                    group.check(R.id.pay_deposit_1000);
                    break;
                case R.id.pay_deposit_500:
                    mPayDeposit = 500;
                    mPayDepositLayout2.clearCheck();
                    group.check(R.id.pay_deposit_500);
                    break;
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_pay_deposit;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("充值");
    }

    @Override
    public void initDatas() {
        mPresenter = new PayDepositPresenter();
        mPresenter.attachView(this);

//        mWXApi = WXAPIFactory.createWXAPI(this, APP_ID);
//        mWXApi.registerApp(APP_ID);

        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        //textView 局部响应点击事件功能
        SpannableString spanText = new SpannableString("点击确定，即表示您已同意《用户服务协议》");
        spanText.setSpan(mClickableSpan, spanText.length() - 8, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mPayDepositContractTv.setHighlightColor(Color.TRANSPARENT);
        mPayDepositContractTv.setText(spanText);
        mPayDepositContractTv.setMovementMethod(LinkMovementMethod.getInstance());
        mPayDepositLayout1.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mPayDepositLayout2.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mPayDepositLayout1.check(R.id.pay_deposit_500);
    }

    private ClickableSpan mClickableSpan = new ClickableSpan() {
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(Color.parseColor("#9d724d"));
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            //查看充值协议
            UserDepositAgreementActivity.startActivity(PayDepositActivity.this);
        }
    };
//
//    /**
//     * 判断微信客户端是否安装,判断微信API是否支持
//     *
//     * @return
//     */
//    private boolean isWXAppInstalledAndSupported() {
//        return mWXApi.isWXAppInstalled();
//    }

    @Override
    public void showProgressLoading() {
        showDialog("");
    }

    @Override
    public void dismissProgressLoading() {
        dismissDialog();
    }

    @Override
    public void wxPayReq(String appid, String partnerid, String prepayid, String packageName,
                         String noncestr, String timestamp, String sign) {
        PayReq req = new PayReq();
        req.appId = appid;
        req.partnerId = partnerid;
        req.prepayId = prepayid;
        req.packageValue = packageName;
        req.nonceStr = noncestr;
        req.timeStamp = timestamp;
        req.sign = sign;

//        mWXApi.registerApp(APP_ID);
//        mWXApi.sendReq(req);
    }

    @Override
    public void aliPayReq(String aliPayInfo) {
        mAliPayInfo = aliPayInfo;
        pay();
    }

    @Override
    public void showDialogTip(int resId, final boolean refresh) {
        String msg = getString(resId);
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                if (refresh) {
                    setResult(RESULT_OK);
                }
                finish();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void showNoPermissionDialog(int kickOut) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(kickOut));
        dialog.hasNoCancel(false);
        dialog.setCancelable(false);
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                LoginActivity.startActivity(PayDepositActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveWXPayResult(WXPayResultBean wxPayResult) {
        dismissProgressLoading();
        if (wxPayResult != null) {
            switch (wxPayResult.code) {
                case 0:
                    mPresenter.requestWXPayResult();
                    break;
                case -1:
                    showDialogTip(R.string.pay_failed, false);
                    break;
                case -2:
                    break;
            }
        }
    }

    public void pay() {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayDepositActivity.this);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.detachView();
        }
        EventBus.getDefault().unregister(this);
    }
}
