package com.tzpt.cloudlibrary.app.ebook.books.controller;

import android.content.Context;

import com.tzpt.cloudlibrary.app.ebook.books.listener.EpubUIListener;
import com.tzpt.cloudlibrary.app.ebook.constant.EbookConstant;
import com.tzpt.cloudlibrary.app.ebook.downLoader.DownLoadHelper;
import com.tzpt.cloudlibrary.app.ebook.utils.FileUtil;
import com.tzpt.cloudlibrary.app.ebook.utils.SharePreferencesUtil;
import com.tzpt.cloudlibrary.app.ebook.utils.ZipUtils;

/**
 * epub业务控制层
 * Created by ZhiqiangJia on 2017-02-06.
 */
public class EpubReaderController implements DownLoadHelper.CallbackDownLoadFile {

    private EpubUIListener listener;
    //电子书的绝对路径
    public static String absolutePath;

    public EpubReaderController(EpubUIListener listener) {
        this.listener = listener;
    }

    //==================================电子书下载，解析目录==========================================

    /**
     * 下载电子书
     * 如果文件夹不存在则下载文件，如果存在则打开文件夹解析内容
     *
     * @param fileName 文件名称
     */
    public void downLoadOrParserEpubFile(Context context, String fileName, String fileDownLoadUrl) {
        absolutePath = EbookConstant.FILE_SAVE_URL + fileName;
        boolean isOpened = SharePreferencesUtil.getBoolean(context, fileDownLoadUrl, false);
        //是否解压过的程序：如果没有解压过，则执行下载程序
        if (!isOpened) {
            downLoadOrUnZipFile(context, fileName, fileDownLoadUrl);
            return;
        }
        if (FileUtil.isFolderExist(absolutePath)) {
            //打开epub电子图书
            listener.openEpubBooks(absolutePath, fileDownLoadUrl);
        } else {
            //解压和下载程序
            downLoadOrUnZipFile(context, fileName, fileDownLoadUrl);
        }
    }

    /**
     * 解压和下载程序
     *
     * @param fileName
     */
    private void downLoadOrUnZipFile(Context context, String fileName, String fileDownLoadUrl) {
        if (FileUtil.isFileExist(absolutePath + ".epub")) {
            try {
                unZipAndDeleteZip(fileName, fileDownLoadUrl);
            } catch (Exception e) {
                listener.toastInfo("打开文件失败！");
                SharePreferencesUtil.setBoolean(context, fileDownLoadUrl, false);
                listener.finishActivity();
            }
        } else {
            DownLoadHelper.getInstance().downLoadEpubFile(fileName, fileDownLoadUrl, this);
        }
    }

    /**
     * 下载文件成功,之行解压文件
     */
    @Override
    public void callbackDownLoadSuccess(String fileName, String url) {
        listener.downLoadSuccess();
        try {
            unZipAndDeleteZip(fileName, url);
        } catch (Exception e) {
            listener.toastInfo("打开文件失败！");
            String fileSavePath = EbookConstant.FILE_SAVE_URL + fileName;
            listener.openEpubBooksFailure(fileSavePath, url);
            listener.finishActivity();
        }
    }

    /**
     * 解压和删除epub原文件
     *
     * @param fileName
     */
    private void unZipAndDeleteZip(String fileName, String fileDownLoadUrl) throws Exception {
        String fileAbsolutePath = EbookConstant.FILE_SAVE_URL + fileName;
        String fileAllName = fileAbsolutePath + ".epub";
        //解压文件
        ZipUtils.unzipEpub(fileAllName, fileAbsolutePath, "");
        //删除文件
        ZipUtils.deleteFile(fileAllName);
        //打开epub电子图书
        listener.openEpubBooks(absolutePath, fileDownLoadUrl);

    }

    /**
     * 下载文件失败
     */
    @Override
    public void callbackDownLoadFailure(String fileName, String downLoadUrl) {
        String fileSavePath = EbookConstant.FILE_SAVE_URL + fileName;
        listener.downLoadFailure(fileSavePath, downLoadUrl);
        listener.finishActivity();
    }

    /**
     * 获取下载进度
     *
     * @param progress
     */
    @Override
    public void callbackDownLoadProgress(int progress) {
        if (null != listener) {
            listener.downLoadProgress(progress);
        }
    }

    @Override
    public void callbackDownLoadStart() {
        if (null != listener) {
            listener.downLoadStart();
        }
    }

}
