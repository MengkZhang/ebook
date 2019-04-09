package com.tzpt.cloudlibrary.ui.account.setting;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.PermissionsDialogFragment;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.utils.PhoneCallUtil;
import com.tzpt.cloudlibrary.utils.VersionUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 关于我们
 * Created by Administrator on 2018/1/4.
 */

public class AboutUsActivity extends BaseActivity {
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AboutUsActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.version_name_tv)
    TextView mVersionNameTv;

    @OnClick({R.id.tel_tv, R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tel_tv:
                startPhoneCall(getString(R.string.call_phone_number), "4001015756");
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_about_us;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("关于我们");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        mVersionNameTv.setText(getString(R.string.current_version, VersionUtils.getVersionInfo()));
    }

    /**
     * 拨打电话
     *
     * @param phoneNumber 电话号码
     */
    private void startPhoneCall(String message, final String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return;
        }
        //自定义对话框
        final CustomDialog callPhoneDialog = new CustomDialog(this, R.style.DialogTheme, message);
        callPhoneDialog.setCancelable(false);
        callPhoneDialog.hasNoCancel(true);
        callPhoneDialog.setButtonTextForCallPhone();
        callPhoneDialog.setText(message);
        callPhoneDialog.show();
        callPhoneDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                callPhoneDialog.dismiss();
                initCallPhonePermission(phoneNumber);
            }

            @Override
            public void onClickCancel() {
                callPhoneDialog.dismiss();
            }
        });

    }

    //检查打电话权限
    private void initCallPhonePermission(final String phoneNumber) {
        if (Build.VERSION.SDK_INT < 23) {
            PhoneCallUtil.startPhoneCall(this, phoneNumber);
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(Manifest.permission.CALL_PHONE)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {
                            PhoneCallUtil.startPhoneCall(AboutUsActivity.this, phoneNumber);
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
        mCallPhoneDialogFragment.show(this.getFragmentManager(), "PermissionsDialogFragment");
    }
}
