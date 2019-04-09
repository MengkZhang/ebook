package com.tzpt.cloudlibrary.zlibrary.core.view;

/**
 * Created by Administrator on 2017/4/7.
 */

public interface ZLViewWidget {
    void reset();
    void repaint();

    void startManualScrolling(int x, int y, ZLView.Direction direction);
    void scrollManuallyTo(int x, int y);
    void startAnimation();
    void abortAnimation();
    void startAnimatedScrolling(ZLView.PageIndex pageIndex, int x, int y, ZLView.Direction direction, int speed);
//    void startAnimatedScrolling(ZLView.PageIndex pageIndex, ZLView.Direction direction, int speed);
    void startAnimatedScrolling(int x, int y, int speed);
    void operateNavigation(boolean isHide);

    void setScreenBrightness(int percent);
}
