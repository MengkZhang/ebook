package com.tzpt.cloundlibrary.manager.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.bean.PenaltyBean;
import com.tzpt.cloundlibrary.manager.ui.adapter.PenaltyListAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.PenaltyDealContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.PenaltyDealPresenter;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;
import com.tzpt.cloundlibrary.manager.widget.popupwindow.PenaltyDealPPW;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 罚金处理
 * Created by Administrator on 2018/12/12.
 */

public class PenaltyDealActivity extends BaseActivity implements PenaltyDealContract.View {
    private static final String FROM_TYPE = "from_type";
    private static final String READER_ID = "reader_id";

    public static void startActivity(Context context, int fromType, String readerId) {
        Intent intent = new Intent(context, PenaltyDealActivity.class);
        intent.putExtra(FROM_TYPE, fromType);
        intent.putExtra(READER_ID, readerId);
        context.startActivity(intent);
    }

    public static void startActivityForResult(Activity activity, String readerId, int requestCode) {
        Intent intent = new Intent(activity, PenaltyDealActivity.class);
        intent.putExtra(FROM_TYPE, 1000);
        intent.putExtra(READER_ID, readerId);
        activity.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.reader_name_number_tv)
    TextView mReaderNameNumberTv;
    @BindView(R.id.borrowable_sum_tv)
    TextView mNoBackSumTv;
    @BindView(R.id.usable_deposit_tv)
    TextView mUsableDepositTv;
    @BindView(R.id.penalty_list_rl)
    RelativeLayout mPenaltyListRl;
    @BindView(R.id.penalty_list_rv)
    RecyclerView mPenaltyListRv;
    @BindView(R.id.penalty_btn_ll)
    LinearLayout mPenaltyBtnLl;
    @BindView(R.id.deal_penalty_right_btn)
    Button mDealPenaltyRightBtn;
    @BindView(R.id.book_sum_tv)
    TextView mBookSumTv;
    @BindView(R.id.book_price_tv)
    TextView mBookPriceTv;
    @BindView(R.id.book_under_penalty_tv)
    TextView mBookUnderPenaltyTv;

    private PenaltyListAdapter mAdapter;
    private PenaltyDealPresenter mPresenter;

    private int mFromType;
    private String mReaderId;

    private PenaltyDealPPW mPenaltyDealPPW;

