package com.tzpt.cloudlibrary.modle.remote.support;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.Installation;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.utils.Utils;
import com.tzpt.cloudlibrary.utils.VersionUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/11/14.
 */

public class UserApiHeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder();
        Request request = builder.addHeader("token", null == UserRepository.getInstance().getToken() ? "" : UserRepository.getInstance().getToken())
                .addHeader("version", VersionUtils.getVersionInfo())
                .addHeader("client", "Android")
                .addHeader("identity", Installation.id(CloudLibraryApplication.getAppContext()))
                .build();
        return chain.proceed(request);
    }
}
