package com.tzpt.cloudlibrary.ui.account.selfhelp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.PermissionsDialogFragment;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 自助界面
 * Created by Administrator on 2017/12/14.
 */

public class SelfHelpActivity extends BaseActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SelfHelpActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.self_help_borrow_book_btn, R.id.self_help_buy_book_btn, R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.self_help_borrow_book_btn:
                initCameraPermission(0);
                break;
            case R.id.self_help_buy_book_btn:
                initCameraPermission(1);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_self_help;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("自助");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {

    }

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
    protected void onDestroy() {
        super.onDestroy();
        //取消事件订阅
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recviceLoginOut(AccountMessage loginMessage) {
        if (loginMessage.mIsLoginOut) {
            finish();
        }
    }

    //初始化摄像头权限
    private void initCameraPermission(final int selfHelpType) {
        if (Build.VERSION.SDK_INT < 23) {
            handleShelfHelp(selfHelpType);
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(Manifest.permission.CAMERA)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {//权限已授权
                            handleShelfHelp(selfHelpType);

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

    /**
     * 跳转自助界面
     *
     * @param selfHelpType 自助类型
     */
    private void handleShelfHelp(int selfHelpType) {
        switch (selfHelpType) {
            case 0:
                SelfHelpBorrowBooksScanningActivity.startActivity(SelfHelpActivity.this);
//                Intent intent = new Intent(this, ScanningActivity.class);
//                startActivity(intent);
                break;
            case 1:
                SelfHelpBuyBookScanningActivity.startActivity(SelfHelpActivity.this);
                break;
        }
    }
}
