package com.nep.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.nep.constants.MessageConstant;

/**
 * 配件错误码
 * @author Neptune
 * @date 2026-06-06
 */
@RequiredArgsConstructor
@Getter
public enum HardwareErrorCode implements BaseErrorInfo {
    HARDWARE_NOT_FOUND(40200, 404, MessageConstant.HARDWARE_NOT_FOUND),
    HARDWARE_CATEGORY_NOT_FOUND(40201, 404, MessageConstant.HARDWARE_CATEGORY_NOT_FOUND);

    private final Integer code;
    private final Integer httpStatus;
    private final String message;
}
