package com.tzpt.cloudlibrary;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tzpt.cloudlibrary.modle.local.SharedPreferencesUtil;
import com.tzpt.cloudlibrary.ui.main.MainActivity;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.functions.Action1;

/**
 * 启动界面
 */
public class SplashScreenActivity extends AppCompatActivity implements SplashScreenContract.View {

    @BindView(R.id.bg_splash_iv)
    ImageView mBgSplashIv;

    private SplashScreenPresenter mPresenter;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //把背景图设置为空避免图片会一直存在于内存中
        getWindow().setBackgroundDrawable(null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        unbinder = ButterKnife.bind(this);

        mPresenter = new SplashScreenPresenter();
        mPresenter.attachView(this);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.INVISIBLE);
        }

        String launchImg = SharedPreferencesUtil.getInstance().getString("LAUNCH_IMG");
//        launchImg = null;
        if (!TextUtils.isEmpty(launchImg)) {
            initLocationPermission(launchImg, 5);
        } else {
            initLocationPermission(null, 0);
        }

        mPresenter.getLaunchImgData();
    }

    //初始化定位权限
    private void initLocationPermission(final String launchImg, final int time) {
        if (Build.VERSION.SDK_INT < 23) {
            mPresenter.startLocation();
            mPresenter.startTimer(launchImg, time);
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {//权限已授权
                            mPresenter.startLocation();
                            mPresenter.startTimer(launchImg, time);
                        } else if (permission.shouldShowRequestPermissionRationale) {//下次继续提示(应该显示请求许可理由)
                            mPresenter.startTimer(launchImg, time);
                        } else {//没有权限,不能使用权限模块-去设置权限
                            mPresenter.startLocation();
                            mPresenter.startTimer(launchImg, time);
                        }
                    }
                });
    }


    @Override
    public void timeToComplete() {
        MainActivity.startActivity(SplashScreenActivity.this);
        finish();
    }

    @Override
    public void loadAdImg(String launchImg) {
        //如果图片为空的时候 则不需要延时 直接到main
        GlideApp.with(SplashScreenActivity.this)
                .load(launchImg)
                .placeholder(R.mipmap.bg_splash_full_screen)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(mBgSplashIv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
        unbinder.unbind();
    }

}
