package com.tzpt.cloudlibrary.ui.account.deposit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.DepositBalanceBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.utils.CashierInputFilter;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.utils.MoneyUtils;
import com.tzpt.cloudlibrary.utils.Utils;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 退押金
 */
public class RefundDepositActivity extends BaseActivity implements
        RefundDepositContract.View {

    private static final int REQUEST_CODE = 1000;

    public static void startActivityForResult(Activity context, int requestCode) {
        Intent intent = new Intent(context, RefundDepositActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.online_deposit_tv)
    TextView mOnlineDepositTv;
    @BindView(R.id.offline_deposit_tv)
    TextView mOfflineDepositTv;
    @BindView(R.id.refund_deposit_money_et)
    EditText mRefundDepositMoneyEt;
    @BindView(R.id.progress_layout)
    LoadingProgressView mProgressLayout;
    @BindView(R.id.refund_deposit_btn)
    Button mRefundDepositBtn;
    @BindView(R.id.offline_deposit_tip_tv)
    TextView mOfflineDepositTipTv;

    private RefundDepositPresenter mPresenter;

    @OnClick({R.id.titlebar_left_btn, R.id.refund_deposit_btn, R.id.offline_deposit_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.refund_deposit_btn:
                KeyboardUtils.hideSoftInput(this);
                String deposit = mRefundDepositMoneyEt.getText().toString().trim();
                mRefundDepositMoneyEt.setText(MoneyUtils.formatMoney(deposit));
                mPresenter.submitRefundDeposit(mRefundDepositMoneyEt.getText().toString().trim(), Utils.getIP(this));
                break;
            case R.id.offline_deposit_tv:
                mPresenter.getOfflineDepositList();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_refund_deposit;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("退押金");
    }

    @Override
    public void initDatas() {
        mPresenter = new RefundDepositPresenter();
        mPresenter.attachView(this);
        mPresenter.requestWithdrawInfo();

        setUserDepositInfo();
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        //过滤只能输入小数点后两位金额
        InputFilter[] filters = {new CashierInputFilter(false)};
        mRefundDepositMoneyEt.setFilters(filters);
    }

    //设置用户押金信息
    private void setUserDepositInfo() {
        mOnlineDepositTv.setText(getString(R.string.online_available_balance, "0.00"));
        mOfflineDepositTv.setText(getString(R.string.offline_available_balance, "0.00"));
    }

    @Override
    public void showLoadingDialog() {
        showDialog("加载中...");
    }

    @Override
    public void dismissLoadingDialog() {
        dismissDialog();
    }

    @Override
    public void showProgressLoading() {
        mProgressLayout.showProgressLayout();
    }

    @Override
    public void dismissProgressLoading() {
        mProgressLayout.hideProgressLayout();
    }

    /**
     * 设置可退押金信息
     *
     * @param online  在线可退押金
     * @param offline 线下可退押金
     */
    @Override
    public void setAvailableBalance(String online, String offline) {
        double onLineDeposit = MoneyUtils.stringToDouble(online);
        if (onLineDeposit > 0) {
            mRefundDepositBtn.setClickable(true);
            mRefundDepositBtn.setBackgroundResource(R.drawable.btn_login);
        } else {
            mRefundDepositBtn.setClickable(false);
            mRefundDepositBtn.setBackgroundResource(R.drawable.bg_round_c2c2c2);
        }
        mOnlineDepositTv.setText(getString(R.string.online_available_balance, online));
        mOfflineDepositTv.setText(getString(R.string.offline_available_balance, offline));

        try {
            if (Double.valueOf(offline) > 0) {
                mOfflineDepositTipTv.setVisibility(View.VISIBLE);
                mOfflineDepositTv.setVisibility(View.VISIBLE);
                Drawable flup = getResources().getDrawable(R.mipmap.ic_orange_arrow);
                flup.setBounds(0, 0, flup.getMinimumWidth(), flup.getMinimumHeight());
                mOfflineDepositTv.setCompoundDrawables(null, null, flup, null);
                mOfflineDepositTv.setClickable(true);
            } else {
                mOfflineDepositTipTv.setVisibility(View.GONE);
                mOfflineDepositTv.setVisibility(View.GONE);
                mOfflineDepositTv.setClickable(false);
            }
        } catch (Exception e) {
            mOfflineDepositTv.setClickable(false);
            e.printStackTrace();
        }
    }

    @Override
    public void showDialogTip(int resId, final boolean finish) {
        showDialogTip(getString(resId), finish);
    }

    @Override
    public void showDialogTip(String str, final boolean finish) {
        final CustomDialog customDialog = new CustomDialog(this, R.style.DialogTheme);
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(false);
        customDialog.setButtonTextConfirmOrYes(true);
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
                if (finish) {
                    finish();
                }
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
            }
        });
        customDialog.setText(str);
        customDialog.show();
    }

    @Override
    public void withdrawDepositLimitTip(String deposit, boolean isLimit) {
        if (isLimit) {
            showDialogTip(getString(R.string.withdraw_deposit_limit_money_once, deposit), false);
        } else {
            showDialogTip(getString(R.string.withdraw_deposit_limit_money, deposit), false);
        }
    }

    /**
     * 退款成功
     */
    @Override
    public void showSuccessDialog() {
        final CustomDialog customDialog = new CustomDialog(this, R.style.DialogTheme);
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(false);
        customDialog.setButtonTextConfirmOrYes(true);
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
            }
        });
        customDialog.setText(getString(R.string.withdraw_deposit_success));
        customDialog.show();
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
    }

    /**
     * 已扣除未处理的罚金XX.00元
     *
     * @param penalty 罚金金额
     */
    @Override
    public void showProcessedPenaltyDialog(String penalty) {
        final CustomDialog customDialog = new CustomDialog(this, R.style.DialogTheme);
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(false);
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
        customDialog.setText(getString(R.string.had_pay_penalty, penalty));
        customDialog.show();
    }

    /**
     * 存在逾期罚金未处理
     */
    @Override
    public void showHandlePenaltyDialog() {
        final CustomDialog customDialog = new CustomDialog(this, R.style.DialogTheme);
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(true);
        customDialog.setOkText("前往处理");
        customDialog.setCancelText("返回");
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
                PayPenaltyActivity.startActivityForResult(RefundDepositActivity.this, REQUEST_CODE);
                finish();
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
                finish();
            }
        });
        customDialog.setText(getString(R.string.had_penalty_not_handle));
        customDialog.show();
    }

    @Override
    public void setOfflineDeposit(ArrayList<DepositBalanceBean> list) {
        if (list != null && list.size() > 0) {
            DepositBalanceActivity.startActivity(this, list, 1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }

    /**
     * 重新请求用户押金信息（用户交钱成功以后）
     *
     * @param requestCode 请求code
     * @param resultCode  返回值code
     * @param data        返回数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != mPresenter && requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            mPresenter.requestWithdrawInfo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消事件订阅
        EventBus.getDefault().unregister(this);
        mPresenter.detachView();
        mPresenter = null;
    }

}
