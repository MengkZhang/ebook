package com.tzpt.cloudlibrary.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by Administrator on 2017/6/6.
 */

public class ActivityUtils {
    public static void addFragmentToActivity(FragmentManager fragmentManager,
                                             Fragment fragment, int frameId) {
        if (fragmentManager != null && fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(frameId, fragment);
            transaction.commit();
        }
    }

    public static void showFragmentInActivity(FragmentManager fragmentManager,
                                                 Fragment currentFragment, Fragment fragment, int frameId) {
        if (fragmentManager != null && fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            if (!fragment.isAdded()) {
                transaction.add(frameId, fragment).commit();
            } else {
                transaction.show(fragment).commit();
            }
        }
    }

    public static void hideFragmentInActivity(FragmentManager fragmentManager,
                                              Fragment fragment) {
        if (fragmentManager != null && fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(fragment);
            transaction.commit();
        }
    }
}
