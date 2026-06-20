package com.nep.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nep.common.entity.BaseLogicEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 用户实体类，对应数据库中的 users 表。
 * 用于存储系统用户的基本信息，包括登录凭证、个人资料和审计字段。
 * @author nep
 */
@Data
@TableName("users")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseLogicEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID) 
    private Long id;

    private String username;

    private String passwordHash;

    private String email;

    private String avatar;

    private String nickname;
    private String bio;
    private Integer status;
    private LocalDateTime lastLoginTime;


}
