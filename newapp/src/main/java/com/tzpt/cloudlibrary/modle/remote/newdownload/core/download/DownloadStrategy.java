package com.tzpt.cloudlibrary.modle.remote.newdownload.core.download;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tzpt.cloudlibrary.modle.VideoRepository;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadTask;
import com.tzpt.cloudlibrary.modle.remote.newdownload.PDownload;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint.BreakpointInfo;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception.NetworkPolicyException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/8/6.
 */

public class DownloadStrategy {
    private static final String TAG = "DownloadStrategy";

    private static final Pattern TMP_FILE_NAME_PATTERN = Pattern
            .compile(".*\\\\|/([^\\\\|/|?]*)\\??");

    void validFilenameFromResponse(@Nullable String responseFileName,
                                   @NonNull DownloadTask task,
                                   @NonNull BreakpointInfo info) throws IOException {
        if (Util.isEmpty(task.getFilename())) {
            final String filename = determineFilename(responseFileName, task);

            // Double check avoid changed by other block.
            if (Util.isEmpty(task.getFilename())) {
                synchronized (task) {
                    if (Util.isEmpty(task.getFilename())) {
                        task.getFilenameHolder().set(filename);
                        info.getFilenameHolder().set(filename);
                    }

                }
            }
        }
    }

    private String determineFilename(@Nullable String responseFileName,
                                     @NonNull DownloadTask task) throws IOException {

        if (Util.isEmpty(responseFileName)) {

            final String url = task.getUrl();
            Matcher m = TMP_FILE_NAME_PATTERN.matcher(url);
            String filename = null;
            while (m.find()) {
                filename = m.group(1);
            }

            if (Util.isEmpty(filename)) {
                filename = Util.md5(url);
            }

            if (filename == null) {
                throw new IOException("Can't find valid filename.");
            }

            return filename;
        }

        return responseFileName;
    }


    public static class FilenameHolder {
        private volatile String filename;

        public FilenameHolder() {
        }

        public FilenameHolder(@NonNull String filename) {
            this.filename = filename;
        }

        void set(@NonNull String filename) {
            this.filename = filename;
        }

        @Nullable
        public String get() {
            return filename;
        }

        @Override
        public boolean equals(Object obj) {
            if (super.equals(obj)) return true;

            if (obj instanceof FilenameHolder) {
                if (filename == null) {
                    return ((FilenameHolder) obj).filename == null;
                } else {
                    return filename.equals(((FilenameHolder) obj).filename);
                }
            }

            return false;
        }

        @Override
        public int hashCode() {
            return filename == null ? 0 : filename.hashCode();
        }
    }

    boolean isServerCanceled(int responseCode) {
        if (responseCode != HttpURLConnection.HTTP_PARTIAL
                && responseCode != HttpURLConnection.HTTP_OK) {
            return true;
        }
        return false;
    }

    private ConnectivityManager manager = null;

    public void inspectNetworkAvailable() throws UnknownHostException {
        if (manager == null) {
            manager = (ConnectivityManager) PDownload.with().context()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        if (!Util.isNetworkAvailable(manager)) {
            throw new UnknownHostException("network is not available!");
        }
    }

    public void inspectNetworkOnWifi() throws IOException {
        if (VideoRepository.getInstance().checkMobileNetAble()
                && !VideoRepository.getInstance().checkFirstMobileNetTip())
            return;

        if (manager == null) {
            manager = (ConnectivityManager) PDownload.with().context()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        if (Util.isNetworkNotOnWifiType(manager)) {
            throw new NetworkPolicyException();
        }
    }
}
