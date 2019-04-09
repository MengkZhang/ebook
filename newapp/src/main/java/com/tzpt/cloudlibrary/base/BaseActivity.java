package com.tzpt.cloudlibrary.base;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.utils.StatusBarUtil;
import com.tzpt.cloudlibrary.widget.CustomLoadingDialog;
import com.tzpt.cloudlibrary.widget.titlebar.TitleBarView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/5/22.
 */

public abstract class BaseActivity extends AppCompatActivity {
    /*implements SwipeBackHelper.SlideBackManager */

    public TitleBarView mCommonTitleBar;
    protected Context mContext;
    //private SwipeBackHelper mSwipeBackHelper;
    Unbinder unbinder;
    private CustomLoadingDialog mLoadingDialog;//进度条
    //private boolean mSupportSlideBack = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        ViewGroup contentFrameLayout = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        View parentView = contentFrameLayout.getChildAt(0);
        if (parentView != null && Build.VERSION.SDK_INT >= 14) {
            parentView.setFitsSystemWindows(true);
        }

        StatusBarUtil.StatusBarLightMode(this);

        mContext = this;
        unbinder = ButterKnife.bind(this);

        mCommonTitleBar = ButterKnife.findById(this, R.id.common_toolbar);
        if (mCommonTitleBar != null) {
            initToolBar();
        }
        initDatas();
        configViews();
    }


    public abstract int getLayoutId();

    public abstract void initToolBar();

    public abstract void initDatas();

    public abstract void configViews();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (mSwipeBackHelper == null) {
//            mSwipeBackHelper = new SwipeBackHelper(this);
//        }
//        return mSwipeBackHelper.processTouchEvent(ev) || super.dispatchTouchEvent(ev);
//    }

//    @Override
//    public Activity getSlideActivity() {
//        return this;
//    }
//
//    @Override
//    public boolean supportSlideBack() {
//        return mSupportSlideBack;
//    }
//
//    @Override
//    public boolean canBeSlideBack() {
//        return true;
//    }

//    /**
//     * 设置是否可滑动
//     *
//     * @param supportSlideBack 滑动变量
//     */
//    public void setSupportSlideBack(boolean supportSlideBack) {
//        mSupportSlideBack = supportSlideBack;
//    }

//    @Override
//    public void finish() {
//        if (mSwipeBackHelper != null) {
//            mSwipeBackHelper.finishSwipeImmediately();
//            mSwipeBackHelper = null;
//        }
//        super.finish();
//    }

    // mLoadingDialog
    private CustomLoadingDialog getDialog(String tips) {
        if (mLoadingDialog == null) {
            mLoadingDialog = CustomLoadingDialog.instance(this, tips);
            mLoadingDialog.setCancelable(false);
        }
        return mLoadingDialog;
    }

    public void hideDialog() {
        if (mLoadingDialog != null)
            mLoadingDialog.hide();
    }

    public void showDialog(String tips) {
        if (!getDialog(tips).isShowing()) {
            getDialog(tips).show();
        }
    }

    public void dismissDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

}
