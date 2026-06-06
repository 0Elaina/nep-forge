package com.nep.exception;

import lombok.RequiredArgsConstructor;
import lombok.Getter;

/**
 * 交互错误码
 * @author Neptune
 * @date 2026-06-06
 */
@RequiredArgsConstructor
@Getter
public enum InteractionErrorCode implements BaseErrorInfo {
    FAVORITE_FOLDER_NOT_FOUND(40400, 404, "收藏夹不存在"),
    ALREADY_LIKED(40401, 409, "已点赞, 请勿重复操作"),
    ALREADY_FAVORITED(40402, 409, "已收藏, 请勿重复操作");

    private final Integer code;
    private final Integer httpStatus;
    private final String message;
}
