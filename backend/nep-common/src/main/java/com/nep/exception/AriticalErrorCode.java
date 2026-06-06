package com.nep.exception;

import lombok.RequiredArgsConstructor;
import lombok.Getter;

/**
 * 文章错误码
 * @author Neptune
 * @date 2026-06-06
 */
@RequiredArgsConstructor
@Getter
public enum AriticalErrorCode implements BaseErrorInfo {
    ARTICLE_NOT_FOUND(40500, 404, "文章不存在"),
    ARTICLE_NOT_PUBLISHED(40501, 403, "文章未发布或已下架"),
    TAG_NOT_FOUND(40502, 404, "标签不存在");

    private final Integer code;
    private final Integer httpStatus;
    private final String message;
}
