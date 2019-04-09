package com.journeyapps.barcodescanner;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.journeyapps.barcodescanner.camera.CameraSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * 移动对焦
 * Created by ZhiqiangJia on 2017-04-06.
 */
public class AutoMoveFocusManager implements SensorControler.CameraFocusListener {

    private SensorControler controler;
    private Camera mCamera;
    private Context mContext;

    public AutoMoveFocusManager(Context context, Camera camera, CameraSettings settings) {
        this.mContext = context;
        this.mCamera = camera;
        this.controler = new SensorControler(context);
        controler = new SensorControler(context);
        controler.setCameraFocusListener(this);
        resetFoucs();
        controler.unlockFocus();
    }

    public void resetFoucs() {
        if (null != controler) {
            controler.restFoucs();
        }
    }

    public void start() {
        if (null != controler) {
            controler.onStart();
        }
    }

    public void stop() {
        if (null != controler) {
            controler.onStop();
        }
    }

    @Override
    public void onFocus() {
        int screenWidth = Utils.getScreenWidth(mContext);
        Point point = new Point(screenWidth / 2, screenWidth / 2);
        onCameraFocus(point);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "onFocus", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * @param point
     */
    private void onCameraFocus(Point point) {
        onCameraFocus(point, false);
    }

    /**
     * 相机对焦
     *
     * @param point
     * @param needDelay 是否需要延时
     */
    public void onCameraFocus(final Point point, boolean needDelay) {
        long delayDuration = needDelay ? 300 : 0;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!controler.isFocusLocked()) {
                    if (onFocus(point, autoFocusCallback)) {
                        controler.lockFocus();
                    }
                }
            }
        }, delayDuration);
    }

    private final Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            //聚焦之后根据结果修改图片
            if (success) {
                onFocusSuccess();
            } else {
                //聚焦失败显示的图片，由于未找到合适的资源，这里仍显示同一张图片
                onFocusFailed();
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //一秒之后才能再次对焦
                    controler.unlockFocus();
                }
            }, 1000);
        }
    };


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    /**
     * 手动聚焦
     *
     * @param point 触屏坐标
     */
    protected boolean onFocus(Point point, Camera.AutoFocusCallback callback) {
        if (mCamera == null) {
            return false;
        }
        Camera.Parameters parameters = null;
        try {
            parameters = mCamera.getParameters();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //不支持设置自定义聚焦，则使用自动聚焦，返回
        if (Build.VERSION.SDK_INT >= 14) {
            if (parameters.getMaxNumFocusAreas() <= 0) {
                return focus(callback);
            }
            List<Camera.Area> areas = new ArrayList<Camera.Area>();
            int left = point.x - 300;
            int top = point.y - 300;
            int right = point.x + 300;
            int bottom = point.y + 300;
            left = left < -1000 ? -1000 : left;
            top = top < -1000 ? -1000 : top;
            right = right > 1000 ? 1000 : right;
            bottom = bottom > 1000 ? 1000 : bottom;
            areas.add(new Camera.Area(new Rect(left, top, right, bottom), 100));
            parameters.setFocusAreas(areas);
            try {
                //本人使用的小米手机在设置聚焦区域的时候经常会出异常，看日志发现是框架层的字符串转int的时候出错了，
                //目测是小米修改了框架层代码导致，在此try掉，对实际聚焦效果没影响
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return false;
            }
        }
        return focus(callback);
    }

    private boolean focus(Camera.AutoFocusCallback callback) {
        try {
            mCamera.autoFocus(callback);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 聚焦成功
     */
    private void onFocusSuccess() {

    }

    /**
     * 聚焦失败
     */
    private void onFocusFailed() {

    }

}
