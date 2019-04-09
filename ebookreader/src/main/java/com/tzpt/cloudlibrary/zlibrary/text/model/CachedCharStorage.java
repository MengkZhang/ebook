package com.tzpt.cloudlibrary.zlibrary.text.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Administrator on 2017/4/9.
 */

public final class CachedCharStorage {
    protected final ArrayList<WeakReference<char[]>> mArray = new ArrayList<>();

    private final String mDirectoryName;
    private final String mFileExtension;

    public CachedCharStorage(String directoryName, String fileExtension, int blocksNumber) {
        mDirectoryName = directoryName + '/';
        mFileExtension = '.' + fileExtension;
        mArray.addAll(Collections.nCopies(blocksNumber, new WeakReference<char[]>(null)));
    }

    private String fileName(int index) {
        return mDirectoryName + index + mFileExtension;
    }

//    public int size() {
//        return mArray.size();
//    }

    private String exceptionMessage(int index, String extra) {
        final StringBuilder buffer = new StringBuilder("Cannot read " + fileName(index));
        if (extra != null) {
            buffer.append("; ").append(extra);
        }
        buffer.append("\n");
        try {
            final File dir = new File(mDirectoryName);
            buffer.append("ts = ").append(System.currentTimeMillis()).append("\n");
            buffer.append("dir exists = ").append(dir.exists()).append("\n");
            for (File f : dir.listFiles()) {
                buffer.append(f.getName()).append(" :: ");
                buffer.append(f.length()).append(" :: ");
                buffer.append(f.lastModified()).append("\n");
            }
        } catch (Throwable t) {
            buffer.append(t.getClass().getName());
            buffer.append("\n");
            buffer.append(t.getMessage());
        }
        return buffer.toString();
    }

    /**
     * char数据持久化
     *
     * @param index
     * @return
     */
    public char[] block(int index) {
        if (index < 0 || index >= mArray.size()) {
            return null;
        }
        char[] block = mArray.get(index).get();
        if (block == null) {
            try {
                File file = new File(fileName(index));
                int size = (int) file.length();
                if (size < 0) {
                    throw new CachedCharStorageException(exceptionMessage(index, "size = " + size));
                }
                block = new char[size / 2];
                InputStreamReader reader =
                        new InputStreamReader(
                                new FileInputStream(file),
                                "UTF-16LE" // "UTF-16LE"
                        );
                final int rd = reader.read(block);
                if (rd != block.length) {
                    throw new CachedCharStorageException(exceptionMessage(index, "; " + rd + " != " + block.length));
                }
                reader.close();
            } catch (IOException e) {
                throw new CachedCharStorageException(exceptionMessage(index, null), e);
            }
            mArray.set(index, new WeakReference<>(block));
        }
        return block;
    }
}
