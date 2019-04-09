package com.tzpt.cloudlibrary.zlibrary.ui.android.pageflip;

import android.content.Context;

import com.tzpt.cloudlibrary.reader.R;

import static android.opengl.GLES20.glGetUniformLocation;

/**
 * Created by Administrator on 2017/9/26.
 */

public class FoldBackVertexProgram extends VertexProgram {

    final static String VAR_TEXTRUE_OFFSET = "u_texXOffset";
    final static String VAR_MASK_COLOR     = "u_maskColor";
    final static String VAR_SHADOW_TEXTURE = "u_shadow";

    int mShadowLoc;
    int mMaskColorLoc;
    int mTexXOffsetLoc;

    public FoldBackVertexProgram() {
        super();

        mShadowLoc = INVALID_GL_HANDLE;
        mMaskColorLoc = INVALID_GL_HANDLE;
        mTexXOffsetLoc = INVALID_GL_HANDLE;
    }

    /**
     * Initiate shader program
     *
     * @param context Android app context
     * @return self
     * @throws PageFlipException if fail to read and compile shader scripts
     */
    public FoldBackVertexProgram init(Context context) throws
            PageFlipException {
        super.init(context,
                R.raw.fold_back_vertex_shader,
                R.raw.fold_back_fragment_shader);
        return this;
    }

    /**
     * Get variable handles defined in shader script
     */
    protected void getVarsLocation() {
        super.getVarsLocation();

        if (mProgramRef != 0) {
            mShadowLoc = glGetUniformLocation(mProgramRef, VAR_SHADOW_TEXTURE);
            mMaskColorLoc = glGetUniformLocation(mProgramRef, VAR_MASK_COLOR);
            mTexXOffsetLoc = glGetUniformLocation(mProgramRef,
                    VAR_TEXTRUE_OFFSET);
        }
    }

    /**
     * Delete all handles
     */
    public void delete() {
        super.delete();

        mShadowLoc = INVALID_GL_HANDLE;
        mMaskColorLoc = INVALID_GL_HANDLE;
        mTexXOffsetLoc = INVALID_GL_HANDLE;
    }
}
