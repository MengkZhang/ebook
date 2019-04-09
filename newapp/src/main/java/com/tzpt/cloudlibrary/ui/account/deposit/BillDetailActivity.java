package com.tzpt.cloudlibrary.ui.account.deposit;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.utils.MoneyUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/25.
 */

public class BillDetailActivity extends BaseActivity {

    private static final String BILL_MONEY = "bill_money";
    private static final String BILL_TYPE = "bill_type";
    private static final String BILL_PAY_TYPE_TITLE = "bill_pay_type_title";
    private static final String BILL_PAY_TYPE = "bill_pay_type";
    private static final String BILL_PAY_TIME = "bill_pay_time";
    private static final String BILL_ORDER_NUM = "bill_order_num";
    private static final String BILL_OPERATE_ORDER = "bill_operate_order";
    private static final String BILL_DEDUCTION_MONEY = "bill_deduction_money";
    private static final String BILL_COMMENT = "bill_remark";
    private static final String BILL_TRANSACTION_TYPE = "bill_transaction_type";

    public static void startActivity(Context context, String money, String type, String payTypeTitle,
                                     String payType, String time, String orderNum, String operateOrder,
                                     double deductionMoney, String comment, int transactionType) {
        Intent intent = new Intent(context, BillDetailActivity.class);
        intent.putExtra(BILL_MONEY, money);
        intent.putExtra(BILL_TYPE, type);
        intent.putExtra(BILL_PAY_TYPE_TITLE, payTypeTitle);
        intent.putExtra(BILL_PAY_TYPE, payType);
        intent.putExtra(BILL_PAY_TIME, time);
        intent.putExtra(BILL_ORDER_NUM, orderNum);
        intent.putExtra(BILL_OPERATE_ORDER, operateOrder);
        intent.putExtra(BILL_DEDUCTION_MONEY, deductionMoney);
        intent.putExtra(BILL_COMMENT, comment);
        intent.putExtra(BILL_TRANSACTION_TYPE, transactionType);
        context.startActivity(intent);
    }

    @BindView(R.id.bill_money_tv)
    TextView mBillMoneyTv;
    @BindView(R.id.bill_pay_money)
    TextView mBillPayMoney;
    @BindView(R.id.bill_pay_money_ll)
    LinearLayout mBillPayMoneyLl;
    @BindView(R.id.bill_type_tv)
    TextView mBillTypeTv;
    @BindView(R.id.bill_pay_type_title_tv)
    TextView mBillPayTypeTitleTv;
    @BindView(R.id.bill_pay_type_tv)
    TextView mBillPayTypeTv;
    @BindView(R.id.bill_pay_time_tv)
    TextView mBillPayTimeTv;
    @BindView(R.id.bill_num_tv)
    TextView mBillNumTv;
    @BindView(R.id.bill_operate_order_tv)
    TextView mBillOperateOrderTv;
    @BindView(R.id.bill_remark_tv)
    TextView mBillRemarkTv;
    @BindView(R.id.bill_num_ll)
    LinearLayout mBillNumLL;
    @BindView(R.id.bill_operate_order_ll)
    LinearLayout mBillOperateOrderLL;

    @OnClick({R.id.titlebar_left_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bill_detail;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("账单详情");
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        String money = getIntent().getStringExtra(BILL_MONEY);
        String type = getIntent().getStringExtra(BILL_TYPE);
        String payTypeTitle = getIntent().getStringExtra(BILL_PAY_TYPE_TITLE);
        String payType = getIntent().getStringExtra(BILL_PAY_TYPE);
        String time = getIntent().getStringExtra(BILL_PAY_TIME);
        String orderNum = getIntent().getStringExtra(BILL_ORDER_NUM);
        String operateOrder = getIntent().getStringExtra(BILL_OPERATE_ORDER);
        double deductionMoney = getIntent().getDoubleExtra(BILL_DEDUCTION_MONEY, 0);
        String comment = getIntent().getStringExtra(BILL_COMMENT);
        int transactionType = getIntent().getIntExtra(BILL_TRANSACTION_TYPE, 0);

        mBillMoneyTv.setText(money);
        mBillTypeTv.setText(type);
        mBillPayTypeTitleTv.setText(payTypeTitle);
        mBillPayTypeTv.setText(payType);
        mBillPayTimeTv.setText(time);
        if (TextUtils.isEmpty(comment)) {
            mBillRemarkTv.setVisibility(View.GONE);
        } else {
            mBillRemarkTv.setVisibility(View.VISIBLE);
            mBillRemarkTv.setText(comment);
        }

        if (TextUtils.isEmpty(orderNum)) {
            mBillNumLL.setVisibility(View.GONE);
        } else {
            mBillNumLL.setVisibility(View.VISIBLE);
            mBillNumTv.setText(orderNum);
        }

        if (TextUtils.isEmpty(operateOrder)) {
            mBillOperateOrderLL.setVisibility(View.GONE);
        } else {
            mBillOperateOrderLL.setVisibility(View.VISIBLE);
            mBillOperateOrderTv.setText(operateOrder);
        }

        if (deductionMoney < 0 && transactionType == 6) {
            mBillPayMoneyLl.setVisibility(View.VISIBLE);
            mBillMoneyTv.setText(MoneyUtils.formatMoney(deductionMoney));
            mBillPayMoney.setText(money);
        } else {
            mBillPayMoneyLl.setVisibility(View.GONE);
        }

    }
}
