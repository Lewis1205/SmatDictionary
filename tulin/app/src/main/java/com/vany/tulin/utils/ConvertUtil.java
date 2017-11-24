package com.vany.tulin.utils;

/**
 * Created by van元 on 2017/1/26.
 */

public class ConvertUtil {

    /**
     * 将9转换成z
     * @param word
     * @return
     */
    public static String convert9toz(String word) {
       return word.replaceAll("9+", "z");
    }

    /**
     * 将所有大写字母转成小写字母
     * @param word
     * @return
     */
    public static String convertUper2Lower(String word) {
        return word.toLowerCase();
    }

    /**
     * 判断是否为中文
     * @param word
     * @return
     */
    public static boolean isChinese(String word) {
       return word.matches("[\\u4e00-\\u9fa5]+");
    }
}
