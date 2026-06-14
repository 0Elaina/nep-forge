package com.nep.system.vo;

import lombok.Builder;
import lombok.Data;


/**
 * 登录响应VO
 *     用于表示登录响应的VO，包含访问令牌、令牌类型、过期时间、当前用户信息等字段
 * @author Neptune
 * @date 2026-06-14
 */
@Data
@Builder
public class LoginResponse {
    private String accessToken; // 访问令牌
    private String tokenType; // 令牌类型
    private Long expiresIn; // 过期时间

    private CurrentUserVO user; // 当前用户信息
}
