package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.ui.contract.ChargeLibDepositContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.ChargeLibDepositPresenter;
import com.tzpt.cloundlibrary.manager.utils.CashierInputFilter;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 读者交馆押金
 * Created by Administrator on 2018/12/18.
 */
public class ChargeLibDepositActivity extends BaseActivity implements ChargeLibDepositContract.View {
    private static final String READER_ID = "reader_id";

    public static void startActivity(Context context, String readerId) {
        Intent intent = new Intent(context, ChargeLibDepositActivity.class);
        intent.putExtra(READER_ID, readerId);
        context.startActivity(intent);
    }

    @BindView(R.id.reader_name_number_tv)
    TextView mReaderNameNumberTv;
    @BindView(R.id.borrowable_sum_tv)
    TextView mBorrowableSumTv;
    @BindView(R.id.usable_deposit_tv)
    TextView mUsableDepositTv;
    @BindView(R.id.pay_cost_money_et)
    EditText mPayCostMoneyEt;
    @BindView(R.id.pay_cost_pwd_et)
    EditText mPayCostPwdEt;
    @BindView(R.id.pay_cost_confirm_btn)
    Button mPayCostConfirmBtn;

    private ChargeLibDepositPresenter mPresenter;
    private String mReaderId;

    @OnClick({R.id.titlebar_left_btn, R.id.pay_cost_confirm_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.pay_cost_confirm_btn:
                mPresenter.chargeLibDeposit(mReaderId, MoneyUtils.stringToDouble(mPayCostMoneyEt.getText().toString()));
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_charge_lib_deposit;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("交馆押金");
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
    }

    @Override
    public void initDatas() {
        mPresenter = new ChargeLibDepositPresenter();
        mPresenter.attachView(this);

        mReaderId = getIntent().getStringExtra(READER_ID);
        mPresenter.getReaderInfo(mReaderId);
    }

    @Override
    public void configViews() {
        InputFilter[] filters = {new CashierInputFilter(false)};
        mPayCostMoneyEt.setFilters(filters);
        mPayCostMoneyEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setConfirmBtnStatus();
            }
        });

        mPayCostPwdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setConfirmBtnStatus();
            }
        });
    }

    private void setConfirmBtnStatus() {
        if (!TextUtils.isEmpty(mPayCostMoneyEt.getText().toString())
                && !TextUtils.isEmpty(mPayCostPwdEt.getText().toString())
                && mPayCostPwdEt.getText().toString().length() >= 6) {
            mPayCostConfirmBtn.setEnabled(true);
            mPayCostConfirmBtn.setClickable(true);
        } else {
            mPayCostConfirmBtn.setEnabled(false);
            mPayCostConfirmBtn.setClickable(false);
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
    public void setNoLoginPermission(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                LoginActivity.startActivity(ChargeLibDepositActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void showDialogGetReaderInfoFailed(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.hasNoCancel(false);
        dialog.setCancelable(false);
        dialog.setButtonTextConfirmOrYes2(true);
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void setReaderNameNumber(String info) {
        mReaderNameNumberTv.setText(info);
    }

    @Override
    public void setBorrowableSum(String info) {
        mBorrowableSumTv.setText(info);
    }

    @Override
    public void setDepositOrPenalty(String info) {
        mUsableDepositTv.setText(info);
    }

    @Override
    public void setChargeDepositSuccess() {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, "交押金成功！");
        dialog.hasNoCancel(false);
        dialog.setCancelable(false);
        dialog.setButtonTextConfirmOrYes2(true);
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mPayCostPwdEt.setText("");
                mPayCostMoneyEt.setText("");
                mPresenter.getReaderInfo(mReaderId);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
