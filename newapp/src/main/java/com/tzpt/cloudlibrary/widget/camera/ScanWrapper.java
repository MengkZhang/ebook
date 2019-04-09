package com.tzpt.cloudlibrary.widget.camera;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.camera.CameraPreview;
import com.tzpt.cloudlibrary.camera.ScanBoxView;
import com.tzpt.cloudlibrary.camera.ScanCallback;
import com.tzpt.cloudlibrary.utils.BeepManager;

/**
 * 扫描封装
 * Created by Administrator on 2017/7/4.
 */

public class ScanWrapper extends FrameLayout {

    private CameraPreview mPreviewView;

    private BeepManager mBeepManager;
    private Activity mActivity;

    public ScanWrapper(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public ScanWrapper(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ScanWrapper(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.view_custom_camera_barcode_preview, this);
        ScanBoxView scanBoxView = (ScanBoxView) findViewById(R.id.scanner_box_view);
        mPreviewView = (CameraPreview) findViewById(R.id.capture_preview);

        mPreviewView.setFrameRect(scanBoxView);
    }

    public void openCamera() {
        mPreviewView.startCamera();
    }

    public void releaseCamera() {
        mPreviewView.closeCamera();
    }

    public void pausePreviewScan(){
        mPreviewView.pausePreviewScan();
    }

    public void startPreviewScan(){
        mPreviewView.startPreviewScan();
    }

    public void setScanCallback(ScanCallback callback) {
        mPreviewView.setScanCallback(callback);
    }

    public void bindActivity(Activity activity) {
        this.mActivity = activity;
        initSound();
    }

    /**
     * 初始化声音
     *
     * @param
     */
    private void initSound() {
        if (null != mActivity) {
            mBeepManager = new BeepManager(mActivity);
        }
    }


    /**
     * 播放声音
     */
    public void playSound() {
        mBeepManager.playBeepSoundAndVibrate();
    }

    public void turnLight() {
        mPreviewView.turnLight();
    }
}