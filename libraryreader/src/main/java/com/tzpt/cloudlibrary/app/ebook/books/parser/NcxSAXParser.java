package com.tzpt.cloudlibrary.app.ebook.books.parser;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * epub ncx 文件解析
 */
public class NcxSAXParser extends DefaultHandler {

    private final ArrayList<EpubParser.NavPoint> mNavMap;
    private EpubParser.NavPoint navPoint;

    private long navLevel = 0;
    private String mStartTag;
    private String prevUrl;

    public NcxSAXParser(ArrayList<EpubParser.NavPoint> nav) {
        mNavMap = nav;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        mStartTag = localName;

        if (localName.equalsIgnoreCase("navPoint")) {
            navPoint = new EpubParser.NavPoint();

            navPoint.id = attributes.getValue("id");
            navPoint.playOrder = attributes.getValue("playOrder");
            navPoint.navLevel = navLevel;
            ++navLevel;
        }

        if ("content".equalsIgnoreCase(localName)) {
            String src = attributes.getValue("src");
            int index = src.lastIndexOf("#");

            String url = (index > 0) ? src.substring(0, index) : src;
            String anchor = (index > 0) ? src.substring(index + 1) : "";
            navPoint.src = url;
            navPoint.anchor = anchor;

            // add this navPoint to TOC
            mNavMap.add(navPoint);

            prevUrl = url;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);

        StringBuffer sb = new StringBuffer();
        for (int i = start; i < (start + length); i++) {
            sb.append(ch[i]);
        }
        String value = sb.toString();

        if ("text".equalsIgnoreCase(mStartTag) && navPoint != null)
            navPoint.navLabel = value;
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri, localName, qName);

        if (mStartTag.equalsIgnoreCase(localName))
            mStartTag = "";

        if (localName.equalsIgnoreCase("navPoint")) {
            --navLevel;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

}
