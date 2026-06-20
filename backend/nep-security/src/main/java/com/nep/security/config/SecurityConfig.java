package com.nep.security.config;

import com.nep.security.jwt.JwtAuthenticationFilter;
import org.springframework.http.HttpMethod;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.nep.common.constants.RoleConstants;
import com.nep.security.handler.CustomAccessDeniedHandler;
import com.nep.security.handler.CustomAuthenticationEntryPoint;


import lombok.RequiredArgsConstructor;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * 安全配置类
 * 包含HTTP安全过滤链的配置
 * 包括CSRF保护、表单登录、会话管理、异常处理、接口权限配置等
 * 
 * 该类负责配置HTTP安全过滤链，确保应用的安全性
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * 配置HTTP安全过滤链
     * @param httpSecurity HTTP安全配置对象
     * @return 配置好的安全过滤链对象
     * @throws Exception 如果配置过程中发生错误
     * 
     * 配置HTTP安全过滤链
     * 包括CSRF保护、表单登录、会话管理、异常处理、接口权限配置等
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
        // CSRF配置: jwt存在header而非cookie中, 不需要CSRF保护
        .csrf(AbstractHttpConfigurer::disable)
        // Form登录配置: 不需要表单登录
        .formLogin(AbstractHttpConfigurer::disable)
        // 会话管理配置: 使用JWT令牌进行认证, 服务端不创建/维护HttpSession, 每次请求独立校验令牌
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        // 异常处理配置: authenticationEntryPoint处理未认证(401), accessDeniedHandler处理无权限(403)
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        )
        // 接口权限配置: 按粒度从松到紧排列 (白名单 → 公共读 → 管理员 → 其余需认证)
        .authorizeHttpRequests(auth -> auth
            // 认证接口: 注册/登录无需令牌
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()

            // Swagger/Knife4j 文档接口: 开放给开发和测试环境
            .requestMatchers(
                "/doc.html",
                "/webjars/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/favicon.ico"
            ).permitAll()

            // 公开读取接口: 硬件/文章/分类/标签/构建信息等可匿名访问
            .requestMatchers(HttpMethod.GET, "/api/v1/hardware/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/articles/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/article-categories/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/article-tags/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/builds/public").permitAll()

            // 管理员接口: 需 ROLE_ADMIN 权限
            .requestMatchers("/api/v1/admin/**").hasAuthority("ROLE_ADMIN")

            // 版主 + 管理员: 社区治理能力
            .requestMatchers(HttpMethod.POST, "/api/v1/comments/**")
            .hasAnyAuthority(RoleConstants.ROLE_MODERATOR, RoleConstants.ROLE_ADMIN)

            .requestMatchers(HttpMethod.PUT, "/api/v1/articles/*/status")
            .hasAnyAuthority(RoleConstants.ROLE_MODERATOR, RoleConstants.ROLE_ADMIN)

            .requestMatchers(HttpMethod.PUT, "/api/v1/comments/*/status")
            .hasAnyAuthority(RoleConstants.ROLE_MODERATOR, RoleConstants.ROLE_ADMIN)


            // 登录用户: 普通用户、版主、管理员都可以使用的业务能力
            .requestMatchers("/api/v1/users/me").authenticated()
            .requestMatchers("/api/v1/builds/**").authenticated()
            .requestMatchers("/api/v1/interactions/**").authenticated()
            .requestMatchers("/api/v1/favorites/**").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/v1/articles/**").authenticated()
            .requestMatchers(HttpMethod.PUT, "/api/v1/articles/**").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/v1/comments/**").authenticated()

            // 其余所有请求: 必须携带有效JWT令牌
            .anyRequest().authenticated()
        )
        // 将JWT过滤器插入在UsernamePasswordAuthenticationFilter之前, 先校验令牌再执行认证
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();

    }
}
