package com.tzpt.cloudlibrary.ui.library;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tzpt.cloudlibrary.AppIntentGlobalName;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.CameraAndStoragePermission;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.LoginActivity;
import com.tzpt.cloudlibrary.ui.common.GalleyActivity;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.PermissionsDialogFragment;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.CustomChoosePicWindow;
import com.tzpt.cloudlibrary.widget.CustomDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;

/**
 * 我要留言
 */
public class LibraryToLeaveMessageActivity extends BaseActivity implements
        LibraryToLeaveMessageContract.View {

    private static final String LIBRARY_CODE = "library_code";

    public static void startActivity(Activity activity, String libCode, int requestCode) {
        Intent intent = new Intent(activity, LibraryToLeaveMessageActivity.class);
        intent.putExtra(LIBRARY_CODE, libCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.to_leave_msg_content_et)
    EditText mToLeaveMsgContentEt;
    @BindView(R.id.to_leave_msg_total_tv)
    TextView mToLeaveMsgTotalTv;
    @BindView(R.id.to_leave_msg_contact_et)
    EditText mToLeaveMsgContactEt;
    @BindView(R.id.to_leave_msg_img_iv)
    ImageView mToLeaveMsgImgIv;
    @BindView(R.id.to_leave_del_img_btn)
    ImageButton mToLeaveDelImgBtn;

    private String mFilePath;
    private String mFilePathTemp;
    private LibraryToLeaveMessagePresenter mPresenter;

    @OnClick({R.id.titlebar_left_btn, R.id.to_leave_msg_submit_btn, R.id.to_leave_msg_img_iv, R.id.to_leave_del_img_btn})
    public void onViewClicked(View view) {
        KeyboardUtils.hideSoftInput(this);
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                if (hasMessageContent()) {
                    showMessageDialog(R.string.content_have_not_submit);
                } else {
                    finish();
                }
                break;
            case R.id.to_leave_msg_submit_btn:
                String content = mToLeaveMsgContentEt.getText().toString().trim();
                String contract = mToLeaveMsgContactEt.getText().toString().trim();
                if (StringUtils.isHaveEmoji(content)
                        || StringUtils.isHaveEmoji(contract)) {
                    final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, getString(R.string.emoji_tips));
                    dialog.setCancelable(false);
                    dialog.hasNoCancel(false);
                    dialog.setButtonTextConfirmOrYes(true);
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
                } else {
                    mPresenter.pubMsg(contract, content, mFilePath);
                }
                break;
            case R.id.to_leave_msg_img_iv:
                if (!TextUtils.isEmpty(mFilePath)) {
                    ArrayList<String> imageList = new ArrayList<>();
                    imageList.add(mFilePath);

                    int location[] = new int[2];
                    mToLeaveMsgImgIv.getLocationOnScreen(location);
                    GalleyActivity.startActivity(this, imageList, location[0], location[1], mToLeaveMsgImgIv.getWidth(), mToLeaveMsgImgIv.getHeight());

                } else {
                    new CustomChoosePicWindow(this, mOnOptionsListener);
                }
                break;
            case R.id.to_leave_del_img_btn:
                GlideApp.with(this)
                        .load(R.mipmap.ic_add_img)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .into(mToLeaveMsgImgIv);
                mToLeaveDelImgBtn.setVisibility(View.GONE);
                mFilePathTemp = "";
                mFilePath = "";
                break;
        }
    }

    private CustomChoosePicWindow.PicOptionsListener mOnOptionsListener = new CustomChoosePicWindow.PicOptionsListener() {
        @Override
        public void onOptionClick(int optionType) {
            switch (optionType) {
                case 0:
                    takePicture();
                    break;
                case 1:
                    choosePicture();
                    break;
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_library_to_leave_message;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("我要留言");
    }

    @Override
    public void initDatas() {
        mPresenter = new LibraryToLeaveMessagePresenter();
        mPresenter.attachView(this);

        String libCode = getIntent().getStringExtra(LIBRARY_CODE);
        mPresenter.setLibraryCode(libCode);
    }

    @Override
    public void configViews() {
        mToLeaveMsgContentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mToLeaveMsgTotalTv.setText(new StringBuffer().append(s.length()).append("/300"));
            }
        });
    }

    private void choosePicture() {
        if (Build.VERSION.SDK_INT < 23) {
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*");
            startActivityForResult(intent, 1000);
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {//权限已授权
                            Intent intent = new Intent(Intent.ACTION_PICK, null);
                            intent.setDataAndType(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    "image/*");
                            startActivityForResult(intent, 1000);
                        } else if (permission.shouldShowRequestPermissionRationale) {//下次继续提示(应该显示请求许可理由)

                        } else {
                            showPermissionPopUpWindow(1);
                        }
                    }
                });

    }


    private void takePicture() {
        if (Build.VERSION.SDK_INT < 23) {
            mFilePathTemp = Environment.getExternalStorageDirectory() + "/ytsg/" + System.currentTimeMillis() + ".jpg";
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri uri = Uri.fromFile(new File(mFilePathTemp));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, 1001);
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
                    mFilePathTemp = Environment.getExternalStorageDirectory() + "/ytsg/" + System.currentTimeMillis() + ".jpg";
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri uri = Uri.fromFile(new File(mFilePathTemp));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, 1001);
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
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000://相册返回
                    if (null != data) {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(data.getData(),
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        mFilePath = cursor.getString(columnIndex);  //获取照片路径
                        cursor.close();

                        GlideApp.with(this)
                                .load(mFilePath)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .placeholder(R.color.color_ffffff)
                                .centerCrop()
                                .into(mToLeaveMsgImgIv);
                        mToLeaveDelImgBtn.setVisibility(View.VISIBLE);
                    }
                    break;
                case 1001://拍照返回
                    mFilePath = mFilePathTemp;

                    GlideApp.with(this)
                            .load(mFilePath)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .placeholder(R.color.color_ffffff)
                            .centerCrop()
                            .into(mToLeaveMsgImgIv);
                    mToLeaveDelImgBtn.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 如果按了返回键
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (hasMessageContent()) {
                showMessageDialog(R.string.content_have_not_submit);
            } else {
                finish();
            }
        }
        return false;
    }

    private boolean hasMessageContent() {
        if (!TextUtils.isEmpty(mFilePath)) {
            return true;
        } else if (mToLeaveMsgContentEt.getText().toString().trim().length() > 0) {
            return true;
        } else if (mToLeaveMsgContactEt.getText().toString().trim().length() > 0) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }

    }

    private void showMessageDialog(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.setButtonTextConfirmOrYes(true);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                KeyboardUtils.hideSoftInput(LibraryToLeaveMessageActivity.this);
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
    public void showMsgDialog(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }

    @Override
    public void showPostDialog() {
        showDialog("发送中...");
    }

    @Override
    public void dismissPostDialog() {
        dismissDialog();
    }

    @Override
    public void postSuccess() {
//        final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, "留言成功！");
//        dialog.setCancelable(false);
//        dialog.hasNoCancel(false);
//        dialog.setButtonTextConfirmOrYes(true);
//        dialog.show();
//        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
//            @Override
//            public void onClickOk() {
//                dialog.dismiss();
//                Intent intent = new Intent();
//                intent.putExtra(AppIntentGlobalName.SUBMIT_MSG_SUCCESS, true);
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//
//            @Override
//            public void onClickCancel() {
//                dialog.dismiss();
//            }
//        });
        ToastUtils.showSingleToast("留言成功！");
        Intent intent = new Intent();
        intent.putExtra(AppIntentGlobalName.SUBMIT_MSG_SUCCESS, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = false;
        EventBus.getDefault().post(accountMessage);

        final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, getString(R.string.account_login_other_device));
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.setLoginAnewBtn();
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                LoginActivity.startActivity(LibraryToLeaveMessageActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });

    }

    @Override
    public void pleaseLogin() {
        LoginActivity.startActivity(this);
    }


}
