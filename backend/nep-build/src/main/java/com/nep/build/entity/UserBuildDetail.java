package com.nep.build.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nep.common.entity.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 用户装机单详情
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("user_build_details")
public class UserBuildDetail extends BaseTimeEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    // 装机单详情ID, 雪花算法生成
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    // 装机单ID
    private Long buildId;

    // 配件ID
    private Long hardwareId;

    // 数量
    private Integer quantity;
}
