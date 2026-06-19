package com.nep.security.jwt;

import java.util.List;

/**
 * JWT 中解析出的登录用户信息
 * record是一种新的数据类型，用于表示不可变的值对象
 */
public record JwtUserInfo(
    Long userId,
    String username,
    List<String> roles
) {}
