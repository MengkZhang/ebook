package com.amse.ys.zip;

import com.tzpt.cloudlibrary.zlibrary.core.util.InputStreamHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

/**
 * Created by Administrator on 2017/4/7.
 */

public final class ZipFile {
    private final static Comparator<String> ourIgnoreCaseComparator = new Comparator<String>() {
        @Override
        public final int compare(String s0, String s1) {
            return s0.compareToIgnoreCase(s1);
        }
    };

    private final InputStreamHolder mStreamHolder;
    private final Map<String, LocalFileHeader> mFileHeaders =
            new TreeMap<String, LocalFileHeader>(ourIgnoreCaseComparator);

    private boolean myAllFilesAreRead;

    public ZipFile(final String fileName) {
        this(new InputStreamHolder() {
            public InputStream getInputStream() throws IOException {
                return new FileInputStream(fileName);
            }
        });
    }

    public ZipFile(final File file) {
        this(new InputStreamHolder() {
            public InputStream getInputStream() throws IOException {
                return new FileInputStream(file);
            }
        });
    }

    public ZipFile(InputStreamHolder streamHolder) {
        mStreamHolder = streamHolder;
    }

    public Collection<LocalFileHeader> headers() {
        try {
            readAllHeaders();
        } catch (IOException e) {
        }
        return mFileHeaders.values();
    }

    private boolean readFileHeader(MyBufferedInputStream baseStream, String fileToFind) throws IOException { // 图片调用
        LocalFileHeader header = new LocalFileHeader();
        header.readFrom(baseStream); // LocalFileHeader中

        if (header.Signature != LocalFileHeader.FILE_HEADER_SIGNATURE) {
            return false;
        }
        if (header.FileName != null) {
            // 创建出的LocalFileHeader被加入到myFileHeaders
            mFileHeaders.put(header.FileName, header);
            if (header.FileName.equalsIgnoreCase(fileToFind)) {
                return true;
            }
        }
        if ((header.Flags & 0x08) == 0) {
            baseStream.skip(header.CompressedSize);
        } else {
            findAndReadDescriptor(baseStream, header);
        }
        return false;
    }

    private void readAllHeaders() throws IOException {

        if (myAllFilesAreRead) {
            return;
        }
        myAllFilesAreRead = true;

        MyBufferedInputStream baseStream = getBaseStream();
        baseStream.setPosition(0);
        mFileHeaders.clear();

        try {
            while (baseStream.available() > 0) {
                readFileHeader(baseStream, null);
            }
        } finally {
            storeBaseStream(baseStream);
        }
    }

    /**
     * Finds descriptor of the last header and installs sizes of files
     */
    private void findAndReadDescriptor(MyBufferedInputStream baseStream, LocalFileHeader header) throws IOException {

        final Decompressor decompressor = Decompressor.init(baseStream, header);
        int uncompressedSize = 0;
        while (true) {
            int blockSize = decompressor.read(null, 0, 2048);
            if (blockSize <= 0) {
                break;
            }
            uncompressedSize += blockSize;
        }
        header.UncompressedSize = uncompressedSize;
        Decompressor.storeDecompressor(decompressor);
    }

    private final Queue<MyBufferedInputStream> myStoredStreams = new LinkedList<MyBufferedInputStream>();

    synchronized void storeBaseStream(MyBufferedInputStream baseStream) {
        myStoredStreams.add(baseStream);
    }

    synchronized MyBufferedInputStream getBaseStream() throws IOException {
        final MyBufferedInputStream stored = myStoredStreams.poll();
        if (stored != null) {
            return stored;
        }
        return new MyBufferedInputStream(mStreamHolder);
    }

    private ZipInputStream createZipInputStream(LocalFileHeader header) throws IOException {
        return new ZipInputStream(this, header);
    }

    public boolean entryExists(String entryName) {
        try {
            return getHeader(entryName) != null;
        } catch (IOException e) {
            return false;
        }
    }

    public int getEntrySize(String entryName) throws IOException {

        return getHeader(entryName).UncompressedSize;
    }

    public InputStream getInputStream(String entryName) throws IOException {
        return createZipInputStream(getHeader(entryName)); // 取出对应的LocalFileHeader类之后
        // 以此为参数调用ZipFile类的createZipInputStream方法。这个方法带又调用了一个ZipInputStream类的构造函数
    }

    public LocalFileHeader getHeader(String entryName) throws IOException {
        if (!mFileHeaders.isEmpty()) { // 不走
            LocalFileHeader header = mFileHeaders.get(entryName);
            if (header != null) {
                return header;
            }
            if (myAllFilesAreRead) {
                throw new ZipException("Entry " + entryName + " is not found");
            }
        }
        // 每次打开图片时调用
        // 开始读取图片头信息
        // ready to read file header
        MyBufferedInputStream baseStream = getBaseStream();
        baseStream.setPosition(0);
        try {
            while (baseStream.available() > 0 && !readFileHeader(baseStream, entryName)) {
            }
            final LocalFileHeader header = mFileHeaders.get(entryName);
            if (header != null) {
//                // 包含图片压缩前及压缩后的大小
//                LogUtil.i21(header.FileName + ""); // OPS/images/Copyright.jpg
//                LogUtil.i21(header.NameLength + ""); // 24
//                LogUtil.i21(header.ExtraLength + ""); // 0
//                LogUtil.i21(header.CRC32 + ""); // 1197082208
//                LogUtil.i21(header.ModificationDate + ""); // 16154
//                LogUtil.i21(header.CompressedSize + ""); // 22080
//                LogUtil.i21(header.UncompressedSize + ""); // 38206
//                LogUtil.i21(header.DataOffset + ""); // 910909
                return header;
            }
        } finally {
            storeBaseStream(baseStream);
        }
        throw new ZipException("Entry " + entryName + " is not found");
    }
}
