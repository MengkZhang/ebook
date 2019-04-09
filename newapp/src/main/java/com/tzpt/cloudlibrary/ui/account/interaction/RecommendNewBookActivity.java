package com.tzpt.cloudlibrary.ui.account.interaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.ui.paperbook.BookDetailActivity;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.PermissionsDialogFragment;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.utils.ISBNUtil;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 推荐新书
 */
public class RecommendNewBookActivity extends BaseActivity {

    @BindView(R.id.recommend_code_et)
    EditText mRecommendCodeEt;
    @BindView(R.id.progress_layout)
    LoadingProgressView mProgressLayout;


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, RecommendNewBookActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_recommend_new_book;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("新书推荐");
    }

    @Override
    public void initDatas() {
//        mPresenter.getUserRecommendBookInfo();
    }

    @Override
    public void configViews() {
        mRecommendCodeEt.setHint("输入ISBN号");
        //默认不显示游标
        mRecommendCodeEt.setCursorVisible(false);
        //只能输入数字
        mRecommendCodeEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        mRecommendCodeEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    verifyIsbnCode();
                    return true;
                }
                return false;
            }
        });
    }

    //验证isbn是否正确
    private void verifyIsbnCode() {
        KeyboardUtils.hideSoftInput(mRecommendCodeEt);
        String isbn = mRecommendCodeEt.getText().toString().trim().toUpperCase();
        if (TextUtils.isEmpty(isbn)) {
            ToastUtils.showSingleToast(R.string.enter_ISBN_number);
            return;
        }
        if (ISBNUtil.isISBN(isbn)) {
            BookDetailActivity.startActivity(this, isbn, null, null, 3);
        } else {
            ToastUtils.showSingleToast(R.string.ISBN_code_error);
        }
    }

    @OnClick({R.id.titlebar_left_btn, R.id.recommend_code_et, R.id.recommend_scanner_img_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                KeyboardUtils.hideSoftInput(mRecommendCodeEt);
                finish();
                break;
            case R.id.recommend_code_et:
                mRecommendCodeEt.setCursorVisible(true);
                break;
            case R.id.recommend_scanner_img_btn:
                KeyboardUtils.hideSoftInput(mRecommendCodeEt);
                initCameraPermission();
                break;
        }
    }

//    @Override
//    public void setUserRecommendBookInfo(int recommendBookSum, int total) {
//        this.mRecommendBookSum = recommendBookSum;
//        this.mTotal = total;
//    }


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
    }

    //初始化摄像头权限
    private void initCameraPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            RecommendNewBookScannerActivity.startActivity(RecommendNewBookActivity.this);
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(Manifest.permission.CAMERA)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {//权限已授权
                            RecommendNewBookScannerActivity.startActivity(RecommendNewBookActivity.this);
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
}
