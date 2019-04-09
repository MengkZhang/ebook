package com.tzpt.cloudlibrary.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/11/9.
 */

public class GalleryDetailLoader extends AsyncTaskLoader<List<String>> {
    private Context mContext;

    public GalleryDetailLoader(Context context) {
        super(context);
        mContext = context;
    }


    @Override
    public List<String> loadInBackground() {
        String[] columns = new String[]{MediaStore.Images.Media.DATA};
        List<String> imgPaths = new ArrayList<>();
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                null, null, MediaStore.Images.Media.DATE_MODIFIED);
        if (cursor != null && cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                String currPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                imgPaths.add(currPath);
            }
            cursor.close();
        }
        return imgPaths;
    }
}
