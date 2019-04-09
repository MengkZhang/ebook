package com.tzpt.cloudlibrary.utils.badger;

import android.content.ComponentName;
import android.content.Context;

import com.tzpt.cloudlibrary.utils.badger.exception.ShortcutBadgeException;

import java.util.List;

/**
 * Created by tonyjia on 2018/5/23.
 */
public interface BaseBadger {

    void executeBadge(Context context, ComponentName componentName, int badgeCount) throws ShortcutBadgeException;

    List<String> getSupportLaunchers();
}
