package com.duxl.baselib.utils;

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.duxl.baselib.R;

import java.util.Collection;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 16/12/08
 *     desc  : Utils初始化相关
 * </pre>
 */
public class Utils {

    private static Context context;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        Utils.context = context;
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) {
            return context;
        }
        throw new NullPointerException("u should init first");
    }

    /**
     * get App versionCode
     *
     * @return
     */
    public static int getVersionCode() {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        int versionCode = 0;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * get App versionName
     *
     * @return
     */
    public static String getVersionName() {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 是否是中文
     *
     * @param c
     * @return
     */
    public static boolean IsChinese(char c) {
        //通过字节码进行判断
        return c >= 0x4E00 && c <= 0x9FA5;
    }

    /**
     * 列表转字符串
     *
     * @param list      字符串列表
     * @param delimiter 连接符
     * @return
     */
    public static String listToString(List<String> list, String delimiter) {
        if (list == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String str : list) {
            if (flag) {
                result.append(delimiter);
            } else {
                flag = true;
            }
            result.append(str);
        }
        return result.toString();
    }

    /**
     * 用指定的分隔符分割手号码，前3位一组，中间4位一组，后4位一组。
     * 例如手机号：13577778888，分隔符：-，格式化后：135-7777-8888
     *
     * @param mobileNum 手机号
     * @param delimiter 分隔符
     * @return
     */
    public static String mobileFormat(String mobileNum, String delimiter) {
        if (EmptyUtils.isNotNull(mobileNum) && mobileNum.length() == 11) {
            return mobileNum.substring(0, 3) + delimiter + mobileNum.substring(3, 7) + delimiter + mobileNum.substring(7);

        } else {
            return mobileNum;
        }

    }

    /**
     * <pre>
     * 手机号脱敏，将手机号后中间4位用指定的密文符替换
     * 例如手机号：13577778888，密文符：*，脱敏后：135****8888
     * <pre/>
     * @param mobileNum  手机号码
     * @param ciphertext 密文符
     * @return 成功加敏返回加敏后的字符串，否则原样返回
     */
    public static String mobileDesensitization(String mobileNum, String ciphertext) {
        if (EmptyUtils.isNotNull(mobileNum) && mobileNum.length() == 11) {
            return mobileNum.substring(0, 3) + ciphertext + ciphertext + ciphertext + ciphertext + mobileNum.substring(7);

        } else {
            return mobileNum;
        }
    }

    /**
     * 是否是合法的手机号码，验证规则在string.xml字符串资源里面制定，子module可以更改规则
     *
     * @param mobileNum
     * @return
     */
    public static boolean isMobileNum(String mobileNum) {
        /**
         * 判断字符串是否符合手机号码格式
         * 移动号段: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
         * 联通号段: 130,131,132,145,155,156,170,171,175,176,185,186
         * 电信号段: 133,149,153,170,173,177,180,181,189
         * @param str
         * @return 待检测的字符串
         */
        // "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        // ^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\d{8}$
        String telRegex = getContext().getString(R.string.tel_regex);
        if (TextUtils.isEmpty(mobileNum)) {
            return false;
        } else {
            return mobileNum.matches(telRegex);
        }
    }

    /**
     * <pre>
     *     名字脱敏，将姓名用指定的密文符替换
     *     如果是两个字，将第一个字加敏，例如“张三”加敏后“*三”
     *     如果是三个或多个字，将中间的字加敏，例如“王朝马汉”加敏后“王**汉”
     * </pre>
     *
     * @param name       姓名
     * @param ciphertext 密文符
     * @return 成功加敏返回加敏后的字符串，否则原样返回
     */
    public static String nameDesensitization(String name, String ciphertext) {
        if (EmptyUtils.isNotNull(name)) {
            try {
                if (name.length() == 2) {
                    return ciphertext + name.substring(1);
                } else if (name.length() > 2) {
                    StringBuilder sb = new StringBuilder(name.substring(0, 1));
                    for (int i = 1; i < name.length() - 1; i++) {
                        sb.append(ciphertext);
                    }
                    sb.append(name.substring(name.length() - 2, name.length() - 1));
                    return sb.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return name;
    }

    /**
     * 复制到剪切板
     *
     * @param text
     * @return
     */
    public static String copyAndPaste(String text) {
        ClipboardManager myClipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);
        return text;
    }

    public static String getPrintSize(long size) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
        }
    }


    /**
     * 用来判断服务是否运行.
     *
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                getContext().getSystemService(Context.ACTIVITY_SERVICE);
        //此处只在前30个中查找，大家根据需要调整
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    //boolean hasInstalled = AppUtils.checkHasInstalledApp(context, "com.xunmeng.pinduoduo");
    public static boolean checkHasInstalledApp(String pkgName) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(pkgName, PackageManager.GET_GIDS);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        } catch (RuntimeException e) {
            app_installed = false;
        } catch (Exception e) {
            app_installed = false;
        }
        return app_installed;
    }

    /**
     * 获取app当前的渠道号或application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值 ， 或者异常)，则返回值为空
     */
    public static String getAppMetaData(String key) {
        if (context == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String channelNumber = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelNumber = applicationInfo.metaData.getString(key);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return channelNumber;
    }

    public static String getAndroidId() {
        return Settings.Secure.getString(Utils.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

    }
}


