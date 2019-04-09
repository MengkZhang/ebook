package com.tzpt.cloudlibrary.zlibrary.ui.android.pageflip;

/**
 * Created by Administrator on 2017/9/26.
 */

public interface OnPageFlipListener {

    /**
     * Can page flip forward?
     *
     * @return true if page can flip forward
     */
    boolean canFlipForward();

    /**
     * Can page flip backward?
     *
     * @return true if page can flip backward
     */
    boolean canFlipBackward();
//
//    /**
//     * Can page flip forward? And prepare next bitmap
//     *
//     * @return true if page can flip forward
//     */
//    boolean canFlipForwardAndPrepareBmp();
//
//    /**
//     * Can page flip backward? and prepare pre bitmap
//     *
//     * @return true if page can flip backward
//     */
//    boolean canFlipBackwardAndPrepareBmp();

}
