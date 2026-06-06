package com.nep.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.nep.constants.MessageConstant;

/**
 * 用户错误码
 * @author Neptune
 * @date 2026-06-06
 */
@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements BaseErrorInfo {
    USERNAME_OR_PASSWORD_ERROR(40100, 401, MessageConstant.USERNAME_OR_PASSWORD_ERROR),
    USER_DISABLED(40101, 403, MessageConstant.USER_DISABLED),
    USERNAME_EXISTS(40102, 409, MessageConstant.USERNAME_EXISTS),
    EMAIL_EXISTS(40103, 409, MessageConstant.EMAIL_EXISTS);

    private final Integer code;
    private final Integer httpStatus;
    private final String message;

}
