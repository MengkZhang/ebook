package com.tzpt.cloudlibrary.widget.pullrefreshlayout.api;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * 默认Header创建器
 */
public interface DefaultRefreshHeaderCreator {
    @NonNull
    RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout);
}
