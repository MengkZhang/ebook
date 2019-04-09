package com.tzpt.cloudlibrary.ui.account.setting;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.widget.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/7/26.
 */

public class DownloadSettingActivity extends BaseActivity implements DownloadSettingContract.View {
    private DownloadSettingPresenter mPresenter;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, DownloadSettingActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.download_setting_net_cb)
    CheckBox mDownloadNetCb;

    @OnClick({R.id.titlebar_left_btn, R.id.download_setting_net_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.download_setting_net_ll:
                mPresenter.changeMobileNetAble();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_download_setting;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("下载设置");
    }

    @Override
    public void initDatas() {
        mPresenter = new DownloadSettingPresenter();
        mPresenter.attachView(this);
        mPresenter.checkMobileNetAble();
    }

    @Override
    public void configViews() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void setCheckBoxStatus(boolean isMobileNetRequire) {
        mDownloadNetCb.setChecked(isMobileNetRequire);
    }

    @Override
    public void showDialog(int msgId, int okId, int cancelId) {
        final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.setBtnOKAndBtnCancelTxt(getString(okId), getString(cancelId));
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mPresenter.setMobileNetAble(true);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }
}
