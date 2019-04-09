package com.tzpt.cloudlibrary.ui.account.selfhelp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.business_bean.SelfHelpBookInfoBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.deposit.PayDepositActivity;
import com.tzpt.cloudlibrary.ui.account.deposit.PayPenaltyActivity;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.PermissionsDialogFragment;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 自助借书
 */
public class SelfHelpBorrowBooksActivity extends BaseActivity implements
        SelfHelpBorrowBooksContract.View {
    private static final String BAR_NUMBER = "bar_number";
    private static final int REQUEST_CODE_SCANNER = 1000;
    private static final int REQUEST_CODE_PAY = REQUEST_CODE_SCANNER + 1;
    private static final int REQUEST_CODE_DEAL_PENALTY = REQUEST_CODE_PAY + 1;

    public static void startActivity(Context context, String barNumber) {
        Intent intent = new Intent(context, SelfHelpBorrowBooksActivity.class);
        intent.putExtra(BAR_NUMBER, barNumber);
        context.startActivity(intent);
    }

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.self_book_sum_tv)
    TextView mSelfBookSumTv;
    @BindView(R.id.self_book_money_tv)
    TextView mSelfBookMoneyTv;
    @BindView(R.id.self_book_scanner_btn)
    Button mSelfBookScannerBtn;
    @BindView(R.id.self_book_take_deposit_tv)
    TextView mSelfBookTakeDepositTv;
    @BindView(R.id.book_operation_tv)
    TextView mSelfBookOperationTv;
    @BindView(R.id.self_book_total_layout)
    LinearLayout mSelfBookTotalLayout;
    @BindView(R.id.progress_layout)
    LoadingProgressView mProgressLayout;
    @BindView(R.id.self_book_confirm_btn)
    Button mSelfBookConfirmBtn;

    private SelfHelpBorrowBookAdapter mAdapter;
    private SelfHelpBorrowBooksPresenter mPresenter;
    private boolean mIsBorrowResultStatus = false;
    private String mBarNumber;

    @OnClick({R.id.titlebar_left_btn, R.id.self_book_confirm_btn, R.id.self_book_scanner_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                showExitTip();
                break;
            case R.id.self_book_confirm_btn:
                if (mIsBorrowResultStatus) {
                    //借书结果，有未接图书->退出
                    finish();
                } else {
                    //退出
                    if (mAdapter.getCount() == 0) {
                        finish();
                        return;
                    }
                    //如果当前不是提交借书结果状态，则点击提交借书
                    mPresenter.submitBorrowBookList();
                }
                break;
            case R.id.self_book_scanner_btn:
                initCameraPermission();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_self_help_borrow_books;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("自助借书");
    }

    @Override
    public void initDatas() {
        mPresenter = new SelfHelpBorrowBooksPresenter();
        mPresenter.attachView(this);
        //注册事件
        EventBus.getDefault().register(this);
        //如果扫描了条码，则请求图书信息
        mBarNumber = getIntent().getStringExtra(BAR_NUMBER);
        if (!TextUtils.isEmpty(mBarNumber)) {
            mSelfBookScannerBtn.setText("继续扫码");
            mPresenter.getBookInfo(mBarNumber);
        } else {
            mSelfBookScannerBtn.setText("点击扫码");
        }
    }

    @Override
    public void configViews() {
        mAdapter = new SelfHelpBorrowBookAdapter(this, mDelClickListener);
        mAdapter.setEditStatus(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_rv_vertical_two));
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setAdapter(mAdapter);
    }

    View.OnClickListener mDelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            mPresenter.removeDataByIndex(position);
        }
    };

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SCANNER://扫描后获取书籍信息
                    if (data != null) {
                        String barNumber = data.getStringExtra(BAR_NUMBER);
                        mPresenter.getBookInfo(barNumber);
                        mSelfBookScannerBtn.setText("继续扫码");
                    }
                    break;
                case REQUEST_CODE_PAY://支付成功后请求可用押金信息
                    mPresenter.getReaderDeposit(null);
                    break;
                case REQUEST_CODE_DEAL_PENALTY:
                    mPresenter.getBookInfo(mBarNumber);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter.getCount() > 0) {
            mAdapter.clear();
        }
        if (null != mPresenter) {
            mPresenter.detachView();
            mPresenter = null;
        }

        //取消事件订阅
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void showProgressDialog() {
        mProgressLayout.showProgressLayout();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressLayout.hideProgressLayout();
    }

    @Override
    public void setSelfBookList(List<SelfHelpBookInfoBean> bookList) {
        mAdapter.clear();
        mAdapter.addAll(bookList);
        mSelfBookConfirmBtn.setText("确定");
    }

    @Override
    public void setSelfBookEmpty() {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();

        mSelfBookTotalLayout.setVisibility(View.GONE);
        mSelfBookConfirmBtn.setText("退出");
    }

    @Override
    public void showBookTotalInfo(String sum, String money, String deposit, boolean isShowDeposit) {
        mSelfBookTakeDepositTv.setVisibility(isShowDeposit ? View.VISIBLE : View.GONE);
        mSelfBookTotalLayout.setVisibility(View.VISIBLE);
        mSelfBookSumTv.setText(sum);
        mSelfBookMoneyTv.setText(money);
        mSelfBookTakeDepositTv.setText(deposit);
    }

    @Override
    public void showDialogTips(String tips, final boolean finish) {
        if (TextUtils.isEmpty(tips)) {
            return;
        }
        //自定义对话框
        final CustomDialog customDialog = new CustomDialog(this,
                R.style.DialogTheme, tips);
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(false);
        customDialog.setButtonTextConfirmOrYes(true);
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
                if (finish){
                    finish();
                }
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
            }
        });
        customDialog.setText(tips);
        customDialog.show();
    }

    @Override
    public void showDialogTips(int tipsId) {
        showDialogTips(getString(tipsId), false);
    }

    @Override
    public void borrowBooksSuccess() {
        //自定义对话框
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.success = true;
        EventBus.getDefault().post(accountMessage);

        final CustomDialog customDialog = new CustomDialog(this,
                R.style.DialogTheme, "");
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(true);
        customDialog.setButtonTextConfirmOrYes(false);
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
                setSelfBookEmpty();
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
                finish();
            }
        });
        customDialog.setText("借书成功！\n是否继续借书？");
        customDialog.show();
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
    }

    @Override
    public void showPenaltyDialogTips(String penaltyInfo, final String libCode, final boolean finish) {
        //自定义对话框
        final CustomDialog customDialog = new CustomDialog(this,
                R.style.DialogTheme, penaltyInfo);
        customDialog.setCancelable(false);
        if (finish) {
            customDialog.hasNoCancel(false);
        } else {
            customDialog.hasNoCancel(true);
        }
        customDialog.setButtonTextConfirmOrYes(true);
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
                if (finish) {
                    finish();//只能使用馆押金处理罚金，关闭界面
                } else {
                    PayPenaltyActivity.startActivityForResult(SelfHelpBorrowBooksActivity.this, libCode, REQUEST_CODE_DEAL_PENALTY);
                }
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
                finish();
            }
        });
        customDialog.setTextForHtml(penaltyInfo);
        customDialog.show();
    }

    @Override
    public void showIdCardRegisterInfoDialog(int resId) {
        final CustomDialog customDialog = new CustomDialog(this,
                R.style.DialogTheme, getString(resId));
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(false);
        customDialog.setButtonTextConfirmOrYes(true);
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }

    @Override
    public void showRechargeDialog(int msgId) {
        final CustomDialog customDialog = new CustomDialog(this,
                R.style.DialogTheme, getString(msgId));
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(true);
        customDialog.setOkText("充值");
        customDialog.setCancelText("取消");
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
                PayDepositActivity.startActivityForResult(SelfHelpBorrowBooksActivity.this, REQUEST_CODE_PAY);
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }

    //借书成功，刷新所有图书书籍
    @Override
    public void borrowSuccessRefreshBookList(List<SelfHelpBookInfoBean> bookInfoBeanList, int successBookSum, int errorBookSum) {
        mSelfBookOperationTv.setText("状态");
        mIsBorrowResultStatus = true;
        mAdapter.setEditStatus(false);
        mAdapter.clear();
        mAdapter.addAll(bookInfoBeanList);
        //设置借书合计结果
        mSelfBookTotalLayout.setVisibility(View.VISIBLE);
        if (successBookSum > 0) {
            mSelfBookSumTv.setText(getString(R.string.book_status_success_sum, successBookSum));
        } else {
            mSelfBookSumTv.setText("");
        }
        if (errorBookSum > 0) {
            mSelfBookMoneyTv.setText(getString(R.string.book_status_failure_sum, errorBookSum));
        } else {
            mSelfBookMoneyTv.setText("");
        }
        mSelfBookTakeDepositTv.setText("");
        //隐藏扫描控件
        mSelfBookScannerBtn.setVisibility(View.GONE);
        mSelfBookConfirmBtn.setText("退出");

        //自定义对话框
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.success = true;
        EventBus.getDefault().post(accountMessage);

    }

    /**
     * 有逾期未还书籍，不可借书，点击退出
     */
    @Override
    public void showHasOverdueBookTipsDialog() {
        final CustomDialog customDialog = new CustomDialog(this,
                R.style.DialogTheme, "存在逾期未还图书，\n无法继续借书！");
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
        customDialog.show();
    }

    @Override
    public void turnToScan(int usableBorrowNum, boolean needDeposit, double penalty, double usableDeposit,
                           int borrowedNum, double totalPrice, double occupyDeposit) {
        SelfHelpBorrowBooksScanningActivity.startActivityForResult(SelfHelpBorrowBooksActivity.this,
                usableBorrowNum, needDeposit, penalty, usableDeposit, borrowedNum, totalPrice, occupyDeposit,
                REQUEST_CODE_SCANNER);
    }

    //初始化摄像头权限
    private void initCameraPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            mPresenter.getBorrowInfoTurnToScan();
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(Manifest.permission.CAMERA)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {//权限已授权
                            mPresenter.getBorrowInfoTurnToScan();
                        } else if (permission.shouldShowRequestPermissionRationale) {//下次继续提示(应该显示请求许可理由)

                        } else {//没有权限,不能使用权限模块-去设置权限
                            showCameraPermissionPopUpWindow();
                        }
                    }
                });
    }

    PermissionsDialogFragment mSetCameraDialogFragment;

    //展示设置权限弹窗
    private void showCameraPermissionPopUpWindow() {
        if (null == mSetCameraDialogFragment) {
            mSetCameraDialogFragment = new PermissionsDialogFragment();
        }
        if (mSetCameraDialogFragment.isAdded()) {
            return;
        }
        mSetCameraDialogFragment.initPermissionUI(PermissionsDialogFragment.PERMISSION_CAMERA);
        mSetCameraDialogFragment.show(getFragmentManager(), "PermissionsDialogFragment");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recviceLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 如果按了返回键
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            showExitTip();
        }
        return false;
    }

    private void showExitTip() {
        if (!mIsBorrowResultStatus && mAdapter.getCount() > 0) {
            //自定义对话框
            final CustomDialog customDialog = new CustomDialog(this, R.style.DialogTheme);
            customDialog.setCancelable(false);
            customDialog.hasNoCancel(true);
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
            customDialog.setText("借书未完成，确定是否退出？");
            customDialog.show();
        } else {
            finish();
        }
    }
}
