package com.nep.hardware.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nep.common.entity.BaseLogicEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 硬件分类实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("hw_category")
public class HardwareCategory extends BaseLogicEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    // 主键id, 自动递增
    @TableId(type=IdType.AUTO)
    private Integer id;

    // 分类名称
    private String name;
    
    // 父分类id
    private Integer parentId;

    // 分类状态: 0-禁用, 1-启用
    private Integer status;

    // 排序顺序(越小越靠前)
    private Integer sortOrder;

}
