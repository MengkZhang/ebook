package com.tzpt.cloundlibrary.manager.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.bean.WXPayResultBean;
import com.tzpt.cloundlibrary.manager.ui.contract.SubstitutePayDepositContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.SubstitutePayDepositPresenter;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.utils.PayResult;
import com.tzpt.cloundlibrary.manager.utils.ToastUtils;
import com.tzpt.cloundlibrary.manager.utils.Utils;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 代收押金（代交赔金/罚金）
 * Created by Administrator on 2018/12/17.
 */

public class SubstitutePayDepositActivity extends BaseActivity implements SubstitutePayDepositContract.View {
    private static final String MONEY = "money";
    private static final String FROM_TYPE = "from_type";
    private static final String READER_ID = "reader_id";

    public static void startActivityForResult(Activity activity, int type, String readerId, double money, int requestCode) {
        Intent intent = new Intent(activity, SubstitutePayDepositActivity.class);
        intent.putExtra(MONEY, money);
        intent.putExtra(FROM_TYPE, type);
        intent.putExtra(READER_ID, readerId);
        activity.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.substitute_pay_deposit_info_tv)
    TextView mPayDepositInfoTv;
    @BindView(R.id.pay_deposit_lib_ck)
    CheckBox mPayDepositLibCk;
    @BindView(R.id.pay_deposit_wechat_ck)
    CheckBox mPayDepositWechatCk;
    @BindView(R.id.pay_deposit_alipay_ck)
    CheckBox mPayDepositAlipayCk;
    @BindView(R.id.lib_balance_tv)
    TextView mLibBalanceTv;
    @BindView(R.id.pay_deposit_status_tv)
    TextView mPayDepositStatusTv;
    @BindView(R.id.pay_deposit_btn)
    Button mPayDepositBtn;

    private double mMoney;
    private double mLibBalance;
    private String mReaderId;

    private SubstitutePayDepositPresenter mPresenter;

    private IWXAPI mWXApi;
        private static final String APP_ID = "wxfe0e5fda7c64f6e3";//正式服务器
//    private static final String APP_ID = "wxbf71708d20976b86";//测试服务器

    private String mAliPayInfo;

