package com.tzpt.cloudlibrary.zlibrary.core.filesystem;

import com.tzpt.cloudlibrary.zlibrary.core.util.InputStreamHolder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 文件信息
 * Created by Administrator on 2017/4/7.
 */

public abstract class ZLFile implements InputStreamHolder {
    private final static HashMap<String, ZLFile> ourCachedFiles = new HashMap<>();

    interface ArchiveType {//常量接口
        int NONE = 0;
        int COMPRESSED = 0x00ff;
        int ZIP = 0x0100;
        int TAR = 0x0200;
        int ARCHIVE = 0xff00;
    }

    private String mExtension;
    private String mShortName;
    int mArchiveType;
    private boolean mIsCached;

    /**
     * 设定mExtension
     */
    protected void init() {
        final String name = getLongName();
        final int index = name.lastIndexOf('.');
        mExtension = (index > 0) ? name.substring(index + 1).toLowerCase().intern() : "";
        mShortName = name.substring(name.lastIndexOf('/') + 1);
        int archiveType = ArchiveType.NONE;
        switch (mExtension) {
            case "zip":
                archiveType |= ArchiveType.ZIP;
                break;
            case "oebzip":
                archiveType |= ArchiveType.ZIP;
                break;
            case "epub":
                archiveType |= ArchiveType.ZIP;
                break;
//            case "tar":
//                archiveType |= ArchiveType.TAR;
//                break;
        }
        mArchiveType = archiveType;
    }

    public static ZLFile createFileByPath(String path) {
        if (path == null) {
            return null;
        }
        ZLFile cached = ourCachedFiles.get(path);
        if (cached != null) {
            return cached;
        }

        int len = path.length();
        char first = len == 0 ? '*' : path.charAt(0);
        if (first != '/') {
            while (len > 1 && first == '.' && path.charAt(1) == '/') {
                path = path.substring(2);
                len -= 2;
                first = len == 0 ? '*' : path.charAt(0);
            }
            return ZLResourceFile.createResourceFile(path);
        }
        int index = path.lastIndexOf(':');
        if (index > 1) {
            final ZLFile archive = createFileByPath(path.substring(0, index));
            if (archive != null && archive.mArchiveType != 0) {
                return ZLArchiveEntryFile.createArchiveEntryFile(
                        archive, path.substring(index + 1)
                );
            }
        }
        return new ZLPhysicalFile(path);
    }

    public abstract long size();

    public abstract boolean exists();

    public abstract boolean isDirectory();

    public abstract String getPath();

    public abstract ZLFile getParent();

    public abstract ZLPhysicalFile getPhysicalFile();

    public abstract InputStream getInputStream() throws IOException;

//    public long lastModified() {
//        final ZLFile physicalFile = getPhysicalFile();
//        return physicalFile != null ? physicalFile.lastModified() : 0;
//    }

//    public final InputStream getInputStream(FileEncryptionInfo encryptionInfo) throws IOException {
//        if (encryptionInfo == null) {
//            return getInputStream();
//        }
//        if (EncryptionMethod.EMBEDDING.equals(encryptionInfo.Method)) {
//            return new EmbeddingInputStream(getInputStream(), encryptionInfo.ContentId);
//        }
//
//        throw new IOException("Encryption method " + encryptionInfo.Method + " is not supported");
//    }

    public String getUrl() {
        return "file://" + getPath();
    }

    public boolean isReadable() {
        return true;
    }

    public final boolean isCompressed() {
        return (0 != (mArchiveType & ArchiveType.COMPRESSED));
    }

    public final boolean isArchive() {
        return (0 != (mArchiveType & ArchiveType.ARCHIVE));
    }

    public abstract String getLongName();

    public final String getShortName() {
        return mShortName;
    }

    public final String getExtension() {
        return mExtension;
    }

    protected List<ZLFile> directoryEntries() {
        return Collections.emptyList();
    }

//    public final List<ZLFile> children() {
////        if (exists()) {
////            if (isDirectory()) {
////                return directoryEntries();
////            } else if (isArchive()) {
////                return ZLArchiveEntryFile.archiveEntries(this);
////            }
////        }
//        return Collections.emptyList();
//    }

    @Override
    public int hashCode() {
        return getPath().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ZLFile)) {
            return false;
        }
        return getPath().equals(((ZLFile) o).getPath());
    }

    @Override
    public String toString() {
        return "ZLFile [" + getPath() + "]";
    }

    boolean isCached() {
        return mIsCached;
    }

    public void setCached(boolean cached) {
        mIsCached = cached;
        if (cached) {
            ourCachedFiles.put(getPath(), this);
        } else {
            ourCachedFiles.remove(getPath());
            if (0 != (mArchiveType & ArchiveType.ZIP)) {
                ZLZipEntryFile.removeFromCache(this);
            }
        }
    }
}
