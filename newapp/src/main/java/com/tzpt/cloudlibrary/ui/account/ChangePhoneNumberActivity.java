package com.tzpt.cloudlibrary.ui.account;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.CustomTipsDialog;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 更改手机号 -0 更改手机号码 1 绑定手机号码
 */
public class ChangePhoneNumberActivity extends BaseActivity implements
        ChangePhoneNumberContract.View {
    private static final String FROM_TYPE = "from_type";

    public static void startActivity(Context context, int fromType) {
        Intent intent = new Intent(context, ChangePhoneNumberActivity.class);
        intent.putExtra(FROM_TYPE, fromType);
        context.startActivity(intent);
    }

    @BindView(R.id.change_old_phone_tv)
    TextView mChangeOldPhoneTv;
    @BindView(R.id.change_old_layout)
    LinearLayout mChangeOldLayout;
    @BindView(R.id.change_new_phone_et)
    EditText mChangeNewPhoneEt;
    @BindView(R.id.change_clear_box_iv)
    ImageView mChangeClearBoxIv;
    @BindView(R.id.change_new_phone_layout)
    LinearLayout mChangeNewPhoneLayout;
    @BindView(R.id.change_binding_phone_et)
    EditText mChangeBindingPhoneEt;
    @BindView(R.id.change_clear_box_iv2)
    ImageView mChangeClearBoxIv2;
    @BindView(R.id.change_binding_phone_layout)
    LinearLayout mChangeBindingPhoneLayout;
    @BindView(R.id.change_verify_code_et)
    EditText mChangeVerifyCodeEt;
    @BindView(R.id.change_clear_box_iv3)
    ImageView mChangeClearBoxIv3;
    @BindView(R.id.change_send_code_tv)
    TextView mChangeSendCodeTv;
    @BindView(R.id.change_confirm_btn)
    Button mChangeConfirmBtn;
    @BindView(R.id.progress_layout)
    LoadingProgressView mProgressLayout;

    private ChangePhoneNumberPresenter mPresenter;
    private int mFromType;
    private String mOldPhoneNumber;


    @OnClick({R.id.titlebar_left_btn, R.id.change_send_code_tv, R.id.change_confirm_btn,
            R.id.change_clear_box_iv, R.id.change_clear_box_iv2, R.id.change_clear_box_iv3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                KeyboardUtils.hideSoftInput(this);
                finish();
                break;
            case R.id.change_send_code_tv:
                KeyboardUtils.hideSoftInput(this);
                mPresenter.sendVerifyCode(mOldPhoneNumber,
                        mChangeNewPhoneEt.getText().toString().trim(),
                        mChangeBindingPhoneEt.getText().toString().trim());
                break;
            case R.id.change_confirm_btn:
                KeyboardUtils.hideSoftInput(this);
                submitCodeAndChangePhone();
                break;
            case R.id.change_clear_box_iv:
                mChangeNewPhoneEt.setText("");
                mChangeNewPhoneEt.requestFocus();
                break;
            case R.id.change_clear_box_iv2:
                mChangeBindingPhoneEt.setText("");
                mChangeBindingPhoneEt.requestFocus();
                break;
            case R.id.change_clear_box_iv3:
                mChangeVerifyCodeEt.setText("");
                mChangeVerifyCodeEt.requestFocus();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_change_phone_number;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mPresenter = new ChangePhoneNumberPresenter();
        mPresenter.attachView(this);

        //0 更改手机号码 1 绑定手机号码
        Intent intent = getIntent();
        mFromType = intent.getIntExtra(FROM_TYPE, 0);
        mPresenter.setFromType(mFromType);
        switch (mFromType) {
            case 0:
                mCommonTitleBar.setTitle(R.string.change_phone_num);
                mChangeOldLayout.setVisibility(View.VISIBLE);
                mChangeNewPhoneLayout.setVisibility(View.VISIBLE);
                mChangeBindingPhoneLayout.setVisibility(View.GONE);
                //设置原号码
                mPresenter.getLoginInfo();
                break;
            case 1:
                mCommonTitleBar.setTitle(R.string.binding_phone_num);
                mChangeOldLayout.setVisibility(View.GONE);
                mChangeNewPhoneLayout.setVisibility(View.GONE);
                mChangeBindingPhoneLayout.setVisibility(View.VISIBLE);
                break;
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        //默认设置提交按钮不可点击
        mChangeConfirmBtn.setClickable(false);
        //默认设置发送验证码是否可点击
        setVerifyTextViewState(true);
        //更改手机号
        mChangeNewPhoneEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mChangeNewPhoneEt.getText().length() > 0) {
                    mChangeClearBoxIv.setVisibility(View.VISIBLE);
                } else {
                    mChangeClearBoxIv.setVisibility(View.INVISIBLE);
                }
            }
        });
        //绑定手机号
        mChangeBindingPhoneEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mChangeBindingPhoneEt.getText().length() > 0) {
                    mChangeClearBoxIv2.setVisibility(View.VISIBLE);
                } else {
                    mChangeClearBoxIv2.setVisibility(View.INVISIBLE);
                }
            }
        });
        //验证码
        mChangeVerifyCodeEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mChangeVerifyCodeEt.getText().length() > 0) {
                    mChangeClearBoxIv3.setVisibility(View.VISIBLE);
                } else {
                    mChangeClearBoxIv3.setVisibility(View.INVISIBLE);
                }
            }
        });
        mChangeVerifyCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //输入验证码后状态修改
                mChangeClearBoxIv3.setVisibility((s.length() == 0) ? View.INVISIBLE : View.VISIBLE);
                mChangeConfirmBtn.setBackgroundResource((s.length() > 3) ?
                        R.drawable.btn_login : R.drawable.phone_manage_button_bg);
                mChangeConfirmBtn.setClickable((s.length() > 3));
                if (s.length() > 3) {
                    mChangeVerifyCodeEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            submitCodeAndChangePhone();
                            return false;
                        }
                    });
                } else {
                    mChangeVerifyCodeEt.setOnEditorActionListener(null);
                }
            }
        });
    }

    /**
     * 默认设置发送验证码是否可点击
     *
     * @param canClick
     */
    private void setVerifyTextViewState(boolean canClick) {
        if (canClick) {
            //默认设置发送验证码可点击
            mChangeSendCodeTv.setClickable(true);
            mChangeSendCodeTv.setBackgroundResource(R.drawable.item_verifycode_selector);
        } else {
            //默认设置发送验证码不可点击
            mChangeSendCodeTv.setClickable(false);
            mChangeSendCodeTv.setBackgroundResource(R.drawable.bg_round_verify_code);
        }
    }

    /**
     * 提交验证码和修改或绑定手机号码
     */
    private void submitCodeAndChangePhone() {
        mPresenter.submitVerifyCodeAndChangePhoneNumber(mChangeNewPhoneEt.getText().toString().trim(),
                mChangeBindingPhoneEt.getText().toString().trim(),
                mChangeVerifyCodeEt.getText().toString().trim());
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
    public void showToastMsg(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }

    @Override
    public void showToastMsg(String msg) {
        ToastUtils.showSingleToast(msg);
    }

    //发送验证码处理
    @Override
    public void setSendCodeStyle(int type) {
        switch (type) {
            case 0://设置不可点击
                setVerifyTextViewState(false);
                break;
            case 1://计时完成
                mChangeSendCodeTv.setText(R.string.send_verify_code_again);
                setVerifyTextViewState(true);
                setPhoneEditAble(true);
                break;
            case 2://出现错误-网络故障等
                setVerifyTextViewState(true);
                mChangeSendCodeTv.setText(R.string.send_verify_code_again);
                ToastUtils.showSingleToast(R.string.network_fault);
                setPhoneEditAble(true);
                break;
        }
    }

    @Override
    public void setPhoneEditAble(boolean able) {
        switch (mFromType) {
            case 0:
                setEditTextAble(mChangeNewPhoneEt, able);
                mChangeClearBoxIv.setVisibility(able ? View.VISIBLE : View.GONE);
                break;
            case 1:
                setEditTextAble(mChangeBindingPhoneEt, able);
                mChangeClearBoxIv2.setVisibility(able ? View.VISIBLE : View.GONE);
                break;
        }
    }

    //计时中
    @Override
    public void setSendCodeTime(long time) {
        setVerifyTextViewState(false);
        mChangeSendCodeTv.setText(Html.fromHtml("<font color='#ff0000'>" + time + "</font><font color = '#694a2c'>秒后重发</font>"));
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


    @Override
    public void sendVerifyMessageCode(boolean state) {
        if (!state) {
            mChangeSendCodeTv.setText(R.string.send_verify_code_again);
            setVerifyTextViewState(true);
            setPhoneEditAble(true);
        }
    }

    @Override
    public void changePhoneSuccess() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.success = true;
        EventBus.getDefault().post(accountMessage);
        KeyboardUtils.hideSoftInput(this);
        finish();
    }

    @Override
    public void setOldTelNum(String tel) {
        if (!TextUtils.isEmpty(tel)) {
            this.mOldPhoneNumber = tel;
            mChangeOldPhoneTv.setText(StringUtils.formatTel(mOldPhoneNumber));
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
    public void setBindingPhoneTips(String idCard, final String phone) {
        final CustomTipsDialog dialog = new CustomTipsDialog(this, R.style.DialogTheme, "");
        dialog.setSubTips(getString(R.string.binding_id_card_number, idCard));
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomTipsDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mPresenter.sendVerifyCodeByPhoneNumber(phone);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
                finish();
            }
        });
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
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receviceLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }
}
