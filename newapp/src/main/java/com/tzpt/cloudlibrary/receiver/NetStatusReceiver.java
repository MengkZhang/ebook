package com.tzpt.cloudlibrary.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tzpt.cloudlibrary.modle.VideoRepository;

/**
 * Created by Administrator on 2018/8/15.
 */

public class NetStatusReceiver extends BroadcastReceiver{
    private final String TAG = "NetworkChangedReceiver";
    public static boolean mIsWifiNetConnected = false;
    public static boolean mIsMobileNetConnected = false;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 101:
                    Log.e(TAG, "WIFI已连接");
                    VideoRepository.getInstance().startDownloadAllError();
                    break;
                case 102:
                    Log.e(TAG, "移动数据已连接");
                    VideoRepository.getInstance().dealDownloadWifiToMobile();
                    break;
                case 103:
                    Log.e(TAG, "无网络");
                    VideoRepository.getInstance().dealNoNetForDownload();
                    break;
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        //获得ConnectivityManager对象
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取WIFI连接的信息
        NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //获取移动数据连接的信息
        NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        mHandler.removeMessages(103);
        if (wifiNetworkInfo.isConnected()) {
            if (!mIsWifiNetConnected) {
                mHandler.sendEmptyMessage(101);
            }
            mIsWifiNetConnected = true;
            mIsMobileNetConnected = false;
        } else if (dataNetworkInfo.isConnected()) {
            if (!mIsMobileNetConnected) {
                mHandler.sendEmptyMessage(102);
            }
            mIsMobileNetConnected = true;
            mIsWifiNetConnected = false;
        } else {
            mIsWifiNetConnected = false;
            mIsMobileNetConnected = false;
            mHandler.sendEmptyMessage(103);
        }
    }
}
