package com.tzpt.cloudlibrary.utils.badger.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.tzpt.cloudlibrary.utils.badger.BaseBadger;
import com.tzpt.cloudlibrary.utils.badger.exception.ShortcutBadgeException;
import com.tzpt.cloudlibrary.utils.badger.BroadcastHelper;

import java.util.Arrays;
import java.util.List;

public class AdwHomeBadger implements BaseBadger {

    public static final String INTENT_UPDATE_COUNTER = "org.adw.launcher.counter.SEND";
    public static final String PACKAGENAME = "PNAME";
    public static final String CLASSNAME = "CNAME";
    public static final String COUNT = "COUNT";

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws ShortcutBadgeException {

        Intent intent = new Intent(INTENT_UPDATE_COUNTER);
        intent.putExtra(PACKAGENAME, componentName.getPackageName());
        intent.putExtra(CLASSNAME, componentName.getClassName());
        intent.putExtra(COUNT, badgeCount);
        if (BroadcastHelper.canResolveBroadcast(context, intent)) {
            context.sendBroadcast(intent);
        } else {
            throw new ShortcutBadgeException("unable to resolve intent: " + intent.toString());
        }
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "org.adw.launcher",
                "org.adwfreak.launcher"
        );
    }
}
