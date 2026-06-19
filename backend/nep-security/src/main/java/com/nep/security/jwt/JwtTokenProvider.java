package com.nep.security.jwt;

import org.springframework.stereotype.Component;

import com.nep.security.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.List;

import javax.crypto.SecretKey;

/**
 * JWT令牌提供器
 *     用于生成和解析JWT令牌的组件。
 * 
 * @author Neptune
 * @date 2026-06-19
 */
@Component
public class JwtTokenProvider {

    // 用户名声明键
    private static final String CLAIM_USERNAME = "username";
    // 角色声明键
    private static final String CLAIM_ROLES = "roles";

    private final JwtProperties jwtProperties;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * 生成JWT令牌
     * 
     * @param userId   用户ID
     * @param username 用户名
     * @param roles    用户角色列表
     * @return 生成的JWT令牌
     */
    public String generateAccessToken(Long userId, String username, List<String> roles) {
        // 获取当前的UTC时间
        Instant now = Instant.now();

        return Jwts.builder()
                // 设置签发者
                .issuer(jwtProperties.getIssuer())
                // 设置主题（用户ID）
                .subject(String.valueOf(userId))
                // 设置签发时间(当前时间)
                .issuedAt(Date.from(now))
                // 设置过期时间(当前时间 + 过期时间)
                .expiration(Date.from(now.plusSeconds(jwtProperties.getAccessTokenExpiration())))
                // 设置用户名声明
                .claim(CLAIM_USERNAME, username)
                // 设置角色声明
                .claim(CLAIM_ROLES, roles)
                // 签名: 使用HS256算法, 签名密钥为获取到的密钥
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                // compact(): 生成JWT令牌
                .compact();
    }


    /**
     * 解析JWT令牌
     * 
     * @param token JWT令牌
     * @return 解析出的登录用户信息
     * @throws IllegalArgumentException 如果JWT令牌无效
     */
    public JwtUserInfo parseAccessToken(String token){
        try{
            Claims claims = Jwts
            // 开始创建JWT解析器
            .parser()
            // 设置解析器的签名密钥为获取到的密钥
            .verifyWith(getSigningKey())
            // 完成解析器构建, 返回解析器对象, 开始解析JWT令牌
            .build()
            // 解析JWT令牌, 返回解析后的Claims对象
            .parseSignedClaims(token)
            // 从解析后的Claims对象中获取有效载荷（payload）
            .getPayload();

            // 从Claims对象中获取用户ID（主题）
            Long userId = Long.valueOf(claims.getSubject());
            // 从Claims对象中获取用户名声明
            String username = claims.get(CLAIM_USERNAME, String.class);
            // 从Claims对象中获取角色声明
            // 角色声明是一个List<Object>, 需要转换为List<String>
            List<String> roles = parseRoles(claims.get(CLAIM_ROLES));
            
            return new JwtUserInfo(userId, username, roles);

        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("非法的JWT令牌", e);
        }
    }

    /**
     * 获取JWT签名密钥
     * 
     * @return 签名密钥
     */
    private SecretKey getSigningKey() {
        String secret = jwtProperties.getSecret();
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("JWT 签名密钥长度必须至少为32个字节");
        }
        // 从密钥字符串创建HMAC-SHA256密钥
        // Keys.hmacShaKeyFor() 方法会自动根据密钥字符串的长度选择合适的HMAC算法, 包装为SecretKey对象
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 获取JWT令牌过期时间（毫秒）
     * 
     * @return 过期时间（毫秒）
     */
    public Long getAccessTokenExpiration() {
        return jwtProperties.getAccessTokenExpiration();
    }

    /**
     * 解析角色声明
     * 
     * @param rolesClaim 角色声明对象
     * @return 角色列表
     */
    private List<String> parseRoles(Object rolesClaim) {
        // 检查角色声明是否为List类型, 如果不是, 则返回空列表
        // 如果是List类型, 则自动转换为List<Object>
        if (!(rolesClaim instanceof List<?> roleList)) {
            return List.of();
        }

        return roleList.stream()
                // 过滤出非空值
                .filter(Objects::nonNull)
                // 转换为字符串
                .map(String::valueOf)
                .distinct()
                .toList();
    }
}
