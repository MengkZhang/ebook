package com.tzpt.cloudlibrary.zlibrary.ui.android.pageflip;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES10.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by Administrator on 2017/9/26.
 */

public class ShadowVertexes {

    // how many vertexes in vertex float buffer will be drawn on screen
    int mVertexesSize;

    // universal Z coordinate for all shadow vertex
    // we will enable DEPTH_TEST while drawing fold shadow to avoid some drawing
    // issue
    float vertexZ;

    // float array and float buffer for storing vertexes
    float[] mVertexes;
    FloatBuffer mVertexesBuffer;

    // shadow color
    ShadowColor mColor;

    // the start position of backward vertexes
    int mMaxBackward;

    // reserve space between backward and forward index
    // need to preserver space for fold top edge shadow when compute fold edge
    // shadow since the top edge shadow will be computed at last
    //
    // +--------------------+------------+--------------------+
    // |   <-- mBackward    |  reserved  |    mForward -->    |
    // +--------------------+------------+--------------------+
    private int mSpaceOfFrontRear;

    // forward and backward index for adding vertex
    private int mBackward;
    private int mForward;

    /**
     * Default constructor
     */
    public ShadowVertexes() {
        release();
        mColor = new ShadowColor();
    }

    /**
     * Constructor
     *
     * @param spaceOfFrontRear reserve space for special usage
     * @param startColor shadow start color, range is [0 .. 1]
     * @param startAlpha shadow alpha, range is [0 .. 1]
     * @param endColor shadow end color, range is [0 .. 1]
     * @param endAlpha shadow end alpah, range is [0 .. 1]
     */
    public ShadowVertexes(int spaceOfFrontRear,
                          float startColor, float startAlpha,
                          float endColor, float endAlpha) {
        release();
        mSpaceOfFrontRear = spaceOfFrontRear;
        mColor = new ShadowColor(startColor, startAlpha, endColor, endAlpha);
    }

    /**
     * Set with vertex count
     *
     * @param meshCount mesh count
     * @return self
     */
    public ShadowVertexes set(int meshCount) {
        // every mesh need two vertexes:
        // (startX, startY , startColor, startAlpha)  and
        // (endX, endY, endColor, endAlpha), that is why it is meshCount * 8
        mMaxBackward = meshCount << 3;

        // double meshCount since fold shadow has two sides, for example:
        // fold edge shadow has left and right edge along the fold triangle
        int size = (meshCount << 4) + (mSpaceOfFrontRear << 2);
        mVertexes = new float[size];
        mVertexesBuffer = ByteBuffer.allocateDirect(size << 2)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        reset();
        return this;
    }

    /**
     * Release all resources
     */
    public void release() {
        mBackward = 0;
        mForward = 0;
        mMaxBackward = 0;
        mSpaceOfFrontRear = 0;
        mVertexes = null;
        mVertexesBuffer = null;
    }

    /**
     * Reset index of float array before adding vertex to buffer
     * <p>There are two index: forward and backward, all of them have to be
     * reset to middle position(exclude reserved space) before adding vertexes
     * </p>
     */
    public void reset() {
        vertexZ = 0;
        mBackward = mMaxBackward;
        mForward = mMaxBackward + (mSpaceOfFrontRear << 2);
    }

    /**
     * Set vertex in given offset
     *
     * @param offset where to start saving vertex
     * @param startX start x coordinate
     * @param startY start y coordinate
     * @param endX end x coordinate
     * @param endY end y coordinate
     * @return self
     */
    public ShadowVertexes setVertexes(int offset,
                                      float startX, float startY,
                                      float endX, float endY) {
        mVertexes[offset++] = startX;
        mVertexes[offset++] = startY;
        mVertexes[offset++] = mColor.startColor;
        mVertexes[offset++] = mColor.startAlpha;
        mVertexes[offset++] = endX;
        mVertexes[offset++] = endY;
        mVertexes[offset++] = mColor.endColor;
        mVertexes[offset] = mColor.endAlpha;
        return this;
    }

    /**
     * Backward add vertex to float buffer
     * <p></p>Call {@link #reset()} before start calling any add operations</p>
     *
     * @param startX start x coordinate
     * @param startY start y coordinate
     * @param endX end x coordinate
     * @param endY end y coordinate
     * @return self
     */
    public ShadowVertexes addVertexesBackward(float startX, float startY,
                                              float endX, float endY) {
        mVertexes[--mBackward] = mColor.endAlpha;
        mVertexes[--mBackward] = mColor.endColor;
        mVertexes[--mBackward] = endY;
        mVertexes[--mBackward] = endX;
        mVertexes[--mBackward] = mColor.startAlpha;
        mVertexes[--mBackward] = mColor.startColor;
        mVertexes[--mBackward] = startY;
        mVertexes[--mBackward] = startX;
        return this;
    }

    /**
     * Forward add vertex to float buffer
     * <p></p>Call {@link #reset()} before start calling any add operations</p>
     *
     * @param startX start x coordinate
     * @param startY start y coordinate
     * @param endX end x coordinate
     * @param endY end y coordinate
     * @return self
     */
    public ShadowVertexes addVertexesForward(float startX, float startY,
                                             float endX, float endY) {
        mVertexes[mForward++] = startX;
        mVertexes[mForward++] = startY;
        mVertexes[mForward++] = mColor.startColor;
        mVertexes[mForward++] = mColor.startAlpha;
        mVertexes[mForward++] = endX;
        mVertexes[mForward++] = endY;
        mVertexes[mForward++] = mColor.endColor;
        mVertexes[mForward++] = mColor.endAlpha;
        return this;
    }

    /**
     * Add vertex to float buffer
     * Call {@link #reset()} before calling any add operations
     *
     * @param isForward is backward or forward adding
     * @param startX start x coordinate
     * @param startY start y coordinate
     * @param endX end x coordinate
     * @param endY end y coordinate
     * @return self
     */
    public ShadowVertexes addVertexes(boolean isForward,
                                      float startX, float startY,
                                      float endX, float endY) {
        return isForward ?
                addVertexesForward(startX, startY, endX, endY) :
                addVertexesBackward(startX, startY, endX, endY);
    }

    /**
     * Put data from float array to float buffer
     */
    public void toFloatBuffer() {
        mVertexesSize = (mForward - mBackward) / 4;
        mVertexesBuffer.put(mVertexes, mBackward, mForward - mBackward)
                .position(0);
    }

    /**
     * put given length data from float array to float buffer
     *
     * @param length data length
     */
    public void toFloatBuffer(int length) {
        mVertexesBuffer.put(mVertexes, 0, length).position(0);
        mVertexesSize = length / 4;
    }

    /**
     * Draw shadow
     *
     * @param program shadow vertex shader program
     */
    public void draw(ShadowVertexProgram program) {
        if (mVertexesSize > 0) {
            glUniformMatrix4fv(program.mMVPMatrixLoc, 1, false,
                    VertexProgram.MVPMatrix, 0);

            glUniform1f(program.mVertexZLoc, vertexZ);

            // disable texture, and enable blend
            glDisable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            // draw shadow
            glVertexAttribPointer(program.mVertexPosLoc, 4, GL_FLOAT, false, 0,
                    mVertexesBuffer);
            glEnableVertexAttribArray(program.mVertexPosLoc);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, mVertexesSize);

            glDisable(GL_BLEND);
        }
    }
}
