package com.tzpt.cloudlibrary.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.bean.NetWordMsg;

import org.greenrobot.eventbus.EventBus;

/**
 * 检查网络工具
 * Created by ZhiqiangJia on 2017-02-24.
 */

public class CheckInternetUtil {

    /**
     * 如果当前网络为3G
     *
     * @param context
     */
    public static boolean checkInternetTypeIs3G(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);//获得网络连接管理器
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();//获取网络连接信息
        if (activeInfo != null && activeInfo.isAvailable()) {
            if (activeInfo.getType() == ConnectivityManager.TYPE_WIFI) {//连接的wifi
                return false;
            } else if (activeInfo.getType() == ConnectivityManager.TYPE_MOBILE) {//连接的手机移动网络
                return true;
            } else if (activeInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                return false;
            }
        }
        return false;
    }

    /**
     * 是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean checkInternetisAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);//获得网络连接管理器
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();//获取网络连接信息
        return (activeInfo != null && activeInfo.isAvailable());
    }


    /**
     * wifi 是否打开
     *
     * @return
     */
    public static boolean isWifiOpen() {
        ConnectivityManager cm = (ConnectivityManager) CloudLibraryApplication.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        if (!info.isAvailable() || !info.isConnected()) return false;
        if (info.getType() != ConnectivityManager.TYPE_WIFI) return false;
        return true;
    }

    /**
     * 网络是否活跃
     *
     * @return
     */
    public static boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) CloudLibraryApplication.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }
        if (!info.isAvailable() || !info.isConnected()) {
            return false;
        }
        return true;
    }

    public static final int NET_WORK_WIFI = 1000;       //wifi
    public static final int NET_WORK_MOBILE = 1001;     //mobile
    public static final int NET_WORK_NONE = 1002;       //none

    private static int sCurrentNetWorkState = NET_WORK_WIFI;

    public static int checkNetWork(Context context, boolean sendMsg) {
        sCurrentNetWorkState = NET_WORK_WIFI;

        ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cManager) {
            NetworkInfo networkInfo = cManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                switch (networkInfo.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                        sCurrentNetWorkState = NET_WORK_WIFI;
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        sCurrentNetWorkState = NET_WORK_MOBILE;
                        break;
                }
            } else {
                sCurrentNetWorkState = NET_WORK_NONE;
            }
        }
        if (sendMsg) {
            NetWordMsg msg = new NetWordMsg();
            msg.mNetWorkState = sCurrentNetWorkState;
            EventBus.getDefault().post(msg);
        }
        return sCurrentNetWorkState;
    }
}
