package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.OnClick;

/**
 * 读者押金管理界面
 * Created by Administrator on 2018/12/17.
 */

public class DepositManagerActivity extends BaseActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, DepositManagerActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.titlebar_left_btn, R.id.pay_deposit_btn, R.id.refund_deposit_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.pay_deposit_btn:
                if (DataRepository.getInstance().getLibraryInfo().mChargeDepositPermission) {
                    ReaderLoginActivity.startActivity(this, 4);
                } else {
                    showMessageDialog(R.string.no_permission);
                }
                break;
            case R.id.refund_deposit_btn:
                if (DataRepository.getInstance().getLibraryInfo().mRefundDepositPermission) {
                    ReaderLoginActivity.startActivity(this, 2);
                } else {
                    showMessageDialog(R.string.no_permission);
                }
                break;
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_deposit_manager;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("押金管理");
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {

    }

    private void showMessageDialog(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
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
}
