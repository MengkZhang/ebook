package com.tzpt.cloudlibrary.ui.account.deposit;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 钱包
 */
public class UserDepositModuleActivity extends BaseActivity implements UserDepositModuleContract.View {
    private static final int REQUEST_CODE_PAY_DEPOSIT = 1001;
    private static final int REQUEST_CODE_REFUND_DEPOSIT = 1002;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, UserDepositModuleActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.progress_layout)
    LoadingProgressView mProgressLayout;

    private UserDepositModulePresenter mPresenter;

    @OnClick({R.id.titlebar_left_btn, R.id.pay_deposit_btn, R.id.pay_penalty_btn,
            R.id.refund_deposit_btn, R.id.bill_btn, R.id.pay_deposit_explain_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.bill_btn:
                UserDepositActivity.startActivity(this);
                break;
            case R.id.pay_deposit_btn:
                PayDepositActivity.startActivityForResult(this, REQUEST_CODE_PAY_DEPOSIT);
                break;
            case R.id.refund_deposit_btn:
                RefundDepositActivity.startActivityForResult(this, REQUEST_CODE_REFUND_DEPOSIT);
                break;
            case R.id.pay_deposit_explain_btn:
                String readId = mPresenter.getReaderId();
                if (!TextUtils.isEmpty(readId)) {
                    UserDepositAgreementActivity.startActivity(this,
                            "押金说明", "http://img.ytsg.cn/html/userapp/depositDesc.html?userId=" + readId);
                }
                break;
            case R.id.pay_penalty_btn:
                PayPenaltyActivity.startActivity(this);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_deposit_module;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("钱包");
    }

    @Override
    public void initDatas() {
        mPresenter = new UserDepositModulePresenter();
        mPresenter.attachView(this);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {

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
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }

        //取消事件订阅
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginOut(AccountMessage loginMessage) {
        if (loginMessage.mIsLoginOut) {
            finish();
        }
    }
}
