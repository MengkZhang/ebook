package com.tzpt.cloudlibrary.modle.local.db;

import android.content.Context;
import android.util.Log;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.rx.RxQuery;

import java.util.List;

import rx.Observable;

/**
 * 数据库操作接口
 * Created by Administrator on 2017/5/22.
 */

public final class DBManager {
    private static final String DB_NAME = "cloud_library";
    private static DBManager mInstance;
    private DaoSession mDaoSession;

    public static DBManager getInstance() {
        if (mInstance == null) {
            throw new RuntimeException("DBManager not been initialized!");
        }
        return mInstance;
    }

    public static void init(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
    }

    private DBManager(Context context) {
        UpgradeHelper helper = new UpgradeHelper(context, DB_NAME);
        Database database = helper.getWritableDb();
        mDaoSession = new DaoMaster(database).newSession();
    }

    /**
     * 插入书籍
     */
    public void insertBook(String bookId, String author, String title, String coverImg, String downloadUrl,
                           String localPath, String size, String shareUrl, String shareContent, String belongLibCode,String descContent) {
        BookColumnsDao bookDao = mDaoSession.getBookColumnsDao();
        BookColumns oldBook = bookDao.queryBuilder().where(BookColumnsDao.Properties.Book_id.eq(bookId)).build().unique();
        if (oldBook == null) {
            BookColumns book = new BookColumns();
            book.setBook_id(bookId);
            book.setAuthor(author);
            book.setTitle(title);
            book.setCover_image(coverImg);
            book.setDownload_file(downloadUrl);
            book.setLocal_path(localPath);
            book.setSize(size);
            book.setShare_url(shareUrl);
            book.setShare_content(shareContent);
            book.setBelongLibCode(belongLibCode);
            book.setDesc_content(descContent);
            bookDao.insertOrReplace(book);
        }
    }

    /**
     * 获取本地书籍列表
     *
     * @return 书籍列表
     */
    public List<BookColumns> getBookList() {
        BookColumnsDao bookDao = mDaoSession.getBookColumnsDao();
        List<BookColumns> list = bookDao.queryBuilder().orderDesc(BookColumnsDao.Properties.Time_stamp).list();
        return list;
    }

    /**
     * 返回书籍数目
     *
     * @return 书籍数量
     */
    public int getBookCount() {
        BookColumnsDao bookDao = mDaoSession.getBookColumnsDao();
        List<BookColumns> list = bookDao.queryBuilder().list();
        if (list != null && list.size() > 0) {
            return list.size();
        }
        return 0;
    }

    /**
     * 返回视频数量
     * @return
     */
    public int getVideoCount() {
        VideoColumnsDao videoColumnsDao = mDaoSession.getVideoColumnsDao();
        List<VideoColumns> list = videoColumnsDao.queryBuilder().list();
        if (list != null && list.size() > 0) {
            return list.size();
        }
        return 0;
    }

    /**
     * 删除书籍
     *
     * @param bookIds
     */
    public void delBook(List<String> bookIds) {
        BookColumnsDao bookDao = mDaoSession.getBookColumnsDao();
        for (String id : bookIds) {
            BookColumns book = bookDao.queryBuilder().where(BookColumnsDao.Properties.Book_id.eq(id)).build().unique();
            bookDao.delete(book);
            delBookMarks(id);
        }
    }

    /**
     * 获取书籍最后阅读位置
     *
     * @param bookId
     * @return
     */
    public BookColumns getBookStoredPosition(String bookId) {
        BookColumnsDao bookColumnsDao = mDaoSession.getBookColumnsDao();
        BookColumns bookColumns = bookColumnsDao.queryBuilder().where(BookColumnsDao.Properties.Book_id.eq(bookId)).build().unique();
        if (bookColumns != null && bookColumns.getParagraph_index() != null) {
            return bookColumns;
        }
        return null;
    }

