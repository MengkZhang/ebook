package com.tzpt.cloudlibrary.zlibrary.ui.android.library;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;
import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLResourceFile;
import com.tzpt.cloudlibrary.zlibrary.core.library.ZLibrary;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.SharedPreferencesUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * 获取android相关接口，包括屏幕宽高，版本号等信息
 * Created by Administrator on 2017/4/7.
 */

public final class ZLAndroidLibrary extends ZLibrary {
    private final Application mApplication;

    //子类初始化前，一定先初始化父类
    public ZLAndroidLibrary(Application application) {
        mApplication = application;
        SharedPreferencesUtil.init(application, "tzpt_cloudlibrary_reader", 0);
    }

    @Override
    public ZLResourceFile createResourceFile(String path) {
        return new AndroidAssetsFile(path);
    }

    @Override
    public int getScreenWidth() {
        WindowManager wm = (WindowManager) mApplication
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    @Override
    public int getScreenHeight() {
        WindowManager wm = (WindowManager) mApplication
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    @Override
    public String getCurrentTimeString() {
        return DateFormat.getTimeFormat(mApplication.getApplicationContext()).format(new Date());
    }

    private DisplayMetrics myMetrics;

    private DisplayMetrics getMetrics() {
        if (myMetrics == null) {
            myMetrics = mApplication.getApplicationContext().getResources().getDisplayMetrics();
        }
        return myMetrics;
    }

    @Override
    public String getExternalCacheDir() {
        return mApplication.getExternalCacheDir().getPath();
    }

    @Override
    public int getDisplayDPI() {
        final DisplayMetrics metrics = getMetrics();
        return metrics == null ? 0 : (int) (160 * metrics.density);
    }

    @Override
    public float getDPI() {
        final DisplayMetrics metrics = getMetrics();
        return metrics == null ? 0 : metrics.density;
    }

    @Override
    public float getSP() {
        final DisplayMetrics metrics = getMetrics();
        return metrics == null ? 0 : metrics.scaledDensity;
    }

    @Override
    public int getWidthInPixels() {
        final DisplayMetrics metrics = getMetrics();
        return metrics == null ? 0 : metrics.widthPixels;
    }

    @Override
    public int getHeightInPixels() {
        final DisplayMetrics metrics = getMetrics();
        return metrics == null ? 0 : metrics.heightPixels;
    }

    private interface StreamStatus {
        int UNKNOWN = -1;
        int NULL = 0;
        int OK = 1;
        int EXCEPTION = 2;
    }

    private final class AndroidAssetsFile extends ZLResourceFile {
        private final AndroidAssetsFile mParent;
        private int mStreamStatus = StreamStatus.UNKNOWN;
        private long mSize = -1;

        AndroidAssetsFile(String path) {
            super(path);
            if (path.length() == 0) {
                mParent = null;
            } else {
                final int index = path.lastIndexOf('/');
                mParent = new AndroidAssetsFile(index >= 0 ? path.substring(0, path.lastIndexOf('/')) : "");
            }
        }

        private int streamStatus() {
            if (mStreamStatus == StreamStatus.UNKNOWN) {
                try {
                    final InputStream stream = mApplication.getAssets().open(getPath());
                    if (stream == null) {
                        mStreamStatus = StreamStatus.NULL;
                    } else {
                        stream.close();
                        mStreamStatus = StreamStatus.OK;
                    }
                } catch (IOException e) {
                    mStreamStatus = StreamStatus.EXCEPTION;
                }
            }
            return mStreamStatus;
        }

        @Override
        public boolean isDirectory() {
            return streamStatus() != StreamStatus.OK;
        }

        @Override
        public boolean exists() {
            if (streamStatus() == StreamStatus.OK) {
                return true;
            }
            final String path = getPath();
            if ("".equals(path)) {
                return true;
            }
            // a hack: we store help files in fb2 format
            if (path.endsWith(".fb2")) {
                return false;
            }
            try {
                String[] names = mApplication.getAssets().list(getPath());
                if (names != null && names.length != 0) {
                    // directory exists
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public long size() {
            if (mSize == -1) {
                mSize = sizeInternal();
            }
            return sizeInternal();
        }

        private long sizeInternal() {
            try {
                AssetFileDescriptor descriptor = mApplication.getAssets().openFd(getPath());
                // for some files (archives, crt) descriptor cannot be opened
                if (descriptor == null) {
                    return sizeSlow();
                }
                long length = descriptor.getLength();
                descriptor.close();
                return length;
            } catch (IOException e) {
                return sizeSlow();
            }
        }

        private long sizeSlow() {
            try {
                final InputStream stream = getInputStream();
                if (stream == null) {
                    return 0;
                }
                long size = 0;
                final long step = 1024 * 1024;
                while (true) {
                    // TODO: does skip work as expected for these files?
                    long offset = stream.skip(step);
                    size += offset;
                    if (offset < step) {
                        break;
                    }
                }
                return size;
            } catch (IOException e) {
                return 0;
            }
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return mApplication.getAssets().open(getPath());
        }

        @Override
        public ZLFile getParent() {
            return mParent;
        }
    }
}
