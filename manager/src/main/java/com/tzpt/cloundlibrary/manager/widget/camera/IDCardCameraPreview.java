package com.tzpt.cloundlibrary.manager.widget.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.widget.FrameLayout;

import com.idcard.TFieldID;
import com.idcard.TParam;
import com.idcard.TRECAPIImpl;
import com.idcard.TStatus;
import com.idcard.TengineID;
import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.IDCardBean;
import com.tzpt.cloundlibrary.manager.utils.SoundManager;
import com.tzpt.cloundlibrary.manager.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/7/4.
 */

public class IDCardCameraPreview extends FrameLayout implements TextureView.SurfaceTextureListener {

    private static final int START_PREVIEW = 1000;
    private static final int START_PREVIEW_ERROR = 1001;

    public TRECAPIImpl engineDemo = new TRECAPIImpl();
    public static TengineID tengineID = TengineID.TIDCARD2;
    private TextureView mTextureView;
    private Camera mCamera;
    private Context mContext;
    private CameraConfigurationManager mConfigManager;
    private SoundManager mSoundManager;

    private boolean mIsDecode;
    private boolean isTurnOn;
    private boolean mIsCameraReleased = false; // 当前摄像头释放被释放

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_PREVIEW:
                case START_PREVIEW_ERROR:
                    if (mCallback != null) {
                        mCallback.callbackCheckCameraPermission();
                    }
                    break;
            }
        }
    };

    public IDCardCameraPreview(@NonNull Context context) {
        this(context, null);
    }

    public IDCardCameraPreview(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IDCardCameraPreview(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mConfigManager = new CameraConfigurationManager(context);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.view_custom_camera_preview, this);
        mTextureView = (TextureView) findViewById(R.id.camera_view);
        mTextureView.setSurfaceTextureListener(this);

        initializeEngine();
        initSound();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            initCamera(surface);
            mHandler.sendEmptyMessage(START_PREVIEW);
        } catch (Exception e) {
            mHandler.sendEmptyMessage(START_PREVIEW_ERROR);
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
            turnLightOff();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (null != mHandler) {
            mHandler.removeMessages(START_PREVIEW);
            mHandler.removeMessages(START_PREVIEW_ERROR);
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        if (!mIsDecode) {
            mIsDecode = true;
            onFocusSuccessDecode();
        }
    }

    private void initCamera(SurfaceTexture surface) throws IOException {
        mCamera = Camera.open();
        mCamera.setPreviewTexture(surface);
        mConfigManager.initFromCameraParameters(mCamera);
        mConfigManager.setDesiredCameraParameters(mCamera);
        mCamera.setDisplayOrientation(90);//将预览旋转90度
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        mCamera.setParameters(parameters);
    }

    /**
     * 预览数据
     */
    Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            try {
                //设置预览数据
                if (camera != null) {
                    AsyncDecode asyncDecode = new AsyncDecode();
                    asyncDecode.execute(data);
                    mCamera.setPreviewCallback(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 对焦成功后执行解析
     */
    private void onFocusSuccessDecode() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(mPreviewCallback);
        }
    }

    /**
     * 初始化声音
     *
     * @param
     */
    private void initSound() {
        mSoundManager = new SoundManager();
        mSoundManager.initSound(mContext);
    }

    /**
     * 播放声音
     */
    public void playSound() {
        mSoundManager.startPlaySound(mContext);
        mSoundManager.stopPlaySound(mContext);
    }

    /**
     * 初始化引擎
     */
    private void initializeEngine() {
        try {
            TStatus tStatus = engineDemo.TR_StartUP(Utils.getContext());

            engineDemo.TR_SetSupportEngine(TengineID.TIDCARD2);
            if (tStatus == TStatus.TR_TIME_OUT) {
                //toastInfo("引擎过期!");
            } else if (tStatus == TStatus.TR_FAIL) {
                //toastInfo("引擎初始化失败!");
            }
            if (tengineID == TengineID.TIDCARD2) {
                if (engineDemo.TR_SetSupportEngine(TengineID.TIDCARD2) == TStatus.TR_FAIL) {
                    //toastInfo("引擎不支持!");
                    //TODO 销毁界面
                }
                //设置获取身份证头像模式
                engineDemo.TR_SetParam(TParam.T_SET_HEADIMG, 1);
            }
        } catch (Exception e) {
            System.exit(0);
        }

    }


    /**
     * 开始预览
     */
    public void startPreview() {
        if (null == mCamera) {
            try {
                if (null != mTextureView && null != mTextureView.getSurfaceTexture()) {
                    initCamera(mTextureView.getSurfaceTexture());
                }
            } catch (Exception e) {
                mCamera = null;
            }
        }
        if (null != mCamera) {
            mIsDecode = false;
            mCamera.startPreview();//开始预览
        }
    }

    /**
     * 停止预览
     */
    public void stopPreview() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
        }
    }


    public void turnLight() {
        if (isTurnOn) {
            turnLightOff();
        } else {
            turnLightOn();
        }
    }

    /**
     * 打开手电筒
     */
    private void turnLightOn() {
        if (mCamera == null) {
            return;
        }
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
            isTurnOn = true;
        } catch (Exception e) {
        }
    }

    /**
     * 关闭手电筒
     */
    public void turnLightOff() {
        if (mCamera == null) {
            return;
        }
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            isTurnOn = false;
        } catch (Exception e) {
        }
    }

    /**
     * 重启摄像头
     */
    public void restartCamera() {
        if (mIsCameraReleased) {
            mIsCameraReleased = false;
            startPreview();
        }
    }

    /**
     * 释放摄像头
     */
    public void releaseCamera() {
        if (null != mCamera) {
            turnLightOff();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            mIsCameraReleased = true;
        }
    }


    private class AsyncDecode extends AsyncTask<byte[], Void, IDCardBean> {
        private Bitmap TakeBitmap = null;
        private Bitmap SmallBitmap = null;

        @Override
        protected IDCardBean doInBackground(byte[]... params) {
            Point cameraResolution = mConfigManager.getCameraResolution();
            decode(params[0], cameraResolution.x, cameraResolution.y);
            return null;
        }

        @Override
        protected void onPostExecute(IDCardBean iDcardBean) {
            mIsDecode = false;
            if (TakeBitmap != null) {
                TakeBitmap.recycle();
                TakeBitmap = null;
            }
            if (SmallBitmap != null) {
                SmallBitmap.recycle();
                SmallBitmap = null;
            }
            super.onPostExecute(iDcardBean);
        }

        private void decode(byte[] data, int width, int height) {
            if (TakeBitmap != null) {
                TakeBitmap.recycle();
                TakeBitmap = null;
            }
            if (SmallBitmap != null) {
                SmallBitmap.recycle();
                SmallBitmap = null;
            }
            YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width,
                    height, null);
            ByteArrayOutputStream outputSteam = new ByteArrayOutputStream();
            yuvimage.compressToJpeg(new Rect(0, 0, width, height), 100, outputSteam);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            TakeBitmap = BitmapFactory.decodeByteArray(outputSteam.toByteArray(), 0, outputSteam.toByteArray().length, options);
            try {
                outputSteam.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Rect rect = getFramingRectInPreview();
            engineDemo.TR_LoadMemBitMap(TakeBitmap);
            int ret = engineDemo.TR_BankJudgeExist4Margin(rect.left, rect.top, rect.right, rect.bottom);
            Log.e("IDCardCameraView", "++++++++++++++++++++++++++++++++++++++++++ret: " + ret);
            if (ret == 15 || ret == 7 || ret == 11 || ret == 13 || ret == 14
                    || (tengineID == TengineID.TIDBANK && ret == 10)) {
                TStatus isRecSucess = engineDemo.TR_RECOCR();
                if (tengineID == TengineID.TIDCARD2
                        || tengineID == TengineID.TIDSSCCARD
                        || tengineID == TengineID.TIDLPR) {
                    engineDemo.TR_FreeImage();
                    TStatus tStatus1 = engineDemo.TR_GetCardNumState();
                    if (tStatus1 == TStatus.TR_FAIL) {

                    } else if (isRecSucess == TStatus.TR_TIME_OUT) {

                    } else if (tengineID == TengineID.TIDCARD2) {
                        String num = engineDemo.TR_GetOCRFieldStringBuf(TFieldID.NUM);
                        String name = engineDemo.TR_GetOCRFieldStringBuf(TFieldID.NAME);
                        String bir = engineDemo.TR_GetOCRFieldStringBuf(TFieldID.BIRTHDAY);
                        String address = engineDemo.TR_GetOCRFieldStringBuf(TFieldID.ADDRESS);
                        String folk = engineDemo.TR_GetOCRFieldStringBuf(TFieldID.FOLK);
                        String sex = engineDemo.TR_GetOCRFieldStringBuf(TFieldID.SEX);
                        String period = engineDemo.TR_GetOCRFieldStringBuf(TFieldID.PERIOD);
                        String issue = engineDemo.TR_GetOCRFieldStringBuf(TFieldID.ISSUE);

                        if (num != null) {
                            Bundle bundle = new Bundle();
                            String smallBitmapPath = "";
                            if (TakeBitmap != null && width != 0 && height != 0) {
                                bundle.putParcelable("image", null);
                                SmallBitmap = null;
                            } else
                                bundle.putParcelable("image", null);
                            byte[] hdata = engineDemo.TR_GetHeadImgBuf();
                            int size = engineDemo.TR_GetHeadImgBufSize();
                            if (size > 0 && hdata != null && hdata.length > 0) {
                                SmallBitmap = BitmapFactory.decodeByteArray(hdata, 0, size);
                                smallBitmapPath = saveJpeg(SmallBitmap);
                            }
                            IDCardBean cardInfo = new IDCardBean();
                            cardInfo.NAME = name;
                            cardInfo.SEX = sex;
                            cardInfo.FOLK = folk + "族";
                            cardInfo.BIRTHDAY = bir;
                            cardInfo.ADDRESS = address;
                            cardInfo.NUM = num;
                            cardInfo.PERIOD = period;
                            cardInfo.ISSUE = issue;
                            if (!TextUtils.isEmpty(smallBitmapPath)) {
                                cardInfo.smallHeadPath = smallBitmapPath;
                            }
                            if (mCallback != null) {
                                mCallback.callbackBarCode(cardInfo);
                            }
                            stopPreview();
                            playSound();
                        }
                    }
                }
            } else {
                engineDemo.TR_FreeImage();
            }
        }


        /**
         * saveJpeg功能说明：
         * 以时间戳为文件名，保存Bitmap到SD卡的AATurec/img的目录下
         */
        private String saveJpeg(Bitmap bm) {
            String jpegName = "";
            String savePath = Utils.getContext().getExternalCacheDir() + "/head_img/";
            File folder = new File(savePath);
            if (!folder.exists()) {
                folder = new File(savePath);
                folder.mkdir();
            }

            long dataTake = System.currentTimeMillis();
            jpegName = savePath + "head_temp" + ".jpg";
            try {
                FileOutputStream fout = new FileOutputStream(jpegName);
                BufferedOutputStream bos = new BufferedOutputStream(fout);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jpegName;
        }


        private Rect getFramingRectInPreview() {
            Point screenResolution = mConfigManager.getCameraResolution();

            int h = screenResolution.y;
            int w = screenResolution.x;

            int x1 = 0, x2 = 0, y1 = 0, y2 = 0;
            if ((h * 203) >> 7 < w/*宽高比小于1.58*/) {
                int thresExt = 9;
                int DistZoomY = h / thresExt; //预览框的内缩值
                int RectHeight = h - (DistZoomY << 1);
                int RectWidth = (RectHeight * 203) >> 7;//(RectHeight*203*rateExpress)>>14;
                int DistZoomX = (w - RectWidth) >> 1;
                //得到预览方框的宽与高
                x1 = DistZoomX;//
                y1 = DistZoomY;//
                x2 = x1 + RectWidth - 1;
                y2 = y1 + RectHeight - 1;//
            } else {
                int thresExt = 9;
                int DistZoomX = w / thresExt; //预览框的内缩值
                int RectWidth = w - (DistZoomX << 1);
                int RectHeight = (RectWidth * 81) >> 7;
                int DistZoomY = (h - RectHeight) >> 1;

                //得到预览方框的宽与高
                x1 = DistZoomX;//
                y1 = DistZoomY;//
                x2 = x1 + RectWidth - 1;
                y2 = y1 + RectHeight - 1;//
            }
            Rect rect = new Rect();
            rect.left = x1;
            rect.right = x2;
            rect.top = y1;
            rect.bottom = y2;
            return rect;
        }

    }

    private CallbackIDCard mCallback;

    public void setIDCardListener(CallbackIDCard callback) {
        this.mCallback = callback;
    }

    public interface CallbackIDCard {
        void callbackBarCode(IDCardBean idCard);

        void callbackCheckCameraPermission();
    }

}
