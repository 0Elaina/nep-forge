package com.nep.hardware.dto;

import java.io.Serializable;
import java.io.Serial;
import java.math.BigDecimal;

import com.nep.common.constants.MessageConstant;
import com.nep.common.constants.ValidationConstant;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 硬件分页查询请求 DTO。
 * 封装硬件列表查询的所有筛选条件，包含分页参数、关键词搜索、
 * 分类筛选、品牌筛选、价格区间以及排序规则。
 * 使用 jakarta.validation 注解约束各参数合法范围。
 */
@Data
public class HardwareQueryRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码，从 1 开始。默认由调用方自行处理 null 时的默认值（通常为 1）。
     */
    @Min(
        value = ValidationConstant.HARDWARE_PAGE_NUM_MIN,
        message = MessageConstant.HARDWARE_PAGE_NUM_MIN_LIMIT
    )
    private Integer pageNum;

    /**
     * 每页记录数。允许范围 [1, 100]。
     */
    @Min(
        value = ValidationConstant.HARDWARE_PAGE_SIZE_MIN,
        message = MessageConstant.HARDWARE_PAGE_SIZE_MIN_LIMIT
    )
    @Max(
        value = ValidationConstant.HARDWARE_PAGE_SIZE_MAX,
        message = MessageConstant.HARDWARE_PAGE_SIZE_MAX_LIMIT
    )
    private Integer pageSize;

    /**
     * 搜索关键词，支持模糊匹配硬件名称/描述等文本字段。
     * 为 null 或空字符串时不参与筛选。
     */
    private String keyword;

    /**
     * 硬件分类 ID，用于按分类筛选。
     * 为 null 时不按分类过滤。
     */
    private Integer categoryId;

    /**
     * 品牌名称，用于按品牌精确筛选。
     * 为 null 时不参与筛选。
     */
    private String brand;

    /**
     * 最低价格（含），范围筛选的下界。
     * 为 null 时表示不设价格下限。
     */
    @DecimalMin(
        value = ValidationConstant.HARDWARE_MIN_PRICE_MIN,
        message = MessageConstant.HARDWARE_MIN_PRICE_MIN_LIMIT
    )
    private BigDecimal minPrice;

    /**
     * 最高价格（含），范围筛选的上界。
     * 为 null 时表示不设价格上限。
     */
    @DecimalMin(
        value = ValidationConstant.HARDWARE_MAX_PRICE_MIN,
        message = MessageConstant.HARDWARE_MAX_PRICE_MIN_LIMIT
    )
    private BigDecimal maxPrice;

    /**
     * 排序字段，可选值：price — 按价格排序，createTime — 按创建时间排序，releaseTime — 按发布时间排序。
     */
    private String sortField;

    /**
     * 排序方向：asc — 升序排列，desc — 降序排列。
     */
    private String sortOrder;

}
