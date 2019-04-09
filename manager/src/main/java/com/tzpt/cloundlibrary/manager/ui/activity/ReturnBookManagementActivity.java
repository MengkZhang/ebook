package com.tzpt.cloundlibrary.manager.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.ReaderInfo;
import com.tzpt.cloundlibrary.manager.permissions.Permission;
import com.tzpt.cloundlibrary.manager.permissions.PermissionsDialogFragment;
import com.tzpt.cloundlibrary.manager.permissions.RxPermissions;
import com.tzpt.cloundlibrary.manager.ui.adapter.ReturnBookListAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.ReturnBookManagementContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.ReturnBookManagementPresenter;
import com.tzpt.cloundlibrary.manager.utils.KeyboardUtils;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.widget.TitleBarView;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 还书管理界面
 * Created by Administrator on 2017/7/10.
 */
public class ReturnBookManagementActivity extends BaseActivity implements ReturnBookManagementContract.View {
    private static final int REQUEST_CODE_SCAN = 1001;
    private static final int REQUEST_CODE_DEAL_PENALTY = REQUEST_CODE_SCAN + 1;

    private static final String READER_INFO = "reader_info";

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ReturnBookManagementActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.return_book_title)
    TitleBarView mReturnBookTitle;
    @BindView(R.id.reader_name_number_tv)
    TextView mReaderNameNumberTv;
    @BindView(R.id.borrowable_sum_tv)
    TextView mBorrowableSumTv;
    @BindView(R.id.usable_deposit_tv)
    TextView mUsableDepositTv;
    @BindView(R.id.edit_code_et)
    EditText mEditCodeEt;
    @BindView(R.id.return_book_list_rv)
    RecyclerView mReturnBookListRv;
    @BindView(R.id.book_sum_tv)
    TextView mBookSumTv;
    @BindView(R.id.book_price_tv)
    TextView mBookPriceTv;
    @BindView(R.id.book_penalty_tv)
    TextView mBookPenaltyTv;
    @BindView(R.id.book_under_penalty_tv)
    TextView mBookUnderPenaltyTv;
    @BindView(R.id.return_book_list_rl)
    RelativeLayout mReturnBookListRl;
    @BindView(R.id.penalty_or_exit_btn)
    Button mPenaltyOrExitBtn;

    private ReturnBookManagementPresenter mPresenter;

    private ReturnBookListAdapter mAdapter;
    private List<BookInfoBean> mBookList = new ArrayList<>();
    private static final String FINISH_RETURN_BOOK_FLAG = "exit_return_book";

    @OnClick({R.id.scan_code_btn, R.id.lost_book_btn, R.id.penalty_or_exit_btn, R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.scan_code_btn:
                initCameraPermission();
                break;
            case R.id.lost_book_btn:
                ReaderLoginActivity.startActivity(this, 1);
                finish();
                break;
            case R.id.penalty_or_exit_btn:
                mPresenter.clickRightBtn();
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_return_book_management;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        mPresenter = new ReturnBookManagementPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {
        mReturnBookTitle.setTitle("还书管理");
        mReturnBookTitle.setLeftBtnIcon(R.mipmap.ic_arrow_left);

        mEditCodeEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                KeyboardUtils.hideSoftInput(mEditCodeEt);
                String barNumber = mEditCodeEt.getText().toString().trim();
                mPresenter.returnBook(barNumber);
                return false;
            }
        });

        mReturnBookListRv.setHasFixedSize(true);
        mReturnBookListRv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ReturnBookListAdapter(this, mBookList);
        mReturnBookListRv.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void showProgressLoading() {
        showDialog(getString(R.string.loading));
    }

    @Override
    public void dismissProgressLoading() {
        dismissDialog();
    }

