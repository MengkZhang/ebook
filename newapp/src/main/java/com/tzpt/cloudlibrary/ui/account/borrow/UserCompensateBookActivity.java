package com.tzpt.cloudlibrary.ui.account.borrow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.business_bean.BorrowBookBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.deposit.PayDepositActivity;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户赔书
 */
public class UserCompensateBookActivity extends BaseActivity implements
        UserCompensateBookContract.View {

    private static final String RESULT_COMPENSATE_SUCCESS = "compensate_success";
    private static final String BORROW_BOOK_ID = "borrow_book_id";
    private static final int REQUEST_CODE_COMPENSATE_BOOK = 1000;
    private static final int BTN_TYPE_CHARGE = 0;       //充值
    private static final int BTN_TYPE_COMPENSATE = 1;   //赔书
    private static final int BTN_TYPE_LEAVE = 2;        //离开
    private int mBtnType = BTN_TYPE_COMPENSATE;

    public static void startActivityForResult(Activity activity, long borrowBookId, int requestCode) {
        Intent intent = new Intent(activity, UserCompensateBookActivity.class);
        intent.putExtra(BORROW_BOOK_ID, borrowBookId);
        activity.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.loading_progress_view)
    LoadingProgressView mLoadingProgressView;
    @BindView(R.id.item_borrow_book_position_tv)
    TextView mPositionTv;
    @BindView(R.id.margin_view)
    View mMarginView;
    @BindView(R.id.item_book_image)
    ImageView mItemBookImageIv;
    @BindView(R.id.item_book_title)
    TextView mItemBookTitleTv;
    @BindView(R.id.item_book_author)
    TextView mItemBookAuthorTv;
    @BindView(R.id.item_book_publishing_company)
    TextView mItemBookPublishingCompanyTv;
    @BindView(R.id.item_book_publishing_year)
    TextView mItemBookPublishingYearTv;
    @BindView(R.id.item_book_isbn)
    TextView mItemBookIsbnTv;
    @BindView(R.id.item_book_category)
    TextView mItemBookTypeTv;
    @BindView(R.id.lost_book_can_use_deposit_tv)
    TextView mBorrowBookCanUseDepositTv;
    @BindView(R.id.lost_book_take_deposit_tv)
    TextView mBorrowBookTakeDepositTv;
    @BindView(R.id.lost_book_price_tv)
    TextView mBorrowBookPriceTv;
    @BindView(R.id.lost_book_money_et)
    EditText mBorrowBookMoneyEt;
    @BindView(R.id.lost_book_psw_et)
    EditText mBorrowBookPswEt;
    @BindView(R.id.lost_book_tip_tv)
    TextView mBorrowBookTipTv;
    @BindView(R.id.lost_book_confirm_btn)
    Button mLostBookConfirmBtn;

    private UserCompensateBookPresenter mPresenter;
    private long mBorrowBookId;
    private double mPrice;

    @OnClick({R.id.titlebar_left_btn, R.id.lost_book_confirm_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.lost_book_confirm_btn:
                KeyboardUtils.hideSoftInput(this);
                switch (mBtnType) {
                    case BTN_TYPE_LEAVE:        //离开
                        finish();
                        break;
                    case BTN_TYPE_CHARGE:       //充值
                        PayDepositActivity.startActivityForResult(this, mPrice, REQUEST_CODE_COMPENSATE_BOOK);
                        break;
                    case BTN_TYPE_COMPENSATE:   //赔书
                        mPresenter.compensateBook(mBorrowBookPswEt.getText().toString().trim(), mBorrowBookId);
                        break;
                }
        }
    }

    private TextWatcher mPswTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                mLostBookConfirmBtn.setClickable(true);
                mLostBookConfirmBtn.setBackgroundResource(R.drawable.btn_login);
            } else {
                mLostBookConfirmBtn.setClickable(false);
                mLostBookConfirmBtn.setBackgroundResource(R.drawable.phone_manage_button_bg);
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_compensate_book;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("赔书");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mPresenter = new UserCompensateBookPresenter();
        mPresenter.attachView(this);

        mBorrowBookId = getIntent().getLongExtra(BORROW_BOOK_ID, -1);
        mPresenter.getBookInfo(mBorrowBookId);

        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        mPositionTv.setVisibility(View.GONE);
        mMarginView.setVisibility(View.VISIBLE);

        mBorrowBookPswEt.addTextChangedListener(mPswTextWatcher);
    }

    @Override
    public void showProgressDialog() {
        mMultiStateLayout.showProgress();
    }

    @Override
    public void postingProgressDialog() {
        mLoadingProgressView.showProgressLayout();
    }

    @Override
    public void dismissProgressDialog() {
        mLoadingProgressView.hideProgressLayout();
    }

    @Override
    public void showNetError() {
        mMultiStateLayout.showRetryError(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getBookInfo(mBorrowBookId);
            }
        });
    }

    //<editor-fold desc="配置赔书回调UI">
    @Override
    public void showErrorMsg(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }

    @Override
    public void showErrorMsgFinish(int msgId) {
        final CustomDialog customDialog = new CustomDialog(this, R.style.DialogTheme);
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(false);
        customDialog.setButtonTextConfirmOrYes(true);
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
                finish();
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
            }
        });
        customDialog.setText(getString(msgId));
        customDialog.show();
    }

    @Override
    public void setContentView() {
        mMultiStateLayout.showContentView();
    }


    @Override
    public void setLostBookInfo(BorrowBookBean bean) {
        mItemBookTitleTv.setText(bean.mBook.mName);
        GlideApp.with(this).load(bean.mBook.mCoverImg).placeholder(R.mipmap.ic_nopicture).error(R.mipmap.ic_nopicture).centerCrop().into(mItemBookImageIv);
        mItemBookAuthorTv.setText(getString(R.string.book_list_author, bean.mAuthor.mName));
        mItemBookIsbnTv.setText(getString(R.string.stay_library2, bean.mLibrary.mName));//所在馆
        mItemBookPublishingCompanyTv.setText(getString(R.string.book_list_publisher, bean.mPress.mName));
        mItemBookPublishingYearTv.setText(getString(R.string.book_list_isbn, bean.mBook.mIsbn));//isbn
        mItemBookTypeTv.setText(getString(R.string.borrow_book_date, bean.mHistoryBorrowDate));//借阅日

    }

    @Override
    public void setDepositInfo(String canUseDeposit, String takeDeposit) {
        mBorrowBookCanUseDepositTv.setText(getString(R.string.can_borrow_book_price, canUseDeposit));
        mBorrowBookTakeDepositTv.setText(getString(R.string.take_deposit, takeDeposit));

    }

    @Override
    public void setCompensatePrice(String compensatePrice) {
        mBorrowBookPriceTv.setText(getString(R.string.compensate_price, compensatePrice));
        mBorrowBookMoneyEt.setText(compensatePrice);
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
        finish();
    }

    //赔书成功
    @Override
    public void compensateBooksSuccess() {
        final CustomDialog customDialog = new CustomDialog(this, R.style.DialogTheme);
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(false);
        customDialog.setButtonTextConfirmOrYes(true);
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra(RESULT_COMPENSATE_SUCCESS, true);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
            }
        });
        customDialog.setText(getString(R.string.lost_book_success));
        customDialog.show();
    }

    //</editor-fold>

    //<editor-fold desc="配置赔书状态UI">
    @Override
    public void setCompensateBookUI() {
        mBorrowBookTipTv.setVisibility(View.GONE);
        mBorrowBookMoneyEt.setVisibility(View.VISIBLE);
        mBorrowBookPswEt.setVisibility(View.VISIBLE);
        mLostBookConfirmBtn.setText("确认扣押金");
        mLostBookConfirmBtn.setBackgroundResource(R.drawable.phone_manage_button_bg);
        mLostBookConfirmBtn.setClickable(false);
        mBtnType = BTN_TYPE_COMPENSATE;
    }

    @Override
    public void setChargeDepositUI(double price) {
        mPrice = price;
        mBorrowBookTipTv.setVisibility(View.VISIBLE);
//        mLostBookInputLl.setVisibility(View.GONE);
        mBorrowBookMoneyEt.setVisibility(View.GONE);
        mBorrowBookPswEt.setVisibility(View.GONE);
        mBorrowBookTipTv.setText(R.string.please_charge);
        mLostBookConfirmBtn.setText("充值");
        mLostBookConfirmBtn.setBackgroundResource(R.drawable.btn_login);
        mLostBookConfirmBtn.setClickable(true);
        mBtnType = BTN_TYPE_CHARGE;
    }

    @Override
    public void setPlatformDepositUI() {
        mBorrowBookTipTv.setVisibility(View.VISIBLE);
        mBorrowBookMoneyEt.setVisibility(View.GONE);
        mBorrowBookPswEt.setVisibility(View.GONE);
        mBorrowBookTipTv.setText(R.string.please_to_platform);
        mLostBookConfirmBtn.setText("确定");
        mLostBookConfirmBtn.setBackgroundResource(R.drawable.btn_login);
        mLostBookConfirmBtn.setClickable(true);
        mBtnType = BTN_TYPE_LEAVE;
    }

    @Override
    public void setLibraryDepositUI() {
        mBorrowBookTipTv.setVisibility(View.VISIBLE);
        mBorrowBookMoneyEt.setVisibility(View.GONE);
        mBorrowBookPswEt.setVisibility(View.GONE);
        mBorrowBookTipTv.setText(R.string.please_to_library);
        mLostBookConfirmBtn.setText("确定");
        mLostBookConfirmBtn.setBackgroundResource(R.drawable.btn_login);
        mLostBookConfirmBtn.setClickable(true);
        mBtnType = BTN_TYPE_LEAVE;
    }

    //</editor-fold>

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_COMPENSATE_BOOK && resultCode == RESULT_OK) {
            mPresenter.getUserDepositInfo(false);//获取用户押金信息
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receviceLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }
}
