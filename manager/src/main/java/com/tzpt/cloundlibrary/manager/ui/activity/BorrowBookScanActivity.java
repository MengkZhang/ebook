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
import com.tzpt.cloundlibrary.manager.ui.contract.BorrowBookManagementScanContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.BorrowBookManagementScanPresenter;
import com.tzpt.cloundlibrary.manager.utils.SoundManager;
import com.tzpt.cloundlibrary.manager.widget.TitleBarView;
import com.tzpt.cloundlibrary.manager.widget.camera.ScanWrapper;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 借书扫描界面
 * Created by Administrator on 2017/7/10.
 */

public class BorrowBookScanActivity extends BaseActivity implements BorrowBookManagementScanContract.View {
    private static final String READER_INFO = "reader_info";

    public static void startActivityForResult(Activity activity, ReaderInfo readerInfo, int requestCode) {
        Intent intent = new Intent(activity, BorrowBookScanActivity.class);
        intent.putExtra(READER_INFO, readerInfo);
        activity.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.borrow_book_scan_title)
    TitleBarView mBorrowBookScanTitle;
    @BindView(R.id.reader_name_number_tv)
    TextView mReaderNameNumberTv;
    @BindView(R.id.borrowable_sum_tv)
    TextView mBorrowableSumTv;
    @BindView(R.id.usable_deposit_tv)
    TextView mUsableDepositTv;
    @BindView(R.id.borrow_book_scan_wrapper)
    ScanWrapper mScanWrapper;
    @BindView(R.id.scan_book_status)
    TextView mBorrowBookStatus;
    @BindView(R.id.scan_book_number)
    TextView mBorrowBookNumber;
    @BindView(R.id.scan_book_price)
    TextView mBorrowBookPrice;
    @BindView(R.id.scan_book_deposit)
    TextView mBorrowBookDeposit;

    private ReaderInfo mReaderInfo;
    private BorrowBookManagementScanPresenter mPresenter;

    private SoundManager mSoundManager;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                mBorrowBookStatus.setText("");
                mScanWrapper.startPreviewScan();
            }
        }
    };

    @OnClick({R.id.light_btn, R.id.return_list_btn, R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.light_btn:
                mScanWrapper.turnLight();
                break;
            case R.id.return_list_btn:
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_borrow_book_scan;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        mPresenter = new BorrowBookManagementScanPresenter();
        mPresenter.attachView(this);

        mReaderInfo = (ReaderInfo) getIntent().getSerializableExtra(READER_INFO);
        if (mReaderInfo == null) {
            finish();
        }
        mPresenter.getReaderInfo(mReaderInfo);

        mSoundManager = new SoundManager();
        mSoundManager.initSound(this);
    }

    @Override
    public void configViews() {
        mBorrowBookScanTitle.setTitle("借书管理");
        mBorrowBookScanTitle.setLeftBtnIcon(R.mipmap.ic_arrow_left);

        mScanWrapper.setScanCallback(new ScanCallback() {
            @Override
            public void onScanResult(String content) {
                if (mSoundManager != null) {
                    mSoundManager.startPlaySound(BorrowBookScanActivity.this);
                    mSoundManager.stopPlaySound(BorrowBookScanActivity.this);
                }
                mScanWrapper.pausePreviewScan();
                if (!TextUtils.isEmpty(content)) {
                    mPresenter.getBookInfo(content, mReaderInfo);
                } else {
                    enteringBookTips(R.string.bar_code_error);
                }
            }
        });
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
    public void setTotalInfo(String sumInfo, String moneyInfo) {
        mBorrowBookNumber.setText(sumInfo);
        mBorrowBookPrice.setText(moneyInfo);
        mBorrowBookDeposit.setVisibility(View.GONE);
    }

    @Override
    public void setTotalInfoDeposit(String sumInfo, String moneyInfo, String depositMoneyInfo) {
        mBorrowBookNumber.setText(sumInfo);
        mBorrowBookPrice.setText(moneyInfo);
        mBorrowBookDeposit.setText(depositMoneyInfo);
        mBorrowBookDeposit.setVisibility(View.VISIBLE);
    }

    @Override
    public void enteringBookTips(int msgId) {
        mBorrowBookStatus.setText(msgId);
        mHandler.sendEmptyMessageDelayed(1000, 1200);
        if (TextUtils.isEmpty(getString(msgId))) {
            mPresenter.getReaderInfo(mReaderInfo);//获取最新书籍数量和合计信息
        }
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
                LoginActivity.startActivity(BorrowBookScanActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    //开启预览
    private void startCameraPreView() {
        if (Build.VERSION.SDK_INT < 23) {
            if (null != mScanWrapper) {
                mScanWrapper.openCamera();
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
                            if (null != mScanWrapper) {
                                mScanWrapper.openCamera();
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

    @Override
    protected void onResume() {
        super.onResume();
        startCameraPreView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScanWrapper.releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(1000);
        mPresenter.detachView();
    }
}
