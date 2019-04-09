package com.tzpt.cloundlibrary.manager.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 判断网络工具
 * Created by ZhiqiangJia on 2017-07-16.
 */
public class NetworkUtils {

    public static boolean isAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);//获得网络连接管理器
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();//获取网络连接信息
        return (activeInfo != null && activeInfo.isAvailable());
    }
}
