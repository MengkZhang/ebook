package com.tzpt.cloudlibrary.ui.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.utils.AllCapTransformationMethod;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity implements
        LoginContract.View {

    private static final String ACTION_ID = "action_id";

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, int actionId) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(ACTION_ID, actionId);
        context.startActivity(intent);
    }

    public static void startActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.login_account)
    EditText mLoginAccount;
    @BindView(R.id.login_password)
    EditText mLoginPassword;
    @BindView(R.id.login_delete_box1)
    ImageView mClearBox1;
    @BindView(R.id.login_delete_box2)
    ImageView mClearBox2;
    @BindView(R.id.login_progress_layout)
    LoadingProgressView mProgressLayout;
    @BindView(R.id.login_register_btn)
    Button mLoginRegisterBtn;

    private LoginPresenter mPresenter;

    private int mActionId;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_txt_btn, R.id.login_forget_password, R.id.login_btn,
            R.id.login_delete_box1, R.id.login_delete_box2, R.id.login_register_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                KeyboardUtils.hideSoftInput(this);
                finish();
                break;
            case R.id.titlebar_right_txt_btn:
                KeyboardUtils.hideSoftInput(this);
                RegisterActivity.startActivity(this);
                break;
            case R.id.login_forget_password:
                ForgotPasswordActivity.startActivity(this);
                break;
            case R.id.login_btn:
                KeyboardUtils.hideSoftInput(this);
                mPresenter.login(mLoginAccount.getText().toString().trim(), mLoginPassword.getText().toString().trim());
                break;
            case R.id.login_delete_box1:
                mLoginAccount.setText("");
                mLoginAccount.requestFocus();
                break;
            case R.id.login_delete_box2:
                mLoginPassword.setText("");
                mLoginPassword.requestFocus();
                break;
            case R.id.login_register_btn:
                VisitorRegistrationActivity.startActivity(this, mActionId);
                finish();
                break;
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setRightBtnText(R.string.register);
        mCommonTitleBar.setTitle("登录");
        //setSupportSlideBack(false);
    }

    @Override
    public void initDatas() {
        mPresenter = new LoginPresenter();
        mPresenter.attachView(this);
        mPresenter.getLoginCacheInfo();
        //注册事件
        EventBus.getDefault().register(this);

        //读友会未注册读者登记
        mActionId = getIntent().getIntExtra(ACTION_ID, -1);
        if (mActionId != -1) {
            mLoginRegisterBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void configViews() {
        mLoginAccount.setTransformationMethod(new AllCapTransformationMethod());
        mLoginPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    KeyboardUtils.hideSoftInput(mLoginPassword);
                    mPresenter.login(mLoginAccount.getText().toString().trim(), mLoginPassword.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        mLoginAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mLoginAccount.getText().length() > 0) {
                    mClearBox1.setVisibility(View.VISIBLE);
                } else {
                    mClearBox1.setVisibility(View.INVISIBLE);
                }
            }
        });
        mLoginAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mClearBox1.setVisibility((s.length() == 0) ? View.INVISIBLE : View.VISIBLE);
            }
        });
        mLoginPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mLoginPassword.getText().length() > 0) {
                    mClearBox2.setVisibility(View.VISIBLE);
                } else {
                    mClearBox2.setVisibility(View.INVISIBLE);
                }
            }
        });
        mLoginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mClearBox2.setVisibility((s.length() == 0) ? View.INVISIBLE : View.VISIBLE);
            }
        });
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
    public void showToastMessage(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }

    @Override
    public void loginSuccess() {
        setResult(RESULT_OK);
        //登录成功
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.success = true;
        EventBus.getDefault().post(accountMessage);
        this.finish();
    }

    @Override
    public void setLoginCacheInfo(String account) {
        mLoginAccount.setText(account);
        mClearBox1.setVisibility(View.VISIBLE);
        mLoginPassword.requestFocus();
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
        //取消事件订阅
        EventBus.getDefault().unregister(this);
    }

    //忘记密码，执行自动登录
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void autoLogin(AutoLoginMessage msg) {
        if (null != msg) {
            mLoginAccount.setText(msg.idCard);
            mLoginPassword.setText(msg.psw);
            KeyboardUtils.hideSoftInput(this);
            mPresenter.login(mLoginAccount.getText().toString().trim(), mLoginPassword.getText().toString().trim());
        }
    }

    //登录成功，结束登录界面
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receviceLoginSuccess(AccountMessage loginMessage) {
        //如果登录成功，则访问用户数据
        if (null != mPresenter && loginMessage.success) {
            finish();
        }
    }
}

