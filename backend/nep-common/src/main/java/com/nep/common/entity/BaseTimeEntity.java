package com.nep.common.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;

import lombok.Data;

/**
 * 基础时间实体类
 * 用于表示所有实体类的创建时间和更新时间，自动填充当前时间戳。
 */
@Data
public class BaseTimeEntity implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;

    // 创建时间, 在插入时自动填充当前时间戳
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 更新时间, 在插入和更新时自动填充当前时间戳
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
