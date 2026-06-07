package com.nep.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

/**
 * 密码编码器配置类。
 * 配置 Spring Security 的密码编码器 Bean，用于对用户密码进行加密存储和校验。
 * 采用委派模式支持多种编码算法，默认使用 BCrypt。
 *
 * @author Neptune
 * @date 2026-06-07
 * @see PasswordEncoder
 * @see PasswordEncoderFactories
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * 注册密码编码器 Bean。
     * 使用 {@link PasswordEncoderFactories#createDelegatingPasswordEncoder()}
     * 创建委派密码编码器，具备以下特点：
     * - 默认使用 BCrypt 对密码进行加密存储
     * - 支持读取多种编码前缀（如 {bcrypt}、{pbkdf2}、{scrypt} 等），
     * - 便于对数据库中的存量密码进行算法升级
     * - 加密后的密码自带算法前缀，校验时根据前缀自动选择对应的编码器
     *
     * @return 委派密码编码器实例
     * @see PasswordEncoderFactories#createDelegatingPasswordEncoder()
     * @see PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }
}