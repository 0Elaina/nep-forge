package com.nep.common.exception;

import lombok.RequiredArgsConstructor;

import com.nep.common.constants.MessageConstant;

import lombok.Getter;

/**
 * 文章错误码
 * @author Neptune
 * @date 2026-06-06
 */
@RequiredArgsConstructor
@Getter
public enum ArticleErrorCode implements BaseErrorInfo {
    ARTICLE_NOT_FOUND(40500, 404, MessageConstant.ARTICLE_NOT_FOUND),
    ARTICLE_NOT_PUBLISHED(40501, 403, MessageConstant.ARTICLE_NOT_PUBLISHED),
    TAG_NOT_FOUND(40502, 404, MessageConstant.TAG_NOT_FOUND);

    private final Integer code;
    private final Integer httpStatus;
    private final String message;
}
