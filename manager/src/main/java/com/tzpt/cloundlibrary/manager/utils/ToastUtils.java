package com.tzpt.cloundlibrary.manager.utils;

import android.widget.Toast;

/**
 * Created by Administrator on 2017/8/8.
 */

public class ToastUtils {

    private static Toast mToast;

    /********************** 非连续弹出的Toast ***********************/
    public static void showSingleToast(int resId) { //R.string.**
        getSingleToast(resId, Toast.LENGTH_SHORT).show();
    }

    public static void showSingleToast(String text) {
        getSingleToast(text, Toast.LENGTH_SHORT).show();
    }

    public static void showSingleLongToast(int resId) {
        getSingleToast(resId, Toast.LENGTH_LONG).show();
    }

    public static void showSingleLongToast(String text) {
        getSingleToast(text, Toast.LENGTH_LONG).show();
    }

    /*********************** 连续弹出的Toast ************************/
    public static void showToast(int resId) {
        getToast(resId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String text) {
        getToast(text, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(int resId) {
        getToast(resId, Toast.LENGTH_LONG).show();
    }

    public static void showLongToast(String text) {
        getToast(text, Toast.LENGTH_LONG).show();
    }

    private static Toast getSingleToast(int resId, int duration) { // 连续调用不会连续弹出，只是替换文本
        return getSingleToast(Utils.getContext().getResources().getText(resId).toString(), duration);
    }

    private static Toast getSingleToast(String text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(Utils.getContext(), text, duration);
        } else {
            mToast.setText(text);
        }
        return mToast;
    }

    private static Toast getToast(int resId, int duration) { // 连续调用会连续弹出
        return getToast(Utils.getContext().getResources().getText(resId).toString(), duration);
    }

    private static Toast getToast(String text, int duration) {
        return Toast.makeText(Utils.getContext(), text, duration);
    }
}
