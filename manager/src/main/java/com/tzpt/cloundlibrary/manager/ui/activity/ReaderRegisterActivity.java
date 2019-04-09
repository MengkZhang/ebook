package com.tzpt.cloundlibrary.manager.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.bean.IDCardBean;
import com.tzpt.cloundlibrary.manager.ui.contract.ReaderRegisterContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.ReaderRegisterPresenter;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;
import com.tzpt.cloundlibrary.manager.utils.glide.GlideApp;
import com.tzpt.cloundlibrary.manager.widget.CustomTipsDialog;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 读者注册界面
 * Created by Administrator on 2017/7/6.
 */
public class ReaderRegisterActivity extends BaseActivity implements ReaderRegisterContract.View {
    private static final String ID_CARD_BEAN = "id_card_bean";
    private static final String FROM_TYPE = "from_type";

    public static void startActivity(Context context, IDCardBean idCardBean, int fromType) {
        Intent intent = new Intent(context, ReaderRegisterActivity.class);
        intent.putExtra(ID_CARD_BEAN, idCardBean);
        intent.putExtra(FROM_TYPE, fromType);
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
    @BindView(R.id.register_pwd_et)
    EditText mRegisterPwdEt;
    @BindView(R.id.register_sure_pwd_et)
    EditText mRegisterSurePwdEt;
    @BindView(R.id.register_phone_et)
    EditText mRegisterPhoneEt;
    @BindView(R.id.register_code_et)
    EditText mRegisterCodeEt;
    @BindView(R.id.register_send_code_tv)
    TextView mRegisterSendCodeTv;

    private ReaderRegisterPresenter mPresenter;
    private IDCardBean mIDCardBean;
    private Bitmap mHeadBmp;
    private TimeCount mTimer;
    private int mFromType;

    private String mBundleTel;

    private static final int START_TIMER = 1000;
    private static final int FINISH_TIMER = 1001;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_TIMER:
                    if (mRegisterSendCodeTv != null) {
                        mRegisterSendCodeTv.setClickable(false);
                        long millisUntilFinished = (long) msg.obj;
                        mRegisterSendCodeTv.setText(Html.fromHtml("<font color='#ff0000'>" +
                                millisUntilFinished / 1000 + "</font><font color = '#8a633d'>秒后重发</font>"));
                    }

                    break;
                case FINISH_TIMER:
                    if (mRegisterSendCodeTv != null) {
                        mRegisterSendCodeTv.setText(Html.fromHtml("<u>" + "再次发送" + "</u>"));
                        mRegisterSendCodeTv.setClickable(true);
                        //设置手机号可编辑
                        setEditTextAble(mRegisterPhoneEt, true);
                    }
                    break;
            }
        }
    };

    @OnClick({R.id.titlebar_left_btn, R.id.register_send_code_tv, R.id.register_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.register_send_code_tv:
                mRegisterSendCodeTv.setClickable(false);

                //发送验证码
                String phoneContent = mRegisterPhoneEt.getText().toString().trim();
                int isContinue = 0;
                if (!TextUtils.isEmpty(mBundleTel) && mBundleTel.equals(phoneContent)) {
                    isContinue = 1;
                }
                mPresenter.getCode(phoneContent, isContinue);
                break;
            case R.id.register_btn:
                String code = mRegisterCodeEt.getText().toString().trim();
                String pwd = mRegisterPwdEt.getText().toString().trim();
                String surePwd = mRegisterSurePwdEt.getText().toString().trim();
                String telNum = mRegisterPhoneEt.getText().toString().trim();
                mPresenter.register(mIDCardBean.NUM, code, mIDCardBean.NAME, mHeadBmp, pwd, surePwd, telNum, mIDCardBean.SEX, mBundleTel);
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_reader_register;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("读者注册");
    }

    @Override
    public void initDatas() {
        mIDCardBean = (IDCardBean) getIntent().getSerializableExtra(ID_CARD_BEAN);
        mFromType = getIntent().getIntExtra(FROM_TYPE, 0);

        GlideApp.with(this).load(mIDCardBean.smallHeadPath).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.mipmap.ic_idcard).centerCrop().into(mReaderHeadIv);
        if (!TextUtils.isEmpty(mIDCardBean.smallHeadPath)) {
            GlideApp.with(this).asBitmap().load(mIDCardBean.smallHeadPath).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            mHeadBmp = resource;
                        }
                    });
        }
        mReaderNameTv.setText(mIDCardBean.NAME);
        mReaderIdCardTv.setText(StringUtils.setIdCardNumberForRegisterReader(mIDCardBean.NUM));
        mReaderSexTv.setText(mIDCardBean.SEX);

        if (!TextUtils.isEmpty(mIDCardBean.mBundleTel) && StringUtils.isMobileNumber(mIDCardBean.mBundleTel)) {
            mBundleTel = mIDCardBean.mBundleTel;
            mRegisterPhoneEt.setText(mIDCardBean.mBundleTel);
        }

        mPresenter = new ReaderRegisterPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        if (mHandler != null) {
            mHandler.removeMessages(START_TIMER);
            mHandler.removeMessages(FINISH_TIMER);
            mHandler = null;
        }
    }

    @Override
    public void sendVerifyMessageCode(boolean state, String msg) {
        showToastMsg(msg);
        if (!state) {
            stopTimer();
            mRegisterSendCodeTv.setClickable(true);
            setEditTextAble(mRegisterPhoneEt, true);
        } else {
            startTimer();
        }
    }

    @Override
    public void showDialogTelUnBundle(String bundleIdCard) {
        stopTimer();
        mRegisterSendCodeTv.setClickable(true);
        setEditTextAble(mRegisterPhoneEt, true);

        if (mIDCardBean.NUM.equals(bundleIdCard)) {
            showDialogRegisterFailed(R.string.tel_is_same);
        } else {
            String newIdCard = bundleIdCard.replace(bundleIdCard.substring(6, 14), "****");

            final CustomTipsDialog dialog = new CustomTipsDialog(this, R.style.DialogTheme, "");
            dialog.setSubTips(getString(R.string.binding_id_card_number, newIdCard));
            dialog.setCancelable(false);
            dialog.show();
            dialog.setOnClickBtnListener(new CustomTipsDialog.OnClickBtnListener() {
                @Override
                public void onClickOk() {
                    dialog.dismiss();
                    mPresenter.getCode(mRegisterPhoneEt.getText().toString().trim(), 1);
                }

                @Override
                public void onClickCancel() {
                    dialog.dismiss();
                }
            });
        }


    }

    @Override
    public void registerSuccess(String readerId) {
        if (null != mIDCardBean) {
            mIDCardBean.ID = readerId;
            gotoActivity();
        }
    }

    @Override
    public void showDialogRegisterFailed(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.hasNoCancel(false);
        dialog.setCancelable(false);
        dialog.show();
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
    }

    private void gotoActivity() {
        switch (mFromType) {
            case 0:
                BorrowBookManagementActivity.startActivity(this, mIDCardBean.ID);
                finish();
                break;
            case 1:
                Intent intent1 = new Intent(this, LostBookActivity.class);
                intent1.putExtra("ID_CARD_BEAN_ID", mIDCardBean.ID);
                intent1.putExtra("ID_CARD_BEAN_NUM", mIDCardBean.NUM);
                startActivityForResult(intent1, 1000);
                finish();
                break;
            case 2:
                RefundDepositActivity.startActivity(this, mIDCardBean.ID);
                finish();
                break;
            case 3:
                finish();
                break;
            case 4:
                ChargeLibDepositActivity.startActivity(this, mIDCardBean.ID);
                break;
        }
    }

    @Override
    public void showToastMsg(String msg) {
        mRegisterSendCodeTv.setClickable(true);
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
                LoginActivity.startActivity(ReaderRegisterActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
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
            if (mRegisterSendCodeTv != null) {
                mRegisterSendCodeTv.setText(Html.fromHtml("<u>" + "发送验证码" + "</u>"));
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
