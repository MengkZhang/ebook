package com.tzpt.cloudlibrary.zlibrary.core.view;

/**
 * 提供操作接口
 * Created by Administrator on 2017/4/7.
 */

public abstract class ZLView implements ZLViewEnums {
    private ZLPaintContext mViewContext = null;

    protected final void setContext(ZLPaintContext context) {
        mViewContext = context;
    }

    public final ZLPaintContext getContext() {
        return mViewContext;
    }

    protected final int getContextWidth() {
        return mViewContext.getWidth();
    }

    protected final int getContextHeight() {
        return mViewContext.getHeight();
    }

//    public abstract void preparePage(ZLPaintContext context, PageIndex pageIndex);

    public abstract void paint(ZLPaintContext context, PageIndex pageIndex);

    public abstract void onScrollingFinished(PageIndex pageIndex);

    public abstract boolean canScroll(PageIndex index);


}
