package com.tzpt.cloudlibrary.base.data;

/**
 * Created by Administrator on 2018/10/31.
 */

public class Note {
    public long mId;
    public String mContent;
    public String mModifyDate;

    @Override
    public boolean equals(Object obj) {
        if (((Note) obj).mId == this.mId) {
            return true;
        }
        return super.equals(obj);
    }
}
