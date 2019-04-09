package com.tzpt.cloudlibrary.zlibrary.core.util;

import android.util.Xml;

import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;

import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Administrator on 2017/4/7.
 */

public abstract class XmlUtil {
    public static boolean parseQuietly(ZLFile file, DefaultHandler handler) {
        try {
            Xml.parse(file.getInputStream(), Xml.Encoding.UTF_8, handler);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
