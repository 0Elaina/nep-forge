package com.nep.common.exception;

import com.nep.common.constants.MessageConstant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 装机单错误码
 * @author Neptune
 * @date 2026-06-06
 */
@RequiredArgsConstructor
@Getter
public enum BuildErrorCode implements BaseErrorInfo {
    BUILD_NOT_FOUND(40300, 404, MessageConstant.BUILD_NOT_FOUND),
    BUILD_FORBIDDEN(40301, 403, MessageConstant.BUILD_FORBIDDEN),
    BUILD_HARDWARE_EXISTS(40302, 409, MessageConstant.BUILD_HARDWARE_EXISTS);

    private final Integer code;
    private final Integer httpStatus;
    private final String message;
}
