package com.tzpt.cloudlibrary.zlibrary.core.resources;

import com.tzpt.cloudlibrary.zlibrary.core.language.Language;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLStringOption;

import java.util.Locale;

/**
 * 本地资源文件,国际化定义的名字
 * Created by Administrator on 2017/4/8.
 */
public abstract class ZLResource {
    public final String Name;

    private static final ZLStringOption ourLanguageOption =
            new ZLStringOption("LookNFeel", "Language", Language.SYSTEM_CODE);

    public static ZLStringOption getLanguageOption() {
        return ourLanguageOption;
    }

    public static String getLanguage() {
        final String lang = getLanguageOption().getValue();
        return Language.SYSTEM_CODE.equals(lang) ? Locale.getDefault().getLanguage() : lang;
    }

    public static ZLResource resource(String key) {
        ZLTreeResource.buildTree();
        if (ZLTreeResource.ourRoot == null) {
            return ZLMissingResource.Instance;
        }
        try {
            return ZLTreeResource.ourRoot.getResource(key);
        } finally {
        }
    }

    protected ZLResource(String name) {
        Name = name;
    }

    abstract public boolean hasValue();

    abstract public String getValue();

    abstract public String getValue(int condition);

    abstract public ZLResource getResource(String key);
}
