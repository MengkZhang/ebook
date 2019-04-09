package com.tzpt.cloundlibrary.manager.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tzpt.cloudlibrary.camera.ScanCallback;
import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.permissions.Permission;
import com.tzpt.cloundlibrary.manager.permissions.PermissionsDialogFragment;
import com.tzpt.cloundlibrary.manager.permissions.RxPermissions;
import com.tzpt.cloundlibrary.manager.ui.contract.EntranceGuardScanContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.EntranceGuardScanPresenter;
import com.tzpt.cloundlibrary.manager.utils.SoundManager;
import com.tzpt.cloundlibrary.manager.widget.TitleBarView;
import com.tzpt.cloundlibrary.manager.widget.camera.ScanWrapper;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 门禁检查扫描界面
 * Created by Administrator on 2017/7/12.
 */

public class EntranceGuardScanActivity extends BaseActivity implements EntranceGuardScanContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, EntranceGuardScanActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.entrance_guard_scan_wrapper)
    ScanWrapper mEntranceGuardScanWrapper;
    @BindView(R.id.entrance_guard_scan_title)
    TitleBarView mEntranceGuardScanTitle;
    @BindView(R.id.reader_name_number_tv)
    TextView mReaderNameNumberTv;
    @BindView(R.id.borrowable_sum_tv)
    TextView mBorrowableSumTv;
    @BindView(R.id.usable_deposit_tv)
    TextView mUsableDepositTv;
    @BindView(R.id.flag_iv)
    ImageView mFlagIv;
    @BindView(R.id.scan_book_status)
    TextView mStatusTv;

    private EntranceGuardScanPresenter mPresenter;
    private SoundManager mSoundManager;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                setDefaultUI();
                mEntranceGuardScanWrapper.startPreviewScan();
            }
        }
    };


    @OnClick({R.id.light_btn, R.id.return_list_btn, R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.light_btn:
                mEntranceGuardScanWrapper.turnLight();
                break;
            case R.id.return_list_btn:
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_entrance_guard_scan;
    }

    @Override
    public void initToolBar() {
        mSoundManager = new SoundManager();
        mSoundManager.initSound(this);
    }

    @Override
    public void initDatas() {
        mPresenter = new EntranceGuardScanPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {
        mEntranceGuardScanTitle.setTitle("门禁检查");
        mEntranceGuardScanTitle.setLeftBtnIcon(R.mipmap.ic_arrow_left);

        mEntranceGuardScanWrapper.setScanCallback(new ScanCallback() {
            @Override
            public void onScanResult(String content) {
//                if (mSoundManager != null) {
//                    mSoundManager.startPlaySound(EntranceGuardScanActivity.this);
//                    mSoundManager.stopPlaySound(EntranceGuardScanActivity.this);
//                }
                mEntranceGuardScanWrapper.pausePreviewScan();
                if (!TextUtils.isEmpty(content)) {
                    mPresenter.entranceCheck(content);
                } else {
                    setEntranceStateError(R.string.bar_code_error);
                }
            }
        });
//        mEntranceGuardScanCameraPreview.setBarCodeListener(new CameraPreview.CallbackBarCode() {
//            @Override
//            public void callbackBarCode(String barCode) {
//                mPresenter.entranceCheck(barCode);
//            }
//
//            @Override
//            public void callbackCheckCameraPermission() {
//                startCameraPreView();
//            }
//        });
    }

    @Override
    public void setReaderName(String name) {
        mReaderNameNumberTv.setText(name);
    }

    @Override
    public void setBorrowableSum(String sum) {
        mBorrowableSumTv.setText(sum);
    }

    @Override
    public void setDepositOrPenalty(String info) {
        mUsableDepositTv.setText(info);
    }

    @Override
    public void setEntranceStatePass() {
        if (mSoundManager != null) {
            mSoundManager.startPlaySoundForEntrance(this, 0);
            mSoundManager.stopPlaySoundForEntrance(this, 0);
        }

        mFlagIv.setImageResource(R.mipmap.ic_entrance_pass);
        mFlagIv.setVisibility(View.VISIBLE);
        mStatusTv.setText("通过！");
        mStatusTv.setTextColor(Color.parseColor("#20A920"));

        resetToDefaultUI();
    }

    @Override
    public void setEntranceStateError(int msgId) {
        if (mSoundManager != null) {
            mSoundManager.startPlaySoundForEntrance(this, 1);
            mSoundManager.stopPlaySoundForEntrance(this, 1);
        }

        mFlagIv.setImageResource(R.mipmap.ic_entrance_error);
        mFlagIv.setVisibility(View.VISIBLE);
        mStatusTv.setText(msgId);
        mStatusTv.setTextColor(Color.parseColor("#ff0000"));

        resetToDefaultUI();
    }

    @Override
    public void setEntranceStateLost(String readerInfo, String phone) {
        if (mSoundManager != null) {
            mSoundManager.startPlaySoundForEntrance(this, 2);
            mSoundManager.stopPlaySoundForEntrance(this, 2);
        }

        setDefaultUI();
        String lostLocalInfo = "办借已超3小时，请核实身份:";
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, "");
        dialog.hasNoCancel(false);
        dialog.setCancelable(false);
        dialog.setTextColor(Color.parseColor("#ff0000"));
        dialog.setText(Html.fromHtml("<html>" + lostLocalInfo + "<font color='#333333'><br>" + readerInfo + "<br>" + phone + "</font></htm>"));
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mEntranceGuardScanWrapper.startPreviewScan();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void showMsgDialog(int resId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(resId));
        dialog.hasNoCancel(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mEntranceGuardScanWrapper.startPreviewScan();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void noPermissionPrompt(int resId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(resId));
        dialog.hasNoCancel(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                LoginActivity.startActivity(EntranceGuardScanActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    private void setDefaultUI() {
        mReaderNameNumberTv.setText("");
        mBorrowableSumTv.setText("");
        mUsableDepositTv.setText("");
        mStatusTv.setText("");
        mFlagIv.setVisibility(View.GONE);
    }

    private void resetToDefaultUI() {
        mHandler.removeMessages(1000);
        mHandler.sendEmptyMessageDelayed(1000, 1200);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraPreView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEntranceGuardScanWrapper.releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(1000);
        mPresenter.detachView();
    }

    //开启预览
    private void startCameraPreView() {
        if (Build.VERSION.SDK_INT < 23) {
            if (null != mEntranceGuardScanWrapper) {
                mEntranceGuardScanWrapper.openCamera();
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
                            if (null != mEntranceGuardScanWrapper) {
                                mEntranceGuardScanWrapper.openCamera();
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
