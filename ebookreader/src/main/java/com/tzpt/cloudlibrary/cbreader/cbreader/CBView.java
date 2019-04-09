package com.tzpt.cloudlibrary.cbreader.cbreader;

import com.tzpt.cloudlibrary.cbreader.bookmodel.TOCTree;
import com.tzpt.cloudlibrary.cbreader.cbreader.options.ColorProfile;
import com.tzpt.cloudlibrary.cbreader.cbreader.options.ImageOptions;
import com.tzpt.cloudlibrary.cbreader.cbreader.options.ViewOptions;
import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLIntegerRangeOption;
import com.tzpt.cloudlibrary.zlibrary.core.util.ZLColor;
import com.tzpt.cloudlibrary.zlibrary.core.view.ZLViewEnums;
import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextModel;
import com.tzpt.cloudlibrary.zlibrary.text.view.ZLTextView;
import com.tzpt.cloudlibrary.zlibrary.text.view.ZLTextWordCursor;
import com.tzpt.cloudlibrary.zlibrary.text.view.sytle.ZLTextStyleCollection;
import com.tzpt.cloudlibrary.zlibrary.ui.android.library.ZLAndroidLibrary;

/**
 * 操作阅读UI，包括手势、设置显示样式（边界、字体、图片）参数
 * Created by Administrator on 2017/4/8.
 */

public final class CBView extends ZLTextView {
    private final ImageOptions mImageOptions = new ImageOptions();
    private final ViewOptions mViewOptions = new ViewOptions();
    private TOCTree mTOCTree;

    void setModel(ZLTextModel model, TOCTree tocTree) {
        super.setModel(model);
        mTOCTree = tocTree;
    }

    @Override
    public ZLTextStyleCollection getTextStyleCollection() {
        return mViewOptions.getTextStyleCollection();
    }

    @Override
    public ImageFitting getImageFitting() {
        return mImageOptions.FitToScreen.getValue();
    }

    @Override
    public int getLeftMargin() {
        return (int) (ZLAndroidLibrary.Instance().getDPI() * 8);
    }

    @Override
    public int getRightMargin() {
        return (int) (ZLAndroidLibrary.Instance().getDPI() * 8);
    }

    @Override
    public int getTopMargin() { // 顶部距离
        return (int) (ZLAndroidLibrary.Instance().getDPI() * 27);
    }

    @Override
    public int getBottomMargin() {
        return (int) (ZLAndroidLibrary.Instance().getDPI() * 27);
    }

    @Override
    public int getSpaceBetweenColumns() {
        return 50;
    }

    @Override
    public boolean twoColumnView() {
        return false;
    }

    @Override
    public ZLFile getWallpaperFile() {
        final String filePath = mViewOptions.getColorProfile().WallpaperOption.getValue();
        if ("".equals(filePath)) {
            return null;
        }
        final ZLFile file = ZLFile.createFileByPath(filePath);
        if (file == null || !file.exists()) {
            return null;
        }
        return file;
    }

    @Override
    public ZLColor getBackgroundColor() {
        return mViewOptions.getColorProfile().BackgroundOption.getValue();
    }

    @Override
    public ZLColor getTextColor() {
        final ColorProfile profile = mViewOptions.getColorProfile();
        return profile.RegularTextOption.getValue();
    }

    public static final int SCROLLBAR_SHOW_AS_FOOTER = 3;

    @Override
    protected String getBookTocTitle(int index) {
        TOCTree treeToSelect = null;
        for (TOCTree tree : mTOCTree) {//章节起始点\
            int paragraphIndex = tree.getParagraphIndex();
            if (paragraphIndex == -1) {
                continue;
            }
            if (paragraphIndex > index + 1) {
                break;
            }
            treeToSelect = tree;
        }
        return treeToSelect == null ? "" : treeToSelect.getText();
    }

    @Override
    public synchronized void onScrollingFinished(ZLViewEnums.PageIndex pageIndex) {
        super.onScrollingFinished(pageIndex);
    }

    ZLIntegerRangeOption getFontSizeOption() {
        return mViewOptions.getTextStyleCollection().getBaseStyle().FontSizeOption;
    }

    void setColorProfileName(String value) {
        mViewOptions.ColorProfileName.setValue(value);
    }

    void setRegularTextColor(ZLColor value) {
        mViewOptions.getColorProfile().RegularTextOption.setValue(value);
    }

    void setBackgroundColor(ZLColor value) {
        mViewOptions.getColorProfile().BackgroundOption.setValue(value);
    }

    boolean isColorProfileDay() {
        return mViewOptions.ColorProfileName.getValue().equals(ColorProfile.DAY);
    }
}
