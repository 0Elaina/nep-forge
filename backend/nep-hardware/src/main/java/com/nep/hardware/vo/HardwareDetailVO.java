package com.nep.hardware.vo;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * 配件详情VO
 * 
 * 用于展示配件的详细信息，包括分类、品牌、价格、封面图片、发布时间、来源、规格差异化参数、是否点赞、是否收藏、创建时间等。
 */
@Data
@Builder
public class HardwareDetailVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 配件ID
    private String id;

    // 分类ID
    private Integer categoryId;
    
    // 分类名称
    private String categoryName;

    // 配件名称
    private String name;

    // 品牌
    private String brand;

    // 价格
    private BigDecimal price;

    // 封面图片URL
    private String coverImage;

    // 来源名称
    private String sourceName;

    // 来源URL
    private String sourceUrl;

    // 发布时间
    private LocalDate releaseDate;

    // 上次同步时间
    private LocalDateTime lastSyncTime;

    // 规格差异化参数
    private Map<String, Object> specs;

    // 是否点赞
    private Boolean liked;

    // 是否收藏
    private Boolean favorited;

    // 创建时间
    private LocalDateTime createTime;
}
