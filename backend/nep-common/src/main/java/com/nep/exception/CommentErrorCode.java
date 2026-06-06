package com.nep.exception;

import lombok.RequiredArgsConstructor;
import lombok.Getter;

/**
 * 评论错误码
 * @author Neptune
 * @date 2026-06-06
 */
@RequiredArgsConstructor
@Getter
public enum CommentErrorCode implements BaseErrorInfo {
    COMMENT_NOT_FOUND(40600, 404, "评论不存在"),
    COMMENT_DISABLED(40601, 403, "评论已被禁用");

    private final Integer code;
    private final Integer httpStatus;
    private final String message;
}
