package com.tzpt.cloudlibrary.cbreader.cbreader.options;

import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLColorOption;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLStringOption;
import com.tzpt.cloudlibrary.zlibrary.core.util.ZLColor;

import java.util.HashMap;

/**
 * 颜色（背景、文字颜色）配置文件
 * Created by Administrator on 2017/4/8.
 */

public class ColorProfile {
    public static final String DAY = "defaultLight";
    public static final String NIGHT = "defaultDark";

    private static final HashMap<String, ColorProfile> ourProfiles = new HashMap<>();

    public static ColorProfile get(String name) {
        ColorProfile profile = ourProfiles.get(name);
        if (profile == null) {
            profile = new ColorProfile(name);
            ourProfiles.put(name, profile);
        }
        return profile;
    }

    public final String Name;

    public final ZLStringOption WallpaperOption;
    public final ZLColorOption BackgroundOption;
    public final ZLColorOption RegularTextOption;

    private static ZLColorOption createOption(String profileName, String optionName, int r, int g, int b) {
        return new ZLColorOption("Colors", profileName + ':' + optionName, new ZLColor(r, g, b));
    }

    private ColorProfile(String name) {
        Name = name;
        if (NIGHT.equals(name)) {
            WallpaperOption = new ZLStringOption("Colors", name + ":Wallpaper", "wallpapers/paper11.jpg");
            BackgroundOption = createOption(name, "Background", 51, 43, 36);
            RegularTextOption = createOption(name, "Text", 123, 109, 98);
        } else {
            WallpaperOption = new ZLStringOption("Colors", name + ":Wallpaper", "wallpapers/paper.jpg");
            BackgroundOption = createOption(name, "Background", 214, 200, 169);
            RegularTextOption = createOption(name, "Text", 68, 68, 68);
        }
    }
}
