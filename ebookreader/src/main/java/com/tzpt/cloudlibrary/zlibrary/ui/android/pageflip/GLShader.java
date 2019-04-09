package com.tzpt.cloudlibrary.zlibrary.ui.android.pageflip;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glShaderSource;

/**
 * Created by Administrator on 2017/9/26.
 */

public class GLShader {

    private final static String TAG = "GLShader";
    private final int INVALID_GL_HANDLE = -1;

    // shader object reference
    int mShaderRef;

    /**
     * Default constructor
     */
    public GLShader() {
        mShaderRef = INVALID_GL_HANDLE;
    }

    /**
     * Read shader script from resources and compile
     *
     * @param context android context
     * @param type  GL_VERTEX_SHADER or GL_FRAGMENT_SHADER
     * @param resId script resource id
     * @return self
     * @throws PageFlipException if fail to compile shader script
     */
    public GLShader compile(Context context, int type, int resId)
            throws PageFlipException {
        // read shader scripts from resource
        String codes = readGLSLFromResource(context, resId);
        if (codes.length() < 1) {
            throw new PageFlipException("Empty GLSL shader for resource id:"
                    + resId);
        }

        // create a shader
        mShaderRef = glCreateShader(type);
        if (mShaderRef != INVALID_GL_HANDLE) {
            // upload shader scripts to GL
            glShaderSource(mShaderRef, codes);

            // compile shader scripts
            glCompileShader(mShaderRef);

            // get compile results to check if it is successful
            final int[] result = new int[1];
            glGetShaderiv(mShaderRef, GL_COMPILE_STATUS, result, 0);
            if (result[0] == 0) {
                // delete shader if compile is failed
                Log.e(TAG, "Can'top compile shader for type: " + type +
                        "Error: " + glGetError());
                Log.e(TAG, "Compile shader error: " +
                        glGetShaderInfoLog(mShaderRef));
                glDeleteShader(mShaderRef);
                throw new PageFlipException("Can't compile shader for" +
                        "type: " + type);
            }
        } else {
            throw new PageFlipException("Can't create shader. Error: " +
                    glGetError());
        }

        return this;
    }

    /**
     * Delete shader
     */
    public void delete() {
        if (mShaderRef != INVALID_GL_HANDLE) {
            glDeleteShader(mShaderRef);
            mShaderRef = INVALID_GL_HANDLE;
        }
    }

    /**
     * Get shader object reference
     *
     * @return shader object reference in OpenGL
     */
    public int getShaderRef() {
        return mShaderRef;
    }

    /**
     * Read shader script from resources
     *
     * @param context android context
     * @param resId script resource id
     * @return shader script contents
     * @throws PageFlipException if fail to read script from resources
     */
    String readGLSLFromResource(Context context, int resId) throws
            PageFlipException {
        StringBuilder s = new StringBuilder();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(
                    context.getResources().openRawResource(resId)));
            String line;

            while ((line = reader.readLine()) != null) {
                s.append(line);
                s.append("\n");
            }
        }
        catch (IOException e) {
            throw new PageFlipException("Could not open resource: "
                    + resId , e);
        }
        finally {
            // close
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (IOException e) {
            }
        }

        return s.toString();
    }
}
