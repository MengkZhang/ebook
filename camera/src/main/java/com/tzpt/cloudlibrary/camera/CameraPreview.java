package com.tzpt.cloudlibrary.camera;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 照相机预览图
 * Created by Administrator on 2018/10/17.
 */

public class CameraPreview extends FrameLayout implements SurfaceHolder.Callback {

    private CameraManager mCameraManager;
    private CameraScanAnalysis mPreviewCallback;
    private SurfaceHolder mHolder;

    private IFrameRect mCoverView;
    private Rect scaledRect, rotatedRect;

    private ScanCallback mCallback;

    public CameraPreview(Context context) {
        this(context, null);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mCameraManager = new CameraManager(context);
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    if (mCallback != null)
                        mCallback.onScanResult((String) msg.obj);
                } else {
                    mCameraManager.setOneShotPreviewCallback(mPreviewCallback);
                }
            }
        };
        mPreviewCallback = new CameraScanAnalysis(handler);
    }

    public void setFrameRect(IFrameRect coverView) {
        if (coverView instanceof View) {
            this.mCoverView = coverView;
        } else {
            throw new IllegalArgumentException("IFrameRect必须是View对象");
        }
    }

    /**
     * Set Scan results callback.
     *
     * @param callback {@link ScanCallback}.
     */
    public void setScanCallback(ScanCallback callback) {
        mCallback = callback;
    }

    /**
     * 打开系统相机
     */
    public void startCamera() {
        mCameraManager.openDriver();
        mCameraManager.setCameraParameters();

        removeAllViews();
        SurfaceView surfaceView = new SurfaceView(getContext());
        addView(surfaceView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void closeCamera() {
        pausePreviewScan();
        mCameraManager.closeDriver();
    }

    public void startPreviewScan() {
        try {
            Camera.Parameters parameters = mCameraManager.getCameraParameters();
            if (parameters == null) {
                return;
            }
            int previewWidth = parameters.getPreviewSize().width;
            int previewHeight = parameters.getPreviewSize().height;

            //根据CoverView和preview的尺寸之比，缩放扫码区域
            Rect rect = getScaledRect(previewWidth, previewHeight);
            rect = getRotatedRect(previewWidth, previewHeight, rect);
            mPreviewCallback.onStart(rect);
            mCameraManager.startPreview(mHolder, mPreviewCallback);

            mCameraManager.autoFocus(mFocusCallback);//自动对焦
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pausePreviewScan() {
        removeCallbacks(mAutoFocusTask);
        mPreviewCallback.onStop();

        mCameraManager.stopPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreviewScan();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pausePreviewScan();
    }

    private Camera.AutoFocusCallback mFocusCallback = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            postDelayed(mAutoFocusTask, 1000);
        }
    };

    private Runnable mAutoFocusTask = new Runnable() {
        public void run() {
            mCameraManager.autoFocus(mFocusCallback);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        closeCamera();
        super.onDetachedFromWindow();
    }

    /**
     * 根据CoverView和preview的尺寸之比，缩放扫码区域
     */
    public Rect getScaledRect(int previewWidth, int previewHeight) {
        if (scaledRect == null) {
            Rect framingRect = mCoverView.getFramingRect();//获得扫码框区域
            int viewFinderViewWidth = ((View) mCoverView).getWidth();
            int viewFinderViewHeight = ((View) mCoverView).getHeight();

            scaledRect = new Rect(framingRect);
            scaledRect.left = scaledRect.left * previewHeight / viewFinderViewWidth;
            scaledRect.right = scaledRect.right * previewHeight / viewFinderViewWidth;
            scaledRect.top = scaledRect.top * previewWidth / viewFinderViewHeight;
            scaledRect.bottom = scaledRect.bottom * previewWidth / viewFinderViewHeight;
        }

        return scaledRect;
    }

    public Rect getRotatedRect(int previewWidth, int previewHeight, Rect rect) {
        if (rotatedRect == null) {
            int rotationCount = mCameraManager.getRotationCount();
            rotatedRect = new Rect(rect);

            if (rotationCount == 1) {//若相机图像需要顺时针旋转90度，则将扫码框逆时针旋转90度
                rotatedRect.left = rect.top - 30;
                rotatedRect.top = previewHeight - rect.right - 30;
                rotatedRect.right = rect.bottom + 30;
                rotatedRect.bottom = previewHeight - rect.left + 30;
            } else if (rotationCount == 2) {//若相机图像需要顺时针旋转180度,则将扫码框逆时针旋转180度
                rotatedRect.left = previewWidth - rect.right;
                rotatedRect.top = previewHeight - rect.bottom;
                rotatedRect.right = previewWidth - rect.left;
                rotatedRect.bottom = previewHeight - rect.top;
            } else if (rotationCount == 3) {//若相机图像需要顺时针旋转270度，则将扫码框逆时针旋转270度
                rotatedRect.left = previewWidth - rect.bottom;
                rotatedRect.top = rect.left;
                rotatedRect.right = previewWidth - rect.top;
                rotatedRect.bottom = rect.right;
            }
        }

        return rotatedRect;
    }

    public void turnLight() {
        mCameraManager.turnLight();
    }
}
