package com.nep.common.util;

import org.springframework.util.StringUtils;

/**
 * 字符串工具类
 */
public class StringCommonUtils {
    private StringCommonUtils(){}

    /**
     * 去除字符串首尾空格，若字符串为空或仅包含空格，则返回null
     * 
     * @param value 输入字符串
     * @return 处理后的字符串
     */
    public static String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
