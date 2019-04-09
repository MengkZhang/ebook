package com.tzpt.cloudlibrary.ui.account.setting;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 修改密码
 */
public class ChangePasswordActivity extends BaseActivity implements
        ChangePasswordContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.old_password)
    EditText mOldPassword;
    @BindView(R.id.clear_old_password_box)
    ImageView mClearOldPasswordBox;
    @BindView(R.id.new_password)
    EditText mNewPassword;
    @BindView(R.id.clear_new_password_box1)
    ImageView mClearNewPasswordBox1;
    @BindView(R.id.repeat_password)
    EditText mRepeatPassword;
    @BindView(R.id.clear_new_password_box2)
    ImageView mClearNewPasswordBox2;
    @BindView(R.id.progress_layout)
    LoadingProgressView mProgressLayout;

    private ChangePasswordPresenter mPresenter;


    @OnClick({R.id.titlebar_left_btn, R.id.confirm_btn, R.id.clear_old_password_box,
            R.id.clear_new_password_box1, R.id.clear_new_password_box2})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.confirm_btn:
                KeyboardUtils.hideSoftInput(this);
                changePsw();
                break;
            case R.id.clear_old_password_box:
                mOldPassword.setText("");
                break;
            case R.id.clear_new_password_box1:
                mNewPassword.setText("");
                break;
            case R.id.clear_new_password_box2:
                mRepeatPassword.setText("");
                break;
//            case R.id.changpassword_parent_layout:
//                KeyboardUtils.hideSoftInput(this);
//                break;
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_change_password;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("修改密码");
    }

    @Override
    public void initDatas() {
        mPresenter = new ChangePasswordPresenter();
        mPresenter.attachView(this);

        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        mRepeatPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    KeyboardUtils.hideSoftInput(mRepeatPassword);
                    changePsw();
                    return true;
                }
                return false;
            }
        });
        mOldPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mOldPassword.getText().length() > 0) {
                    mClearOldPasswordBox.setVisibility(View.VISIBLE);
                } else {
                    mClearOldPasswordBox.setVisibility(View.GONE);
                }
            }
        });
        mOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mClearOldPasswordBox.setVisibility((s.length() == 0) ? View.INVISIBLE : View.VISIBLE);
            }
        });
        mNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mNewPassword.getText().length() > 0) {
                    mClearNewPasswordBox1.setVisibility(View.VISIBLE);
                } else {
                    mClearNewPasswordBox1.setVisibility(View.GONE);
                }
            }
        });
        mNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mClearNewPasswordBox1.setVisibility((s.length() == 0) ? View.INVISIBLE : View.VISIBLE);
            }
        });
        mRepeatPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mRepeatPassword.getText().length() > 0) {
                    mClearNewPasswordBox2.setVisibility(View.VISIBLE);
                } else {
                    mClearNewPasswordBox2.setVisibility(View.GONE);
                }
            }
        });
        mRepeatPassword.addTextChangedListener(new TextWatcher() {
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

    private void changePsw() {
        String oldPsw = mOldPassword.getText().toString().trim();
        String newPsw = mNewPassword.getText().toString().trim();
        String repeatPsw = mRepeatPassword.getText().toString().trim();
        mPresenter.changePsw(oldPsw, newPsw, repeatPsw);
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
    public void changePasswordSuccess() {
        finish();
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
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
