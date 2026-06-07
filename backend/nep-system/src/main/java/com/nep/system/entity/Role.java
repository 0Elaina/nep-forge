package com.nep.system.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.io.Serial;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

/**
 * 角色实体类，对应数据库中的 roles 表。
 * 用于存储系统角色的基本信息，包括角色名称、角色编码、描述和审计字段。
 * @author nep
 */
@Data
@TableName("roles")
@NoArgsConstructor
@AllArgsConstructor
public class Role implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String roleName;
    private String roleCode;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}
