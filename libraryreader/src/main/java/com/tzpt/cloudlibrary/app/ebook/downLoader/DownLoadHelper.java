package com.tzpt.cloudlibrary.app.ebook.downLoader;

import com.tzpt.cloudlibrary.app.ebook.constant.EbookConstant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;

/**
 * epub 文件下载器
 * Created by ZhiqiangJia on 2017-01-16.
 */
public class DownLoadHelper {

    private static DownLoadHelper ourInstance = new DownLoadHelper();

    public static DownLoadHelper getInstance() {
        return ourInstance;
    }

    private DownLoadHelper() {
    }


    /**
     * 下载epub电子书文件
     *
     * @param fileName 文件名称
     * @param url      下载地址
     */
    public void downLoadEpubFile(final String fileName, final String url, final CallbackDownLoadFile callback) {
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .connTimeOut(30000L)
                .execute(new FileCallBack(EbookConstant.FILE_SAVE_URL, fileName + ".epub") {
                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        if (null != callback) {
                            callback.callbackDownLoadStart();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (null != callback) {
                            callback.callbackDownLoadFailure(fileName, url);
                        }
                    }

                    @Override
                    public void onResponse(File response, int id) {

                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        if (null == callback) {
                            return;
                        }
                        int newProgress = (int) (100 * progress);
                        callback.callbackDownLoadProgress(newProgress);
                        if (newProgress == 100) {
                            callback.callbackDownLoadSuccess(fileName, url);
                        }
                    }
                });
    }

    /**
     * 下载文件的回调
     */
    public interface CallbackDownLoadFile {
        /**
         * 下载文件成功
         */
        void callbackDownLoadSuccess(String fileName, String url);

        /**
         * 下载文件失败
         */
        void callbackDownLoadFailure(String fileSavePath, String downLoadUrl);

        /**
         * 获取下载进度
         *
         * @param progress
         */
        void callbackDownLoadProgress(int progress);

        /**
         * 开始下载
         */
        void callbackDownLoadStart();
    }
}
