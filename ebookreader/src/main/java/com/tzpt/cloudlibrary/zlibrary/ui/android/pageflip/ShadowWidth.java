package com.tzpt.cloudlibrary.zlibrary.ui.android.pageflip;

/**
 * Created by Administrator on 2017/9/26.
 */

public final class ShadowWidth {

    // minimal shadow width
    float mMin;

    // maximal shadow width
    float mMax;

    // the shadow width ratio based on fold cylinder radius
    // shadow width will be dynamically computed upon fold cylinder radius
    float mRatio;

    public ShadowWidth(float min, float max, float ratio) {
        set(min, max, ratio);
    }

    /**
     * Set minimal, maximal and ratio value of shadow width
     *
     * @param min minimal value
     * @param max maximal value
     * @param ratio width ratio based on fold cylinder radius
     */
    public void set(float min, float max, float ratio) {
        if (min < 0 || max < 0 || min > max ||
                ratio <= 0 || ratio > 1) {
            throw new IllegalArgumentException("One of Min(" + min + ") Max(" +
                    max + ") Ration(" + ratio + ")" +
                    "is invalid!");
        }

        mMin = min;
        mMax = max;
        mRatio = ratio;
    }

    /**
     * Compute shadow width upon fold cylinder radius
     * <p>if width is out of (min, max), one of them will be returned</p>
     *
     * @param r fold cylinder radius
     * @return shadow width
     */
    public float width(float r) {
        float w = r * mRatio;
        if (w < mMin) {
            return mMin;
        }
        else if (w > mMax) {
            return mMax;
        }
        else {
            return w;
        }
    }
}
