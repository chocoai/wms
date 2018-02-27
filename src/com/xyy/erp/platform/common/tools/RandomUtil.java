package com.xyy.erp.platform.common.tools;

import java.util.Random;

/**
 * 随机数工具类
 *
 * @author caofei
 */
public class RandomUtil {

    private static final String numberChar = "0123456789";

//    private static final String mixedChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String mixedChar = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ0123456789";

    /**
     * 获取指定长度随机数-纯数字
     *
     * @param length
     * @return
     */
    public static String getRandomCode(int length) {
        Long seed = System.currentTimeMillis();// 获得系统时间，作为生成随机数的种子
        StringBuffer sb = new StringBuffer();// 装载生成的随机数
        Random random = new Random(seed);// 调用种子生成随机数
        for (int i = 0; i < length; i++) {
            sb.append(numberChar.charAt(random.nextInt(numberChar.length())));
        }
        Integer i = Double.valueOf(
                Math.random() * Double.valueOf(sb.toString()).doubleValue())
                .intValue();
        StringBuffer buffer = new StringBuffer();
        buffer.append(i.toString());
        if (i.toString().length() < length) {
            int s = length - i.toString().length();
            for (int j = 0; j < s; j++) {
                buffer.append(j);
            }
        }
        return buffer.toString();
    }

    /**
     * 获取指定长度随机数-字母数字混合型
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(mixedChar.charAt(number));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(getRandomString(6));
        System.out.println(getRandomCode(6));
    }

}
