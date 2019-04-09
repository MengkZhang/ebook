package com.tzpt.cloudlibrary.widget.recyclerview.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 设置recyclerView 第一行高度
 * Created by ZhiqiangJia on 2019/2/26.
 */

public class GridFirstRowTopDecoration extends RecyclerView.ItemDecoration {
    private int mSpanCount;
    private int mRowTopHeight;

    public GridFirstRowTopDecoration(int spanCount, int rowTopHeight) {
        this.mSpanCount = spanCount;
        this.mRowTopHeight = rowTopHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        //在第一行设置高度
        if (position / mSpanCount == 0) {
            outRect.top = mRowTopHeight;
        } else {
            outRect.top = 0;
        }
        //配置左右间距
        int spacing = (parent.getMeasuredWidth() - view.getLayoutParams().width * mSpanCount) / (mSpanCount - 1);
        int column = position % mSpanCount;

        outRect.left = column * spacing / mSpanCount;
        outRect.right = spacing - (column + 1) * spacing / mSpanCount;

    }
}
