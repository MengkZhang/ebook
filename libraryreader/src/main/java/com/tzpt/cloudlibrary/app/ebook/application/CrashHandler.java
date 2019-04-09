package com.tzpt.cloudlibrary.app.ebook.application;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.tzpt.cloudlibrary.app.ebook.utils.SharePreferencesUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候。会调用该类的uncaughtException()方法
 *
 * @author
 */
public class CrashHandler implements UncaughtExceptionHandler {

    /**
     * RCSCrashHandler实例
     */
    private static volatile CrashHandler mInstance = null;
    /**
     * 系统默认的UncaughtException处理类
     */
    private UncaughtExceptionHandler mDefaultUEHandler;
    /**
     * 程序的Context对象
     */
    private Context mContext;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {


        if (mInstance == null) {
            synchronized (CrashHandler.class) {
                if (mInstance == null) {
                    mInstance = new CrashHandler();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化，获取系统默认的UncaughtException处理器, 设置该RCSCrashHandler为程序的默认处理器
     *
     * @param context 注册的Context对象
     */
    public void init(Context context) {
        mContext = context;
        mDefaultUEHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 在这个方法中增加对发生异常时的处理工作，比如清除内存，注销登陆。
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (null != mDefaultUEHandler) {
            clearNotification(mContext);
            // 常的函数处理，也可以自定义自己的处理函数(暂时没有实现)
            StringBuffer buffer = new StringBuffer();
            buffer.append("getErrorInfo==> \n").append(getErrorInfo(ex))
                    .append("getMobileInfo==> \n").append(getMobileInfo())
                    .append("getVersionInfo==> \n").append(getVersionInfo())
                    .append("crash_time==> \n").append(getNowTime());
            sendEmailToExceptionService(buffer.toString());
            mDefaultUEHandler.uncaughtException(thread, ex);
            //干掉当前的程序
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private String getNowTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    /**
     * 发送Exception 到我的邮箱
     */
    private void sendEmailToExceptionService(String message) {
        Log.e("message--->", "message--->" + message);
        if (message.contains(".service.CloudIntentService")) {
            SharePreferencesUtil.setString(mContext, "tzyt_log_service", message);
        } else {
            SharePreferencesUtil.setString(mContext, "tzyt_log", message);
        }
    }

    private void clearNotification(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }

    /**
     * 获取错误的信息
     *
     * @param arg1
     * @return
     */
    private String getErrorInfo(Throwable arg1) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        arg1.printStackTrace(pw);
        pw.close();
        String error = writer.toString();
        return error;
    }

    /**
     * 获取手机的硬件信息
     *
     * @return
     */
    private String getMobileInfo() {
        StringBuffer sb = new StringBuffer();
        //通过反射获取系统的硬件信息
        try {

            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                //暴力反射 ,获取私有的信息
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                sb.append(name + "=" + value);
                sb.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取手机的版本信息
     *
     * @return
     */
    private String getVersionInfo() {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }
}
