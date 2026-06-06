package com.nep.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 用户错误码
 * @author Neptune
 * @date 2026-06-06
 */
@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements BaseErrorInfo {
    USERNAME_OR_PASSWORD_ERROR(40100, 401, "用户名或密码错误"),
    USER_DISABLED(40101, 403, "用户已被删除或不可用"),
    USERNAME_EXISTS(40102, 409, "用户名已存在"),
    EMAIL_EXISTS(40103, 409, "邮箱已存在");

    private final Integer code;
    private final Integer httpStatus;
    private final String message;

}
