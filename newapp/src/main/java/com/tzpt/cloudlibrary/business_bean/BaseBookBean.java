package com.tzpt.cloudlibrary.business_bean;

import com.tzpt.cloudlibrary.base.data.Author;
import com.tzpt.cloudlibrary.base.data.Book;
import com.tzpt.cloudlibrary.base.data.BookCategory;
import com.tzpt.cloudlibrary.base.data.EBook;
import com.tzpt.cloudlibrary.base.data.Library;
import com.tzpt.cloudlibrary.base.data.Press;

/**
 * Created by Administrator on 2018/10/30.
 */

public abstract class BaseBookBean {
    public Book mBook = new Book();
    public EBook mEBook = new EBook();
    public Author mAuthor = new Author();
    public Press mPress = new Press();
    public Library mLibrary = new Library();
    public BookCategory mCategory = new BookCategory();
}
