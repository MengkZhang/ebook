package com.tzpt.cloundlibrary.manager.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.ui.contract.RefundPlatformDepositContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.RefundPlatformDepositPresenter;
import com.tzpt.cloundlibrary.manager.utils.CashierInputFilter;
import com.tzpt.cloundlibrary.manager.utils.Utils;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 提现
 */
public class RefundPlatformDepositActivity extends BaseActivity implements
        RefundPlatformDepositContract.View {

    public static void startActivityForResult(Activity context, int requestCode) {
        Intent intent = new Intent(context, RefundPlatformDepositActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.refund_deposit_money_et)
    EditText mRefundDepositMoneyEt;
    @BindView(R.id.refund_deposit_pwd_et)
    EditText mPwdEt;
    @BindView(R.id.pay_can_use_deposit_tv)
    TextView mPayCanUseDepositTv;
    @BindView(R.id.refund_deposit_change_account_tv)
    TextView mRefundDepositChangeAccountTv;

    private RefundPlatformDepositPresenter mPresenter;

    private boolean mHaveRefundAccount;

    @OnClick({R.id.titlebar_left_btn, R.id.refund_deposit_btn, R.id.refund_deposit_change_account_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.refund_deposit_btn:
                mPresenter.submitRefundDeposit(mPwdEt.getText().toString().trim(),
                        mRefundDepositMoneyEt.getText().toString().trim(),
                        Utils.getIP(this));
                break;
            case R.id.refund_deposit_change_account_tv:
                RefundAccountActivity.startActivity(this, mHaveRefundAccount);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_refund_platform_deposit;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("提现");
    }

    @Override
    public void initDatas() {
        mPresenter = new RefundPlatformDepositPresenter();
        mPresenter.attachView(this);
        mPresenter.requestRefundInfo();
        mPayCanUseDepositTv.setText("可退金额 0.00");
    }

    @Override
    public void configViews() {
        //过滤只能输入小数点后两位金额
        InputFilter[] filters = {new CashierInputFilter(false)};
        mRefundDepositMoneyEt.setFilters(filters);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.detachView();
        }
    }

    @Override
    public void showProgressLoading() {
        showDialog("");
    }

    @Override
    public void dismissProgressLoading() {
        dismissDialog();
    }

    @Override
    public void showNoPermissionDialog(int kickOut) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(kickOut));
        dialog.hasNoCancel(false);
        dialog.setCancelable(false);
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                LoginActivity.startActivity(RefundPlatformDepositActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void setRefundableDeposit(String deposit) {
        mPayCanUseDepositTv.setText(getString(R.string.refundable_deposit, deposit));
    }

    @Override
    public void showDialogMsg(int msgId, boolean isFinish) {
        String msg = getString(msgId);
        showDialogMsg(msg, isFinish);
    }

    @Override
    public void showDialogMsg(String msg, final boolean isFinish) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                if (isFinish) {
                    finish();
                }
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void noAliPayAccount(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                RefundAccountActivity.startActivity(RefundPlatformDepositActivity.this, mHaveRefundAccount);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void withdrawDepositLimitTip(String limitMoney) {
        String msg = getString(R.string.withdraw_deposit_limit, limitMoney);
        showDialogMsg(msg, false);
    }

    @Override
    public void showSuccessDialog(int msgId) {
        String msg = getString(msgId);
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void setRefundAccountTitle(boolean haveRefundAccount) {
        mHaveRefundAccount = haveRefundAccount;
        if (haveRefundAccount) {
            mRefundDepositChangeAccountTv.setText(R.string.change_refund_account);
        } else {
            mRefundDepositChangeAccountTv.setText(R.string.set_refund_account);
        }

    }
}
