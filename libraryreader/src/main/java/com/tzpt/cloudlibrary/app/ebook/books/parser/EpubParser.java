package com.tzpt.cloudlibrary.app.ebook.books.parser;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;

import com.tzpt.cloudlibrary.app.ebook.books.controller.EpubReaderController;
import com.tzpt.cloudlibrary.app.ebook.books.model.Book;
import com.tzpt.cloudlibrary.app.ebook.books.model.Chapter;
import com.tzpt.cloudlibrary.app.ebook.utils.FileDES;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * epub 解析工具
 * 1.依据OCF规范，META-INF用于存放容器信息，默认情况下（即加密处理），该目录包含一个文件，即container.xml
 * 2.(备注)OEBPS目录用于存放OPS文档、OPF文档、CSS文档、NCX文档
 * 3.OPF文档是epub电子书的核心文件，且是一个标准的XML文件，依据OPF规范
 * 4.ncx，脊骨，其主要功能是提供书籍的线性阅读次序
 */
public class EpubParser implements IParser {
    private static final String TEMP_FOLDER_NAME = "temp";
    private static final int BUFFER_SIZE = 1024 * 8;
    private static final int CHAR_8K = 1024 * 8;
    private static final String META_INF_CONTAINER = "META-INF/container.xml";

    /**
     * book metadata 元数据
     */
    public class Metadata {
        public String title;        //书名
        public String creator;      //作者
        public String bookId;
        public String publisher;
        public String date;         //日期
        public String ISBN;
        public String language;
        public String cover;
        public String rights;       //权限描述
    }

    /**
     * 文件列表
     */
    public class Manifest {
        public String id;           //表示文件的ID号
        public String href;         //文件的相对路径
        public String media_type;   //文件的媒体类型
    }

    /**
     * 阅读章节
     */
    public static class NavPoint {
        public String id;           //id
        public String playOrder;    //阅读顺序
        public long navLevel;       //章节层级
        public String navLabel;     //章节标题
        public String src;          //文件相对路径
        public String anchor;       //锚
        public int chapterIndex = 0;
        public int pageIndex = 0;
    }

    /**
     * <spine toc="ncx">，脊骨，其主要功能是提供书籍的线性阅读次序
     */
    public class Spine {
        public String idref;            //即参照menifest列出的ID
        public boolean linear;
        public ArrayList<NavPoint> navcontent;
    }

    /**
     * <guide>,指南,依次列出电子书的特定页面, 例如封面、目录、序言等, 属性值指向文件保存地址。一般情况下，epub电子书可以不用该元素。
     */
    public class Guide {
        public String type;     //类型
        public String title;    //标题
        public String href;     //链接
    }

    private class NavPointSort implements Comparator<NavPoint> {
        @Override
        public int compare(NavPoint lhs, NavPoint rhs) {
            return lhs.playOrder.compareTo(rhs.playOrder);
        }
    }

    private boolean mSupportEncryption = false;
    private Metadata mMetadata;
    private HashMap<String, Manifest> mManifest;
    private ArrayList<NavPoint> mNavMap;
    private ArrayList<NavPoint> mSpineToc;
    private ArrayList<Spine> mSpine;
    private ArrayList<Guide> mGuide;
    private ArrayList<String> mCssFiles;
    private Context mContext;
    private ArrayList<String> mCacheFolder;
    private String mOpfFilePath;
    private String mNcxFilePath;
    private String mOpfRelativePath;
    private String mSavePath;
    private String unZipSaveFolderPath;       //解压的存放地址
    private String mResourceRelativePath;

    public EpubParser(Context context, final String epubFile) {
        this(context, epubFile, null, null, null);
    }

