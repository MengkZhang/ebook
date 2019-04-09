package com.tzpt.cloudlibrary.cbreader.book;

import java.util.UUID;

/**
 * Created by Administrator on 2017/4/8.
 */

public class Label {
    public final String Uid;
    public final String Name;

    public Label(String uid, String name) {
        if (uid == null || name == null) {
            throw new IllegalArgumentException("Label(" + uid + "," + name + ")");
        }
        Uid = uid;
        Name = name;
    }

    Label(String name) {
        this(UUID.randomUUID().toString(), name);
    }

    @Override
    public String toString() {
        return Name + "[" + Uid + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Label)) {
            return false;
        }
        return Name.equals(((Label)other).Name);
    }

    @Override
    public int hashCode() {
        return Name.hashCode();
    }
}
