package com.duxl.baselib.http;

import com.duxl.baselib.BuildConfig;
import com.duxl.baselib.http.interceptor.NetworkChecker;
import com.duxl.baselib.utils.NetCheckUtil;
import com.duxl.baselib.utils.Utils;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * 全局Http配置
 * create by duxl 2020/8/18
 */
public abstract class GlobalHttpConfig {

    /**
     * 是否开发模式，开发模式会打印日志信息
     *
     * @return
     */
    public boolean isDEBUG() {
        return BuildConfig.DEBUG;
    }

    /**
     * baseUrl必须斜杠结尾
     *
     * @return
     */
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
     * 如果检测器监测到没有网，直接报异常就不再请求接口。
     * 因为没有网络的情况下去请求接口是毫无意义的
     *
     * @return 网络监测器
     */
    public NetworkChecker getNetworkChecker() {
        return () -> NetCheckUtil.checkNet(Utils.getApp());
    }

    /**
     * 对OKHttp配置额外信息，比如https双向认证
     *
     * @param builder
     */
    public void configurationOKHttp(OkHttpClient.Builder builder) {
        /*
        builder.sslSocketFactory(socketFactory);
        //解决报错javax.net.ssl.SSLPeerUnverifiedException: Hostname 127.0.0.1 not verified
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                System.out.println("主机:" + s);
                return true;
            }
        });
         */
    }
}
