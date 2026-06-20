package com.nep.system.entity;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nep.common.entity.BaseTimeEntity;
import lombok.EqualsAndHashCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.IdType;

/**
 * 角色用户关联实体类，对应数据库中的 role_users 表。
 * 用于存储系统角色与用户之间的关联关系，包括用户ID、角色ID、创建时间、更新时间。
 * @author nep
 */
@Data
@TableName("role_users")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RoleUser extends BaseTimeEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Integer roleId;
}
