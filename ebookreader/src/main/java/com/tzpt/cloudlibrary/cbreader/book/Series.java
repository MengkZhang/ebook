package com.tzpt.cloudlibrary.cbreader.book;

import com.tzpt.cloudlibrary.cbreader.sort.TitledEntity;

/**
 * Created by Administrator on 2017/4/8.
 */

public  class Series extends TitledEntity {
    public Series(String title) {
        super(title);
    }

    public String getLanguage() {
        // TODO: return real language
        return "en";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Series)) {
            return false;
        }
        return getTitle().equals(((Series)o).getTitle());
    }

    @Override
    public int hashCode() {
        return getTitle().hashCode();
    }
}
