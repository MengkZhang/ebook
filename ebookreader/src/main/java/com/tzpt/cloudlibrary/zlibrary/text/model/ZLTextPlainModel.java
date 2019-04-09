package com.tzpt.cloudlibrary.zlibrary.text.model;

import com.tzpt.cloudlibrary.zlibrary.core.image.ZLImage;
import com.tzpt.cloudlibrary.zlibrary.text.view.ZLTextOtherStyleEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * 文本数据模型
 * Created by Administrator on 2017/4/9.
 */

public final class ZLTextPlainModel implements ZLTextModel, ZLTextStyleEntry.Feature {
    private final String mId;
    private final String mLanguage;

    private int[] mStartEntryIndices;//每个段落所在CachedCharStorage文件索引值数组
    private int[] mStartEntryOffsets;//每个段落所在CachedCharStorage偏移量数据组
    private int[] mParagraphLengths;//每个段落的长度数组 TODO 有疑问？？
    private int[] mTextSizes;       //每个段落文字的大小数组 TODO 有疑问？？
    private byte[] mParagraphKinds;//每个段落的类型

    private int mParagraphsNumber; //段落数量

    private final CachedCharStorage mStorage;
    private final Map<String, ZLImage> mImageMap;

//    private final FontManager mFontManager;

    final class EntryIteratorImpl implements ZLTextParagraph.EntryIterator {
        private int mCounter;
        private int mLength;
        private byte mType;

        int mDataIndex;//段落所在CachedCharStorage文件的索引值
        int mDataOffset;//段落所在CachedCharStorage偏移量

        // TextEntry data
        private char[] mTextData;
        private int mTextOffset;
        private int mTextLength;

        // ControlEntry data
        private byte mControlKind;
        private boolean mControlIsStart;
        // HyperlinkControlEntry data
        private byte mHyperlinkType;
        //跳转的ID
        private String mHyperlinkId;

        // ImageEntry
        private ZLImageEntry mImageEntry;

        // VideoEntry
        private ZLVideoEntry mVideoEntry;

        // ExtensionEntry
        private ExtensionEntry mExtensionEntry;

        // StyleEntry
        private ZLTextStyleEntry mStyleEntry;

        // FixedHSpaceEntry data
        private short mFixedHSpaceLength;

        EntryIteratorImpl(int index) {
            reset(index);
        }

        private void reset(int index) {
            mCounter = 0;
            mLength = mParagraphLengths[index];
            mDataIndex = mStartEntryIndices[index];
            mDataOffset = mStartEntryOffsets[index];
        }

        public byte getType() {
            return mType;
        }

        public char[] getTextData() {
            return mTextData;
        }

        public int getTextOffset() {
            return mTextOffset;
        }

        public int getTextLength() {
            return mTextLength;
        }

        public byte getControlKind() {
            return mControlKind;
        }

        public boolean getControlIsStart() {
            return mControlIsStart;
        }

//        public byte getHyperlinkType() {
//            return mHyperlinkType;
//        }
//
//        public String getHyperlinkId() {
//            return mHyperlinkId;
//        }

        public ZLImageEntry getImageEntry() {
            return mImageEntry;
        }

//        public ZLVideoEntry getVideoEntry() {
//            return mVideoEntry;
//        }
//
//        public ExtensionEntry getExtensionEntry() {
//            return mExtensionEntry;
//        }
//
//        public ZLTextStyleEntry getStyleEntry() {
//            return mStyleEntry;
//        }
//
//        public short getFixedHSpaceLength() {
//            return mFixedHSpaceLength;
//        }

