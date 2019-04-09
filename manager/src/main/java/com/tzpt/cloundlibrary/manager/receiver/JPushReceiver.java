package com.tzpt.cloundlibrary.manager.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.base.Constant;
import com.tzpt.cloundlibrary.manager.bean.MsgCountBean;
import com.tzpt.cloundlibrary.manager.ui.activity.MainActivity;
import com.tzpt.cloundlibrary.manager.ui.activity.MessageActivity;
import com.tzpt.cloundlibrary.manager.ui.activity.SplashActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/7/25.
 */

public class JPushReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            if (isForeground(context, "com.tzpt.cloundlibrary.manager.ui.activity.MainActivity")) {
                Intent intent1 = new Intent(context, MessageActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            } else {
                SplashActivity.startActivity(context);
            }
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            MsgCountBean msgCountBean = new MsgCountBean();
            msgCountBean.refresh = true;
            EventBus.getDefault().post(msgCountBean);
        }

    }

    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert am != null;
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        for (ActivityManager.RunningTaskInfo taskInfo : list) {
            if (taskInfo.baseActivity.getClassName().contains(className)) {
                return true;
            }
        }
        return false;
    }

}
