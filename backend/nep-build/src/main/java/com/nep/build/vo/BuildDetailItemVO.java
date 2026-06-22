package com.nep.build.vo;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 装机单配件详情项VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildDetailItemVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 装机单详情ID
    private String detailId;

    // 配件ID
    private String hardwareId;
    
    // 配件名称
    private String name;

    // 配件品牌
    private String brand;

    // 配件分类ID
    private Integer categoryId;

    // 配件分类名称
    private String categoryName;

    // 价格
    private BigDecimal price;

    // 数量
    private Integer quantity;

    // 总金额
    private BigDecimal subTotal;

    // 配件封面图片
    private String coverImage;

    // 规格
    private Object specs;
}
