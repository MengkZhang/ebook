package com.tzpt.cloudlibrary.widget.pageflipview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;

import com.tzpt.cloudlibrary.zlibrary.ui.android.pageflip.OnPageFlipListener;
import com.tzpt.cloudlibrary.zlibrary.ui.android.pageflip.PageFlip;

/**
 * Created by Administrator on 2017/10/19.
 */

public abstract class PageRender implements OnPageFlipListener {

    public final static int MSG_ENDED_DRAWING_FRAME = 1;
    private final static String TAG = "PageRender";

    final static int DRAW_MOVING_FRAME = 0;
    final static int DRAW_ANIMATING_FRAME = 1;
    final static int DRAW_FULL_PAGE = 2;
    final static int DRAW_END = 3;

    int mDrawCommand;
    Bitmap mBitmap;
    Canvas mCanvas;
    Handler mHandler;
    PageFlip mPageFlip;
    OnReadPageCountListener mOnReadPageCountListener;

    public PageRender(PageFlip pageFlip, Handler handler) {
        mPageFlip = pageFlip;
        mDrawCommand = DRAW_FULL_PAGE;
        mCanvas = new Canvas();
        mPageFlip.setListener(this);
        mHandler = handler;
    }

    /**
     * Release resources
     */
    public void release() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }

        mPageFlip.setListener(null);
        mCanvas = null;
    }

    /**
     * Handle finger moving event
     *
//     * @param x x coordinate of finger moving
//     * @param y y coordinate of finger moving
     * @return true if event is handled
     */
    public boolean onFingerMove() {
        mDrawCommand = DRAW_MOVING_FRAME;
        return true;
    }

    /**
     * Handle finger up event
     *
//     * @param x x coordinate of finger up
//     * @param y y coordinate of inger up
     * @return true if event is handled
     */
    public boolean onFingerUp() {
        if (mPageFlip.animating()) {
            mDrawCommand = DRAW_ANIMATING_FRAME;
            return true;
        }

        return false;
    }

    /**
     * Render page frame
     */
    abstract void onDrawFrame();

    /**
     * Handle surface changing event
     *
     * @param width  surface width
     * @param height surface height
     */
    abstract void onSurfaceChanged(int width, int height);

    /**
     * Handle drawing ended event
     *
     * @param what draw command
     * @return true if render is needed
     */
    abstract boolean onEndedDrawing(int what);

    abstract void changeDrawCommand();

    abstract void reset();

    void setOnReadPageCountListener(OnReadPageCountListener listener){
        mOnReadPageCountListener = listener;
    }
}
