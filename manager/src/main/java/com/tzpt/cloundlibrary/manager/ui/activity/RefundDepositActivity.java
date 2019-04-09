package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.ui.adapter.RefundDepositBookListAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.RefundDepositContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.RefundDepositPresenter;
import com.tzpt.cloundlibrary.manager.utils.CashierInputFilter;
import com.tzpt.cloundlibrary.manager.utils.KeyboardUtils;
import com.tzpt.cloundlibrary.manager.widget.TitleBarView;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 退押金
 * Created by Administrator on 2017/7/11.
 */
public class RefundDepositActivity extends BaseActivity implements RefundDepositContract.View {
    private static final String ID_CARD_BEAN_ID = "id_card_bean_id";

    public static void startActivity(Context context, String readerId) {
        Intent intent = new Intent(context, RefundDepositActivity.class);
        intent.putExtra(ID_CARD_BEAN_ID, readerId);
        context.startActivity(intent);
    }

    @BindView(R.id.refund_deposit_title)
    TitleBarView mRefundDepositTitle;
    @BindView(R.id.reader_name_number_tv)
    TextView mReaderNameNumberTv;
    @BindView(R.id.borrowable_sum_tv)
    TextView mBorrowableSumTv;
    @BindView(R.id.usable_deposit_tv)
    TextView mUsableDepositTv;
    @BindView(R.id.refund_deposit_book_list_rv)
    RecyclerView mRefundDepositBookListRv;
    @BindView(R.id.book_total_title)
    TextView mBookTotalTitle;
    @BindView(R.id.book_sum_tv)
    TextView mBookSumTv;
    @BindView(R.id.book_price_tv)
    TextView mBookPriceTv;
    @BindView(R.id.refund_deposit_money_et)
    EditText mRefundDepositMoneyEt;
    @BindView(R.id.refund_deposit_pwd_et)
    EditText mRefundDepositPwdEt;
    @BindView(R.id.confirm_btn)
    Button mConfirmBtn;
    @BindView(R.id.operator_ll)
    LinearLayout mOperatorLl;
    @BindView(R.id.no_refund_tips_tv)
    TextView mNoRefundTipsTv;
    @BindView(R.id.total_info_ll)
    RelativeLayout mTotalInfoLl;

    private RefundDepositPresenter mPresenter;

    private RefundDepositBookListAdapter mAdapter;
    private List<BookInfoBean> mBookList = new ArrayList<>();

    @OnClick({R.id.confirm_btn, R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confirm_btn:
                mPresenter.refundDeposit(mRefundDepositMoneyEt.getText().toString().trim(),
                        mRefundDepositPwdEt.getText().toString().trim());
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_refund_deposit;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        String readerId = getIntent().getStringExtra(ID_CARD_BEAN_ID);
        mPresenter = new RefundDepositPresenter();
        mPresenter.attachView(this);

        mPresenter.getBorrowBookList(readerId);
    }

    @Override
    public void configViews() {
        mRefundDepositTitle.setTitle("退馆押金");
        mRefundDepositTitle.setLeftBtnIcon(R.mipmap.ic_arrow_left);

        InputFilter[] filters = {new CashierInputFilter(false)};
        mRefundDepositMoneyEt.setFilters(filters);
        mRefundDepositMoneyEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    KeyboardUtils.hideSoftInput(RefundDepositActivity.this);
                    mPresenter.refundDeposit(mRefundDepositMoneyEt.getText().toString().trim(),
                            mRefundDepositPwdEt.getText().toString().trim());
                }
                return false;
            }
        });

        mRefundDepositBookListRv.setHasFixedSize(true);
        mRefundDepositBookListRv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RefundDepositBookListAdapter(this, mBookList);
        mRefundDepositBookListRv.setAdapter(mAdapter);
    }

    @Override
    public void showLoading() {
        showDialog("");
    }

    @Override
    public void dismissLoading() {
        dismissDialog();
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
    public void setRefundOperatorVisibility(boolean visibility, String tips) {
        if (visibility) {
            mOperatorLl.setVisibility(View.VISIBLE);
            mNoRefundTipsTv.setVisibility(View.GONE);
        } else {
            mOperatorLl.setVisibility(View.GONE);
            mNoRefundTipsTv.setVisibility(View.VISIBLE);
            mNoRefundTipsTv.setText(tips);
        }

    }

    @Override
    public void setTotalInfo(String sumInfo, String moneyInfo) {
        mBookSumTv.setText(sumInfo);
        mBookPriceTv.setText(moneyInfo);
    }

    @Override
    public void setTotalInfoVisibility(int visibility) {
        mTotalInfoLl.setVisibility(visibility);
    }

    @Override
    public void refreshBookList(List<BookInfoBean> list) {
        mBookList.clear();
        mBookList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMsgNoCancelable(String msg) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
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

    @Override
    public void showMsgNoCancelable(int msgId) {
        showMsgNoCancelable(getString(msgId));
    }

    @Override
    public void showRefundDepositSuccess(String msg) {
        mRefundDepositMoneyEt.setText("");
        mRefundDepositPwdEt.setText("");
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
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

    @Override
    public void noPermissionDialog(int kickedOffline) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(kickedOffline));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                LoginActivity.startActivity(RefundDepositActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void showMsgNoCancelableFinish(int msgId) {
        showMsgNoCancelableFinish(getString(msgId));
    }

    @Override
    public void showMsgNoCancelableFinish(String msg) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
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
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.detachView();
        }
    }
}
