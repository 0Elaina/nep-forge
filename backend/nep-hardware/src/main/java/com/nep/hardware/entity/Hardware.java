package com.nep.hardware.entity;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nep.common.entity.BaseLogicEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 配件实体类
 * 用于存储硬件配件的详细信息，包括名称、品牌、价格、数据来源、发布时间、最近同步时间、封面图片URL、差异化参数JSON字符串等。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("hardware")
public class Hardware extends BaseLogicEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    // 配件ID: 雪花算法生成
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    // 配件分类ID
    private Integer categoryId;

    // 配件名称
    private String name;

    // 品牌
    private String brand;

    // 价格
    private BigDecimal price;

    // 数据来源名称
    private String sourceName;

    // 数据来源连接
    private String sourceUrl;

    // 发布时间
    private LocalDate releaseDate;

    // 最近同步时间
    private LocalDateTime lastSyncTime;

    // 封面图片URL
    private String coverImage;

    // 配件差异化参数JSON字符串
    // 用于存储配件的差异化参数，例如尺寸、重量、颜色等
    private String specsJson;
}