//    @Override
//    public void setReturnDepositBtnVisibility(int visibility) {
////        mReturnDepositBtn.setVisibility(visibility);
//    }

    @Override
    public void setReturnDepositBtnText(String string) {
//        mReturnDepositBtn.setText(string);
    }

    @Override
    public void setBarEditTextHint(String hint) {
        mEditCodeEt.setHint(hint);
    }

    @Override
    public void showCancelableDialog(String msg) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        dialog.hasNoCancel(false);
        dialog.setCancelable(false);
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
        dialog.show();
    }

    @Override
    public void showCancelableDialog(int msgId) {
        showCancelableDialog(getString(msgId));
    }

    @Override
    public void showNoPermissionDialog(int kickOut) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(kickOut));
        dialog.hasNoCancel(false);
        dialog.setCancelable(false);
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                LoginActivity.startActivity(ReturnBookManagementActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void showMsgDialog(int msgId) {
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
    public void setReaderNameNumber(String info) {
        mReaderNameNumberTv.setText(info);
    }

    @Override
    public void setNoBackBookSum(String info) {
        mBorrowableSumTv.setText(info);
    }

    @Override
    public void setDepositOrPenalty(String info) {
        mUsableDepositTv.setText(info);
    }

    @Override
    public void setTotalInfoDeposit(String size, String totalMoney, String penalty, double underPenalty) {
        mBookSumTv.setText(size);
        mBookPriceTv.setText(totalMoney);
        mBookPenaltyTv.setText(penalty);
        mBookUnderPenaltyTv.setText(getString(R.string.total_penalty, MoneyUtils.formatMoney(underPenalty)));
        if (underPenalty > 0) {
            mPenaltyOrExitBtn.setText("罚金处理");
        } else {
            mPenaltyOrExitBtn.setText("退出");
        }
    }

    @Override
    public void setBookListVisibility(int visibility) {
        mBookSumTv.setVisibility(visibility);
        mBookPriceTv.setVisibility(visibility);
        mBookPenaltyTv.setVisibility(visibility);
        mReturnBookListRl.setVisibility(visibility);
        mBookUnderPenaltyTv.setVisibility(visibility);
    }

    @Override
    public void setBookTotalVisibility(int visibility) {
        mBookSumTv.setVisibility(visibility);
        mBookPriceTv.setVisibility(visibility);
        mBookPenaltyTv.setVisibility(visibility);
        mBookUnderPenaltyTv.setVisibility(visibility);
    }

    @Override
    public void refreshBookList(List<BookInfoBean> list) {
        mBookList.clear();
        mBookList.addAll(list);
        mAdapter.notifyDataSetChanged();
        mReturnBookListRv.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDialogGetReaderInfoFailed(String msg) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        dialog.hasNoCancel(true);
        dialog.setCancelable(false);
        dialog.setButtonTextConfirmOrYes2(false);
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mPresenter.refreshReaderInfo();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void exitReturnBook() {
        finish();
    }

    @Override
    public void turnToDealPenalty(String readerId) {
        PenaltyDealActivity.startActivityForResult(this, readerId, REQUEST_CODE_DEAL_PENALTY);
    }

    @Override
    public void turnToScanActivity(ReaderInfo readerInfo) {
        ReturnBookScanActivity.startActivityForResult(this, readerInfo, REQUEST_CODE_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            boolean exitReturnBook = data.getBooleanExtra(FINISH_RETURN_BOOK_FLAG, false);
            if (exitReturnBook) {
                this.finish();
            }
            ReaderInfo readerInfo = (ReaderInfo) data.getSerializableExtra(READER_INFO);
            if (readerInfo != null) {
                mPresenter.getReturnBookList(readerInfo);
            }
        } else if (requestCode == REQUEST_CODE_DEAL_PENALTY && resultCode == RESULT_OK) {
            mPresenter.refreshReaderInfo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.clearBookList();
        mPresenter.delOrderNumber();
        mPresenter.detachView();
    }


    //初始化摄像头权限
    private void initCameraPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            mPresenter.clickScanBtn();
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(Manifest.permission.CAMERA)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {//权限已授权
                            mPresenter.clickScanBtn();
                        } else if (permission.shouldShowRequestPermissionRationale) {//下次继续提示(应该显示请求许可理由)

                        } else {//没有权限,不能使用权限模块-去设置权限
                            showCameraPermissionPopUpWindow();
                        }
                    }
                });
    }

    private PermissionsDialogFragment mSetCameraDialogFragment;

    //展示设置权限弹窗
    private void showCameraPermissionPopUpWindow() {
        if (null == mSetCameraDialogFragment) {
            mSetCameraDialogFragment = new PermissionsDialogFragment();
        }
        if (mSetCameraDialogFragment.isAdded()) {
            return;
        }
        mSetCameraDialogFragment.initPermissionUI(PermissionsDialogFragment.PERMISSION_CAMERA);
        mSetCameraDialogFragment.show(this.getFragmentManager(), "PermissionsDialogFragment");
    }
}
