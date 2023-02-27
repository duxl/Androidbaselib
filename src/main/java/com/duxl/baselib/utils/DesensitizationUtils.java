package com.duxl.baselib.utils;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 脱敏工具类
 * create by duxl 2021/3/8
 */
public class DesensitizationUtils {
    /**
     * <pre>
     * 手机号脱敏，将手机号后中间4位用指定的密文符替换
     * 例如手机号：13577778888，密文符：*，脱敏后：135****8888
     * <pre/>
     * @param mobileNum  手机号码
     * @param cipherText 密文符
     * @return 成功加敏返回加敏后的字符串，否则原样返回
     */
    public static CharSequence formatMobile(CharSequence mobileNum, CharSequence cipherText) {
        if (EmptyUtils.isNotNull(mobileNum) && mobileNum.length() == 11) {
            return format(mobileNum, cipherText, 3, 4);

        } else {
            return mobileNum;
        }
    }

    /**
     * <pre>
     *     名字脱敏，将姓名用指定的密文符替换
     *     如果是小于等于两个字，将第一个字加敏，例如“张三”加敏后“*三”
     *     如果是三个或多个字，将中间的字加敏，例如“王朝马汉”加敏后“王**汉”
     * </pre>
     *
     * @param name       姓名
     * @param cipherText 密文符
     * @return 成功加敏返回加敏后的字符串，否则原样返回
     */
    public static CharSequence formatName(CharSequence name, CharSequence cipherText) {
        if (EmptyUtils.isNotNull(name)) {
            try {
                if (name.length() == 1) {
                    return cipherText.toString();
                } else if (name.length() == 2) {
                    return cipherText.toString() + name.subSequence(1, 2);
                } else if (name.length() > 2) {
                    StringBuilder sb = new StringBuilder(name.subSequence(0, 1));
                    for (int i = 1; i < name.length() - 1; i++) {
                        sb.append(cipherText);
                    }
                    sb.append(name.subSequence(name.length() - 1, name.length()));
                    return sb.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return name;
    }

    /**
     * 字符串脱敏
     *
     * @param text        要脱敏的字符串
     * @param cipherText  密文符
     * @param startLength 开始多少位不脱敏
     * @param endLength   结束多少位不脱敏
     * @return
     */
    public static CharSequence format(CharSequence text, CharSequence cipherText, int startLength, int endLength) {
        return format(text, cipherText, -1, startLength, endLength);
    }

    /**
     * 字符串脱敏
     *
     * @param text         要脱敏的字符串
     * @param cipherText   密文符
     * @param cipherLength 密文符长度：<=0时替换了后多少位就显示多少个密文符，>0显示固定长度的密文符号
     * @param startLength  开始多少位不脱敏
     * @param endLength    结束多少位不脱敏
     * @return
     */
    public static CharSequence format(CharSequence text, CharSequence cipherText, int cipherLength, int startLength, int endLength) {
        if (EmptyUtils.isNotNull(text)) {
            try {
                if (startLength < 0 || endLength < 0) {
                    return text;
                }

                if (startLength + endLength > text.length()) {
                    return text;
                }

                StringBuilder sb = new StringBuilder();

                if (text.length() >= startLength) {
                    sb.append(text.subSequence(0, startLength));
                }

                if (cipherLength <= 0) {
                    for (int i = 0; i < text.length() - endLength - startLength; i++) {
                        sb.append(cipherText);
                    }
                } else {
                    for (int i = 0; i < cipherLength; i++) {
                        sb.append(cipherText);
                    }
                }

                if (text.length() - endLength > 0) {
                    sb.append(text.subSequence(text.length() - endLength, text.length()));
                }

                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return text;
    }
}
