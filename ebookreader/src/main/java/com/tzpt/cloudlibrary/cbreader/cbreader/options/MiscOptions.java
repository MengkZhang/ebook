package com.tzpt.cloudlibrary.cbreader.cbreader.options;

import com.tzpt.cloudlibrary.cbreader.cbreader.DurationEnum;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLBooleanOption;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLEnumOption;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLIntegerRangeOption;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLStringOption;

/**
 * Created by Administrator on 2017/4/9.
 */

public class MiscOptions {
    public final ZLBooleanOption AllowScreenBrightnessAdjustment;
    public final ZLStringOption TextSearchPattern;

    public final ZLBooleanOption EnableDoubleTap;
    public final ZLBooleanOption NavigateAllWords;

    public static enum WordTappingActionEnum {
        doNothing, selectSingleWord, startSelecting, openDictionary
    }
    public final ZLEnumOption<WordTappingActionEnum> WordTappingAction;

    public final ZLIntegerRangeOption ToastFontSizePercent;
    public static enum FootnoteToastEnum {
        never, footnotesOnly, footnotesAndSuperscripts, allInternalLinks
    }
    public final ZLEnumOption<FootnoteToastEnum> ShowFootnoteToast;
    public final ZLEnumOption<DurationEnum> FootnoteToastDuration;

    public MiscOptions() {
        AllowScreenBrightnessAdjustment =
                new ZLBooleanOption("LookNFeel", "AllowScreenBrightnessAdjustment", true);
        TextSearchPattern =
                new ZLStringOption("TextSearch", "Pattern", "");

        EnableDoubleTap =
                new ZLBooleanOption("Options", "EnableDoubleTap", false);
        NavigateAllWords =
                new ZLBooleanOption("Options", "NavigateAllWords", false);

        WordTappingAction =
                new ZLEnumOption<WordTappingActionEnum>("Options", "WordTappingAction", WordTappingActionEnum.startSelecting);

        ToastFontSizePercent =
                new ZLIntegerRangeOption("Options", "ToastFontSizePercent", 25, 100, 90);
        ShowFootnoteToast =
                new ZLEnumOption<FootnoteToastEnum>("Options", "ShowFootnoteToast", FootnoteToastEnum.footnotesAndSuperscripts);
        FootnoteToastDuration =
                new ZLEnumOption<DurationEnum>("Options", "FootnoteToastDuration", DurationEnum.duration5);
    }
}