    /**
     * 保存书籍最后阅读位置
     */
    public void saveBookReadPosition(String bookId, String paragraphIndex, String elementIndex,
                                     String charIndex, String timestamp, String progress, int pageCount) {
        BookColumnsDao bookColumnsDao = mDaoSession.getBookColumnsDao();
        BookColumns bookColumns = bookColumnsDao.queryBuilder().where(BookColumnsDao.Properties.Book_id.eq(bookId)).build().unique();
        if (bookColumns != null) {
            bookColumns.setParagraph_index(paragraphIndex);
            bookColumns.setElement_index(elementIndex);
            bookColumns.setChar_index(charIndex);
            bookColumns.setTime_stamp(timestamp);
            bookColumns.setRead_progress(progress);
            bookColumns.setReadPageCount(pageCount);
            bookColumnsDao.update(bookColumns);
        }
    }

    /**
     * 获取书籍的书签列表
     *
     * @param bookId 书籍ID
     * @return 书签列表
     */
    public List<BookMarkColumns> getBookMarkList(String bookId) {
        BookMarkColumnsDao bookMarkColumnsDao = mDaoSession.getBookMarkColumnsDao();
        List<BookMarkColumns> bookMarkColumnses = bookMarkColumnsDao.queryBuilder().where(BookMarkColumnsDao.Properties.Book_id.eq(bookId)).build().list();
        return bookMarkColumnses;
    }

    /**
     * 保存书籍书签
     *
     * @param bookId         书籍ID
     * @param paragraphIndex 段落位置
     * @param elementIndex   元素位置
     * @param charIndex      字符位置
     * @param progress       阅读进度
     * @param tocStr         书签所在章节
     * @return false 表示已经重复书签，true表示保存成功
     */
    public long saveBookMark(String bookId, String paragraphIndex, String elementIndex,
                             String charIndex, String timestamp, String progress, String tocStr,
                             String content) {
        BookMarkColumnsDao bookMarkColumnsDao = mDaoSession.getBookMarkColumnsDao();
        BookMarkColumns bookMarkColumns = new BookMarkColumns();
        bookMarkColumns.setBook_id(bookId);
        bookMarkColumns.setToc_title(tocStr);
        bookMarkColumns.setContent(content);
        bookMarkColumns.setProgress(progress);
        bookMarkColumns.setParagraph_index(paragraphIndex);
        bookMarkColumns.setElement_index(elementIndex);
        bookMarkColumns.setChar_index(charIndex);
        bookMarkColumns.setAdd_date(timestamp);
        return bookMarkColumnsDao.insertOrReplace(bookMarkColumns);
    }

    /**
     * 删除书籍书签
     *
     * @param bookId
     * @param position
     */
    public void delBookMark(String bookId, String position) {
        BookMarkColumnsDao bookMarkColumnsDao = mDaoSession.getBookMarkColumnsDao();
        BookMarkColumns bookMark = bookMarkColumnsDao.queryBuilder().where(BookMarkColumnsDao.Properties.Book_id.eq(bookId),
                BookMarkColumnsDao.Properties.Paragraph_index.eq(position)).unique();
        if (bookMark != null) {
            bookMarkColumnsDao.delete(bookMark);
        }
    }

    /**
     * 删除某本书的所有书签
     *
     * @param bookId 书籍ID
     */
    private void delBookMarks(String bookId) {
        BookMarkColumnsDao bookMarkColumnsDao = mDaoSession.getBookMarkColumnsDao();
        List<BookMarkColumns> bookMarkColumnses = bookMarkColumnsDao.queryBuilder().where(BookMarkColumnsDao.Properties.Book_id.eq(bookId)).build().list();
        if (bookMarkColumnses != null && bookMarkColumnses.size() > 0) {
            bookMarkColumnsDao.deleteInTx(bookMarkColumnses);
        }
    }

    /**
     * 获取本地所有视频合集，包括视频列表
     *
     * @return 视频合集和视频列表
     */
    public Observable<List<VideoSetColumns>> getVideoSet() {
        mDaoSession.getVideoSetColumnsDao().detachAll();
        return mDaoSession.getVideoSetColumnsDao().rx().loadAll();
    }

