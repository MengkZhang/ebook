package com.tzpt.cloudlibrary.widget.pageflipview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.tzpt.cloudlibrary.zlibrary.ui.android.pageflip.PageFlip;
import com.tzpt.cloudlibrary.zlibrary.ui.android.pageflip.PageFlipException;

import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2017/10/19.
 */

public class PageFlipView extends GLSurfaceView implements GLSurfaceView.Renderer, GestureDetector.OnGestureListener {

    private final static String TAG = "PageFlipView";

    Handler mHandler;
    PageFlip mPageFlip;
    PageRender mPageRender;
    ReentrantLock mDrawLock;

    private GestureDetector mGestureDetector;
    private NavigationOperateListener mListener;
    private OnReadPageCountListener mReadPageCountListener;
    private RestrictReadListener mRestrictReadListener;
    private boolean mIsMoved;
    private boolean mIsRestrict;

    public PageFlipView(Context context) {
        super(context);
        inti(context);
    }

    public PageFlipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inti(context);
    }

    public void inti(Context context) {
        mGestureDetector = new GestureDetector(context, this);

        // create handler to tackle message
        newHandler();

        // create PageFlip
        mPageFlip = new PageFlip(context);
        mPageFlip.setSemiPerimeterRatio(0.8f)
                .setShadowWidthOfFoldEdges(5, 60, 0.3f)
                .setShadowWidthOfFoldBase(5, 80, 0.4f)
                .setPixelsOfMesh(5)
                .enableAutoPage(true);
        setEGLContextClientVersion(2);

        // init others
        mDrawLock = new ReentrantLock();
        mPageRender = new SinglePageRender(mPageFlip, mHandler);
        // configure render
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mPageRender.setOnReadPageCountListener(new OnReadPageCountListener() {
            @Override
            public void onReadPageCount(int count) {
                if (mReadPageCountListener != null) {
                    mReadPageCountListener.onReadPageCount(count);
                }
            }
        });
    }

    public void setOnReadPageCountListener(OnReadPageCountListener listener) {
        mReadPageCountListener = listener;
    }

    /**
     * Is auto page mode enabled?
     *
     * @return true if auto page mode enabled
     */
    public boolean isAutoPageEnabled() {
        return mPageFlip.isAutoPageEnabled();
    }

    public void setRestrict(boolean restrict) {
        mIsRestrict = restrict;
    }

    /**
     * Enable/Disable auto page mode
     *
     * @param enable true is enable
     */
    public void enableAutoPage(boolean enable) {
        if (mPageFlip.enableAutoPage(enable)) {
            try {
                mDrawLock.lock();
                mPageRender = new SinglePageRender(mPageFlip,
                        mHandler);
                mPageRender.onSurfaceChanged(mPageFlip.getSurfaceWidth(),
                        mPageFlip.getSurfaceHeight());
                requestRender();
            } finally {
                mDrawLock.unlock();
            }
        }
    }

    /**
     * Get pixels of mesh
     *
     * @return pixels of mesh
     */
    public int getPixelsOfMesh() {
        return mPageFlip.getPixelsOfMesh();
    }

    /**
     * Handle finger down event
     *
     * @param x finger x coordinate
     * @param y finger y coordinate
     */
    public void onFingerDown(float x, float y) {
        // if the animation is going, we should ignore this event to avoid
        // mess drawing on screen
        if (!mPageFlip.isAnimating() &&
                mPageFlip.getFirstPage() != null) {
            mPageFlip.onFingerDown(x, y);
        }
    }

    public void changeDrawCommand() {
        mPageRender.changeDrawCommand();
    }

    public void reset() {
        mPageRender.reset();
    }

    public void release() {
        mPageRender.release();
    }

    /**
     * Handle finger moving event
     *
     * @param x finger x coordinate
     * @param y finger y coordinate
     */
    public void onFingerMove(float x, float y) {
        if (mPageFlip.isAnimating()) {
            // nothing to do during animating
        } else if (mPageFlip.canAnimate(x, y)) {
            // if the point is out of current page, try to start animating
            onFingerUp(x, y);
        }
        // move page by finger
        else if (mPageFlip.onFingerMove(x, y)) {
            try {
                mDrawLock.lock();
                if (mPageRender != null &&
                        mPageRender.onFingerMove()) {
                    requestRender();
                }
            } finally {
                mDrawLock.unlock();
            }
        }
    }

    /**
     * Handle finger up event and start animating if need
     *
     * @param x finger x coordinate
     * @param y finger y coordinate
     */
    public void onFingerUp(float x, float y) {
        if (!mPageFlip.isAnimating()) {
            mPageFlip.onFingerUp(x, y);
            try {
                mDrawLock.lock();
                if (mPageRender != null &&
                        mPageRender.onFingerUp()) {
                    requestRender();//相当于invalidate()
                }
            } finally {
                mDrawLock.unlock();
            }
        }

    }

    /**
     * Draw frame
     *
     * @param gl OpenGL handle
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        try {
            mDrawLock.lock();
            if (mPageRender != null) {
                mPageRender.onDrawFrame();
            }
        } finally {
            mDrawLock.unlock();
        }
    }

    /**
     * Handle surface is changed
     *
     * @param gl     OpenGL handle
     * @param width  new width of surface
     * @param height new height of surface
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        try {
            mPageFlip.onSurfaceChanged(width, height);

            // if there is only one page, create single page render when need
//            mPageRender.release();
//            mPageRender = new SinglePageRender(getContext(),
//                    mPageFlip,
//                    mHandler);
            // let page render handle surface change
            mPageRender.onSurfaceChanged(width, height);
        } catch (PageFlipException e) {
            Log.e(TAG, "Failed to run PageFlipFlipRender:onSurfaceChanged");
        }
    }

    /**
     * Handle surface is created
     *
     * @param gl     OpenGL handle
     * @param config EGLConfig object
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        try {
            mPageFlip.onSurfaceCreated();
        } catch (PageFlipException e) {
            Log.e(TAG, "Failed to run PageFlipFlipRender:onSurfaceCreated");
        }
    }

    /**
     * Create message handler to cope with messages from page render,
     * Page render will send message in GL thread, but we want to handle those
     * messages in main thread that why we need handler here
     */
    @SuppressLint("HandlerLeak")
    private void newHandler() {
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case PageRender.MSG_ENDED_DRAWING_FRAME:
                        try {
                            mDrawLock.lock();
                            // notify page render to handle ended drawing
                            // message
                            if (mPageRender != null &&
                                    mPageRender.onEndedDrawing(msg.arg1)) {
                                requestRender();
                            }
                        } finally {
                            mDrawLock.unlock();
                        }
                        break;

                    default:
                        break;
                }
            }
        };
    }

    public void setOnNavigationOperateListener(NavigationOperateListener listener) {
        mListener = listener;
    }

    public void setOnRestrictListener(RestrictReadListener listener) {
        mRestrictReadListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int width = getWidth();
            if (event.getX() < 0.3f * width || event.getX() > 0.7f * width || mIsMoved) {
                if (mIsRestrict) {
                    mRestrictReadListener.onRestrictRead();
                } else {
                    onFingerUp(event.getX(), event.getY());
                }
            } else {
                mListener.operateNavigation();
            }
            return true;
        }
        if (mIsRestrict) {
            return true;
        }
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mIsMoved = false;
        onFingerDown(e.getX(), e.getY());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mIsMoved = true;
        onFingerMove(e2.getX(), e2.getY());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
