package com.tzpt.cloudlibrary.zlibrary.ui.android.pageflip;

import android.content.Context;

import com.tzpt.cloudlibrary.reader.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;

/**
 * Created by Administrator on 2017/9/26.
 */

public class ShadowVertexProgram extends GLProgram {

    // variable names defined in shader scripts
    final static String VAR_MVP_MATRIX  = "u_MVPMatrix";
    final static String VAR_VERTEX_Z    = "u_vexZ";
    final static String VAR_VERTEX_POS  = "a_vexPosition";

    int mMVPMatrixLoc;
    int mVertexZLoc;
    int mVertexPosLoc;

    /**
     * Constructor
     */
    public ShadowVertexProgram() {
        super();

        mMVPMatrixLoc = INVALID_GL_HANDLE;
        mVertexZLoc = INVALID_GL_HANDLE;
        mVertexPosLoc = INVALID_GL_HANDLE;
    }

    /**
     * Initiate shader program
     *
     * @param context android context
     * @return self
     * @throws PageFlipException raise exception if fail to compile & link
     *                           program
     */
    public ShadowVertexProgram init(Context context) throws
            PageFlipException {
        super.init(context,
                R.raw.shadow_vertex_shader,
                R.raw.shadow_fragment_shader);
        return this;
    }

    /**
     * Get variable handles from linked shader program
     */
    protected void getVarsLocation() {
        if (mProgramRef != 0) {
            mVertexZLoc = glGetUniformLocation(mProgramRef, VAR_VERTEX_Z);
            mVertexPosLoc = glGetAttribLocation(mProgramRef, VAR_VERTEX_POS);
            mMVPMatrixLoc = glGetUniformLocation(mProgramRef, VAR_MVP_MATRIX);
        }
    }

    /**
     * Delete shader resources
     */
    public void delete() {
        super.delete();

        mMVPMatrixLoc = INVALID_GL_HANDLE;
        mVertexZLoc = INVALID_GL_HANDLE;
        mVertexPosLoc = INVALID_GL_HANDLE;
    }
}
