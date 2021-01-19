package com.duxl.baselib.http;

/**
 * BaseRoot
 * create by duxl 2020/8/18
 */
public interface BaseRoot {

    /**
     * 业务code
     *
     * @return
     */
    int getCode();

    /**
     * 业务Msg
     *
     * @return
     */
    String getMsg();

    /**
     * 是否成功
     *
     * @return
     */
    boolean isSuccess();
}
