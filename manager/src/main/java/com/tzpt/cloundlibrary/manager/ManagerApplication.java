package com.tzpt.cloundlibrary.manager;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.tzpt.cloundlibrary.manager.log.CrashHandler;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.local.SharedPreferencesUtil;
import com.tzpt.cloundlibrary.manager.utils.Utils;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/6/20.
 */

public class ManagerApplication extends Application {
    public static String TOKEN = "";

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferencesUtil.init(this, "tzpt_cloudlibrary_manager", 0);
        Utils.init(this);
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler.getInstance(this));

        TOKEN = DataRepository.getInstance().getToken();

        JPushInterface.setDebugMode(false);   // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
        getResources();
    }

    /**
     * 配置字体大小不受系统字体改变而改变
     *
     * @return
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        config.fontScale = 1;
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
}
