package com.tzpt.cloundlibrary.manager.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.bean.IDCardBean;
import com.tzpt.cloundlibrary.manager.permissions.Permission;
import com.tzpt.cloundlibrary.manager.permissions.PermissionsDialogFragment;
import com.tzpt.cloundlibrary.manager.permissions.RxPermissions;
import com.tzpt.cloundlibrary.manager.ui.contract.ReaderLoginContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.ReaderLoginPresenter;
import com.tzpt.cloundlibrary.manager.utils.KeyboardUtils;
import com.tzpt.cloundlibrary.manager.widget.TitleBarView;
import com.tzpt.cloundlibrary.manager.widget.camera.IDCardCameraPreview;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 读者登录界面
 * Created by Administrator on 2017/7/4.
 */

public class ReaderLoginActivity extends BaseActivity implements ReaderLoginContract.View {
    private static final String FROM_TYPE = "from_type";
    private static final String CARD_INFO = "card_info";

    public static void startActivity(Context context, int fromType) {
        Intent intent = new Intent(context, ReaderLoginActivity.class);
        intent.putExtra(FROM_TYPE, fromType);
        context.startActivity(intent);
    }

    @BindView(R.id.reader_login_rl)
    RelativeLayout mReaderLoginRl;
    @BindView(R.id.reader_login_scan_idcard_rb)
    RadioButton mReaderLoginScanIdcardRb;
    @BindView(R.id.reader_login_account_rb)
    RadioButton mReaderLoginAccountRb;
    @BindView(R.id.reader_login_rg)
    RadioGroup mReaderLoginRg;
    @BindView(R.id.light_btn)
    Button mLightBtn;
    @BindView(R.id.reader_login_account_et)
    EditText mReaderLoginAccountEt;
    @BindView(R.id.reader_login_pwd_et)
    EditText mReaderLoginPwdEt;
    @BindView(R.id.reader_login_btn)
    Button mReaderLoginBtn;
    @BindView(R.id.reader_login_account_ll)
    LinearLayout mReaderLoginAccountLl;
    @BindView(R.id.reader_login_camera_preview)
    IDCardCameraPreview mIDCardCameraPreview;
    @BindView(R.id.reader_login_title)
    TitleBarView mTitleBar;

    private int mFromType;
    private boolean mTabType = true;

    private ReaderLoginPresenter mPresenter;

