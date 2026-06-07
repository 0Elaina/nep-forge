package com.nep.common.exception;

import lombok.RequiredArgsConstructor;

import com.nep.common.constants.MessageConstant;

import lombok.Getter;

/**
 * 评论错误码
 * @author Neptune
 * @date 2026-06-06
 */
@RequiredArgsConstructor
@Getter
public enum CommentErrorCode implements BaseErrorInfo {
    COMMENT_NOT_FOUND(40600, 404, MessageConstant.COMMENT_NOT_FOUND),
    COMMENT_DISABLED(40601, 403, MessageConstant.COMMENT_DISABLED);

    private final Integer code;
    private final Integer httpStatus;
    private final String message;
}
