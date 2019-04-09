package com.tzpt.cloudlibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/****
 * 图片压缩处理
 *
 * 此方法只压缩图片的size大小，不改变图片高宽
 */
public class BitmapCompressionUtil {

    /***
     * 图片压缩方法
     *
     * @param bmp
     * @return
     * @throws IOException
     */
    public static Bitmap revisionImageSize(Bitmap bmp) {
        Bitmap bitmap = null;
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream isBm = null;
        try {
            baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 90;
            while (baos.toByteArray().length / 1024 > 128) { // 循环判断如果压缩后图片是否大于128kb,大于继续压缩
                baos.reset();// 重置baos即清空baos
                bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;// 每次都减少10
            }
            isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inPreferredConfig = Config.RGB_565;
            bitmap = BitmapFactory.decodeStream(isBm, null, op);// 把ByteArrayInputStream数据生成图片
        } catch (Exception e) {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (isBm != null) {
                    isBm.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 图片按比例大小压缩方法（根据Bitmap图片压缩）
     *
     * @param image
     * @return
     */
    public static Bitmap compressBitmap(Bitmap image) {
        Bitmap bitmap = null;
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream isBm = null;
        try {
            baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            if (baos.toByteArray().length / 1024 > 100) {// 判断如果图片大于128kb,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
                baos.reset();// 重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
            }
            isBm = new ByteArrayInputStream(baos.toByteArray());
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            // 320*480分辨率，所以高和宽我们设置为
            float hh = 640f;// 这里设置高度为480f
            float ww = 480f;// 这里设置宽度为320f
            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;// be=1表示不缩放
            if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
                be = (int) (newOpts.outWidth / ww);
            } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
                be = (int) (newOpts.outHeight / hh);
            }
            if (be <= 0) {
                be = 1;
            }
            newOpts.inSampleSize = be;// 设置缩放比例
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            isBm = new ByteArrayInputStream(baos.toByteArray());
            bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        } catch (OutOfMemoryError err) {
            bitmap.recycle();
            bitmap = null;
        } catch (Exception e) {
            bitmap.recycle();
            bitmap = null;
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (isBm != null) {
                    isBm.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 压缩好比例大小后再进行质量压缩
        return revisionImageSize(bitmap);
    }

    /**
     * @param srcPath
     * @return
     */
    public static Bitmap getImage(String srcPath) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            // 是640*480分辨率，所以高和宽我们设置为
            float hh = 640f;// 这里设置高度为640f
            float ww = 480f;// 这里设置宽度为480f
            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;// be=1表示不缩放
            if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
                be = (int) (newOpts.outWidth / ww);
            } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
                be = (int) (newOpts.outHeight / hh);
            }
            if (be <= 0) {
                be = 1;
            }
            newOpts.inSampleSize = be;// 设置缩放比例
            newOpts.inPurgeable = true;// 同时设置才会有效
            newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
            newOpts.inInputShareable = false;
        } catch (OutOfMemoryError err) {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        } catch (Exception e) {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        }
        return revisionImageSize(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return
     */
    public static void deleteFileWithPath(String filePath) {
        SecurityManager checker = new SecurityManager();
        File f = new File(filePath);
        checker.checkDelete(filePath);
        if (f.isFile()) {
            f.delete();
        }
    }

    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     * @param angle 被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }
}