    private static final int SDK_PAY_FLAG = 1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    dismissProgressDialog();
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);

                    //对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")
                            || TextUtils.equals(resultStatus, "8000")
                            || TextUtils.equals(resultStatus, "6004")) {
                        mPresenter.requestAliPayResult(mReaderId);
                    } else if (TextUtils.equals(resultStatus, "6001")) {

                    } else {
                        showDialogTip(R.string.pay_failed);
                    }
                    break;

            }
        }
    };

    @OnClick({R.id.titlebar_left_btn, R.id.pay_deposit_lib_ck, R.id.pay_deposit_wechat_ck,
            R.id.pay_deposit_alipay_ck, R.id.pay_deposit_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.pay_deposit_lib_ck:
                mPayDepositLibCk.setChecked(true);
                mPayDepositWechatCk.setChecked(false);
                mPayDepositAlipayCk.setChecked(false);
                mLibBalanceTv.setVisibility(View.VISIBLE);
                mPayDepositStatusTv.setVisibility(View.VISIBLE);
                if (mMoney > mLibBalance) {
                    mPayDepositBtn.setEnabled(false);
                    mPayDepositBtn.setClickable(false);
                } else {
                    mPayDepositBtn.setEnabled(true);
                    mPayDepositBtn.setClickable(true);
                }
                break;
            case R.id.pay_deposit_wechat_ck:
                mPayDepositLibCk.setChecked(false);
                mPayDepositWechatCk.setChecked(true);
                mPayDepositAlipayCk.setChecked(false);
                mLibBalanceTv.setVisibility(View.GONE);
                mPayDepositStatusTv.setVisibility(View.GONE);
                mPayDepositBtn.setEnabled(true);
                mPayDepositBtn.setClickable(true);
                break;
            case R.id.pay_deposit_alipay_ck:
                mPayDepositLibCk.setChecked(false);
                mPayDepositWechatCk.setChecked(false);
                mPayDepositAlipayCk.setChecked(true);
                mLibBalanceTv.setVisibility(View.GONE);
                mPayDepositStatusTv.setVisibility(View.GONE);
                mPayDepositBtn.setEnabled(true);
                mPayDepositBtn.setClickable(true);
                break;
            case R.id.pay_deposit_btn:
                if (mPayDepositWechatCk.isChecked()) {
                    if (isWXAppInstalledAndSupported()) {
                        mPresenter.requestWXPayInfo(mMoney, Utils.getIP(this), mReaderId);
                    } else {
                        ToastUtils.showSingleToast("未安装微信客户端！");
                    }
                } else if (mPayDepositAlipayCk.isChecked()) {
                    mPresenter.requestAliPayInfo(mMoney, Utils.getIP(this), mReaderId);
                } else {
                    mPresenter.payDeposit(mReaderId, mMoney);
                }
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_substitute_pay_deposit;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
    }

    @Override
    public void initDatas() {
//        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);//支付宝沙箱环境

        mPresenter = new SubstitutePayDepositPresenter();
        mPresenter.attachView(this);

        mPresenter.getLibBalanceInfo();

        int fromType = getIntent().getIntExtra(FROM_TYPE, 0);
        mReaderId = getIntent().getStringExtra(READER_ID);
        mMoney = getIntent().getDoubleExtra(MONEY, 0);
        if (mMoney == 0) {
            finish();
        }
        if (fromType == 0) {
            mCommonTitleBar.setTitle("上交赔金");
            mPayDepositInfoTv.setText("上交赔金金额 " + MoneyUtils.formatMoney(mMoney));
        } else {
            mCommonTitleBar.setTitle("上交罚金");
            mPayDepositInfoTv.setText("上交罚金金额 " + MoneyUtils.formatMoney(mMoney));
        }

        mWXApi = WXAPIFactory.createWXAPI(this, APP_ID);
        mWXApi.registerApp(APP_ID);

        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {

    }

    @Override
    public void showProgressDialog() {
        showDialog("");
    }

    @Override
    public void dismissProgressDialog() {
        dismissDialog();
    }

    @Override
    public void setNoPermissionDialog(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                LoginActivity.startActivity(SubstitutePayDepositActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void showDialogTip(int msgId) {
        String msg = getString(msgId);
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                setResult(RESULT_OK);

                finish();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void setBalanceInfo(double balance) {
        mLibBalance = balance;
        mLibBalanceTv.setText("当前余额: " + MoneyUtils.formatMoney(balance));
        if (mMoney <= balance) {
            mPayDepositBtn.setEnabled(true);
            mPayDepositBtn.setClickable(true);
            mPayDepositStatusTv.setText(MoneyUtils.formatMoney(balance) + " - " + MoneyUtils.formatMoney(mMoney) + " = " + MoneyUtils.formatMoney(MoneyUtils.sub(balance, mMoney)));
        } else {
            mPayDepositBtn.setEnabled(false);
            mPayDepositBtn.setClickable(false);
            mPayDepositStatusTv.setText("余额不足，请选择其他支付方式");
        }
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
                PayTask alipay = new PayTask(SubstitutePayDepositActivity.this);
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
     * 判断微信客户端是否安装,判断微信API是否支持
     *
     * @return
     */
    private boolean isWXAppInstalledAndSupported() {
        return mWXApi.isWXAppInstalled();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveWXPayResult(WXPayResultBean wxPayResult) {
        dismissProgressDialog();
        if (wxPayResult != null) {
            switch (wxPayResult.code) {
                case 0:
                    mPresenter.requestWXPayResult(mReaderId);
                    break;
                case -1:
                    showDialogTip(R.string.pay_failed);
                    break;
                case -2:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        EventBus.getDefault().unregister(this);
    }
}
