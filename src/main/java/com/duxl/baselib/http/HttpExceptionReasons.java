package com.duxl.baselib.http;

import com.duxl.baselib.R;
import com.duxl.baselib.utils.Utils;

/**
 * 请求网络失败原因
 * create by duxl 2021/1/18
 */
public interface HttpExceptionReasons {


    /**
     * 解析数据出错
     */
    HttpExceptionReason PARSE_ERROR = new HttpExceptionReason() {
        @Override
        public int getCode() {
            return Utils.getApp().getResources().getInteger(R.integer.http_exception_parse_error_code);
        }

        @Override
        public String getMsg() {
            return Utils.getApp().getString(R.string.http_exception_parse_error_msg);
        }
    };


    /**
     * 网络问题
     */
    HttpExceptionReason BAD_NETWORK = new HttpExceptionReason() {
        @Override
        public int getCode() {
            return Utils.getApp().getResources().getInteger(R.integer.http_exception_bad_network_code);
        }

        @Override
        public String getMsg() {
            return Utils.getApp().getString(R.string.http_exception_bad_network_msg);
        }
    };

    /**
     * 连接错误
     */
    HttpExceptionReason CONNECT_ERROR = new HttpExceptionReason() {
        @Override
        public int getCode() {
            return Utils.getApp().getResources().getInteger(R.integer.http_exception_connect_error_code);
        }

        @Override
        public String getMsg() {
            return Utils.getApp().getString(R.string.http_exception_connect_error_msg);
        }
    };

    /**
     * 连接超时
     */
    HttpExceptionReason CONNECT_TIMEOUT = new HttpExceptionReason() {
        @Override
        public int getCode() {
            return Utils.getApp().getResources().getInteger(R.integer.http_exception_connect_timeout_code);
        }

        @Override
        public String getMsg() {
            return Utils.getApp().getString(R.string.http_exception_connect_timeout_msg);
        }
    };

    /**
     * 没有连接网络（没有开wifi和移动网络）
     */
    HttpExceptionReason CONNECT_NO = new HttpExceptionReason() {
        @Override
        public int getCode() {
            return Utils.getApp().getResources().getInteger(R.integer.http_exception_connect_no_code);
        }

        @Override
        public String getMsg() {
            return Utils.getApp().getString(R.string.http_exception_connect_no_msg);
        }
    };

    /**
     * 未知错误
     */
    HttpExceptionReason UNKNOWN_ERROR = new HttpExceptionReason() {
        @Override
        public int getCode() {
            return Utils.getApp().getResources().getInteger(R.integer.http_exception_unknown_error_code);
        }

        @Override
        public String getMsg() {
            return Utils.getApp().getString(R.string.http_exception_unknown_error_msg);
        }
    };
}
