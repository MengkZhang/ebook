package com.tzpt.cloundlibrary.manager.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.ui.contract.VerifyIdentityContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.VerifyIdentityPresenter;
import com.tzpt.cloundlibrary.manager.utils.AllCapTransformationMethod;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 验证身份
 * Created by Administrator on 2018/9/26.
 */

public class VerifyIdentityActivity extends BaseActivity implements VerifyIdentityContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, VerifyIdentityActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.library_num_et)
    EditText mLibCodeEt;
    @BindView(R.id.phone_et)
    EditText mPhoneEt;
    @BindView(R.id.verify_code_et)
    EditText mVerifyCodeEt;
    @BindView(R.id.send_code_tv)
    TextView mSendCodeTv;

    private VerifyIdentityPresenter mPresenter;

    private TimeCount mTimer;

    private static final int START_TIMER = 1000;
    private static final int FINISH_TIMER = 1001;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_TIMER:
                    if (mSendCodeTv != null) {
                        setSendCodeTvClickable(false);
                        long millisUntilFinished = (long) msg.obj;
                        mSendCodeTv.setText(Html.fromHtml("<font color='#ff0000'>" + millisUntilFinished / 1000
                                + "</font><font color = '#8a633d'>秒后重发</font>"));
                    }
                    break;
                case FINISH_TIMER:
                    if (mSendCodeTv != null) {
                        mSendCodeTv.setText(Html.fromHtml("<u>" + "再次发送" + "</u>"));
                        setSendCodeTvClickable(true);
                    }
                    break;
            }
        }
    };

    @OnClick({R.id.titlebar_left_btn, R.id.send_code_tv, R.id.confirm_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.send_code_tv:
                mPresenter.sendCode(mLibCodeEt.getText().toString().trim(), mPhoneEt.getText().toString().trim());
                break;
            case R.id.confirm_btn:
                mPresenter.verifyIdentity(mVerifyCodeEt.getText().toString().trim(),
                        mLibCodeEt.getText().toString().trim(),
                        mPhoneEt.getText().toString().trim());
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_verify_identity;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("验证身份");
    }

    @Override
    public void initDatas() {
        mPresenter = new VerifyIdentityPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {
        mSendCodeTv.setClickable(false);
        mPhoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mSendCodeTv.setClickable(true);
                } else {
                    mSendCodeTv.setClickable(false);
                }
            }
        });
        mLibCodeEt.setTransformationMethod(new AllCapTransformationMethod());
    }

    @Override
    public void showDialogNoPermission(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                LoginActivity.startActivity(VerifyIdentityActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void showToastMsg(String msg) {
        setSendCodeTvClickable(true);
        showMessageDialog(msg);
    }

    @Override
    public void sendVerifyMessageCode(boolean state, String msg) {
        showToastMsg(msg);
        if (!state) {
            stopTimer();
            setSendCodeTvClickable(true);
        } else {
            startTimer();
        }
    }

    @Override
    public void showDialogVerifyFailed(int msgId) {
        stopTimer();
        setSendCodeTvClickable(true);

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
    public void verifySuccess(String hallCode, String userName, int id) {
        SetPwdActivity.startActivity(this, id, hallCode, userName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    private void setSendCodeTvClickable(boolean clickable) {
        if (StringUtils.isMobileNumber(mPhoneEt.getText().toString().trim())) {
            mSendCodeTv.setClickable(clickable);
        } else {
            mSendCodeTv.setClickable(false);
        }
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
            if (mSendCodeTv != null) {
                mSendCodeTv.setText(Html.fromHtml("<u>" + "发送验证码" + "</u>"));
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

}
