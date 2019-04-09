package com.tzpt.cloudlibrary.ui.account.deposit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.AliPayResultBean;
import com.tzpt.cloudlibrary.bean.DepositBalanceBean;
import com.tzpt.cloudlibrary.bean.WXPayResultBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.utils.MoneyUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.utils.Utils;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 交押金
 */
public class PayDepositActivity extends BaseActivity implements
        PayDepositContract.View {
    private static final int SDK_PAY_FLAG = 1;
    private static final String CONSTANT_PRICE = "constant_price";

    public static void startActivityForResult(Activity context, int requestCode) {
        Intent intent = new Intent(context, PayDepositActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForResult(Activity context, double price, int requestCode) {
        Intent intent = new Intent(context, PayDepositActivity.class);
        intent.putExtra(CONSTANT_PRICE, price);
        context.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.user_total_deposit_tv)
    TextView mUserTotalDepositTv;
    @BindView(R.id.pay_deposit_layout1)
    RadioGroup mPayDepositLayout1;
    @BindView(R.id.pay_deposit_layout2)
    RadioGroup mPayDepositLayout2;
    @BindView(R.id.pay_deposit_layout3)
    RadioGroup mPayDepositLayout3;
    @BindView(R.id.pay_deposit_protocol_tv)
    TextView mPayDepositContractTv;
    @BindView(R.id.pay_deposit_wechat_ck)
    CheckBox mPayDepositWechatCk;
    @BindView(R.id.pay_deposit_alipay_ck)
    CheckBox mPayDepositAlipayCk;
    @BindView(R.id.progress_layout)
    LoadingProgressView mLoadingProgressView;
    @BindView(R.id.pay_deposit_10)
    RadioButton mPayDeposit10;
    @BindView(R.id.pay_deposit_50)
    RadioButton mPayDeposit50;
    @BindView(R.id.pay_deposit_100)
    RadioButton mPayDeposit100;
    @BindView(R.id.pay_deposit_200)
    RadioButton mPayDeposit200;
    @BindView(R.id.pay_deposit_constant)
    RadioButton mPayDepositConstant;
    @BindView(R.id.pay_deposit_protocol_cb)
    CheckBox mPayDepositProtocolCb;
    @BindView(R.id.pay_deposit_confirm_btn)
    Button mPayDepositConfirmBtn;

    private double mPayDeposit = 10;
    private double mConstantPayDeposit;

    private PayDepositPresenter mPresenter;

    private IWXAPI mWXApi;
    private static final String APP_ID = "wxfe0e5fda7c64f6e3";//微信正式key

    private String mAliPayInfo;

    private ArrayList<DepositBalanceBean> mDepositBalanceList = new ArrayList<>();
    private ArrayList<DepositBalanceBean> mPenaltyList = new ArrayList<>();

    private ClickableSpan mClickableSpan = new ClickableSpan() {
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(Color.parseColor("#9d724d"));
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            //查看押金协议
            UserDepositAgreementActivity.startActivity(PayDepositActivity.this,
                    "用户服务协议", "http://img.ytsg.cn/html/userapp/payAgreement.html");
        }
    };

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case R.id.pay_deposit_200:
                    mPayDeposit = 200;
                    mPayDepositLayout1.clearCheck();
                    mPayDepositLayout3.clearCheck();
                    group.check(R.id.pay_deposit_200);
                    break;
                case R.id.pay_deposit_100:
                    mPayDeposit = 100;
                    mPayDepositLayout1.clearCheck();
                    mPayDepositLayout3.clearCheck();
                    group.check(R.id.pay_deposit_100);
                    break;
                case R.id.pay_deposit_50:
                    mPayDeposit = 50;
                    mPayDepositLayout2.clearCheck();
                    mPayDepositLayout3.clearCheck();
                    group.check(R.id.pay_deposit_50);
                    break;
                case R.id.pay_deposit_10:
                    mPayDeposit = 10;
                    mPayDepositLayout2.clearCheck();
                    mPayDepositLayout3.clearCheck();
                    group.check(R.id.pay_deposit_10);
                    break;
                case R.id.pay_deposit_constant:
                    mPayDeposit = mConstantPayDeposit;
                    mPayDepositLayout1.clearCheck();
                    mPayDepositLayout2.clearCheck();
                    group.check(R.id.pay_deposit_constant);
                    break;

            }
        }
    };

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
                        showDialogTip(R.string.pay_failed, false);
                    }
                    break;

            }
        }
    };

    @OnClick({R.id.titlebar_left_btn, R.id.pay_deposit_wechat_pay_ll, R.id.pay_deposit_alipay_ll,
            R.id.pay_deposit_confirm_btn, R.id.user_total_deposit_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
            case R.id.pay_deposit_confirm_btn:
                if (mPayDepositWechatCk.isChecked()) {
                    if (isWXAppInstalledAndSupported()) {
                        mPresenter.requestWXPayInfo(mPayDeposit, Utils.getIP(this));
                    } else {
                        ToastUtils.showSingleToast("未安装微信客户端！");
                    }
                } else {
                    mPresenter.requestAliPayInfo(mPayDeposit, Utils.getIP(this));
                }
                break;
            case R.id.user_total_deposit_tv:
                if (mPenaltyList.size() > 0) {
                    DepositBalancePenaltyActivity.startActivity(this, mPenaltyList);
                } else if (mDepositBalanceList.size() > 0) {
                    DepositBalanceActivity.startActivity(this, mDepositBalanceList, 0);
                }
                break;
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_pay_deposit;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("交押金");
    }

    @Override
    public void initDatas() {
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);//支付宝沙箱环境
        mPresenter = new PayDepositPresenter();
        mPresenter.attachView(this);
        mPresenter.requestUserDeposit();

        mWXApi = WXAPIFactory.createWXAPI(this, APP_ID);
        mWXApi.registerApp(APP_ID);

        mUserTotalDepositTv.setText(getString(R.string.deposit_balance, "0.00"));
        mUserTotalDepositTv.setEnabled(false);

        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        mConstantPayDeposit = getIntent().getDoubleExtra(CONSTANT_PRICE, -1);

        if (mConstantPayDeposit < 0) {
            mPayDeposit10.setChecked(true);
            mPayDepositLayout3.setVisibility(View.GONE);
        } else {
            mPayDepositLayout3.setVisibility(View.VISIBLE);
            mPayDepositConstant.setText(getString(R.string.pay_deposit_constant_money,
                    MoneyUtils.formatMoney(mConstantPayDeposit)));
            mPayDepositConstant.setChecked(true);
            mPayDeposit = mConstantPayDeposit;

            if (10 < mConstantPayDeposit) {
                mPayDeposit10.setEnabled(false);
            } else if (50 < mConstantPayDeposit) {
                mPayDeposit10.setEnabled(false);
                mPayDeposit50.setEnabled(false);
            } else if (100 < mConstantPayDeposit) {
                mPayDeposit10.setEnabled(false);
                mPayDeposit50.setEnabled(false);
                mPayDeposit100.setEnabled(false);
            } else if (200 < mConstantPayDeposit) {
                mPayDeposit10.setEnabled(false);
                mPayDeposit50.setEnabled(false);
                mPayDeposit100.setEnabled(false);
                mPayDeposit200.setEnabled(false);
            }
        }

        //textView 局部响应点击事件功能
        SpannableString spanText = new SpannableString("我已阅读并同意《用户服务协议》");
        spanText.setSpan(mClickableSpan, spanText.length() - 8, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mPayDepositContractTv.setHighlightColor(Color.TRANSPARENT);
        mPayDepositContractTv.setText(spanText);
        mPayDepositContractTv.setMovementMethod(LinkMovementMethod.getInstance());
        mPayDepositLayout1.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mPayDepositLayout2.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mPayDepositLayout3.setOnCheckedChangeListener(mOnCheckedChangeListener);

        mPayDepositProtocolCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPayDepositConfirmBtn.setEnabled(true);
                    mPayDepositConfirmBtn.setClickable(true);
                } else {
                    mPayDepositConfirmBtn.setEnabled(false);
                    mPayDepositConfirmBtn.setClickable(false);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.detachView();
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void showUserDeposit(double penalty, double totalDeposit, double occupyDeposit, List<DepositBalanceBean> list, List<DepositBalanceBean> penaltyList) {
        if (penalty > 0) {
            Drawable drawable = getResources().getDrawable(R.mipmap.ic_orange_arrow);
            mUserTotalDepositTv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            mUserTotalDepositTv.setEnabled(true);
            mUserTotalDepositTv.setText(getString(R.string.deposit_balance_penalty, MoneyUtils.formatMoney(totalDeposit), MoneyUtils.formatMoney(penalty)));
        } else {
            if (totalDeposit > 0 || occupyDeposit > 0) {
                Drawable drawable = getResources().getDrawable(R.mipmap.ic_orange_arrow);
                mUserTotalDepositTv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                mUserTotalDepositTv.setEnabled(true);
                mUserTotalDepositTv.setText(getString(R.string.deposit_balance_occupy_deposit, MoneyUtils.formatMoney(totalDeposit), MoneyUtils.formatMoney(occupyDeposit)));
            } else {
                mUserTotalDepositTv.setEnabled(false);
                mUserTotalDepositTv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                mUserTotalDepositTv.setText(getString(R.string.deposit_balance_occupy_deposit, MoneyUtils.formatMoney(0), MoneyUtils.formatMoney(0)));
            }
        }

        mDepositBalanceList.clear();
        mDepositBalanceList.addAll(list);
        mPenaltyList.clear();
        mPenaltyList.addAll(penaltyList);
    }

    @Override
    public void showLoading() {
        mLoadingProgressView.showProgressLayout();
    }

    @Override
    public void dismissLoading() {
        mLoadingProgressView.hideProgressLayout();
    }

    @Override
    public void showProgressLoading() {
        showDialog("加载中...");
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
    public void showDialogTip(int resId, final boolean refresh) {
        final CustomDialog customDialog = new CustomDialog(this, R.style.DialogTheme);
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(false);
        customDialog.setButtonTextConfirmOrYes(true);
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
                if (refresh) {
                    setResult(RESULT_OK);
                }
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

    @Override
    public void showLimitMoneyTip(String money) {
        final CustomDialog customDialog = new CustomDialog(this, R.style.DialogTheme);
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(false);
        customDialog.setButtonTextConfirmOrYes(true);
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
        customDialog.setText(Html.fromHtml(getString(R.string.pay_deposit_limit_tip, money)));
        customDialog.show();
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receviceLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }
}
