package com.duxl.baselib.http.interceptor;


import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
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
        if (Utils.getApp().getGlobalHttpConfig() != null
                && EmptyUtils.isNotNull(Utils.getApp().getGlobalHttpConfig().getNetworkChecker())
                && !Utils.getApp().getGlobalHttpConfig().getNetworkChecker().isConnected()) {
            throw new NoNetworkException("network is disconnected");
        }

        return chain.proceed(chain.request());
    }

}