    /**
     * 添加视频
     *
     * @param videoSet 视频合集，如果没有记录会新增
     * @param video    视频
     */
    public void addVideo(final VideoSetColumns videoSet, final VideoColumns video) {
        VideoSetColumnsDao videoSetDao = mDaoSession.getVideoSetColumnsDao();
        VideoSetColumns videoSetTmp = videoSetDao.queryBuilder().where(VideoSetColumnsDao.Properties.Id.eq(videoSet.getId())).unique();
        if (videoSetTmp == null) {
            videoSetDao.insert(videoSet);
        }

        VideoColumnsDao videoDao = mDaoSession.getVideoColumnsDao();
        VideoColumns videoTmp = videoDao.queryBuilder().where(VideoColumnsDao.Properties.Id.eq(video.getId())).unique();
        if (videoTmp == null) {
            video.setVideoSetId(videoSet.getId());
            videoDao.insert(video);
        }
    }

    /**
     * 删除合集数据
     *
     * @param idList 合集ID列表
     */
    public void delVideoSet(List<Long> idList) {
        VideoSetColumnsDao videoSetDao = mDaoSession.getVideoSetColumnsDao();
        videoSetDao.deleteByKeyInTx(idList);
    }

    /**
     * 删除合集数据
     *
     * @param setId 合集ID
     */
    public void delVideoSet(long setId) {
        VideoSetColumnsDao videoSetDao = mDaoSession.getVideoSetColumnsDao();
        videoSetDao.deleteByKey(setId);
    }

    public VideoSetColumns getVideoSet(long id) {
        return mDaoSession.getVideoSetColumnsDao().queryBuilder().where(VideoSetColumnsDao.Properties.Id.eq(id)).unique();
    }

    public Observable<List<VideoColumns>> getVideoList(long setId) {
        mDaoSession.getVideoColumnsDao().detachAll();
        RxQuery<VideoColumns> rxQuery = mDaoSession.getVideoColumnsDao().queryBuilder().where(VideoColumnsDao.Properties.VideoSetId.eq(setId)).rx();
        return rxQuery.list();
    }

    public Observable<List<VideoColumns>> getVideoList() {
        return mDaoSession.getVideoColumnsDao().queryBuilder().rx().list();
    }

    public Observable<List<DownInfoColumns>> getDownInfoList() {
        mDaoSession.getVideoSetColumnsDao().detachAll();
        return mDaoSession.getDownInfoColumnsDao().rx().loadAll();
    }

    /**
     * 批量删除下载记录
     *
     * @param downLoadUrlList 下载URL
     */
    public void deleteDownloadInfo(List<String> downLoadUrlList) {
        DownInfoColumnsDao downInfoColumnsDao = mDaoSession.getDownInfoColumnsDao();
        downInfoColumnsDao.deleteByKeyInTx(downLoadUrlList);
    }

    /**
     * 删除下载记录
     *
     * @param downLoadUrl 下载URL
     */
    public void deleteDownloadInfo(String downLoadUrl) {
        DownInfoColumnsDao downInfoColumnsDao = mDaoSession.getDownInfoColumnsDao();
        downInfoColumnsDao.deleteByKey(downLoadUrl);
    }

    /**
     * 更新下载记录数据
     *
     * @param downInfoColumns 下载对象
     */
    public void updateDownInfo(DownInfoColumns downInfoColumns) {
        DownInfoColumnsDao downInfoColumnsDao = mDaoSession.getDownInfoColumnsDao();
        downInfoColumnsDao.update(downInfoColumns);
    }

    /**
     * 更新某个下载记录的状态
     *
     * @param url
     * @param status
     */
    public void updateDownInfoStatus(String url, int status) {
        DownInfoColumnsDao downInfoColumnsDao = mDaoSession.getDownInfoColumnsDao();
        DownInfoColumns downInfoTmp = downInfoColumnsDao.queryBuilder().where(DownInfoColumnsDao.Properties.Url.eq(url)).unique();
        if (downInfoTmp != null) {
            downInfoTmp.setStatus(status);
            downInfoColumnsDao.update(downInfoTmp);
        }
    }

