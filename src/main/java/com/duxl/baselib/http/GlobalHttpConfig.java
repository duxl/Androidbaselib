package com.duxl.baselib.http;

import com.duxl.baselib.http.interceptor.NetworkChecker;
import com.duxl.baselib.http.interceptor.NetworkInterceptor;
import com.duxl.baselib.utils.NetCheckUtil;
import com.duxl.baselib.utils.Utils;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 全局Http配置
 * create by duxl 2020/8/18
 */
public abstract class GlobalHttpConfig {

    public abstract String getBaseUrl();

    /**
     * 配置http头信息
     *
     * @return
     */
    public HashMap<String, String> getHeaders() {
        return null;
    }

    /**
     * Log日志拦截
     *
     * @param chain
     * @return 返回null表示用默认的日志拦截
     */
    public Response intercept(Interceptor.Chain chain) throws IOException {
        /*
        Request request = chain.request();
        // 请求链接包含userServer/login，直接返回不需要打印日志
        if (request.url().toString().contains("/userServer/login")) {
            return chain.proceed(request);
        }
         */
        return null;
    }

    /**
     * 网络监测
     *
     * @return 如果检测器监测到没有网，直接报异常
     */
    public NetworkChecker getNetworkChecker() {
        return () -> NetCheckUtil.checkNet(Utils.getContext());
    }
}
