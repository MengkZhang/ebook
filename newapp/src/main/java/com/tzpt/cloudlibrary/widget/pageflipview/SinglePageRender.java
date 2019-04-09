package com.tzpt.cloudlibrary.widget.pageflipview;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tzpt.cloudlibrary.zlibrary.core.application.ZLApplication;
import com.tzpt.cloudlibrary.zlibrary.core.view.ZLView;
import com.tzpt.cloudlibrary.zlibrary.core.view.ZLViewEnums;
import com.tzpt.cloudlibrary.zlibrary.ui.android.pageflip.Page;
import com.tzpt.cloudlibrary.zlibrary.ui.android.pageflip.PageFlip;
import com.tzpt.cloudlibrary.zlibrary.ui.android.pageflip.PageFlipState;
import com.tzpt.cloudlibrary.zlibrary.ui.android.view.BitmapManagerImpl;

/**
 * Created by Administrator on 2017/10/19.
 */

public class SinglePageRender extends PageRender {
    private final BitmapManagerImpl mBitmapManager;
    private int mReadCount;

    SinglePageRender(PageFlip pageFlip, Handler handler) {
        super(pageFlip, handler);
        mBitmapManager = new BitmapManagerImpl();
        mReadCount = 0;
    }

    /**
     * Draw frame
     */
    public void onDrawFrame() {
        // 1. delete unused textures
        mPageFlip.deleteUnusedTextures();
        Page page = mPageFlip.getFirstPage();

        mBitmapManager.setSize((int) page.width(), (int) page.height());

        // 2. handle drawing command triggered from finger moving and animating
        if (mDrawCommand == DRAW_MOVING_FRAME ||
                mDrawCommand == DRAW_ANIMATING_FRAME) {
            if (mPageFlip.getFlipState() == PageFlipState.FORWARD_FLIP) {
                // is forward flip
                // check if second texture of first page is valid, if not,
                // create new one
                if (!page.isSecondTextureSet()) {
                    page.setSecondTexture(mBitmap);
                }
            } else if (mPageFlip.getFlipState() == PageFlipState.BACKWARD_FLIP) {
                // in backward flip, check first texture of first page is valid
                if (!page.isFirstTextureSet()) {
                    page.setFirstTexture(mBitmap);
                }
            }

            // draw frame for page flip
            mPageFlip.drawFlipFrame();
        }
        // draw stationary page without flipping
        else if (mDrawCommand == DRAW_FULL_PAGE) {
//            if (!page.isFirstTextureSet()) {
            drawPageCurrent();
            page.setFirstTexture(mBitmap);
//            }

            mPageFlip.drawPageFrame();
        } else if (mDrawCommand == DRAW_END) {
            if (!page.isFirstTextureSet()) {
                drawPageCurrent();
                page.setFirstTexture(mBitmap);
            }

            mPageFlip.drawPageFrame();
        }

        // 3. send message to main thread to notify drawing is ended so that
        // we can continue to calculate next animation frame if need.
        // Remember: the drawing operation is always in GL thread instead of
        // main thread
        Message msg = Message.obtain();
        msg.what = MSG_ENDED_DRAWING_FRAME;
        msg.arg1 = mDrawCommand;
        mHandler.sendMessage(msg);
    }

    /**
     * Handle GL surface is changed
     *
     * @param width  surface width
     * @param height surface height
     */
    public void onSurfaceChanged(int width, int height) {
        // recycle bitmap resources if need

        if (mBitmap != null) {
            mBitmap.recycle();
        }

        // create bitmap and canvas for page
        Page page = mPageFlip.getFirstPage();
        mBitmap = Bitmap.createBitmap((int) page.width(), (int) page.height(),
                Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);
    }

    public void changeDrawCommand() {
        mDrawCommand = DRAW_FULL_PAGE;
    }

    @Override
    void reset() {
        mBitmapManager.reset();
    }

