package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.ui.adapter.CompensateBookAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.LostBookContact;
import com.tzpt.cloundlibrary.manager.ui.presenter.LostBookPresenter;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.widget.TitleBarView;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 赔书管理
 * Created by Administrator on 2017/7/11.
 */

public class LostBookActivity extends BaseActivity implements LostBookContact.View {
    private static final int REQUEST_CODE_SUBSTITUTE = 1000;
    private static final String ID_CARD_BEAN_ID = "ID_CARD_BEAN_ID";

    public static void startActivity(Context context, String readerId) {
        Intent intent = new Intent(context, LostBookActivity.class);
        intent.putExtra(ID_CARD_BEAN_ID, readerId);
        context.startActivity(intent);
    }

    @BindView(R.id.lost_book_title)
    TitleBarView mLostBookTitle;
    @BindView(R.id.reader_name_number_tv)
    TextView mReaderNameNumberTv;
    @BindView(R.id.borrowable_sum_tv)
    TextView mBorrowableSumTv;
    @BindView(R.id.usable_deposit_tv)
    TextView mUsableDepositTv;
    @BindView(R.id.book_total_title)
    TextView mBookTotalTitleTv;
    @BindView(R.id.book_sum_tv)
    TextView mBookSumTv;
    @BindView(R.id.book_price_tv)
    TextView mBookPriceTv;
    @BindView(R.id.operator_pwd_et)
    EditText mOperatorPwdEt;
    @BindView(R.id.lost_book_list_rv)
    RecyclerView mLostBookListRv;
    @BindView(R.id.confirm_btn)
    Button mConfirmBtn;
    @BindView(R.id.unable_compensate_ll)
    LinearLayout mUnableCompensateLl;
    @BindView(R.id.compensate_book_ll)
    LinearLayout mCompensateBookLl;
    @BindView(R.id.compensate_money_tv)
    TextView mCompensateMoneyTv;
    @BindView(R.id.use_platform_deposit_btn)
    Button mUsePlatformDepositBtn;
    @BindView(R.id.cash_compensate_btn)
    Button mCashCompensateBtn;

    private LostBookPresenter mPresenter;
    private CompensateBookAdapter mAdapter;

