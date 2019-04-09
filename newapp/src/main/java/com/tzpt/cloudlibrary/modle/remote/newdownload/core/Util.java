package com.tzpt.cloudlibrary.modle.remote.newdownload.core;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.StatFs;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.BuildConfig;
import android.util.Log;

import com.tzpt.cloudlibrary.modle.remote.newdownload.PDownload;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.DownloadStore;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.connection.DownloadConnection;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.connection.DownloadOkHttp3Connection;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/8/6.
 */

public class Util {

    // request header fields.
    public static final String RANGE = "Range";
    private static final String IF_MATCH = "If-Match";
    private static final String USER_AGENT = "User-Agent";

    // response header fields.
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_RANGE = "Content-Range";

    public static final int CHUNKED_CONTENT_LENGTH = -1;

    public interface Logger {
        void e(String tag, String msg, Exception e);

        void w(String tag, String msg);

        void d(String tag, String msg);

        void i(String tag, String msg);
    }

    public static class EmptyLogger implements Logger {
        @Override
        public void e(String tag, String msg, Exception e) {
        }

        @Override
        public void w(String tag, String msg) {
        }

        @Override
        public void d(String tag, String msg) {
        }

        @Override
        public void i(String tag, String msg) {
        }
    }

    @SuppressWarnings("PMD.LoggerIsNotStaticFinal")
    private static Logger logger = new EmptyLogger();

    /**
     * Enable logger used for okdownload, and print each log with {@link Log}.
     */
    public static void enableConsoleLog() {
        logger = null;
    }

    /**
     * Set the logger which using on okdownload.
     * default one is {@link EmptyLogger}.
     *
     * @param l if provide logger is {@code null} we will using {@link Log} as default.
     */
    public static void setLogger(@Nullable Logger l) {
        logger = l;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void e(String tag, String msg, Exception e) {
        if (logger != null) {
            logger.e(tag, msg, e);
            return;
        }

        Log.e(tag, msg, e);
    }

    public static void w(String tag, String msg) {
        if (logger != null) {
            logger.w(tag, msg);
            return;
        }

        Log.w(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (logger != null) {
            logger.d(tag, msg);
            return;
        }

        Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (logger != null) {
            logger.i(tag, msg);
            return;
        }

        Log.i(tag, msg);
    }

    // For avoid mock whole android framework methods on unit-test.
    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static ThreadFactory threadFactory(final String name, final boolean daemon) {
        return new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable runnable) {
                final Thread result = new Thread(runnable, name);
                result.setDaemon(daemon);
                return result;
            }
        };
    }

    @Nullable
    public static String md5(String string) {
        byte[] hash = null;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ignored) {
        }

        if (hash != null) {
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10) hex.append('0');
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        }

        return null;
    }

    public static boolean isCorrectFull(long fetchedLength, long contentLength) {
        return fetchedLength == contentLength;
    }

    public static long getFreeSpaceBytes(@NonNull StatFs statFs) {
        // NEED CHECK PERMISSION?
        long freeSpaceBytes;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            freeSpaceBytes = statFs.getAvailableBytes();
        } else {
            //noinspection deprecation
            freeSpaceBytes = statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
        }

        return freeSpaceBytes;
    }

    public static String humanReadableBytes(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = String.valueOf(("KMGTPE").charAt(exp - 1));
        return String.format(Locale.ENGLISH, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    @NonNull
    public static DownloadConnection.Factory createDefaultConnectionFactory() {
        return new DownloadOkHttp3Connection.Factory();
    }

    public static long parseContentLength(@Nullable String contentLength) {
        if (contentLength == null) return CHUNKED_CONTENT_LENGTH;

        try {
            return Long.parseLong(contentLength);
        } catch (NumberFormatException ignored) {
            ignored.printStackTrace();
        }

        return CHUNKED_CONTENT_LENGTH;
    }

    public static boolean isNetworkNotOnWifiType(ConnectivityManager manager) {
        if (manager == null) {
            return true;
        }

        final NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.getType() != ConnectivityManager.TYPE_WIFI;
    }

    public static boolean checkPermission(String permission) {
        final int perm = PDownload.with().context().checkCallingOrSelfPermission(permission);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    public static long parseContentLengthFromContentRange(@Nullable String contentRange) {
        if (contentRange == null || contentRange.length() == 0) return CHUNKED_CONTENT_LENGTH;
        final String pattern = "bytes (\\d+)-(\\d+)/\\d+";
        try {
            final Pattern r = Pattern.compile(pattern);
            final Matcher m = r.matcher(contentRange);
            if (m.find()) {
                final long rangeStart = Long.parseLong(m.group(1));
                final long rangeEnd = Long.parseLong(m.group(2));
                return rangeEnd - rangeStart + 1;
            }
        } catch (Exception e) {
            Util.w("Util", "parse content-length from content-range failed " + e);
        }
        return CHUNKED_CONTENT_LENGTH;
    }

    public static boolean isUriContentScheme(@NonNull Uri uri) {
        return uri.getScheme().equals(ContentResolver.SCHEME_CONTENT);
    }

    public static boolean isUriFileScheme(@NonNull Uri uri) {
        return uri.getScheme().equals(ContentResolver.SCHEME_FILE);
    }

    @Nullable
    public static String getFilenameFromContentUri(@NonNull Uri contentUri) {
        final ContentResolver resolver = PDownload.with().context().getContentResolver();
        final Cursor cursor = resolver.query(contentUri, null, null, null, null);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
                return cursor
                        .getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            } finally {
                cursor.close();
            }
        }

        return null;
    }

    @NonNull
    public static File getParentFile(final File file) {
        final File candidate = file.getParentFile();
        return candidate == null ? new File("/") : candidate;
    }

    public static long getSizeFromContentUri(@NonNull Uri contentUri) {
        final ContentResolver resolver = PDownload.with().context().getContentResolver();
        final Cursor cursor = resolver.query(contentUri, null, null, null, null);
        if (cursor != null) {
            try {
                cursor.moveToFirst();
                return cursor
                        .getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
            } finally {
                cursor.close();
            }
        }
        return 0;
    }

    public static boolean isNetworkAvailable(ConnectivityManager manager) {
        if (manager == null) {
            return true;
        }

        //noinspection MissingPermission, because we check permission accessable when invoked
        @SuppressLint("MissingPermission") final NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static void inspectUserHeader(@NonNull Map<String, String> headerField)
            throws IOException {
        if (headerField.containsKey(IF_MATCH) || headerField.containsKey(RANGE)) {
            throw new IOException(IF_MATCH + " and " + RANGE + " only can be handle by internal!");
        }
    }

    public static void addUserRequestHeaderField(@NonNull Map<String, String> userHeaderField,
                                                 @NonNull DownloadConnection connection)
            throws IOException {
        inspectUserHeader(userHeaderField);

        for (Map.Entry<String, String> entry : userHeaderField.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            connection.addHeader(key, value);
        }
    }

    public static void addDefaultUserAgent(@NonNull final DownloadConnection connection) {
        final String userAgent = "PDownload/" + BuildConfig.VERSION_NAME;
        connection.addHeader(USER_AGENT, userAgent);
    }
}
