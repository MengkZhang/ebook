package com.tzpt.cloudlibrary.app.ebook.books.listener;

/**
 * Created by ZhiqiangJia on 2017-02-06.
 */

public interface EpubUIListener {


    void toastInfo(String messsge);

    /**
     * 打开epub电子图书
     *
     * @param fileRelativePath
     */
    void openEpubBooks(String fileRelativePath, String fileDownLoadUrl);

    /**
     * 获取下载进度
     */
    void downLoadProgress(int progress);

    /**
     * 销毁界面
     */
    void finishActivity();

    /**
     * 文件下载成功
     */
    void downLoadSuccess();

    /**
     * 文件下载失败
     */
    void downLoadFailure(String fileSavePath, String downLoadUrl);

    /**
     * 开始下载
     */
    void downLoadStart();

    /**
     * 打开文件失败
     */
    void openEpubBooksFailure(String fileSavePath, String url);
}
