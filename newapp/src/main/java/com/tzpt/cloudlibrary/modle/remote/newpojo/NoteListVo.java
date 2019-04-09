package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/11/17.
 */

public class NoteListVo {
    @SerializedName("pageCount")
    @Expose
    public int pageCount;

    @SerializedName("pageNo")
    @Expose
    public int pageNo;

    @SerializedName("totalCount")
    @Expose
    public int totalCount;

    @SerializedName("matchCount")
    @Expose
    public int matchCount;

    @SerializedName("totalPage")
    @Expose
    public int totalPage;

    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("resultList")
    @Expose
    public List<BookNoteItemVo> bookList;

    public class BookNoteItemVo {
        @SerializedName("bookId")
        @Expose
        public long bookId;

        @SerializedName("bookName")
        @Expose
        public String bookName;

        @SerializedName("borrowState")
        @Expose
        public int borrowState;

        @SerializedName("readingNoteDtoList")
        @Expose
        public List<NoteListItemVo> resultList;
    }
}
