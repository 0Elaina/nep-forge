package com.nep.exception;

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
    BUILD_NOT_FOUND(40300, 404, "装机单不存在"),
    BUILD_FORBIDDEN(40301, 403, "无权访问该装机单"),
    BUILD_HARDWARE_EXISTS(40302, 409, "装机单中已存在该配件");

    private final Integer code;
    private final Integer httpStatus;
    private final String message;
}
