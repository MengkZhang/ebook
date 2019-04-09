package com.tzpt.cloudlibrary.utils.badger;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;

import com.tzpt.cloudlibrary.utils.badger.exception.ShortcutBadgeException;
import com.tzpt.cloudlibrary.utils.badger.impl.AdwHomeBadger;
import com.tzpt.cloudlibrary.utils.badger.impl.ApexHomeBadger;
import com.tzpt.cloudlibrary.utils.badger.impl.AsusHomeBadger;
import com.tzpt.cloudlibrary.utils.badger.impl.DefaultBadger;
import com.tzpt.cloudlibrary.utils.badger.impl.EverythingMeHomeBadger;
import com.tzpt.cloudlibrary.utils.badger.impl.HuaweiHomeBadger;
import com.tzpt.cloudlibrary.utils.badger.impl.LGHomeBadger;
import com.tzpt.cloudlibrary.utils.badger.impl.NewHtcHomeBadger;
import com.tzpt.cloudlibrary.utils.badger.impl.NovaHomeBadger;
import com.tzpt.cloudlibrary.utils.badger.impl.OPPOHomeBader;
import com.tzpt.cloudlibrary.utils.badger.impl.SamsungHomeBadger;
import com.tzpt.cloudlibrary.utils.badger.impl.SonyHomeBadger;
import com.tzpt.cloudlibrary.utils.badger.impl.VivoHomeBadger;
import com.tzpt.cloudlibrary.utils.badger.impl.XiaomiHomeBadger;
import com.tzpt.cloudlibrary.utils.badger.impl.ZTEHomeBadger;
import com.tzpt.cloudlibrary.utils.badger.impl.ZukHomeBadger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public final class ShortcutBadger {

    private static final String LOG_TAG = "ShortcutBadger";
    private static final int SUPPORTED_CHECK_ATTEMPTS = 3;

    private static final List<Class<? extends BaseBadger>> BADGERS = new LinkedList<Class<? extends BaseBadger>>();

    private volatile static Boolean sIsBadgeCounterSupported;
    private final static Object sCounterSupportedLock = new Object();

    static {
        BADGERS.add(AdwHomeBadger.class);
        BADGERS.add(ApexHomeBadger.class);
        BADGERS.add(DefaultBadger.class);
        BADGERS.add(NewHtcHomeBadger.class);
        BADGERS.add(NovaHomeBadger.class);
        BADGERS.add(SonyHomeBadger.class);
        BADGERS.add(AsusHomeBadger.class);
        BADGERS.add(HuaweiHomeBadger.class);
        BADGERS.add(OPPOHomeBader.class);
        BADGERS.add(SamsungHomeBadger.class);
        BADGERS.add(ZukHomeBadger.class);
        BADGERS.add(VivoHomeBadger.class);
        BADGERS.add(ZTEHomeBadger.class);
        BADGERS.add(EverythingMeHomeBadger.class);
        BADGERS.add(XiaomiHomeBadger.class);
        BADGERS.add(LGHomeBadger.class);
    }

    private static BaseBadger sShortcutBadger;
    private static ComponentName sComponentName;

    public static boolean applyCount(Context context, int badgeCount) {
        try {
            applyCountOrThrow(context, badgeCount);
            return true;
        } catch (ShortcutBadgeException e) {
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "Unable to execute badge", e);
            }
            return false;
        }
    }

    public static void applyCountOrThrow(Context context, int badgeCount) throws ShortcutBadgeException {
        if (sShortcutBadger == null) {
            boolean launcherReady = initBadger(context);

            if (!launcherReady)
                throw new ShortcutBadgeException("No default launcher available");
        }

        try {
            sShortcutBadger.executeBadge(context, sComponentName, badgeCount);
        } catch (Exception e) {
            throw new ShortcutBadgeException("Unable to execute badge", e);
        }
    }

    public static boolean removeCount(Context context) {
        return applyCount(context, 0);
    }


    public static void removeCountOrThrow(Context context) throws ShortcutBadgeException {
        applyCountOrThrow(context, 0);
    }

    public static boolean isBadgeCounterSupported(Context context) {
        if (sIsBadgeCounterSupported == null) {
            synchronized (sCounterSupportedLock) {
                if (sIsBadgeCounterSupported == null) {
                    String lastErrorMessage = null;
                    for (int i = 0; i < SUPPORTED_CHECK_ATTEMPTS; i++) {
                        try {
                            Log.i(LOG_TAG, "Checking if platform supports badge counters, attempt "
                                    + String.format("%d/%d.", i + 1, SUPPORTED_CHECK_ATTEMPTS));
                            if (initBadger(context)) {
                                sShortcutBadger.executeBadge(context, sComponentName, 0);
                                sIsBadgeCounterSupported = true;
                                Log.i(LOG_TAG, "Badge counter is supported in this platform.");
                                break;
                            } else {
                                lastErrorMessage = "Failed to initialize the badge counter.";
                            }
                        } catch (Exception e) {
                            lastErrorMessage = e.getMessage();
                        }
                    }

                    if (sIsBadgeCounterSupported == null) {
                        Log.w(LOG_TAG, "Badge counter seems not supported for this platform: "
                                + lastErrorMessage);
                        sIsBadgeCounterSupported = false;
                    }
                }
            }
        }
        return sIsBadgeCounterSupported;
    }


    private static boolean initBadger(Context context) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (launchIntent == null) {
            Log.e(LOG_TAG, "Unable to find launch intent for package " + context.getPackageName());
            return false;
        }

        sComponentName = launchIntent.getComponent();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resolveInfos) {
            String currentHomePackage = resolveInfo.activityInfo.packageName;

            for (Class<? extends BaseBadger> badger : BADGERS) {
                BaseBadger shortcutBadger = null;
                try {
                    shortcutBadger = badger.newInstance();
                } catch (Exception ignored) {
                }
                if (shortcutBadger != null && shortcutBadger.getSupportLaunchers().contains(currentHomePackage)) {
                    sShortcutBadger = shortcutBadger;
                    break;
                }
            }
            if (sShortcutBadger != null) {
                break;
            }
        }
        if (sShortcutBadger == null) {
            if (Build.MANUFACTURER.equalsIgnoreCase("ZUK"))
                sShortcutBadger = new ZukHomeBadger();
            else if (Build.MANUFACTURER.equalsIgnoreCase("OPPO"))
                sShortcutBadger = new OPPOHomeBader();
            else if (Build.MANUFACTURER.equalsIgnoreCase("VIVO"))
                sShortcutBadger = new VivoHomeBadger();
            else if (Build.MANUFACTURER.equalsIgnoreCase("ZTE"))
                sShortcutBadger = new ZTEHomeBadger();
            else
                sShortcutBadger = new DefaultBadger();
        }

        return true;
    }

    private ShortcutBadger() {
    }

}
