package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/11/17.
 */

public class BorrowBookDetailInfoVo extends BaseDataResultVo{
    @SerializedName("BorrowerBook")
    @Expose
    public BorrowBookListItemVo BorrowerBook;

    @SerializedName("libraryBorrowerNote")
    @Expose
    public List<NoteListItemVo> libraryBorrowerNotes;
}
