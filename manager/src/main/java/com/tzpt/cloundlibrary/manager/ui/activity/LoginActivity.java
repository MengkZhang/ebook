package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.ManagerApplication;
import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.base.Constant;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.bean.UpdateAppBean;
import com.tzpt.cloundlibrary.manager.event.LoginOutEvent;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.ui.contract.LoginContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.LoginPresenter;
import com.tzpt.cloundlibrary.manager.utils.AllCapTransformationMethod;
import com.tzpt.cloundlibrary.manager.utils.KeyboardUtils;
import com.tzpt.cloundlibrary.manager.widget.popupwindow.UpdateAppPPW;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 操作员登录界面
 * Created by Administrator on 2017/6/21.
 */

public class LoginActivity extends BaseActivity implements LoginContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        ManagerApplication.TOKEN = "";
        DataRepository.getInstance().clearToken();

        LoginOutEvent loginOutEvent = new LoginOutEvent();
        loginOutEvent.mIsLoginOut = true;
        EventBus.getDefault().post(loginOutEvent);
    }

    public static void startActivity(Context context, int messageFlag) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(Constant.TO_MESSAGE_VIEW, messageFlag);
        context.startActivity(intent);
    }

    @BindView(R.id.library_num_et)
    EditText mLibraryNumEt;
    @BindView(R.id.user_name_et)
    EditText mUserNameEt;
    @BindView(R.id.pwd_et)
    EditText mPwdEt;
    @BindView(R.id.login_btn)
    Button mLoginBtn;
    @BindView(R.id.login_ll)
    LinearLayout mRootView;
    @BindView(R.id.scrollview)
    ScrollView mScrollview;
    private LoginPresenter mPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        mPresenter = new LoginPresenter();
        mPresenter.attachView(this);
        mPresenter.getLoginInfo();
        mPresenter.updateAppInfo(this);

    }

    @Override
    public void configViews() {
        mPwdEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    KeyboardUtils.hideSoftInput(LoginActivity.this);
                    login();
                    return true;
                }
                return false;
            }
        });
        mLibraryNumEt.setTransformationMethod(new AllCapTransformationMethod());
    }

    @OnClick({R.id.login_btn, R.id.login_ll, R.id.forget_pwd_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                login();
                break;
            case R.id.login_ll:
                KeyboardUtils.hideSoftInput(LoginActivity.this);
                break;
            case R.id.forget_pwd_tv:
                VerifyIdentityActivity.startActivity(this);
                break;
        }

    }

    private void login() {
        String libName = mLibraryNumEt.getText().toString().trim();
        String userName = mUserNameEt.getText().toString().trim();
        String pwd = mPwdEt.getText().toString().trim();

        mPresenter.login(libName.toUpperCase(), userName, pwd);
    }

    @Override
    public void loginSuccess() {
        int msgFlag = getIntent().getIntExtra(Constant.TO_MESSAGE_VIEW, -1);
        MainActivity.startActivity(this, msgFlag, true);
        finish();
    }

    @Override
    public void loginFailed(String msg) {
        showMessageDialog(msg);
    }

    @Override
    public void loginFailed(int msgId) {
        showMessageDialog(getString(msgId));
    }

    @Override
    public void showLoginInfo(LibraryInfo info) {
        if (!TextUtils.isEmpty(info.mHallCode) && !TextUtils.isEmpty(info.mOperaterName)) {
            mLibraryNumEt.setText(info.mHallCode);
            mUserNameEt.setText(info.mOperaterName);
            mPwdEt.requestFocus();
        } else {
            mLibraryNumEt.requestFocus();
        }
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
    public void showUpdateAppInfo(UpdateAppBean updateAppBean) {
        if (null != mScrollview && null != updateAppBean) {
            if (updateAppBean.forceUpdate == 1) {
                setLoginEditAble();
            }
            UpdateAppPPW updateAppPPW = new UpdateAppPPW(LoginActivity.this, mScrollview, updateAppBean);
            updateAppPPW.showAtLocation(mScrollview, Gravity.CENTER, 0, 0);
            KeyboardUtils.hideSoftInput(this);
        }
    }

    /**
     * 操作员第一次登录，进入设置密码界面
     */
    @Override
    public void setFirstLoginOperatorPswTip() {
        SetOperatorFirstPswActivity.startActivity(this);
        mPwdEt.setText("");
    }

    private void setLoginEditAble() {
        mLibraryNumEt.setEnabled(false);
        mUserNameEt.setEnabled(false);
        mPwdEt.setEnabled(false);
        mLoginBtn.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
