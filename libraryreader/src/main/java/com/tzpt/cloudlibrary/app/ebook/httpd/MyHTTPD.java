
package com.tzpt.cloudlibrary.app.ebook.httpd;

import android.util.Pair;

import com.tzpt.cloudlibrary.app.ebook.books.parser.IParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MyHTTPD extends NanoHTTPD {

    public static int HTTPD_PORT = 1988;
    public static String HTTPD_URL_BASE = "http://localhost:1988/";

    private final IParser mParser;

    public MyHTTPD(int port, File wwwroot, IParser parser) throws IOException {
        super(port, wwwroot);

        mParser = parser;
    }

    @Override
    public Response serveFile(String uri, Properties header, File homeDir,
                              boolean allowDirectoryListing) {
        Response res = null;

        if (mParser != null) {
            synchronized (mParser) {
                try {
                    //一直会出现favico.ico 和解析无关
                    //解决中文乱码
                    uri = new String(uri.getBytes("ISO8859-1"), "utf-8");
                    uri = uri.startsWith("/") ? uri.substring(1) : uri;
                    // Get MIME type from file name extension, if possible
                    String mime = null;
                    int dot = uri.lastIndexOf('.');
                    if (dot >= 0)
                        mime = (String) theMimeTypes.get(uri.substring(dot + 1).toLowerCase());
                    if (mime == null)
                        mime = MIME_DEFAULT_BINARY;
                    Pair<? extends InputStream, Long> resouce = mParser.getFileStream(uri);
                    if (resouce.first != null) {
                        res = new Response(HTTP_OK, mime, resouce.first);
                        res.addHeader("Content-Length", "" + resouce.second);
                    } else {
                        res = new Response(HTTP_FORBIDDEN, MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
                    }
                } catch (Exception e) {
                    res = new Response(HTTP_FORBIDDEN, MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
                } finally {
                }
            }
        }
        res.addHeader("Accept-Ranges", "bytes"); // Announce that the file
        // server accepts partial
        // content requestes
        return res;
    }

}
