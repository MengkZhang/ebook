package com.tzpt.cloudlibrary.utils.badger.impl;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.utils.Utils;
import com.tzpt.cloudlibrary.utils.badger.BaseBadger;
import com.tzpt.cloudlibrary.utils.badger.exception.ShortcutBadgeException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class XiaomiHomeBadger implements BaseBadger {

    private NotificationManager mNotificationManager;

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws ShortcutBadgeException {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            Notification.Builder builder = new Notification.Builder(Utils.getContext());
            Notification.Builder builder = new Notification.Builder(CloudLibraryApplication.getAppContext());
            Notification notification = builder.build();

            try {
                Field field = notification.getClass().getDeclaredField("extraNotification");
                Object extraNotification = field.get(notification);
                Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
                method.invoke(extraNotification, badgeCount);
                if (null == mNotificationManager) {
                    mNotificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                }
                mNotificationManager.cancel(1);
                mNotificationManager.notify(1, notification);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "com.miui.miuilite",
                "com.miui.home",
                "com.miui.miuihome",
                "com.miui.miuihome2",
                "com.miui.mihome",
                "com.miui.mihome2",
                "com.i.miui.launcher"
        );
    }
}
