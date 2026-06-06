package com.nep.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 通用错误码
 * @author Neptune
 * @date 2026-06-06
 */
@RequiredArgsConstructor
@Getter
public enum CommonErrorCode implements BaseErrorInfo {

    SUCCESS(0, 200, "成功"),
    REQUEST_PARAM_ERROR(40000, 400, "请求参数错误"),
    UNAUTHORIZED(40001, 401, "未登录或登录已过期"),
    FORBIDDEN(40003, 403, "无权限访问"),
    NOT_FOUND(40004, 404, "资源不存在"),
    CONFLICT(40009, 409, "数据冲突"),
    TOO_MANY_REQUESTS(42900, 429, "请求过于频繁"),
    SYSTEM_ERROR(50000, 500, "系统内部错误");

    private final Integer code;
    private final Integer httpStatus;
    private final String message;

}
