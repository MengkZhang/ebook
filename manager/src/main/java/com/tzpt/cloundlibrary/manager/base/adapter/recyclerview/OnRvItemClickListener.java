package com.tzpt.cloundlibrary.manager.base.adapter.recyclerview;

import android.view.View;

/**
 * Created by Administrator on 2017/6/12.
 */

public interface OnRvItemClickListener<T> {
    void onItemClick(View view, int position, T data);
}
