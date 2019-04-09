package com.tzpt.cloudlibrary.modle.remote.newdownload.core.file;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.system.Os;

import com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Administrator on 2018/8/6.
 */

public class DownloadUriOutputStream implements DownloadOutputStream {

    @NonNull
    private final FileChannel channel;
    @NonNull
    private final ParcelFileDescriptor pdf;
    @NonNull
    private final BufferedOutputStream out;
    @NonNull
    private final FileOutputStream fos;

    DownloadUriOutputStream(Context context, Uri uri, int bufferSize) throws
            FileNotFoundException {
        final ParcelFileDescriptor pdf = context.getContentResolver().openFileDescriptor(uri, "rw");
        if (pdf == null)
            throw new FileNotFoundException("result of " + uri + " is null!");
        this.pdf = pdf;

        this.fos = new FileOutputStream(pdf.getFileDescriptor());
        this.channel = fos.getChannel();
        this.out = new BufferedOutputStream(fos, bufferSize);
    }



    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        out.close();
        fos.close();
    }

    @Override
    public void flushAndSync() throws IOException {
        out.flush();
        pdf.getFileDescriptor().sync();
    }

    @Override
    public void seek(long offset) throws IOException {
        channel.position(offset);
    }

    @Override
    public void setLength(long newLength) {
        final String tag = "DownloadUriOutputStream";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Os.ftruncate(pdf.getFileDescriptor(), newLength);
            } catch (Throwable e) {
                Util.w(tag, "It can't pre-allocate length(" + newLength + ") on the sdk"
                        + " version(" + Build.VERSION.SDK_INT + "), because of " + e);
            }
        } else {
            Util.w(tag,
                    "It can't pre-allocate length(" + newLength + ") on the sdk "
                            + "version(" + Build.VERSION.SDK_INT + ")");
        }
    }

    public static class Factory implements DownloadOutputStream.Factory {

        @Override
        public DownloadOutputStream create(Context context, File file, int flushBufferSize) throws
                FileNotFoundException {
            return new DownloadUriOutputStream(context, Uri.fromFile(file), flushBufferSize);
        }

        @Override
        public DownloadOutputStream create(Context context, Uri uri, int flushBufferSize) throws
                FileNotFoundException {
            return new DownloadUriOutputStream(context, uri, flushBufferSize);
        }

        @Override public boolean supportSeek() {
            return true;
        }
    }
}
