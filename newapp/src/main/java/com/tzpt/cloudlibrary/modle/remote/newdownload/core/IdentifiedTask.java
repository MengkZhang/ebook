package com.tzpt.cloudlibrary.modle.remote.newdownload.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by Administrator on 2018/8/6.
 */

public abstract class IdentifiedTask {

    @NonNull
    public abstract String getUrl();

    @NonNull
    public abstract File getParentFile();

    @Nullable
    public abstract String getFilename();
}
