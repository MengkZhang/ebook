package com.tzpt.cloundlibrary.manager.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
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
import com.tzpt.cloundlibrary.manager.ui.adapter.BookListAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.BorrowBookManagementContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.BorrowBookManagementPresenter;
import com.tzpt.cloundlibrary.manager.utils.KeyboardUtils;
import com.tzpt.cloundlibrary.manager.utils.ToastUtils;
import com.tzpt.cloundlibrary.manager.widget.TitleBarView;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 借书管理界面
 * Created by Administrator on 2017/7/4.
 */
public class BorrowBookManagementActivity extends BaseActivity implements BorrowBookManagementContract.View {
    private static final String ID_CARD_BEAN_ID = "id_card_barn_id";

    public static void startActivity(Context context, String readerId) {
        Intent intent = new Intent(context, BorrowBookManagementActivity.class);
        intent.putExtra(ID_CARD_BEAN_ID, readerId);
        context.startActivity(intent);
    }

    @BindView(R.id.borrow_book_title)
    TitleBarView mBorrowBookTitle;
    @BindView(R.id.reader_name_number_tv)
    TextView mReaderNameTv;
    @BindView(R.id.borrowable_sum_tv)
    TextView mBorrowableSumTv;
    @BindView(R.id.usable_deposit_tv)
    TextView mUsableDepositTv;
    @BindView(R.id.edit_code_et)
    EditText mEditCodeEt;
    @BindView(R.id.borrow_book_list_rv)
    RecyclerView mBorrowBookListRv;
    @BindView(R.id.book_sum_tv)
    TextView mBookSumTv;
    @BindView(R.id.book_price_tv)
    TextView mBookPriceTv;
    @BindView(R.id.book_penalty_tv)
    TextView mBookPenaltyTv;
    @BindView(R.id.borrow_book_list_rl)
    RelativeLayout mBorrowBookListRl;
    @BindView(R.id.borrow_book_root_rl)
    RelativeLayout mBorrowBookRootRl;

    private BorrowBookManagementPresenter mPresenter;

    private BookListAdapter mAdapter;
    private List<BookInfoBean> mBookList = new ArrayList<>();
    private String mReaderId;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            mPresenter.removeBook(mBookList.get(position));
            mBookList.remove(position);
            mAdapter.notifyDataSetChanged();
        }
    };

    @OnClick({R.id.scan_code_btn, R.id.post_btn, R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.scan_code_btn:
                initCameraPermission();
                break;
            case R.id.post_btn:
                mPresenter.borrowBook();
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_borrow_book_management;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        mReaderId = getIntent().getStringExtra(ID_CARD_BEAN_ID);
        mPresenter = new BorrowBookManagementPresenter();
        mPresenter.attachView(this);
        mPresenter.getReaderInfo(mReaderId);
    }

    @Override
    public void configViews() {
        mBorrowBookTitle.setTitle("借书管理");
        mBorrowBookTitle.setLeftBtnIcon(R.mipmap.ic_arrow_left);

        mEditCodeEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                KeyboardUtils.hideSoftInput(mEditCodeEt);
                String barNumber = mEditCodeEt.getText().toString().trim();
                mPresenter.getBookInfo(barNumber);
                return false;
            }
        });

        mBorrowBookListRv.setHasFixedSize(true);
        mBorrowBookListRv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BookListAdapter(this, mBookList, mOnClickListener);
        mBorrowBookListRv.setAdapter(mAdapter);
    }

    @Override
    public void showProgressLoading() {
        showDialog(getString(R.string.loading));
    }

    @Override
    public void dismissProgressLoading() {
        dismissDialog();
    }

    @Override
    public void setReaderNameNumber(String info) {
        mReaderNameTv.setText(info);
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
    public void showDialogGetReaderInfoFailed(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.hasNoCancel(false);
        dialog.setCancelable(false);
        dialog.setButtonTextConfirmOrYes2(true);
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
    public void showCancelableDialog(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
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
    public void setBook(BookInfoBean book) {
        mBookList.add(0, book);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setTotalInfo(String sumInfo, String moneyInfo) {
        mBookSumTv.setText(sumInfo);
        mBookPriceTv.setText(moneyInfo);
        mBookPenaltyTv.setVisibility(View.GONE);
    }

    @Override
    public void setTotalInfoDeposit(String sumInfo, String moneyInfo, String depositMoneyInfo) {
        mBookSumTv.setText(sumInfo);
        mBookPriceTv.setText(moneyInfo);
        mBookPenaltyTv.setVisibility(View.VISIBLE);
        mBookPenaltyTv.setText(depositMoneyInfo);
    }

    @Override
    public void showToastTip(int msgId) {
        ToastUtils.showSingleToast(getString(msgId));
    }

    @Override
    public void setBookListVisibility(int visibility) {
        mBorrowBookListRl.setVisibility(visibility);
    }

    @Override
    public void showDepositDeficiencyDialog() {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, "可用押金不足！");
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.setButtonTextConfirmOrYes2(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mPresenter.getReaderInfo(mReaderId);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void showBookLockedDialog(String msg) {
        final CustomDialog deleteDialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        deleteDialog.setCancelable(false);
        deleteDialog.hasNoCancel(false);
        deleteDialog.setButtonTextConfirmOrYesDelete();
        deleteDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                deleteDialog.dismiss();
            }

            @Override
            public void onClickCancel() {
            }
        });
        deleteDialog.show();
    }

    @Override
    public void refreshBookList(List<BookInfoBean> list) {
        mBookList.clear();
        mBookList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void clearBookList() {
        mBookList.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void borrowBookSuccess() {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, "借书成功！");
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
    public void setNoLoginPermission(int kickedOffline) {
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
                LoginActivity.startActivity(BorrowBookManagementActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void showOverdueNumTipDialog() {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, "存在逾期未还图书，\n无法继续借书！");
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
    public void turnToScanActivity(ReaderInfo readerInfo) {
        BorrowBookScanActivity.startActivityForResult(this, readerInfo, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1001) {
            mPresenter.getBookListFromScan();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBookList.clear();
        mAdapter.clear();
        mPresenter.delReaderInfo();
        mPresenter.delOrderNumber();
        mPresenter.clearBookList();
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
