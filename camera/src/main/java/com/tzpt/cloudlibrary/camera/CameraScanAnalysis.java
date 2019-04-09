package com.tzpt.cloudlibrary.camera;

import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.zbar.Config;
import com.tzpt.cloudlibrary.zbar.Image;
import com.tzpt.cloudlibrary.zbar.ImageScanner;
import com.tzpt.cloudlibrary.zbar.Symbol;
import com.tzpt.cloudlibrary.zbar.SymbolSet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 扫描分析
 * Created by Administrator on 2018/10/17.
 */

public class CameraScanAnalysis implements Camera.PreviewCallback {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private ImageScanner mImageScanner;
    private Handler mHandler;
    private Rect mRect;

    private Image barcode;
    private boolean allowAnalysis = false;

    CameraScanAnalysis(Handler handler) {
        mImageScanner = new ImageScanner();
        mImageScanner.setConfig(0, Config.X_DENSITY, 3);
        mImageScanner.setConfig(0, Config.Y_DENSITY, 3);

        mHandler = handler;
    }

    void onStop() {
        this.allowAnalysis = false;
    }

    void onStart(Rect rect) {
        this.allowAnalysis = true;
        mRect = rect;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (allowAnalysis) {
            System.gc();
            System.gc();

            Camera.Size size = camera.getParameters().getPreviewSize();
//            Log.e("CameraScanAnalysis", "onPreviewFrame*********************************" + data.length);
//            Log.e("CameraScanAnalysis", "left: " + mRect.left + " top: " + mRect.top + " width:" + mRect.width() + " height: " + mRect.height());
            if (barcode == null) {
                barcode = new Image(size.width, size.height, "Y800");
            }
            barcode.setData(data);
            barcode.setCrop(mRect.left, mRect.top, mRect.width(), mRect.height());

            executorService.execute(mAnalysisTask);
        }
    }

    private Runnable mAnalysisTask = new Runnable() {
        @Override
        public void run() {
//            Log.e("CameraScanAnalysis", "mAnalysisTask*********************************start");
            int result = mImageScanner.scanImage(barcode);

            String resultStr = null;
            if (result != 0) {
                SymbolSet symSet = mImageScanner.getResults();
                for (Symbol sym : symSet)
                    resultStr = sym.getData();
            }

            if (!TextUtils.isEmpty(resultStr)) {
                Message message = mHandler.obtainMessage();
                message.what = 0;
                message.obj = resultStr.trim();
                message.sendToTarget();
//                Log.e("CameraScanAnalysis", "mAnalysisTask*********************************end");
            } else {
                //识别失败
                Message message = mHandler.obtainMessage();
                message.what = -1;
                message.sendToTarget();
//                Log.e("CameraScanAnalysis", "mAnalysisTask*********************************failed");
            }
        }
    };
}
