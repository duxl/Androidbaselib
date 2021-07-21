package com.duxl.baselib.download;

import com.liulishuo.filedownloader.connection.FileDownloadConnection;
import com.liulishuo.filedownloader.util.FileDownloadHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp3Connection
 * create by duxl 2021/7/21
 */
public class OkHttp3Connection implements FileDownloadConnection {

    private OkHttpClient mClient;
    private Request.Builder mRequestBuilder;

    private Request mRequest;
    private Response mResponse;

    public OkHttp3Connection(String url, OkHttpClient client) {
        try {
            mRequestBuilder = new Request.Builder().url(url);
            mClient = client;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addHeader(String name, String value) {
        mRequestBuilder.addHeader(name, value);
    }

    @Override
    public boolean dispatchAddResumeOffset(String etag, long offset) {
        return false;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (mResponse == null) throw new IllegalStateException("Please invoke #execute first!");
        return mResponse.body().byteStream();
    }

    @Override
    public Map<String, List<String>> getRequestHeaderFields() {
        if (mRequest == null) {
            mRequest = mRequestBuilder.build();
        }

        return mRequest.headers().toMultimap();
    }

    @Override
    public Map<String, List<String>> getResponseHeaderFields() {
        return mResponse == null ? null : mResponse.headers().toMultimap();
    }

    @Override
    public String getResponseHeaderField(String name) {
        return mResponse == null ? null : mResponse.header(name);
    }

    @Override
    public boolean setRequestMethod(String method) throws ProtocolException {
        return true;
    }

    @Override
    public void execute() throws IOException {
        if (mRequest == null) {
            mRequest = mRequestBuilder.build();
        }

        mResponse = mClient.newCall(mRequest).execute();
    }

    @Override
    public int getResponseCode() throws IOException {
        if (mResponse == null) throw new IllegalStateException("Please invoke #execute first!");

        return mResponse.code();
    }

    @Override
    public void ending() {
        mRequest = null;
        mResponse = null;
    }

    /**
     * The creator for the connection implemented with the okhttp3.
     */
    public static class Creator implements FileDownloadHelper.ConnectionCreator {

        private OkHttpClient mClient;
        private OkHttpClient.Builder mBuilder;

        public Creator() {
        }

        /**
         * Create the Creator with the customized {@code client}.
         *
         * @param builder the builder for customizing the okHttp client.
         */
        public Creator(OkHttpClient.Builder builder) {
            mBuilder = builder;
        }

        @Override
        public OkHttp3Connection create(String url) throws IOException {
            if (mClient == null) {
                synchronized (Creator.class) {
                    if (mClient == null) {
                        mClient = mBuilder != null ? mBuilder.build() : new OkHttpClient();
                        mBuilder = null;
                    }
                }
            }
            return new OkHttp3Connection(url, mClient);
        }
    }
}
