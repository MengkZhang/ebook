package com.tzpt.cloudlibrary.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2019/1/15.
 */

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spanCount;

    public GridSpacingItemDecoration(int spanCount) {
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int spacing = (parent.getMeasuredWidth() - view.getLayoutParams().width * spanCount) / (spanCount - 1);
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        outRect.left = column * spacing / spanCount;
        outRect.right = spacing - (column + 1) * spacing / spanCount;
    }
}
