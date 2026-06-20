package com.nep.hardware.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

/**
 * 配件列表VO
 */
@Data
@Builder
public class HardwareListVO {
    // id前端使用字符串, 防止 JS 大整数精度丢失
    private String id;

    private Integer categoryId;
    private String name;
    private String brand;
    private BigDecimal price;
    private String coverImage;
    private LocalDate releaseDate;
    private String sourceName;
    private LocalDateTime createTime;
}
