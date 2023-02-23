package com.zpw.net.core.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HttpHeaderInterceptor implements Interceptor {
    private static final String token = "";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = original.newBuilder()
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .method(original.method(), original.body())
                .build();
        return chain.proceed(request);
    }
}