    @OnClick({R.id.confirm_btn, R.id.titlebar_left_btn, R.id.use_platform_deposit_btn,
            R.id.cash_compensate_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confirm_btn:
                mPresenter.compensateBook(mOperatorPwdEt.getText().toString().trim());
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.use_platform_deposit_btn:
                finish();
                break;
            case R.id.cash_compensate_btn:
                mUnableCompensateLl.setVisibility(View.GONE);
                mCompensateBookLl.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_lost_book;
    }

    @Override
    public void initToolBar() {
    }

    @Override
    public void initDatas() {
        String readerId = getIntent().getStringExtra(ID_CARD_BEAN_ID);

        mPresenter = new LostBookPresenter();
        mPresenter.attachView(this);
        mPresenter.getReturnBookInfo(readerId);
    }

    @Override
    public void configViews() {
        mLostBookTitle.setTitle(R.string.compensate_management);
        mLostBookTitle.setLeftBtnIcon(R.mipmap.ic_arrow_left);

        mBookTotalTitleTv.setText(R.string.compensate_book_total);

        mLostBookListRv.setHasFixedSize(true);
        mLostBookListRv.setLayoutManager(new LinearLayoutManager(this));
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
    public void setBorrowableSum(int count) {
        mBorrowableSumTv.setText(getString(R.string.book_no_back, count));
    }

    @Override
    public void setDepositOrPenalty(String info) {
        if (TextUtils.isEmpty(info)) {
            mUsableDepositTv.setText("");
        } else {
            mUsableDepositTv.setText(info);
        }

    }

    @Override
    public void setTotalInfo(int count, String moneyInfo) {
        mBookSumTv.setText(getString(R.string.total_sum, String.valueOf(count)));
        mBookPriceTv.setText(getString(R.string.total_price, moneyInfo));
    }

    @Override
    public void refreshBookList(List<BookInfoBean> list) {
        if (mAdapter == null) {
            mAdapter = new CompensateBookAdapter(this, list, new CompensateBookAdapter.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(int position, boolean isChecked) {
                    mPresenter.setBookStatus(position, isChecked);
                }
            });
            mLostBookListRv.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setEmptyBookList() {
        if (mAdapter != null) {
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setOperateTips(int pwdHintStrId, int btnStrId, String payMoney, int editType, boolean editAble) {
        mOperatorPwdEt.setHint(pwdHintStrId);
        mOperatorPwdEt.setInputType(editType);
        mCompensateMoneyTv.setText(payMoney);
        mConfirmBtn.setText(btnStrId);
    }

    @Override
    public void showDialogMsg(int msgId) {
        showMessageDialog(getString(msgId));
    }

    @Override
    public void compensateBookSuccess(final String readerId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(R.string.compensate_book_success));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mPresenter.getReturnBookInfo(readerId);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    //提示收款金额不能小于s%元
    @Override
    public void showMoneyDialogTips(String money) {
        showMessageDialog(getString(R.string.money_not_less_than, money));
    }

    @Override
    public void showDialog1BtnFinish(int msgId) {
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
                LoginActivity.startActivity(LostBookActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void clearPswText() {
        mOperatorPwdEt.setText("");
    }

    @Override
    public void showLostBookUI(double payMoney) {
        mUnableCompensateLl.setVisibility(View.GONE);
        mCompensateBookLl.setVisibility(View.VISIBLE);

        mBookTotalTitleTv.setTextColor(Color.parseColor("#888888"));
        mBookSumTv.setTextColor(Color.parseColor("#888888"));
        mBookPriceTv.setTextColor(Color.parseColor("#888888"));
        mOperatorPwdEt.setText("");
        mOperatorPwdEt.setHint("读者密码");
        mOperatorPwdEt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        mConfirmBtn.setText("确认扣押金");
        mCompensateMoneyTv.setText(MoneyUtils.formatMoney(payMoney));
    }

    @Override
    public void showUnableLostBookUI(int agreementLevel, double payMoney) {
        if (agreementLevel == 1) {
            mUnableCompensateLl.setVisibility(View.VISIBLE);
            mCompensateBookLl.setVisibility(View.GONE);
            mUsePlatformDepositBtn.setVisibility(View.VISIBLE);
            mCashCompensateBtn.setVisibility(View.GONE);
        } else if (agreementLevel == 2) {
            mUnableCompensateLl.setVisibility(View.VISIBLE);
            mCompensateBookLl.setVisibility(View.GONE);
            mUsePlatformDepositBtn.setVisibility(View.VISIBLE);
            mCashCompensateBtn.setVisibility(View.VISIBLE);
            mCashCompensateBtn.setText("代收赔金");
        } else if (agreementLevel == 3) {
            mUnableCompensateLl.setVisibility(View.VISIBLE);
            mCompensateBookLl.setVisibility(View.GONE);
            mUsePlatformDepositBtn.setVisibility(View.VISIBLE);
            mCashCompensateBtn.setVisibility(View.VISIBLE);
            mCashCompensateBtn.setText("收赔金");
        } else {
            mUnableCompensateLl.setVisibility(View.VISIBLE);
            mCompensateBookLl.setVisibility(View.GONE);
            mUsePlatformDepositBtn.setVisibility(View.GONE);
            mCashCompensateBtn.setVisibility(View.VISIBLE);
            mCashCompensateBtn.setText("收赔金");
        }
        mBookTotalTitleTv.setTextColor(Color.RED);
        mBookSumTv.setTextColor(Color.RED);
        mBookPriceTv.setTextColor(Color.RED);
        mOperatorPwdEt.setText("");
        mOperatorPwdEt.setHint("操作员密码");
        mOperatorPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mConfirmBtn.setText("确认收现金");
        mCompensateMoneyTv.setText(MoneyUtils.formatMoney(payMoney));
    }

    @Override
    public void hideLostBookUI() {
        mUnableCompensateLl.setVisibility(View.GONE);
        mCompensateBookLl.setVisibility(View.GONE);
        mBookTotalTitleTv.setTextColor(Color.parseColor("#888888"));
        mBookSumTv.setTextColor(Color.parseColor("#888888"));
        mBookPriceTv.setTextColor(Color.parseColor("#888888"));
    }

    @Override
    public void turnToDaishouPenalty(String readerId, double money) {
        SubstitutePayDepositActivity.startActivityForResult(this, 0, readerId, money, REQUEST_CODE_SUBSTITUTE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SUBSTITUTE && resultCode == RESULT_OK) {
            mPresenter.compensateBookDirect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            if (mAdapter != null) {
                mAdapter.clear();
            }
            mPresenter.detachView();
        }
    }
}