    public void updateDownInfoTotalBytes(String url, long totalBytes) {
        DownInfoColumnsDao downInfoColumnsDao = mDaoSession.getDownInfoColumnsDao();
        DownInfoColumns downInfoTmp = downInfoColumnsDao.queryBuilder().where(DownInfoColumnsDao.Properties.Url.eq(url)).unique();
        if (downInfoTmp != null) {
            downInfoTmp.setCountLength(totalBytes);
            downInfoColumnsDao.update(downInfoTmp);
        }
    }

    public void updateDownInfoReadBytes(String url, long readBytes) {
        DownInfoColumnsDao downInfoColumnsDao = mDaoSession.getDownInfoColumnsDao();
        DownInfoColumns downInfoTmp = downInfoColumnsDao.queryBuilder().where(DownInfoColumnsDao.Properties.Url.eq(url)).unique();
        if (downInfoTmp != null) {
            downInfoTmp.setReadLength(readBytes);
            downInfoColumnsDao.update(downInfoTmp);
        }
    }

    /**
     * 新增下载记录数据
     *
     * @param downInfoColumns 下载对象
     */
    public void insertDownInfo(DownInfoColumns downInfoColumns, boolean reset) {
        DownInfoColumnsDao downInfoColumnsDao = mDaoSession.getDownInfoColumnsDao();
        DownInfoColumns downInfoTmp = downInfoColumnsDao.queryBuilder().where(DownInfoColumnsDao.Properties.Url.eq(downInfoColumns.getUrl())).unique();
        if (downInfoTmp == null) {
            downInfoColumns.setAddTime(System.currentTimeMillis());
            downInfoColumnsDao.insert(downInfoColumns);
        } else if (reset) {
            downInfoColumnsDao.update(downInfoColumns);
        }
    }

    public DownInfoColumns getDownInfo(String url) {
        DownInfoColumnsDao downInfoColumnsDao = mDaoSession.getDownInfoColumnsDao();
        return downInfoColumnsDao.queryBuilder().where(DownInfoColumnsDao.Properties.Url.eq(url)).unique();
    }


    public void updateVideo(List<VideoColumns> videoList) {
        VideoColumnsDao videoDao = mDaoSession.getVideoColumnsDao();
        videoDao.updateInTx(videoList);
    }

    public void delVideo(long id) {
        VideoColumnsDao videoDao = mDaoSession.getVideoColumnsDao();
        videoDao.deleteByKey(id);
    }

    public void delVideo(List<Long> idList) {
        VideoColumnsDao videoDao = mDaoSession.getVideoColumnsDao();
        videoDao.deleteByKeyInTx(idList);
    }

    /**
     * 查询所有视频播放记录
     *
     * @return
     */
    public Observable<List<VideoPlayColumns>> getVideoPlayList() {
        return mDaoSession.getVideoPlayColumnsDao().queryBuilder().rx().list();
    }

    /**
     * 插入更新视频播放记录
     *
     * @param videoId    视频ID
     * @param playedTime 视频已播放时间
     * @param totalTime  视频合计时间
     */
    public void insertOrUpdateRecordPlayTime(long videoId, long playedTime, long totalTime) {
        VideoPlayColumns videoPlayColumns = new VideoPlayColumns();
        videoPlayColumns.setVideoId(videoId);
        videoPlayColumns.setPlayTime(playedTime);
        videoPlayColumns.setTotalTime(totalTime);
        VideoPlayColumnsDao playTimeDao = mDaoSession.getVideoPlayColumnsDao();
        VideoPlayColumns videoPlay = playTimeDao.queryBuilder().where(VideoPlayColumnsDao.Properties.VideoId.eq(videoId)).unique();
        if (videoPlay == null) {
            playTimeDao.insert(videoPlayColumns);
        } else {
            playTimeDao.update(videoPlayColumns);
        }
    }
}
