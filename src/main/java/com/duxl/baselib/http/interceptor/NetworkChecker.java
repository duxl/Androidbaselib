package com.duxl.baselib.http.interceptor;

/**
 * 网络连接监测器
 * create by duxl 2021/1/18
 */
public interface NetworkChecker {
    /**
     * 是否连接了网络
     * @return
     */
    boolean isConnected();
}
