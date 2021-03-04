package com.duxl.baselib.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间日期工具类
 * create by duxl 2021/3/4
 */
public class TimeUtils {


    /**
     * 常用的时间格式
     */
    public interface CommonPattern {
        String full = "yyyy-MM-dd HH:mm:ss";
        String full_date1 = "yyyy-MM-dd"; // 短横线隔开
        String full_date2 = "yyyy.MM.dd"; // 下点隔开
        String full_date3 = "yyyy·MM·dd"; // 中点隔开
        String full_date4 = "yyyy/MM/dd"; // 斜杠隔开
        String full_time = "HH:mm:ss";
    }

    /**
     * 格式化时间
     *
     * @param fromPattern 原时间格式
     * @param toPattern   格式化后的格式
     * @param fromTime    原时间字符串
     * @return 成功格式化返回格式化后的时间字符串，否则返回原时间字符串
     */
    public static String format(String fromPattern, String toPattern, String fromTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(toPattern);
            return sdf.format(parse(fromPattern, fromTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fromTime;
    }

    /**
     * 解析时间
     *
     * @param fromPattern 原时间格式
     * @param fromTime    原时间字符串
     * @return 成功解析返回Date，失败返回null
     */
    public static Date parse(String fromPattern, String fromTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(fromPattern);
            return sdf.parse(fromTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
