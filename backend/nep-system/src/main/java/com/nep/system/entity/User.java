package com.nep.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

import java.io.Serial;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 用户实体类，对应数据库中的 users 表。
 * 用于存储系统用户的基本信息，包括登录凭证、个人资料和审计字段。
 * @author nep
 */
@Data
@TableName("users")
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID) 
    private Long id;

    private String username;

    private String passwordHash;

    private String email;

    private String avatar;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDeleted;

    private String nickname;
    private String bio;
    private Integer status;
    private LocalDateTime lastLoginTime;


}
