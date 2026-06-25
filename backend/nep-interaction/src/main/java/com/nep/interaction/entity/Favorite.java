package com.nep.interaction.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nep.common.entity.BaseTimeEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收藏夹实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("favorites")
public class Favorite extends BaseTimeEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    // 主键ID, 雪花算法生成
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    // 用户ID
    private Long userId;

    // 收藏夹名称
    private String name;

    // 收藏夹描述
    private String description;

    // 是否公开 0: 私有, 1: 公开
    private Integer isPublic;
}
