package com.tzpt.cloudlibrary.app.ebook.books.bookelectronicshelf;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * 自定义RecyclerView 条目的分割线
 * Created by ZhiqiangJia
 */
public class GridItemHeaderDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};//利用系统自带的listDivider数组;可以在theme.xml中找到该属性的使用情况
    private Drawable mDivider;//绘制
    private boolean hasHeader;//是否有头
    private int spanCount;//跨度
    private int spacing;//间隔宽高
    private boolean includeEdge;//是否包括边缘
    private boolean drawLine;//是否有边框

    public GridItemHeaderDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);//获取属性组
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    /**
     * @param context      上下文
     * @param header      是否有头部文件
     * @param spanCount   item个数
     * @param spacing     间隔宽高
     * @param includeEdge 是否有边框 （item 与头部衔接）
     * @param drawLine    //是否有边框 （有效）
     */
    public GridItemHeaderDecoration(Context context, boolean header, int spanCount, int spacing, boolean includeEdge, boolean drawLine) {
        this(context);
        hasHeader = header;
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
        this.drawLine = drawLine;
    }

    /**
     *  1、画分割线
     * @param c         画布
     * @param parent    RecyclerView
     * @param state     RecyclerView的状态信息
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (drawLine) {
            drawHorizontal(c, parent);//画水平分割线
            drawVertical(c, parent);//画垂直分割线
        }
    }

    /**
     * 获取 跨度数量
     * @param parent RecyclerView
     * @return 跨度数量
     */
    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {//如果是Grid布局管理器

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {//交错的grid布局管理器
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    /**
     * 画水平分割线
     * @param c
     * @param parent
     */
    public void drawHorizontal(Canvas c, RecyclerView parent) {

        int childCount = parent.getChildCount();//获取子孩子的数量
        for (int i = 0; i < childCount; i++) {//遍历
            if (i == 0) {
                return;
            }
            final View child = parent.getChildAt(i);//获取到每个子view
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();//获取到参数管理器
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin
                    + mDivider.getIntrinsicWidth();//mDivider.getIntrinsicWidth()固有宽度
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();//加上固有的高度
            mDivider.setBounds(left, top, right, bottom);//绘制范围
            mDivider.draw(c);//绘制
        }
    }

    /**
     * 绘制垂直的分割线
     * @param c 画布
     * @param parent RecyclerView
     */
    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    /**
     * 是否最后一列 水平和垂直的处理不一样
     * @param parent
     * @param pos item的位置
     * @param spanCount
     * @param childCount
     * @return
     */
    private boolean isLastColum(RecyclerView parent, int pos, int spanCount,
                                int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
            {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }

    /**
     * 2、设置分割线的size
     * getItemOffsets 可以通过outRect.set()为每个Item设置一定的偏移量，主要用于绘制Decorator
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);

        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();

        int pos = position;

        if (hasHeader) {
            if (position == 0) {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());//矩形范围
                return;
            } else {
                pos = position - 1;
                int column = pos % spanCount; // item column 行数

                if (includeEdge) {
                    outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                    outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                    if (pos < spanCount) { // top edge
                        outRect.top = spacing;
                    }
                    outRect.bottom = spacing; // item bottom
                } else {
                    outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                    outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                    if (pos >= spanCount) {
                        outRect.top = spacing; // item top
                    }
                }
            }
        } else {
            if (isLastColum(parent, pos, spanCount, childCount)) {//???????????
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
            } else {//?????????????????
                outRect.set(0, 0, mDivider.getIntrinsicWidth(),
                        mDivider.getIntrinsicHeight());
            }

        }


    }
}
