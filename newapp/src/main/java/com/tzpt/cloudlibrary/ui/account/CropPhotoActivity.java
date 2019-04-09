package com.tzpt.cloudlibrary.ui.account;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.tzpt.cloudlibrary.AppIntentGlobalName;
import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.widget.PicCropViewFinder;
import com.tzpt.cloudlibrary.widget.TouchImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/11/9.
 */

public class CropPhotoActivity extends AppCompatActivity {

    @BindView(R.id.crop_photo_tiv)
    TouchImageView mTouchImageView;
    @BindView(R.id.crop_photo_pcvf)
    PicCropViewFinder mPicCropViewFinder;

    private Unbinder mUnbinder;

    private static final int PICTURE_SAVE_COMPLETE = 1;
    private static final int GET_BITMAP_FAILED = 2;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case PICTURE_SAVE_COMPLETE:
                    Intent intent = new Intent();
                    File f = new File(CloudLibraryApplication.getAppContext().getExternalCacheDir(), "head.jpg");
                    intent.putExtra(AppIntentGlobalName.PIC_PATH, f.getPath());

                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case GET_BITMAP_FAILED:

                    break;
                default:
                    break;
            }
        }
    };


    @OnClick({R.id.crop_photo_cancel_btn, R.id.crop_photo_done_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.crop_photo_cancel_btn:
                finish();
                break;
            case R.id.crop_photo_done_btn:
                //                mCurrentDialog = new LoadingTipsDialog(CropPhotoActivity.this,
//                        getResources().getString(R.string.usercenter_saving_pic_tip));
//                mCurrentDialog.show();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        Bitmap finalBitmap = getBitmap();
                        if (finalBitmap == null) {
                            mHandler.sendEmptyMessage(GET_BITMAP_FAILED);
                        } else {
                            saveBitmap("head.jpg", finalBitmap);
                            finalBitmap.recycle();
                            mHandler.sendEmptyMessage(PICTURE_SAVE_COMPLETE);
                        }

                    }
                }).start();
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_crop_photo);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        mUnbinder = ButterKnife.bind(this);

        String imgPath = getIntent().getStringExtra(AppIntentGlobalName.PIC_PATH);
        Bitmap srcBitmap = getSrcBitmap(imgPath);
        if (srcBitmap != null) {
            mTouchImageView.setImageBitmap(srcBitmap);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;// 获取屏幕分辨率宽度
            int height = dm.heightPixels;

            mTouchImageView.initImageView(width, height, 0);
        } else {
            finish();
        }
    }

    private Bitmap getSrcBitmap(String path) {
        // File file = new File(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        // options.inSampleSize = 4;// 图片宽高都为原来的二分之一，即图片为原来的四分之一
        options.inJustDecodeBounds = true;
        Bitmap b = BitmapFactory.decodeFile(path, options);
        int width, height;
        width = options.outWidth;
        height = options.outHeight;

        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        int W = mDisplayMetrics.widthPixels;
        int H = mDisplayMetrics.heightPixels;

//        int widthFinal, heightFinal;
        int sizeW = 0, sizeH = 0;
        if (W < width) {
            sizeW = Math.round(width / W);
        }
        if (H < height) {
            sizeH = Math.round(height / H);
        }

//        options.inSampleSize = computeSampleSize(options, -1, W * H);

        if (sizeW >= sizeH) {
            options.inSampleSize = sizeW;
        } else {
            options.inSampleSize = sizeH;
        }

        options.inJustDecodeBounds = false;
//        int angle = getExifOrientation(path);
        b = BitmapFactory.decodeFile(path, options);
//        widthFinal = options.outWidth;
//        heightFinal = options.outHeight;

//        if (width > 0 && height > 0) {
//            Matrix matrix = new Matrix();
//            if (angle != 0) {
//                matrix.postRotate(angle);
//            }
//
//            Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, widthFinal, heightFinal, matrix, true);
//            if (resizeBmp != b) {
//                b.recycle();
//            }
//
//            b = resizeBmp;
//        }

        return b;
    }


    /**
     * 处理旋转后的图片
     *
     * @param filepath
     * @return
     */
    private int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }

    // 获取Activity的截屏
    private Bitmap takeScreenShot() {
        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(false);
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    /* 获取矩形区域内的截图 */
    private Bitmap getBitmap() {
        Bitmap screenShoot = takeScreenShot();

        int width = mPicCropViewFinder.getWidth();
        int height = mPicCropViewFinder.getHeight();

        Bitmap finalBitmap = null;
        try {
            Matrix matrix = new Matrix();
            int strokeWidth = getResources().getDimensionPixelSize(R.dimen.crop_photo_stroke_width);
            int marginWidth = getResources().getDimensionPixelSize(R.dimen.crop_photo_margin);
            float bitMapWidth = width - 2 * marginWidth - strokeWidth;
            float bWidthScale = 140f / bitMapWidth;
            matrix.postScale(bWidthScale, bWidthScale); //长和宽放大缩小的比例
            finalBitmap = Bitmap.createBitmap(screenShoot, 2 + marginWidth,
                    (height - (width - 2 * marginWidth)) / 2  + 2,
                    width - 2 * marginWidth - strokeWidth,
                    width - 2 * marginWidth - strokeWidth, matrix, true);
        } catch (OutOfMemoryError | NullPointerException oom) {
            finalBitmap = null;
        } finally {
            if (screenShoot != null) {
                screenShoot.recycle();
            }
        }


        return finalBitmap;
    }

    private void saveBitmap(String picName, Bitmap bm) {
        File f = new File(CloudLibraryApplication.getAppContext().getExternalCacheDir(), picName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

}
