package com.tzpt.cloudlibrary.widget.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

/**
 * 识别照片camera view
 * Created by tonyjia on 2018/3/9.
 */
public class FaceCameraPreview extends FrameLayout implements
        TextureView.SurfaceTextureListener {


    public FaceCameraPreview(@NonNull Context context) {
        super(context);
        initFaceCameraView(context);
    }

    public FaceCameraPreview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initFaceCameraView(context);
    }

    public FaceCameraPreview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFaceCameraView(context);
    }

    private Camera.FaceDetectionListener mFaceDetectionListener = new Camera.FaceDetectionListener() {
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            if (mTakePicBtn.getVisibility() == View.VISIBLE && faces.length > 0) {
                mFaceView.setFaces(faces);
            } else {
                mFaceView.clearFaces();
            }
        }
    };
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                //Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                // Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
            loadLocalFaceImage();
        }
    };

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.camera_take_cancel_btn://重新拍摄
                    if (mTakeCancelBtn.getText().toString().equals("取消")) {
                        if (null != mFaceCameraListener) {
                            mFaceCameraListener.cancelFaceCamera();
                        }
                    } else {
                        mTakePicBtn.setVisibility(View.VISIBLE);
                        mTakeCancelBtn.setText(R.string.cancel);
                        mTakeUsePicBtn.setVisibility(View.GONE);
                        startPreview();
                        delMediaFileByPath();
                        //回收图片
                        mCameraPreviewIv.setImageBitmap(null);
                        mCameraPreviewIv.setVisibility(View.GONE);
                    }
                    break;
                case R.id.camera_use_pic_btn:
                    if (null != mFaceCameraListener) {
                        mFaceCameraListener.useFacePicture(mLocalImagePath);
                    }
                    break;
                case R.id.camera_take_pic_btn:
                    if (null != mCamera) {
                        stopFaceDetection();
                        mFaceView.clearFaces();
                        mTakePicBtn.setVisibility(View.GONE);
                        mTakeCancelBtn.setText(R.string.re_take_pic);
                        mTakeUsePicBtn.setVisibility(View.VISIBLE);
                        mCamera.takePicture(null, null, mPictureCallback);
                    }
                    break;
            }
        }
    };

    private Camera mCamera;
    private ImageView mCameraPreviewIv;
    private Button mTakePicBtn;
    private Button mTakeCancelBtn;
    private Button mTakeUsePicBtn;
    private String mLocalImagePath;
    private FaceView mFaceView;

    private void initFaceCameraView(Context context) {
        inflate(context, R.layout.view_face_camera, this);
        TextureView textureView = (TextureView) findViewById(R.id.camera_view);
        mCameraPreviewIv = (ImageView) findViewById(R.id.camera_preview_iv);
        mTakePicBtn = (Button) findViewById(R.id.camera_take_pic_btn);
        mTakeCancelBtn = (Button) findViewById(R.id.camera_take_cancel_btn);
        mTakeUsePicBtn = (Button) findViewById(R.id.camera_use_pic_btn);
        mFaceView = (FaceView) findViewById(R.id.face_view);

        textureView.setSurfaceTextureListener(this);
        mTakePicBtn.setOnClickListener(mClickListener);
        mTakeCancelBtn.setOnClickListener(mClickListener);
        mTakeUsePicBtn.setOnClickListener(mClickListener);
    }

    //=======================textureView=============================
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            mCamera.setPreviewTexture(surface);
            initFaceCameraParameters();
            mCamera.startPreview();
            startFaceDetection();

        } catch (Exception e) {
            if (null != mCamera) {
                mCamera.release();
                mCamera = null;
            }
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (null != mCamera) {
            //如果是拍照状态，则不释放图片
            if (mTakePicBtn.getVisibility() == View.VISIBLE) {
                releaseImageViewResource(mCameraPreviewIv);
                mCameraPreviewIv.setImageBitmap(null);
            }
//            stopFaceDetection();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    //===========================camera config=========================
    private void startFaceDetection() {
        if (null != mCamera) {
            Camera.Parameters params = mCamera.getParameters();
            if (params.getMaxNumDetectedFaces() > 0) {
                mCamera.startFaceDetection();
            }
            mCamera.setFaceDetectionListener(mFaceDetectionListener);
        }
    }

    private void stopFaceDetection() {
        if (null != mCamera) {
            Camera.Parameters params = mCamera.getParameters();
            if (params.getMaxNumDetectedFaces() > 0) {
                mCamera.stopFaceDetection();
            }
        }
    }

    /**
     * 配置前置摄像头参数
     */
    private void initFaceCameraParameters() {
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        Camera.Size previewSize = getLargePreviewSize(mCamera);
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        Camera.Size pictureSize = getLargePictureSize(mCamera);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        parameters.setRotation(90);
        //获取摄像头信息
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, info);
        //判断是否是前置摄像头，如果是前置摄像头，继续调整旋转角度
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            parameters.setRotation(270);
        }
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);//将预览旋转90度
    }

    public Camera.Size getLargePictureSize(Camera camera) {
        if (camera != null) {
            List<Camera.Size> sizes = camera.getParameters().getSupportedPictureSizes();
            Camera.Size temp = sizes.get(0);
            for (int i = 1; i < sizes.size(); i++) {
                float scale = (float) (sizes.get(i).height) / sizes.get(i).width;
                if (temp.width < sizes.get(i).width && scale < 0.6f && scale > 0.5f)
                    temp = sizes.get(i);
            }
            return temp;
        }
        return null;
    }


    public Camera.Size getLargePreviewSize(Camera camera) {
        if (camera != null) {
            List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
            Camera.Size temp = sizes.get(0);
            for (int i = 1; i < sizes.size(); i++) {
                if (temp.width < sizes.get(i).width)
                    temp = sizes.get(i);
            }
            return temp;
        }
        return null;
    }


    /**
     * 开始预览
     */
    private void startPreview() {
        if (null != mCamera) {
            mCamera.startPreview();
        }
    }

    /**
     * 停止预览
     */
    private void stopPreview() {
        if (null != mCamera) {
            stopFaceDetection();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
        }
    }

    //保存图片
    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ytsg");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mLocalImagePath = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
            mediaFile = new File(mLocalImagePath);
        } else {
            return null;
        }
        return mediaFile;
    }

    private void loadLocalFaceImage() {
        //加载本地图片
        GlideApp.with(this)
                .asBitmap()
                .load(mLocalImagePath)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        mCameraPreviewIv.setVisibility(View.VISIBLE);
                        mCameraPreviewIv.setImageBitmap(resource);
                    }
                });
    }

    /**
     * 删除图片
     */
    public void delMediaFileByPath() {
        if (null != mLocalImagePath) {
            File file = new File(mLocalImagePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 回收图片
     */
    private void releaseImageViewResource(ImageView imageView) {
        if (imageView == null) return;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    private FaceCameraListener mFaceCameraListener;

    public void setFaceCameraListener(FaceCameraListener listener) {
        this.mFaceCameraListener = listener;
    }

    public interface FaceCameraListener {
        void useFacePicture(String localImage);

        void cancelFaceCamera();
    }

}