    @OnClick({R.id.deal_penalty_left_btn, R.id.deal_penalty_right_btn, R.id.titlebar_left_btn,})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.deal_penalty_left_btn:
                if (mPenaltyDealPPW == null) {
                    mPenaltyDealPPW = new PenaltyDealPPW(this);
                }
                mPenaltyDealPPW.showAtLocation(mCommonTitleBar, Gravity.BOTTOM, 0, 0);
                mPenaltyDealPPW.showApplyFreeChargeView();
                mPenaltyDealPPW.setOnApplyFreeChargeClickListener(new PenaltyDealPPW.OnApplyFreeChargeClickListener() {
                    @Override
                    public void applyFreeCharge(String reason, String pwd) {
                        mPresenter.applyPenaltyFree(reason, pwd);
                    }
                });
                break;
            case R.id.deal_penalty_right_btn:
                mPresenter.clickRightBtn();
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_penalty_deal;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("罚金处理");
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
    }

    @Override
    public void initDatas() {
        mPresenter = new PenaltyDealPresenter();
        mPresenter.attachView(this);

        mFromType = getIntent().getIntExtra(FROM_TYPE, -1);
        if (mFromType == -1) {
            finish();
        }

        mReaderId = getIntent().getStringExtra(READER_ID);
        mPresenter.getLibraryInfo();
        mPresenter.getPenaltyList(mReaderId);
    }

    @Override
    public void configViews() {
        mPenaltyListRv.setHasFixedSize(true);
        mPenaltyListRv.setLayoutManager(new LinearLayoutManager(this));
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
    public void showDialogTipAndFinish(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.hasNoCancel(false);
        dialog.setCancelable(false);
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
    public void showDialogTip(String msg) {
        showMessageDialog(msg);
    }

    @Override
    public void showDialogTip(int msgId) {
        showMessageDialog(getString(msgId));
    }

    @Override
    public void setRightBtnStatus(int agreementLevel) {
        if (agreementLevel == 1) {
            mDealPenaltyRightBtn.setText("退出");
        } else if (agreementLevel == 2) {
            mDealPenaltyRightBtn.setText("代收罚金");
        } else {
            mDealPenaltyRightBtn.setText("收罚金");
        }
    }

    @Override
    public void dealPenaltySuccess(final String readerId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, "交罚金成功！");
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                switch (mFromType) {
                    case 0:
                        BorrowBookManagementActivity.startActivity(PenaltyDealActivity.this, readerId);
                        finish();
                        break;
                    case 2:
                        RefundDepositActivity.startActivity(PenaltyDealActivity.this, readerId);
                        finish();
                        break;
                    case 1000:
                        finish();
                        break;
                }
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void applyChargeFreePenaltySuccess(final String readerId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, "提交申请成功！");
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                switch (mFromType) {
                    case 0:
                        BorrowBookManagementActivity.startActivity(PenaltyDealActivity.this, readerId);
                        finish();
                        break;
                    case 2:
                        RefundDepositActivity.startActivity(PenaltyDealActivity.this, readerId);
                        finish();
                        break;
                    case 1000:
                        finish();
                        break;
                }
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
                finish();
            }
        });
    }

    @Override
    public void setReaderNameNumber(String info) {
        mReaderNameNumberTv.setText(info);
    }

    @Override
    public void setNoBackSum(String info) {
        mNoBackSumTv.setText(info);
    }

    @Override
    public void setDepositInfo(String info) {
        mUsableDepositTv.setText(info);
    }

    @Override
    public void setPenaltyList(List<PenaltyBean> list) {
        if (list != null && list.size() > 0) {
            mPenaltyListRl.setVisibility(View.VISIBLE);
            mPenaltyBtnLl.setVisibility(View.VISIBLE);
            if (mAdapter == null) {
                mAdapter = new PenaltyListAdapter(this, list);
                mPenaltyListRv.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mPenaltyListRl.setVisibility(View.GONE);
            mPenaltyBtnLl.setVisibility(View.GONE);
        }
    }

    @Override
    public void setTotalInfo(int sumInfo, double totalPrice, double totalPenalty) {
        mBookSumTv.setText(getString(R.string.total_sum, String.valueOf(sumInfo)));
        mBookPriceTv.setText(getString(R.string.total_price, MoneyUtils.formatMoney(totalPrice)));
        mBookUnderPenaltyTv.setText(getString(R.string.total_penalty, MoneyUtils.formatMoney(totalPenalty)));
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
                LoginActivity.startActivity(PenaltyDealActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void exitDealPenalty() {
        finish();
    }

    @Override
    public void showSubstituteChargePenalty(double penalty) {
        if (mPenaltyDealPPW == null) {
            mPenaltyDealPPW = new PenaltyDealPPW(this);
        }
        mPenaltyDealPPW.showAtLocation(mCommonTitleBar, Gravity.BOTTOM, 0, 0);
        mPenaltyDealPPW.showSubstituteChargePenalty(penalty);
        mPenaltyDealPPW.setOnChargePenaltyClickListener(new PenaltyDealPPW.OnChargePenaltyClickListener() {
            @Override
            public void chargePenalty(String pwd) {
                mPresenter.chargePenalty(pwd);
            }
        });
    }

    @Override
    public void showCashChargePenalty(double penalty) {
        if (mPenaltyDealPPW == null) {
            mPenaltyDealPPW = new PenaltyDealPPW(this);
        }
        mPenaltyDealPPW.showAtLocation(mCommonTitleBar, Gravity.BOTTOM, 0, 0);
        mPenaltyDealPPW.showCashChargePenalty(penalty);
        mPenaltyDealPPW.setOnChargePenaltyClickListener(new PenaltyDealPPW.OnChargePenaltyClickListener() {
            @Override
            public void chargePenalty(String pwd) {
                mPresenter.chargePenalty(pwd);
            }
        });
    }

    @Override
    public void turnToSubstituteChargePenalty(String readerId, double money) {
        SubstitutePayDepositActivity.startActivityForResult(this, 1, readerId, money, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            mPresenter.getPenaltyList(mReaderId);
        }
    }

    @Override
    public void finish() {
        if (mFromType == 1000) {
            setResult(RESULT_OK);
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }
}
