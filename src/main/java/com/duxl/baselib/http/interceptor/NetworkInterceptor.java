package com.duxl.baselib.http.interceptor;


import com.duxl.baselib.BaseApplication;
import com.duxl.baselib.utils.EmptyUtils;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <pre>
 * 拦截器
 * 网络拦截器
 * Created by duxl on 2021/01/18.
 * </pre>
 */

public class NetworkInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (BaseApplication.getInstance().getGlobalHttpConfig() != null
                && EmptyUtils.isNotNull(BaseApplication.getInstance().getGlobalHttpConfig().getNetworkChecker())
                && !BaseApplication.getInstance().getGlobalHttpConfig().getNetworkChecker().isConnected()) {
            throw new NoNetworkException("network is disconnected");
        }

        return chain.proceed(chain.request());
    }

}
