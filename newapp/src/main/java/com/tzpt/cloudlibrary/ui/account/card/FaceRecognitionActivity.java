package com.tzpt.cloudlibrary.ui.account.card;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.CameraAndStoragePermission;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.PermissionsDialogFragment;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;

/**
 * 人脸认证照片
 */
public class FaceRecognitionActivity extends BaseActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, FaceRecognitionActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.titlebar_left_btn, R.id.face_recognition_start_btn, R.id.titlebar_right_txt_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.face_recognition_start_btn:
                takeFrontCamera();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_face_recognition;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("完善人脸照片");
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {

    }

    /**
     * 进入拍照界面
     */
    private void takeFrontCamera() {
        if (Build.VERSION.SDK_INT < 23) {
            Intent intent = new Intent(this, FaceCameraActivity.class);
            intent.putExtra("from_type", 1);
            startActivity(intent);
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);

        Observable<Permission> observable1 = rxPermissions.requestEach(Manifest.permission.CAMERA);
        Observable<Permission> observable2 = rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        Observable.zip(observable1, observable2, new Func2<Permission, Permission, CameraAndStoragePermission>() {
            @Override
            public CameraAndStoragePermission call(Permission permission, Permission permission2) {
                return new CameraAndStoragePermission(permission, permission2);
            }
        }).subscribe(new Action1<CameraAndStoragePermission>() {
            @Override
            public void call(CameraAndStoragePermission cameraAndStoragePermission) {
                if (cameraAndStoragePermission.cameraGranted && cameraAndStoragePermission.storageGranted) {//权限已授权
                    Intent intent = new Intent(FaceRecognitionActivity.this, FaceCameraActivity.class);
                    intent.putExtra("from_type", 1);
                    startActivity(intent);
                } else if (cameraAndStoragePermission.cameraShouldShowRequestPermissionRationale
                        || cameraAndStoragePermission.storageShouldShowRequestPermissionRationale) {//下次继续提示(应该显示请求许可理由)

                } else {//没有权限,不能使用权限模块-去设置权限
                    if (!cameraAndStoragePermission.cameraGranted) {
                        showPermissionPopUpWindow(0);
                    } else {
                        showPermissionPopUpWindow(1);
                    }
                }
            }
        });
    }

    private PermissionsDialogFragment mSetCameraDialogFragment;

    //展示设置权限弹窗
    private void showPermissionPopUpWindow(int dialogType) {
        if (null == mSetCameraDialogFragment) {
            mSetCameraDialogFragment = new PermissionsDialogFragment();
        }
        if (mSetCameraDialogFragment.isAdded()) {
            return;
        }
        mSetCameraDialogFragment.initPermissionUI(dialogType == 0 ? PermissionsDialogFragment.PERMISSION_CAMERA : PermissionsDialogFragment.PERMISSION_STORAGE);
        mSetCameraDialogFragment.show(this.getFragmentManager(), "PermissionsDialogFragment");
    }

    //更新用户人脸图片
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveUpdateFaceImage(FaceMessage faceMessage) {
        if (null != faceMessage && faceMessage.mIsUpdateFaceImage) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
