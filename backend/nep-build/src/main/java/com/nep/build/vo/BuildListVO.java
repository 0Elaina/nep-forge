package com.nep.build.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.io.Serial;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * 装机单列表VO
 * 用于展示装机单列表
 */
@Data
@Builder
public class BuildListVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 装机单ID
    private String id;
    
    // 标题
    private String title;

    // 总价格
    private BigDecimal totalPrice;

    // 总功耗
    private BigDecimal totalPower;

    // 是否公开
    private Boolean isPublic;

    // 装机单状态
    private Integer status;

    // 封面图片
    private String coverImage;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;
}
