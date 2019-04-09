package com.tzpt.cloundlibrary.manager.permissions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.BuildConfig;
import com.tzpt.cloundlibrary.manager.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 权限控制
 * Created by Administrator on 2017/7/16.
 */
public class PermissionsDialogFragment extends DialogFragment {

    Unbinder unbinder;
    @BindView(R.id.permission_title)
    TextView mPermissionTitle;
    @BindView(R.id.permission_content)
    TextView mPermissionContent;

    public static final int PERMISSION_LOCATION = 0;    //0定位
    public static final int PERMISSION_CAMERA = 1;      // 1相机
    public static final int PERMISSION_CALL_PHONE = 2;  // 2电话
    public static final int PERMISSION_STORAGE = 3;     //3存储卡
    private int mPermissionType = PERMISSION_LOCATION;
    private boolean mShowDialog = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (null != getDialog() && null != getDialog().getWindow()) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        View view = inflater.inflate(R.layout.fragment_set_location_dialog, null);
        unbinder = ButterKnife.bind(this, view);
        builder.setView(view);
        return builder.create();
    }

    //0定位 1相机
    public void initPermissionUI(int permissionType) {
        this.mPermissionType = permissionType;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (mShowDialog) {
            return;
        }
        mShowDialog = true;
        super.show(manager, tag);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mShowDialog = false;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mShowDialog = false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        switch (mPermissionType) {
            case PERMISSION_LOCATION:
                mPermissionTitle.setText("您还未开启定位！");
                mPermissionContent.setText("开启定位，查找结果等准确！");
                break;
            case PERMISSION_CAMERA:
                mPermissionTitle.setText("您还未开启相机权限！");
                mPermissionContent.setText("");
                break;
            case PERMISSION_CALL_PHONE:
                mPermissionTitle.setText("您还未开启拨打电话权限！");
                mPermissionContent.setText("");
                break;
            case PERMISSION_STORAGE:
                mPermissionTitle.setText("您还未开启存储卡权限！");
                mPermissionContent.setText("");
                break;
        }
    }

    @OnClick({R.id.not_set, R.id.set_up})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.not_set:
                dismiss();
                break;
            case R.id.set_up:
                gotoMiUiPermission();
                dismiss();
                break;
        }
    }

    /**
     * 跳转到miui的权限管理页面
     */
    private void gotoMiUiPermission() {
        Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
        ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        i.setComponent(componentName);
        i.putExtra("extra_pkgname", getActivity().getPackageName());
        try {
            getActivity().startActivity(i);
        } catch (Exception e) {
            gotoMeiZuPermission();
        }
    }

    /**
     * 跳转到魅族的权限管理系统
     */
    private void gotoMeiZuPermission() {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        try {
            getActivity().startActivity(intent);
        } catch (Exception e) {
            gotoHuaWeiPermission();
        }
    }

    /**
     * 华为的权限管理页面
     */
    private void gotoHuaWeiPermission() {
        if (mPermissionType == PERMISSION_STORAGE) {
            getActivity().startActivity(getAppDetailSettingIntent());
            return;
        }
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            getActivity().startActivity(intent);
        } catch (Exception e) {
            getActivity().startActivity(getAppDetailSettingIntent());
        }

    }

    /**
     * 获取应用详情页面intent
     *
     * @return
     */
    private Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
        return localIntent;
    }
}