    private static final int DECODE_SUCCESS = 1001;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mTabType) {
                if (msg.what == DECODE_SUCCESS) {
                    Bundle bundle = msg.getData();
                    IDCardBean idCardBean = (IDCardBean) bundle.getSerializable(CARD_INFO);
                    ReaderAuthenticationActivity.startActivity(ReaderLoginActivity.this, mFromType, idCardBean);
                    finish();
                }
            }
        }
    };

    @OnClick({R.id.light_btn, R.id.reader_login_btn, R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.light_btn:
                mIDCardCameraPreview.turnLight();
                break;
            case R.id.reader_login_btn:
                if (mFromType == 0 || mFromType == 2) {
                    mPresenter.readerLogin(mReaderLoginAccountEt.getText().toString().trim(),
                            mReaderLoginPwdEt.getText().toString().trim(), true);
                } else {
                    mPresenter.readerLogin(mReaderLoginAccountEt.getText().toString().trim(),
                            mReaderLoginPwdEt.getText().toString().trim(), false);
                }
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_reader_login;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        mFromType = getIntent().getIntExtra(FROM_TYPE, 0);
        mPresenter = new ReaderLoginPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {
        switch (mFromType) {
            case 0:
                mTitleBar.setTitle("借书管理");
                break;
            case 1:
                mTitleBar.setTitle("赔书管理");
                break;
            case 2:
                mTitleBar.setTitle("退馆押金");
                break;
            case 3:
                mTitleBar.setTitle("读者管理");
                break;
            case 4:
                mTitleBar.setTitle("交馆押金");
                break;
        }
        mTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);

        mReaderLoginRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.reader_login_scan_idcard_rb:
                        mTabType = true;
                        KeyboardUtils.hideSoftInput(ReaderLoginActivity.this);
                        mReaderLoginAccountLl.setVisibility(View.GONE);
                        mLightBtn.setVisibility(View.VISIBLE);
                        mIDCardCameraPreview.setVisibility(View.VISIBLE);
                        startCameraPreView();
                        break;
                    case R.id.reader_login_account_rb:
                        mTabType = false;
                        mReaderLoginAccountLl.setVisibility(View.VISIBLE);
                        mLightBtn.setVisibility(View.GONE);
                        mIDCardCameraPreview.turnLightOff();
                        mIDCardCameraPreview.setVisibility(View.GONE);
                        mIDCardCameraPreview.stopPreview();
                        break;
                }
            }
        });
        mReaderLoginPwdEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    //开始登录
                    if (mFromType == 0 || mFromType == 2) {
                        mPresenter.readerLogin(mReaderLoginAccountEt.getText().toString().trim(),
                                mReaderLoginPwdEt.getText().toString().trim(), true);
                    } else {
                        mPresenter.readerLogin(mReaderLoginAccountEt.getText().toString().trim(),
                                mReaderLoginPwdEt.getText().toString().trim(), false);
                    }

                    return true;
                }
                return false;
            }
        });

        mIDCardCameraPreview.setIDCardListener(new IDCardCameraPreview.CallbackIDCard() {
            @Override
            public void callbackBarCode(IDCardBean idCard) {
                if (mTabType) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(CARD_INFO, idCard);
                    Message message = new Message();
                    message.what = DECODE_SUCCESS;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
            }

            @Override
            public void callbackCheckCameraPermission() {
                startCameraPreView();
            }
        });
    }

    //开启预览
    private void startCameraPreView() {
        if (Build.VERSION.SDK_INT < 23) {
            if (null != mIDCardCameraPreview) {
                mIDCardCameraPreview.startPreview();
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
                            if (null != mIDCardCameraPreview) {
                                mIDCardCameraPreview.startPreview();
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
        if (mTabType) {
            mIDCardCameraPreview.restartCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTabType) {
            mIDCardCameraPreview.releaseCamera();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHandler) {
            mHandler.removeMessages(DECODE_SUCCESS);
        }
        mPresenter.detachView();
    }

    @Override
    public void showToastMsg(String msg) {
        showMessageDialog(msg);
    }

    @Override
    public void showLoading(String msg) {
        showDialog(msg);
    }

    @Override
    public void dismissLoading() {
        dismissDialog();
    }

    @Override
    public void readerLoginSuccess(String readerId) {
        gotoActivity(readerId);
    }

    @Override
    public void readerLoginFailed(String msg) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        dialog.hasNoCancel(false);
        dialog.setCancelable(false);
        dialog.show();
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
    }

    @Override
    public void readerLoginFailed(int msgId) {
        readerLoginFailed(getString(msgId));
    }

    @Override
    public void noPermissionDialog(int msgId) {
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
                LoginActivity.startActivity(ReaderLoginActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void noPermissionPrompt(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                LoginActivity.startActivity(ReaderLoginActivity.this);
                finish();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void showDialogForFinish(int msgId) {
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
    public void turnToDealPenalty(String readerId) {
        PenaltyDealActivity.startActivity(this, mFromType, readerId);
        finish();
    }

    private void gotoActivity(String readerId) {
        switch (mFromType) {
            case 0:
                BorrowBookManagementActivity.startActivity(this, readerId);
                break;
            case 1:
                LostBookActivity.startActivity(this, readerId);
                break;
            case 2:
                RefundDepositActivity.startActivity(this, readerId);
                break;
            case 3:
                ReaderPwdManagementActivity.startActivity(this, readerId);
                break;
            case 4:
                ChargeLibDepositActivity.startActivity(this, readerId);
                break;
        }
        finish();
    }


}
