package com.tzpt.cloudlibrary.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

/**
 * 开始拨打电话
 * Created by ZhiqiangJia on 2016-12-12.
 */

public class PhoneCallUtil {
    /**
     * 开始拨打电话
     *
     * @param context
     * @param phoneNumber
     */
    public static void startPhoneCall(Context context, String phoneNumber) {
        if (null == phoneNumber || TextUtils.isEmpty(phoneNumber)) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) ==
                PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + phoneNumber);
            intent.setData(data);
            context.startActivity(intent);
        }

    }
}
