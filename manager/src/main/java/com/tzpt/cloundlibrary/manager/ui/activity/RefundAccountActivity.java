package com.tzpt.cloundlibrary.manager.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.ui.contract.RefundAccountContact;
import com.tzpt.cloundlibrary.manager.ui.presenter.RefundAccountPresenter;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置退款账户
 * Created by Administrator on 2018/5/23.
 */

public class RefundAccountActivity extends BaseActivity implements RefundAccountContact.View {
    private static final String HAVE_REFUND_ACCOUNT = "have_refund_account";

    public static void startActivity(Context context, boolean haveRefundAccount) {
        Intent intent = new Intent(context, RefundAccountActivity.class);
        intent.putExtra(HAVE_REFUND_ACCOUNT, haveRefundAccount);
        context.startActivity(intent);
    }

    @BindView(R.id.refund_account_et)
    EditText mRefundAccountEt;
    @BindView(R.id.refund_really_name_et)
    EditText mRefundReallyNameEt;
    @BindView(R.id.refund_code_et)
    EditText mRefundCodeEt;
    @BindView(R.id.refund_send_code_tv)
    TextView mRefundSendCodeTv;

    private TimeCount mTimer;
    private RefundAccountPresenter mPresenter;
    private boolean mHaveRefundAccount;

    private static final int START_TIMER = 1000;
    private static final int FINISH_TIMER = 1001;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_TIMER:
                    if (mRefundSendCodeTv != null) {
                        mRefundSendCodeTv.setClickable(false);
                        long millisUntilFinished = (long) msg.obj;
                        mRefundSendCodeTv.setText(Html.fromHtml("<font color='#ff0000'>" +
                                millisUntilFinished / 1000 + "</font><font color = '#8a633d'>秒后重发</font>"));
                    }

                    break;
                case FINISH_TIMER:
                    if (mRefundSendCodeTv != null) {
                        mRefundSendCodeTv.setText(Html.fromHtml("<u>"+"再次发送"+"</u>"));
                        mRefundSendCodeTv.setClickable(true);
                    }
                    break;
            }
        }
    };

    @OnClick({R.id.refund_send_code_tv, R.id.refund_account_change_btn, R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.refund_send_code_tv:
                mPresenter.getCode();
                break;
            case R.id.refund_account_change_btn:
                mPresenter.changeRefundAccount(mRefundCodeEt.getText().toString().trim(),
                        mRefundAccountEt.getText().toString().trim(),
                        mRefundReallyNameEt.getText().toString().trim());
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_refund_account;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
    }

    @Override
    public void initDatas() {
        mPresenter = new RefundAccountPresenter();
        mPresenter.attachView(this);
        mHaveRefundAccount = getIntent().getBooleanExtra(HAVE_REFUND_ACCOUNT, false);
        if (mHaveRefundAccount) {
            mCommonTitleBar.setTitle(R.string.change_refund_account);
        } else {
            mCommonTitleBar.setTitle(R.string.set_refund_account);
        }
    }

    @Override
    public void configViews() {

    }

    @Override
    public void sendVerifyMessageCode(boolean state, String msg) {
        showToastMsg(msg);
        if (!state) {
            stopTimer();
            mRefundSendCodeTv.setClickable(true);
        } else {
            startTimer();
        }
    }

    @Override
    public void showToastMsg(String msg) {
        mRefundSendCodeTv.setClickable(true);
        showMessageDialog(msg);
    }

    @Override
    public void showLoading(String msg) {
        showDialog(msg);
    }

    @Override
    public void dismissLoading() {
        dismissDialog();
    }

    @Override
    public void noPermissionPrompt(int kickedOffline) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(kickedOffline));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                LoginActivity.startActivity(RefundAccountActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void showFailedDialog(int msgId) {
        stopTimer();
        mRefundSendCodeTv.setClickable(true);

        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void showSuccessDialog() {
        String msg;
        if (mHaveRefundAccount) {
            msg = getString(R.string.change_refund_account_success);
        } else {
            msg = getString(R.string.set_refund_account_success);
        }
        stopTimer();
        mRefundSendCodeTv.setClickable(true);

        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void initTimer() {
        mTimer = new TimeCount(60000, 1000);
    }

    /**
     * 开始发送验证码-计时
     */
    private void startTimer() {
        if (null == mTimer) {
            initTimer();
        }
        if (null != mTimer) {
            mTimer.start();
        }
    }

    /**
     * 停止发送验证码
     */
    private void stopTimer() {
        if (null != mTimer) {
            mTimer.cancel();
            if (mRefundSendCodeTv != null) {
                mRefundSendCodeTv.setText(Html.fromHtml("<u>"+"发送验证码"+"</u>"));
            }
        }
    }


    /* 定义一个倒计时的内部类 */
    private class TimeCount extends CountDownTimer {

        private TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            if (mHandler != null) {
                mHandler.sendEmptyMessage(FINISH_TIMER);
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            if (mHandler != null) {
                Message msg = new Message();
                msg.what = START_TIMER;
                msg.obj = millisUntilFinished;
                mHandler.sendMessage(msg);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null){
            mPresenter.detachView();
            mPresenter = null;
        }

    }
}
