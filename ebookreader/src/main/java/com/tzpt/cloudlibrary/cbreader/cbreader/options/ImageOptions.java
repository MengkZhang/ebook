package com.tzpt.cloudlibrary.cbreader.cbreader.options;

import com.tzpt.cloudlibrary.cbreader.cbreader.CBView;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLBooleanOption;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLColorOption;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLEnumOption;
import com.tzpt.cloudlibrary.zlibrary.core.util.ZLColor;

/**
 * Created by Administrator on 2017/4/9.
 */

public class ImageOptions {
    public final ZLColorOption ImageViewBackground;

    public final ZLEnumOption<CBView.ImageFitting> FitToScreen;
    public static enum TapActionEnum {
        doNothing, selectImage, openImageView
    }
    public final ZLEnumOption<TapActionEnum> TapAction;
    public final ZLBooleanOption MatchBackground;

    public ImageOptions() {
        ImageViewBackground =
                new ZLColorOption("Colors", "ImageViewBackground", new ZLColor(255, 255, 255));
        FitToScreen =
                new ZLEnumOption<CBView.ImageFitting>("Options", "FitImagesToScreen", CBView.ImageFitting.covers);
        TapAction =
                new ZLEnumOption<TapActionEnum>("Options", "ImageTappingAction", TapActionEnum.openImageView);
        MatchBackground =
                new ZLBooleanOption("Colors", "ImageMatchBackground", true);
    }
}
