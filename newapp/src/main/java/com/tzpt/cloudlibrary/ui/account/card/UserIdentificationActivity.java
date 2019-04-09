package com.tzpt.cloudlibrary.ui.account.card;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.base.adapter.glide.RoundedCornersTransformation;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;
import com.tzpt.cloudlibrary.widget.usertopbar.UserCommonTopBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 名片
 */
public class UserIdentificationActivity extends BaseActivity implements
        UserIdentificationContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, UserIdentificationActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.user_top_bar)
    UserCommonTopBar mUserCommonTopBar;
    @BindView(R.id.account_scanner_bar_code_iv)
    ImageView mAccountScannerBarCodeIv;
    @BindView(R.id.account_scanner_qc_iv)
    ImageView mAccountScannerQcIv;
    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.progress_layout)
    LoadingProgressView mProgressLayout;
    private boolean mShouldRefresh = true;

    private UserIdentificationPresenter mPresenter;

    private View.OnClickListener mRetryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPresenter.getTokenBar();
        }
    };

    @OnClick({R.id.titlebar_left_btn, R.id.account_refresh_tv, R.id.face_photo_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.account_refresh_tv:
                mPresenter.getTokenBar();
                break;
            case R.id.face_photo_btn:
                mPresenter.getUserImgStatus();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_identification;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("名片");
    }

    @Override
    public void initDatas() {
        mPresenter = new UserIdentificationPresenter();
        mPresenter.attachView(this);
        mPresenter.getLoginInfo();

        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        //隐藏箭头
        mUserCommonTopBar.hideRightArrow();
    }

    @Override
    public void setBarCodeBitmap(Bitmap bitmap) {
        mAccountScannerBarCodeIv.setImageBitmap(bitmap);
    }

    @Override
    public void setQRBitmap(Bitmap bitmap) {
        mAccountScannerQcIv.setImageBitmap(bitmap);
    }

    @Override
    public void showProgressView() {
        mMultiStateLayout.showProgress();
    }

    @Override
    public void showContentView() {
        mMultiStateLayout.showContentView();
    }

    @Override
    public void showError() {
        mMultiStateLayout.showRetryError(mRetryClickListener);
    }

    //设置昵称
    @Override
    public void setUserNickName(String nickName) {
        mUserCommonTopBar.setUserNickName(nickName);
    }

    //设置电话号码
    @Override
    public void setUserPhoneNumber(String phone) {
        mUserCommonTopBar.setUserPhone(phone);
    }

    //设置用户头像
    @Override
    public void setUserHeadImage(String headImg, boolean isMan) {
        GlideApp.with(this)
                .load(headImg)
                .placeholder(isMan ? R.mipmap.ic_head_mr : R.mipmap.ic_head_miss)
                .error(isMan ? R.mipmap.ic_head_mr : R.mipmap.ic_head_miss)
                .centerCrop()
                .transform(new RoundedCornersTransformation(5, RoundedCornersTransformation.CornerType.ALL))
                .into(mUserCommonTopBar.getUserHeadImageView());
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
    }

    //设置头像地址
    @Override
    public void setFaceRecognitionImage(String faceImage) {
        mShouldRefresh = false;
        if (!TextUtils.isEmpty(faceImage)) {
            //更换人脸照片
            FacePreviewActivity.startActivity(this, 0, faceImage);
        } else {
            //完善人脸照片
            FaceRecognitionActivity.startActivity(this);
        }
    }

    @Override
    public void showProgressDialog() {
        mProgressLayout.showProgressLayout();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressLayout.hideProgressLayout();
    }

    @Override
    public void showDialogTip(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, getString(msgId));
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
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
        if (mShouldRefresh) {
            mPresenter.getTokenBar();
        }
        mShouldRefresh = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.detachView();
            mPresenter = null;
        }
        EventBus.getDefault().unregister(this);
        recycleBitmap(mAccountScannerBarCodeIv);
        recycleBitmap(mAccountScannerQcIv);
    }

    //回收bitmap
    private void recycleBitmap(ImageView imageView) {
        if (null != imageView) {
            Drawable drawable = imageView.getDrawable();
            if (drawable != null && drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap = null;
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receviceLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }
}
