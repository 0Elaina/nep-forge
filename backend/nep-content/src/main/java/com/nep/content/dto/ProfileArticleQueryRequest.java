package com.nep.content.dto;

import java.io.Serializable;

import com.nep.common.constants.MessageConstant;
import com.nep.common.constants.QueryConstant;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 个人中心文章查询请求DTO
 */
@Data
public class ProfileArticleQueryRequest implements Serializable {
    
    // 分页页码
    @Min(value = QueryConstant.PAGE_NUM_MIN, message = MessageConstant.PAGE_NUM_MIN_LIMIT)
    private Integer pageNum = QueryConstant.DEFAULT_PAGE_NUM;

    // 分页每页数量
    @Min(value = QueryConstant.PAGE_SIZE_MIN, message = MessageConstant.PAGE_SIZE_MIN_LIMIT)
    @Max(value = QueryConstant.PAGE_SIZE_MAX, message = MessageConstant.PAGE_SIZE_MAX_LIMIT)
    private Integer pageSize = QueryConstant.DEFAULT_PAGE_SIZE;

    // 文章状态 0: 草稿 1: 已发布 2: 已下架
    private Integer status;
}
