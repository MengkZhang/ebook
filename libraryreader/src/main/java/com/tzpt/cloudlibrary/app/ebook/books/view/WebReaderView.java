
package com.tzpt.cloudlibrary.app.ebook.books.view;

import android.content.Context;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.tzpt.cloudlibrary.app.ebook.books.model.ReaderSettings;

@SuppressWarnings("deprecation")
public class WebReaderView extends WebView implements OnTouchListener {

    protected Context ctx;
    protected Region lastSelectedRegion = null;
    private OnTouchListener mExternalOnTouchListener;
    private ReaderSettings mReaderSettings;
    private boolean mJavascriptInjected = false;

    public WebReaderView(Context context) {
        super(context);
        this.ctx = context;
        this.setup(context);
    }

    public WebReaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.ctx = context;
        this.setup(context);

    }

    public WebReaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ctx = context;
        this.setup(context);
    }

    // *****************************************************
    // *
    // * Touch Listeners
    // *
    // *****************************************************

    private boolean mScrolling = false;
    private float mScrollDiffY = 0;
    private float mLastTouchY = 0;
    private float mScrollDiffX = 0;
    private float mLastTouchX = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (mExternalOnTouchListener != null)
            mExternalOnTouchListener.onTouch(v, event);

        float xPoint = getDensityIndependentValue(event.getX(), ctx)
                / getDensityIndependentValue(this.getScale(), ctx);
        float yPoint = getDensityIndependentValue(event.getY(), ctx)
                / getDensityIndependentValue(this.getScale(), ctx);

        // TODO: Need to update this to use this.getScale() as a factor.

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            String startTouchUrl = String.format("javascript:android.selection.startTouch(%f, %f);", xPoint, yPoint);

            mLastTouchX = xPoint;
            mLastTouchY = yPoint;

            this.loadUrl(startTouchUrl);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mScrollDiffX = 0;
            mScrollDiffY = 0;
            mScrolling = false;

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mScrollDiffX += (xPoint - mLastTouchX);
            mScrollDiffY += (yPoint - mLastTouchY);
            mLastTouchX = xPoint;
            mLastTouchY = yPoint;
            // Only account for legitimate movement.
            if (Math.abs(mScrollDiffX) > 10 || Math.abs(mScrollDiffY) > 10) {
                mScrolling = true;
            }
            return (event.getAction() == MotionEvent.ACTION_MOVE);
        }

        // If this is in selection mode, then nothing else should handle this
        // touch
        return false;
    }

    @JavascriptInterface
    public void startTouch(MotionEvent event) {
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        RectF rect = new RectF((int) mReaderSettings.mLeftMargin, (int) mReaderSettings.mTopMargin, (int) (width - mReaderSettings.mRightMargin), (int) (height - mReaderSettings.mBottomMargin));

        if (!rect.contains(event.getX(), event.getY()))
            return;
        float scale = getDensityIndependentValue(this.getScale(), ctx);

        float xPoint = getDensityIndependentValue(event.getX(), ctx)
                / scale;
        float yPoint = getDensityIndependentValue(event.getY(), ctx)
                / scale;

        String startTouchUrl = String.format("javascript:android.selection.startTouch(%f, %f);",
                xPoint, yPoint);

        mLastTouchX = xPoint;
        mLastTouchY = yPoint;

        this.loadUrl(startTouchUrl);
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(oldw, oldh, oldw, oldh);
//    }

    public void onLongClick(MotionEvent event) {
        startTouch(event);
    }
    // *****************************************************
    // *
    // * Setup
    // *
    // *****************************************************

    /**
     * Setups up the web view.
     *
     * @param context
     */
    protected void setup(Context context) {
        this.setOnTouchListener(this);
        WebSettings settings = getSettings();
        // Webview setup
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // Only hide the scrollbar, not disables the scrolling:
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        settings.setDomStorageEnabled(true);
        // Only disabled the horizontal scrolling:
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //webview
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
        }
        // Set to the empty region
        Region region = new Region();
        region.setEmpty();
        this.lastSelectedRegion = region;
    }

    /**
     * Returns the density independent value of the given float
     *
     * @param val
     * @param ctx
     * @return
     */
    public static float getDensityIndependentValue(float val, Context ctx) {
        // Get display from context
        Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        // Calculate min bound based on metrics
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return val / (metrics.densityDpi / 160f);
    }

    public OnTouchListener getExternalOnTouchListener() {
        return mExternalOnTouchListener;
    }

    public void setExternalOnTouchListener(OnTouchListener mExternalOnTouchListener) {
        this.mExternalOnTouchListener = mExternalOnTouchListener;
    }

    public ReaderSettings getReaderSettings() {
        return mReaderSettings;
    }

    public void setReaderSettings(ReaderSettings settings) {
        this.mReaderSettings = settings;
    }

    public boolean isJavascriptInjected() {
        return mJavascriptInjected;
    }

    public void setJavascriptInjected(boolean javascriptInjected) {
        this.mJavascriptInjected = javascriptInjected;
    }

    public static class StyleText {
        public int chapterIndex;
        public int startNodeIndex;
        public int startNodeOffset;
        public int endNodeIndex;
        public int endNodeOffset;
        public int type;
        public long bgColor;
    }
}
