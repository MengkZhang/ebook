package com.tzpt.cloundlibrary.manager.widget.camera;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.tzpt.cloudlibrary.camera.CameraPreview;
import com.tzpt.cloudlibrary.camera.ScanBoxView;
import com.tzpt.cloudlibrary.camera.ScanCallback;
import com.tzpt.cloundlibrary.manager.R;

/**
 * 扫描封装
 * Created by Administrator on 2018/12/4.
 */

public class ScanWrapper extends FrameLayout {

    private CameraPreview mPreviewView;

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

    public void turnLight() {
        mPreviewView.turnLight();
    }

}
