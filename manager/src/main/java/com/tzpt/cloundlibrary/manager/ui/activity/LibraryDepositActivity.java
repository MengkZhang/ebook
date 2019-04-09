package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.ui.contract.LibraryDepositContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.LibraryDepositPresenter;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 押金
 */
public class LibraryDepositActivity extends BaseActivity implements
        LibraryDepositContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LibraryDepositActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.titlebar_title_tv)
    TextView mTitleBarTitleTv;
    @BindView(R.id.titlebar_right_tv)
    TextView mTitleBarRightTv;
    @BindView(R.id.library_deposit_tv)
    TextView mLibraryDepositTv;
    @BindView(R.id.library_pay_deposit_btn)
    Button mLibraryPayDepositBtn;
    @BindView(R.id.library_refund_deposit_btn)
    Button mLibraryRefundDepositBtn;


    private LibraryDepositPresenter mPresenter;

    @OnClick({R.id.titlebar_right_tv, R.id.titlebar_left_btn, R.id.library_pay_deposit_btn,
            R.id.library_refund_deposit_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_tv:
                LibraryDepositDetailActivity.startActivity(this);
                break;
            case R.id.library_pay_deposit_btn:
                PayDepositActivity.startActivityForResult(this, 1001);
                break;
            case R.id.library_refund_deposit_btn:
                RefundPlatformDepositActivity.startActivityForResult(this, 1000);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_library_deposit;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        mTitleBarTitleTv.setText("备用金");
        mTitleBarRightTv.setVisibility(View.VISIBLE);
        mTitleBarRightTv.setText("明细");

        mPresenter = new LibraryDepositPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {
        mPresenter.getLibraryDeposit();
    }

    @Override
    public void setNoPermissionDialog(int kickOut) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(kickOut));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                LoginActivity.startActivity(LibraryDepositActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    //设置错误提示
    @Override
    public void setErrorDialog(int msgId) {
        mLibraryDepositTv.setVisibility(View.VISIBLE);
        mLibraryDepositTv.setBackgroundColor(Color.TRANSPARENT);
        mLibraryDepositTv.setGravity(Gravity.CENTER_HORIZONTAL);
        mLibraryDepositTv.setTextColor(Color.parseColor("#888888"));
        mLibraryDepositTv.setTextSize(16);
        mLibraryDepositTv.setText(msgId);
    }

    @Override
    public void showProgressDialog() {
        showDialog("加载中...");
    }

    @Override
    public void dismissProgressDialog() {
        dismissDialog();
    }

    //设置可用押金
    @Override
    public void setAvailableBalance(String availableBalance, boolean showRefundBtn) {
        if (MoneyUtils.stringToDouble(availableBalance) > 0) {
            mLibraryRefundDepositBtn.setClickable(true);
            mLibraryRefundDepositBtn.setBackgroundResource(R.drawable.bg_btn_round_8a633d);
        } else {
            mLibraryRefundDepositBtn.setClickable(false);
            mLibraryRefundDepositBtn.setBackgroundResource(R.drawable.bg_round_cccccc);
        }
        mLibraryPayDepositBtn.setVisibility(View.VISIBLE);
        if (showRefundBtn) {
            mLibraryRefundDepositBtn.setVisibility(View.VISIBLE);
        } else {
            mLibraryRefundDepositBtn.setVisibility(View.GONE);
        }

        mLibraryDepositTv.setVisibility(View.VISIBLE);
        mLibraryDepositTv.setText(Html.fromHtml("可用余额<font color= '#9e724d'> " + availableBalance + "</font> 元"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            mPresenter.getLibraryDeposit();
        } else if (requestCode == 1001 && resultCode == RESULT_OK) {
            mPresenter.getLibraryDeposit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

}
