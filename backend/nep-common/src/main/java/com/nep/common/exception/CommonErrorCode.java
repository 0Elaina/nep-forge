package com.nep.common.exception;

import com.nep.common.constants.MessageConstant;

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

    SUCCESS(0, 200, MessageConstant.SUCCESS),
    REQUEST_PARAM_ERROR(40000, 400, MessageConstant.REQUEST_PARAM_ERROR),
    UNAUTHORIZED(40001, 401, MessageConstant.UNAUTHORIZED),
    FORBIDDEN(40003, 403, MessageConstant.FORBIDDEN),
    NOT_FOUND(40004, 404, MessageConstant.NOT_FOUND),
    CONFLICT(40009, 409, MessageConstant.CONFLICT),
    TOO_MANY_REQUESTS(42900, 429, MessageConstant.TOO_MANY_REQUESTS),
    SYSTEM_ERROR(50000, 500, MessageConstant.SYSTEM_ERROR);

    private final Integer code;
    private final Integer httpStatus;
    private final String message;

}
