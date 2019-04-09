package com.tzpt.cloudlibrary.zlibrary.ui.android.pageflip;

/**
 * Created by Administrator on 2017/9/26.
 */

public final class ShadowColor {

    float startColor;
    float startAlpha;
    float endColor;
    float endAlpha;

    /**
     * Default constructor
     */
    public ShadowColor() {
        startColor = 0;
        startAlpha = 0;
        endColor = 0;
        endAlpha = 0;
    }

    /**
     * Constructor
     *
     * @param startColor start color, range is [0 .. 1]
     * @param startAlpha start alpha, range is [0 .. 1]
     * @param endColor end color, range is [0 .. 1]
     * @param endAlpha end alpha, range is [0 .. 1]
     */
    public ShadowColor(float startColor, float startAlpha,
                       float endColor, float endAlpha) {
        set(startColor, startAlpha, endColor, endAlpha);
    }

    /**
     * Set color and alpha
     *
     * @param startColor start color, range is [0 .. 1]
     * @param startAlpha start alpha, range is [0 .. 1]
     * @param endColor end color, range is [0 .. 1]
     * @param endAlpha end alpha, range is [0 .. 1]
     */
    public void set(float startColor, float startAlpha,
                    float endColor, float endAlpha) {
        if (startColor < 0 || startColor > 1 ||
                startAlpha < 0 || startAlpha > 1 ||
                endColor < 0 || endColor > 1 ||
                endAlpha < 0 || endAlpha > 1) {
            throw new IllegalArgumentException("Illegal color or alpha value!");
        }

        this.startColor = startColor;
        this.startAlpha = startAlpha;
        this.endColor = endColor;
        this.endAlpha = endAlpha;
    }

}
