package com.tzpt.cloudlibrary.app.ebook.books.model;

/**
 * 书籍信息
 */
public class Book {
    /**
     * book name
     */
    public String name;
    
    /**
     * book author
     */
    public String author;
    
    /**
     * book's publisher
     */
    public String publish;
    
    /**
     * book file path
     */
    public String path;
    
    /**
     * total character count
     */
    public long totalCharacterCount = 0;
    
    /**
     * total page count
     */
    public int pageCount;
    
    /**
     * total files(all the html files) size
     */
    public long fileSize;
}
