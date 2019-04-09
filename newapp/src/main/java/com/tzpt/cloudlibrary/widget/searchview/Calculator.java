package com.tzpt.cloudlibrary.widget.searchview;

import android.animation.TypeEvaluator;
import android.view.animation.Interpolator;

public class Calculator implements Interpolator, TypeEvaluator<Number> {

    @Override
    public float getInterpolation(float input) {
        return input;
    }

    @Override
    public Number evaluate(float fraction, Number startValue, Number endValue) {
        return startValue.floatValue() + fraction * (endValue.floatValue() - startValue.floatValue());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
