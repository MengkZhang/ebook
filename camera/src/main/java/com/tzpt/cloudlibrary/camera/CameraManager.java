package com.tzpt.cloudlibrary.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2018/10/17.
 */

public final class CameraManager {

    private Camera mCamera;
    private final Context mContext;

    private static final int MIN_PREVIEW_PIXELS = 480 * 320; // normal screen
    private static final double MAX_ASPECT_DISTORTION = 0.15;

    public CameraManager(Context context) {
        mContext = context;
    }

    /**
     * Opens the mCamera driver and initializes the hardware parameters.
     */
    public synchronized void openDriver() {
        if (mCamera != null)
            return;

        mCamera = Camera.open();
    }

    public void setCameraParameters() {
        if (mCamera == null) {
            openDriver();
        }

        Camera.Parameters parameters = mCamera.getParameters();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point theScreenResolution = new Point();
        display.getSize(theScreenResolution);//得到屏幕的尺寸，单位是像素
        Point previewSizeOnScreen = findBestPreviewSizeValue(parameters, theScreenResolution);//通过相机尺寸、屏幕尺寸来得到最好的展示尺寸，此尺寸为相机的
        parameters.setPreviewSize(previewSizeOnScreen.x, previewSizeOnScreen.y);
        Log.e("CameraManager", "width: " + previewSizeOnScreen.x + "height: " + previewSizeOnScreen.y);

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(parameters);
    }

    public Camera.Parameters getCameraParameters() {
        if (mCamera != null) {
            return mCamera.getParameters();
        }
        return null;
    }

    /**
     * Closes the camera driver if still in use.
     */
    public synchronized void closeDriver() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * Camera is opened.
     *
     * @return true, other wise false.
     */
    public boolean isOpen() {
        return mCamera != null;
    }

    /**
     * Camera start preview.
     *
     * @param holder          {@link SurfaceHolder}.
     * @param previewCallback {@link Camera.PreviewCallback}.
     * @throws IOException if the method fails (for example, if the surface is unavailable or unsuitable).
     */
    public void startPreview(SurfaceHolder holder, Camera.PreviewCallback previewCallback) throws IOException {
        if (mCamera != null) {
            mCamera.setDisplayOrientation(getDisplayOrientation());
            mCamera.setPreviewDisplay(holder);
            mCamera.setOneShotPreviewCallback(previewCallback);
            mCamera.startPreview();
        }
    }

    public void setOneShotPreviewCallback(Camera.PreviewCallback previewCallback) {
        if (mCamera != null) {
            mCamera.setOneShotPreviewCallback(previewCallback);
        }
    }

    public int getDisplayOrientation() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        Camera.CameraInfo camInfo = new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, camInfo);

        return (camInfo.orientation - degrees + 360) % 360;
    }

    /**
     * 获取（旋转角度/90）
     */
    public int getRotationCount() {
        int displayOrientation = getDisplayOrientation();
        return displayOrientation / 90;
    }

    /**
     * Camera stop preview.
     */
    public void stopPreview() {
        if (mCamera != null) {
            mCamera.cancelAutoFocus();
            mCamera.setOneShotPreviewCallback(null);
            mCamera.stopPreview();
        }
    }

    /**
     * Focus on, make a scan action.
     *
     * @param callback {@link Camera.AutoFocusCallback}.
     */
    public void autoFocus(Camera.AutoFocusCallback callback) {
        if (mCamera != null)
            try {
                mCamera.autoFocus(callback);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }


    /**
     * 打开/关闭手电筒
     */
    public void turnLight() {
        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                if (parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                } else {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Point findBestPreviewSizeValue(Camera.Parameters parameters, Point screenResolution) {
        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            Camera.Size defaultSize = parameters.getPreviewSize();
            if (defaultSize == null) {
                throw new IllegalStateException("Parameters contained no preview size!");
            }
            return new Point(defaultSize.width, defaultSize.height);
        }

        // Sort by size, descending
        List<Camera.Size> supportedPreviewSizes = new ArrayList<>(rawSupportedSizes);
        Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });

        double screenAspectRatio;
        if (screenResolution.x > screenResolution.y) {
            screenAspectRatio = screenResolution.x / (double) screenResolution.y;//屏幕尺寸比例
        } else {
            screenAspectRatio = screenResolution.y / (double) screenResolution.x;//屏幕尺寸比例
        }

        // Remove sizes that are unsuitable
        Iterator<Camera.Size> it = supportedPreviewSizes.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewSize = it.next();
            int realWidth = supportedPreviewSize.width;
            int realHeight = supportedPreviewSize.height;
            if (realWidth * realHeight <= MIN_PREVIEW_PIXELS) {//delete if less than minimum size
                it.remove();
                continue;
            }

            //camera preview width > height
            boolean isCandidatePortrait = realWidth < realHeight;//width less than height
            int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
            int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;
            double aspectRatio = maybeFlippedWidth / (double) maybeFlippedHeight;//ratio for camera
            double distortion = Math.abs(aspectRatio - screenAspectRatio);//returan absolute value
            Log.e("CameraManager", "width: " + supportedPreviewSize.width + " height: " + supportedPreviewSize.height);
            Log.e("CameraManager", "distortion: " + distortion);
            if (distortion > MAX_ASPECT_DISTORTION) {//delete if distoraion greater than 0.15
                it.remove();
            }
        }

        if (!supportedPreviewSizes.isEmpty()) {
            Camera.Size largestPreview;
            if (supportedPreviewSizes.size() > 3) {
                largestPreview = supportedPreviewSizes.get(2);
            } else {
                largestPreview = supportedPreviewSizes.get(supportedPreviewSizes.size() - 1);
            }

            return new Point(largestPreview.width, largestPreview.height);
        }

        // If there is nothing at all suitable, return current preview size
        Camera.Size defaultPreview = parameters.getPreviewSize();
        if (defaultPreview == null) {
            throw new IllegalStateException("Parameters contained no preview size!");
        }
        return new Point(defaultPreview.width, defaultPreview.height);
    }
}
