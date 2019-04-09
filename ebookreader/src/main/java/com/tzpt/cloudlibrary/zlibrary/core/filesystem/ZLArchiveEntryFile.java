package com.tzpt.cloudlibrary.zlibrary.core.filesystem;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/4/7.
 */

public abstract class ZLArchiveEntryFile extends ZLFile {
    protected final ZLFile mParent;
    protected final String mName;

    protected ZLArchiveEntryFile(ZLFile parent, String name) {
        mParent = parent;
        mName = name;
        init();
    }

    private static String normalizeEntryName(String entryName) {
        while (entryName.startsWith("./")) {
            entryName = entryName.substring(2);
        }
        while (true) {
            final int index = entryName.lastIndexOf("/./");
            if (index == -1) {
                break;
            }
            entryName = entryName.substring(0, index) + entryName.substring(index + 2);
        }
        while (true) {
            final int index = entryName.indexOf("/../");
            if (index <= 0) {
                break;
            }
            final int prevIndex = entryName.lastIndexOf('/', index - 1);
            if (prevIndex == -1) {
                entryName = entryName.substring(index + 4);
                break;
            }
            entryName = entryName.substring(0, prevIndex) + entryName.substring(index + 3);
        }
        return entryName;
    }

    static ZLArchiveEntryFile createArchiveEntryFile(ZLFile archive, String entryName) {
        // images里的所有图片 每次打开图片都走这
        if (archive == null) {
            return null;
        }
        entryName = normalizeEntryName(entryName);
        switch (archive.mArchiveType & ArchiveType.ARCHIVE) {
            case ArchiveType.ZIP:
                return new ZLZipEntryFile(archive, entryName);
            default:
                return null;
        }
    }

    static List<ZLFile> archiveEntries(ZLFile archive) {
        switch (archive.mArchiveType & ArchiveType.ARCHIVE) {
            case ArchiveType.ZIP:
                return ZLZipEntryFile.archiveEntries(archive);
            case ArchiveType.TAR:
//                return ZLTarEntryFile.archiveEntries(archive);
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public String getPath() {
        return mParent.getPath() + ":" + mName;
    }

    @Override
    public String getLongName() {
        return mName;
    }

    @Override
    public ZLFile getParent() {
        return mParent;
    }

    @Override
    public ZLPhysicalFile getPhysicalFile() {
        ZLFile ancestor = mParent;
        while ((ancestor != null) && !(ancestor instanceof ZLPhysicalFile)) {
            ancestor = ancestor.getParent();
        }
        return (ZLPhysicalFile) ancestor;
    }
}