        public boolean next() {
            if (mCounter >= mLength) {
                return false;
            }

            int dataOffset = mDataOffset;
            char[] data = mStorage.block(mDataIndex);
            if (data == null) {
                return false;
            }
            if (dataOffset >= data.length) {
                data = mStorage.block(++mDataIndex);
                if (data == null) {
                    return false;
                }
                dataOffset = 0;
            }
            short first = (short) data[dataOffset];
            byte type = (byte) first;
            if (type == 0) {
                data = mStorage.block(++mDataIndex);
                if (data == null) {
                    return false;
                }
                dataOffset = 0;
                first = (short) data[0];
                type = (byte) first;
            }
            mType = type;
            ++dataOffset;
            switch (type) {
                case ZLTextParagraph.Entry.TEXT: {
                    int textLength = (int) data[dataOffset++];
                    textLength += (((int) data[dataOffset++]) << 16);
                    textLength = Math.min(textLength, data.length - dataOffset);
                    mTextLength = textLength;
                    mTextData = data;
                    mTextOffset = dataOffset;
                    dataOffset += textLength;
                    break;
                }
                case ZLTextParagraph.Entry.CONTROL: {
                    short kind = (short) data[dataOffset++];
                    mControlKind = (byte) kind;
                    mControlIsStart = (kind & 0x0100) == 0x0100;
                    mHyperlinkType = 0;
                    break;
                }
                case ZLTextParagraph.Entry.HYPERLINK_CONTROL: {
                    final short kind = (short) data[dataOffset++];
                    mControlKind = (byte) kind;
                    mControlIsStart = true;
                    mHyperlinkType = (byte) (kind >> 8);
                    final short labelLength = (short) data[dataOffset++];
                    mHyperlinkId = new String(data, dataOffset, labelLength);
                    dataOffset += labelLength;
                    break;
                }
                case ZLTextParagraph.Entry.IMAGE: {
                    final short vOffset = (short) data[dataOffset++];//没有图片不显示？？
                    final short len = (short) data[dataOffset++];
                    final String id = new String(data, dataOffset, len);
                    dataOffset += len;
                    final boolean isCover = data[dataOffset++] != 0;
                    mImageEntry = new ZLImageEntry(mImageMap, id, isCover);
                    break;
                }
                case ZLTextParagraph.Entry.FIXED_HSPACE:
                    mFixedHSpaceLength = (short) data[dataOffset++];
                    break;
                case ZLTextParagraph.Entry.STYLE_CSS:
                case ZLTextParagraph.Entry.STYLE_OTHER: {
                    final short depth = (short) ((first >> 8) & 0xFF);
                    final ZLTextStyleEntry entry = new ZLTextOtherStyleEntry();

                    final short mask = (short) data[dataOffset++];
                    for (int i = 0; i < NUMBER_OF_LENGTHS; ++i) {
                        if (ZLTextStyleEntry.isFeatureSupported(mask, i)) {
                            final short size = (short) data[dataOffset++];
                            final byte unit = (byte) data[dataOffset++];
                            entry.setLength(i, size, unit);
                        }
                    }
                    if (ZLTextStyleEntry.isFeatureSupported(mask, ALIGNMENT_TYPE) ||
                            ZLTextStyleEntry.isFeatureSupported(mask, NON_LENGTH_VERTICAL_ALIGN)) {
                        final short value = (short) data[dataOffset++];
                        if (ZLTextStyleEntry.isFeatureSupported(mask, ALIGNMENT_TYPE)) {
                            entry.setAlignmentType((byte) (value & 0xFF));
                        }
                        if (ZLTextStyleEntry.isFeatureSupported(mask, NON_LENGTH_VERTICAL_ALIGN)) {
                            entry.setVerticalAlignCode((byte) ((value >> 8) & 0xFF));
                        }
                    }
                    if (ZLTextStyleEntry.isFeatureSupported(mask, FONT_FAMILY)) {
                        dataOffset++;
                        //entry.setFontFamilies(mFontManager, (short) data[dataOffset++]);
                    }
                    if (ZLTextStyleEntry.isFeatureSupported(mask, FONT_STYLE_MODIFIER)) {
                        final short value = (short) data[dataOffset++];
                        entry.setFontModifiers((byte) (value & 0xFF), (byte) ((value >> 8) & 0xFF));
                    }

                    mStyleEntry = entry;
                }
                case ZLTextParagraph.Entry.STYLE_CLOSE:
                    // No data
                    break;
                case ZLTextParagraph.Entry.RESET_BIDI:
                    // No data
                    break;
                case ZLTextParagraph.Entry.AUDIO:
                    // No data
                    break;
                case ZLTextParagraph.Entry.VIDEO: {
                    mVideoEntry = new ZLVideoEntry();
                    final short mapSize = (short) data[dataOffset++];
                    for (short i = 0; i < mapSize; ++i) {
                        short len = (short) data[dataOffset++];
                        final String mime = new String(data, dataOffset, len);
                        dataOffset += len;
                        len = (short) data[dataOffset++];
                        final String src = new String(data, dataOffset, len);
                        dataOffset += len;
                        mVideoEntry.addSource(mime, src);
                    }
                    break;
                }
                case ZLTextParagraph.Entry.EXTENSION: {
                    final short kindLength = (short) data[dataOffset++];
                    final String kind = new String(data, dataOffset, kindLength);
                    dataOffset += kindLength;

                    final Map<String, String> map = new HashMap<String, String>();
                    final short dataSize = (short) ((first >> 8) & 0xFF);
                    for (short i = 0; i < dataSize; ++i) {
                        final short keyLength = (short) data[dataOffset++];
                        final String key = new String(data, dataOffset, keyLength);
                        dataOffset += keyLength;
                        final short valueLength = (short) data[dataOffset++];
                        map.put(key, new String(data, dataOffset, valueLength));
                        dataOffset += valueLength;
                    }
                    mExtensionEntry = new ExtensionEntry(kind, map);
                    break;
                }
            }
            ++mCounter;
            mDataOffset = dataOffset;
            return true;
        }
    }

