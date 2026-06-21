package com.nep.build.entity;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nep.common.entity.BaseLogicEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 用户装机单实体类
 * 包含用户装机单的详细信息，如标题、价格、功率、描述、是否公开、状态、封面图片等。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("user_builds")
public class UserBuild extends BaseLogicEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    // 主键ID, 雪花算法生成
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    // 用户ID
    private Long userId;

    // 装机单标题
    private String title;

    // 总价格
    private BigDecimal totalPrice;

    // 总功率
    private BigDecimal totalPower;

    // 装机单描述
    private String description;

    // 是否公开
    // 0: 否, 1: 是
    private Integer isPublic;

    // 状态
    // 0: 草稿, 1: 正常, 2: 下架
    private Integer status;

    // 装机单封面图片
    private String coverImage;
}
