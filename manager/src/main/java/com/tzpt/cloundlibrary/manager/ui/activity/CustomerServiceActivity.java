package com.tzpt.cloundlibrary.manager.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.permissions.Permission;
import com.tzpt.cloundlibrary.manager.permissions.PermissionsDialogFragment;
import com.tzpt.cloundlibrary.manager.permissions.RxPermissions;

import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by Administrator on 2018/10/18.
 */

public class CustomerServiceActivity extends BaseActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, CustomerServiceActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.call_btn, R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.call_btn:
                initCallPhonePermission("4001015756");
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_customer_service;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("客服");
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {

    }


    //检查打电话权限
    private void initCallPhonePermission(final String phoneNumber) {
        if (Build.VERSION.SDK_INT < 23) {
            startPhoneCall(phoneNumber);
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(Manifest.permission.CALL_PHONE)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {
                            startPhoneCall(phoneNumber);
                        } else if (permission.shouldShowRequestPermissionRationale) {//下次继续提示(应该显示请求许可理由)

                        } else {
                            showSetPermissionPopUpWindow();
                        }
                    }
                });
    }

    private PermissionsDialogFragment mCallPhoneDialogFragment;

    //展示设置权限弹窗
    private void showSetPermissionPopUpWindow() {
        if (null == mCallPhoneDialogFragment) {
            mCallPhoneDialogFragment = new PermissionsDialogFragment();
        }
        if (mCallPhoneDialogFragment.isAdded()) {
            return;
        }
        mCallPhoneDialogFragment.initPermissionUI(PermissionsDialogFragment.PERMISSION_CALL_PHONE);
        mCallPhoneDialogFragment.show(getFragmentManager(), "PermissionsDialogFragment");
    }

    /**
     * 开始拨打电话
     */
    private void startPhoneCall(String phoneNumber) {
        if (null == phoneNumber || TextUtils.isEmpty(phoneNumber)) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) ==
                PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + phoneNumber);
            intent.setData(data);
            startActivity(intent);
        }

    }
}
