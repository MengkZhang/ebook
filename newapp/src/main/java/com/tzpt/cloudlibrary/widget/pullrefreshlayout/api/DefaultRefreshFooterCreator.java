package com.tzpt.cloudlibrary.widget.pullrefreshlayout.api;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * 默认Footer创建器
 */

public interface DefaultRefreshFooterCreator {
    @NonNull
    RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout);
}
