package com.tzpt.cloundlibrary.manager.modle.remote.support;

import com.tzpt.cloundlibrary.manager.ManagerApplication;
import com.tzpt.cloundlibrary.manager.utils.VersionUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/6/21.
 */

public class HeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder();
        Request request = builder.addHeader("token", null == ManagerApplication.TOKEN ? "" : ManagerApplication.TOKEN)
                .addHeader("version", VersionUtils.getVersionInfo())
                .build();
        return chain.proceed(request);
    }
}
