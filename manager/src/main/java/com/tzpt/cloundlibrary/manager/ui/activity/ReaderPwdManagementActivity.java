package com.tzpt.cloundlibrary.manager.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.ui.contract.ReaderPwdContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.ReaderPwdPresenter;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;
import com.tzpt.cloundlibrary.manager.utils.glide.GlideApp;
import com.tzpt.cloundlibrary.manager.widget.CustomTipsDialog;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 读者密码管理
 * Created by Administrator on 2017/7/9.
 */

public class ReaderPwdManagementActivity extends BaseActivity implements ReaderPwdContract.View {
    private static final String ID_CARD_BEAN_ID = "id_card_bean_id";

    public static void startActivity(Context context, String readerId) {
        Intent intent = new Intent(context, ReaderPwdManagementActivity.class);
        intent.putExtra(ID_CARD_BEAN_ID, readerId);
        context.startActivity(intent);
    }

    @BindView(R.id.reader_head_iv)
    ImageView mReaderHeadIv;
    @BindView(R.id.reader_name_tv)
    TextView mReaderNameTv;
    @BindView(R.id.reader_id_card_tv)
    TextView mReaderIdCardTv;
    @BindView(R.id.reader_sex_tv)
    TextView mReaderSexTv;
    @BindView(R.id.reader_pwd_et)
    EditText mReaderPwdEt;
    @BindView(R.id.reader_sure_pwd_et)
    EditText mReaderSurePwdEt;
    @BindView(R.id.reader_phone_et)
    EditText mReaderPhoneEt;
    @BindView(R.id.reader_code_et)
    EditText mReaderCodeEt;
    @BindView(R.id.send_code_tv)
    TextView mSendCodeTv;

    private String mReaderId;
    private ReaderPwdPresenter mPresenter;
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
                        mSendCodeTv.setClickable(false);
                        long millisUntilFinished = (long) msg.obj;
                        mSendCodeTv.setText(Html.fromHtml("<font color='#ff0000'>" + millisUntilFinished / 1000
                                + "</font><font color = '#8a633d'>秒后重发</font>"));
                    }
                    break;
                case FINISH_TIMER:
                    if (mSendCodeTv != null) {
                        mSendCodeTv.setText(Html.fromHtml("<u>"+"再次发送"+"</u>"));
                        mSendCodeTv.setClickable(true);
                        //设置手机号可编辑
                        setEditTextAble(mReaderCodeEt, true);
                    }
                    break;
            }
        }
    };

    @OnClick({R.id.confirm_btn, R.id.send_code_tv, R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confirm_btn:
                String pwd = mReaderPwdEt.getText().toString().trim();
                String surePwd = mReaderSurePwdEt.getText().toString().trim();
                String phone = mReaderPhoneEt.getText().toString().trim();
                String code = mReaderCodeEt.getText().toString().trim();
                mPresenter.modifyInfo(pwd, surePwd, phone, code);
                break;
            case R.id.send_code_tv:
                mSendCodeTv.setClickable(false);
                mPresenter.getCode(mReaderPhoneEt.getText().toString().trim(), 0);
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_reader_pwd_management;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("读者管理");
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
    }

    @Override
    public void initDatas() {
        mReaderId = getIntent().getStringExtra(ID_CARD_BEAN_ID);

        mPresenter = new ReaderPwdPresenter();
        mPresenter.attachView(this);
        mPresenter.getReaderInfo(mReaderId);
    }

    @Override
    public void configViews() {

    }


    @Override
    public void setReaderName(String name) {
        mReaderNameTv.setText(name);
    }

    @Override
    public void setReaderIdCard(String idCard) {
        mReaderIdCardTv.setText(StringUtils.setIdCardNumberForRegisterReader(idCard));
    }

    @Override
    public void setReaderGender(String gender) {
        mReaderSexTv.setText(gender);
    }

    @Override
    public void setReaderHead(String headImg) {
        GlideApp.with(this).load(headImg).placeholder(R.mipmap.ic_idcard).centerCrop().into(mReaderHeadIv);
    }

    @Override
    public void setReaderPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            mReaderPhoneEt.setHint("手机号");
        } else {
            mReaderPhoneEt.setHint(phone);
        }
    }

    @Override
    public void showDialogGetInfoFailedRetry(String msg) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.setButtonTextConfirmOrYes2(false);
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mPresenter.getReaderInfo(mReaderId);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    @Override
    public void sendVerifyMessageCode(boolean state, String msg) {
        showToastMsg(msg);
        if (!state) {
            stopTimer();
            mSendCodeTv.setClickable(true);
            setEditTextAble(mReaderPhoneEt, true);
        } else {
            startTimer();
        }
    }

    @Override
    public void showDialogTelUnBundle(String bundleIdCard) {
        stopTimer();
        mSendCodeTv.setClickable(true);
        setEditTextAble(mReaderCodeEt, true);

        String newIdCard = bundleIdCard.replace(bundleIdCard.substring(6, 14), "****");

        final CustomTipsDialog dialog = new CustomTipsDialog(this, R.style.DialogTheme, "");
        dialog.setSubTips(getString(R.string.binding_id_card_number, newIdCard));
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomTipsDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mPresenter.getCode(mReaderPhoneEt.getText().toString().trim(), 1);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });

    }

    @Override
    public void showToastMsg(String msg) {
        mSendCodeTv.setClickable(true);
        showMessageDialog(msg);
    }

    @Override
    public void showDialogModifySuccess(final int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
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

    @Override
    public void showDialogModifyFailed(int msgId) {
        stopTimer();
        mSendCodeTv.setClickable(true);
        setEditTextAble(mReaderCodeEt, true);

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

    //没有权限
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
                LoginActivity.startActivity(ReaderPwdManagementActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void showLoadingDialog(String tips) {
        showDialog(tips);
    }

    @Override
    public void dismissLoadingDialog() {
        dismissDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        mPresenter.detachView();
        if (mHandler != null) {
            mHandler.removeMessages(START_TIMER);
            mHandler.removeMessages(FINISH_TIMER);
            mHandler = null;
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
                mSendCodeTv.setText(Html.fromHtml("<u>"+"发送验证码"+"</u>"));
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

    /**
     * 设置editText 是否可编辑状态
     *
     * @param editText
     * @param able
     */
    private void setEditTextAble(EditText editText, boolean able) {
        editText.setFocusable(able);
        editText.setFocusableInTouchMode(able);
        if (able) {
            editText.requestFocus();
        } else {
            editText.clearFocus();
        }
    }
}
