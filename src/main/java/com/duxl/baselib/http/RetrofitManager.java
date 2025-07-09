package com.duxl.baselib.http;

import com.duxl.baselib.http.interceptor.HttpHeaderInterceptor;
import com.duxl.baselib.http.interceptor.LogInterceptor;
import com.duxl.baselib.http.interceptor.NetworkInterceptor;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.Utils;

import java.util.concurrent.TimeUnit;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitManager
 * create by duxl 2020/8/18
 */
public class RetrofitManager {

    private static final int DEFAULT_TIME_OUT = 30;//超时时间 5s
    private static final int DEFAULT_READ_TIME_OUT = 30;
    private Retrofit mRetrofit;

    private RetrofitManager() {
        if (EmptyUtils.isNull(Utils.getApp().getGlobalHttpConfig())
                || EmptyUtils.isEmpty(Utils.getApp().getGlobalHttpConfig().getBaseUrl())) {
            throw new IllegalArgumentException("需要将你的Appliaction继承至BaseApplication，并重写getGlobalHttpConfig和实现getBaseUrl方法，getBaseUrl不能为空");
        }

        // 创建 OKHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//连接超时时间
        builder.writeTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//写操作 超时时间
        builder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//读操作超时时间

        // 添加公共参数拦截器
        HttpHeaderInterceptor headerInterceptor = new HttpHeaderInterceptor();
        builder.addInterceptor(headerInterceptor);

//        HttpCacheInterceptor httpCacheInterceptor = new HttpCacheInterceptor();
//        builder.addInterceptor(httpCacheInterceptor);
        if (Utils.getApp().getGlobalHttpConfig().isDEBUG()) {
            builder.addInterceptor(new LogInterceptor());
        }

        // 添加网络连接器
        NetworkInterceptor networkInterceptor = new NetworkInterceptor();
        builder.addInterceptor(networkInterceptor);

        // 额外OKHttp配置信息
        Utils.getApp().getGlobalHttpConfig().configurationOKHttp(builder);
        // 使用RetrofitUrlManager创建okHttpClient实例，
        // RetrofitUrlManager可以动态切换baseUrl
        OkHttpClient okHttpClient = RetrofitUrlManager.getInstance().with(builder).build();

        // 创建retrofitBuilder
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Utils.getApp().getGlobalHttpConfig().getBaseUrl());

        // 额外retrofitBuilder配置信息
        Utils.getApp().getGlobalHttpConfig().configurationRetrofit(retrofitBuilder);

        // 创建Retrofit
        mRetrofit = retrofitBuilder.build();
    }

    private static class SingletonHolder {
        private static final RetrofitManager INSTANCE = new RetrofitManager();
    }

    /**
     * 获取RetrofitServiceManager
     *
     * @return
     */
    public static RetrofitManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取对应的Service
     *
     * @param service Service 的 class
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);
    }

}
