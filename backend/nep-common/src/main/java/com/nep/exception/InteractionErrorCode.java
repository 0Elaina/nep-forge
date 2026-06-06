package com.nep.exception;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import com.nep.constants.MessageConstant;

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
