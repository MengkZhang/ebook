package com.tzpt.cloudlibrary.ui.account.selfhelp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.camera.ScanCallback;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.utils.MoneyUtils;
import com.tzpt.cloudlibrary.widget.camera.ScanWrapper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 自助借书扫描界面
 */
public class SelfHelpBorrowBooksScanningActivity extends BaseActivity {

    private static final String FROM_TYPE = "from_type";
    private static final String BAR_NUMBER = "bar_number";
    private static final String USABLE_BORROW_NUM = "usable_borrow_num";
    private static final String NEED_DEPOSIT = "need_deposit";
    private static final String PENALTY = "penalty";
    private static final String USABLE_DEPOSIT = "";
    private static final String BORROWED_NUM = "borrowed_num";
    private static final String BOOK_TOTAL_PRICE = "book_total_num";
    private static final String OCCUPY_DEPOSIT = "occupy_deposit";

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SelfHelpBorrowBooksScanningActivity.class);
        intent.putExtra(FROM_TYPE, 0);
        context.startActivity(intent);
    }

    public static void startActivityForResult(Activity context, int usableBorrowNum, boolean needDeposit,
                                              double penalty, double usableDeposit, int borrowedNum,
                                              double totalPrice, double occupyDeposit, int requestCode) {
        Intent intent = new Intent(context, SelfHelpBorrowBooksScanningActivity.class);
        intent.putExtra(FROM_TYPE, 1);
        intent.putExtra(USABLE_BORROW_NUM, usableBorrowNum);
        intent.putExtra(NEED_DEPOSIT, needDeposit);
        intent.putExtra(PENALTY, penalty);
        intent.putExtra(USABLE_DEPOSIT, usableDeposit);
        intent.putExtra(BORROWED_NUM, borrowedNum);
        intent.putExtra(BOOK_TOTAL_PRICE, totalPrice);
        intent.putExtra(OCCUPY_DEPOSIT, occupyDeposit);
        context.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.self_book_camera_preview)
    ScanWrapper mSelfBookCameraPreview;
    @BindView(R.id.self_book_reader_borrowable_tv)
    TextView mSelfBookCanBorrowTv;
    @BindView(R.id.self_book_reader_usable_deposit_tv)
    TextView mSelfBookDepositTv;
    @BindView(R.id.self_book_money_tv)
    TextView mSelfBookMoneyTv;
    @BindView(R.id.self_book_take_deposit_tv)
    TextView mSelfBookTakeDepositTv;
    @BindView(R.id.self_book_sum_tv)
    TextView mBorrowBookSumTv;

//    private SelfHelpBorrowBooksScanningPresenter mPresenter;
    private int mFromType = 0;

    @OnClick({R.id.titlebar_left_btn, R.id.btn_light, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.btn_light:
                mSelfBookCameraPreview.turnLight();
                break;
            case R.id.btn_back:
                if (mFromType == 0) {//进入图书列表
                    SelfHelpBorrowBooksActivity.startActivity(this, null);
                }
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_self_help_borrow_books_scanning;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("自助借书");
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);

        mFromType = getIntent().getIntExtra(FROM_TYPE, -1);

        int usableBorrowNum = getIntent().getIntExtra(USABLE_BORROW_NUM, 0);
        boolean needDeposit = getIntent().getBooleanExtra(NEED_DEPOSIT, false);
        double penalty = getIntent().getDoubleExtra(PENALTY, 0);
        double usableDeposit = getIntent().getDoubleExtra(USABLE_DEPOSIT, 0);
        int borrowedNum = getIntent().getIntExtra(BORROWED_NUM, 0);
        double totalPrice = getIntent().getDoubleExtra(BOOK_TOTAL_PRICE, 0);
        double occupyDeposit = getIntent().getDoubleExtra(OCCUPY_DEPOSIT, 0);

        if (borrowedNum > 0) {
            setBorrowableBookSum(usableBorrowNum);
            setPenaltyOrDepositInfo("可用押金 " + MoneyUtils.formatMoney(usableDeposit));
//            if (needDeposit) {
//
//            } else {
//                setPenaltyOrDepositInfo("欠逾期罚金 " + MoneyUtils.formatMoney(penalty));
//            }

            updateBookTotalInfo("数量 " + borrowedNum,
                    "金额 " + MoneyUtils.formatMoney(totalPrice),
                    "押金 " + MoneyUtils.formatMoney(occupyDeposit));
        }
//
//        mPresenter.checkReaderAndBookInfo();
    }

    @Override
    public void configViews() {
        mSelfBookCameraPreview.bindActivity(this);
        mSelfBookCameraPreview.setScanCallback(new ScanCallback() {
            @Override
            public void onScanResult(String content) {
                if (!TextUtils.isEmpty(content)) {
                    if (mFromType == 0) {//进入图书列表
                        SelfHelpBorrowBooksActivity.startActivity(SelfHelpBorrowBooksScanningActivity.this, content);
                    } else if (mFromType == 1) {
                        Intent data = new Intent();
                        data.putExtra(BAR_NUMBER, content);
                        setResult(RESULT_OK, data);
                    }
                    finish();
                }
            }
        });
    }

    public void updateBookTotalInfo(String sum, String money, String deposit) {
        mBorrowBookSumTv.setText(sum);
        mSelfBookMoneyTv.setText(money);
        mSelfBookTakeDepositTv.setText(deposit);
//        mSelfBookTakeDepositTv.setVisibility(View.VISIBLE);
    }

    public void setPenaltyOrDepositInfo(String moneyInfo) {
        mSelfBookDepositTv.setText(moneyInfo);
    }

    public void setBorrowableBookSum(int bookSum) {
        mSelfBookCanBorrowTv.setText(getString(R.string.can_borrow_book_sum, bookSum));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginOut(AccountMessage loginMessage) {
        if (loginMessage.mIsLoginOut) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
        mSelfBookCameraPreview.openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
        mSelfBookCameraPreview.releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
