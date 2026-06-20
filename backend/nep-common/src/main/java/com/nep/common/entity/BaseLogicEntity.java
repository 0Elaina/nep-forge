package com.nep.common.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableLogic;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础逻辑删除实体类
 * 用于表示所有实体类的逻辑删除字段，自动填充当前时间戳。
 */
@Data
// EqualsAndHashCode 注解用于自动生成 equals 方法，考虑父类的字段
@EqualsAndHashCode(callSuper = true)
// 为什么需要继承 BaseTimeEntity，获取 createTime 和 updateTime 字段？
// 因为 BaseLogicEntity 是一个逻辑删除实体类，需要考虑创建时间和更新时间。
public class BaseLogicEntity extends BaseTimeEntity{
    @Serial
    private static final long serialVersionUID = 1L;

    // 逻辑删除字段, 0 表示未删除, 1 表示已删除
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
