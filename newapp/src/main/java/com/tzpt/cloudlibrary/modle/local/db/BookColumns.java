package com.tzpt.cloudlibrary.modle.local.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 书籍
 * Created by Administrator on 2017/5/22.
 */
@Entity
public class BookColumns {
    @Id
    private Long id;
    //书籍唯一标识符
    @Property(nameInDb = "book_id")
    private String book_id;
    //书籍作者
    @Property(nameInDb = "author")
    private String author;
    //书籍名字
    @Property(nameInDb = "title")
    private String title;
    //书籍下载路径
    @Property(nameInDb = "download_file")
    private String download_file;
    //书籍大小
    @Property(nameInDb = "size")
    private String size;
    //本地路径
    @Property(nameInDb = "local_path")
    private String local_path;
    //书籍封面图片
    @Property(nameInDb = "cover_image")
    private String cover_image;
    //段落索引
    @Property(nameInDb = "paragraph_index")
    private String paragraph_index;
    //元素索引
    @Property(nameInDb = "element_index")
    private String element_index;
    //字符索引
    @Property(nameInDb = "char_index")
    private String char_index;
    //最后阅读时间戳
    @Property(nameInDb = "time_stamp")
    private String time_stamp;
    //阅读进度
    @Property(nameInDb = "read_progress")
    private String read_progress;
    //分享地址
    @Property(nameInDb = "share_url")
    private String share_url;
    //分享简介
    @Property(nameInDb = "share_content")
    private String share_content;
    //阅读页数
    @Property(nameInDb = "read_page_count")
    private int readPageCount;
    //所属管
    @Property(nameInDb = "belong_lib_code")
    private String belongLibCode;
    //内容简介
    @Property(nameInDb = "desc_content")
    private String desc_content;

    public String getDesc_content() {
        return desc_content;
    }

    public void setDesc_content(String desc_content) {
        this.desc_content = desc_content;
    }

    public String getRead_progress() {
        return this.read_progress;
    }
    public void setRead_progress(String read_progress) {
        this.read_progress = read_progress;
    }
    public String getTime_stamp() {
        return this.time_stamp;
    }
    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }
    public String getChar_index() {
        return this.char_index;
    }
    public void setChar_index(String char_index) {
        this.char_index = char_index;
    }
    public String getElement_index() {
        return this.element_index;
    }
    public void setElement_index(String element_index) {
        this.element_index = element_index;
    }
    public String getParagraph_index() {
        return this.paragraph_index;
    }
    public void setParagraph_index(String paragraph_index) {
        this.paragraph_index = paragraph_index;
    }
    public String getCover_image() {
        return this.cover_image;
    }
    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }
    public String getLocal_path() {
        return this.local_path;
    }
    public void setLocal_path(String local_path) {
        this.local_path = local_path;
    }
    public String getSize() {
        return this.size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public String getDownload_file() {
        return this.download_file;
    }
    public void setDownload_file(String download_file) {
        this.download_file = download_file;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return this.author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getBook_id() {
        return this.book_id;
    }
    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getShare_content() {
        return this.share_content;
    }
    public void setShare_content(String share_content) {
        this.share_content = share_content;
    }
    public String getShare_url() {
        return this.share_url;
    }
    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }
    public String getBelongLibCode() {
        return this.belongLibCode;
    }
    public void setBelongLibCode(String belongLibCode) {
        this.belongLibCode = belongLibCode;
    }
    public int getReadPageCount() {
        return this.readPageCount;
    }
    public void setReadPageCount(int readPageCount) {
        this.readPageCount = readPageCount;
    }
    @Generated(hash = 386703573)
    public BookColumns(Long id, String book_id, String author, String title,
            String download_file, String size, String local_path,
            String cover_image, String paragraph_index, String element_index,
            String char_index, String time_stamp, String read_progress,
            String share_url, String share_content, int readPageCount,
            String belongLibCode, String desc_content) {
        this.id = id;
        this.book_id = book_id;
        this.author = author;
        this.title = title;
        this.download_file = download_file;
        this.size = size;
        this.local_path = local_path;
        this.cover_image = cover_image;
        this.paragraph_index = paragraph_index;
        this.element_index = element_index;
        this.char_index = char_index;
        this.time_stamp = time_stamp;
        this.read_progress = read_progress;
        this.share_url = share_url;
        this.share_content = share_content;
        this.readPageCount = readPageCount;
        this.belongLibCode = belongLibCode;
        this.desc_content = desc_content;
    }

    @Generated(hash = 816677064)
    public BookColumns() {
    }
}
