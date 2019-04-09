package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Intent;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.ui.contract.PayCostContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.PayCostPresenter;
import com.tzpt.cloundlibrary.manager.utils.CashierInputFilter;
import com.tzpt.cloundlibrary.manager.utils.KeyboardUtils;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 支付处理罚金界面
 * Created by Administrator on 2017/7/8.
 */
public class PayCostActivity extends BaseActivity implements PayCostContract.View {
    private static final String PAY_FLAG = "pay_flag";
    private static final String PENALTY_MONEY = "penalty_money";

    @BindView(R.id.pat_cost_info_tv)
    TextView mPatCostInfoTv;
    @BindView(R.id.pay_cost_money_et)
    EditText mPayCostMoneyEt;
    @BindView(R.id.pay_cost_pwd_et)
    EditText mPayCostPwdEt;
    @BindView(R.id.pay_cost_confirm_btn)
    Button mPayCostConfirmBtn;
    @BindView(R.id.apply_free_charge_cb)
    CheckBox mApplyFreeChargeCb;
    @BindView(R.id.apply_free_charge_reason_et)
    EditText mApplyFreeChargeReasonEt;

    private double mPenaltyMoney;
    private int mPayFlag;

    private PayCostPresenter mPresenter;

    @OnClick(R.id.pay_cost_confirm_btn)
    public void onViewClicked(View v) {
        if (mApplyFreeChargeCb.isChecked()) {
            String pwd = mPayCostPwdEt.getText().toString().trim();
            mPresenter.applyFreeCharge(mApplyFreeChargeReasonEt.getText().toString().trim(), pwd);
        } else {
            double payMoney = MoneyUtils.stringToDouble(mPayCostMoneyEt.getText().toString().trim());
            String pwd = mPayCostPwdEt.getText().toString().trim();
            mPresenter.payCost(mPayFlag, payMoney, pwd);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_pay_cost;
    }

    @Override
    public void initToolBar() {
    }

    @Override
    public void initDatas() {
        mPenaltyMoney = getIntent().getDoubleExtra(PENALTY_MONEY, 0);
        mPayFlag = getIntent().getIntExtra(PAY_FLAG, 0);

        mPresenter = new PayCostPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {
        InputFilter[] filters = {new CashierInputFilter(false)};
        mPayCostMoneyEt.setFilters(filters);
        switch (mPayFlag) {
            case 0:
                //非押金模式(非押金模式-收罚金)
                String noDepositTips = "收罚金" + MoneyUtils.formatMoney(mPenaltyMoney) + "元,请收罚金！";
                mPatCostInfoTv.setText(noDepositTips);
                mPayCostMoneyEt.setText(MoneyUtils.formatMoney(mPenaltyMoney));
                mPayCostMoneyEt.setEnabled(false);
                mPayCostMoneyEt.setTextColor(getResources().getColor(R.color.color_999999));
                mPayCostMoneyEt.setBackgroundResource(R.drawable.bg_round_stroke_f4f4f4);
                int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_12dp);
                mPayCostMoneyEt.setPadding(spacingInPixels, 0, spacingInPixels, 0);

                mPayCostPwdEt.setHint("操作员密码");
                mPayCostPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mPayCostConfirmBtn.setText("确认收现金");

                if (mPresenter.checkPenaltyFreePermission()) {
                    mApplyFreeChargeCb.setVisibility(View.VISIBLE);
                }
                break;
            case 1:
                //押金模式(押金模式-收罚金)
                String depositTips = "欠罚金" + MoneyUtils.formatMoney(mPenaltyMoney) + "元未扣,请收押金！";
                mPatCostInfoTv.setText(depositTips);
                mPayCostMoneyEt.setText(MoneyUtils.formatMoney(mPenaltyMoney));
                mPayCostMoneyEt.setEnabled(false);
                mPayCostMoneyEt.setTextColor(getResources().getColor(R.color.color_999999));
                mPayCostMoneyEt.setBackgroundResource(R.drawable.bg_round_stroke_f4f4f4);
                spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_12dp);
                mPayCostMoneyEt.setPadding(spacingInPixels, 0, spacingInPixels, 0);

                mPayCostMoneyEt.setSelection(MoneyUtils.formatMoney(mPenaltyMoney).length());
                mPayCostMoneyEt.requestFocus();
                mPayCostPwdEt.setHint("操作员密码");
                mPayCostPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mPayCostConfirmBtn.setText("确认收现金");

                if (mPresenter.checkPenaltyFreePermission()) {
                    mApplyFreeChargeCb.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                //押金模式(押金模式-交押金)
                mPatCostInfoTv.setText("押金不足，请收押金！");
                mPayCostMoneyEt.setText("");
                mPayCostMoneyEt.requestFocus();
                mPayCostMoneyEt.setTextColor(getResources().getColor(R.color.color_333333));
                mPayCostPwdEt.setHint("操作员密码");
                mPayCostPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mPayCostConfirmBtn.setText("确认收现金");

                mApplyFreeChargeCb.setVisibility(View.GONE);
                break;
        }
        mPayCostPwdEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    KeyboardUtils.hideSoftInput(mPayCostPwdEt);
                    //验证信息是否为空
                    double payMoney = MoneyUtils.stringToDouble(mPayCostMoneyEt.getText().toString().trim());
                    String pwd = mPayCostPwdEt.getText().toString().trim();
                    mPresenter.payCost(mPayFlag, payMoney, pwd);
                }
                return false;
            }
        });

        mApplyFreeChargeCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPayCostConfirmBtn.setText("提交申请");
                    mApplyFreeChargeReasonEt.setVisibility(View.VISIBLE);
                    mPayCostMoneyEt.setVisibility(View.GONE);
                } else {
                    mPayCostConfirmBtn.setText("确认收现金");
                    mApplyFreeChargeReasonEt.setVisibility(View.GONE);
                    mPayCostMoneyEt.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }


    @Override
    public void payCostSuccess() {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, (mPayFlag == 2) ? "交押金成功！" : "交罚金成功！");
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra(PAY_FLAG, mPayFlag);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 展示交押金信息
     *
     * @param msgId
     * @param maxMoney 最大可交押金
     */
    @Override
    public void showDepositMsg(int msgId, String maxMoney) {
        showMessageDialog(getString(msgId, maxMoney));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_bottom);
    }

    @Override
    public void showErrorMsg(String msg) {
        showMessageDialog(msg);
    }

    @Override
    public void showErrorMsg(int msgId) {
        showMessageDialog(getString(msgId));
    }

    @Override
    public void setNoLoginPermission(int kickedOffline) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(kickedOffline));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                LoginActivity.startActivity(PayCostActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void applyPenaltyFreeSuccess() {
        Intent intent = new Intent();
        intent.putExtra(PAY_FLAG, mPayFlag);
        intent.putExtra("APPLY_PENALTY_SUCCESS", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void clearPswText() {
        mPayCostPwdEt.setText("");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            setResult(RESULT_CANCELED);
        }
        return super.onKeyDown(keyCode, event);
    }
}
