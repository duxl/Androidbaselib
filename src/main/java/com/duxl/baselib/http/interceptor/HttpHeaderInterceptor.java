package com.duxl.baselib.http.interceptor;


import com.duxl.baselib.BaseApplication;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.Utils;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <pre>
 * 拦截器
 * 向请求头里添加公共参数
 * Created by leibown on 16/11/10.
 * </pre>
 */

public class HttpHeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (Utils.getApp().getGlobalHttpConfig() == null
                || EmptyUtils.isEmpty(Utils.getApp().getGlobalHttpConfig().getHeaders())) {
            return chain.proceed(chain.request());
        }

        Request oldRequest = chain.request();
        Request.Builder requestBuilder = oldRequest.newBuilder();
        requestBuilder.method(oldRequest.method(), oldRequest.body());
        for (Map.Entry<String, String> entry : Utils.getApp().getGlobalHttpConfig().getHeaders().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (EmptyUtils.isNotEmpty(key) && EmptyUtils.isNotNull(value)) {
                requestBuilder.addHeader(key, value);
            }
        }
        Request newRequest = requestBuilder.build();
        return chain.proceed(newRequest);
    }

}
