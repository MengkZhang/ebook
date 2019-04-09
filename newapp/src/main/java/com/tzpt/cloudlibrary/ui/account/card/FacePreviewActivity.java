package com.tzpt.cloudlibrary.ui.account.card;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.CameraAndStoragePermission;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.PermissionsDialogFragment;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;

/**
 * 人脸认证照片
 */
public class FacePreviewActivity extends BaseActivity implements
        FacePreviewContract.View {

    private static final String FROM_TYPE = "from_type";
    private static final String FACE_IMAGE = "face_image";
    private static final int START_FACE_CODE = 1000;
    private int mFromType;

    public static void startActivity(Context context, int fromType, String faceImage) {
        Intent intent = new Intent(context, FacePreviewActivity.class);
        intent.putExtra(FROM_TYPE, fromType);
        intent.putExtra(FACE_IMAGE, faceImage);
        context.startActivity(intent);
    }

    private View.OnClickListener mRetryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loadFaceImageData();
        }
    };

    @OnClick({R.id.titlebar_left_btn, R.id.face_preview_re_photo_btn, R.id.face_preview_upload_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.face_preview_upload_btn:
                mPresenter.updateLoadFaceImage();
                break;
            case R.id.face_preview_re_photo_btn:
                takeFrontCamera();
                break;
        }
    }

    @BindView(R.id.face_preview_pic_iv)
    ImageView mFacePreviewPicIv;
    @BindView(R.id.face_preview_upload_btn)
    Button mFacePreviewUploadBtn;
    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    private FacePreviewPresenter mPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_face_preview;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("人脸照片");
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
        mPresenter = new FacePreviewPresenter();
        mPresenter.attachView(this);
        Intent intent = getIntent();
        mFromType = intent.getIntExtra(FROM_TYPE, 0);
        loadFaceImageData();
    }

    private void loadFaceImageData() {
        switch (mFromType) {
            case 0://check face image
                //设置不显示上传按钮
                mFacePreviewUploadBtn.setVisibility(View.GONE);
                //显示查询图片
                String faceImage = getIntent().getStringExtra(FACE_IMAGE);
                loadFaceImage(faceImage);
                break;
            case 1://preview face image-加载本地图片
                mFacePreviewUploadBtn.setVisibility(View.VISIBLE);
                String localImagePath = getIntent().getStringExtra(FACE_IMAGE);
                mPresenter.setLocalImagePath(localImagePath);
                loadFaceImage(localImagePath);
                break;
        }
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
            intent.putExtra("from_type", 0);
            startActivityForResult(intent, START_FACE_CODE);
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

                    Intent intent = new Intent(FacePreviewActivity.this, FaceCameraActivity.class);
                    intent.putExtra("from_type", 0);
                    startActivityForResult(intent, START_FACE_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != data && resultCode == RESULT_OK) {
            // 设置按钮为上传状态
            mFacePreviewUploadBtn.setVisibility(View.VISIBLE);
            String localFaceImagePath = data.getStringExtra("local_face_image");
            mPresenter.setLocalImagePath(localFaceImagePath);
            loadFaceImage(localFaceImagePath);
        }
    }

    //设置本地图片
    private void loadFaceImage(String faceImagePath) {
        if (TextUtils.isEmpty(faceImagePath)) {
            return;
        }
        mMultiStateLayout.showProgress();
        //加载本地图片
        GlideApp.with(this)
                .asBitmap()
                .load(faceImagePath)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerInside()
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        mMultiStateLayout.showRetryError(mRetryClickListener);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        int faceWidth = resource.getWidth();
                        int faceHeight = resource.getHeight();
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mFacePreviewPicIv.getLayoutParams();
                        if (faceWidth > faceHeight) {
                            float screenWidth = DpPxUtils.getScreenWidth(FacePreviewActivity.this);
                            float imgWidth = screenWidth - DpPxUtils.dipToPx(FacePreviewActivity.this, 100);
                            params.height = (int) imgWidth * faceHeight / faceWidth;
                            params.width = (int) imgWidth;
                        } else {
                            float screenHeight = DpPxUtils.getScreenHeight(FacePreviewActivity.this);
                            float imgHeight = screenHeight - DpPxUtils.dipToPx(FacePreviewActivity.this, 300);
                            params.height = (int) imgHeight;
                            params.width = (int) imgHeight * faceWidth / faceHeight;
                        }
                        params.gravity = Gravity.CENTER;
                        mFacePreviewPicIv.setLayoutParams(params);
                        mFacePreviewPicIv.setAdjustViewBounds(true);
                        mFacePreviewPicIv.setImageBitmap(resource);
                        mMultiStateLayout.showContentView();
                        return true;
                    }
                }).into(mFacePreviewPicIv);
    }

    @Override
    public void showUploadProgressDialog() {
        showDialog("保存中...");
    }

    @Override
    public void dismissUploadProgressDialog() {
        dismissDialog();
    }

    @Override
    public void uploadSuccess() {
        final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, getString(R.string.upload_success));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                FaceMessage faceMessage = new FaceMessage();
                faceMessage.mIsUpdateFaceImage = true;
                EventBus.getDefault().post(faceMessage);
                finish();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void uploadFail() {
        ToastUtils.showSingleToast(R.string.upload_fail);
        mFacePreviewUploadBtn.setText(R.string.re_upload);
    }

    @Override
    public void pleaseLoginTip() {
        //自定义对话框
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        EventBus.getDefault().unregister(this);
    }

}
