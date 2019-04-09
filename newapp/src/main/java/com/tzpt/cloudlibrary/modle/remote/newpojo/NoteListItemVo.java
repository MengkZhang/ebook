package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/17.
 */

public class NoteListItemVo {
    @SerializedName("bookId")
    @Expose
    public long bookId;

    @SerializedName("bookName")
    @Expose
    public String bookName;

    @SerializedName("borrowState")
    @Expose
    public int borrowState;

    @SerializedName("borrowerBookId")
    @Expose
    public int borrowerBookId;

    @SerializedName("buyBookId")
    @Expose
    public int buyBookId;

    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("idCard")
    @Expose
    public String idCard;

    @SerializedName("noteDate")
    @Expose
    public long noteDate;

    @SerializedName("noteDateStr")
    @Expose
    public String noteDateStr;

    @SerializedName("noteTime")
    @Expose
    public String noteTime;

    @SerializedName("noteType")
    @Expose
    public int noteType;

    @SerializedName("readingNote")
    @Expose
    public String readingNote;

}
