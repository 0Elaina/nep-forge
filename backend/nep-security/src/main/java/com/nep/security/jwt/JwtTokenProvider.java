package com.nep.security.jwt;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtTokenProvider {
    /**
     * 生成JWT令牌
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @param roles 用户角色列表
     * @return 生成的JWT令牌
     */
    public String generateAccessToken(Long userId, String username, List<String> roles) {
        // 实现JWT令牌生成逻辑
        return "generatedAccessToken";
    }

    /**
     * 获取JWT令牌过期时间（毫秒）
     * 
     * @return 过期时间（毫秒）
     */
    public long getAccessTokenExpiration() {
        return 1000 * 3600 * 24;
    }
}
