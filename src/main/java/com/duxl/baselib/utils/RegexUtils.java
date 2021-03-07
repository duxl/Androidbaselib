package com.duxl.baselib.utils;

import com.duxl.baselib.R;

/**
 * 正则表达式工具类
 */
public class RegexUtils {


    public static void main(String[] args) {
        System.out.println(verify("12", "^\\d{2}$")); // 2位数字
        System.out.println(verify("abcD", "^[a-z,A-Z]*$")); // 任意个数的字母
        System.out.println(verify("正则表达式", "^[\\u4E00-\\u9FA5]+$")); // 1个或多个汉字
    }

    /**
     * 常用正则表达式。
     * 如果需要修改验证规则，可以在主module中重新定义正则表达式覆盖lib中的规则
     */
    public interface RegexConstants {
        /***  手机号码 */
        String MOBILE_NUM = Utils.getContext().getString(R.string.regex_mobile_no);
        /*** 邮箱 */
        String EMAIL = Utils.getContext().getString(R.string.regex_email);
        /*** url地址 */
        String URL = Utils.getContext().getString(R.string.regex_url);
        /*** ip地址 */
        String IP = Utils.getContext().getString(R.string.regex_ip);
        /*** 身份证号码（粗验证：只验证格式，最后一位校验码不一定正确） */
        String ID_CARD = Utils.getContext().getString(R.string.regex_id_card);
        /*** 纯数字 */
        String DIGITAL = Utils.getContext().getString(R.string.regex_digital);
        /*** 任意字母，不区分大小写 */
        String LETTER = Utils.getContext().getString(R.string.regex_letter);
        /*** 全小写字母 */
        String LETTER_LOWER = Utils.getContext().getString(R.string.regex_letter_lower);
        /*** 全大写字母 */
        String LETTER_UPPER = Utils.getContext().getString(R.string.regex_letter_upper);
        /*** 全汉字 */
        String CHINESE = Utils.getContext().getString(R.string.regex_chinese);
    }

    /**
     * 正则验证
     *
     * @param text  要验证的内容
     * @param regex 正则表达式
     * @return 通过true，否则false
     */
    public static boolean verify(CharSequence text, CharSequence regex) {
        if (text != null) {
            return text.toString().matches(regex.toString());
        }
        return false;
    }

}
