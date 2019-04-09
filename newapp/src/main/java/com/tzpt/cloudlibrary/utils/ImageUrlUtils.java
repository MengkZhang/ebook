package com.tzpt.cloudlibrary.utils;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.CloudLibraryApplication;

import java.net.URLEncoder;

/**
 * Created by Administrator on 2017/6/7.
 */

public class ImageUrlUtils {
    private static final String IMAGE_IP = "http://img.ytsg.cn/";

    /**
     * 获取原图片下载路径
     *
     * @param imagePath
     * @return
     */
    public static String getDownloadOriginalImagePath(String imagePath) {
        if (null == imagePath || TextUtils.isEmpty(imagePath)) {
            return "";
        }
        StringBuilder imagePathSB = new StringBuilder();
        try {
            imagePathSB.append(IMAGE_IP);
            if (!isChinese(imagePath)) {
                return imagePathSB.append(imagePath).toString();
            }
            String newImagePath = URLEncoder.encode(imagePath, "UTF-8");
            return imagePathSB.append(newImagePath).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePathSB.append(imagePath).toString();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
//        final float scale = Utils.getContext().getResources().getDisplayMetrics().density;
        final float scale = CloudLibraryApplication.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    // 判断一个字符是否是中文
    private static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }

    // 判断一个字符串是否含有中文
    private static boolean isChinese(String str) {
        if (str == null) return false;
        for (char c : str.toCharArray()) {
            if (isChinese(c)) return true;// 有一个中文字符就返回
        }
        return false;
    }
}
