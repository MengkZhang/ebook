package com.tzpt.cloudlibrary.widget.searchview;

public class ExpoOutInterpolator extends Calculator {

    @Override
    public float getInterpolation(float input) {
        return (float) ((input == 1) ? 1 : (-Math.pow(2, -10 * input) + 1));
    }
}
