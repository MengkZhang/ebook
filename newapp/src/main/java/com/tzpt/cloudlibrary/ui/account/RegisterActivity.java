package com.tzpt.cloudlibrary.ui.account;


import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户注册
 */
public class RegisterActivity extends BaseActivity implements
        RegisterContract.View {

    private static final int SEND_MSG_CODE_VIEW = 0;
    private static final int CHECK_ID_CARD_VIEW = 1;
    private static final int SET_PSW_VIEW = 2;
    private static final int TO_LOGIN_VIEW = 3;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.register_next_btn)
    Button mRegisterNextBtn;

    //send msg code UI
    @BindView(R.id.register_send_tv)
    TextView mRegisterSendTv;
    @BindView(R.id.register_phone_et)
    EditText mRegisterPhoneEt;
    @BindView(R.id.register_code_et)
    EditText mRegisterCodeEt;
    @BindView(R.id.register_delete_box1)
    ImageView mRegisterDeleteBox1;

    //verify idCard UI
    @BindView(R.id.register_nick_name_et)
    EditText mRegisterNickNameEt;
    @BindView(R.id.register_reader_name_et)
    EditText mRegisterReaderNameEt;
    @BindView(R.id.register_id_card_et)
    EditText mRegisterIdCardEt;
    @BindView(R.id.register_nick_delete_box1)
    ImageView mRegisterNickNameDeleteBox1;
    @BindView(R.id.register_name_delete_box1)
    ImageView mRegisterNameDeleteBox1;
    @BindView(R.id.register_name_delete_box2)
    ImageView mRegisterNameDeleteBox2;

    //set password UI
    @BindView(R.id.register_new_psw1_et)
    EditText mRegisterNewPswEt;
    @BindView(R.id.register_new_psw2_et)
    EditText mRegisterReNewPswEt;
    @BindView(R.id.register_psw_delete_box1)
    ImageView mRegisterPswDeleteBox1;
    @BindView(R.id.register_psw_delete_box2)
    ImageView mRegisterPswDeleteBox2;

    //set model UI
    @BindView(R.id.register_verify_code_ll)
    LinearLayout mRegisterVerifyCodeLl;
    @BindView(R.id.register_idcard_info_ll)
    LinearLayout mRegisterIdCardInfoCodeLl;
    @BindView(R.id.register_psw_info_ll)
    LinearLayout mRegisterPswInfoCodeLl;
    @BindView(R.id.register_to_login_ll)
    LinearLayout mRegisterToLoginCodeLl;

    private RegisterPresenter mPresenter;
    private int mCurrentUI = SEND_MSG_CODE_VIEW;

    @OnClick({R.id.titlebar_left_btn, R.id.register_next_btn, R.id.register_send_tv,
            R.id.register_delete_box1, R.id.register_name_delete_box1, R.id.register_name_delete_box2,
            R.id.register_psw_delete_box1, R.id.register_psw_delete_box2, R.id.register_nick_delete_box1})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                KeyboardUtils.hideSoftInput(this);
                operationKeyBack();
                break;
            case R.id.register_next_btn:
                KeyboardUtils.hideSoftInput(this);
                operationNextButton();
                break;
            case R.id.register_send_tv:
                mPresenter.sendPhoneVerifyCode(mRegisterPhoneEt.getText().toString().trim());
                break;
            case R.id.register_delete_box1:
                mRegisterPhoneEt.setText("");
                break;
            case R.id.register_name_delete_box1:
                mRegisterReaderNameEt.setText("");
                break;
            case R.id.register_name_delete_box2:
                mRegisterIdCardEt.setText("");
                break;
            case R.id.register_psw_delete_box1:
                mRegisterNewPswEt.setText("");
                break;
            case R.id.register_psw_delete_box2:
                mRegisterReNewPswEt.setText("");
                break;
            case R.id.register_nick_delete_box1:
                mRegisterNickNameEt.setText("");
                break;
        }
    }

    //操作下一步按钮
    private void operationNextButton() {
        switch (mCurrentUI) {
            case SEND_MSG_CODE_VIEW:
                mPresenter.verifyMsgCode(
                        mRegisterPhoneEt.getText().toString().trim(),
                        mRegisterCodeEt.getText().toString().trim());
                break;
            case CHECK_ID_CARD_VIEW:
                mPresenter.checkIdCardAvailable(
                        mRegisterIdCardEt.getText().toString().trim(),
                        mRegisterReaderNameEt.getText().toString().trim(),
                        mRegisterNickNameEt.getText().toString().trim());
                break;
            case SET_PSW_VIEW:
                mPresenter.register(
                        mRegisterPhoneEt.getText().toString().trim(),
                        mRegisterIdCardEt.getText().toString().trim(),
                        mRegisterReaderNameEt.getText().toString().trim(),
                        mRegisterNewPswEt.getText().toString().trim(),
                        mRegisterReNewPswEt.getText().toString().trim(),
                        mRegisterNickNameEt.getText().toString().trim());
                break;
            case TO_LOGIN_VIEW:
                mPresenter.startLogin();
                break;
        }
    }

    private InputFilter mReaderNameFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (" ".equals(source)) {
                return "";
            }
            return null;
        }
    };
    private TextWatcher mPhoneWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mRegisterDeleteBox1.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            mRegisterNextBtn.setBackgroundResource(s.length() >= 11 ? R.drawable.btn_login : R.drawable.phone_manage_button_bg);
            mRegisterNextBtn.setClickable(s.length() >= 11);
        }
    };
    private TextWatcher mNickNameWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mRegisterNickNameDeleteBox1.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            boolean isAble = mRegisterIdCardEt.getText().toString().trim().length() > 0
                    && mRegisterReaderNameEt.getText().toString().trim().length() > 0
                    && s.length() > 0;
            mRegisterNextBtn.setBackgroundResource(isAble ? R.drawable.btn_login : R.drawable.phone_manage_button_bg);
            mRegisterNextBtn.setClickable(isAble);
        }
    };
    private TextWatcher mReaderNameWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mRegisterNameDeleteBox1.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            boolean isAble = mRegisterIdCardEt.getText().toString().trim().length() > 0
                    && mRegisterNickNameEt.getText().toString().trim().length() > 0
                    && s.length() > 0;
            mRegisterNextBtn.setBackgroundResource(isAble ? R.drawable.btn_login : R.drawable.phone_manage_button_bg);
            mRegisterNextBtn.setClickable(isAble);
        }
    };
    private TextWatcher mIdCardWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mRegisterNameDeleteBox2.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            boolean isAble = mRegisterReaderNameEt.getText().toString().trim().length() > 0
                    && mRegisterNickNameEt.getText().toString().trim().length() > 0
                    && s.length() > 0;
            mRegisterNextBtn.setBackgroundResource(isAble ? R.drawable.btn_login : R.drawable.phone_manage_button_bg);
            mRegisterNextBtn.setClickable(isAble);
        }
    };
    private TextWatcher mPswWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mRegisterPswDeleteBox1.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            boolean isAble = mRegisterReNewPswEt.getText().toString().trim().length() == 6 && s.length() == 6;
            mRegisterNextBtn.setBackgroundResource(isAble ? R.drawable.btn_login : R.drawable.phone_manage_button_bg);
            mRegisterNextBtn.setClickable(isAble);
        }
    };
    private TextWatcher mRePswWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mRegisterPswDeleteBox2.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            boolean isAble = mRegisterNewPswEt.getText().toString().trim().length() == 6 && s.length() == 6;
            mRegisterNextBtn.setBackgroundResource(isAble ? R.drawable.btn_login : R.drawable.phone_manage_button_bg);
            mRegisterNextBtn.setClickable(isAble);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle(R.string.register);
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mPresenter = new RegisterPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {
        mRegisterPhoneEt.addTextChangedListener(mPhoneWatcher);
        mRegisterNickNameEt.addTextChangedListener(mNickNameWatcher);
        mRegisterReaderNameEt.addTextChangedListener(mReaderNameWatcher);
        mRegisterReaderNameEt.setFilters(new InputFilter[]{mReaderNameFilter, new InputFilter.LengthFilter(20)});
        mRegisterIdCardEt.addTextChangedListener(mIdCardWatcher);
        mRegisterNewPswEt.addTextChangedListener(mPswWatcher);
        mRegisterReNewPswEt.addTextChangedListener(mRePswWatcher);
        setSendMsgCodeView(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void showProgressDialog(String tips) {
        showDialog(tips);
    }

    @Override
    public void dismissProgressDialog() {
        dismissDialog();
    }

    @Override
    public void setSendCodeStyle(int type) {
        switch (type) {
            case 0://设置不可点击
                setVerifyTextViewState(false);
                mRegisterDeleteBox1.setVisibility(View.GONE);
                mRegisterPhoneEt.setEnabled(false);
                break;
            case 1://计时完成
                setVerifyTextViewState(true);
                mRegisterPhoneEt.setEnabled(true);
                mRegisterSendTv.setText(R.string.send_verify_code);
                break;
            case 2://出现错误-网络故障等
                setVerifyTextViewState(true);
                mRegisterPhoneEt.setEnabled(true);
                mRegisterSendTv.setText(R.string.send_verify_code);
                break;
        }
    }

    /**
     * 默认设置发送验证码是否可点击
     *
     * @param canClick
     */
    private void setVerifyTextViewState(boolean canClick) {
        if (canClick) {
            //默认设置发送验证码可点击
            mRegisterSendTv.setClickable(true);
            mRegisterSendTv.setBackgroundResource(R.drawable.item_verifycode_selector);
        } else {
            //默认设置发送验证码不可点击
            mRegisterSendTv.setClickable(false);
            mRegisterSendTv.setBackgroundResource(R.drawable.bg_round_verify_code);
        }
    }

    @Override
    public void setSendCodeTime(Long time) {
        setVerifyTextViewState(false);
        mRegisterSendTv.setText(Html.fromHtml("<font color='#ff0000'>" + time + "</font><font color = '#694a2c'>秒后重发</font>"));
    }

    @Override
    public void registerSuccess() {
        setToLoginView();
    }

    @Override
    public void showMsgDialog(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
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

    //检验验证码成功，进入输入姓名身份证界面
    @Override
    public void verifyMsgCodeSuccess() {
        setCheckIdCardView(false);
    }

    @Override
    public void checkIdCardSuccess() {
        setPasswordView();
    }

    //登录成功
    @Override
    public void loginSuccess() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.success = true;
        EventBus.getDefault().post(accountMessage);
        this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 如果按了返回键
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            operationKeyBack();
        }
        return false;
    }

    //操作返回键
    private void operationKeyBack() {
        switch (mCurrentUI) {
            case TO_LOGIN_VIEW:
            case SEND_MSG_CODE_VIEW:
                finish();
                break;
            case CHECK_ID_CARD_VIEW:
                setSendMsgCodeView(true);
                break;
            case SET_PSW_VIEW:
                setCheckIdCardView(true);
                break;
        }
    }

    //===========================set UI===============================
    //send msg code UI
    private void setSendMsgCodeView(boolean isBack) {
        mCommonTitleBar.setTitle(R.string.register);
        mRegisterNextBtn.setText(R.string.forget_pwd_next);
        mRegisterVerifyCodeLl.setVisibility(View.VISIBLE);
        mRegisterIdCardInfoCodeLl.setVisibility(View.GONE);
        mRegisterPswInfoCodeLl.setVisibility(View.GONE);
        mRegisterToLoginCodeLl.setVisibility(View.GONE);
        mCurrentUI = SEND_MSG_CODE_VIEW;
        if (isBack) {
            //清空读者身份信息
            mRegisterReaderNameEt.setText("");
            mRegisterIdCardEt.setText("");
        }
        mRegisterNextBtn.setBackgroundResource(isBack ? R.drawable.btn_login : R.drawable.phone_manage_button_bg);
        mRegisterNextBtn.setClickable(isBack);
    }

    //check idCard UI
    private void setCheckIdCardView(boolean isBack) {
        mCommonTitleBar.setTitle(R.string.verify_real_name);
        mRegisterNextBtn.setText(R.string.forget_pwd_next);
        mRegisterVerifyCodeLl.setVisibility(View.GONE);
        mRegisterIdCardInfoCodeLl.setVisibility(View.VISIBLE);
        mRegisterPswInfoCodeLl.setVisibility(View.GONE);
        mRegisterToLoginCodeLl.setVisibility(View.GONE);
        mCurrentUI = CHECK_ID_CARD_VIEW;
        if (isBack) {
            //清空密码
            mRegisterNewPswEt.setText("");
            mRegisterReNewPswEt.setText("");
        }
        mRegisterNextBtn.setBackgroundResource(isBack ? R.drawable.btn_login : R.drawable.phone_manage_button_bg);
        mRegisterNextBtn.setClickable(isBack);
    }

    //set password
    private void setPasswordView() {
        mCommonTitleBar.setTitle(R.string.set_psw);
        mRegisterNextBtn.setText(R.string.forget_pwd_next);
        mRegisterVerifyCodeLl.setVisibility(View.GONE);
        mRegisterIdCardInfoCodeLl.setVisibility(View.GONE);
        mRegisterPswInfoCodeLl.setVisibility(View.VISIBLE);
        mRegisterToLoginCodeLl.setVisibility(View.GONE);
        mCurrentUI = SET_PSW_VIEW;
        mRegisterNextBtn.setBackgroundResource(R.drawable.phone_manage_button_bg);
        mRegisterNextBtn.setClickable(false);
    }

    //to login UI
    private void setToLoginView() {
        mCommonTitleBar.setTitle(R.string.register);
        mRegisterNextBtn.setText(R.string.start_login_right_now);
        mRegisterVerifyCodeLl.setVisibility(View.GONE);
        mRegisterIdCardInfoCodeLl.setVisibility(View.GONE);
        mRegisterPswInfoCodeLl.setVisibility(View.GONE);
        mRegisterToLoginCodeLl.setVisibility(View.VISIBLE);
        mCurrentUI = TO_LOGIN_VIEW;
        mRegisterNextBtn.setBackgroundResource(R.drawable.btn_login);
        mRegisterNextBtn.setClickable(true);
        mCommonTitleBar.setLeftTxtBtnVisibility(View.GONE);
    }
}
