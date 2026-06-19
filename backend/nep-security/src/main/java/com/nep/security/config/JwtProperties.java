package com.nep.security.config;

import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "nep.jwt")
@Getter
@Setter
public class JwtProperties {
    /**
     * JWT 签名密钥
     * HS256 算法 至少需要32个字节
     */
    private String secret;

    /**
     * JWT 访问令牌过期时间(秒)
     */
    private Long accessTokenExpiration = 7200L;

    /**
     * token 签发者
     */
    private String issuer = "nep-forge";
}
