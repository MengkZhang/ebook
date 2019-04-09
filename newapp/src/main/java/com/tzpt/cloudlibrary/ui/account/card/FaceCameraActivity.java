package com.tzpt.cloudlibrary.ui.account.card;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.utils.StatusBarUtil;
import com.tzpt.cloudlibrary.widget.camera.FaceCameraPreview;

/**
 * 人脸认证照片
 */
public class FaceCameraActivity extends AppCompatActivity {

    private FaceCameraPreview.FaceCameraListener mFaceCameraListener = new FaceCameraPreview.FaceCameraListener() {
        @Override
        public void useFacePicture(String imagePath) {
            switch (mFromType) {
                case 0://来自预览界面有返回
                    Intent intent = new Intent();
                    intent.putExtra("local_face_image", imagePath);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case 1://来自开始拍照界面
                    FacePreviewActivity.startActivity(FaceCameraActivity.this, 1, imagePath);
                    finish();
                    break;
            }
        }

        @Override
        public void cancelFaceCamera() {
            mFaceCameraPreview.delMediaFileByPath();
            finish();
        }
    };

    FaceCameraPreview mFaceCameraPreview;
    private int mFromType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        }
        setContentView(R.layout.activity_face_camera);

        initDatas();
        configViews();
    }

    private void initDatas() {
        Intent intent = getIntent();
        mFromType = intent.getIntExtra("from_type", -1);
    }

    private void configViews() {
        mFaceCameraPreview = (FaceCameraPreview) findViewById(R.id.face_camera_view);
        mFaceCameraPreview.setFaceCameraListener(mFaceCameraListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            mFaceCameraPreview.delMediaFileByPath();
            finish();
        }
        return false;
    }
}
