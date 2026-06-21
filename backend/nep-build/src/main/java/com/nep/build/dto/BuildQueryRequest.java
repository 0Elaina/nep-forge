package com.nep.build.dto;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import com.nep.common.constants.ValidationConstant;
import com.nep.common.constants.MessageConstant;
import lombok.Data;


/**
 * 装机单查询请求
 * 用于查询装机单列表
 */
@Data
public class BuildQueryRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 页码, 默认第一页
     */
    @Min(
        value = ValidationConstant.PAGE_NUM_MIN,
        message = MessageConstant.PAGE_NUM_MIN_LIMIT
    )
    private Integer pageNum;

    /**
     * 每页数量, 默认10条
     */
    @Min(
        value = ValidationConstant.PAGE_SIZE_MIN,
        message = MessageConstant.PAGE_SIZE_MIN_LIMIT
    )
    @Max(
        value = ValidationConstant.PAGE_SIZE_MAX,
        message = MessageConstant.PAGE_SIZE_MAX_LIMIT
    )
    private Integer pageSize;

    /**
     * 标题关键词
     * 公开装机单列表使用
     */
    private String keyword;

    /**
     * 装机单状态: 0 草稿, 1 正常, 2 下架
     * 我的装机单列表使用
     */
    private Integer status;

    /**
     * 排序字段
     * createTime / totalPrice / totalPower
     */
    private String sortField;

    /**
     * 排序方式
     * asc / desc
     */
    private String sortOrder;
}
