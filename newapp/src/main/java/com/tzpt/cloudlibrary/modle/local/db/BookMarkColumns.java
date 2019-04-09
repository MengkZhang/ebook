package com.tzpt.cloudlibrary.modle.local.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 书签
 * Created by Administrator on 2017/5/22.
 */
@Entity
public class BookMarkColumns {
    @Id
    private Long id;
    //书籍唯一标识
    @Property(nameInDb = "book_id")
    private String book_id;
    //目录名字
    @Property(nameInDb = "toc_title")
    private String toc_title;
    //书签当前页内容
    @Property(nameInDb = "content")
    private String content;
    //标签进度
    @Property(nameInDb = "progress")
    private String progress;
    //标签摘要
    @Property(nameInDb = "original_text")
    private String original_text;
    //段落索引
    @Property(nameInDb = "paragraph_index")
    @Index(unique = true)
    private String paragraph_index;
    //元素索引
    @Property(nameInDb = "element_index")
    private String element_index;
    //字符索引
    @Property(nameInDb = "char_index")
    private String char_index;
    //添加日期
    @Property(nameInDb = "add_date")
    private String add_date;
    public String getAdd_date() {
        return this.add_date;
    }
    public void setAdd_date(String add_date) {
        this.add_date = add_date;
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
    public String getOriginal_text() {
        return this.original_text;
    }
    public void setOriginal_text(String original_text) {
        this.original_text = original_text;
    }
    public String getProgress() {
        return this.progress;
    }
    public void setProgress(String progress) {
        this.progress = progress;
    }
    public String getToc_title() {
        return this.toc_title;
    }
    public void setToc_title(String toc_title) {
        this.toc_title = toc_title;
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
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    @Generated(hash = 1370057874)
    public BookMarkColumns(Long id, String book_id, String toc_title,
            String content, String progress, String original_text,
            String paragraph_index, String element_index, String char_index,
            String add_date) {
        this.id = id;
        this.book_id = book_id;
        this.toc_title = toc_title;
        this.content = content;
        this.progress = progress;
        this.original_text = original_text;
        this.paragraph_index = paragraph_index;
        this.element_index = element_index;
        this.char_index = char_index;
        this.add_date = add_date;
    }
    @Generated(hash = 929820812)
    public BookMarkColumns() {
    }
}
