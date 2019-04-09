package com.tzpt.cloudlibrary.app.ebook.utils;

import android.net.Uri;
import android.text.TextUtils;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tzpt.cloudlibrary.app.ebook.R;

/**
 * 加载图片
 * Created by ZhiqiangJia on 2017-03-06.
 */
public class ImageLoader {

    public static void loadImage(SimpleDraweeView imageView, String imagePath) {
        //设置图片
        if (!TextUtils.isEmpty(imagePath)) {
            imageView.setImageURI(Uri.parse(imagePath));
        } else {
            imageView.setImageResource(R.mipmap.ic_nopicture);
        }
    }

}
