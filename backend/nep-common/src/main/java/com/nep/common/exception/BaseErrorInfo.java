package com.nep.common.exception;

/**
 * 基础错误信息接口
 * @author Neptune
 * @date 2026-06-06
 */
public interface BaseErrorInfo {
    /**
     * 获取错误码
     * @return 错误码
     */
    Integer getCode();

    /**
     * 获取HTTP状态码
     * @return HTTP状态码
     */
    Integer getHttpStatus();
    /**
     * 获取错误信息
     * @return 错误信息
     */
    String getMessage();
}
