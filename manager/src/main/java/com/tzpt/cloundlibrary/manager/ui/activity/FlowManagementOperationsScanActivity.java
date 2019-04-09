package com.tzpt.cloundlibrary.manager.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloudlibrary.camera.ScanCallback;
import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.permissions.Permission;
import com.tzpt.cloundlibrary.manager.permissions.PermissionsDialogFragment;
import com.tzpt.cloundlibrary.manager.permissions.RxPermissions;
import com.tzpt.cloundlibrary.manager.ui.contract.FlowManagementOperationsScanContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.FlowManagementOperationsScanPresenter;
import com.tzpt.cloundlibrary.manager.utils.SoundManager;
import com.tzpt.cloundlibrary.manager.widget.TitleBarView;
import com.tzpt.cloundlibrary.manager.widget.camera.ScanWrapper;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 流出录入扫描界面
 * Created by Administrator on 2017/7/15.
 */
public class FlowManagementOperationsScanActivity extends BaseActivity implements FlowManagementOperationsScanContract.View {

    @BindView(R.id.flow_operation_scan_wrapper)
    ScanWrapper mFlowOperationScanWrapper;
    @BindView(R.id.flow_operation_scan_title)
    TitleBarView mFlowOperationScanTitle;
    @BindView(R.id.flow_header_library_info_tv)
    TextView mFlowHeaderLibraryInfoTv;
    @BindView(R.id.flow_header_user_info_tv)
    TextView mFlowHeaderUserInfoTv;
    @BindView(R.id.scan_book_status)
    TextView mScanBookStatus;
    @BindView(R.id.scan_book_number)
    TextView mScanBookNumber;
    @BindView(R.id.scan_book_price)
    TextView mScanBookPrice;
    @BindView(R.id.scan_book_deposit)
    TextView mScanBookDeposit;

    private static final String FLOW_OPERATION_FROM_TYPE = "FlowManageListBean_from_type";
    private int mFromType;

    private FlowManagementOperationsScanPresenter mPresenter;

    private SoundManager mSoundManager;

    private static final int UPDATE_MSG = 1000;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_MSG) {
                mScanBookStatus.setText("");
                mFlowOperationScanWrapper.startPreviewScan();
            }
        }
    };

    @OnClick({R.id.light_btn, R.id.return_list_btn, R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.light_btn:
                mFlowOperationScanWrapper.turnLight();
                break;
            case R.id.return_list_btn:
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);//返回上一个界面获取内存数据
        super.finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_flow_management_operation_scan;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        mFromType = getIntent().getIntExtra(FLOW_OPERATION_FROM_TYPE, 0);

        mPresenter = new FlowManagementOperationsScanPresenter();
        mPresenter.attachView(this);
        mPresenter.getHeadInfo();
        mPresenter.getBookList();

        mSoundManager = new SoundManager();
        mSoundManager.initSound(this);
    }

    @Override
    public void configViews() {
        switch (mFromType) {
            case 0:
                mFlowOperationScanTitle.setTitle("流出录入");
                break;
            case 1:
                mFlowOperationScanTitle.setTitle("流出录入");
                break;
            case 2:
                mFlowOperationScanTitle.setTitle("清点删单");
                break;
        }

        mScanBookDeposit.setVisibility(View.GONE);
        mFlowOperationScanTitle.setLeftBtnIcon(R.mipmap.ic_arrow_left);

        mFlowOperationScanWrapper.setScanCallback(new ScanCallback() {
            @Override
            public void onScanResult(String content) {
                if (mSoundManager != null) {
                    mSoundManager.startPlaySound(FlowManagementOperationsScanActivity.this);
                    mSoundManager.stopPlaySound(FlowManagementOperationsScanActivity.this);
                }
                mFlowOperationScanWrapper.pausePreviewScan();
                if (!TextUtils.isEmpty(content)) {
                    mPresenter.operationFlowManageEditValueByFromType(content, mFromType);
                } else {
                    setScanTips(R.string.bar_code_error);
                }
            }
        });
//        mFlowOperationScanCameraPreview.setBarCodeListener(new CameraPreview.CallbackBarCode() {
//            @Override
//            public void callbackBarCode(String barCode) {
//                mPresenter.operationFlowManageEditValueByFromType(barCode, mFromType);
//            }
//
//            @Override
//            public void callbackCheckCameraPermission() {
//                startCameraPreView();
//            }
//
//        });
    }

    @Override
    public void setHeaderLibraryInfo(StringBuilder info) {
        mFlowHeaderLibraryInfoTv.setText(info);
    }

    @Override
    public void setHeaderUserInfo(StringBuilder info) {
        mFlowHeaderUserInfoTv.setText(info);
    }

    @Override
    public void setBookNumber(String info) {
        mScanBookNumber.setText(info);
    }

    @Override
    public void setBookPrice(String info) {
        mScanBookPrice.setText(info);
    }

    @Override
    public void setScanTips(String smg) {
        mScanBookStatus.setText(smg);
        mHandler.sendEmptyMessageDelayed(UPDATE_MSG, 1200);
    }

    @Override
    public void setScanTips(int msgId) {
        setScanTips(getString(msgId));
    }

    @Override
    public void noPermissionPrompt(int kickedOffline) {
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
                LoginActivity.startActivity(FlowManagementOperationsScanActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraPreView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFlowOperationScanWrapper.releaseCamera();
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
            mFlowOperationScanWrapper.openCamera();
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(Manifest.permission.CAMERA)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {//权限已授权
                            mFlowOperationScanWrapper.openCamera();
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
