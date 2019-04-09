package com.tzpt.cloudlibrary.ui.account.deposit;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 退押金认证
 */
public class RefundDepositAuthenticationActivity extends BaseActivity implements
        RefundDepositAuthenticationContract.View {

    @BindView(R.id.user_deposit_can_use_tv)
    TextView mUserDepositCanUseTv;
    @BindView(R.id.user_deposit_take_tv)
    TextView mUserDepositTakeTv;
    @BindView(R.id.refund_deposit_account_et)
    EditText mRefundDepositAccountEt;
    @BindView(R.id.refund_deposit_name_et)
    EditText mRefundDepositNameEt;
    private static final String FROM_TYPE = "from_type";

    private RefundDepositAuthenticationPresenter mPresenter;

    public static void startActivity(Context context, int fromType) {
        Intent intent = new Intent(context, RefundDepositAuthenticationActivity.class);
        intent.putExtra(FROM_TYPE, fromType);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_refund_deposit_authentication;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("退押金");
    }

    @Override
    public void initDatas() {
        mPresenter = new RefundDepositAuthenticationPresenter();
        mPresenter.attachView(this);

        Intent intent = getIntent();
        int fromType = intent.getIntExtra(FROM_TYPE, 0);
        setUserDepositAuthenticationType(fromType);
        mPresenter.setFromType(fromType);
        setUserDepositInfo();
    }

    @Override
    public void configViews() {

    }

    @OnClick({R.id.titlebar_left_btn, R.id.refund_deposit_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.refund_deposit_btn:
                mPresenter.startAuthentication(mRefundDepositAccountEt.getText().toString().trim(),
                        mRefundDepositNameEt.getText().toString().trim());
                break;
        }
    }

    //设置用户押金信息
    private void setUserDepositInfo() {
        mUserDepositCanUseTv.setText(getString(R.string.can_use_deposit, "0.00"));
        mUserDepositTakeTv.setText(getString(R.string.take_deposit, "0.00"));
    }

    //设置账户类型
    private void setUserDepositAuthenticationType(int fromType) {
        switch (fromType) {
            case 0://微信
                mRefundDepositAccountEt.setHint("微信账户");
                break;
            case 1://支付宝
                mRefundDepositAccountEt.setHint("支付宝账户");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.detachView();
        }
    }

    @Override
    public void showErrorMsg(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }
}