    public ZLTextPlainModel(
            String id,
            String language,
            int paragraphsNumber,
            int[] entryIndices,
            int[] entryOffsets,
            int[] paragraphLengths,
            int[] textSizes,
            byte[] paragraphKinds,
            String directoryName,
            String fileExtension,
            int blocksNumber,
            Map<String, ZLImage> imageMap
    ) {
        mId = id;
        mLanguage = language;
        mParagraphsNumber = paragraphsNumber;
        mStartEntryIndices = entryIndices;
        mStartEntryOffsets = entryOffsets;
        mParagraphLengths = paragraphLengths;
        mTextSizes = textSizes;
        mParagraphKinds = paragraphKinds;
        mStorage = new CachedCharStorage(directoryName, fileExtension, blocksNumber);
        mImageMap = imageMap;
    }

    public final String getId() {
        return mId;
    }

    public final String getLanguage() {
        return mLanguage;
    }

    /**
     * 获取总段落数量
     *
     * @return 段落数量
     */
    public final int getParagraphsNumber() {
        return mParagraphsNumber;
    }

    public final ZLTextParagraph getParagraph(int index) {
        final byte kind = mParagraphKinds[index];
        return (kind == ZLTextParagraph.Kind.TEXT_PARAGRAPH) ?
                new ZLTextParagraphImpl(this, index) :
                new ZLTextSpecialParagraphImpl(kind, this, index);
    }

    public final int getTextLength(int index) {
        if (mTextSizes.length == 0) {
            return 0;
        }
        return mTextSizes[Math.max(Math.min(index, mParagraphsNumber - 1), 0)];
    }

    private static int binarySearch(int[] array, int length, int value) {
        int lowIndex = 0;
        int highIndex = length - 1;

        while (lowIndex <= highIndex) {
            int midIndex = (lowIndex + highIndex) >>> 1;
            int midValue = array[midIndex];
            if (midValue > value) {
                highIndex = midIndex - 1;
            } else if (midValue < value) {
                lowIndex = midIndex + 1;
            } else {
                return midIndex;
            }
        }
        return -lowIndex - 1;
    }

    public final int findParagraphByTextLength(int length) {
        int index = binarySearch(mTextSizes, mParagraphsNumber, length);
        if (index >= 0) {
            return index;
        }
        return Math.min(-index - 1, mParagraphsNumber - 1);
    }
}