    /**
     * Parser Epub file 解析epub文件
     *
     * @param context       上下文
     * @param epubFile      文件路径
     * @param encryptionKey 加密密钥
     * @param random        随机数
     */
    public EpubParser(Context context, final String epubFile, String encryptionKey, String random, String unZipSavePath) {
        mContext = context;
        this.unZipSaveFolderPath = unZipSavePath;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            mSavePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + TEMP_FOLDER_NAME;
        else
            mSavePath = mContext.getFilesDir().getAbsolutePath() + File.separator + TEMP_FOLDER_NAME;

        //如果文件夹存在，则获取解析内容
        File file = new File(epubFile);
        if (file.isDirectory() && file.exists() && file.canRead()) {
            try {
                mMetadata = new Metadata();
                mManifest = new HashMap<String, Manifest>();
                mNavMap = new ArrayList<NavPoint>();
                mSpineToc = new ArrayList<NavPoint>();
                mCssFiles = new ArrayList<String>();
                mSpine = new ArrayList<Spine>();
                mGuide = new ArrayList<Guide>();
                parser();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭电子书
     */
    public void close() {
        if (mMetadata != null)
            mMetadata = null;

        if (mManifest != null) {
            mManifest.clear();
            mManifest = null;
        }

        if (mNavMap != null) {
            mNavMap.clear();
            mNavMap = null;
        }

        if (mSpineToc != null) {
            mSpineToc.clear();
            mSpineToc = null;
        }

        if (mCssFiles != null) {
            mCssFiles.clear();
            mCssFiles = null;
        }

        if (mSpine != null) {
            mSpine.clear();
            mSpine = null;
        }

        if (mGuide != null) {
            mGuide.clear();
            mGuide = null;
        }

        try {
            if (mCacheFolder != null) {
                for (String folder : mCacheFolder) {
                    File file = new File(folder);
                    DeleteRecursive(file);
                }
                mCacheFolder.clear();
                mCacheFolder = null;
            }
        } catch (Exception e) {
            if (null != mCacheFolder) {
                mCacheFolder.clear();
                mCacheFolder = null;
            }
        }
    }

    private void DeleteRecursive(File fileOrDirectory) {
        if (null != fileOrDirectory && fileOrDirectory.exists() && fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                if (child.exists()) {
                    DeleteRecursive(child);
                }
            }
        }
        if (null != fileOrDirectory && fileOrDirectory.exists()) {
            fileOrDirectory.delete();
        }

    }

    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    public Metadata getBookInfo() {
        return mMetadata;
    }

    public ArrayList<NavPoint> getNavMap() {
        return mNavMap;
    }

    /**
     * 获取本地文件路径
     *
     * @param entryName
     * @return
     */
    @Override
    public String getFileLocalPath(String entryName) {
        String relativePath = mResourceRelativePath;

        if (entryName.startsWith("../") && !TextUtils.isEmpty(relativePath)) {
            if (relativePath.endsWith("/"))
                relativePath = relativePath.substring(0, relativePath.length() - 1);

            if (relativePath.lastIndexOf("/") != -1)
                relativePath = relativePath.substring(0, relativePath.lastIndexOf("/") + 1);
            else
                relativePath = "";
        }
        if (entryName.startsWith("../"))
            entryName = entryName.substring(3);
        String path = mSavePath + File.separator + (TextUtils.isEmpty(mOpfRelativePath) ? "" : mOpfRelativePath)
                + (TextUtils.isEmpty(relativePath) ? "" : relativePath) + entryName;
        File file = new File(path);
        if (file.isFile() && file.exists()) {
            if (mCacheFolder == null)
                mCacheFolder = new ArrayList<String>();
            if (!mCacheFolder.contains(file.getParentFile().getAbsolutePath()))
                mCacheFolder.add(file.getParentFile().getAbsolutePath());
            return path;
        }

        String filepath = this.unZipSaveFolderPath + "/" + (TextUtils.isEmpty(mOpfRelativePath) ? "" : mOpfRelativePath)
                + (TextUtils.isEmpty(relativePath) ? "" : relativePath) + entryName;
        File fileLast = new File(filepath);
        if (fileLast.isFile() && fileLast.exists()) {
            try {
                if (mCacheFolder == null)
                    mCacheFolder = new ArrayList<String>();
                file.getParentFile().mkdirs();
                if (!mCacheFolder.contains(file.getParentFile().getAbsolutePath()))
                    mCacheFolder.add(file.getParentFile().getAbsolutePath());
                InputStream is = new FileInputStream(fileLast);
                FileOutputStream fo = null;
                try {
                    fo = new FileOutputStream(path);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int read = 0;
                    while ((read = is.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        fo.write(buffer, 0, read);
                    }
                    fo.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                } finally {
                    try {
                        if (is != null)
                            is.close();
                        if (fo != null)
                            fo.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    public ArrayList<Spine> getSpine() {
        return mSpine;
    }

    public String getSpineItemRef(Spine spine) {
        return mManifest.get(spine.idref).href;
    }

    /**
     * 解析资源流
     *
     * @param entryName
     * @return
     */
    public Pair<? extends InputStream, Long> getFileStream(String entryName) {
        //如果ico里面有favicon.ico 则返回默认图标
        InputStream is = null;
        BufferedInputStream bis = null;
        long streamSize = 0;
        ByteArrayOutputStream baos = null;
        String relativePath = mResourceRelativePath;
        if (entryName.startsWith("../") && !TextUtils.isEmpty(relativePath)) {
            if (relativePath.endsWith("/"))
                relativePath = relativePath.substring(0, relativePath.length() - 1);
            if (relativePath.lastIndexOf("/") != -1)
                relativePath = relativePath.substring(0, relativePath.lastIndexOf("/"));
            else
                relativePath = "";
        }
        if (entryName.startsWith("../")) {
            entryName = entryName.substring(3);
        }
        try {
            String mResourceRelativeEntryPath = EpubReaderController.absolutePath + "/" + (TextUtils.isEmpty(mOpfRelativePath) ? "" : mOpfRelativePath) + (TextUtils.isEmpty(relativePath) ? "" : relativePath) + entryName;
            File file = new File(mResourceRelativeEntryPath);
            //如果文件不存在则修改路径
            if (!file.exists()) {
                mResourceRelativeEntryPath = EpubReaderController.absolutePath + "/" + (TextUtils.isEmpty(mOpfRelativePath) ? "" : mOpfRelativePath) + entryName;
                file = new File(mResourceRelativeEntryPath);
            }
            if (file.exists() && file.isFile()) {
                is = new FileInputStream(file);
                if (mSupportEncryption) {
                    baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[BUFFER_SIZE];
                    do {
                        int count = is.read(buffer, 0, BUFFER_SIZE);
                        if (count < 0)
                            break;
                        baos.write(buffer, 0, count);
                    } while (true);
                    baos.flush();
                    is.close();
                    // Get the raw body as a byte []
                    byte[] fbuf = baos.toByteArray();
                    // Create a BufferedReader for easily reading it as
                    // string.
                    is = new ByteArrayInputStream(fbuf, 0, fbuf.length);
                    streamSize = fbuf.length;
                } else {
                    if (file.length() != is.available()) {
                        bis = new BufferedInputStream(is, CHAR_8K);
                        baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int count;
                        while ((count = bis.read(buffer)) != -1) {
                            baos.write(buffer, 0, count);
                        }
                        baos.flush();
                        is.close();
                        // Get the raw body as a byte []
                        byte[] fbuf = baos.toByteArray();
                        // Create a BufferedReader for easily reading it as
                        // string.
                        is = new ByteArrayInputStream(fbuf, 0, fbuf.length);
                    }
                    streamSize = file.length();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null)
                    bis.close();
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (is != null && streamSize > 0)
            return Pair.create(is, streamSize);
        else
            return Pair.create(null, Long.valueOf(0));
    }

    /**
     * 获取文章内容大小
     *
     * @param entryName 文件名称
     * @return 文章内容大小
     */
    public long getChapterContentSize(String entryName) {
        for (Spine spine : mSpine) {
            String src = mManifest.get(spine.idref).href;
            if (src.equals(entryName) && null != EpubReaderController.absolutePath) {
                File file = new File(EpubReaderController.absolutePath + "/" + entryName);
                if (file.exists() && file.isFile()) {
                    return file.length();
                }
                break;
            }
        }
        return 0;
    }

    /**
     * 获取文章内容
     *
     * @param entryName 文件名称
     * @return epub 文章内容
     */
    public String getChapterContent(String entryName) {
        String content = "";
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        for (Spine spine : mSpine) {
            String src = mManifest.get(spine.idref).href;
            if (src.equals(entryName)) {
                try {
                    mResourceRelativePath = (entryName.lastIndexOf("/") != -1)
                            ? (entryName.substring(0, entryName.lastIndexOf("/") + 1)) : "";
                    String mOpfEntryPath = this.unZipSaveFolderPath + "/" + mOpfRelativePath + entryName;
                    File file = new File(mOpfEntryPath);
                    if (file.isFile() && file.exists()) {
                        is = new FileInputStream(file);
                        if (mSupportEncryption) {
                            byte[] buffer = new byte[BUFFER_SIZE];
                            baos = new ByteArrayOutputStream();
                            do {
                                int read = is.read(buffer, 0, BUFFER_SIZE);
                                if (read < 0)
                                    break;
                                baos.write(buffer, 0, read);
                            } while (true);
                            content = new String(baos.toByteArray(), "UTF-8");
                        } else {
                            BufferedInputStream bin = new BufferedInputStream(is, CHAR_8K);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(bin, "UTF-8"), CHAR_8K);

                            char[] buffer = new char[(int) file.length()];
                            int size = reader.read(buffer);

                            reader.close();
                            bin.close();
                            content = new String(buffer, 0, size);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null)
                            is.close();

                        if (baos != null)
                            baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
        return content;
    }

    /**
     * 解析epub内部文件
     *
     * @throws Exception
     */
    private void parser() throws Exception {
        //1.find the container.xml and find the opf file path
        String container = this.unZipSaveFolderPath + "/" + META_INF_CONTAINER;
        InputStream inputStream = null;
        InputStream fileInput = null;
        FileDES fileDES = new FileDES("");
        if (new File(container).exists()) {
            try {
                fileInput = new FileInputStream(new File(container));
                inputStream = fileDES.doDecryptFile(fileInput);
                //解析Container文件内容
                parserMETA_INF_Container(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileInput != null) {
                        fileInput.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 2. parser opf file
        try {
            String mOpfFileAbsolutePath = this.unZipSaveFolderPath + "/" + mOpfFilePath;
            fileInput = new FileInputStream(new File(mOpfFileAbsolutePath));
            inputStream = fileDES.doDecryptFile(fileInput);
            //解析opf文件
            parserOpf(inputStream);
        } catch (Exception e) {

        } finally {
            try {
                if (fileInput != null) {
                    fileInput.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 3. parser ncx file
        String mNcxFileAbsolurePath = this.unZipSaveFolderPath + "/" + mNcxFilePath;
        File ncxFile = new File(mNcxFileAbsolurePath);
        if (ncxFile.exists() && ncxFile.getName().endsWith(".ncx")) {
            try {
                fileInput = new FileInputStream(ncxFile);
                inputStream = fileDES.doDecryptFile(fileInput);
                //解析ncx文件
                parserNcx(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileInput != null) {
                        fileInput.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (mNavMap != null && mSpine != null && !mSpine.isEmpty()) {
            int nSpineLen = mSpine.size();
            for (int index = 0; index < nSpineLen; index++) {
                Spine spine = mSpine.get(index);
                String herf = mManifest.get(spine.idref).href;
                int nNavMapLen = mNavMap.size();
                for (int i = 0; i < nNavMapLen; i++) {
                    NavPoint nav = mNavMap.get(i);
                    if (nav.src.equals(herf)) {
                        nav.chapterIndex = index;
                        if (spine.navcontent == null)
                            spine.navcontent = new ArrayList<NavPoint>();
                        spine.navcontent.add(nav);
                    }
                }
            }
        }

    }

    public ArrayList<String> getCssFiles() {
        return mCssFiles;
    }

    /**
     * 解析ncx文件
     *
     * @param is
     */
    private void parserNcx(InputStream is) {
        if (is == null)
            return;
        InputStreamReader reader = null;
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            NcxSAXParser saxParser = new NcxSAXParser(mNavMap);
            xr.setContentHandler(saxParser);
            reader = new InputStreamReader(is);
            xr.parse(new InputSource(reader));

            // if (mNavMap.size() > 0) {
            // NavPointSort sort = new NavPointSort();
            // Collections.sort(mNavMap, sort);
            // }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
            } catch (Exception e) {
            }

        }
    }

    /**
     * 解析META_INF下的Container文件
     *
     * @param is
     */
    private void parserMETA_INF_Container(InputStream is) {
        if (is == null)
            return;
        // XmlPullParser
        BufferedInputStream bin = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            bin = new BufferedInputStream(is, CHAR_8K);
            bin.mark(3);
            byte[] bom = new byte[3];
            int length = bin.read(bom);
            if (length == 3 && (bom[0] & 0xff) == 0xEF && (bom[1] & 0xff) == 0xBB && (bom[2] & 0xff) == 0xBF) {
                // 有BOM�?
            } else
                bin.reset();
            xpp.setInput(bin, "UTF-8");
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equalsIgnoreCase("rootfile")) {
                            for (int index = 0; index < xpp.getAttributeCount(); ++index) {
                                if (xpp.getAttributeName(index).equalsIgnoreCase("full-path")) {
                                    mOpfRelativePath = xpp.getAttributeValue(index);
                                    mOpfFilePath = mOpfRelativePath;//opf 的相对路径
                                    mOpfRelativePath = mOpfRelativePath.substring(0, mOpfRelativePath.lastIndexOf("/") + 1);
                                    break;
                                }
                            }
                            break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        break;
                }
                eventType = xpp.next();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bin != null)
                    bin.close();
                bin = null;
            } catch (IOException e) {
            }
        }
    }

    /**
     * 解析opf文件
     *
     * @param is
     */
    private void parserOpf(InputStream is) {
        if (is == null)
            return;
        boolean enterMetadata = false;
        boolean enterManifest = false;
        boolean enterSpine = false;
        boolean enterGuide = false;
        String tagName = "";
        String tagId = "";
        String opf_role = "";
        String opf_event = "";
        int linearIndex = 0;
        BufferedInputStream bin = null;

        // XmlPullParser
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            bin = new BufferedInputStream(is, CHAR_8K);

            bin.mark(3);
            byte[] bom = new byte[3];
            int length = bin.read(bom);

            if (length == 3 && (bom[0] & 0xff) == 0xEF && (bom[1] & 0xff) == 0xBB && (bom[2] & 0xff) == 0xBF) {
                // 有BOM�?
            } else
                bin.reset();

            xpp.setInput(bin, "UTF-8");
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        tagName = xpp.getName();
                        if (tagName.equalsIgnoreCase("metadata"))
                            enterMetadata = true;
                        else if (tagName.equalsIgnoreCase("manifest"))
                            enterManifest = true;
                        else if (tagName.equalsIgnoreCase("spine"))
                            enterSpine = true;
                        else if (tagName.equalsIgnoreCase("guide"))
                            enterGuide = true;

                        if (enterMetadata) {
                            tagId = xpp.getAttributeValue(null, "id");
                            opf_role = xpp.getAttributeValue(null, "opf:role");
                            opf_event = xpp.getAttributeValue(null, "opf:event");

                            if (tagName.equalsIgnoreCase("meta")) {
                                String name = xpp.getAttributeValue(null, "name");
                                String content = xpp.getAttributeValue(null, "content");
                                if (name != null && name.equalsIgnoreCase("cover"))
                                    mMetadata.cover = content;
                            }
                        } else if (enterManifest) {
                            if (tagName.equalsIgnoreCase("item")) {
                                Manifest item = new Manifest();
                                item.id = xpp.getAttributeValue(null, "id");
                                item.href = xpp.getAttributeValue(null, "href");
                                item.media_type = xpp.getAttributeValue(null, "media-type");

                                if (!TextUtils.isEmpty(mMetadata.cover) && mMetadata.cover.equalsIgnoreCase(item.id))
                                    mMetadata.cover = item.href;

                                mManifest.put(item.id, item);

                                if (item.id.equalsIgnoreCase("ncx"))
                                    mNcxFilePath = (TextUtils.isEmpty(mOpfRelativePath) ? "" : mOpfRelativePath)
                                            + item.href;
                            }
                        } else if (enterSpine) {
                            if (tagName.equalsIgnoreCase("itemref")) {
                                String linear = xpp.getAttributeValue(null, "linear");

                                Spine item = new Spine();
                                item.idref = xpp.getAttributeValue(null, "idref");
                                item.linear = (linear == null) ? true : linear.equalsIgnoreCase("yes") ? true : false;
                                if (!item.linear)
                                    mSpine.add(item);
                                else
                                    mSpine.add(linearIndex++, item);
                            }
                        } else if (enterGuide) {
                            if (tagName.equalsIgnoreCase("reference")) {
                                Guide item = new Guide();
                                item.type = xpp.getAttributeValue(null, "type");
                                item.title = xpp.getAttributeValue(null, "title");
                                item.href = xpp.getAttributeValue(null, "href");

                                mGuide.add(item);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tagName = xpp.getName();
                        if (tagName.equalsIgnoreCase("metadata"))
                            enterMetadata = false;
                        else if (tagName.equalsIgnoreCase("manifest"))
                            enterManifest = false;
                        else if (tagName.equalsIgnoreCase("spine"))
                            enterSpine = false;
                        else if (tagName.equalsIgnoreCase("guide"))
                            enterGuide = false;

                        break;
                    case XmlPullParser.TEXT:
                        String text = xpp.getText().trim();
                        if (!TextUtils.isEmpty(text) && enterMetadata) {
                            if (tagName.equalsIgnoreCase("dc:title") || tagName.equalsIgnoreCase("title"))
                                mMetadata.title = text;
                            else if (tagName.equalsIgnoreCase("dc:creator") || tagName.equalsIgnoreCase("creator"))
                                mMetadata.creator = text;
                            else if (tagName.equalsIgnoreCase("dc:publisher") || tagName.equalsIgnoreCase("publisher"))
                                mMetadata.publisher = text;
                            else if (tagName.equalsIgnoreCase("dc:date") || tagName.equalsIgnoreCase("date"))
                                if (!TextUtils.isEmpty(opf_event) && opf_event.equalsIgnoreCase("epub-publication")) {
                                    mMetadata.date = text;
                                } else
                                    mMetadata.date = text;
                            else if (tagName.equalsIgnoreCase("dc:language") || tagName.equalsIgnoreCase("language"))
                                mMetadata.language = text;
                            else if (tagName.equalsIgnoreCase("dc:rights") || tagName.equalsIgnoreCase("rights"))
                                mMetadata.rights = text;
                            else if (tagName.equalsIgnoreCase("dc:identifier") || tagName.equalsIgnoreCase("identifier")) {
                                if (tagId != null) {
                                    if (tagId.equalsIgnoreCase("bookid"))
                                        mMetadata.bookId = text;
                                    else if (tagId.equalsIgnoreCase("ISBN"))
                                        mMetadata.ISBN = text;
                                }
                            }
                        }
                        break;
                }
                eventType = xpp.next();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bin != null)
                    bin.close();
                bin = null;
            } catch (IOException e) {
            }
        }
    }

    /**
     * 构造文章列表
     *
     * @param chapterList 文章列表
     * @param book        图书信息
     */
    @Override
    public void constructChapterList(SparseArray<Chapter> chapterList, Book book) {
        ArrayList<Spine> spinearray = getSpine();
        if (spinearray != null) {
            int index = 0;
            for (Spine spine : spinearray) {
                Chapter chapter = new Chapter();
                if (spine.navcontent == null) {
                    chapter.index = index;
                    chapter.src = getSpineItemRef(spine);
                    chapter.previousChapterFileSize = book.fileSize;
                    chapter.fileSize = getChapterContentSize(chapter.src);
                    book.fileSize += chapter.fileSize;
                    chapterList.put(index++, chapter);
                    continue;
                }
                NavPoint firstNav = spine.navcontent.get(0);
                chapter.index = index;
                chapter.id = firstNav.id;
                chapter.src = firstNav.src;
                chapter.anchor = firstNav.anchor;
                chapter.title = firstNav.navLabel;
                chapter.previousChapterFileSize = book.fileSize;
                chapter.fileSize = getChapterContentSize(firstNav.src);
                book.fileSize += chapter.fileSize;
                chapterList.put(index++, chapter);
            }
        }
    }

    /**
     * 获取解析类型
     *
     * @return
     */
    @Override
    public ParserType getParserType() {
        return IParser.ParserType.Epub;
    }

}
