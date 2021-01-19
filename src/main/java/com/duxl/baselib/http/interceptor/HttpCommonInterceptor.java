package com.duxl.baselib.http.interceptor;


import com.duxl.baselib.BaseApplication;
import com.duxl.baselib.utils.EmptyUtils;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 拦截器
 * <p>
 * 向请求头里添加公共参数
 * Created by leibown on 16/11/10.
 */

public class HttpCommonInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (BaseApplication.getInstance().getGlobalHttpConfig() == null
                || EmptyUtils.isEmpty(BaseApplication.getInstance().getGlobalHttpConfig().getHeaders())) {
            return chain.proceed(chain.request());
        }

        Request oldRequest = chain.request();
        Request.Builder requestBuilder = oldRequest.newBuilder();
        requestBuilder.method(oldRequest.method(), oldRequest.body());
        for (Map.Entry<String, String> entry : BaseApplication.getInstance().getGlobalHttpConfig().getHeaders().entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        Request newRequest = requestBuilder.build();
        return chain.proceed(newRequest);
    }

}
