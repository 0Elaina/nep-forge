package com.nep.exception;

import lombok.Getter;


/**
 * 通用异常类
 * @author Neptune
 * @date 2026-06-06
 */
@Getter
public class CommonException extends RuntimeException {
    private final BaseErrorInfo errorInfo;

    /**
     * 接收基础错误信息
     * @param errorInfo 错误信息
     */
    public CommonException(BaseErrorInfo errorInfo) {
        super(errorInfo.getMessage());
        this.errorInfo = errorInfo;
    }

    public CommonException(BaseErrorInfo errorInfo, String message) {
        super(message);
        this.errorInfo = errorInfo;
    }

    /**
     * 接收基础错误信息 + 覆盖原有的错误信息 (适用于需要补充详细报错的情况)
     * @param errorInfo 错误信息
     * @param message 错误信息
     * @param cause 异常
     */
    public CommonException(BaseErrorInfo errorInfo, Throwable cause, String message) {
        super(message, cause);
        this.errorInfo = errorInfo;
    }
}
