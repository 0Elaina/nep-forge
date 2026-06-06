package com.nep.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 配件错误码
 * @author Neptune
 * @date 2026-06-06
 */
@RequiredArgsConstructor
@Getter
public enum HardwareErrorCode implements BaseErrorInfo {
    HARDWARE_NOT_FOUND(40200, 404, "配件不存在"),
    HARDWARE_CATEGORY_NOT_FOUND(40201, 404, "配件分类不存在");

    private final Integer code;
    private final Integer httpStatus;
    private final String message;
}
