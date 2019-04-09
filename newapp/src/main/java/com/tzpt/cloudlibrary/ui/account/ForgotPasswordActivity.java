package com.tzpt.cloudlibrary.ui.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.utils.PhoneNumberUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 忘记密码
 */
public class ForgotPasswordActivity extends BaseActivity implements
        ForgotPasswordContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ForgotPasswordActivity.class);
        context.startActivity(intent);
    }


    @BindView(R.id.forget_psw_et)
    EditText mForgetPswEt;
    @BindView(R.id.forget_clear_box)
    ImageView mForgetClearBox;
    @BindView(R.id.forget_id_card_layout)
    LinearLayout mForgetIdCardLayout;
    @BindView(R.id.forget_send_msg_tv)
    TextView mForgetSendMsgTv;
    @BindView(R.id.forget_send_error_tv)
    TextView mForgetSendErrorTv;
    @BindView(R.id.forget_send_code_et)
    EditText mForgetSendCodeEt;
    @BindView(R.id.forget_send_tv)
    TextView mForgetSendTv;
    @BindView(R.id.input_verify_code_layout)
    LinearLayout mInputVerifyCodeLayout;
    @BindView(R.id.forget_new_password)
    EditText mForgetNewPassword;
    @BindView(R.id.clear_new_password_box)
    ImageView mClearNewPasswordBox;
    @BindView(R.id.forget_new_password2)
    EditText mForgetNewPassword2;
    @BindView(R.id.clear_new_password_box2)
    ImageView mClearNewPasswordBox2;
    @BindView(R.id.forget_new_psw_layout)
    LinearLayout mForgetNewPswLayout;
    @BindView(R.id.ic_password_ok)
    ImageView mIcPasswordOk;
    @BindView(R.id.forget_psw_ok_layout)
    RelativeLayout mForgetPswOkLayout;
    @BindView(R.id.forget_psw_next_btn)
    Button mForgetPswNextBtn;
    @BindView(R.id.progress_layout)
    LoadingProgressView mProgressLayout;

    private ForgotPasswordPresenter mPresenter;

    private static final int FORGET_PSW_TYPE_DEFAULT = 0;           //默认界面
    private static final int FORGET_PSW_TYPE_NOT_BINDING_PHONE_OR_NOT_REGISTER = 1; //没有绑定手机号界面或者没有注册
    private static final int FORGET_PSW_TYPE_SEND_CODE = 2;         //发送验证码界面
    private static final int FORGET_PSW_TYPE_CHANG_PASSWORD = 3;    //设置新密码界面
    private static final int FORGET_PSW_TYPE_LOGIN = 4;             //去登录界面界面
    private int mViewStatus = FORGET_PSW_TYPE_DEFAULT;
    private String mPhoneNumber;


    @Override
    public int getLayoutId() {
        return R.layout.activity_forgot_password;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("忘记密码");
    }

    @Override
    public void initDatas() {
        mPresenter = new ForgotPasswordPresenter();
        mPresenter.attachView(this);
        mPresenter.getIDCard();
    }

    @Override
    public void configViews() {
        mForgetPswEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mForgetClearBox.setVisibility((s.length() == 0) ? View.INVISIBLE : View.VISIBLE);
                mForgetPswNextBtn.setBackgroundResource((s.length() > 17) ?
                        R.drawable.btn_login : R.drawable.phone_manage_button_bg);
                mForgetPswNextBtn.setClickable((s.length() > 17));
            }
        });

        //验证码是六位开启下一步
        mForgetSendCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mForgetPswNextBtn.setBackgroundResource((s.length() > 3) ?
                        R.drawable.btn_login : R.drawable.phone_manage_button_bg);
                mForgetPswNextBtn.setClickable((s.length() > 3));
            }
        });
        //新密码

        mForgetNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mClearNewPasswordBox.setVisibility((s.length() == 0) ? View.INVISIBLE : View.VISIBLE);
            }
        });
        //新密码2
        mForgetNewPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mClearNewPasswordBox2.setVisibility((s.length() == 0) ? View.INVISIBLE : View.VISIBLE);
            }
        });

    }

    @OnClick({R.id.titlebar_left_btn, R.id.forget_psw_next_btn, R.id.forget_send_tv, R.id.forget_clear_box,
            R.id.clear_new_password_box2, R.id.clear_new_password_box})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                goBack();
                break;
            case R.id.forget_psw_next_btn://下一步

                switch (mViewStatus) {
                    case FORGET_PSW_TYPE_DEFAULT:   //检查身份证是否绑定手机号
                        KeyboardUtils.hideSoftInput(mForgetPswEt);
                        mPresenter.checkPhoneIsBinding(mForgetPswEt.getText().toString().trim());
                        break;
                    case FORGET_PSW_TYPE_NOT_BINDING_PHONE_OR_NOT_REGISTER:

                        break;
                    case FORGET_PSW_TYPE_SEND_CODE:
                        if (null != mPhoneNumber) {
                            mPresenter.verifyCode(mPhoneNumber, mForgetSendCodeEt.getText().toString().trim());
                        }
                        break;
                    case FORGET_PSW_TYPE_CHANG_PASSWORD:
                        KeyboardUtils.hideSoftInput(this);
                        mPresenter.resetPsw(mForgetPswEt.getText().toString().trim(),
                                mForgetNewPassword.getText().toString().trim(),
                                mForgetNewPassword2.getText().toString().trim());
                        break;
                    case FORGET_PSW_TYPE_LOGIN://去登录
                        AutoLoginMessage msg = new AutoLoginMessage();
                        msg.idCard = mForgetPswEt.getText().toString().trim();
                        msg.psw = mForgetNewPassword.getText().toString().trim();
                        EventBus.getDefault().post(msg);
                        finish();
                        break;
                }

                break;
            case R.id.forget_send_tv:
                if (!TextUtils.isEmpty(mPhoneNumber)) {
                    mPresenter.getVerifyCode(mPhoneNumber);
                }
                break;
            case R.id.forget_clear_box:
                mForgetPswEt.setText("");
                break;
            case R.id.clear_new_password_box2:
                mForgetNewPassword2.setText("");
                break;
            case R.id.clear_new_password_box:
                mForgetNewPassword.setText("");
                break;

        }

    }

    @Override
    public void showProgressDialog() {
        mProgressLayout.showProgressLayout();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressLayout.hideProgressLayout();
    }

    @Override
    public void showToastMsg(String msg) {
        ToastUtils.showSingleToast(msg);
    }

    @Override
    public void showToastMsg(int resId) {
        ToastUtils.showSingleToast(resId);
    }

    //2.绑定手机号，发送验证码
    @Override
    public void idCardBindingPhoneNumber(String phone) {
        this.mPhoneNumber = phone;
        setSendCodeView();
        if (!TextUtils.isEmpty(mPhoneNumber)) {
            mPresenter.getVerifyCode(mPhoneNumber);
        }
    }

    //1没有绑定手机号界面
    @Override
    public void idCardNotBindingPhoneNumber() {
        mViewStatus = FORGET_PSW_TYPE_NOT_BINDING_PHONE_OR_NOT_REGISTER;
        mForgetIdCardLayout.setVisibility(View.VISIBLE);
        mForgetSendMsgTv.setVisibility(View.GONE);
        mForgetSendErrorTv.setVisibility(View.VISIBLE);
        mInputVerifyCodeLayout.setVisibility(View.GONE);
        mForgetNewPswLayout.setVisibility(View.GONE);
        mForgetPswOkLayout.setVisibility(View.GONE);

        mForgetSendErrorTv.setTextColor(Color.parseColor("#fb7359"));
        mForgetSendErrorTv.setText(R.string.forget_pwd_no_binding_phone);
        mForgetPswNextBtn.setBackgroundResource(R.drawable.phone_manage_button_bg);
        mForgetPswNextBtn.setClickable(false);
        mForgetPswNextBtn.setText(R.string.forget_pwd_next);
    }

    @Override
    public void idCardNotRegister() {
        mViewStatus = FORGET_PSW_TYPE_NOT_BINDING_PHONE_OR_NOT_REGISTER;
        mForgetIdCardLayout.setVisibility(View.VISIBLE);
        mForgetSendMsgTv.setVisibility(View.GONE);
        mForgetSendErrorTv.setVisibility(View.VISIBLE);
        mInputVerifyCodeLayout.setVisibility(View.GONE);
        mForgetNewPswLayout.setVisibility(View.GONE);
        mForgetPswOkLayout.setVisibility(View.GONE);

        mForgetSendErrorTv.setTextColor(Color.parseColor("#fb7359"));
        mForgetSendErrorTv.setText(R.string.forget_pwd_no_register);
        mForgetPswNextBtn.setBackgroundResource(R.drawable.phone_manage_button_bg);
        mForgetPswNextBtn.setClickable(false);
        mForgetPswNextBtn.setText(R.string.forget_pwd_next);
    }

    @Override
    public void setSendCodeStyle(int type) {
        switch (type) {
            case 0://设置不可点击
                setVerifyTextViewState(false);
                break;
            case 1://计时完成
                mForgetSendTv.setText(R.string.send_verify_code);
                setVerifyTextViewState(true);
                break;
            case 2://出现错误-网络故障等
                setVerifyTextViewState(true);
                mForgetSendTv.setText(R.string.send_verify_code);
                break;
        }
    }

    @Override
    public void setSendCodeTime(long time) {
        if (null != mForgetSendTv) {
            setVerifyTextViewState(false);
            mForgetSendTv.setText(Html.fromHtml("<font color='#ff0000'>" + time + "</font><font color = '#694a2c'>秒后重发</font>"));
        }
    }

    //验证验证码成功
    @Override
    public void setVerifyCodeSuccess() {
        setNewPswView();
    }

    //重置密码成功
    @Override
    public void resetPswSuccess() {
        setToLoginView();
    }

    @Override
    public void setIdCard(String idCard) {
        if (!TextUtils.isEmpty(idCard)) {
            mForgetPswEt.setText(idCard);
            mForgetPswEt.setSelection(idCard.length());//设置光标
            mForgetClearBox.setVisibility((idCard.length() == 0) ? View.INVISIBLE : View.VISIBLE);
            mForgetPswNextBtn.setBackgroundResource((idCard.length() > 17) ?
                    R.drawable.btn_login : R.drawable.phone_manage_button_bg);
            mForgetPswNextBtn.setClickable(idCard.length() > 17);
        } else {
            mForgetPswNextBtn.setClickable(false);
        }
    }

    @Override
    public void pleaseLoginTip() {

    }

    /**
     * 默认设置发送验证码是否可点击
     *
     * @param canClick
     */
    private void setVerifyTextViewState(boolean canClick) {
        if (canClick) {
            //默认设置发送验证码可点击
            mForgetSendTv.setClickable(true);
            mForgetSendTv.setBackgroundResource(R.drawable.item_verifycode_selector);
        } else {
            //默认设置发送验证码不可点击
            mForgetSendTv.setClickable(false);
            mForgetSendTv.setBackgroundResource(R.drawable.bg_round_verify_code);
        }
    }

    private void setDefaultView() {
        mViewStatus = FORGET_PSW_TYPE_DEFAULT;
        mForgetIdCardLayout.setVisibility(View.VISIBLE);
        mForgetSendMsgTv.setVisibility(View.GONE);
        mForgetSendErrorTv.setVisibility(View.GONE);
        mInputVerifyCodeLayout.setVisibility(View.GONE);
        mForgetNewPswLayout.setVisibility(View.GONE);
        mForgetPswOkLayout.setVisibility(View.GONE);
        mForgetPswNextBtn.setText(R.string.forget_pwd_next);

        mForgetPswNextBtn.setBackgroundResource(R.drawable.phone_manage_button_bg);
        String idCard = mForgetPswEt.getText().toString().trim();
        mForgetClearBox.setVisibility((idCard.length() == 0) ? View.INVISIBLE : View.VISIBLE);
        mForgetPswNextBtn.setBackgroundResource((idCard.length() > 17) ?
                R.drawable.btn_login : R.drawable.phone_manage_button_bg);
        mForgetPswNextBtn.setClickable(idCard.length() > 17);
        resetSendCode();
    }


    private void setSendCodeView() {
        mViewStatus = FORGET_PSW_TYPE_SEND_CODE;
        mForgetIdCardLayout.setVisibility(View.GONE);
        mForgetSendMsgTv.setVisibility(View.VISIBLE);
        mForgetSendErrorTv.setVisibility(View.VISIBLE);
        mInputVerifyCodeLayout.setVisibility(View.VISIBLE);
        mForgetNewPswLayout.setVisibility(View.GONE);
        mForgetPswOkLayout.setVisibility(View.GONE);
        mForgetPswNextBtn.setText(R.string.forget_pwd_next);

        mForgetSendMsgTv.setText(Html.fromHtml("已发送<font color = '#ff0000'>验证码</font>到您绑定的手机<font color= '#ff0000'>"
                + PhoneNumberUtils.setPhoneNumberInfo(mPhoneNumber) + "</font>"));
        mForgetSendErrorTv.setTextColor(Color.parseColor("#666666"));
        mForgetSendErrorTv.setText(R.string.forget_pwd_change_phone_tip);

    }

    private void setNewPswView() {
        mViewStatus = FORGET_PSW_TYPE_CHANG_PASSWORD;
        mForgetIdCardLayout.setVisibility(View.GONE);
        mForgetSendMsgTv.setVisibility(View.GONE);
        mForgetSendErrorTv.setVisibility(View.GONE);
        mInputVerifyCodeLayout.setVisibility(View.GONE);
        mForgetNewPswLayout.setVisibility(View.VISIBLE);
        mForgetPswOkLayout.setVisibility(View.GONE);
        mForgetPswNextBtn.setText("确定");
        resetSendCode();
    }

    private void setToLoginView() {
        mViewStatus = FORGET_PSW_TYPE_LOGIN;
        mForgetIdCardLayout.setVisibility(View.GONE);
        mForgetSendMsgTv.setVisibility(View.GONE);
        mForgetSendErrorTv.setVisibility(View.GONE);
        mInputVerifyCodeLayout.setVisibility(View.GONE);
        mForgetNewPswLayout.setVisibility(View.GONE);
        mForgetPswOkLayout.setVisibility(View.VISIBLE);
        mForgetPswNextBtn.setText("立即登录");
    }

    //重置发送验证码
    private void resetSendCode() {
        mPresenter.unSubscriptionTime();
        mForgetSendTv.setText(R.string.send_verify_code);
        setVerifyTextViewState(true);
    }

    private void goBack() {
        switch (mViewStatus) {
            case FORGET_PSW_TYPE_DEFAULT:
                finish();
                break;
            case FORGET_PSW_TYPE_NOT_BINDING_PHONE_OR_NOT_REGISTER:
                finish();
                break;
            case FORGET_PSW_TYPE_SEND_CODE:
                setDefaultView();
                break;
            case FORGET_PSW_TYPE_CHANG_PASSWORD:
                setSendCodeView();
                break;
            case FORGET_PSW_TYPE_LOGIN:
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 如果按了返回键
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            goBack();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }
}
