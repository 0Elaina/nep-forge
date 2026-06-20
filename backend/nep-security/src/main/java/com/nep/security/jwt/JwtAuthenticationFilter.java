package com.nep.security.jwt;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;


import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * JWT认证过滤器
 * 从请求头中提取JWT令牌，解析令牌，设置用户信息到SecurityContextHolder
 * 如果令牌无效或缺失，直接放请求通过
 * 如果令牌有效，解析令牌，设置用户信息到SecurityContextHolder
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String token = extractToken(request);

        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            JwtUserInfo userInfo = jwtTokenProvider.parseAccessToken(token);

            List<GrantedAuthority> authorities = userInfo.roles()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .map(authority -> (GrantedAuthority) authority)
                .toList();
            
            // 创建AuthenticationToken
            // 包含用户信息、密码（null）、角色列表
            // 这里密码为null，因为JWT令牌中不包含密码
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userInfo, null, authorities);

            // 设置AuthenticationToken的详细信息
            // 包含请求的IP地址、端口号、请求路径等
            authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // 设置当前线程的Authentication
            // 这样后续的Security检查就可以使用这个Authentication
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (IllegalArgumentException ex) {
            // 如果JWT令牌无效， 清除当前线程的Authentication
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取JWT令牌
     * @param request HttpServletRequest对象
     * @return JWT令牌
     */
    private String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        if(!StringUtils.hasText(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            return null;
        }

        return authorization.substring(BEARER_PREFIX.length()).trim();
    }
}
