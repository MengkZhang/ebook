package com.tzpt.cloudlibrary.widget.touchimageview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

public class TouchImageView extends PhotoView {


    private static final int ANIMATION_DURATION = 300;
    private static final int TRANSLATE_DISTANCE = 500;
    private static final int MAX_ALPHA_VALUE = 255;
    private static final int MIN_ALPHA_VALUE = 0;
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    private Paint mPaint;
    private float mImageWidth;
    private float mImageHeight;

    private float mDownX;
    private float mDownY;
    private float mTranslateX;
    private float mTranslateY;
    private float mScaleX;
    private float mScaleY;

    private int mOriginWidth;
    private int mOriginHeight;

    private float mOriginCenterX;
    private float mOriginCenterY;
    private float mNewTranslateX;
    private float mNewTranslateY;

    private boolean mDragEvent = false;

    private float mMinScale = 0.5f;
    private float mScale = 1;
    private int mAlpha = MAX_ALPHA_VALUE;

    public TouchImageView(Context context) {
        this(context, null);
    }

    public TouchImageView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public TouchImageView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setAlpha(mAlpha);
        canvas.drawRect(0, 0, mImageWidth, mImageHeight, mPaint);
        canvas.translate(mTranslateX, mTranslateY);
        canvas.scale(mScale, mScale, mImageWidth / 2, mImageHeight / 2);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mImageWidth = w;
        mImageHeight = h;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (getScale() == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onActionDown(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    //防止多张图片，图片被拖动，ViewPager 无法执行的情况
                    if (mTranslateY == 0 && mTranslateX != 0) {
                        if (!mDragEvent) {
                            mScale = 1f;
                            return super.dispatchTouchEvent(event);
                        }
                    }

                    if (mTranslateY >= 0 && event.getPointerCount() == 1) {
                        onActionMove(event.getX(), event.getY());
                        if (mTranslateY != 0) {
                            mDragEvent = true;
                        }
                        return true;
                    }

                    if (mTranslateY >= 0 && mScale < 0.95f) {
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (event.getPointerCount() == 1) {
                        onActionUp();
                    }
                    break;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void onActionDown(float downX, float downY) {
        mDownX = downX;
        mDownY = downY;
        mDragEvent = false;
    }

    private void onActionMove(float moveX, float moveY) {
        mTranslateX = moveX - mDownX;
        mTranslateY = moveY - mDownY;

        if (mTranslateY < 0) {
            mTranslateY = 0;
        }

        //计算缩放百分比
        float percent = mTranslateY / TRANSLATE_DISTANCE;
        if (mScale >= mMinScale && mScale <= 1f) {
            mScale = 1f - percent;

            //设置alpha值
            mAlpha = (int) (MAX_ALPHA_VALUE * mScale);
            if (mAlpha > MAX_ALPHA_VALUE) {
                mAlpha = MAX_ALPHA_VALUE;
            } else if (mAlpha < MIN_ALPHA_VALUE) {
                mAlpha = MIN_ALPHA_VALUE;
            }
        }

        if (mScale < mMinScale) {
            mScale = mMinScale;
        } else if (mScale > 1f) {
            mScale = 1f;
        }
        invalidate();
    }

    private void onActionUp() {
        if (mDragEvent) {
            if (mTranslateY > TRANSLATE_DISTANCE) {
                executeExitAnimation(mTranslateX, mTranslateY);
            } else {
                performAnimation();
            }
        } else {
            mTranslateX = 0;
            mTranslateY = 0;
        }
    }

    public boolean isDraging() {
        return mDragEvent;
    }

    /**
     * 执行缩放，移动动画
     */
    private void performAnimation() {

        final ValueAnimator scaleAnimator = ValueAnimator.ofFloat(mScale, 1);
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mScale = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        final ValueAnimator translateXAnimator = ValueAnimator.ofFloat(mTranslateX, 0);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mTranslateX = (float) valueAnimator.getAnimatedValue();
            }
        });

        final ValueAnimator translateYAnimator = ValueAnimator.ofFloat(mTranslateY, 0);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mTranslateY = (float) valueAnimator.getAnimatedValue();
            }
        });
        final ValueAnimator alpAnimator = ValueAnimator.ofInt(mAlpha, MAX_ALPHA_VALUE);
        alpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAlpha = (int) valueAnimator.getAnimatedValue();
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleAnimator, translateXAnimator, translateYAnimator, alpAnimator);
        set.setDuration(ANIMATION_DURATION);
        set.setInterpolator(mInterpolator);
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /**
     * 展开动画
     *
     * @param left   原图片左边距
     * @param top    原图片上边距
     * @param width  原图片宽度
     * @param height 原图片高度
     */
    public void executeScaleAnimation(int left, int top, int width, int height) {
        this.mOriginWidth = width;
        this.mOriginHeight = height;

        int[] location = new int[2];
        getLocationOnScreen(location);

        mScaleX = (float) width / mImageWidth;
        mScaleY = (float) height / mImageHeight;

        mOriginCenterX = left + (float) width / 2;
        mOriginCenterY = top + (float) height / 2;

        float targetCenterX = location[0] + mImageWidth / 2;
        float targetCenterY = location[1] + mImageHeight / 2;

        mNewTranslateX = mOriginCenterX - targetCenterX;
        mNewTranslateY = mOriginCenterY - targetCenterY;

        setTranslationX(mNewTranslateX);
        setTranslationY(mNewTranslateY);

        setScaleX(mScaleX);
        setScaleY(mScaleY);

        //do animation
        executeScaleTranslateAnimation();

        mMinScale = mScaleX;
    }

    /**
     * 执行展开动画
     */
    private void executeScaleTranslateAnimation() {
        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(getX(), 0);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setX((float) animation.getAnimatedValue());
            }
        });
        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(getY(), 0);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setY((float) animation.getAnimatedValue());
            }
        });
        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(mScaleX, 1);
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setScaleX((float) animation.getAnimatedValue());
            }
        });
        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(mScaleY, 1);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setScaleY((float) animation.getAnimatedValue());
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(translateXAnimator, translateYAnimator, scaleXAnimator, scaleYAnimator);
        set.setInterpolator(mInterpolator);
        set.setDuration(ANIMATION_DURATION);
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /**
     * 执行点击退出动画
     */
    public void executeFinishAnimation() {
        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(0, mNewTranslateX);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setX((float) animation.getAnimatedValue());
            }
        });
        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(0, mNewTranslateY);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setY((float) animation.getAnimatedValue());
            }
        });
        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(1, mScaleX);
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setScaleX((float) animation.getAnimatedValue());
            }
        });
        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(1, mScaleY);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setScaleY((float) animation.getAnimatedValue());
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(translateXAnimator, translateYAnimator, scaleXAnimator, scaleYAnimator);
        set.setDuration(ANIMATION_DURATION);
        set.setInterpolator(mInterpolator);
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeAllListeners();
                //call finish
                if (mCallbackImage != null) {
                    mCallbackImage.callbackGalley();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /**
     * 执行拖拽退出动画
     *
     * @param transX 坐标X
     * @param transY 坐标Y
     */
    private void executeExitAnimation(float transX, float transY) {
        updateTranslate();
        float viewX = mImageWidth / 2 + transX - mImageWidth * mScaleX / 2;
        float viewY = mImageHeight / 2 + transY - mImageHeight * mScaleY / 2;

        setX(viewX);
        setY(viewY);

        float centerX = getX() + (float) mOriginWidth / 2;
        float centerY = getY() + (float) mOriginHeight;

        float translateX = mOriginCenterX - centerX;
        float translateY = mOriginCenterY - centerY;

        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(getX(), getX() + translateX);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setX((float) animation.getAnimatedValue());
            }
        });

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(getY(), getY() + translateY);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setY((float) animation.getAnimatedValue());
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(translateXAnimator, translateYAnimator);
        set.setInterpolator(mInterpolator);
        set.setDuration(ANIMATION_DURATION);
        set.start();

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeAllListeners();
                //call finish
                if (mCallbackImage != null) {
                    mCallbackImage.callbackGalley();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    private void updateTranslate() {
        mTranslateX = -mImageWidth / 2 + mImageWidth * mScale / 2;
        mTranslateY = -mImageHeight / 2 + mImageHeight * mScale / 2;
        invalidate();
    }

    private GalleyCallback mCallbackImage;

    public void setImageClickListener(GalleyCallback callbackImage) {
        this.mCallbackImage = callbackImage;
    }

    public interface GalleyCallback {
        void callbackGalley();
    }
}
