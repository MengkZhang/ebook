package com.tzpt.cloudlibrary.zlibrary.ui.android.pageflip;

/**
 * Created by Administrator on 2017/9/26.
 */

public final class GLViewRect {

    // view left
    float left;
    // view right
    float right;
    // view top
    float top;
    // view bottom
    float bottom;
    // view width
    float width;
    // view height
    float height;
    // view half width
    float halfW;
    // view half height
    float halfH;
    // view margin left
    float marginL;
    // view margin right
    float marginR;
    // openGL surface width, it should be >= view width
    float surfaceW;
    // openGL surface height, it should be >= view height
    float surfaceH;

    /**
     * Default constructor
     */
    public GLViewRect() {
        left = 0;
        right = 0;
        top = 0;
        bottom = 0;
        width = 0;
        height = 0;
        halfW = 0;
        halfH = 0;
        marginL = 0;
        marginR = 0;
        surfaceW = 0;
        surfaceH = 0;
    }

    /**
     * Construct with surface and margin size
     *
     * @param surfaceW openGL surface width
     * @param surfaceH openGl surface height
     * @param marginL margin left
     * @param marginR margin right
     */
    public GLViewRect(float surfaceW, float surfaceH,
                      float marginL, float marginR) {
        set(surfaceW, surfaceH, marginL, marginR);
    }

    /**
     * Set margin
     *
     * @param marginL margin left
     * @param marginR margin right
     * @return self
     */
    public GLViewRect setMargin(float marginL, float marginR) {
        return set(this.surfaceW, this.surfaceH, marginL, marginR);
    }

    /**
     * Set with surface size
     *
     * @param surfaceW openGL surface width
     * @param surfaceH openGl surface height
     * @return self
     */
    public GLViewRect set(float surfaceW, float surfaceH) {
        return set(surfaceW, surfaceH, this.marginL, this.marginR);
    }

    /**
     * Set with surface size and margin size
     *
     * @param surfaceW openGL surface width
     * @param surfaceH openGl surface height
     * @param marginL margin left
     * @param marginR margin right
     * @return self
     */
    public GLViewRect set(float surfaceW, float surfaceH,
                          float marginL, float marginR) {
        this.surfaceW = surfaceW;
        this.surfaceH = surfaceH;
        this.marginL = marginL;
        this.marginR = marginR;

        width = surfaceW - marginL - marginR;
        height = surfaceH;
        halfW = width * 0.5f;
        halfH = height * 0.5f;
        left = -halfW + marginL;
        right = halfW - marginR;
        top = halfH;
        bottom = -halfH;
        return this;
    }

    /**
     * Get minimal value between width and height
     *
     * @return minimal value
     */
    public float minOfWH() {
        return width > height ? width : height;
    }

    /**
     * Translate Android coordinate to OpenGL coordinate
     * <p>
     * Android screen coordinate:
     * *------------> X[0..Width]
     * |
     * |
     * |
     * V
     * Y[0..Height]
     *
     * OpenGL screen coordinate:
     *              Y[0..Height/2]
     *              ^
     *              |
     *              |
     *              +-----------> X[0..Width/2]
     *             /
     *            /
     *           /
     *          Z[0..1]
     * </pre>
     */
    public float toOpenGLX(float x) {
        return x - halfW;
    }

    public float toOpenGLY(float y) {
        return halfH - y;
    }
}
