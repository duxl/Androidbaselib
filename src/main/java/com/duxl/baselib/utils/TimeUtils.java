package com.duxl.baselib.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
    public static CharSequence format(CharSequence fromPattern, CharSequence toPattern, CharSequence fromTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(toPattern.toString());
            return sdf.format(parse(fromPattern, fromTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fromTime;
    }

    /**
     * 格式化时间
     *
     * @param toPattern 格式化后的格式
     * @param fromTime  原时间
     * @return 成功格式化返回格式化后的时间字符串，否则返回原时间字符串
     */
    public static CharSequence format(CharSequence toPattern, long fromTime) {
        return format(toPattern, new Date(fromTime));
    }

    /**
     * 格式化时间
     *
     * @param toPattern 格式化后的格式
     * @param fromTime  原时间
     * @return 成功格式化返回格式化后的时间字符串，否则返回原时间字符串
     */
    public static CharSequence format(CharSequence toPattern, Date fromTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(toPattern.toString());
            return sdf.format(fromTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(fromTime.getTime());
    }

    /**
     * 解析时间
     *
     * @param fromPattern 原时间格式
     * @param fromTime    原时间字符串
     * @return 成功解析返回Date，失败返回null
     */
    public static Date parse(CharSequence fromPattern, CharSequence fromTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(fromPattern.toString());
            return sdf.parse(fromTime.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * UTC世界标准时间转化为本地时间
     *
     * @param utcTime   utc世界时间，格式例：2023-07-12T06:27:35.000+00:00
     * @param toPattern 转成本地时间的格式
     * @return
     */
    public static String utcFormat(String utcTime, String toPattern) {
        String utcPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
        return utcFormat(utcTime, utcPattern, toPattern);
    }

    /**
     * UTC世界标准时间转化为本地时间
     *
     * @param utcTime    utc世界时间
     * @param utcPattern utc时间格式，例：yyyy-MM-dd'T'HH:mm:ss.SSSXXX
     * @param toPattern  转成本地时间的格式
     * @return
     */
    public static String utcFormat(String utcTime, String utcPattern, String toPattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(utcPattern);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(utcTime);

            sdf.applyPattern(toPattern);
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化成指定时区的格式化时间
     *
     * @param toPattern 格式化后的格式
     * @param toZone    指定目标时区，eg：美国洛杉矶时区 TimeZone.getTimeZone("America/Los_Angeles")，
     *                  其它时区可参考：http://worldtimeapi.org/api/timezone
     * @param time      时间错
     * @return 成功格式化返回格式化后的时间字符串，否则返回原时间字符串
     */
    public static String format(String toPattern, TimeZone toZone, long time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(toPattern);
            sdf.setTimeZone(toZone);
            return sdf.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(time);
    }

    /**
     * 格式化成指定时区的格式化时间
     *
     * @param toPattern 格式化后的格式
     * @param toZone    指定目标时区，eg：美国洛杉矶时区 America/Los_Angeles，
     *                  其它时区可参考：http://worldtimeapi.org/api/timezone
     * @param time      时间错
     * @return 成功格式化返回格式化后的时间字符串，否则返回原时间字符串
     */
    public static String format(String toPattern, String toZone, long time) {
        return format(toPattern, TimeZone.getTimeZone(toZone), time);
    }
}
