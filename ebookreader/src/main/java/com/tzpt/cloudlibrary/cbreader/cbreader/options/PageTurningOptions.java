package com.tzpt.cloudlibrary.cbreader.cbreader.options;

import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLBooleanOption;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLEnumOption;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLIntegerRangeOption;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLStringOption;
import com.tzpt.cloudlibrary.zlibrary.core.view.ZLView;

/**
 * Created by Administrator on 2017/4/8.
 */

public class PageTurningOptions {
    public static enum FingerScrollingType {
        byTap, byFlick, byTapAndFlick
    }
    public final ZLEnumOption<FingerScrollingType> FingerScrolling =
            new ZLEnumOption<FingerScrollingType>("Scrolling", "Finger", FingerScrollingType.byTapAndFlick);

    public final ZLEnumOption<ZLView.Animation> Animation =
            new ZLEnumOption<ZLView.Animation>("Scrolling", "Animation", ZLView.Animation.slide);
    public final ZLIntegerRangeOption AnimationSpeed =
            new ZLIntegerRangeOption("Scrolling", "AnimationSpeed", 1, 10, 2);

    public final ZLBooleanOption Horizontal =
            new ZLBooleanOption("Scrolling", "Horizontal", true);
    public final ZLStringOption TapZoneMap =
            new ZLStringOption("Scrolling", "TapZoneMap", "");
}
