package com.nep.system.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 当前用户VO
 *     用于表示当前登录用户的VO，包含用户ID、用户名、邮箱、头像、角色列表等字段
 * @author Neptune
 * @date 2026-06-14
 */
@Data
@Builder
public class CurrentUserVO {
    private String id;
    private String username;
    private String email;
    private String avatar;
    private List<String> roles;
}
