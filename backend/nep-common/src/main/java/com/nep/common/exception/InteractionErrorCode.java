package com.nep.common.exception;

import lombok.RequiredArgsConstructor;

import com.nep.common.constants.MessageConstant;

import lombok.Getter;

/**
 * 交互错误码
 * @author Neptune
 * @date 2026-06-06
 */
@RequiredArgsConstructor
@Getter
public enum InteractionErrorCode implements BaseErrorInfo {
    FAVORITE_FOLDER_NOT_FOUND(40400, 404, MessageConstant.FAVORITE_FOLDER_NOT_FOUND),
    ALREADY_LIKED(40401, 409, MessageConstant.ALREADY_LIKED),
    ALREADY_FAVORITED(40402, 409, MessageConstant.ALREADY_FAVORITED);

    private final Integer code;
    private final Integer httpStatus;
    private final String message;
}
