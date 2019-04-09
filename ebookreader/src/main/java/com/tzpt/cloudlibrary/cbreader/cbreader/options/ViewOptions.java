package com.tzpt.cloudlibrary.cbreader.cbreader.options;

import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLStringOption;
import com.tzpt.cloudlibrary.zlibrary.text.view.sytle.ZLTextStyleCollection;

/**
 * 包含整个阅读展示控件的参数
 * Created by Administrator on 2017/4/8.
 */

public class ViewOptions {
//    public final ZLBooleanOption TwoColumnView;
//    public final ZLIntegerRangeOption LeftMargin;
//    public final ZLIntegerRangeOption RightMargin;
//    public final ZLIntegerRangeOption TopMargin;
//    public final ZLIntegerRangeOption BottomMargin;
//    public final ZLIntegerRangeOption SpaceBetweenColumns;
//    public final ZLIntegerRangeOption ScrollbarType;
//    public final ZLIntegerRangeOption FooterHeight;
    public final ZLStringOption ColorProfileName;

    private ColorProfile myColorProfile;
    private ZLTextStyleCollection myTextStyleCollection;

    public ViewOptions() {
//        final ZLibrary zlibrary = ZLibrary.Instance();

//        final int dpi = zlibrary.getDisplayDPI();
//        final int x = zlibrary.getWidthInPixels();
//        final int y = zlibrary.getHeightInPixels();
//        final int horMargin = Math.min(dpi / 5, Math.min(x, y) / 30);

//        TwoColumnView =
//                new ZLBooleanOption("Options", "TwoColumnView", x * x + y * y >= 42 * dpi * dpi);
//        LeftMargin =
//                new ZLIntegerRangeOption("Options", "LeftMargin", 0, 100, horMargin);
//        RightMargin =
//                new ZLIntegerRangeOption("Options", "RightMargin", 0, 100, horMargin);
//        TopMargin =
//                new ZLIntegerRangeOption("Options", "TopMargin", 0, 100, 0);
//        BottomMargin =
//                new ZLIntegerRangeOption("Options", "BottomMargin", 0, 100, 4);
//        SpaceBetweenColumns =
//                new ZLIntegerRangeOption("Options", "SpaceBetweenColumns", 0, 300, 3 * horMargin);
//        ScrollbarType =
//                new ZLIntegerRangeOption("Options", "ScrollbarType", 0, 4, CBView.SCROLLBAR_SHOW_AS_FOOTER);
//        FooterHeight =
//                new ZLIntegerRangeOption("Options", "FooterHeight", 8, dpi / 8, dpi / 12);
        ColorProfileName = new ZLStringOption("Options", "ColorProfile", ColorProfile.DAY);
    }

    public ColorProfile getColorProfile() {
        final String name = ColorProfileName.getValue();
        if (myColorProfile == null || !name.equals(myColorProfile.Name)) {
            myColorProfile = ColorProfile.get(name);
        }
        return myColorProfile;
    }

    public ZLTextStyleCollection getTextStyleCollection() {
        if (myTextStyleCollection == null) {
            myTextStyleCollection = new ZLTextStyleCollection();
        }
        return myTextStyleCollection;
    }
}
