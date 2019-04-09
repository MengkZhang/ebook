package com.tzpt.cloudlibrary.utils.badger.impl;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.tzpt.cloudlibrary.utils.badger.BaseBadger;
import com.tzpt.cloudlibrary.utils.badger.exception.ShortcutBadgeException;

import java.util.ArrayList;
import java.util.List;

public class ZTEHomeBadger implements BaseBadger {

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount)
            throws ShortcutBadgeException {
        Bundle extra = new Bundle();
        extra.putInt("app_badge_count", badgeCount);
        extra.putString("app_badge_component_name", componentName.flattenToString());

        context.getContentResolver().call(
                Uri.parse("content://com.android.launcher3.cornermark.unreadbadge"),
                "setAppUnreadCount", null, extra);
    }

    @Override
    public List<String> getSupportLaunchers() {
        return new ArrayList<String>(0);
    }
} 

