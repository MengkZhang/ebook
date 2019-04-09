package com.tzpt.cloudlibrary;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.local.SharedPreferencesUtil;
import com.tzpt.cloudlibrary.modle.local.db.DBManager;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.ui.search.SearchManager;
import com.tzpt.cloudlibrary.ui.share.ShareConfig;
import com.tzpt.cloudlibrary.utils.Utils;
import com.tzpt.cloudlibrary.utils.VersionUtils;
import com.tzpt.cloudlibrary.zlibrary.ui.android.library.ZLAndroidLibrary;
import com.umeng.commonsdk.UMConfigure;

/**
 * 云图应用入口
 * Created by Administrator on 2017/5/22.
 */
public class CloudLibraryApplication extends Application {

    private static final String VERSION_NUMBER = "app_version_number";
    private static final String LOCATION = "LocationBean";
    private static Context mInstance;
//    public static String TOKEN = "";
    public static RxBus mRxBus = new RxBus();
    public static boolean mMobileNetTip = true;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        Utils.init(this);
        //注册侧滑
        //registerActivityLifecycleCallbacks(ActivityLifecycleHelper.build());
        SharedPreferencesUtil.init(this, "tzpt_cloudlibrary", 0);
        //配置微博
        AuthInfo authInfo = new AuthInfo(this, ShareConfig.WEIBO_APP_KEY, ShareConfig.REDIRECT_URL, ShareConfig.SCOPE);
        WbSdk.install(this, authInfo);
        //配置崩溃日志
        CrashHandler mCrashHandler = CrashHandler.getInstance();
        mCrashHandler.init(this);

        new ZLAndroidLibrary(this);

        configUmeng();

        initDatabase();
        initUserInfo();

        if (Build.VERSION.SDK_INT > 23) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
        //配置清除指定的本地数据
        forceClearSharedPreferencesData();
    }

    //强制清除指定的本地数据
    private void forceClearSharedPreferencesData() {
        //指定版本清空定位对象
        int nowVersion = VersionUtils.getVersionNumber();
        if (nowVersion == 178) {
            int lastVersion = SharedPreferencesUtil.getInstance().getInt(VERSION_NUMBER);
            if (lastVersion != nowVersion) {
                SharedPreferencesUtil.getInstance().putInt(VERSION_NUMBER, nowVersion);
                //清除所有搜索本地内容
                SearchManager.clearAllSearchListTag();
            }
        }
    }

    /**
     * 配置友盟信息
     */
    private void configUmeng() {
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "");

        UmengHelper.setUmengDebugMode();
        UmengHelper.setLogEnableEncrypt();
        UmengHelper.setScenarioType(this);
        UmengHelper.setOpenActivityDurationTrackFalse();
    }

    public static Context getAppContext() {
        return mInstance;
    }

    private void initDatabase() {
        DBManager.init(this);
    }

    private void initUserInfo() {
        UserRepository.initLoginUser();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
