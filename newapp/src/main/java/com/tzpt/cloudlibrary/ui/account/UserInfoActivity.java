package com.tzpt.cloudlibrary.ui.account;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tzpt.cloudlibrary.AppIntentGlobalName;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.base.adapter.glide.RoundedCornersTransformation;
import com.tzpt.cloudlibrary.bean.CameraAndStoragePermission;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.PermissionsDialogFragment;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.CustomUserInfoItemView;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;

/**
 * 个人信息
 */
public class UserInfoActivity extends BaseActivity implements
        UserInfoContract.View {

    private static final int NICK_NAME_REQUEST_CODE = 1000;
    private static final int REQUEST_CODE_TAKE_PIC_FROM_CAMERA = NICK_NAME_REQUEST_CODE + 1;
    private static final int REQUEST_CODE_CROP_PHOTO = REQUEST_CODE_TAKE_PIC_FROM_CAMERA + 1;
    private static final int REQUEST_CODE_SYSTEM_GALLERY = REQUEST_CODE_CROP_PHOTO + 1;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.user_info_item_head_civ)
    CustomUserInfoItemView mUserInfoItemHeadCiv;
    @BindView(R.id.user_info_item_nickname_civ)
    CustomUserInfoItemView mUserInfoItemNickNameCiv;
    @BindView(R.id.user_info_item_card_name_civ)
    CustomUserInfoItemView mUserInfoItemCardNameCiv;
    @BindView(R.id.user_info_item_id_card_civ)
    CustomUserInfoItemView mUserInfoItemIdCardCiv;
    @BindView(R.id.user_info_item_phone_civ)
    CustomUserInfoItemView mUserInfoItemPhoneCiv;
    @BindView(R.id.loading_progress_view)
    LoadingProgressView mProgressView;

    private UserInfoPresenter mPresenter;
    private String mNickName;
    private String mHeadImagePath;
    private String mPhoneNum;
    private UserChooseHeadPicBottomDialog mBottomDialog;

    private Uri mCameraImgUri = Uri.parse(AppIntentGlobalName.CAMERA_DEFAULT_PATH);

    private boolean mIsMan;
    private boolean mIsModifyInfo;
    private String mLocalHeadImgPath;

    @OnClick({R.id.titlebar_left_btn, R.id.user_info_item_head_civ, R.id.user_info_item_nickname_civ,
            R.id.user_info_item_phone_civ})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.user_info_item_head_civ:
                if (mBottomDialog == null) {
                    mBottomDialog = new UserChooseHeadPicBottomDialog(this);
                    mBottomDialog.setChoosePicListener(mChoosePicListener);
                }
                mBottomDialog.show();
                break;
            case R.id.user_info_item_nickname_civ:
                UserNickNameActivity.startActivityForResult(this, mNickName, NICK_NAME_REQUEST_CODE);
                break;
            case R.id.user_info_item_phone_civ:
                if (!TextUtils.isEmpty(mPhoneNum)) {
                    ChangePhoneNumberActivity.startActivity(this, 0);
                } else {
                    ChangePhoneNumberActivity.startActivity(this, 1);
                }
                break;
        }
    }

    private UserChooseHeadPicBottomDialog.ChoosePicListener mChoosePicListener = new UserChooseHeadPicBottomDialog.ChoosePicListener() {
        @Override
        public void onPicClick(int clickType) {
            switch (clickType) {
                case 0://拍照
                    initCameraPermission();
                    break;
                case 1://选择手机相册
                    choosePicture();
                    break;
                case 2://系统图片
                    UserHeadActivity.startActivityForResult(UserInfoActivity.this, mHeadImagePath, REQUEST_CODE_SYSTEM_GALLERY);
                    break;
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_info;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("个人信息");
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
        mPresenter = new UserInfoPresenter();
        mPresenter.attachView(this);
        mPresenter.getUserInfo();
    }

    @Override
    public void configViews() {

    }

    @Override
    public void setUserIdCard(String idCard) {
        mUserInfoItemIdCardCiv.setItemName(idCard);
    }

    @Override
    public void setUserCardName(String cardName) {
        mUserInfoItemCardNameCiv.setItemName(cardName);
    }

    @Override
    public void setUserNickName(String nickName) {
        this.mNickName = nickName;
        mUserInfoItemNickNameCiv.setItemName(nickName);
    }

    @Override
    public void setUserHeadImage(String headImage, boolean isMan) {
        this.mHeadImagePath = headImage;
        mIsMan = isMan;
        GlideApp.with(this)
                .load(headImage)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.color.color_ffffff)
                .error(isMan ? R.mipmap.ic_head_mr_edit : R.mipmap.ic_head_miss_edit)
                .centerCrop()
                .transform(new RoundedCornersTransformation(5, RoundedCornersTransformation.CornerType.ALL))
                .into(mUserInfoItemHeadCiv.getImageView());
    }

    @Override
    public void setUserPhone(String phone) {
        mPhoneNum = phone;
        if (TextUtils.isEmpty(phone)) {
            mUserInfoItemPhoneCiv.setItemNameHint("去绑定");
        } else {
            mUserInfoItemPhoneCiv.setItemName(StringUtils.formatTel(phone));
        }
    }

    @Override
    public void showLoadingProgress() {
        mProgressView.showProgressLayout();
    }

    @Override
    public void dismissLoadingProgress() {
        mProgressView.hideProgressLayout();
    }

    @Override
    public void changeUserHeadSuccess() {
        mIsModifyInfo = true;
    }

    @Override
    public void changeUserHeadFailed() {
        final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, getString(R.string.modify_user_head_failed));
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.setLoginAnewBtn();
        dialog.setBtnOKAndBtnCancelTxt("重试", "取消");
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mPresenter.submitUserHead(mLocalHeadImgPath);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void pleaseLoginTips() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_TAKE_PIC_FROM_CAMERA:
                if (resultCode == RESULT_OK) {
                    try {
                        Intent cropPhoto = new Intent(UserInfoActivity.this, CropPhotoActivity.class);
                        cropPhoto.putExtra(AppIntentGlobalName.PIC_PATH, mCameraImgUri.getPath());
                        startActivityForResult(cropPhoto, REQUEST_CODE_CROP_PHOTO);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case NICK_NAME_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    mNickName = data.getStringExtra(AppIntentGlobalName.NICK_NAME);
                    mUserInfoItemNickNameCiv.setItemName(mNickName);
                    mIsModifyInfo = true;
                }
                break;
            case REQUEST_CODE_CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    mLocalHeadImgPath = data.getStringExtra(AppIntentGlobalName.PIC_PATH);
                    GlideApp.with(this)
                            .load(mLocalHeadImgPath)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .placeholder(R.color.color_ffffff)
                            .error(mIsMan ? R.mipmap.ic_head_mr_edit : R.mipmap.ic_head_miss_edit)
                            .centerCrop()
                            .transform(new RoundedCornersTransformation(5, RoundedCornersTransformation.CornerType.ALL))
                            .into(mUserInfoItemHeadCiv.getImageView());
                    mPresenter.submitUserHead(mLocalHeadImgPath);
                }
                break;
            case REQUEST_CODE_SYSTEM_GALLERY:
                if (resultCode == RESULT_OK) {
                    mIsModifyInfo = true;
                    finish();
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recivceLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            mIsModifyInfo = false;
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
        mPresenter.getUserPhone();
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
    }

    @Override
    public void finish() {
        if (mIsModifyInfo) {
            setResult(RESULT_OK);
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPresenter.detachView();
    }


    private void choosePicture() {
        if (Build.VERSION.SDK_INT < 23) {
            GalleryActivity.startActivityForResult(UserInfoActivity.this, REQUEST_CODE_CROP_PHOTO);
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {//权限已授权
                            GalleryActivity.startActivityForResult(UserInfoActivity.this, REQUEST_CODE_CROP_PHOTO);
                        } else if (permission.shouldShowRequestPermissionRationale) {//下次继续提示(应该显示请求许可理由)

                        } else {
                            showPermissionPopUpWindow(1);
                        }
                    }
                });

    }

    //初始化摄像头权限
    private void initCameraPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraImgUri);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PIC_FROM_CAMERA);
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
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraImgUri);
                    startActivityForResult(intent, REQUEST_CODE_TAKE_PIC_FROM_CAMERA);
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
}
