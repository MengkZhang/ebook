package com.tzpt.cloudlibrary.business_bean;

import com.tzpt.cloudlibrary.base.data.Note;

/**
 * 读书笔记列表
 * Created by tonyjia on 2018/11/9.
 */
public class ReadNoteBean {

    public Note mNote;          //笔记
    public long mBorrowBookId;  //借阅ID
    public long mBuyBookId;     //购书ID


    @Override
    public boolean equals(Object obj) {
        if (((ReadNoteBean) obj).mNote.mId == this.mNote.mId) {
            return true;
        }
        return super.equals(obj);
    }
}
