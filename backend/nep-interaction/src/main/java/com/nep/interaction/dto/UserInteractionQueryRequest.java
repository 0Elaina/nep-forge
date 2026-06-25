package com.nep.interaction.dto;

import java.io.Serial;
import java.io.Serializable;

import com.nep.common.constants.MessageConstant;
import com.nep.common.constants.QueryConstant;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 用户交互查询请求DTO
 */
@Data
public class UserInteractionQueryRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Min(value = QueryConstant.PAGE_NUM_MIN, message = MessageConstant.PAGE_NUM_MIN_LIMIT)
    private Integer pageNum;

    @Min(value = QueryConstant.PAGE_SIZE_MIN, message = MessageConstant.PAGE_SIZE_MIN_LIMIT)
    @Max(value = QueryConstant.PAGE_SIZE_MAX, message = MessageConstant.PAGE_SIZE_MAX_LIMIT)
    private Integer pageSize;

    // 目标类型 1: 文章 2: 配件 3: 装机单 4: 评论
    private Integer targetType;

    // 收藏夹Id, 点赞时默认为0
    private Long folderId;
}
