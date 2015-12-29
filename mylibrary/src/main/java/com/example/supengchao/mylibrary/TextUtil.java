package com.example.supengchao.mylibrary;

/**
 * 文本工具类
 *
 * @author wangzengyang 2012-11-19
 */
public class TextUtil {



    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        str = str.trim();
        return str.length() == 0 || str.equals("null");
    }



    /**
     * 比较两个字符串是否相同
     *
     * @param first
     * @param second
     * @return
     */
    public static boolean equals(String first, String second) {
        if (isEmpty(first) || isEmpty(second))
            return false;
        return first.trim().equals(second.trim());
    }

}
