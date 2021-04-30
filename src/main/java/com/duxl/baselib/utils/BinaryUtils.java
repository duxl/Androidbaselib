package com.duxl.baselib.utils;

/**
 * 二进制位运算工具
 * create by duxl 2021/4/30
 */
public class BinaryUtils {
    public static void main(String[] args) {
        // 1、将1001(9)的position=1的位置设置成1，即1011(11)
        int original = 9;
        int result = setBit(original, 1, 1);
        print(original, result); // 输出结果：1001(9) → 1011(11)

        // 2、将111(7)的position=0的位置设置成0，即110(6)
        original = 7;
        result = setBit(original, 0, 0);
        print(original, result); // 输出结果：111(7) → 110(6)

        // 3、将101(5)的position=1的位置设置成0，即101(5)
        original = 5;
        result = setBit(original, 1, 0);
        print(original, result); // 输出结果：101(5) → 101(5)

    }

    private static void print(int original, int result) {
        System.out.println(String.format("%s(%d) → %s(%d)", toString(original), original, toString(result), result));
    }

    /**
     * 将一个数字的指定二进制位设置成对应的value
     *
     * @param original 原始数据
     * @param position 二进制位，从右到左，position >= 0
     * @param value    对应二进制位的数据, 非0即1
     * @return
     */
    public static int setBit(int original, int position, int value) {
        if (position < 0) {
            position = 0;
        }

        // value只能是0和1，如果不是0，那么只能是1
        if (value != 0) {
            value = 1;
        }

        if (value == 1) {
            return original | (1 << position);
        } else {
            // 先判断原来是不是0,原来是0则直接返回
            if (getBit(original, position) == 0) {
                return original;
            }
            return original - (1 << position);
        }
    }

    /**
     * 获取一个数字的指定二进制位
     *
     * @param original
     * @param position
     * @return
     */
    public static int getBit(int original, int position) {
        return original >> position & 1;
    }

    /**
     * 十进制数字转成二进制字符串
     *
     * @param original
     * @return
     */
    public static String toString(int original) {
        return Integer.toBinaryString(original);
    }

    /**
     * 将二进制字符串转成十进制数字
     *
     * @param binaryNum
     * @return
     * @throws NumberFormatException
     */
    public static int toInt(String binaryNum) throws NumberFormatException {
        return Integer.valueOf(binaryNum, 2);
    }
}