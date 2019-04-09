package com.tzpt.cloudlibrary.zlibrary.text.view;


import android.util.LruCache;

import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextModel;

/**
 * 游标管理器
 * Created by Administrator on 2017/4/8.
 */

public class CursorManager extends LruCache<Integer, ZLTextParagraphCursor> {
    private final ZLTextModel mModel;

    CursorManager(ZLTextModel model) {
        super(200); // max 200 cursors in the cache
        mModel = model;
    }

    @Override
    //当get方法获取不到缓存的时候调用
    protected ZLTextParagraphCursor create(Integer index) {
        return new ZLTextParagraphCursor(this, mModel, index);
    }
}
