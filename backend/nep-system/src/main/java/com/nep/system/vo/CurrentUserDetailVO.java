package com.nep.system.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 当前用户详情VO。
 *     用于展示当前登录用户的详细信息，包括用户名、邮箱、昵称、个人简介、头像、状态、最后登录时间、角色列表和创建时间。
 * 
 * @author Neptune
 * @date 2026-06-19
 */
@Data
@Builder
public class CurrentUserDetailVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String email;
    private String nickname;
    private String bio;
    private String avatar;
    private Integer status;
    private String lastLoginTime;
    private List<String> roles;
    private String createTime;
}