    /**
     * Handle ended drawing event
     * In here, we only tackle the animation drawing event, If we need to
     * continue requesting render, please return true. Remember this function
     * will be called in main thread
     *
     * @param what event type
     * @return ture if need render again
     */
    public boolean onEndedDrawing(int what) {
        if (what == DRAW_ANIMATING_FRAME) {
            boolean isAnimating = mPageFlip.animating();
            // continue animating
            if (isAnimating) {
                mDrawCommand = DRAW_ANIMATING_FRAME;
                return true;
            } else {
                // animation is finished
                final PageFlipState state = mPageFlip.getFlipState();
                // update page number for backward flip
                if (state == PageFlipState.END_WITH_BACKWARD) {
                    // don't do anything on page number since mPageNo is always
                    // represents the FIRST_TEXTURE no;

                    mBitmapManager.reset();
                    final ZLView view = ZLApplication.Instance().getCurrentView();
                    view.onScrollingFinished(ZLViewEnums.PageIndex.previous);
                }
                // update page number and switch textures for forward flip
                else if (state == PageFlipState.END_WITH_FORWARD) {
                    mPageFlip.getFirstPage().setFirstTextureWithSecond();

                    mBitmapManager.reset();
                    final ZLView view = ZLApplication.Instance().getCurrentView();
                    view.onScrollingFinished(ZLViewEnums.PageIndex.next);
                } else if (state == PageFlipState.END_WITH_RESTORE_FORWARD) {
                    mPageFlip.getFirstPage().setFirstTextureWithSecond();
                    mBitmapManager.reset();
                }

                ++mReadCount;
                if (mOnReadPageCountListener != null) {
                    mOnReadPageCountListener.onReadPageCount(mReadCount);
                }

                mDrawCommand = DRAW_END;
                return true;
            }
        }
        return false;
    }

    /**
     * Draw current page
     */
    private void drawPageCurrent() {
        final int width = mCanvas.getWidth();
        final int height = mCanvas.getHeight();
        Paint p = new Paint();
        p.setFilterBitmap(true);

        // 1. draw background bitmap
        Bitmap background = mBitmapManager.getBitmap(ZLViewEnums.PageIndex.current);
        Rect rect = new Rect(0, 0, width, height);
        mCanvas.drawBitmap(background, null, rect, p);
    }

    /**
     * Draw next page
     */
    private void drawPageNext() {
        final int width = mCanvas.getWidth();
        final int height = mCanvas.getHeight();
        Paint p = new Paint();
        p.setFilterBitmap(true);

        // 1. draw background bitmap
        Bitmap background = mBitmapManager.getBitmap(ZLViewEnums.PageIndex.next);
        Rect rect = new Rect(0, 0, width, height);
        mCanvas.drawBitmap(background, null, rect, p);
    }

    /**
     * Draw previous page
     */
    private void drawPagePre() {
        final int width = mCanvas.getWidth();
        final int height = mCanvas.getHeight();
        Paint p = new Paint();
        p.setFilterBitmap(true);

        // 1. draw background bitmap
        Bitmap background = mBitmapManager.getBitmap(ZLViewEnums.PageIndex.previous);
        Rect rect = new Rect(0, 0, width, height);
        mCanvas.drawBitmap(background, null, rect, p);
    }

    /**
     * If page can flip forward
     * 是否还有下一页
     *
     * @return true if it can flip forward
     */
    public boolean canFlipForward() {
        final ZLView view = ZLApplication.Instance().getCurrentView();
        if (view.canScroll(ZLViewEnums.PageIndex.next)) {
            Page page = mPageFlip.getFirstPage();
            //提前准备Bitmap防止翻页动画卡顿
            if (!page.isSecondTextureSet()) {
                drawPageNext();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * If page can flip backward
     * 是否还有上一页
     *
     * @return true if it can flip backward
     */
    public boolean canFlipBackward() {
        final ZLView view = ZLApplication.Instance().getCurrentView();
        if (view.canScroll(ZLViewEnums.PageIndex.previous)) {
            mPageFlip.getFirstPage().setSecondTextureWithFirst();
            Page page = mPageFlip.getFirstPage();
            if (!page.isFirstTextureSet()) {
                drawPagePre();
            }
            return true;
        } else {
            return false;
        }
    }
}
