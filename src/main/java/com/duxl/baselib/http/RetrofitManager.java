package com.duxl.baselib.http;

import com.duxl.baselib.http.interceptor.HttpHeaderInterceptor;
import com.duxl.baselib.http.interceptor.NetworkInterceptor;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.Utils;

import java.util.concurrent.TimeUnit;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;

/**
 * RetrofitManager
 * create by duxl 2020/8/18
 */
public class RetrofitManager {

    private static final int DEFAULT_TIME_OUT = 30;//超时时间 5s
    private static final int DEFAULT_READ_TIME_OUT = 30;
    private Retrofit mRetrofit;
    private OkHttpClient.Builder urlOkHttpClientBuilder;

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

        // 添加网络连接器
        NetworkInterceptor networkInterceptor = new NetworkInterceptor();
        builder.addInterceptor(networkInterceptor);

        // 使用RetrofitUrlManager创建okHttpClient实例，RetrofitUrlManager可以动态切换baseUrl
        urlOkHttpClientBuilder = RetrofitUrlManager.getInstance().with(builder);

        // 额外OKHttp配置信息
        Utils.getApp().getGlobalHttpConfig().configurationOKHttp(urlOkHttpClientBuilder);

        // 日志拦截器放最后，便于打印其它拦截器修改参数后的日志
        if (Utils.getApp().getGlobalHttpConfig().isDEBUG()) {
            Interceptor logInterceptor = Utils.getApp().getGlobalHttpConfig().getLogInterceptor();
            if (logInterceptor != null) {
                urlOkHttpClientBuilder.addInterceptor(logInterceptor);
            }
        }
        OkHttpClient okHttpClient = urlOkHttpClientBuilder.build();

        // 创建retrofitBuilder
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .baseUrl(Utils.getApp().getGlobalHttpConfig().getBaseUrl());

        // 额外retrofitBuilder配置信息
        Utils.getApp().getGlobalHttpConfig().configurationRetrofit(retrofitBuilder);
        // 当配置自定义解析器，多个解析器前面的return null，才会执行下一个解析器，抛出异常和进行处理
        // 不会执行到下一个解析器。所以GsonConverterFactory只能配置到最后，作为兜底
        // 配置兜底解析器（GsonConverterFactory能对任何数据进行转换，最多转换异常）
        retrofitBuilder.addConverterFactory(Utils.getApp().getGlobalHttpConfig().defaultConverterFactory());

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

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public OkHttpClient.Builder getOkHttpClickBuilder() {
        return urlOkHttpClientBuilder;
    }

}
