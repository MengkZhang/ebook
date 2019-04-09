package com.tzpt.cloudlibrary.zlibrary.text.view.sytle;

import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLResourceFile;

import java.util.Map;

/**
 * 文字的各种样式
 * Created by Administrator on 2017/4/8.
 */

public class ZLTextStyleCollection {
//    private final String Screen;
    private ZLTextBaseStyle mBaseStyle;
//    private final List<ZLTextNGStyleDescription> myDescriptionList;
    private final ZLTextNGStyleDescription[] mDescriptionMap = new ZLTextNGStyleDescription[256];

    public ZLTextStyleCollection() {
//        Screen = screen;
        final Map<Integer, ZLTextNGStyleDescription> descriptions =
                new SimpleCSSReader().read(ZLResourceFile.createResourceFile("default/styles.css"));
//        myDescriptionList = Collections.unmodifiableList(new ArrayList<>(descriptions.values()));
        for (Map.Entry<Integer, ZLTextNGStyleDescription> entry : descriptions.entrySet()) {
            mDescriptionMap[entry.getKey() & 0xFF] = entry.getValue();
        }
//        XmlUtil.parseQuietly(
//                ZLResourceFile.createResourceFile("default/styles.xml"),
//                new TextStyleReader()
//        );
        mBaseStyle = new ZLTextBaseStyle(
                "Base",
                "Monospace",
                20
        );
    }

    public ZLTextBaseStyle getBaseStyle() {
        return mBaseStyle;
    }

//    public List<ZLTextNGStyleDescription> getDescriptionList() {
//        return myDescriptionList;
//    }

    public ZLTextNGStyleDescription getDescription(byte kind) {
        return mDescriptionMap[kind & 0xFF];
    }

//    private class TextStyleReader extends DefaultHandler {
//        private int intValue(Attributes attributes, String name, int defaultValue) {
//            final String value = attributes.getValue(name);
//            if (value != null) {
//                try {
//                    return Integer.parseInt(value);
//                } catch (NumberFormatException e) {
//                }
//            }
//            return defaultValue;
//        }
//
//        @Override
//        public void startElement(String uri, String localName, String qName, Attributes attributes) {
//            if ("base".equals(localName) && Screen.equals(attributes.getValue("screen"))) {
//                myBaseStyle = new ZLTextBaseStyle(
//                        Screen,
//                        "Monospace",
//                        20
//                );
//            }
//        }
//    }
}
