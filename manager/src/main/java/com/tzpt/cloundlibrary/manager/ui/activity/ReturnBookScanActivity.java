package com.tzpt.cloundlibrary.manager.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloudlibrary.camera.ScanCallback;
import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.bean.ReaderInfo;
import com.tzpt.cloundlibrary.manager.permissions.Permission;
import com.tzpt.cloundlibrary.manager.permissions.PermissionsDialogFragment;
import com.tzpt.cloundlibrary.manager.permissions.RxPermissions;
import com.tzpt.cloundlibrary.manager.ui.contract.ReturnBookManagementScanContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.ReturnBookManagementScanPresenter;
import com.tzpt.cloundlibrary.manager.utils.SoundManager;
import com.tzpt.cloundlibrary.manager.widget.TitleBarView;
import com.tzpt.cloundlibrary.manager.widget.camera.ScanWrapper;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 还书扫描界面
 * Created by Administrator on 2017/7/10.
 */

public class ReturnBookScanActivity extends BaseActivity implements ReturnBookManagementScanContract.View {
    private static final String READER_INFO = "reader_info";

    public static void startActivityForResult(Activity activity, ReaderInfo readerInfo, int requestCode) {
        Intent intent = new Intent(activity, ReturnBookScanActivity.class);
        intent.putExtra(READER_INFO, readerInfo);
        activity.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.return_book_scan_wrapper)
    ScanWrapper mReturnBookScanWrapper;
    @BindView(R.id.return_book_scan_title)
    TitleBarView mReturnBookScanTitle;
    @BindView(R.id.reader_name_number_tv)
    TextView mReaderNameNumberTv;
    @BindView(R.id.borrowable_sum_tv)
    TextView mBorrowableSumTv;
    @BindView(R.id.usable_deposit_tv)
    TextView mUsableDepositTv;
    @BindView(R.id.scan_book_status)
    TextView mScanBookStatus;
    @BindView(R.id.scan_book_number)
    TextView mScanBookNumber;
    @BindView(R.id.scan_book_price)
    TextView mScanBookPrice;
    @BindView(R.id.scan_book_deposit)
    TextView mScanBookDeposit;

    private ReturnBookManagementScanPresenter mPresenter;
    private SoundManager mSoundManager;
    private ReaderInfo mReaderInfo;
    private boolean mIsFinishReturnBook;//是否结束还书流程

    private static final String FINISH_RETURN_BOOK_FLAG = "exit_return_book";
    private static final int UPDATE_MSG = 1000;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_MSG) {
                mScanBookStatus.setText("");
                mReturnBookScanWrapper.startPreviewScan();
            }
        }
    };

    @OnClick({R.id.light_btn, R.id.return_list_btn, R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.light_btn:
                mReturnBookScanWrapper.turnLight();
                break;
            case R.id.return_list_btn:
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_return_book_scan;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        mPresenter = new ReturnBookManagementScanPresenter();
        mPresenter.attachView(this);

        mReaderInfo = (ReaderInfo) getIntent().getSerializableExtra(READER_INFO);
        mPresenter.getReturnBookList(mReaderInfo);

        mSoundManager = new SoundManager();
        mSoundManager.initSound(this);
    }

    @Override
    public void configViews() {
        mReturnBookScanTitle.setTitle("还书管理");
        mReturnBookScanTitle.setLeftBtnIcon(R.mipmap.ic_arrow_left);

        mReturnBookScanWrapper.setScanCallback(new ScanCallback() {
            @Override
            public void onScanResult(String content) {
                if (mSoundManager != null) {
                    mSoundManager.startPlaySound(ReturnBookScanActivity.this);
                    mSoundManager.stopPlaySound(ReturnBookScanActivity.this);
                }

                mReturnBookScanWrapper.pausePreviewScan();
                if (!TextUtils.isEmpty(content)) {
                    String readerId = null;
                    if (mReaderInfo != null) {
                        readerId = mReaderInfo.mReaderId;
                    }
                    mPresenter.returnBook(content, readerId);
                } else {
                    showCancelableDialog(R.string.bar_code_error);
                }
            }
        });
    }

    @Override
    public void showCancelableDialog(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.hasNoCancel(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mReturnBookScanWrapper.startPreviewScan();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
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
                LoginActivity.startActivity(ReturnBookScanActivity.this);
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
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mIsFinishReturnBook = true;
                finish();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void showDialogGetReaderInfoFailed(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
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
        mScanBookNumber.setText(size);
        mScanBookPrice.setText(totalMoney);
        mScanBookDeposit.setText(penalty);
    }

    @Override
    public void returnBookSuccess(ReaderInfo readerInfo) {
        mReaderInfo = readerInfo;
        mScanBookStatus.setText("");
        mHandler.sendEmptyMessageDelayed(UPDATE_MSG, 1200);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraPreView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mReturnBookScanWrapper.releaseCamera();
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(FINISH_RETURN_BOOK_FLAG, mIsFinishReturnBook);
        if (mReaderInfo != null) {
            intent.putExtra(READER_INFO, mReaderInfo);
        }
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(UPDATE_MSG);
        mPresenter.detachView();
    }

    //开启预览
    private void startCameraPreView() {
        if (Build.VERSION.SDK_INT < 23) {
            if (null != mReturnBookScanWrapper) {
                mReturnBookScanWrapper.openCamera();
            }
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(Manifest.permission.CAMERA)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {//权限已授权
                            if (null != mReturnBookScanWrapper) {
                                mReturnBookScanWrapper.openCamera();
                            }
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
