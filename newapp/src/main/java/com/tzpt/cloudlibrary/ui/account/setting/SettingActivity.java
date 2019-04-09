package com.tzpt.cloudlibrary.ui.account.setting;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.AppVersionBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.LoginActivity;
import com.tzpt.cloudlibrary.ui.main.AppUpdateDialogFragment;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.CustomUserGridMenu;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置界面
 * Created by Administrator on 2017/12/14.
 */

public class SettingActivity extends BaseActivity implements SettingContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.check_progress_layout)
    LoadingProgressView mLoadingView;
    @BindView(R.id.login_out_btn)
    CustomUserGridMenu mLoginOutMenuItem;

    private AppUpdateDialogFragment mAppUpdateDialogFragment;

    private SettingPresenter mPresenter;

    @OnClick({R.id.titlebar_left_btn, R.id.modify_pwd_btn, R.id.login_out_btn, R.id.check_version_btn,
            R.id.about_us_btn, R.id.download_set_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.modify_pwd_btn:
                if (mPresenter.isLogin()) {
                    ChangePasswordActivity.startActivity(this);
                } else {
                    LoginActivity.startActivityForResult(this, 1000);
                }
                break;
            case R.id.login_out_btn:
                final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, "");
                dialog.setTitle(getString(R.string.exit_from_current_account));
                dialog.setText(getString(R.string.exit_login_prompt));
                dialog.setTitleTextStyle(R.style.CLDialog_title);
                dialog.setContentTextStyle(R.style.CLDialog_content);
                dialog.setCancelable(false);
                dialog.hasNoCancel(true);
                dialog.show();
                dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
                    @Override
                    public void onClickOk() {
                        dialog.dismiss();
                        AccountMessage accountMessage = new AccountMessage();
                        accountMessage.mIsLoginOut = true;
                        EventBus.getDefault().post(accountMessage);
                        finish();
                    }

                    @Override
                    public void onClickCancel() {
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.check_version_btn:
                mPresenter.checkVersionInfo();
                break;
            case R.id.about_us_btn:
                AboutUsActivity.startActivity(this);
                break;
            case R.id.download_set_btn:
                DownloadSettingActivity.startActivity(this);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("设置");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mPresenter = new SettingPresenter();
        mPresenter.attachView(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        if (mPresenter.isLogin()) {
            mLoginOutMenuItem.setVisibility(View.VISIBLE);
        } else {
            mLoginOutMenuItem.setVisibility(View.GONE);
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000
                && resultCode == RESULT_OK) {
            ChangePasswordActivity.startActivity(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receviceLoginOut(AccountMessage loginMessage) {
        if (loginMessage.mIsLoginOut) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginSuccess(AccountMessage loginMessage) {
        if (loginMessage.success) {
            mLoginOutMenuItem.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showLoading() {
        mLoadingView.showProgressLayout();
    }

    @Override
    public void dismissLoading() {
        mLoadingView.hideProgressLayout();
    }

    @Override
    public void showDialogTip(int resId) {
        final CustomDialog customDialog = new CustomDialog(this, R.style.DialogTheme);
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(false);
        customDialog.setButtonTextConfirmOrYes(true);
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
            }
        });
        customDialog.setText(getString(resId));
        customDialog.show();
    }

    @Override
    public void setAppVersionInfo(AppVersionBean bean) {
        if (null == mAppUpdateDialogFragment) {
            mAppUpdateDialogFragment = new AppUpdateDialogFragment();
        }
        if (mAppUpdateDialogFragment.isAdded()) {
            return;
        }
        mAppUpdateDialogFragment.show(getFragmentManager(), "AppUpdateDialogFragment");
        mAppUpdateDialogFragment.setAppUpdateBean(bean);
    }

}
