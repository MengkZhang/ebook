package com.tzpt.cloudlibrary.zlibrary.core.filesystem;

import com.amse.ys.zip.LocalFileHeader;
import com.amse.ys.zip.ZipFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 压缩文件
 * Created by Administrator on 2017/4/7.
 */
public class ZLZipEntryFile extends ZLArchiveEntryFile {
    private static final HashMap<ZLFile, ZipFile> mZipFileMap = new HashMap<>();

    ZLZipEntryFile(ZLFile parent, String name) {
        super(parent, name);
    }

    private static ZipFile getZipFile(final ZLFile file) throws IOException {
        synchronized (mZipFileMap) {
            // 图片打开时用到
            ZipFile zf = file.isCached() ? mZipFileMap.get(file) : null;
            if (zf == null) {
                zf = new ZipFile(file);
                if (file.isCached()) {
                    // 不走
                    mZipFileMap.put(file, zf);
                }
            }
            return zf;
        }
    }

    static void removeFromCache(ZLFile file) {
        mZipFileMap.remove(file);
    }

    @Override
    public boolean exists() {
        try {
            return mParent.exists() && getZipFile(mParent).entryExists(mName);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public long size() {
        try {
            return getZipFile(mParent).getEntrySize(mName);
        } catch (IOException e) {
            return 0;
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        // epub路径，OEBPS/images/cover.jpg
        return getZipFile(mParent).getInputStream(mName);
    }

    static List<ZLFile> archiveEntries(ZLFile archive) {
        try {
            final ZipFile zf = ZLZipEntryFile.getZipFile(archive);
            final Collection<LocalFileHeader> headers = zf.headers();
            if (!headers.isEmpty()) {
                ArrayList<ZLFile> entries = new ArrayList<>(headers.size());
                for (LocalFileHeader h : headers) {
                    entries.add(new ZLZipEntryFile(archive, h.FileName));
                }
                return entries;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
